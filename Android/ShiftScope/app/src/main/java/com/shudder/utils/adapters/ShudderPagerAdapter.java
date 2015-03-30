package com.shudder.utils.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.shudder.views.fragments.LibraryFragment;
import com.shudder.views.fragments.PlayListFragment;

/**
 * Created by Carlos on 30/03/2015.
 */
public class ShudderPagerAdapter extends FragmentStatePagerAdapter {

    //private final String[] TITLES = { "Library", "PLaylist"};
    private final String[] TITLES = { "Library", "Playlist"};
    public ShudderPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LibraryFragment();
            case 1:
                return new PlayListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
