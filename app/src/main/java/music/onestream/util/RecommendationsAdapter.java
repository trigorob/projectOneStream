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
public class RecommendationsAdapter extends FragmentPagerAdapter {

    public RecommendationsAdapter(FragmentManager fm)
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
        // Show 5 total pages.
        return 5;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Constants.Recommendations_Songs;
            case 1:
                return Constants.Recommendations_Playlists;
            case 2:
                return Constants.Recommendations_TopSongs;
            case 3:
                return Constants.Recommendations_TopArtists;
            case 4:
                return Constants.Recommendations_TopAlbums;

        }
        return null;
    }
}