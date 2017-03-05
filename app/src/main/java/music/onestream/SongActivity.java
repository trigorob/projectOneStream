package music.onestream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;

/**
 * Created by jovan on 2/25/17.
 */

public class SongActivity extends OSActivity {

    private static PlayerActionsHandler playerHandler;
    private static Playlist playlist;
    private static ListView mainList;
    private static ArrayAdapter<Song> adapter;
    final Handler mHandler = new Handler();

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

        initMainList();
        initPlayerHandler();
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
