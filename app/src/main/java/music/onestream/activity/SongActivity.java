package music.onestream.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import music.onestream.R;
import music.onestream.song.SongAdapter;
import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
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

    static TextView artistName;
    static TextView albumName;
    static ImageView albumArt;
    static ActionBar title;
    static Resources resources;

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerHandler.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerHandler.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!playerHandler.isPlayerPlaying() && !playerHandler.isSpotifyPlaying()) {
            playerHandler.playSong(playerHandler.getCurrentSongListPosition());
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);

        mainList = (ListView) findViewById(R.id.ListViewSV);
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");



        artistName = (TextView) findViewById(R.id.artistName);
        albumName = (TextView) findViewById(R.id.albumName);
        albumArt = (ImageView) findViewById(R.id.album);
        resources = this.getResources();
        title = getSupportActionBar();

        initMainList();
        initPlayerHandler();

        Song song = playerHandler.getCurrentSong(playerHandler.getCurrentSongListPosition());
        initDisplay(song);
    }


    public static void initDisplay(Song song) {
        artistName.setText(song.getArtist());
        albumName.setText(song.getAlbum());
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
        Bitmap image = BitmapFactory.decodeByteArray(art, 0, art.length);

        image = Bitmap.createScaledBitmap(image, 300, 300, false);
        albumArt.setImageBitmap(image);
        setSongViewBackground(image);
    }

    public static int getAverageColor(Bitmap bmp) {
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int intArray[] = new int[bmp.getWidth()*bmp.getHeight()];
        bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return intArray[0];
    }

    public static void setSongViewBackground(Bitmap bitmap) {
        View rootView = albumArt.getRootView();
        int backgroundColor = getAverageColor(bitmap);
        rootView.setBackgroundColor(backgroundColor);
        int textColor = Color.rgb(255-Color.red(backgroundColor),
                255-Color.green(backgroundColor),
                255-Color.blue(backgroundColor));
        if (Math.abs(textColor - backgroundColor)<= 2500000)
        {
            textColor = Color.WHITE;
        }
            albumName.setTextColor(textColor);
            artistName.setTextColor(textColor);
    }

    private void initMainList() {
        adapter = new SongAdapter(this, R.layout.songlayout, playlist.getSongInfo());
        mainList.setAdapter(adapter);
    }


    private void initPlayerHandler() {
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIOSV);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.RandomSV);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.RewindSV);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.PrevSV);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.NextSV);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarSV);
        playerHandler =
                initPlayerHandler(this.getApplicationContext(), "SongActivity",
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
