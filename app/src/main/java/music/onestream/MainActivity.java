package music.onestream;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import android.content.BroadcastReceiver;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, AsyncResponse, Player.NotificationCallback, ConnectionStateCallback {

    //Increment this after getting spotify songs. TODO: Implement this functionality
    private static int spotifySongOffset= 0;

    private static String directory;
    private static Boolean directoryChanged = false;
    private static String sortType;
    private static DatabaseActionsHandler dba;

    private static final String CLIENT_ID = "0785a1e619c34d11b2f50cb717c27da0";
    static final String PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged";

    private PlayerActionsHandler playerHandler;

    // variable declaration
    private ListView mainList;
    private MusicGetter mG;
    private SpotifyMusicGetter sMG;
    private MediaPlayer mp;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> spotifyAdapter;
    private ArrayAdapter<String> playlistAdapter;
    private static Playlist listContent;
    private static Playlist spotifyListContent;
    private static ArrayList<Playlist> playlists;
    private static ArrayList<String> playlistNames;
    private SpotifyPlayer spotPlayer;
    private String currentSongType = "Local";
    private BroadcastReceiver mNetworkStateReceiver;
    private static Playlist combinedList;

    //Callback for spotify player
    private final Player.OperationCallback opCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Error error) {

        }
};

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
        Playlist listContent = new Playlist();
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
        mp.release();
        if (spotPlayer != null) {
            Spotify.destroyPlayer(this);
        }
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

    public boolean isSpotifyLoggedIn()
    {
        return (spotPlayer == null || (spotPlayer != null && !spotPlayer.isLoggedIn()));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (spotPlayer != null && !playerHandler.isSpotifyPlaying())
        {
            unregisterReceiver(mNetworkStateReceiver);

            // Note that calling Spotify.destroyPlayer() will also remove any callbacks on whatever
            // instance was passed as the refcounted owner. So in the case of this particular example,
            // it's not strictly necessary to call these methods, however it is generally good practice
            // and also will prevent your application from doing extra work in the background when
            // paused.
            if (spotPlayer != null) {
                spotPlayer.removeNotificationCallback(MainActivity.this);
                spotPlayer.removeConnectionStateCallback(MainActivity.this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the broadcast receiver for network events. Note that we also unregister
        // this receiver again in onPause().
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (spotPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    spotPlayer.setConnectivityStatus(opCallback, connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (spotPlayer != null) {
            spotPlayer.addNotificationCallback(MainActivity.this);
            spotPlayer.addConnectionStateCallback(MainActivity.this);
        }
    }

    public void destroyPlayers() {
        if (spotPlayer != null  &&  spotPlayer.getPlaybackState() != null
                && spotPlayer.getPlaybackState().isPlaying)
        {
            spotPlayer.pause(opCallback);
        }
        if (mp.isPlaying())
        {
            mp.stop();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            destroyPlayers();
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
        if (directoryChanged)
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("directoryChanged");
            editor.commit();
        }
        settings = getSharedPreferences("SORT-TYPE", 0);
        sortType = settings.getString("sortType", "Default");

        initSongLists();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(0);

        mp = new MediaPlayer();

        initListDisplay();
        initButtonListeners();
    }

    private void sortLists(String type, String list) {

        ParallelSorter ps = null;
        Object[] retVal = null;
         if (list.equals("Local") && listContent != null)
         {
            ps = new ParallelSorter(listContent.getSongInfo(), type);
            retVal = ps.getRetArr();
            listContent.setSongInfo((ArrayList<Song>) retVal[0]);

             if (adapter != null) {
                 mainList.invalidateViews();
                 adapter.notifyDataSetChanged();
             }
         }
         else if (spotifyListContent != null && spotifyListContent.size() > 0 && list.equals("Spotify"))
         {
            ps = new ParallelSorter(spotifyListContent.getSongInfo(), type);
            retVal = ps.getRetArr();
            spotifyListContent.setSongInfo((ArrayList<Song>) retVal[0]);

             if (spotifyAdapter != null) {
                 mainList.invalidateViews();
                 spotifyAdapter.notifyDataSetChanged();
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

        if (spotifyListContent != null && spotifyListContent.size() > 0)
        {
            sortLists(sortType, "Spotify");
        }

        if (playlists == null || playlists.size() == 0)
        {
            playlists = new ArrayList<Playlist>();
            playlistNames = new ArrayList<String>();
            getRemotePlaylists();
        }
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
        mainList = (ListView) findViewById(R.id.ListView1);
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
                    startActivityForResult(playlist, 0);

                }
                else {
                    playerHandler.setCurrentSongListPosition(position);
                    mainList.setItemChecked(position, true);
                    playerHandler.playSong(position);
                }
            }});
    }

    public void initButtonListeners() {

        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        loginButton.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyPlayers();
                Intent settings = new Intent(mViewPager.getContext(), LoginActivity.class);
                startActivityForResult(settings, 0);
            }
        });


        final Handler mHandler = new Handler();
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.Random);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.Rewind);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.Prev);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);

        playerHandler = new PlayerActionsHandler(this.getApplicationContext(),fabIO, prev, next, rewind, random, mainList, seekbar, mp, spotPlayer);

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
                        if (isSpotifyLoggedIn())
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
                try {
                    if (playerHandler.isPlayerPlaying()) {
                        int mCurrentPosition = playerHandler.getMediaPlayer().getCurrentPosition();
                        seekbar.setProgress(mCurrentPosition);
                    }
                    else if (playerHandler.isSpotifyPlaying())
                    {
                        int mCurrentPosition = (int) spotPlayer.getPlaybackState().positionMs;
                        seekbar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
                catch (IllegalStateException IE)
                {

                }
            }
        });

    }

    //Use this to grab a row from music list
    public View getViewByPosition(ListView listView, int pos) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void getRemotePlaylists() {
        Object[] params = new Object[2];
        params[0] = "GetPlaylists";
        params[1] = "Admin";
        dba = new DatabaseActionsHandler();
        dba.SAR = this;
        dba.execute(params);
    }

    public void getSpotifyLibrary() {
        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        if (accessToken != null) {
            while (spotifySongOffset < 1000)
            {
                Object[] params = new Object[2];
                params[0] = accessToken;
                params[1] = spotifySongOffset;
                sMG = new SpotifyMusicGetter();
                sMG.SAR = this;
                sMG.execute(params);
                spotifySongOffset += 50;
            }
        }

        Config playerConfig = new Config(getApplicationContext(), accessToken, CLIENT_ID);
        playerConfig.useCache(false); //Prevent memory leaks from spotify!
        // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
        // the second argument in order to refcount it properly. Note that the method
        // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
        // one passed in here. If you pass different instances to Spotify.getPlayer() and
        // Spotify.destroyPlayer(), that will definitely result in resource leaks.

        spotPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer player) {
                    player.setConnectivityStatus(opCallback, getNetworkConnectivity(MainActivity.this));
                    player.addNotificationCallback(MainActivity.this);
                    player.addConnectionStateCallback(MainActivity.this);
                    player.login(CredentialsHandler.getToken(getApplicationContext(), "Spotify"));
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();}
        });

    }

    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mp != null && fromUser && currentSongType.equals("Local")){
            playerHandler.setCurrentSongPosition(progress);
            mp.seekTo(progress);
        }
        else if (spotPlayer!= null && fromUser && currentSongType.equals("Spotify"))
        {
            playerHandler.setCurrentSongPosition(progress);
            spotPlayer.resume(opCallback);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //AutogenStub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //AutogenStub
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
            playlistAdapter.notifyDataSetChanged();
            mainList.invalidateViews();
        }

        else if (type.equals("MusicLoaderService")) {
            listContent.addSongs(((Playlist) retVal).getSongInfo());
            combinedList.addSongs(((Playlist) retVal).getSongInfo());
            if (listContent.size() == listContent.size()) {
                sortLists(sortType, "Local");
            }
            adapter.notifyDataSetChanged();
            mainList.invalidateViews();
        } else if (type.equals("SpotifyMusicGetter")) {
            ArrayList<Song> tempList = (ArrayList<Song>) retVal;
            if (tempList.size() < 20) {
                spotifySongOffset = 1000;
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

    /*
    Call this when adding a new playlist. We want to make a call to the server whenever we add/change a playlist
    It's a bit slower this way, but the implementation is MUCH simpler conceptually.
    Optimizing this is a nice-to-have
     */
    public static void resetPlaylists() {
        playlists = null;
    }


    //Spotify interface implementations
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)) {
                final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
                next.performClick();
        }
        else if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyTrackChanged)) {
            SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
            seekbar.setMax((int) spotPlayer.getMetadata().currentTrack.durationMs);
        }
    }
    @Override
    public void onPlaybackError(Error error) {}
    @Override
    public void onLoggedIn() {
        Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        loginButton.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onLoggedOut() {}
    @Override
    public void onLoginFailed(Error error) {}
    @Override
    public void onTemporaryError() {}
    @Override
    public void onConnectionMessage(String s) {}

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


