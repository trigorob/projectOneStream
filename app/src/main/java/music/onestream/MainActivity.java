package music.onestream;

import android.app.ListActivity;
import android.app.Presentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
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

import android.widget.Adapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.URI;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    public static final int ACTIVITY_CHOOSE_FILE = 5;
    public static final int ACTIVITY_CHANGE_DIR = 6;
    private static String directory;

    // variable declaration
    private ListView mainList;
    private MusicGetter mG;
    private MediaPlayer mp;
    private static String[] listContent;
    private static Integer[] resID;
    private static Uri[] resURI;
    int currentSongListPosition = -1;
    int currentSongPosition = -1;
    boolean randomNext = false;

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

    public void resumeSong(int songIndex) {
        if (currentSongPosition!= -1 && songIndex == currentSongListPosition) {
            mp.seekTo(currentSongPosition);
            mp.start(); // starting mediaplayer

            SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
            seekbar.setMax(mp.getDuration());
            final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
            fabIO.setImageResource(R.drawable.stop);
        }
        else
            playSong(songIndex);
    }

    // Play song
    public void playSong(int songIndex) {

        if (mG == null)
        {
            setMusicDir(mG,directory);
        }
        mp.reset();
        if (resURI != null) {
            mp = MediaPlayer.create(getApplicationContext(), resURI[songIndex]);// creates new mediaplayer with song.
        } else {
            mp = MediaPlayer.create(getApplicationContext(), resID[songIndex]);// creates new mediaplayer with song.
        }
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (!randomNext) {
                    final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
                    next.performClick();
                }
                else {
                    playRandomSong();
                }
            }});

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setMax(mp.getDuration());
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        fabIO.setImageResource(R.drawable.stop);
    }

    public MusicGetter setMusicDir(MusicGetter mG, String dir)
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

        return mG;
    }

    public void stopSong() {
        mp.pause();
        currentSongPosition = mp.getCurrentPosition();
        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
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
                this.directory = bundle.getString("dir");
                this.setMusicDir(mG, directory);
                mViewPager.setCurrentItem(0);
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
        final String directory = settings.getString("dir", "Default");
        mG = setMusicDir(mG, directory);

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

        mViewPager.setCurrentItem(0);

        mp = new MediaPlayer();

        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        final Handler mHandler = new Handler();
        //Make sure you update Seekbar on UI thread

        mainList = (ListView) findViewById(R.id.ListView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContent);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (currentSongListPosition != -1) {
                    getViewByPosition(mainList, currentSongListPosition).setBackgroundColor(getResources().getColor(R.color.default_color));
                }
                currentSongListPosition = position;
                mainList.setItemChecked(currentSongListPosition,true);
                getViewByPosition(mainList,currentSongListPosition).setBackgroundColor(Color.parseColor("#E0ECF8"));
                playSong(position);
            }});

        final FloatingActionButton fabIO = (FloatingActionButton) findViewById(R.id.fabIO);
        fabIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //Play or resume song
                    if (currentSongListPosition != -1) {
                        if (!mp.isPlaying()) {
                            resumeSong(currentSongListPosition);
                        } else //Stop song.
                        {
                            stopSong();
                        }
                    }
                };
        }});

        final FloatingActionButton random = (FloatingActionButton) findViewById(R.id.Random);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (randomNext == true)
                    {
                        randomNext = false;
                        random.setImageResource(R.drawable.shuffle);
                    }
                    else
                    {
                        randomNext = true;
                        random.setImageResource(R.drawable.notrandom);
                    }
                };
            }});

        final FloatingActionButton rewind = (FloatingActionButton) findViewById(R.id.Rewind);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (currentSongListPosition != -1) {
                        playSong(currentSongListPosition);
                    }
                };
            }});

        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.Prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                        int next;
                        if (currentSongListPosition == -1)
                        {
                            return;
                        }

                        if (currentSongListPosition > 0) {
                            next = currentSongListPosition - 1;
                        }
                        else {
                            next = mG.files.length-1;
                        }
                        View nextRow = getViewByPosition(mainList,next);

                        if (currentSongListPosition != -1) {
                            View oldRow = getViewByPosition(mainList, currentSongListPosition);
                            oldRow.setBackgroundColor(getResources().getColor(R.color.default_color));
                        }
                        mainList.requestFocusFromTouch();
                        mainList.performItemClick(mainList, next, mainList.getItemIdAtPosition(next));
                        nextRow.setBackgroundColor(Color.parseColor("#E0ECF8"));
                        currentSongListPosition = next;
                };
            }});


        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    int next;
                    if (currentSongListPosition == -1)
                    {
                        return;
                    }
                    if (currentSongListPosition < mG.files.length-1) {
                        next = currentSongListPosition + 1;
                    }
                    else {
                        next = 0;
                    }
                    View nextRow = getViewByPosition(mainList,next);

                    if (currentSongListPosition != -1) {
                        View oldRow = getViewByPosition(mainList, currentSongListPosition);
                        oldRow.setBackgroundColor(getResources().getColor(R.color.default_color));
                    }
                    mainList.requestFocusFromTouch();
                    mainList.performItemClick(mainList, next, mainList.getItemIdAtPosition(next));
                    nextRow.setBackgroundColor(Color.parseColor("#E0ECF8"));
                    currentSongListPosition = next;
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
                        mainList.setVisibility(View.VISIBLE);
                        setMusicDir(mG, directory);
                        break;
                    case 1:
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        mainList.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp != null && fromUser){
                    currentSongPosition = progress;
                    mp.seekTo(currentSongPosition);
                }
            }
        });


        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mp != null && mp.isPlaying()) {
                        int mCurrentPosition = mp.getCurrentPosition();
                        seekbar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
                catch (IllegalStateException IE)
                {

                }
            }
        });
    }


    public void playRandomSong() {
        try {
            int choice = mG.selectRandomSongAsInt();
            View choiceRow = getViewByPosition(mainList,choice);

            if (currentSongListPosition != -1) {
                View oldRow = getViewByPosition(mainList, currentSongListPosition);
                oldRow.setBackgroundColor(getResources().getColor(R.color.default_color));
            }
            mainList.requestFocusFromTouch();
            mainList.performItemClick(mainList, choice, mainList.getItemIdAtPosition(choice));
            choiceRow.setBackgroundColor(Color.parseColor("#E0ECF8"));
            currentSongListPosition = choice;
        }
        catch (IOException e) {}
    }


    //Use this to grab a row from music list
    public View getViewByPosition(ListView listView, int pos) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mp != null && fromUser){
            mp.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //AutogenStub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //AutogenStub
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
}
