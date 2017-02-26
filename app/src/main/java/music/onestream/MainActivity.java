package music.onestream;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    //Increment this after getting spotify songs. TODO: Implement this functionality
    private static int spotifySongOffset= 0;
    private static int totalLocalSongs = 0;

    private static String directory;
    private static Boolean directoryChanged = false;
    private static Boolean sortOnLoad = false;
    private static Boolean playlistsChanged;
    private static String sortType;
    private static DatabaseActionsHandler dba;

    private PlayerActionsHandler playerHandler;

    // variable declaration
    private ListView mainList;
    private MusicGetter mG;
    private SpotifyMusicGetter sMG;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> spotifyAdapter;
    private ArrayAdapter<String> playlistAdapter;
    private static Playlist listContent;
    private static Playlist spotifyListContent;
    private static ArrayList<Playlist> playlists;
    private static ArrayList<String> playlistNames;
    private static Playlist combinedList;

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

    public MusicGetter setMusicDir(String dir)
    {
        mG = new MusicGetter(dir);
        ArrayList<Song> totalListContent = mG.songs;
        totalLocalSongs = totalListContent.size();
        listContent.setSongInfo(new ArrayList<Song>());
        int localSongOffset= 0;
        while (localSongOffset < totalListContent.size()) {
            Object[] params = new Object[3];
            params[0] = totalListContent;
            params[1] = listContent;
            params[2] = localSongOffset;
            MusicLoaderService mls = new MusicLoaderService();
            mls.SAR = this;
            mls.execute(params);
            localSongOffset+=20;
        }
        //If we change the dir we MUST create a new list to avoid linking things that should not be there
        //Also this is called on boot, which ensures we have local songs when we go into playlists
        return mG;
    }

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

    public static Playlist getCombinedList() {
        return combinedList;
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
            Intent settings = new Intent(mViewPager.getContext(), Settings.class);
            startActivityForResult(settings, 0);

        }
        return super.onOptionsItemSelected(item);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences("dirInfo", 0);
        directory = settings.getString("dir", "Default");
        directoryChanged = settings.getBoolean("directoryChanged", false);
        SharedPreferences.Editor editor = settings.edit();
        if (directoryChanged)
        {
            editor.remove("directoryChanged");
            editor.commit();
        }
        settings = getSharedPreferences("SORT-TYPE", 0);
        sortType = settings.getString("sortType", "Default");
        sortOnLoad = settings.getBoolean("sortOnLoad", false);

        if (sortOnLoad)
        {
            sortLists(sortType, "Local");
            sortLists(sortType, "Spotify");
            sortLists(sortType, "Playlists");
            editor.putBoolean("sortOnLoad", false);
            editor.commit();
        }


        settings = getSharedPreferences("PLAYLIST-CHANGE", 0);
        playlistsChanged = settings.getBoolean("PlaylistsChanged", false);

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
        initSongLists();
        initListDisplay();
        initButtonListeners();
    }

    private void sortLists(String type, String list) {

        MusicSorter ms = null;
        Object[] retVal = null;
         if (list.equals("Local") && listContent != null)
         {
            ms = new MusicSorter(listContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            listContent.setSongInfo((ArrayList<Song>) retVal[0]);

             if (adapter != null) {
                 mainList.invalidateViews();
                 adapter.notifyDataSetChanged();
             }
         }
         else if (spotifyListContent != null && spotifyListContent.size() > 0 && list.equals("Spotify"))
         {
            ms = new MusicSorter(spotifyListContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            spotifyListContent.setSongInfo((ArrayList<Song>) retVal[0]);

             if (spotifyAdapter != null) {
                 mainList.invalidateViews();
                 spotifyAdapter.notifyDataSetChanged();
             }
        }

        else if (playlists != null && list.equals("Playlists"))
        {

            PlaylistSorter ps;
            ps = new PlaylistSorter(playlists, type);
            retVal = ps.getRetArr();
            playlists = ((ArrayList<Playlist>) retVal[0]);
            playlistNames = new ArrayList<String>();
            for (int i = 0; i < playlists.size(); i++)
            {
                playlistNames.add(playlists.get(i).getName());
            }
            if (playlistAdapter != null) {

                mainList.invalidateViews();
                playlistAdapter.notifyDataSetChanged();
            }
        }
    }

    public void initSongLists() {
        if (listContent == null)
        {
            listContent = new Playlist();
        }

        if (spotifyListContent == null)
        {
            spotifyListContent = new Playlist();
        }

        if (combinedList == null)
        {
            combinedList = new Playlist();
        }

        if (listContent.size() == 0 || isDirectoryChanged()) {
            mG = setMusicDir(directory);
            directoryChanged();
        }
        getSpotifyLibrary();

        if (playlists == null || playlists.size() == 0)
        {
            playlists = new ArrayList<Playlist>();
            playlistNames = new ArrayList<String>();
            getRemotePlaylists(getDomain());
        }
    }

    private void setPlaylistsChangedFlag(Boolean value) {
        SharedPreferences settings = getSharedPreferences("PLAYLIST-CHANGE", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("PlaylistsChanged", value);
        editor.commit();
    }


    //Only call this when you change domains
    public static void resetPlaylists()
    {
        playlists = null;
        playlistNames = null;
        dba = null;
    }

    public Boolean isDirectoryChanged() {
        return directoryChanged;
    }

    public void directoryChanged() {
        if (directoryChanged) {
            directoryChanged = false;
            return;
        }
        directoryChanged = true;
    }

    public void initListDisplay() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                listContent.getAdapterList());
        spotifyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                spotifyListContent.getAdapterList());
        playlistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                playlistNames);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (mViewPager.getCurrentItem() == 3)
                {
                    Intent playlist = new Intent(view.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("Playlist", playlists.get(position));
                    playlist.putExtras(b);
                    playerHandler.destroyPlayers();
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
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.Random);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.Rewind);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.Prev);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);

        playerHandler = new PlayerActionsHandler(this.getApplicationContext(),fabIO, prev, next, rewind, random, mainList, seekbar);

    }
    public void initButtonListeners() {

        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        loginButton.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHandler.destroyPlayers();
                Intent settings = new Intent(mViewPager.getContext(), LoginActivity.class);
                startActivityForResult(settings, 0);
            }
        });

        final Handler mHandler = new Handler();
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            //TODO: Optimize. Also need lists for spotify/googlemusic
            @Override
            public void onPageSelected(int position) {
                playerHandler.setCurrentSongListPosition(-1);
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        mainList.setAdapter(adapter);
                        mainList.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        mainList.setAdapter(spotifyAdapter);
                        if (playerHandler.isSpotifyLoggedOut())
                        {
                            loginButton.setVisibility(View.VISIBLE);
                        }
                        mainList.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        //Todo: change to googlemusicStrings
                        if ((spotifyListContent == null))
                        {
                            loginButton.setVisibility(View.VISIBLE);
                        }
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        mainList.setAdapter(playlistAdapter);
                        mainList.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //Seekbar tracker
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerHandler.updateSeekBar();
                mHandler.postDelayed(this, 1000);
            }
        });

    }

    public void getRemotePlaylists(String domain) {
        if (dba == null && isConnected()) {
            Object[] params = new Object[2];
            params[0] = "GetPlaylists";
            params[1] = domain;
            dba = new DatabaseActionsHandler();
            dba.SAR = this;
            dba.execute(params);
        }
    }

    public Boolean isConnected() {
        return !playerHandler.getNetworkConnectivity(this.getApplicationContext()).equals(Connectivity.OFFLINE);
    }

    public String getDomain() {
        final SharedPreferences domainSettings = getSharedPreferences("ONESTREAM_DOMAIN", 0);
        String oldDomain = domainSettings.getString("domain", "Admin");
        return oldDomain;
    }

    public void getSpotifyLibrary() {
        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        if (accessToken != null && isConnected()) {
            while (spotifySongOffset < 1000) {
                Object[] params = new Object[2];
                params[0] = accessToken;
                params[1] = spotifySongOffset;
                sMG = new SpotifyMusicGetter();
                sMG.SAR = this;
                sMG.execute(params);
                spotifySongOffset += 50;
            }

            playerHandler.initSpotifyPlayer(accessToken);
        }

    }

    //Called when threads return
    //Todo: We really really need to refactor to send return value AND what to do with it
    @Override
    public void processFinish(Object[] result) {
        String type = (String) result[0];
        Object retVal = result[1];
        if (retVal == null || type == null)
        {
            return;
        }
        //Case where retrieved from DB. Only DB lists are playlist array
        else if (type.equals("DatabaseActionsHandler"))
        {
            playlists = (ArrayList<Playlist>) retVal;
            for (int i = 0; i < playlists.size(); i++)
            {
                playlistNames.add(playlists.get(i).getName());
            }
            sortLists(sortType, "Playlists");
            playlistAdapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }

        else if (type.equals("MusicLoaderService")) {
            combinedList.addSongs(listContent.getSongInfo());
            if (listContent.size() == totalLocalSongs) {
                sortLists(sortType, "Local");
            }
            adapter.notifyDataSetChanged();
            mainList.invalidateViews();
        } else if (type.equals("SpotifyMusicGetter")) {
            ArrayList<Song> tempList = (ArrayList<Song>) retVal;
            if (tempList.size() < 20) {
                spotifySongOffset = 1000;
                sortLists(sortType, "Spotify");
            }

            spotifyListContent.addSongs(tempList);
            combinedList.addSongs(tempList);
            spotifyAdapter.notifyDataSetChanged();
            mainList.invalidateViews();

        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
    public static ArrayList<String> getPlaylistNames() {return playlistNames;}


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


