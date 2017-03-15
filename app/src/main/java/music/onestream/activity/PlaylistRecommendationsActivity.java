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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import music.onestream.R;
import music.onestream.playlist.Playlist;
import music.onestream.playlist.PlaylistAdapter;
import music.onestream.song.Song;
import music.onestream.song.SongAdapter;
import music.onestream.util.AsyncResponse;
import music.onestream.util.RestServiceActionsHandler;

/**
 * Created by ruspe_000 on 2017-03-10.
 */

public class PlaylistRecommendationsActivity extends Activity implements AsyncResponse {

    private ListView mainList;
    private SongAdapter adapter;
    private PlaylistAdapter playlistAdapter;
    private ArrayList<Playlist> recommendedPlaylists;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        mainList = (ListView) findViewById(R.id.ListViewPR);
        initSongList();

    }

    private void initSongList() {
        recommendedPlaylists = null;
        ArrayList<Song> songs = OneStreamActivity.getPlaylistHandler().getList("Library").getSongInfo();

        adapter = new SongAdapter(this, R.layout.songlayout, songs);
        adapter.setNotifyOnChange(true);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                mainList.setItemChecked(position, true);
                if (mainList.getAdapter().equals(adapter)) {
                    Song song = adapter.getItem(position);
                    getPlaylistRecommendations(song);
                }
                else {
                    Playlist p =  playlistAdapter.getItem(position);
                    Intent playlist = new Intent(view.getContext(), PlaylistActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("Playlist", p);
                    playlist.putExtras(b);
                    startActivityForResult(playlist, 0);
                }
            }});

        final EditText textFilter = (EditText) findViewById(R.id.songFilterPR);
        textFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (mainList.getAdapter().equals(adapter)) {
                    adapter.getFilter().filter(cs);
                }
                else
                {
                    playlistAdapter.getFilter().filter(cs);
                }
                mainList.invalidateViews();
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
    }

    public void getPlaylistRecommendations(Song song) {
        RestServiceActionsHandler restActionHandler = new RestServiceActionsHandler();
        Object[] params = new Object[2];
        params[0] = "GetRecommendations";
        params[1] = song;
        restActionHandler.SAR = this;
        restActionHandler.execute(params);
    }

    @Override
    public void processFinish(Object[] result) {
        ArrayList<Playlist> resultList = (ArrayList<Playlist>) result[1];
        if (resultList != null && resultList.size() > 0)
        {
            this.playlistAdapter = new PlaylistAdapter(this, R.layout.songlayout, resultList);
            playlistAdapter.setNotifyOnChange(true);
            mainList.setAdapter(playlistAdapter);
            mainList.invalidateViews();
        }
        else
        {
            Toast.makeText(this, "Nothing found, try another song.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainList.setAdapter(adapter);
    }

}
