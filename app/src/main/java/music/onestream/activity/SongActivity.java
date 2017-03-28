package music.onestream.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import music.onestream.R;
import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;
import music.onestream.util.ColorCalculator;
import music.onestream.util.Constants;
import music.onestream.util.PlayerActionsHandler;

/**
 * Created by jovan on 2/25/17.
 */

public class SongActivity extends OSActivity {

    private PlayerActionsHandler playerHandler;
    private static Playlist playlist;
    private ListView mainList;
    private static ArrayAdapter<Song> adapter;
    final Handler mHandler = new Handler();

    static TextView artistOrGenreName;
    static TextView albumName;
    static ImageView albumArt;
    static ActionBar title;
    static Resources resources;

    boolean startedSongActivity;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayerHandler();
        if (!playerHandler.isPlaying() && startedSongActivity) {
            playerHandler.playSong(playerHandler.getCurrentSongListPosition());
            playerHandler.serviceIconPausePlay(true);
            startedSongActivity = false;
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);

        mainList = (ListView) findViewById(R.id.ListViewSV);
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");

        artistOrGenreName = (TextView) findViewById(R.id.artistOrGenreName);
        albumName = (TextView) findViewById(R.id.albumName);
        albumArt = (ImageView) findViewById(R.id.album);
        resources = this.getResources();
        title = getSupportActionBar();
        startedSongActivity = true;

        initMainList();
        initPlayerHandler();

        Song song = playerHandler.getCurrentSong();
        initDisplay(song);
    }


    public static void initDisplay(Song song) {
        if (!song.getArtist().equals(Constants.defaultArtistsAlbumGenreName)) {
            artistOrGenreName.setText(song.getArtist());
        }
        if (!song.getAlbum().equals(Constants.defaultArtistsAlbumGenreName))
        {
            albumName.setText(song.getAlbum());
        }
        title.setTitle(song.getName());
        String artURL = song.getAlbumArt();

        if (artURL == null)
        {
            albumArt.setImageResource(R.mipmap.logo);
        }
        else if (song.getType().equals("Local"))
        {
            setLocalAlbumArt(song);
        }
    }

    public static void setAlbumArt(Bitmap bmp) {
        if (bmp != null) {
            albumArt.setImageBitmap(bmp);
            albumArt.invalidate();
        }
        setSongViewBackground(bmp);
    }

    public static void setLocalAlbumArt(Song song) {

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(song.getAlbumArt());
        byte[] art = mmr.getEmbeddedPicture();
        Bitmap image = ColorCalculator.getBitmapFromBytes(art);
        albumArt.setImageBitmap(image);
        setSongViewBackground(image);
    }

    public static void setSongViewBackground(Bitmap bitmap) {
        View rootView = albumArt.getRootView();
        int backgroundColor = ColorCalculator.getAverageColor(bitmap);
        rootView.setBackgroundColor(backgroundColor);
        int textColor = ColorCalculator.getColorInversion(backgroundColor);
        getPlayerHandler().setButtonColors(textColor);
        albumName.setTextColor(textColor);
        artistOrGenreName.setTextColor(textColor);
    }

    private void initMainList() {
        adapter = new SongAdapter(this, R.layout.songlayout, playlist.getSongInfo());
        mainList.setAdapter(adapter);
    }


    private void initPlayerHandler() {
        final ImageButton fabIO = (ImageButton) findViewById(R.id.fabIOSV);
        final ImageButton random = (ImageButton) findViewById(R.id.RandomSV);
        final ImageButton rewind = (ImageButton) findViewById(R.id.RewindSV);
        final ImageButton prev = (ImageButton) findViewById(R.id.PrevSV);
        final ImageButton next = (ImageButton) findViewById(R.id.NextSV);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarSV);
        playerHandler =
                initPlayerHandler(this.getApplicationContext(), Constants.songActivity,
                        null, fabIO, prev, next, rewind,
                        random, seekbar, mainList);

        int songIndex = getIntent().getIntExtra("songIndex", -1);
        playerHandler.setCurrentSongListPosition(songIndex);

        //Seekbar tracker
        SongActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerHandler.updateSeekBar();
                mHandler.postDelayed(this, 1000);
            }
        });

    }

}
