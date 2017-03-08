package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import music.onestream.playlist.Playlist;
import music.onestream.R;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;

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
                        ("", "", OneStreamActivity.getPlaylistHandler().getCombinedList().getSongInfo());
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
            final SongAdapter adapter = new SongAdapter
                    (this, android.R.layout.simple_list_item_1, combinedList.getSongInfo());
            songsList.setAdapter(adapter);

            songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if ((playlist.getSongInfo().contains(combinedList.getSongInfo().get(position))))
                        {
                            combinedList.removeSong(position);
                            return;
                        }
                        Song newSong = adapter.getItem(position);
                        playlist.addSong(newSong);
                        combinedList.removeSongItem(newSong);
                        adapter.getFilteredSongs().remove(newSong);
                        adapter.notifyDataSetChanged();
                        songsList.invalidateViews();

                }
            });

            final EditText textFilter = (EditText) findViewById(R.id.songFilterAS);
            textFilter.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    adapter.getFilter().filter(cs);
                    adapter.notifyDataSetChanged();
                    songsList.invalidateViews();
                }
                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            });

            textFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textFilter.setCursorVisible(true);
                    textFilter.requestFocus();
                }
            });

            textFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId== EditorInfo.IME_ACTION_DONE){
                        //Clear focus here from edittext
                        textFilter.clearFocus();
                        textFilter.setCursorVisible(false);
                    }
                    return false;
                }
            });

            Button back = (Button) findViewById(R.id.discardNewPlaylistChanges);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent back = new Intent(v.getContext(), EditPlaylistActivity.class);
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
                    Intent addSongs = new Intent(v.getContext(), EditPlaylistActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("Playlist", playlist);
                    b.putSerializable("combinedList", combinedList);
                    addSongs.putExtras(b);
                    startActivityForResult(addSongs, 0);
                }
            });

        }
}