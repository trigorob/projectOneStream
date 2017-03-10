package music.onestream.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.spotify.sdk.android.player.Connectivity;

import music.onestream.util.PlayerActionsHandler;
import music.onestream.playlist.Playlist;
import music.onestream.playlist.PlaylistAdapter;
import music.onestream.playlist.PlaylistHandler;
import music.onestream.R;
import music.onestream.util.SectionsPagerAdapter;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;

public class OneStreamActivity extends OSActivity {

    private PlayerActionsHandler playerHandler;
    private static boolean songViewEnabled;

    private static PlaylistHandler playlistHandler;
    private static ListView mainList;

    // variable declaration
    private static ArrayAdapter<Song> adapter;
    private static ArrayAdapter<Song> spotifyAdapter;
    private static ArrayAdapter<Playlist> playlistAdapter;
    private static ArrayAdapter<Song> googleAdapter;
    private static ArrayAdapter<Song> combinedAdapter;
    private static ArrayAdapter<Playlist> artistsAdapter;
    private static ArrayAdapter<Playlist> albumsAdapter;

/**
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * {@link FragmentPagerAdapter} derivative, which will keep every
 * loaded fragment in memory. If this becomes too memory intensive, it
 * may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 */
private SectionsPagerAdapter mSectionsPagerAdapter;

/**
 * The {@link ViewPager} that will host the section contents.
 */
private ViewPager mViewPager;

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerHandler.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerHandler.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerHandler.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            playerHandler.destroyPlayers();
            playerHandler.stopPlayerService();
            Intent settings = new Intent(mViewPager.getContext(), SettingsActivity.class);
            startActivityForResult(settings, 0);

        }
        return super.onOptionsItemSelected(item);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onestream);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mainList = (ListView) findViewById(R.id.ListView1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(0);

        initPlayerHandler();
        initPlaylistHandler();
        initListDisplay();
        initButtonListeners();
    }

    public static void initPlaylistAdapter(Context context) {
        boolean refreshView = false;
        if (mainList.getAdapter().equals(playlistAdapter))
        {
            refreshView = true;
        }
        playlistAdapter = new PlaylistAdapter(context, R.layout.songlayout,
                playlistHandler.getPlaylists());
        playlistAdapter.notifyDataSetChanged();
        mainList.invalidateViews();
        if (refreshView)
        {
            mainList.setAdapter(playlistAdapter);
        }
    }

    public static void notifyAdapters() {
        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
        if (spotifyAdapter != null)
        {
            spotifyAdapter.notifyDataSetChanged();
        }
        if (playlistAdapter != null)
        {
            playlistAdapter.notifyDataSetChanged();
        }
        if (googleAdapter != null)
        {
            googleAdapter.notifyDataSetChanged();
        }
        if (combinedAdapter != null)
        {
            combinedAdapter.notifyDataSetChanged();
        }
        if (artistsAdapter != null)
        {
            artistsAdapter.notifyDataSetChanged();
        }
        if (albumsAdapter != null)
        {
            albumsAdapter.notifyDataSetChanged();
        }
        mainList.invalidateViews();
    }


    public void initListDisplay() {

        adapter = new SongAdapter(this, R.layout.songlayout,
                playlistHandler.getList("Local").getSongInfo());
        spotifyAdapter = new SongAdapter(this, R.layout.songlayout,
                playlistHandler.getList("Spotify").getSongInfo());
        playlistAdapter = new PlaylistAdapter(this, R.layout.songlayout,
               playlistHandler.getPlaylists());
        combinedAdapter = new SongAdapter(this, R.layout.songlayout,
                playlistHandler.getCombinedList().getSongInfo());
        artistsAdapter = new PlaylistAdapter(this, R.layout.songlayout,
                playlistHandler.getArtists());
        albumsAdapter = new PlaylistAdapter(this, R.layout.songlayout,
                playlistHandler.getAlbums());

        final EditText textFilter = (EditText) findViewById(R.id.songFilter);
        textFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                int page = mViewPager.getCurrentItem();
                if (page == 3 || page == 5 || page == 6)
                {
                    ((PlaylistAdapter) mainList.getAdapter()).getFilter().filter(cs);
                }
                else
                {
                    ((SongAdapter) mainList.getAdapter()).getFilter().filter(cs);
                }
                notifyAdapters();
                mainList.invalidateViews();
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
           });

        textFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textFilter.setCursorVisible(true);
                textFilter.requestFocus();
            }
        });

        textFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    textFilter.clearFocus();
                    textFilter.setCursorVisible(false);
                }
                return false;
            }
        });
        //TODO: Implement. Placeholder so we dont have to make lists visible/invisible
        googleAdapter = new SongAdapter(this,R.layout.songlayout,
                new ArrayList<Song>());
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                int page = mViewPager.getCurrentItem();
                if (page == 3 || page == 5 || page == 6)
                {
                    Intent playlist = new Intent(view.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    Playlist p = ((PlaylistAdapter) mainList.getAdapter()).getItem(position);
                    b.putSerializable("Playlist", p);
                    playlist.putExtras(b);
                    playerHandler.onDestroy();
                    startActivityForResult(playlist, 0);
                }
                else {
                    playerHandler.setCurrentSongListPosition(position);
                    mainList.setItemChecked(position, true);
                    playerHandler.playSong(position);
                }
            }});
    }

    private void initPlayerHandler() {
        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        final ImageButton fabIO = (ImageButton) findViewById(R.id.fabIO);
        final ImageButton random = (ImageButton) findViewById(R.id.Random);
        final ImageButton rewind = (ImageButton) findViewById(R.id.Rewind);
        final ImageButton prev = (ImageButton) findViewById(R.id.Prev);
        final ImageButton next = (ImageButton) findViewById(R.id.Next);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        playerHandler =
                initPlayerHandler(this.getApplicationContext(), "OneStreamActivity",
                        loginButton, fabIO, prev, next, rewind,
                        random, seekbar, mainList);
    }

    public static boolean isSongViewEnabled() {
        return songViewEnabled;
    }

    public void initPlaylistHandler() {

        SharedPreferences settings = getSharedPreferences("SongView", 0);
        songViewEnabled = settings.getBoolean("SongView", false);
        settings = getSharedPreferences("dirInfo", 0);
        String directory = settings.getString("dir", "N/A");
        boolean directoryChanged = settings.getBoolean("directoryChanged", false);
        SharedPreferences.Editor editor = settings.edit();
        settings = getSharedPreferences("SORT-TYPE", 0);
        String sortType = settings.getString("sortType", "Default");
        boolean sortOnLoad = settings.getBoolean("sortOnLoad", false);
        settings = getSharedPreferences("ONESTREAM_DOMAIN", 0);
        String domain =  settings.getString("domain", "Admin");

        playlistHandler = new PlaylistHandler(this.getApplicationContext(), playerHandler,
                sortType, directory, directoryChanged, domain);

        if (sortOnLoad)
        {
            playlistHandler.sortAllLists(sortType);
            editor.putBoolean("sortOnLoad", false);
            editor.commit();
        }

    }

    public static void setLoginButtonVisible(boolean visible, Button loginButton) {
        if (visible) {
            loginButton.setVisibility(View.VISIBLE);
        }
        else {
            loginButton.setVisibility(View.INVISIBLE);
        }
    }

    public static PlaylistHandler getPlaylistHandler() {
        return playlistHandler;
    }

    public void initButtonListeners() {

        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        setLoginButtonVisible(false, loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHandler.destroyPlayers();
                playerHandler.stopPlayerService();
                Intent settings = new Intent(mViewPager.getContext(), LoginActivity.class);
                startActivityForResult(settings, 0);
            }
        });

        final Handler mHandler = new Handler();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            //TODO: Google music case when appropriate
            @Override
            public void onPageSelected(int position) {
                playerHandler.setCurrentSongListPosition(-1);
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        mainList.setAdapter(combinedAdapter);
                        setLoginButtonVisible(false, loginButton);;
                        break;
                    case 1:
                        mainList.setAdapter(adapter);
                        setLoginButtonVisible(false, loginButton);;
                        break;
                    case 2:
                        mainList.setAdapter(spotifyAdapter);
                        if (playerHandler.isSpotifyLoggedOut() && spotifyAdapter.getCount() == 0)
                        {
                            setLoginButtonVisible(true, loginButton);;
                        }
                        break;
                    case 4:
                        //Todo: change to googlemusicStrings
                        if ((playlistHandler.getList("Spotify") == null))
                        {
                            setLoginButtonVisible(true, loginButton);;
                        }
                        mainList.setAdapter(googleAdapter);
                        break;
                    case 3:
                        mainList.setAdapter(playlistAdapter);
                        setLoginButtonVisible(false, loginButton);;
                        break;
                    case 5:
                        mainList.setAdapter(artistsAdapter);
                        setLoginButtonVisible(false, loginButton);;
                        break;
                    case 6:
                        mainList.setAdapter(albumsAdapter);
                        setLoginButtonVisible(false, loginButton);;
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //Seekbar tracker
        OneStreamActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerHandler.updateSeekBar();
                mHandler.postDelayed(this, 1000);
            }
        });

    }

    public Boolean isConnected() {
        return !playerHandler.getNetworkConnectivity(this.getApplicationContext()).equals(Connectivity.OFFLINE);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, OneStreamActivity.class);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

    }
}


