package me.mamun.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAMUN AHMED on 02-Nov-15.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public static List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
        notifyDataSetChanged();
    }

    // This is called when notifyDataSetChanged() is called
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

    // Delete a page at a `position`
    public void deletePage(int position)
    {
        // Remove the corresponding item in the data set
        mFragmentList.remove(position);
        // Notify the adapter that the data set is changed
        notifyDataSetChanged();
    }

    public void repalceFragment(int targetPosition, Fragment newFragment) {
        mFragmentList.set(targetPosition, newFragment);
        notifyDataSetChanged();
    }

    public void listClear() {
        mFragmentList.clear();
    }
}
