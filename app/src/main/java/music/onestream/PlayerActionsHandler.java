package music.onestream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;


/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlayerActionsHandler implements SeekBar.OnSeekBarChangeListener, Player.NotificationCallback, ConnectionStateCallback {


    private static final String CLIENT_ID = "0785a1e619c34d11b2f50cb717c27da0";
    static final String PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged";

    final Context context;
    final FloatingActionButton fabIO;
    final FloatingActionButton prev;
    final FloatingActionButton next;
    final FloatingActionButton rewind;
    final FloatingActionButton random;
    final Button loginButton;
    final ListView mainList;
    final SeekBar seekBar;

    boolean receiverIsRegistered;
    boolean serviceInit;
    String parentClass;

    MediaPlayer mp;
    SpotifyPlayer spotPlayer = null;

    private BroadcastReceiver mNetworkStateReceiver;

    int currentSongListPosition = -1;
    int currentSongPosition = -1;
    String currentSongType = "";
    Boolean randomNext = false;

    private final Player.OperationCallback opCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Error error) {

        }
    };

    public PlayerActionsHandler
            (Context context, FloatingActionButton play, FloatingActionButton previous,
             FloatingActionButton next, FloatingActionButton rewind, FloatingActionButton random,
             Button loginButton, ListView list, SeekBar seekBar, String parentClass)
    {
        this.context = context;
        this.fabIO = play;
        this.prev = previous;
        this.next = next;
        this.rewind = rewind;
        this.random = random;
        this.loginButton = loginButton;
        this.mainList = list;
        this.seekBar = seekBar;
        this.mp = new MediaPlayer();
        this.parentClass = parentClass;

        serviceInit = false;
        receiverIsRegistered = false;

        initListeners();
        initPlayerService();
    }

    public void setCurrentSongType(String type)
    {
        this.currentSongType = type;
    }

    public String getCurrentSongType()
    {
        return this.currentSongType;
    }

    public void setCurrentSongListPosition(int position)
    {
        this.currentSongListPosition = position;
    }

    public void setCurrentSongPosition(int position)
    {
        this.currentSongPosition = position;
    }

    public int getCurrentSongListPosition(int position)
    {
        return this.currentSongListPosition;
    }

    public int getCurrentSongPosition(int position)
    {
        return this.currentSongPosition;
    }
    public void setRandomNext(Boolean value)
    {
        this.randomNext = value;
    }
    public Boolean getRandomNext(Boolean value)
    {
        return this.randomNext;
    }
    public MediaPlayer getMediaPlayer()
    {
        return this.mp;
    }

    public SpotifyPlayer getSpotifyPlayer()
    {
        return this.spotPlayer;
    }

    public void initPlayerService() {
        if (!serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            intent.setAction(OneStreamPlayerService.ACTION_INIT);
            intent.putExtra("currentActivity", parentClass);
            context.startService(intent);
            serviceInit = true;
        }
    }

    public void changePlayerServiceAdapter() {
        if (serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            intent.setAction(OneStreamPlayerService.ACTION_INIT);
            intent.putExtra("currentActivity", parentClass);
            context.startService(intent);
        }
    }

    public void stopPlayerService() {
        if (serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            intent.setAction(OneStreamPlayerService.ACTION_STOP);
            context.startService(intent);
            context.stopService(new Intent(context, OneStreamPlayerService.class));
            serviceInit = false;
        }
    }

    public void initListeners() {

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

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (currentSongListPosition != -1) {
                        playSong(currentSongListPosition);
                    }
                };
            }});

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    previousSong();
                };
            }});

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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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

    public void initSpotifyPlayer(String accessToken) {

        Config playerConfig = new Config(context, accessToken, CLIENT_ID);
        playerConfig.useCache(false); //Prevent memory leaks from spotify!
        // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
        // the second argument in order to refcount it properly. Note that the method
        // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
        // one passed in here. If you pass different instances to Spotify.getPlayer() and
        // Spotify.destroyPlayer(), that will definitely result in resource leaks.

        spotPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer player) {
                player.setConnectivityStatus(opCallback, getNetworkConnectivity(context));
                player.addNotificationCallback(PlayerActionsHandler.this);
                player.addConnectionStateCallback(PlayerActionsHandler.this);
                player.login(CredentialsHandler.getToken(context, "Spotify"));
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }


    public Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }


    public boolean isSpotifyLoggedOut()
    {
        return (spotPlayer == null || (spotPlayer != null && !spotPlayer.isLoggedIn()));
    }


    public boolean isSpotifyPlaying()
    {
        return spotPlayer != null && spotPlayer.getPlaybackState()!= null && spotPlayer.getPlaybackState().isPlaying;
    }

    public boolean isPlayerPlaying()
    {
        return mp != null && mp.isPlaying();
    }

    public void updateSeekBar() {
        try {
            if (isPlayerPlaying()) {
                int mCurrentPosition = getMediaPlayer().getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
            }
            else if (isSpotifyPlaying())
            {
                int mCurrentPosition = (int) spotPlayer.getPlaybackState().positionMs;
                seekBar.setProgress(mCurrentPosition);
            }
        }
        catch (IllegalStateException IE)
        {

        }
    }

    public Song getCurrentSong(int songIndex) {
        return OneStreamActivity.getPlaylistHandler().getCombinedList()
                .findSongByName(mainList.getAdapter().getItem(songIndex).toString());
    }

    // Play song
    public void playSong(int songIndex) {

        resetPlayers();
        if (songIndex == -1 && mainList.getAdapter() != null && mainList.getAdapter().getCount() > 0)
        {
            currentSongListPosition = 0;
            songIndex = 0;
            mainList.setSelection(0);
        }
        else if (songIndex == -1)
        {
            return;
        }
        Song currentSong = getCurrentSong(songIndex);
        if (currentSong == null)
        {
            return;
        }
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
        fabIO.setImageResource(R.drawable.stop);
        mainList.setSelection(songIndex);
    }

    public void playLocalSong(Song currentSong) {
        mp.reset();
        if (!currentSong.getType().equals("LocalRaw")) {
            mp = MediaPlayer.create(context, Integer.parseInt(currentSong.getUri()));// creates new mediaplayer with song.
        } else {
            mp = MediaPlayer.create(context, Integer.parseInt(currentSong.getUri()));// creates new mediaplayer with song.
        }
        mp.start();
        currentSongType = "Local";

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }});
        seekBar.setMax(mp.getDuration());
    }

    public void playSpotifySong(Song currentSong) {
        spotPlayer.refreshCache();
        if (!spotPlayer.isLoggedIn())
        {
            spotPlayer.login(CredentialsHandler.getToken(context, "Spotify"));
        }
        spotPlayer.playUri(opCallback, currentSong.getUri(),0,0);
        currentSongType = "Spotify";
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
        fabIO.setImageResource(R.drawable.play);
    }

    public void resumeSong(int songIndex)
    {
        if (currentSongPosition != -1 && songIndex == currentSongListPosition) {
            if (currentSongType.equals("Local")) {
                mp.seekTo(currentSongPosition);
                mp.start(); // starting mediaplayer
                fabIO.setImageResource(R.drawable.stop);

            } else if (currentSongType.equals("Spotify")) {
               spotPlayer.resume(opCallback);
                spotPlayer.playUri(opCallback, spotPlayer.getMetadata().contextUri, 0, currentSongPosition);
                fabIO.setImageResource(R.drawable.stop);
            } else {
                playSong(songIndex);
            }
        }
        mainList.setSelection(songIndex);
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
        mainList.requestFocusFromTouch();
        mainList.performItemClick(mainList, next, mainList.getItemIdAtPosition(next));
        currentSongListPosition = next;
        mainList.setSelection(next);
    }


    public void playRandomSong() {
        if (mainList.getAdapter().getCount() > 0) {
            int choice = (int) (Math.random() * mainList.getAdapter().getCount());
            View choiceRow = getViewByPosition(mainList, choice);
            mainList.requestFocusFromTouch();
            mainList.performItemClick(mainList, choice, mainList.getItemIdAtPosition(choice));
            currentSongListPosition = choice;
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

        mainList.requestFocusFromTouch();
        mainList.performItemClick(mainList, next, mainList.getItemIdAtPosition(next));
        currentSongListPosition = next;
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mp != null && fromUser && currentSongType.equals("Local")){
            setCurrentSongPosition(progress);
            mp.seekTo(progress);
        }
        else if (spotPlayer!= null && fromUser && currentSongType.equals("Spotify"))
        {
            setCurrentSongPosition(progress);
            spotPlayer.resume(opCallback);

        }
    }

    public void onResume() {
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            if (spotPlayer != null) {
                Connectivity connectivity = getNetworkConnectivity(context);
                spotPlayer.setConnectivityStatus(opCallback, connectivity);
            }
        mp = new MediaPlayer();
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(mNetworkStateReceiver, filter);
            receiverIsRegistered = true;

        if (spotPlayer != null) {
            spotPlayer.addNotificationCallback(PlayerActionsHandler.this);
            spotPlayer.addConnectionStateCallback(PlayerActionsHandler.this);
        }
        initPlayerService();
        changePlayerServiceAdapter();
    }


    public void onPause() {
        if (spotPlayer != null && !isSpotifyPlaying())
        {
            if (receiverIsRegistered) {
                context.unregisterReceiver(mNetworkStateReceiver);
                receiverIsRegistered = false;
            }
            spotPlayer.removeNotificationCallback(PlayerActionsHandler.this);
            spotPlayer.removeConnectionStateCallback(PlayerActionsHandler.this);
        }
    }

    public void destroyPlayers() {
        if (isSpotifyPlaying())
        {
            spotPlayer.pause(opCallback);
        }
        if (isPlayerPlaying())
        {
            mp.stop();
        }
    }

    public void onDestroy() {
        mp.release();
        if (spotPlayer != null) {
            spotPlayer.pause(opCallback);
        }
    }

    //Interface required implementations
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onLoggedIn() {
        OneStreamActivity.setLoginButtonVisible(false, loginButton);
    }
    @Override
    public void onLoggedOut() {}
    @Override
    public void onLoginFailed(Error error) {}
    @Override
    public void onTemporaryError() {}
    @Override
    public void onConnectionMessage(String s) {}

    //Spotify interface implementations
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)) {
            next.performClick();
        }
        else if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyTrackChanged)) {
            seekBar.setMax((int) spotPlayer.getMetadata().currentTrack.durationMs);
        }
    }

    @Override
    public void onPlaybackError(Error error) {}

}
