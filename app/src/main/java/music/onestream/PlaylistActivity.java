package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class PlaylistActivity extends Activity {

    private static Playlist playlist;
    private static String[] songNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);
        if (getIntent().getSerializableExtra("Playlist") != null) {
            playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        }

        if (playlist == null || playlist.getSongInfo() == null) {
            playlist = new Playlist();
            songNames = new String[0];
        }
        else {
            songNames = new String[playlist.getSongInfo().size()];
            for (int i = 0; i < playlist.getSongInfo().size(); i++)
            {
                songNames[i] = playlist.getSongInfo().get(i)[0];
            }
        }

        ListView playSongs = (ListView) findViewById(R.id.playListSongs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songNames);
        playSongs.setAdapter(adapter);

        EditText playlistTitle = (EditText) findViewById(R.id.playListName);
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
                addSongs.putExtras(b);
                startActivityForResult(addSongs, 0);
            }
        });
    }
}
