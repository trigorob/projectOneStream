package music.onestream;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, AsyncResponse, Player.NotificationCallback, ConnectionStateCallback {

    //Increment this after getting spotify songs. TODO: Implement this functionality
    private static int spotifySongOffset= 0;

    private static String directory;
    private static String sortType;

    private static final String CLIENT_ID = "0785a1e619c34d11b2f50cb717c27da0";
    static final String PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged";

    // variable declaration
    private ListView mainList;
    private MusicGetter mG;
    private SpotifyMusicGetter sMG;
    private MediaPlayer mp;
    private ArrayAdapter<Song> adapter;
    private ArrayAdapter<Song> spotifyAdapter;
    private static ArrayList<Song> listContent;
    private static ArrayList<Song> spotifyListContent;
    private int currentSongListPosition = -1;
    private int currentSongPosition = -1;
    private SpotifyPlayer spotPlayer;
    boolean randomNext = false;
    private String currentSongType = "Local";
    private BroadcastReceiver mNetworkStateReceiver;
    private static ArrayList<Song> combinedList;

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

    public void resumeSong(int songIndex) {
        if (currentSongType.equals("Local")) {
            if (currentSongPosition != -1 && songIndex == currentSongListPosition) {
                mp.seekTo(currentSongPosition);
                mp.start(); // starting mediaplayer
                final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
                fabIO.setImageResource(R.drawable.stop);
            }
        }
        else if (currentSongType.equals("Spotify"))
        {
            spotPlayer.resume(opCallback);
            spotPlayer.playUri(opCallback, spotifyListContent.get(currentSongListPosition).getUri(), 0, currentSongPosition);
            final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
            fabIO.setImageResource(R.drawable.stop);
        }
        else {
            playSong(songIndex);
        }
    }

    public void resetPlayers() {
        if (isSpotifyPlaying())
        {
            spotPlayer.pause(opCallback);
        }
        if (mp.isPlaying())
        {
            mp.stop();
        }
    }

    // Play song
    public void playSong(int songIndex) {

        resetPlayers();
        Song currentSong = (Song) (mainList.getAdapter().getItem(songIndex));
        String type = currentSong.getType();

        if (type.equals("Local") || type.equals("LocalRaw")) {
            playLocalSong(currentSong);
        }

        else if (type.equals("Spotify") && spotPlayer != null) {
            playSpotifySong(currentSong);
        }

        //Remote mp3 link example
        else {
            //Play google song here
        }
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        fabIO.setImageResource(R.drawable.stop);
    }

    public void playLocalSong(Song currentSong) {
        mp.reset();
        if (mG.getType() == 1) {
            mp = MediaPlayer.create(getApplicationContext(), Uri.parse(currentSong.getUri()));// creates new mediaplayer with song.
        } else {
            mp = MediaPlayer.create(getApplicationContext(), Integer.parseInt(currentSong.getUri()));// creates new mediaplayer with song.
        }
        mp.start();
        currentSongType = "Local";

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                    final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
                    next.performClick();
            }});
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setMax(mp.getDuration());
    }

    public void playSpotifySong(Song currentSong) {
        if (!spotPlayer.isLoggedIn())
        {
            spotPlayer.login(CredentialsHandler.getToken(getApplicationContext(), "Spotify"));
        }
        spotPlayer.playUri(opCallback, currentSong.getUri(),0,0);
        currentSongType = "Spotify";
    }



    public MusicGetter setMusicDir(String dir)
    {
        mG = new MusicGetter(dir);
        ArrayList<Song> totalListContent = mG.songs;
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
        createCombinedList();

        return mG;
    }

    public void stopSong() {

        if (currentSongType.equals("Local"))
        {
            mp.pause();
            currentSongPosition = mp.getCurrentPosition();
        }
        else if ((currentSongType.equals("Spotify")))
        {
            spotPlayer.pause(opCallback);
            currentSongPosition = (int) spotPlayer.getPlaybackState().positionMs;
        }

        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        fabIO.setImageResource(R.drawable.play);
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

    //Only call this when we need to remake the list. Other than that, add things as they come
    private void createCombinedList() {
        //Always create from scratch incase new data loaded && avoid duplicates.
        combinedList = new ArrayList<Song>();

        if (listContent != null) {
            for (int i = 0; i < listContent.size(); i++)
            {
                combinedList.add(listContent.get(i));
            }
        }
        if (spotifyListContent != null) {
            for (int i = 0; i < spotifyListContent.size(); i++)
            {
                combinedList.add(spotifyListContent.get(i));
            }
        }
    }

    public static ArrayList<Song> getCombinedList() {
        return combinedList;
    }

    public boolean isSpotifyPlaying()
    {
        return spotPlayer != null && spotPlayer.getPlaybackState()!= null && spotPlayer.getPlaybackState().isPlaying;
    }

    public boolean isSpotifyLoggedIn()
    {
        return (spotPlayer == null || (spotPlayer != null && !spotPlayer.isLoggedIn()));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (spotPlayer != null && !isSpotifyPlaying())
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
            ps = new ParallelSorter(listContent, type);
            retVal = ps.getRetArr();
            listContent = (ArrayList<Song>) retVal[0];

             if (adapter != null) {
                 mainList.invalidateViews();
                 adapter.notifyDataSetChanged();
             }
         }
         else if (spotifyListContent != null && spotifyListContent.size() > 0 && list.equals("Spotify"))
         {
            ps = new ParallelSorter(spotifyListContent, type);
            retVal = ps.getRetArr();
            spotifyListContent = (ArrayList<Song>) retVal[0];

             if (spotifyAdapter != null) {
                 mainList.invalidateViews();
                 spotifyAdapter.notifyDataSetChanged();
             }
        }
    }

    public void previousSong() {

        int next;
        if (currentSongListPosition == -1)
        {
            return;
        }

        if (currentSongListPosition > 0) {
            next = currentSongListPosition - 1;
        }
        else {
            next = mainList.getCount()-1;
        }
        View nextRow = getViewByPosition(mainList,next);

        if (currentSongListPosition != -1) {
            View oldRow = getViewByPosition(mainList, currentSongListPosition);
            oldRow.setBackgroundColor(getResources().getColor(R.color.default_color));
        }
        mainList.requestFocusFromTouch();
        mainList.performItemClick(mainList, next, mainList.getItemIdAtPosition(next));
        nextRow.setBackgroundColor(Color.parseColor("#E0ECF8"));
        currentSongListPosition = next;
    }

    public void initSongLists() {
        if (listContent == null)
        {
            listContent = new ArrayList<Song>();
        }

        if (spotifyListContent == null)
        {
            spotifyListContent = new ArrayList<Song>();
        }

        if (combinedList == null)
        {
            combinedList = new ArrayList<Song>();
        }

        mG = setMusicDir(directory);
        getSpotifyLibrary();

        if (spotifyListContent != null && spotifyListContent.size() > 0)
        {
            sortLists(sortType, "Spotify");
        }
    }

    public void initListDisplay() {
        mainList = (ListView) findViewById(R.id.ListView1);
        adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, listContent);
        spotifyAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, spotifyListContent);

        if (listContent != null && listContent.size() > 0) {
            mainList.setAdapter(adapter);
        }

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (currentSongListPosition != -1) {
                    getViewByPosition(mainList, currentSongListPosition).setBackgroundColor(getResources().getColor(R.color.default_color));
                }
                currentSongListPosition = position;
                mainList.setItemChecked(currentSongListPosition,true);
                getViewByPosition(mainList,currentSongListPosition).setBackgroundColor(Color.parseColor("#E0ECF8"));
                playSong(position);
            }});
    }

    public void initButtonListeners() {

        final Handler mHandler = new Handler();
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
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

        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        fabIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //Play or resume song

                    if (currentSongListPosition == -1)
                    {
                        if (mp.isPlaying() || (spotPlayer != null && spotPlayer.getPlaybackState().isPlaying))
                        {
                            stopSong();
                        }
                    }
                    else {
                        if (currentSongType.equals("Local")) {
                            if (!mp.isPlaying()) {
                                resumeSong(currentSongListPosition);
                            } else //Stop song.
                            {
                                stopSong();
                            }
                        }
                        else if (currentSongType.equals("Spotify"))
                        {
                            if (!spotPlayer.getPlaybackState().isPlaying) {
                                resumeSong(currentSongListPosition);
                            } else //Stop song.
                            {
                                stopSong();
                            }

                        }
                    }
                };
            }});

        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.Random);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (randomNext == true)
                    {
                        randomNext = false;
                        random.setImageResource(R.drawable.shuffle);
                    }
                    else
                    {
                        randomNext = true;
                        random.setImageResource(R.drawable.notrandom);
                    }
                };
            }});

        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.Rewind);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (currentSongListPosition != -1) {
                        playSong(currentSongListPosition);
                    }
                };
            }});

        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.Prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    previousSong();
                };
            }});


        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (!randomNext) {
                        nextSong();
                    }
                    else {
                        playRandomSong();
                    }
                };
            }});


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            //TODO: Optimize. Also need lists for spotify/googlemusic
            @Override
            public void onPageSelected(int position) {
                currentSongListPosition = -1;
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        mainList.setAdapter(adapter);
                        mainList.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        mainList.setAdapter(spotifyAdapter);
                        //Sometimes logged in but player is not. We check length for handling that
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
                        loginButton.setVisibility(View.INVISIBLE);
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp != null && fromUser && currentSongType.equals("Local")){
                    currentSongPosition = progress;
                    mp.seekTo(currentSongPosition);
                }
                else if (fromUser && currentSongType.equals("Spotify"))
                {
                    currentSongPosition = progress;
                    spotPlayer.resume(opCallback);
                    spotPlayer.seekToPosition(opCallback,progress);
                }
            }
        });

        //Seekbar tracker
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mp != null && mp.isPlaying()) {
                        int mCurrentPosition = mp.getCurrentPosition();
                        seekbar.setProgress(mCurrentPosition);
                    }
                    else if (spotPlayer != null && spotPlayer.getPlaybackState() != null && spotPlayer.getPlaybackState().isPlaying)
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

    public void nextSong() {
        int next;
        if (currentSongListPosition == -1)
        {
            return;
        }
        if (currentSongListPosition < mainList.getCount()-1) {
            next = currentSongListPosition + 1;
        }
        else {
            next = 0;
        }
        View nextRow = getViewByPosition(mainList,next);

        if (currentSongListPosition != -1) {
            View oldRow = getViewByPosition(mainList, currentSongListPosition);
            oldRow.setBackgroundColor(getResources().getColor(R.color.default_color));
        }
        mainList.requestFocusFromTouch();
        mainList.performItemClick(mainList, next, mainList.getItemIdAtPosition(next));
        nextRow.setBackgroundColor(Color.parseColor("#E0ECF8"));
        currentSongListPosition = next;
    }


    public void playRandomSong() {
        if (mainList.getAdapter().getCount() > 0) {
            int choice = (int) (Math.random() * mainList.getAdapter().getCount());
            View choiceRow = getViewByPosition(mainList, choice);

            if (currentSongListPosition != -1) {
                View oldRow = getViewByPosition(mainList, currentSongListPosition);
                oldRow.setBackgroundColor(getResources().getColor(R.color.default_color));
            }
            mainList.requestFocusFromTouch();
            mainList.performItemClick(mainList, choice, mainList.getItemIdAtPosition(choice));
            choiceRow.setBackgroundColor(Color.parseColor("#E0ECF8"));
            currentSongListPosition = choice;
        }
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
            currentSongPosition = progress;
            mp.seekTo(progress);
        }
        else if (spotPlayer!= null && fromUser && currentSongType.equals("Spotify"))
        {
            currentSongPosition = progress;
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

    public ArrayList<Song> processFinish (ArrayList<Song> output) {
        listContent = output;
        adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, listContent);
        mainList.invalidateViews();
        adapter.notifyDataSetChanged();
        mainList.setAdapter(adapter);
        if (listContent.size() == listContent.size())
        {
            sortLists(sortType, "Local");
        }
        return output;
    }

    //Called when spotifyMusicGetter gets music
    @Override
    public void processFinish(String output) {
        if (output == null)
        {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(output);
            JSONArray jArray = jsonObject.getJSONArray("items");
            JSONArray artArray = null;
            JSONArray albumArray = null;
            String album = "";
            String artist = "";

            ArrayList<Song> tempList = spotifyListContent;

            for (int i = 0; i < jArray.length(); i++)
            {
                jsonObject =  (JSONObject) new JSONObject(jArray.get(i).toString()).get("track");
                try {
                    artist = (String) jsonObject.getJSONArray("artists").getJSONObject(0).get("name");
                    album = (String) jsonObject.getJSONObject("album").get("name");
                }
                catch (JSONException je)
                {
                    artist = "<Unknown>";
                    album = "<Unknown>";
                }
                if (artist == null || artist.equals(""))
                {
                    artist = "<Unknown>";
                }
                if (album == null || album.equals(""))
                {
                    album = "<Unknown>";
                }
                Song song = new Song ((String) jsonObject.get("name"),
                        (String) jsonObject.get("uri"), artist, album, "Spotify", i);
                tempList.add(song);
                combinedList.add(song);
            }
            if (tempList.size() < 20)
            {
                spotifySongOffset = 1000;
            }
            spotifyListContent = tempList;
            mainList.invalidateViews();
            spotifyAdapter.notifyDataSetChanged();
            spotifyAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, spotifyListContent);
        } catch (JSONException e) {}
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
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


