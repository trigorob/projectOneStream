package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-13.
 */

public class AddSongsActivity extends Activity {

        private static Playlist playlist;
        private static String[] songNames;
        public static ArrayList<String[]> combinedList; //Contains song info

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.addsongs_activity);
            this.combinedList =  MainActivity.getCombinedList();
            playlist = (Playlist) getIntent().getSerializableExtra("Playlist");

            if (combinedList == null)
            {
                combinedList = new ArrayList<String[]>(); //Will almost never happen, but just incase
            }
            if (songNames == null && combinedList != null || (songNames.length != combinedList.size()))
            {
                songNames = new String[combinedList.size()];
                for (int i = 0; i < songNames.length; i++)
                {
                    songNames[i] = combinedList.get(i)[0];
                }
            }

            TextView playListName = (TextView) findViewById(R.id.listName);
            if (playlist != null && playlist.getName() != null) {
                playListName.setText(playlist.getName());
            }

            ListView playlistslist = (ListView) findViewById(R.id.songList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songNames);
            playlistslist.setAdapter(adapter);

            playlistslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (int i = 0; i < playlist.getSongInfo().size(); i++)
                    {
                        if ((combinedList.get(position)[1].equals(playlist.getSongInfo().get(i)[1]))) //Compare the file locations
                        {
                            return;
                        }
                    }
                        playlist.addSong(combinedList.get(position));

                }
            });

            Button back = (Button) findViewById(R.id.discardNewPlaylistChanges);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent back = new Intent(v.getContext(), PlaylistActivity.class);
                    startActivityForResult(back, 0);
                }
            });
            Button save = (Button) findViewById(R.id.saveNewPlaylistChanges);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addSongs = new Intent(v.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("Playlist", playlist);
                    addSongs.putExtras(b);
                    startActivityForResult(addSongs, 0);
                }
            });

        }
}