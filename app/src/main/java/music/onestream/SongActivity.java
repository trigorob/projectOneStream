package music.onestream;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

/**
 * Created by jovan on 2/25/17.
 */

public class SongActivity extends Activity {

    private static PlayerActionsHandler playerHandler;
    private static PlaylistHandler playlistHandler;

    // variable declaration
    private static ListView mainList;
    private static ArrayAdapter<Song> adapter;
    private static ArrayAdapter<Song> spotifyAdapter;
    private static ArrayAdapter<Playlist> playlistAdapter;
    private static ArrayAdapter<Song> googleAdapter;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerHandler.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerHandler.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerHandler.onResume();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);

    }


    private void initPlayerHandler() {
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIOSV);
        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.RandomSV);
        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.RewindSV);
        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.PrevSV);
        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.NextSV);
        //final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBarSV);

        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        playerHandler.initSpotifyPlayer(accessToken);
    }
}
