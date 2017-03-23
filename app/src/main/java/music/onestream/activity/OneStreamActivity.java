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
import android.widget.ListView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Connectivity;

import music.onestream.util.Constants;
import music.onestream.util.Logger;
import music.onestream.util.LoginHandler;
import music.onestream.util.PlayerActionsHandler;
import music.onestream.playlist.Playlist;
import music.onestream.playlist.PlaylistAdapter;
import music.onestream.playlist.PlaylistHandler;
import music.onestream.R;
import music.onestream.util.OneStreamActivityAdapter;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;

public class OneStreamActivity extends OSAuthenticationActivity {

    private PlayerActionsHandler playerHandler;
    private static boolean songViewEnabled;

    private static PlaylistHandler playlistHandler;
    private static int currentPage;
    private static ListView mainList;

    // variable declaration
    private static SongAdapter adapter;
    private static SongAdapter spotifyAdapter;
    private static PlaylistAdapter playlistAdapter;
    private static SongAdapter googleAdapter;
    private static SongAdapter combinedAdapter;
    private static PlaylistAdapter artistsAdapter;
    private static PlaylistAdapter albumsAdapter;

/**
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * {@link FragmentPagerAdapter} derivative, which will keep every
 * loaded fragment in memory. If this becomes too memory intensive, it
 * may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 */
private OneStreamActivityAdapter mSectionsPagerAdapter;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayerHandler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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

        mSectionsPagerAdapter = new OneStreamActivityAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mainList = (ListView) findViewById(R.id.ListView1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(Constants.OneStream_Library_Pos);

        initPlayerHandler();
        initPlaylistHandler();
        initListDisplay();
        initButtonListeners();
    }

