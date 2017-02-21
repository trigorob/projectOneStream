package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class PlaylistActivity extends Activity implements AsyncResponse {

    private static Playlist playlist;
    private static String[] songNames;
    private static DatabaseActionsHandler dba;
    private static Playlist combinedList;
    private static boolean newList = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);
        dba = new DatabaseActionsHandler();
        dba.SAR = this;

        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        combinedList = (Playlist) getIntent().getSerializableExtra("combinedList");

        if (playlist == null || playlist.getSongInfo() == null) {
            playlist = new Playlist();
            newList = true;
            songNames = new String[0];
            combinedList = new Playlist("", "", MainActivity.getCombinedList().getSongInfo());
        }
        else {
            songNames = new String[playlist.getSongInfo().size()];
            for (int i = 0; i < playlist.getSongInfo().size(); i++)
            {
                songNames[i] = playlist.getSongInfo().get(i).getName();
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
                Intent settings = new Intent(v.getContext(), Settings.class);
                Bundle b = new Bundle();
                b.putSerializable("Playlist", null);
                b.putSerializable("combinedList", null);
                settings.putExtras(b);
                startActivityForResult(settings, 0);
            }
        });

        Button saveChangesButton = (Button) findViewById(R.id.saveNewPlaylist);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlist.setOwner("Admin"); //Todo: Need to store onestream user and get back
                playlist.setName(playlistTitle.getText().toString());
                Object[] params = new Object[3];

                if (newList) {
                    params[0] = "CreatePlaylist";
                }
                else {
                    params[0] = "UpdatePlaylist";
                }
                params[1] = playlist;
                dba.execute(params);

                //Clear out the playlists. Next time mainactivity is loaded, it will have new playlist available
                MainActivity.resetPlaylists();

                Intent settings = new Intent(v.getContext(), Settings.class);
                Bundle b = new Bundle();
                b.putSerializable("Playlist", null);
                b.putSerializable("combinedList", null);
                settings.putExtras(b);
                startActivityForResult(settings, 0);
            }
        });
    }

    @Override
    public void processFinish(Object[] result) {
        return;
    }
}
