package com.letrix.animeapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.letrix.animeapp.adapters.SlidePagerAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.fragments.CommonFragment;
import com.letrix.animeapp.fragments.ConfigFragment;
import com.letrix.animeapp.fragments.FavouritesFragment;
import com.letrix.animeapp.fragments.GenreSelectorFragment;
import com.letrix.animeapp.fragments.RecentFragment;
import com.letrix.animeapp.fragments.SearchFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private static ViewPager pager;
    private PagerAdapter pagerAdapter;
    private SmartTabLayout smartTabLayout;
    private AppCompatImageView searchButton, favouritesButton;
    private View view;
    private List<Fragment> list;
    private MainViewModel mainViewModel;
    private MotionLayout motionLayout;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static void selectPage(int page) {
        pager.setCurrentItem(page);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchButton = view.findViewById(R.id.searchButton);
        favouritesButton = view.findViewById(R.id.favorite);
        motionLayout = view.findViewById(R.id.homeMotionLayout);

        searchButton.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_navigation_host, new SearchFragment());
            transaction.addToBackStack("TAG");
            transaction.commit();
        });

        favouritesButton.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_navigation_host, new FavouritesFragment());
            transaction.addToBackStack("TAG");
            transaction.commit();
        });

        pager = view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(1);
        smartTabLayout = view.findViewById(R.id.viewpagertab);
        pagerAdapter = new SlidePagerAdapter(getChildFragmentManager(), list);
        pager.setAdapter(pagerAdapter);
        smartTabLayout.setViewPager(pager);
        pager.setCurrentItem(2, true);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getSupportFragmentManager().addOnBackStackChangedListener(
                    () -> motionLayout.transitionToEnd());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUpdater appUpdater = new AppUpdater(requireActivity())
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateJSON("https://pastebin.com/raw/SxL4ZTYh")
                .setTitleOnUpdateAvailable("Nueva versión disponible")
                .setButtonUpdate("Actualizar")
                .setButtonDismiss("Más tarde")
                .setButtonDoNotShowAgain("No mostrar de nuevo");
        appUpdater.start();

        list = new ArrayList<>();
        list.add(new ConfigFragment());
        list.add(new GenreSelectorFragment());
        list.add(new RecentFragment());
        for (int i = 1; i <= 6; i++) {
            list.add(CommonFragment.newInstance(i));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }

}

