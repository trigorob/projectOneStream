package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import music.onestream.R;
import music.onestream.playlist.Playlist;
import music.onestream.util.AsyncResponse;
import music.onestream.util.DatabaseActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class EditPlaylistActivity extends Activity implements AsyncResponse {

    private static Playlist playlist;
    private static Playlist oldPlaylist;
    private static String[] songNames;
    private static DatabaseActionsHandler dba;
    private static Playlist combinedList;
    private boolean newList = false;
    private static boolean previouslyExisting = false;
    private static String domain;

    public String getDomain() {
        final SharedPreferences domainSettings = getSharedPreferences("ONESTREAM_DOMAIN", 0);
        return domainSettings.getString("domain", "Admin");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_playlist_activity);
        dba = new DatabaseActionsHandler();
        dba.SAR = this;
        domain = getDomain();

        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        Playlist tempCombinedList = (Playlist) getIntent().getSerializableExtra("combinedList");


        combinedList = new Playlist();
        if (tempCombinedList != null)
        {
            combinedList.addSongs(tempCombinedList.getSongInfo());
        }
        else
        {
            combinedList.addSongs(OneStreamActivity.getPlaylistHandler().getCombinedList().getSongInfo());
        }

        if (oldPlaylist == null) {
            previouslyExisting = getIntent().getBooleanExtra("previouslyExisting", false);
        }

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
        if (songNames == null || combinedList == null)
        {
            songNames = new String[0];
        }
        if (playlist == null) {

            playlist = new Playlist();
            songNames = new String[0];
        }
        else {
            songNames = new String[playlist.getSongInfo().size()];
            for (int i = 0; i < playlist.getSongInfo().size(); i++)
            {
                songNames[i] = playlist.getSongInfo().get(i).getName();
                if (combinedList.getSongInfo().contains(playlist.getSongInfo().get((i))))
                {
                    combinedList.removeSong(combinedList.getSongInfo().indexOf(playlist.getSongInfo().get((i))));
                }
            }
        }

        final ListView playSongs = (ListView) findViewById(R.id.playListSongs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songNames);
        playSongs.setAdapter(adapter);

        playSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                combinedList.addSong(playlist.getSongInfo().get(position));
                playlist.removeSong(position);
                songNames = new String[playlist.getSongInfo().size()];
                for (int i = 0; i < playlist.getSongInfo().size(); i++)
                {
                    songNames[i] = playlist.getSongInfo().get(i).getName();
                }
                playSongs.invalidateViews();
                playSongs.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, songNames));

            }
        });

        final EditText playlistTitle = (EditText) findViewById(R.id.playListName);
        if (playlist.getName() == null || playlist.getName().equals("")) {
            playlistTitle.setText("Playlist Title");
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
            }
        });

        Button addSongsButton = (Button) findViewById(R.id.addSongsToPlaylist);
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

        Button discardChangesButton = (Button) findViewById(R.id.discardNewPlaylist);
        discardChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDelete();
            }
        });

        Button saveChangesButton = (Button) findViewById(R.id.saveNewPlaylist);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave();
            }
        });
    }

    private void setPlaylistsChangedFlag(boolean value) {
        SharedPreferences settings = getSharedPreferences("PLAYLIST-CHANGE", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("PlaylistsChanged", value);
        editor.commit();
    }

    public boolean isListInDatabase() {
        return (playlist != null && !newList && !playlist.getOwner().equals("") && previouslyExisting);
    }

    private void handleDelete()
    {
        Object[] params = new Object[2];
        if (!newList) {
            playlist.setOwner(domain);
            playlist.setName(oldPlaylist.getName());
            params[0] = "DeletePlaylist";
            params[1] = playlist;
            dba.execute(params);

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
            params[0] = "CreatePlaylist";
        }
        else {
            params[0] = "UpdatePlaylist";
            params[2] = playlistTitle.getText().toString();
            params[3] = oldPlaylist.getName();
        }
        params[1] = playlist;

        dba.execute(params);

        playlist.setName(playlistTitle.getText().toString());
        if (playlists != null)
        {
            if (oldPlaylist != null) {
                playlists.remove(oldPlaylist);
            }
            if (!playlists.contains(playlist)) {
                playlists.add(playlist);
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

    @Override
    public void processFinish(Object[] result) {
        return;
    }
}
