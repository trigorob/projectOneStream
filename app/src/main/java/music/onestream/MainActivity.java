package music.onestream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URI;


public class MainActivity extends AppCompatActivity {

    public static final int ACTIVITY_CHOOSE_FILE = 5;
    public static final int ACTIVITY_CHANGE_DIR = 6;

    // variable declaration
    private ListView mainList;
    private MusicGetter mG;
    private MediaPlayer mp;
    private static String[] listContent;
    private static Integer[] resID;
    private static Uri[] resURI;
    int currentSongPosition = -1;

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

    public void playSong(int songIndex) {
        // Play song
        mp.reset();// stops any current playing song

        if (resURI != null) {
            mp = MediaPlayer.create(getApplicationContext(), resURI[songIndex]);// creates new mediaplayer with song.
        }
        else  {
            mp = MediaPlayer.create(getApplicationContext(), resID[songIndex]);// creates new mediaplayer with song.
        }
        mp.start(); // starting mediaplayer
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.change_dir);
        fabIO.setImageResource(R.drawable.stop);
    }

    public void setMusicDir(MusicGetter mG, String dir)
    {
        mG = new MusicGetter(dir);
        listContent = mG.files;
        if (!(mG.fileData == null))
        {
            resID = mG.fileData;
            resURI = null;
        }
        else
        {
            resURI = new Uri[mG.fileURI.length];
            for (int i = 0; i < mG.fileURI.length; i++) {
                resURI[i] = android.net.Uri.parse(mG.fileURI[i].toString());
            }
            resID = null;
        }

    }

    public void stopSong() {
        mp.reset();
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.change_dir);
        fabIO.setImageResource(R.drawable.play);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHANGE_DIR) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = getIntent().getExtras();
                String dir = bundle.getString("dir");
                this.setMusicDir(mG, dir);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings = new Intent(mViewPager.getContext(), Settings.class);
            startActivityForResult(settings, 0);

        }
        return super.onOptionsItemSelected(item);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences settings = getSharedPreferences("dirInfo", 0);
        String directory = settings.getString("dir", "Default");
        setMusicDir(mG, directory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(1);

        // Initializing variables
        mp = new MediaPlayer();
        mainList = (ListView) findViewById(R.id.ListView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listContent);
        mainList.setAdapter(adapter);
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                currentSongPosition = position;
                playSong(position);
            }});

        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.change_dir);
        fabIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //Play song. TODO: change to stopbutton after
                    if (currentSongPosition != -1) {
                        if (!mp.isPlaying()) {
                            playSong(currentSongPosition);
                        } else //Stop song. TODO: change to playbutton after
                        {
                            stopSong();
                        }
                    }
                };
        }});

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            //TODO: Optimize. Also need lists for spotify/soundcloud
            @Override
            public void onPageSelected(int position) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        mainList.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mViewPager.setCurrentItem(1);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Spotify";
                case 1:
                    return "Local";
                case 2:
                    return "Soundcloud";
            }
            return null;
        }
    }
}
