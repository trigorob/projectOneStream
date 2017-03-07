package music.onestream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import music.onestream.R;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;
import music.onestream.playlist.Playlist;
import music.onestream.util.PlayerActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlaylistActivity extends OSActivity {
    private PlayerActionsHandler playerHandler;
    private Playlist playlist;
    private ListView mainList;
    private Button loginLauncherLinkerButton;
    final Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        mainList = (ListView) findViewById(R.id.ListViewPL);
        loginLauncherLinkerButton = (Button) findViewById(R.id.loginLauncherLinkerButtonPL);

        initPlayerHandler();
        initSongList();

        setTitle(playlist.getName());
    }

    public void checkForInvalidSongs(ArrayList<Song> playlistAdapter) {
        String services = "";
        Boolean spotifyAvailable = true;
        for (int i = 0; i < playlist.getSongInfo().size(); i++){
            Song s = playlist.getSongInfo().get(i);
            if (s.getType().equals("Local")) {
                File f = new File(s.getUri());
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    playlistAdapter.add(s);
                }
            }
            else if (s.getType().equals("Spotify") && playerHandler.isSpotifyLoggedOut() && spotifyAvailable)
            {
                loginLauncherLinkerButton.setVisibility(View.VISIBLE);
                mainList.setVisibility(View.INVISIBLE);
                services += " Spotify ";
                spotifyAvailable = false;
            }
            //TODO: Add google case for not logged in here
            else {
                playlistAdapter.add(s);
            }
        }
        String buttonText = "Login to Services to access Playlist: " + services;
        loginLauncherLinkerButton.setText(buttonText);

    }

    private void initSongList() {
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        ArrayList<Song> playlistAdapter = new ArrayList<Song>();

        //Handle songs not accessible by device
        checkForInvalidSongs(playlistAdapter);

        ArrayAdapter<Song> adapter = new SongAdapter(this, R.layout.songlayout, playlistAdapter);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                    playerHandler.setCurrentSongListPosition(position);
                    mainList.setItemChecked(position, true);
                    playerHandler.playSong(position);
            }});
        loginLauncherLinkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHandler.destroyPlayers();
                Intent settings = new Intent(v.getContext(), LoginActivity.class);
                playerHandler.stopPlayerService();
                startActivityForResult(settings, 0);
            }
        });
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
        final Button loginButton = (Button) findViewById(R.id.loginLauncherLinkerButtonPL);
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIOPL);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.RandomPL);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.RewindPL);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.PrevPL);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.NextPL);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarPL);

        playerHandler =
                 initPlayerHandler(this.getApplicationContext(), "PlaylistActivity",
                         loginButton, fabIO, prev, next, rewind,
                        random, seekbar, mainList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit_list) {
            playerHandler.onDestroy();
            playerHandler.stopPlayerService();
            Intent editList = new Intent(PlaylistActivity.this, EditPlaylistActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("Playlist", playlist);
            b.putBoolean("previouslyExisting", true);
            editList.putExtras(b);
            startActivityForResult(editList, 0);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return true;
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
