package music.onestream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlaylistActivity extends AppCompatActivity {
    private PlayerActionsHandler playerHandler;
    private Playlist playlist;
    private ArrayAdapter<String> adapter;
    ListView mainList;
    final Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        mainList = (ListView) findViewById(R.id.ListViewPL);

        initPlayerHandler();
        initSongList();
    }

    private void initSongList() {
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");

        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, playlist.getAdapterList());
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                    playerHandler.setCurrentSongListPosition(position);
                    mainList.setItemChecked(position, true);
                    playerHandler.playSong(position);
            }});
        //Seekbar tracker
        PlaylistActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerHandler.updateSeekBar();
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    private void initPlayerHandler() {
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIOPL);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.RandomPL);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.RewindPL);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.PrevPL);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.NextPL);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarPL);
        playerHandler = new PlayerActionsHandler(this.getApplicationContext(),fabIO, prev, next, rewind, random, mainList, seekbar);

        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        playerHandler.initSpotifyPlayer(accessToken);

    }

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
        playerHandler.onResume();
    }

}
