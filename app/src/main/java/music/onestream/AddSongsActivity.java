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
        private static Playlist oldPlaylist;
        public static Playlist combinedList; //Contains song info
        public static Playlist oldCombinedList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.addsongs_activity);

            playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
            combinedList = (Playlist) getIntent().getSerializableExtra("combinedList");
            if (combinedList == null)
            {
                this.combinedList =  new Playlist
                        ("", "", MainActivity.getCombinedList().getSongInfo());
            }

            TextView playListName = (TextView) findViewById(R.id.listName);
            if (playlist != null && playlist.getName() != null) {
                playListName.setText(playlist.getName());
            }

            this.oldCombinedList = new Playlist();
            this.oldPlaylist = new Playlist();

            for (int i = 0; i < playlist.size(); i++)
            {
                oldPlaylist.addSong(playlist.getSongInfo().get(i));
            }
            for (int i = 0; i < combinedList.size(); i++)
            {
                oldCombinedList.addSong(combinedList.getSongInfo().get(i));
            }

            final ListView songsList = (ListView) findViewById(R.id.songList);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, combinedList.getAdapterList());
            songsList.setAdapter(adapter);

            songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (int i = 0; i < playlist.getSongInfo().size(); i++)
                    {
                        if ((combinedList.getSongInfo().get(position).getUri()
                                .equals(playlist.getSongInfo().get(i).getName()))) //Compare the file locations
                        {
                            return;
                        }
                    }
                        playlist.addSong(combinedList.getSongInfo().get(position));
                        combinedList.removeSong(position);
                        adapter.notifyDataSetChanged();
                        songsList.invalidateViews();

                }
            });

            Button back = (Button) findViewById(R.id.discardNewPlaylistChanges);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent back = new Intent(v.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("Playlist", oldPlaylist);
                    b.putSerializable("combinedList", oldCombinedList);
                    back.putExtras(b);
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
                    b.putSerializable("combinedList", combinedList);
                    addSongs.putExtras(b);
                    startActivityForResult(addSongs, 0);
                }
            });

        }
}