package es.npatarino.android.gotchallenge.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.npatarino.android.gotchallenge.ui.activities.HomeActivity;
import es.npatarino.android.gotchallenge.ui.fragments.GoTCharactersListFragment;
import es.npatarino.android.gotchallenge.ui.fragments.GoTHousesListFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new GoTCharactersListFragment();
        } else {
            return new GoTHousesListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Characters";
            case 1:
                return "Houses";
        }
        return null;
    }
}
