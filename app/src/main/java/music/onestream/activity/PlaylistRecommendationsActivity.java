package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import music.onestream.util.RecommendationsAdapter;
import music.onestream.util.RestServiceActionsHandler;

/**
 * Created by ruspe_000 on 2017-03-10.
 */

public class PlaylistRecommendationsActivity extends OSActivity implements AsyncResponse {

    private ListView mainList;
    private SongAdapter adapter;
    private PlaylistAdapter playlistAdapter;
    private PlaylistAdapter topSongsAdapter;
    private PlaylistAdapter topArtistsAdapter;
    private PlaylistAdapter topAlbumsAdapter;
    private RecommendationsAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Recommendations");
        setContentView(R.layout.activity_recommendation);

        mainList = (ListView) findViewById(R.id.ListViewPR);

        initViewPager();
        initSongList();
    }

    public void initViewPager() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPR);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new RecommendationsAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsPR);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                TextView description = (TextView) findViewById(R.id.tabDescPR);
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        description.setText("Click a song to recommend a playlist");
                        mainList.setAdapter(adapter);
                        break;
                    case 1:
                        description.setText("Click to open recommended playlist");
                        mainList.setAdapter(playlistAdapter);
                        break;
                    case 2:
                        description.setText("Click to open OneStream's top songs");
                        mainList.setAdapter(topSongsAdapter);
                        break;
                    case 3:
                        description.setText("Click to open OneStream users top 50 artists playlists");
                        mainList.setAdapter(topArtistsAdapter);
                        break;
                    case 4:
                        description.setText("Click to open OneStream users top 50 albums playlists");
                        mainList.setAdapter(topAlbumsAdapter);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

    private void initSongList() {
        ArrayList<Playlist> recommendedPlaylists = null;
        ArrayList<Song> songs = OneStreamActivity.getPlaylistHandler().getList("Library").getSongInfo();

        adapter = new SongAdapter(this, R.layout.songlayout, songs);
        playlistAdapter = new PlaylistAdapter(this, R.layout.songlayout, new ArrayList<Playlist>());
        if (topAlbumsAdapter == null || topSongsAdapter == null || topArtistsAdapter == null) {
            topArtistsAdapter = new PlaylistAdapter(this, R.layout.songlayout, new ArrayList<Playlist>());
            topSongsAdapter = new PlaylistAdapter(this, R.layout.songlayout, new ArrayList<Playlist>());
            topAlbumsAdapter = new PlaylistAdapter(this, R.layout.songlayout, new ArrayList<Playlist>());

            getTopRecommendations("Songs");
            getTopRecommendations("Artists");
            getTopRecommendations("Albums");
        }
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
                    Playlist p =  ((PlaylistAdapter) mainList.getAdapter()).getItem(position);
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

    public void getTopRecommendations(String type) {
            RestServiceActionsHandler restActionHandler = new RestServiceActionsHandler();
            Object[] params = new Object[2];
            params[0] = "GetTop" + type;
            restActionHandler.SAR = this;
            restActionHandler.execute(params);
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
        String type = (String) ((Object[]) result[1])[0];
        ArrayList<Playlist> resultList = (ArrayList<Playlist>) ((Object[]) result[1])[1];
        if (resultList != null && resultList.size() > 0)
        {
            if (type.equals("GetRecommendations")) {
                playlistAdapter = new PlaylistAdapter(this, R.layout.songlayout, resultList);
                playlistAdapter.setNotifyOnChange(true);
                mainList.invalidateViews();
                mViewPager.setCurrentItem(1);
            }
            else if (type.equals("GetTopSongs")) {
                topSongsAdapter = new PlaylistAdapter(this, R.layout.songlayout, resultList);
                topSongsAdapter.setNotifyOnChange(true);
                mainList.invalidateViews();
            }
            else if (type.equals("GetTopArtists")) {
                topArtistsAdapter = new PlaylistAdapter(this, R.layout.songlayout, resultList);
                topArtistsAdapter.setNotifyOnChange(true);
                mainList.invalidateViews();
            }
            else if (type.equals("GetTopAlbums")) {
                topAlbumsAdapter = new PlaylistAdapter(this, R.layout.songlayout, resultList);
                topAlbumsAdapter.setNotifyOnChange(true);
                mainList.invalidateViews();
            }
        }
        else if (type.equals("GetRecommendations"))
        {
            Toast.makeText(this, "Nothing found, try another song.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}

