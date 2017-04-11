package music.onestream.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import music.onestream.R;
import music.onestream.activity.OneStreamActivity;
import music.onestream.activity.SongActivity;
import music.onestream.musicgetter.ImageGetter;
import music.onestream.playlist.Playlist;
import music.onestream.service.OneStreamPlayerService;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlayerActionsHandler implements SeekBar.OnSeekBarChangeListener,
        Player.NotificationCallback, ConnectionStateCallback, AsyncResponse, MediaPlayer.OnPreparedListener {

    public static PlayerActionsHandler instance;

    public Context context;
    private ImageButton fabIO;
    private ImageButton prev;
    private ImageButton next;
    private ImageButton rewind;
    private ImageButton random;
    private ImageButton loginButton;
    ListView mainList;
    private SeekBar seekBar;

    private boolean receiverIsRegistered;
    private boolean serviceInit;
    private String parentClass;

    private MediaPlayer mp;
    private SpotifyPlayer spotPlayer = null;

    private BroadcastReceiver mNetworkStateReceiver;
    private Song currentSong;

    int currentSongListPosition = -1;
    private int currentSongPosition = -1;
    private String currentSongType = "";
    private int currentListSize = 0;
    Boolean randomNext = false;

    private final Player.OperationCallback opCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Error error) {

        }
    };

    public static PlayerActionsHandler initPlayerActionsHandler(Context context, ImageButton play,
             ImageButton previous, ImageButton next, ImageButton rewind, ImageButton random,
             ImageButton loginButton, ListView list, SeekBar seekBar, String parentClass) {

        if (instance == null) {
            instance = new PlayerActionsHandler();
            instance.receiverIsRegistered = false;
        }
        instance.initHandlerFields(context, play, previous, next, rewind,
                random, loginButton,
                list, seekBar, parentClass);
        instance.initListeners();
        instance.initPlayerService();

        return instance;
    }

    private void initHandlerFields(Context context, ImageButton play, ImageButton previous,
                                   ImageButton next, ImageButton rewind, ImageButton random,
                                   ImageButton loginButton, ListView list, SeekBar seekBar, String parentClass) {

        instance.context = context;
        instance.fabIO = play;
        instance.prev = previous;
        instance.next = next;
        instance.rewind = rewind;
        instance.random = random;
        instance.loginButton = loginButton;
        instance.mainList = list;
        instance.seekBar = seekBar;
        instance.parentClass = parentClass;
        if (isPlaying())
        {
            instance.fabIO.setImageResource(R.drawable.pause);
        }
        if (isRandomNext())
        {
            instance.random.setImageResource(R.drawable.shuffleoff);
        }
        if (currentSong != null) {
            if (isPlayerPlaying() && currentSongNotSpotify()) {
                seekBar.setMax(mp.getDuration());
            } else if (isSpotifyPlaying() && !currentSongNotSpotify()) {
                Metadata.Track track = spotPlayer.getMetadata().currentTrack;
                if (track != null) {
                    seekBar.setMax((int) track.durationMs);
                }
            }
        }

    }
    public boolean currentSongNotSpotify()
    {
        return (currentSongType.equals(Constants.local) ||
                currentSongType.equals(Constants.soundCloud));
    }

    public void setCurrentSongListPosition(int position)
    {
        this.currentSongListPosition = position;
    }

    public void setCurrentSongPosition(int position)
    {
        this.currentSongPosition = position;
    }

    public int getCurrentSongListPosition()
    {
        return this.currentSongListPosition;
    }

    public void setRandomNext(Boolean value)
    {
        this.randomNext = value;
        if (!randomNext)
        {
            random.setImageResource(R.drawable.shuffle);
        }
        else {
            random.setImageResource(R.drawable.shuffleoff);
        }
    }
    public Boolean isRandomNext()
    {
        return this.randomNext;
    }
    public void setCurrentListSize(int size) {
        currentListSize = size;
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
            currentSongPosition = -1;
            currentSongListPosition = -1;
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            intent.setAction(Constants.ACTION_INIT);
            intent.putExtra("currentActivity", parentClass);
            context.startService(intent);
            serviceInit = true;
        }
    }

    public void changePlayerServiceAdapter() {
        if (serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            intent.setAction(Constants.ACTION_INIT);
            intent.putExtra("currentActivity", parentClass);
            context.startService(intent);
        }
    }

    public void stopPlayerService() {
        if (serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            intent.setAction(Constants.ACTION_STOP);
            context.startService(intent);
            serviceInit = false;
        }
    }

    public void serviceIconPausePlay(boolean isPlaying) {
        if (serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            if (isPlaying) {
                intent.setAction(Constants.ACTION_ICON_PLAY);
            }
            else {
                intent.setAction(Constants.ACTION_ICON_PAUSE);
            }
            context.startService(intent);
        }
    }

    public void serviceIconShuffle() {
        if (serviceInit) {
            Intent intent = new Intent(context, OneStreamPlayerService.class);
            if (isRandomNext()) {
                intent.setAction(Constants.ACTION_ICON_SHUFFLE);
            }
            else {
                intent.setAction(Constants.ACTION_ICON_PAUSE);
            }
            context.startService(intent);
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
                        if (isPlaying())
                        {
                            stopSong();
                        }
                    }
                    else {
                        if (currentSongNotSpotify()) {
                            if (!isPlayerPlaying()) {
                                resumeSong(currentSongListPosition);
                            } else //Stop song.
                            {
                                stopSong();
                            }
                        }
                        else if (currentSongType.equals(Constants.spotify))
                        {
                            if (!isSpotifyPlaying()) {
                                resumeSong(currentSongListPosition);
                            } else //Stop song.
                            {
                                stopSong();
                            }

                        }
                        else {
                            playSong(currentSongListPosition);
                        }
                    }
                };
            }});

        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    setRandomNext(!randomNext);
                    serviceIconShuffle();
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
                    handleNext();
                };
            }});

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fabIO != null) {
                    fabIO.setImageResource(R.drawable.pause);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(isPlayerPlaying() && fromUser && currentSongNotSpotify())
                {
                    currentSongPosition = progress;
                    mp.seekTo(currentSongPosition);
                }
                else if (fromUser && currentSongType.equals(Constants.spotify) && isSpotifyPlaying())
                {
                    currentSongPosition = progress;
                    spotPlayer.resume(opCallback);
                    spotPlayer.seekToPosition(opCallback,progress);
                }
            }
        });

    }

    public void handleNext() {
        if (!randomNext) {
            nextSong();
        }
        else {
            playRandomSong();
        }
    }

    public void resetPlayers() {
        if (isSpotifyPlaying())
        {
            spotPlayer.pause(opCallback);
        }
        if (isPlayerPlaying())
        {
            mp.stop();
        }
    }

    public void initSpotifyPlayer(String accessToken, Boolean cachingOn) {

        Config playerConfig = new Config(context, accessToken, Constants.CLIENT_ID);

        if (cachingOn) {
            playerConfig.useCache(true);
        }
        else {
            playerConfig.useCache(false);
        }


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
                player.login(CredentialsHandler.getToken(context, Constants.spotify));
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
        return (spotPlayer == null || !spotPlayer.isLoggedIn());
    }
    public boolean isSoundCloudLoggedOut() {
        return (CredentialsHandler.getToken(context, Constants.soundCloud) == null);
    }


    public boolean isSpotifyPlaying()
    {
        return spotPlayer != null && spotPlayer.getPlaybackState()!= null && spotPlayer.getPlaybackState().isPlaying;
    }

    public boolean isPlayerPlaying()
    {
        try {
            return mp != null && mp.isPlaying();
        }
        catch (IllegalStateException e)
        {
            return false;
        }
    }

    public boolean isPlaying() {
        return isPlayerPlaying() || isSpotifyPlaying();
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

    public Song getCurrentSong() {
        return currentSong;
    }


    // Play song
    public void playSong(int songIndex) {
        resetPlayers();
        if (songIndex == -1 || currentListSize == 0)
        {
            currentSongListPosition = 0;
            songIndex = 0;
        }
        else if (songIndex == -1)
        {
            return;
        }

        currentSong = OneStreamActivity.getPlaylistHandler().getCurrentSongs().get(songIndex);
        if (currentSong == null) {
            return;
        }
        String type = currentSong.getType();

        if (viewingCurrentList())
        {
            setListSelection(songIndex);
        }

        if (!this.parentClass.equals(Constants.songActivity)
                && OneStreamActivity.shouldUseSongView()) {

            Intent songActivity = new Intent(context, SongActivity.class);
            Bundle b = new Bundle();
            Playlist p = new Playlist("", "", (OneStreamActivity.getPlaylistHandler().getCurrentSongs()));
            b.putSerializable("Playlist", p);
            //Don't use the actual index here, because we might be filtering the list
            b.putSerializable("songIndex", (p.getSongInfo().indexOf(currentSong)));
            songActivity.putExtras(b);
            songActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(songActivity);
            return;
        }

        if (type.equals(Constants.local)) {
            playLocalSong(currentSong);
        }
        else if (type.equals(Constants.spotify) && spotPlayer != null) {
            playSpotifySong(currentSong);
        }
        else if (type.equals(Constants.soundCloud)) {
            playSoundCloudSong(currentSong);
        }

        fabIO.setImageResource(R.drawable.pause);
        setSongViewDisplay(currentSong);
        serviceIconPausePlay(true);
    }

    public void setListSelection(int songIndex) {
        SongAdapter sAdapter = ((SongAdapter) mainList.getAdapter());
        sAdapter.setSelected(currentSong);
        if (mainList.getFirstVisiblePosition() > songIndex
                || mainList.getLastVisiblePosition() < songIndex) {
            mainList.smoothScrollToPositionFromTop(songIndex, 0, 0);
        }
        mainList.setItemChecked(songIndex, true);
    }

    public boolean viewingCurrentList() {
        return (OneStreamActivity.getPlaylistHandler().getCurrentSongs() != null &&
                OneStreamActivity.getPlaylistHandler().getCurrentSongs().equals
                        (((SongAdapter)mainList.getAdapter()).getFilteredSongs()));
    }

    public void setSongViewDisplay(Song song) {
        if (this.parentClass.equals(Constants.songActivity)) {
            SongActivity.initDisplay(song);
            if (song.getType().equals(Constants.spotify) || song.getType().equals(Constants.soundCloud))
            {
                String url = song.getAlbumArt();
                Object[] params = new Object[1];
                params[0] = url;
                ImageGetter imageGetter = new ImageGetter();
                imageGetter.SAR = this;
                imageGetter.execute(params);
            }
            else if (song.getType().equals(Constants.local))
            {
                SongActivity.setLocalAlbumArt(song);
            }
        }
    }

    public void setButtonColors(int filter)
    {
        if (filter == 0) {
            fabIO.setColorFilter(null);
            prev.setColorFilter(null);
            random.setColorFilter(null);
            next.setColorFilter(null);
            rewind.setColorFilter(null);
            seekBar.getThumb().setColorFilter(null);
            seekBar.getProgressDrawable().setColorFilter(null);
        }
        else {
            fabIO.setColorFilter(filter);
            prev.setColorFilter(filter);
            random.setColorFilter(filter);
            next.setColorFilter(filter);
            rewind.setColorFilter(filter);
            seekBar.getThumb().setColorFilter(filter, PorterDuff.Mode.SRC_ATOP);
            seekBar.getProgressDrawable().setColorFilter(filter, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void playLocalSong(Song currentSong) {
        mp = initMediaPlayer(mp, currentSong.getUri());
        mp.start();

        currentSongType = Constants.local;
        seekBar.setMax(mp.getDuration());
    }

    private MediaPlayer initMediaPlayer(MediaPlayer mp, String uri) {
        if (isPlayerPlaying()) {
            mp.reset();
        }
        if (mp != null) {
            mp.release();
        }
        if (!uri.contains("client_id")) {
            mp = MediaPlayer.create(context, Uri.parse(currentSong.getUri()));
        }
        else {
            mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try{
                mp.setDataSource(uri);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handleNext();
            }
        });

        return mp;
    }

    public void playSoundCloudSong(Song currentSong) {
        String uri = currentSong.getUri()+ "?client_id=" + Constants.SOUNDCLOUD_CLIENT_ID;
        currentSongType = Constants.soundCloud;
        mp = initMediaPlayer(mp, uri);
        mp.prepareAsync();
    }

    public void playSpotifySong(Song currentSong) {
        spotPlayer.refreshCache();
        if (isSpotifyLoggedOut())
        {
            spotPlayer.login(CredentialsHandler.getToken(context, Constants.spotify));
        }
        spotPlayer.playUri(opCallback, currentSong.getUri(),0,0);
        currentSongType = Constants.spotify;
    }

    public void stopSong() {

        if (isPlayerPlaying())
        {
            mp.pause();
            currentSongPosition = mp.getCurrentPosition();
        }
        else if (isSpotifyPlaying())
        {
            spotPlayer.pause(opCallback);
            currentSongPosition = (int) spotPlayer.getPlaybackState().positionMs;
        }
        fabIO.setImageResource(R.drawable.play);
        serviceIconPausePlay(false);
    }

    public void resumeSong(int songIndex)
    {
        if (currentSongPosition != -1 && songIndex == currentSongListPosition) {
            if (currentSongNotSpotify()) {
                mp.seekTo(currentSongPosition);
                mp.start(); // starting mediaplayer
                fabIO.setImageResource(R.drawable.pause);

            } else if (currentSongType.equals(Constants.spotify)) {
               spotPlayer.resume(opCallback);
                spotPlayer.playUri(opCallback, spotPlayer.getMetadata().contextUri, 0, currentSongPosition);
                fabIO.setImageResource(R.drawable.pause);
            } else {
                playSong(songIndex);
            }
            serviceIconPausePlay(true);
            if (viewingCurrentList())
            {
                setListSelection(songIndex);
            }
        }
    }

    public void nextSong() {
        int next;
        if (currentSongListPosition == -1)
        {
            return;
        }
        if (currentSongListPosition < currentListSize-1) {
            next = currentSongListPosition + 1;
        }
        else {
            next = 0;
        }
        currentSongListPosition = next;
        playSong(next);
    }


    public void playRandomSong() {
        if (currentListSize > 0) {
            int choice = (int) (Math.random() * currentListSize);
            playSong(choice);
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
            next = currentListSize-1;
        }
        currentSongListPosition = next;
        playSong(next);
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
        if(mp != null && fromUser && currentSongNotSpotify()){
            setCurrentSongPosition(progress);
            mp.seekTo(progress);
        }
        else if (spotPlayer!= null && fromUser && currentSongType.equals(Constants.spotify))
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
        if (mp != null) {
            mp.release();
        }
        if (spotPlayer != null) {
            spotPlayer.pause(opCallback);
        }
        stopPlayerService();
    }

    //Interface required implementations
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onLoggedIn()
    {
        if (loginButton != null) {
            OneStreamActivity.setLoginButtonVisible(false, loginButton);
        }
    }
    @Override
    public void onLoggedOut() {
    }
    @Override
    public void onLoginFailed(Error error) {

    }
    @Override
    public void onTemporaryError() {}
    @Override
    public void onConnectionMessage(String s) {}

    //Spotify interface implementations
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)) {
            handleNext();
        }
        else if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyTrackChanged)) {
            Metadata.Track track = spotPlayer.getMetadata().currentTrack;
            if (track != null)
            {
                seekBar.setMax ((int) track.durationMs);
            }
        }
        else if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyPlay)) {
            if (isPlayerPlaying())
            {
                mp.stop();
            }
        }
    }

    @Override
    public void processFinish(Object[] result) {
        if (result == null || result.length == 0)
        {
            return;
        }

        BitmapDrawable image = (BitmapDrawable) result[0];
        Bitmap bitmap = image.getBitmap();
        if (image != null) {
            SongActivity.setAlbumArt(bitmap);
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        //If a song has a broken link, remove it from the list of songs so it doesn't cause any more trouble
        if (error.toString().equals(Constants.spotifyPlaybackFailed)) {
            Song invalidSong = getCurrentSong();
            OneStreamActivity.getPlaylistHandler().getList(Constants.spotify).removeSongItem(invalidSong);
            OneStreamActivity.getPlaylistHandler().getList(Constants.library).removeSongItem(invalidSong);
            OneStreamActivity.notifyLibraryAdapter();
            OneStreamActivity.notifySpotifyAdapter();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (currentSongType.equals(Constants.soundCloud)) {
            if (isSpotifyPlaying()) {
                resetPlayers();
            }
            mp.start();
        }
        seekBar.setMax(mp.getDuration());
    }
}
