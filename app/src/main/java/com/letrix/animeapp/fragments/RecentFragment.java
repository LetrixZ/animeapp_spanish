package com.letrix.animeapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
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
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.utils.AnimeSection;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class RecentFragment extends Fragment implements AnimeSection.ClickListener {

    private View rootView;
    private MainViewModel mainViewModel;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager gridLayoutManager;

    public RecentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_common, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0 || position == 25 || position == 26 || position == 50 || position == 51 || position == 77) ? 4 : 1;
                }
            });
        }
        else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0 || position == 25 || position == 26 || position == 51 || position == 52 || position == 77) ? 3 : 1;
                }
            });
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        sectionedAdapter = new SectionedRecyclerViewAdapter();
        progressBar.setVisibility(View.VISIBLE);
        mainViewModel.getTVList(1).observe(getViewLifecycleOwner(), animeModels -> {
            sectionedAdapter.addSection("TV", new AnimeSection(animeModels, "Series", RecentFragment.this));
            if (animeModels == null || animeModels.size() < 1) {
                sectionedAdapter.removeSection("TV");
            }
            recyclerView.setAdapter(sectionedAdapter);
            progressBar.setVisibility(View.GONE);
            mainViewModel.getMovieList(1).observe(getViewLifecycleOwner(), animeModels1 -> {
                sectionedAdapter.addSection("Movies", new AnimeSection(animeModels1, "Películas", RecentFragment.this));
                if (animeModels1 == null || animeModels1.size() < 1) {
                    sectionedAdapter.removeSection("Movies");
                }
                sectionedAdapter.notifyItemRangeChanged(24, 24);
                mainViewModel.getOvaList(1).observe(getViewLifecycleOwner(), animeModels2 -> {
                    sectionedAdapter.addSection("OVA", new AnimeSection(animeModels2, "OVAs", RecentFragment.this));
                    if (animeModels2 == null || animeModels2.size() < 1) {
                        sectionedAdapter.removeSection("OVA");
                    }
                    sectionedAdapter.notifyItemRangeChanged(48, 24);
                });
            });
        });

        return rootView;
    }

    @Override
    public void onItemRootViewClicked(String title, int itemAdapterPosition) {
        switch (title) {
            case "Series":
                mainViewModel.setSelectedAnime(mainViewModel.getTVList(1).getValue().get(itemAdapterPosition - 1));
                break;
            case "Películas":
                mainViewModel.setSelectedAnime(mainViewModel.getMovieList(1).getValue().get(itemAdapterPosition - 27));
                break;
            case "OVAs":
                mainViewModel.setSelectedAnime(mainViewModel.getOvaList(1).getValue().get(itemAdapterPosition - 53));
                break;
        }
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }

    @Override
    public void onFooterRootViewClicked(String title) {
        switch (title) {
            case "Series":
                HomeFragment.selectPage(5);
                break;
            case "Películas":
                HomeFragment.selectPage(6);
                break;
            case "OVAs":
                HomeFragment.selectPage(7);
                break;
        }
    }
}
