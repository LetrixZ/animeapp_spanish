package com.letrix.animeapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.letrix.animeapp.fragments.CommonFragment;

import java.util.List;

public class SlidePagerAdapter extends FragmentStatePagerAdapter {

    public static final String[] TABTITLES = new String[]{"Recientes", "En emisión", "Finalizados", "Series", "Películas", "OVAs", "Especiales"};
    private List<Fragment> fragmentList;

    public SlidePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TABTITLES[position];
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
