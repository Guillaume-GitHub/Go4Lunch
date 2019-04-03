package com.android.guillaume.go4launch.utils;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.controler.ListViewFragment;
import com.android.guillaume.go4launch.controler.MapFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments;
    private final int NB_PAGE = 2;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.registeredFragments = new SparseArray<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // show MapFragment
                return new MapFragment();
            case 1: // show ListViewFragment
                return new ListViewFragment();
            default:
                return null;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        this.registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragments(int position) {
        return this.registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return NB_PAGE;
    }
}
