package music.onestream.util;

/**
 * Created by ruspe_000 on 2017-02-03.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import music.onestream.activity.OneStreamActivity;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class OneStreamActivityAdapter extends FragmentPagerAdapter {

    public OneStreamActivityAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return OneStreamActivity.PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 7 total pages.
        return 7;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Constants.OneStream_Library;
            case 1:
                return Constants.OneStream_Local;
            case 2:
                return Constants.OneStream_Spotify;
            case 3:
                return Constants.OneStream_Playlists;
            case 4:
                return Constants.OneStream_GoogleMusic;
            case 5:
                return Constants.OneStream_Artists;
            case 6:
                return Constants.OneStream_Albums;
        }
        return null;
    }
}