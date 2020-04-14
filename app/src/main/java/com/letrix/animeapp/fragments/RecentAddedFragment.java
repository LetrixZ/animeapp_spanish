package com.letrix.animeapp.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.HomeFragment;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.SlidePagerAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.utils.AnimeSection;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class RecentAddedFragment extends Fragment implements AnimeSection.ClickListener {

    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private MainViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private ProgressBar progressBar;
    private ViewGroup rootView;
    private Parcelable mListState = null;
    private GridLayoutManager gridLayoutManager;

    public RecentAddedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
        initRecycler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_common, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        progressBar = rootView.findViewById(R.id.progressBar);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);

        //view = inflater.inflate(R.layout.fragment_recent_anime, container, false);
        return rootView;
    }

    private void initRecycler() {
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 || position == 25 || position == 26 || position == 51 || position == 52 || position == 77) ? 3 : 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mViewModel.getTVList(1).observe(getViewLifecycleOwner(), animeModels -> {
            sectionedAdapter = new SectionedRecyclerViewAdapter();
            AnimeSection tvSeries = new AnimeSection(AnimeSection.SERIES, "Series", RecentAddedFragment.this, animeModels);
            sectionedAdapter.addSection(tvSeries);
            mRecyclerView.setAdapter(sectionedAdapter);
            progressBar.setVisibility(View.GONE);

            mViewModel.getMovieList(1).observe(getViewLifecycleOwner(), animeModels1 -> {
                AnimeSection movies = new AnimeSection(AnimeSection.MOVIES, "Películas", RecentAddedFragment.this, animeModels1);
                sectionedAdapter.addSection(movies);
                sectionedAdapter.notifyDataSetChanged();

                mViewModel.getOvaList(1).observe(getViewLifecycleOwner(), animeModels11 -> {
                    AnimeSection ovas = new AnimeSection(AnimeSection.OVA, "OVAs", RecentAddedFragment.this, animeModels11);
                    sectionedAdapter.addSection(ovas);
                    sectionedAdapter.notifyDataSetChanged();
                });
            });
        });
    }

    @Override
    public void onItemRootViewClicked(final String title, final int itemAdapterPosition) {
        if (title.equals("Series")) {
            mViewModel.setSelectedAnime(mViewModel.getTVList(1).getValue().get(itemAdapterPosition - 1));
        }
        if (title.equals("Películas")) {
            mViewModel.setSelectedAnime(mViewModel.getMovieList(1).getValue().get(itemAdapterPosition - 27));
        }
        if (title.equals("OVAs")) {
            mViewModel.setSelectedAnime(mViewModel.getOvaList(1).getValue().get(itemAdapterPosition - 53));
        }

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }

    @Override
    public void onFooterRootViewClicked(String title) {
        for (int page = 0; page < SlidePagerAdapter.TABTITLES.length; page++) {
            if (SlidePagerAdapter.TABTITLES[page].equals(title)) {
                HomeFragment.selectPage(page);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                }
            }, 50);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 || position == 25 || position == 50) ? 3 : 1;
            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);

    }
}
