package music.onestream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.SpotifyPlayer;


/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlayerActionsHandler {

    final Context context;
    final FloatingActionButton fabIO;
    final FloatingActionButton prev;
    final FloatingActionButton next;
    final FloatingActionButton rewind;
    final FloatingActionButton random;
    final ListView mainList;
    final SeekBar seekBar;
    SpotifyPlayer spotPlayer;
    MediaPlayer mp;

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
            (Context context, FloatingActionButton play, FloatingActionButton previous, FloatingActionButton next,
             FloatingActionButton rewind, FloatingActionButton random, ListView list, SeekBar seekBar,
             MediaPlayer mediaPlayer, SpotifyPlayer spotifyPlayer)
    {
        this.context = context;
        this.fabIO = play;
        this.prev = previous;
        this.next = next;
        this.rewind = rewind;
        this.random = random;
        this.mainList = list;
        this.seekBar = seekBar;
        this.mp = mediaPlayer;
        this.spotPlayer = spotifyPlayer;

        initListeners();
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

    public boolean isSpotifyPlaying()
    {
        return spotPlayer != null && spotPlayer.getPlaybackState()!= null && spotPlayer.getPlaybackState().isPlaying;
    }

    public boolean isPlayerPlaying()
    {
        return mp != null && mp.isPlaying();
    }

    // Play song
    public void playSong(int songIndex) {

        resetPlayers();
        Song currentSong = MainActivity.getCombinedList().findSongByName((String) mainList.getAdapter().getItem(songIndex));
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
            mp = MediaPlayer.create(context, Uri.parse(currentSong.getUri()));// creates new mediaplayer with song.
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
        if (currentSongType.equals("Local"))
        {
            if (currentSongPosition != -1 && songIndex == currentSongListPosition)
            {
                mp.seekTo(currentSongPosition);
                mp.start(); // starting mediaplayer
                fabIO.setImageResource(R.drawable.stop);
            }
        }
        else if (currentSongType.equals("Spotify"))
        {
            spotPlayer.resume(opCallback);
            spotPlayer.playUri(opCallback, spotPlayer.getMetadata().contextUri, 0, currentSongPosition);
            fabIO.setImageResource(R.drawable.stop);
        }
        else
        {
            playSong(songIndex);
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
        View nextRow = getViewByPosition(mainList,next);
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

}
