package music.onestream.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

import music.onestream.R;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;
import music.onestream.playlist.Playlist;
import music.onestream.util.Constants;
import music.onestream.util.CredentialsHandler;
import music.onestream.util.PlayerActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlaylistActivity extends OSActivity {
    private PlayerActionsHandler playerHandler;
    private Playlist playlist;
    private ListView mainList;
    private ImageButton loginLauncherLinkerButton;
    final Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        mainList = (ListView) findViewById(R.id.ListViewPL);
        loginLauncherLinkerButton = (ImageButton) findViewById(R.id.loginLauncherLinkerButtonPL);

        initPlayerHandler();
        initSongList();

        setTitle(playlist.getName());
    }

    public void checkForInvalidSongs(ArrayList<Song> playlistAdapter) {
        boolean spotifyAvailable = true;
        boolean soundCloudAvailable = true;
        boolean soundCloudLoggedOut = (CredentialsHandler.getToken(this, Constants.soundCloud) == null);
        String dir = (Environment.getExternalStorageDirectory().toString());
        for (int i = 0; i < playlist.getSongInfo().size(); i++){
            Song s = playlist.getSongInfo().get(i);
            if (s.getType().equals(Constants.local)) {
                /*     Looks a bit silly but we always have 12chars extra in our URI.
                *Android Emulators* are a bit buggy this way. Need to remove those to get correct path
                Since we don't use it anywhere else, it's not worth keeping elsewhere
                We use the album art uri for the phones. DONT TOUCH THESE
                */
                File f = new File(dir + s.getUri().substring(12));
                File f2 = new File(s.getAlbumArt());
                if (f.exists() || f2.exists()) {
                    playlistAdapter.add(s);
                }
            }
            else if (s.getType().equals(Constants.spotify) && playerHandler.isSpotifyLoggedOut() && spotifyAvailable)
            {
                loginLauncherLinkerButton.setVisibility(View.VISIBLE);
                loginLauncherLinkerButton.setImageResource(R.drawable.spotify);
                mainList.setVisibility(View.INVISIBLE);
                spotifyAvailable = false;
            }
            else if (s.getType().equals(Constants.soundCloud) && soundCloudLoggedOut)
            {
                loginLauncherLinkerButton.setVisibility(View.VISIBLE);
                loginLauncherLinkerButton.setImageResource(R.drawable.googlemusic);
                mainList.setVisibility(View.INVISIBLE);
                spotifyAvailable = false;
            }
            else if (!soundCloudAvailable && !spotifyAvailable)
            {
                //TODO: set hybrid icon here
                i+= playlist.getSongInfo().size();
            }
            //Todo: Add button with google if not logged in && google songs in playlist
            else {
                playlistAdapter.add(s);
            }
        }

        PlaylistActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerHandler.updateSeekBar();
                mHandler.postDelayed(this, 1000);
            }
        });
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
                ArrayList<Song> songs = ((SongAdapter) mainList.getAdapter()).getSongs();
                OneStreamActivity.getPlaylistHandler().setCurrentSongs(songs);
                playerHandler.setCurrentListSize(songs.size());

                playerHandler.setCurrentSongListPosition(position);
                mainList.setItemChecked(position, true);
                playerHandler.playSong(songs.indexOf(mainList.getAdapter().getItem(position)));
            }});
        loginLauncherLinkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(v.getContext(), LoginActivity.class);
                startActivityForResult(settings, 0);
            }
        });
    }

    private void initPlayerHandler() {
        final ImageButton loginButton = (ImageButton) findViewById(R.id.loginLauncherLinkerButtonPL);
        final ImageButton fabIO = (ImageButton) findViewById(R.id.fabIOPL);
        final ImageButton random = (ImageButton) findViewById(R.id.RandomPL);
        final ImageButton rewind = (ImageButton) findViewById(R.id.RewindPL);
        final ImageButton prev = (ImageButton) findViewById(R.id.PrevPL);
        final ImageButton next = (ImageButton) findViewById(R.id.NextPL);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarPL);

        playerHandler =
                 initPlayerHandler(this.getApplicationContext(), Constants.playlistActivity,
                         loginButton, fabIO, prev, next, rewind,
                        random, seekbar, mainList);
        playerHandler.setButtonColors(-1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit_list) {
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
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayerHandler();
    }

}
