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
                return "Library";
            case 1:
                return "Local";
            case 2:
                return "Spotify";
            case 3:
                return "Playlists";
            case 4:
                return "Google Music";
            case 5:
                return "Artists";
            case 6:
                return "Albums";
        }
        return null;
    }
}