package music.onestream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.spotify.sdk.android.player.Spotify;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlaylistActivity extends AppCompatActivity {
    private static PlayerActionsHandler playerHandler;
    private Playlist playlist;
    private ArrayAdapter<String> adapter;
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

    public void checkForInvalidSongs() {
        String services = "";
        Boolean spotifyAvailable = true;
        int i = 0;
        while (i < playlist.getAdapterList().size()){
            Song s = playlist.getSongInfo().get(i);
            if (s.type.equals("Local")) {
                File f = new File(s.getUri());
                if (!f.exists() || f.isDirectory()) {
                    playlist.getAdapterList().remove(i);
                }
            }
            else if (s.type.equals("Spotify") && playerHandler.isSpotifyLoggedOut() && spotifyAvailable)
            {
                loginLauncherLinkerButton.setVisibility(View.VISIBLE);
                mainList.setVisibility(View.INVISIBLE);
                services += " Spotify ";
                spotifyAvailable = false;
            }
            //TODO: Add google case for not logged in here
            else {
                i++;
            }
        }
        String buttonText = "Login to Services to access Playlist: " + services;
        loginLauncherLinkerButton.setText(buttonText);

    }

    private void initSongList() {
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");

        //Handle songs not accessible by device
        checkForInvalidSongs();

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
        loginLauncherLinkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHandler.destroyPlayers();
                Intent settings = new Intent(v.getContext(), LoginActivity.class);
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
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIOPL);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.RandomPL);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.RewindPL);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.PrevPL);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.NextPL);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarPL);
        playerHandler = new PlayerActionsHandler(this.getApplicationContext(),fabIO, prev, next, rewind, random, mainList, seekbar, "PlaylistActivity");

        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        playerHandler.initSpotifyPlayer(accessToken);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit_list) {
            playerHandler.onDestroy();
            Intent editList = new Intent(PlaylistActivity.this, EditPlaylistActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("Playlist", playlist);
            b.putBoolean("previouslyExisting", true);
            editList.putExtras(b);
            startActivityForResult(editList, 0);

        }
        return super.onOptionsItemSelected(item);
    }

    public static PlayerActionsHandler getPlayerHandler() {
        return playerHandler;
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
        playerHandler.stopPlayerService();
    }
    @Override
    protected void onPause() {
        super.onPause();
        playerHandler.onDestroy();
        playerHandler.stopPlayerService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerHandler.onResume();
        initPlayerHandler();
    }

}
