package music.onestream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
        playerHandler.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerHandler.onResume();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);

        mainList = (ListView) findViewById(R.id.ListViewSV);
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");

        artistName = (TextView) findViewById(R.id.artistName);
        albumName = (TextView) findViewById(R.id.albumName);
        albumArt = (ImageView) findViewById(R.id.album);

        initMainList();
        initPlayerHandler();

        Song song = playerHandler.getCurrentSong(playerHandler.currentSongListPosition);
        initDisplay(song);
    }


    public static void initDisplay(Song song) {
        artistName.setText(song.getArtist());
        albumName.setText(song.getAlbum());
        String artURL = song.getAlbumArt();

        if (artURL == null)
        {
            albumArt.setImageResource(R.mipmap.logo);
        }
    }

    public static void setAlbumArt(BitmapDrawable bmp) {
        albumArt.setImageBitmap(bmp.getBitmap());
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
        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        playerHandler.initSpotifyPlayer(accessToken);

        int songIndex = getIntent().getIntExtra("songIndex", -1);
        playerHandler.currentSongListPosition = songIndex;

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