    public static void notifyLocalAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }
    }
    public static void notifySpotifyAdapter() {
        if (spotifyAdapter != null) {
            spotifyAdapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }
    }

    public static void notifyGoogleAdapter() {
        if (googleAdapter != null) {
            googleAdapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }
    }

    public static void notifyArtistsAdapter() {
        if (artistsAdapter != null) {
            artistsAdapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }
    }

    public static void notifyAlbumsAdapter() {
        if (albumsAdapter != null) {
            albumsAdapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }
    }

    public static void initPlaylistAdapter(Context context) {
        boolean refreshView = false;
        if (mainList.getAdapter().equals(playlistAdapter))
        {
            refreshView = true;
        }
        playlistAdapter = new PlaylistAdapter(context, R.layout.songlayout,
                playlistHandler.getPlaylists());
        playlistAdapter.setNotifyOnChange(true);
        mainList.invalidateViews();
        if (refreshView)
        {
            mainList.setAdapter(playlistAdapter);
        }
    }

    public static void invalidateList() {
        if (combinedAdapter != null && currentPage == Constants.OneStream_Library_Pos)
        {
                mainList.setAdapter(combinedAdapter);
                mainList.setSelection(getPlayerHandler().getCurrentSongListPosition());
        }
        mainList.invalidateViews();
    }

    public boolean onPlaylistPage() {
        return (currentPage == Constants.OneStream_Playlists_Pos
                || currentPage == Constants.OneStream_Artists_Pos ||
                currentPage == Constants.OneStream_Albums_Pos);
    }

    public void initListDisplay() {

        adapter = new SongAdapter(this, R.layout.songlayout,
                playlistHandler.getList(Constants.local).getSongInfo());
        spotifyAdapter = new SongAdapter(this, R.layout.songlayout,
                playlistHandler.getList(Constants.spotify).getSongInfo());
        combinedAdapter = new SongAdapter(this, R.layout.songlayout,
                playlistHandler.getList(Constants.library).getSongInfo());
        playlistAdapter = new PlaylistAdapter(this, R.layout.songlayout,
               playlistHandler.getPlaylists());
        artistsAdapter = new PlaylistAdapter(this, R.layout.songlayout,
                playlistHandler.getArtists());
        albumsAdapter = new PlaylistAdapter(this, R.layout.songlayout,
                playlistHandler.getAlbums());

        adapter.setNotifyOnChange(true);
        spotifyAdapter.setNotifyOnChange(true);
        combinedAdapter.setNotifyOnChange(true);
        playlistAdapter.setNotifyOnChange(true);
        artistsAdapter.setNotifyOnChange(true);
        albumsAdapter.setNotifyOnChange(true);

        //TODO: Implement
        googleAdapter = new SongAdapter(this,R.layout.songlayout,
                new ArrayList<Song>());
        mainList.setAdapter(adapter);

        final EditText textFilter = (EditText) findViewById(R.id.songFilter);
        textFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (onPlaylistPage())
                {
                    ((PlaylistAdapter) mainList.getAdapter()).getFilter().filter(cs);
                }
                else
                {
                    ((SongAdapter) mainList.getAdapter()).getFilter().filter(cs);
                }
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

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (onPlaylistPage())
                {
                    Intent playlist = new Intent(view.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    Playlist p = ((PlaylistAdapter) mainList.getAdapter()).getItem(position);
                    b.putSerializable("Playlist", p);
                    playlist.putExtras(b);
                    startActivityForResult(playlist, 0);
                }
                else {
                    ArrayList<Song> songs = ((SongAdapter) mainList.getAdapter()).getSongs();
                    OneStreamActivity.getPlaylistHandler().setCurrentSongs(songs);
                    playerHandler.setCurrentListSize(songs.size());

                    playerHandler.setCurrentSongListPosition(position);
                    mainList.setItemChecked(position, true);
                    playerHandler.playSong(songs.indexOf(mainList.getAdapter().getItem(position)));
                }
            }});
    }

    private void initPlayerHandler() {
        final ImageButton loginButton = (ImageButton) findViewById(R.id.loginLauncherLinkerButton);
        final ImageButton fabIO = (ImageButton) findViewById(R.id.fabIO);
        final ImageButton random = (ImageButton) findViewById(R.id.Random);
        final ImageButton rewind = (ImageButton) findViewById(R.id.Rewind);
        final ImageButton prev = (ImageButton) findViewById(R.id.Prev);
        final ImageButton next = (ImageButton) findViewById(R.id.Next);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        playerHandler =
                initPlayerHandler(this.getApplicationContext(), Constants.oneStreamActivity,
                        loginButton, fabIO, prev, next, rewind,
                        random, seekbar, mainList);
        playerHandler.setButtonColors(-1);
    }

    public static boolean isSongViewEnabled() {
        return songViewEnabled;
    }

    public void initPlaylistHandler() {

        SharedPreferences settings = getSharedPreferences(Constants.songViewLoc, 0);
        songViewEnabled = settings.getBoolean(Constants.songViewOn, false);
        settings = getSharedPreferences(Constants.cachePlaylistsLoc, 0);
        boolean cachePlaylists = settings.getBoolean(Constants.cachePlaylistsOn, false);
        settings = getSharedPreferences(Constants.dirInfoLoc, 0);
        String directory = settings.getString(Constants.directory, Constants.defaultDirectory);
        boolean directoryChanged = settings.getBoolean(Constants.directoryChanged, false);
        SharedPreferences.Editor editor = settings.edit();
        settings = getSharedPreferences(Constants.sortTypeLoc, 0);
        String sortType = settings.getString(Constants.sortType, Constants.defaultSortType);
        boolean sortOnLoad = settings.getBoolean(Constants.sortOnLoad, false);
        settings = getSharedPreferences(Constants.oneStreamDomainLoc, 0);
        boolean spotifyLoginChanged = settings.getBoolean(Constants.spotifyLoginChanged, false);
        String domain =  settings.getString(Constants.domain, Constants.defaultDomain);

        if (playlistHandler == null)
        {
            spotifyLoginChanged = true;
        }

        playlistHandler = PlaylistHandler.initPlaylistHandler(this.getApplicationContext(), playerHandler,
                sortType, directory, directoryChanged, domain, spotifyLoginChanged, cachePlaylists);

        if (sortOnLoad)
        {
            playlistHandler.sortAllLists(sortType);
            editor.putBoolean(Constants.sortOnLoad, false);
            editor.commit();
        }
        if (spotifyLoginChanged)
        {
            editor = settings.edit();
            editor.putBoolean(Constants.spotifyLoginChanged, false);
            editor.commit();
        }

    }

    public static void setLoginButtonVisible(boolean visible, ImageButton loginButton) {
        if (visible) {
            loginButton.setVisibility(View.VISIBLE);
            if (currentPage == Constants.OneStream_Spotify_Pos)
            {
                loginButton.setImageResource(R.drawable.spotify);
            }
            else if (currentPage == Constants.OneStream_GoogleMusic_Pos)
            {
                loginButton.setImageResource(R.drawable.googlemusic);
            }
        }
        else {
            loginButton.setVisibility(View.INVISIBLE);
        }
    }

    public static PlaylistHandler getPlaylistHandler() {
        return playlistHandler;
    }

    public void onLoginButtonClicked() {
        if (currentPage == Constants.OneStream_Spotify_Pos) {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(Constants.SPOTIFY_ID,
                    AuthenticationResponse.Type.TOKEN, Constants.SPOTIFY_REDIRECT_URI)
                    .setScopes(new String[]{"user-library-read", "user-read-private", "playlist-read",
                            "playlist-read-private", "streaming"}).setShowDialog(true)
                    .build();
            AuthenticationClient.openLoginActivity(this, Constants.REQUEST_CODE, request);
        }
        else if (currentPage == Constants.OneStream_GoogleMusic_Pos)
        {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGoogleApiClient());
            startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        }
    }

    public void initButtonListeners() {

        final ImageButton loginButton = (ImageButton) findViewById(R.id.loginLauncherLinkerButton);
        setLoginButtonVisible(false, loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });

        final Handler mHandler = new Handler();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        currentPage = Constants.OneStream_Library_Pos;
                        mainList.setAdapter(combinedAdapter);
                        setLoginButtonVisible(false, loginButton);
                        break;
                    case 1:
                        currentPage = Constants.OneStream_Local_Pos;
                        mainList.setAdapter(adapter);
                        setLoginButtonVisible(false, loginButton);
                        break;
                    case 2:
                        currentPage = Constants.OneStream_Spotify_Pos;
                        mainList.setAdapter(spotifyAdapter);
                        if (playerHandler.isSpotifyLoggedOut() && spotifyAdapter.getCount() == 0)
                        {
                            setLoginButtonVisible(true, loginButton);
                        }
                        break;
                    case 3:
                        currentPage = Constants.OneStream_Playlists_Pos;
                        mainList.setAdapter(playlistAdapter);
                        setLoginButtonVisible(false, loginButton);
                        break;
                    case 4:
                        //Todo: change to Googledapter
                        currentPage = Constants.OneStream_GoogleMusic_Pos;
                        if ((playlistHandler.getList(Constants.spotify) == null))
                        {
                            setLoginButtonVisible(true, loginButton);
                        }
                        mainList.setAdapter(googleAdapter);
                        break;
                    case 5:
                        currentPage = Constants.OneStream_Artists_Pos;
                        mainList.setAdapter(artistsAdapter);
                        setLoginButtonVisible(false, loginButton);
                        break;
                    case 6:
                        currentPage = Constants.OneStream_Albums_Pos;
                        mainList.setAdapter(albumsAdapter);
                        setLoginButtonVisible(false, loginButton);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Logger logger = new Logger(getContext(), OneStreamActivity.class.getSimpleName());

        String response = LoginHandler.handleLogin(requestCode, resultCode, intent, this.getApplicationContext());

        if (response.equals("Error")) {
            logger.logError("Login Failed");
        } else if (response.equals("GoogleGetToken")) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGoogleApiClient());
            startActivityForResult(signInIntent, Constants.RC_GET_TOKEN);

        } else if (response.contains("Token:")) {
            logger.logMessage("Login Success!");
            startMainActivity(response.substring(6), "Spotify");

            SharedPreferences settings = getSharedPreferences(Constants.oneStreamDomainLoc, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.spotifyLoginChanged, true);
            editor.commit();
        }
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
            mainList.invalidateViews();
        }

    }
}


