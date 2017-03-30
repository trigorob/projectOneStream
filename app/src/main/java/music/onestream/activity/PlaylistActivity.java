package music.onestream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

import music.onestream.R;
import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;
import music.onestream.util.Constants;
import music.onestream.util.PlayerActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class PlaylistActivity extends OSAuthenticationActivity {
    private PlayerActionsHandler playerHandler;
    private Playlist playlist;
    private ArrayList<Song> playlistAdapter;
    private SongAdapter adapter;
    private ListView mainList;
    private ImageButton loginLauncherLinkerButton;
    private String loggedOutServices;
    final Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        mainList = (ListView) findViewById(R.id.ListViewPL);
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        loginLauncherLinkerButton = (ImageButton) findViewById(R.id.loginLauncherLinkerButtonPL);
        loggedOutServices = "";

        initPlayerHandler();
        initSongList();

        setTitle(playlist.getName());
    }

    public void checkForInvalidSongs() {
        boolean spotifyAvailable = true;
        boolean soundCloudAvailable = true;

        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        playlistAdapter = new ArrayList<Song>();

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
                loggedOutServices+=Constants.spotify;
                if (!soundCloudAvailable && !spotifyAvailable)
                {
                    loginLauncherLinkerButton.setImageResource(R.drawable.soundify);
                    return;
                }
            }
            else if (s.getType().equals(Constants.soundCloud) && playerHandler.isSoundCloudLoggedOut())
            {
                loginLauncherLinkerButton.setVisibility(View.VISIBLE);
                loginLauncherLinkerButton.setImageResource(R.drawable.soundcloud);
                mainList.setVisibility(View.INVISIBLE);
                loggedOutServices+=Constants.soundCloud;
                soundCloudAvailable = false;
                if (!soundCloudAvailable && !spotifyAvailable)
                {
                    loginLauncherLinkerButton.setImageResource(R.drawable.soundify);
                    return;
                }
            }
            else {
                playlistAdapter.add(s);
            }
        }

        if (loggedOutServices.length() == 0)
        {
            loginLauncherLinkerButton.setVisibility(View.INVISIBLE);
            mainList.setVisibility(View.VISIBLE);

            adapter = new SongAdapter(this, R.layout.songlayout, playlistAdapter);
            mainList.setAdapter(adapter);
            mainList.invalidateViews();
        }
    }

    private void initSongList() {

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                SongAdapter sAdapter = (SongAdapter) mainList.getAdapter();
                ArrayList<Song> songs = sAdapter.getSongs();
                sAdapter.notifyDataSetChanged();
                OneStreamActivity.getPlaylistHandler().setCurrentSongs(songs);
                playerHandler.setCurrentListSize(songs.size());
                playerHandler.setCurrentSongListPosition(position);
                playerHandler.playSong(songs.indexOf(sAdapter.getItem(position)));
            }});
        loginLauncherLinkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedOutServices.contains(Constants.soundCloud)) {
                    handleLogin(Constants.OneStream_SoundCloud_Pos);
                }
                else if (loggedOutServices.contains(Constants.spotify)) {
                    handleLogin(Constants.OneStream_Spotify_Pos);
                }
            }
        });

        PlaylistActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerHandler.updateSeekBar();
                mHandler.postDelayed(this, 1000);
                if (mainList.getVisibility() == View.INVISIBLE)
                {
                    if (!playerHandler.isSoundCloudLoggedOut() && loggedOutServices.contains(Constants.soundCloud))
                    {
                        loggedOutServices = "";
                        checkForInvalidSongs();
                    }
                    else if (!playerHandler.isSpotifyLoggedOut() && loggedOutServices.contains(Constants.spotify))
                    {
                        loggedOutServices = "";
                        checkForInvalidSongs();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && requestCode == Constants.REQUEST_CODE) {
            onLoginActivityResult(requestCode, resultCode, intent);
        }
        else if (requestCode == Constants.REQUEST_CODE) {
            intent = new Intent(getContext(), PlaylistActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("Playlist", playlist);
            if (getCallingActivity() != null) {
                b.putString("Parent", getCallingActivity().toString());
            }
            intent.putExtras(b);
            startActivityForResult(intent, 0);
        }
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
        checkForInvalidSongs();
    }
}
