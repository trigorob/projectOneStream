package music.onestream;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.spotify.sdk.android.player.Connectivity;

public class OneStreamActivity extends AppCompatActivity {

    private static PlayerActionsHandler playerHandler;
    private static PlaylistHandler playlistHandler;

    // variable declaration
    private static ListView mainList;
    private static ArrayAdapter<Song> adapter;
    private static ArrayAdapter<Song> spotifyAdapter;
    private static ArrayAdapter<Playlist> playlistAdapter;
    private static ArrayAdapter<Song> googleAdapter;

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

    public static void notifyAdapters() {

        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
        if (spotifyAdapter != null)
        {
            spotifyAdapter.notifyDataSetChanged();
        }
        mainList.invalidateViews();
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
            Intent settings = new Intent(mViewPager.getContext(), Settings.class);
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

    public static void resetPlaylistAdapter(Context context) {
        playlistAdapter = new ArrayAdapter<Playlist>(context, android.R.layout.simple_list_item_1,
                playlistHandler.getPlaylists());
        if (playlistAdapter != null) {
            playlistAdapter.notifyDataSetChanged();
        }
        mainList.invalidateViews();
    }

    public void initListDisplay() {

        adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1,
                playlistHandler.getList("Local").getSongInfo());
        spotifyAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1,
                playlistHandler.getList("Spotify").getSongInfo());
        playlistAdapter = new ArrayAdapter<Playlist>(this, android.R.layout.simple_list_item_1,
               playlistHandler.getPlaylists());

        //TODO: Implement. Placeholder so we dont have to make lists visible/invisible
        googleAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1,
                new ArrayList<Song>());
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (mViewPager.getCurrentItem() == 3)
                {
                    Intent playlist = new Intent(view.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("Playlist", playlistHandler.getPlaylists().get(position));
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

    public void initPlayerHandler() {
        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.Random);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.Rewind);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.Prev);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);

        playerHandler = new PlayerActionsHandler(this.getApplicationContext(), fabIO, prev, next,
                rewind, random, loginButton, mainList, seekbar, "OneStreamActivity");

    }

    public void initPlaylistHandler() {

        SharedPreferences settings = getSharedPreferences("dirInfo", 0);
        String directory = settings.getString("dir", "Default");
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
                        mainList.setAdapter(adapter);
                        setLoginButtonVisible(false, loginButton);;
                        break;
                    case 1:
                        mainList.setAdapter(spotifyAdapter);
                        if (playerHandler.isSpotifyLoggedOut())
                        {
                            setLoginButtonVisible(true, loginButton);;
                        }
                        break;
                    case 2:
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

    public static PlayerActionsHandler getPlayerHandler() {
        return playerHandler;
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

    }
}


