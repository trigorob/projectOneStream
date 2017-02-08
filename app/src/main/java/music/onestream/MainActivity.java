package music.onestream;

import java.util.Arrays;
import java.util.Arrays.*;

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
import android.provider.MediaStore;
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
    private int spotifySongOffset= 0;

    private static String directory;
    private static String sortType;

    private static final String CLIENT_ID = "0785a1e619c34d11b2f50cb717c27da0";
    static final String PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged";

    // variable declaration
    private ListView mainList;
    private MusicGetter mG;
    private SpotifyMusicGetter sMG;
    private MediaPlayer mp;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> spotifyAdapter;
    private static String[] listContent;
    private static String[] spotifyListContent = {};
    private static Integer[] resID;
    private static Uri[] resURI;
    private static String[] spotURIStrings;
    int currentSongListPosition = -1;
    int currentSongPosition = -1;
    private SpotifyPlayer spotPlayer;
    boolean randomNext = false;
    private String currentSongType = "Local";
    private BroadcastReceiver mNetworkStateReceiver;


    //Todo: Make service (So next song happens when phone screen off)

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

                SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
                seekbar.setMax(mp.getDuration());
                final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
                fabIO.setImageResource(R.drawable.stop);
            }
        }
        else if (currentSongType.equals("Spotify"))
        {
            spotPlayer.resume(opCallback);
            spotPlayer.playUri(opCallback, spotURIStrings[currentSongListPosition], 0, currentSongPosition);

            SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
            final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
            fabIO.setImageResource(R.drawable.stop);
        }
        else {
            playSong(songIndex);
        }
    }

    // Play song
    public void playSong(int songIndex) {

        if (spotPlayer != null && spotPlayer.getPlaybackState() != null && spotPlayer.getPlaybackState().isPlaying)
        {
            spotPlayer.pause(opCallback);
        }
        if (mp.isPlaying())
        {
            mp.stop();
        }

        if (mG == null)
        {
            setMusicDir(mG,directory);
        }
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        if (mainList.getAdapter() == adapter) {
            mp.reset();
            if (resURI != null) {
                mp = MediaPlayer.create(getApplicationContext(), resURI[songIndex]);// creates new mediaplayer with song.
            } else {
                mp = MediaPlayer.create(getApplicationContext(), resID[songIndex]);// creates new mediaplayer with song.
            }
            mp.start();
            currentSongType = "Local";

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    if (!randomNext) {
                        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
                        next.performClick();
                    }
                    else {
                        playRandomSong();
                    }
                }});

            seekbar.setMax(mp.getDuration());
        }

        else if (mainList.getAdapter() == spotifyAdapter && spotPlayer != null) {
            if (!spotPlayer.isLoggedIn())
            {
                spotPlayer.login(CredentialsHandler.getToken(getApplicationContext()));
            }
            spotPlayer.playUri(opCallback, spotURIStrings[songIndex],0,0);
            currentSongType = "Spotify";
        }

        //Remote mp3 link
        else {
            try {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(this.getBaseContext(),
                        Uri.parse("https://p.scdn.co/mp3-preview/ae80da5d6bcb97facc0f60feac34fb53395f12c3?cid=null"));
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mp.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        fabIO.setImageResource(R.drawable.stop);
    }

    public MusicGetter setMusicDir(MusicGetter mG, String dir)
    {
        mG = new MusicGetter(dir);
        listContent = mG.files;
        if (!(mG.fileData == null))
        {
            resID = mG.fileData;
            resURI = null;
        }
        else
        {
            resURI = new Uri[mG.fileURI.length];
            for (int i = 0; i < mG.fileURI.length; i++) {
                resURI[i] = android.net.Uri.parse(mG.fileURI[i].toString());
            }
            resID = null;
        }

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

    @Override
    protected void onPause() {
        super.onPause();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (spotPlayer.getPlaybackState() != null && spotPlayer.getPlaybackState().isPlaying)
            {
                spotPlayer.pause(opCallback);
            }
            if (mp.isPlaying())
            {
                mp.stop();
            }

            Intent settings = new Intent(mViewPager.getContext(), Settings.class);
            startActivityForResult(settings, 0);

        }
        return super.onOptionsItemSelected(item);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SharedPreferences settings = getSharedPreferences("dirInfo", 0);
        final String directory = settings.getString("dir", "Default");
        settings = getSharedPreferences("SORT-TYPE", 0);
        sortType = settings.getString("sortType", "Default");

        mG = setMusicDir(mG, directory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //this to set listener back to this class
        sMG = new SpotifyMusicGetter();
        sMG.SAR = this;
        getSpotifyLibrary();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(0);
        mp = new MediaPlayer();

        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        final Handler mHandler = new Handler();
        //Make sure you update Seekbar on UI thread

        mainList = (ListView) findViewById(R.id.ListView1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContent);

        mainList.setAdapter(adapter);

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

        for (int i = 0; i < mainList.getChildCount(); i++) {
            View listItem = mainList.getChildAt(i);
            listItem.setBackgroundColor(Color.parseColor("#E0ECF8"));
        }

        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButton);
        loginButton.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    nextSong();
                };
            }});

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            //TODO: Optimize. Also need lists for spotify/soundcloud
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
                        if ((spotURIStrings != null && spotURIStrings.length == 0) &&
                                (spotPlayer == null || (spotPlayer != null && !spotPlayer.isLoggedIn())))
                        {
                            loginButton.setVisibility(View.VISIBLE);
                        }
                        mainList.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
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

    private void sortLists(String type) {

        if (type.equals("Default"))
        {
            return;
        }
        else
        {
            ParallelSorter ps = null;
            Object[] retVal = null;
            if (spotifyListContent != null && spotifyListContent.length>0) {
                ps = new ParallelSorter(spotifyListContent, null, spotURIStrings, null, type);
                retVal = ps.getRetArr();
                spotifyListContent = (String[]) retVal[0];
                spotURIStrings = (String[]) retVal[1];
            }
            if (listContent != null && listContent.length>0) {
                if (resID != null)
                {
                    ps =new ParallelSorter(listContent, null, null, resID, type);
                    retVal = ps.getRetArr();
                    listContent = (String[]) retVal[0];
                    resID = (Integer[]) retVal[1];
                }
                else if (resURI != null)
                {
                    ps = new ParallelSorter(listContent, resURI, null, null, type);
                    retVal = ps.getRetArr();
                    resURI = (Uri[]) retVal[1];
                }

            }
            return;
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


    //This is meant to ONLY be called through a service. Do NOT call this within the UI
    public void nextSongInService() {
        int next;
        int totalSongs = -1;
        if (currentSongType.equals("Spotify"))
        {
            totalSongs = spotURIStrings.length-1;
        }
        if (currentSongListPosition == -1)
        {
            return;
        }
        if (currentSongListPosition < totalSongs) {
            next = currentSongListPosition + 1;
        }
        else {
            next = 0;
        }
        View nextRow = getViewByPosition(mainList,next);
        playSong(next);
        currentSongListPosition = next;
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
        try {
            if (currentSongType.equals("Local")) {
                int choice = mG.selectRandomSongAsInt();
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
            else if (currentSongType.equals("Spotify")) {
                int choice = (int) (Math.random() * spotURIStrings.length);
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
        catch (IOException e) {}
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
        final String accessToken = CH.getToken(getBaseContext());
        if (accessToken != null) {
            sMG = new SpotifyMusicGetter();
            sMG.SAR = this;
            sMG.execute(accessToken);
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

            spotifyListContent = new String[jArray.length()];
            spotURIStrings = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++)
            {
                jsonObject =  (JSONObject) new JSONObject(jArray.get(i).toString()).get("track");
                spotURIStrings[i] =  (String) jsonObject.get("uri");
                spotifyListContent[i] =  (String) jsonObject.get("name");
            }
            sortLists(sortType);
        } catch (JSONException e) {}
        setupSpotifyAdapter();
    }

    public void setupSpotifyAdapter()
    {
        spotifyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spotifyListContent);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }


    //Spotify interface implementations
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)) {
            if (!randomNext) {
                final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
                next.performClick();
            }
            else {
                playRandomSong();
            }
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


