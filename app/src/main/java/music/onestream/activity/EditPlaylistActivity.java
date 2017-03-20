package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import music.onestream.R;
import music.onestream.playlist.Playlist;
import music.onestream.song.SongAdapter;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;
import music.onestream.util.RestServiceActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class EditPlaylistActivity extends AppCompatActivity implements AsyncResponse {

    private Playlist playlist;
    private Playlist oldPlaylist;
    private RestServiceActionsHandler restActionHandler;
    private Playlist combinedList;
    private boolean newList = false;
    private boolean previouslyExisting = false;
    private String domain;

    public String getDomain() {
        final SharedPreferences domainSettings = getSharedPreferences(Constants.oneStreamDomainLoc, 0);
        return domainSettings.getString(Constants.domain, Constants.defaultDomain);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_playlist_activity);
        restActionHandler = new RestServiceActionsHandler();
        restActionHandler.SAR = this;
        domain = getDomain();
        getSupportActionBar();

        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        Playlist tempCombinedList = (Playlist) getIntent().getSerializableExtra("combinedList");
        combinedList = new Playlist();

        if (playlist != null && playlist.getName() != null)
        {
            setTitle(playlist.getName());
        }
        else
        {
            setTitle(Constants.defaultPlaylistName);
        }
        if (tempCombinedList != null)
        {
            combinedList.addSongs(tempCombinedList.getSongInfo());
        }
        else
        {
            combinedList.addSongs(OneStreamActivity.getPlaylistHandler().getList("Library").getSongInfo());
        }

        previouslyExisting = getIntent().getBooleanExtra("previouslyExisting", false);

        if (isListInDatabase())
        {
            oldPlaylist = new Playlist();
            oldPlaylist.setSongInfo(playlist.getSongInfo());
            oldPlaylist.setName(playlist.getName());
            oldPlaylist.setOwner(playlist.getOwner());
        }
        else
        {
            newList = true;
        }
        if (playlist == null) {

            playlist = new Playlist();
        }
        else {
            for (int i = 0; i < playlist.getSongInfo().size(); i++)
            {
                if (combinedList.getSongInfo().contains(playlist.getSongInfo().get((i))))
                {
                    combinedList.removeSong(combinedList.getSongInfo().indexOf(playlist.getSongInfo().get((i))));
                }
            }
        }

        final ListView playSongs = (ListView) findViewById(R.id.playListSongs);
        final SongAdapter adapter = new SongAdapter(this, android.R.layout.simple_list_item_1, playlist.getSongInfo());
        playSongs.setAdapter(adapter);

        playSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                combinedList.addSong(playlist.getSongInfo().get(position));
                playlist.removeSong(position);
                adapter.notifyDataSetChanged();
                playSongs.invalidateViews();
            }
        });

        final EditText playlistTitle = (EditText) findViewById(R.id.playListName);
        if (playlist.getName() == null || playlist.getName().equals("")) {
            playlistTitle.setText(Constants.defaultPlaylistName);
        }
        else
        {
            playlistTitle.setText(playlist.getName());
        }
        playlistTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                playlist.setName(s.toString());
                setTitle(playlist.getName());
            }
        });

        playlistTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistTitle.setCursorVisible(true);
                playlistTitle.requestFocus();
            }
        });

        playlistTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    playlistTitle.clearFocus();
                    playlistTitle.setCursorVisible(false);
                }
                return false;
            }
        });

        FloatingActionButton addSongsButton = (FloatingActionButton) findViewById(R.id.addSongsToPlaylist);
        addSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSongs = new Intent(v.getContext(), AddSongsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("Playlist", playlist);
                b.putSerializable("combinedList", combinedList);
                addSongs.putExtras(b);
                startActivityForResult(addSongs, 0);
            }
        });

        FloatingActionButton discardChangesButton = (FloatingActionButton) findViewById(R.id.discardNewPlaylist);
        discardChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDelete();
            }
        });

        FloatingActionButton saveChangesButton = (FloatingActionButton) findViewById(R.id.saveNewPlaylist);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave();
            }
        });
    }

    private void setPlaylistsChangedFlag(boolean value) {
        SharedPreferences settings = getSharedPreferences(Constants.playlistChangeLoc, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.playlistChanged, value);
        editor.commit();
    }

    public boolean isListInDatabase() {
        return (playlist != null && !newList && playlist.getOwner().equals(domain) && previouslyExisting);
    }

    private void handleDelete()
    {
        Object[] params = new Object[2];
        if (!newList) {
            playlist.setOwner(domain);
            playlist.setName(oldPlaylist.getName());
            params[0] = Constants.deletePlaylist;
            params[1] = playlist;
            restActionHandler.execute(params);

            ArrayList<Playlist> playlists = OneStreamActivity.getPlaylistHandler().getPlaylists();
            if (playlists != null)
            {
                playlists.remove(oldPlaylist);
            }
        }

        setPlaylistsChangedFlag(true);

        Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("Playlist", null);
        b.putSerializable("combinedList", null);
        settings.putExtras(b);
        startActivityForResult(settings, 0);
    }

    private void handleSave()
    {
        playlist.setOwner(domain);
        Object[] params = new Object[4];
        EditText playlistTitle = (EditText) findViewById(R.id.playListName);

        ArrayList<Playlist> playlists = OneStreamActivity.getPlaylistHandler().getPlaylists();

        if (!isListInDatabase()) {
            playlist.setName(playlistTitle.getText().toString());
            params[0] = Constants.createPlaylist;
        }
        else {
            params[0] = Constants.updatePlaylist;
            params[2] = playlistTitle.getText().toString();
            params[3] = oldPlaylist.getName();
        }
        params[1] = playlist;

        restActionHandler.execute(params);

        playlist.setName(playlistTitle.getText().toString());
        if (playlists != null)
        {
            if (playlists.contains(playlist)) {
                playlists.remove(playlist);
            }
            playlists.add(playlist);
        }

        setPlaylistsChangedFlag(true);

        Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("Playlist", null);
        b.putSerializable("combinedList", null);
        settings.putExtras(b);
        startActivityForResult(settings, 0);
    }

    @Override
    public void processFinish(Object[] result) {
        return;
    }
}
