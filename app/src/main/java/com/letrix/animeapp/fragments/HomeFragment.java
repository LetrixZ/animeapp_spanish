package com.letrix.animeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.SlidePagerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private SmartTabLayout smartTabLayout;
    private AppCompatImageView searchButton;
    private View view;
    private List<Fragment> list;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        list.add(new RecentAddedFragment());

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchButton = view.findViewById(R.id.searchButton);
        //list.add(new OngoingFragment());
        //list.add(new FinishedAnimeFragment());
        Log.d("info", "ONVIEWCREATED!");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_navigation_host, new SearchFragment());
                transaction.addToBackStack("TAG");
                transaction.commit();
            }
        });

        pager = view.findViewById(R.id.pager);
        smartTabLayout = view.findViewById(R.id.viewpagertab);
        pagerAdapter = new SlidePagerAdapter(getChildFragmentManager(), list);
        pager.setAdapter(pagerAdapter);
        smartTabLayout.setViewPager(pager);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home_fragment, container, false);
        ;

        Log.d("info", "ONCREATEVIEW!");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("info", "ONRESUME!");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("info", "DESTROYED VIEW!");
    }
}
