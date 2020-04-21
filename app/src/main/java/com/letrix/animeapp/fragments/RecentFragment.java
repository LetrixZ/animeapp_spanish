package com.letrix.animeapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.HomeFragment;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.WatchedAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.EpisodeTime;
import com.letrix.animeapp.utils.AnimeSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import timber.log.Timber;

public class RecentFragment extends Fragment implements AnimeSection.ClickListener, WatchedAdapter.OnItemClickListener {

    private MainViewModel mainViewModel;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private boolean tv = true, movies = true;
    private ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> watchedAnimeList = new ArrayList<>();

    public RecentFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);
        TextView watchedAnimeText = rootView.findViewById(R.id.animeType);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        RecyclerView watchedRecyclerView = rootView.findViewById(R.id.watchedRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);
        watchedRecyclerView.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        watchedRecyclerView.setLayoutManager(layoutManager);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recyclerView.setNestedScrollingEnabled(false);
        watchedRecyclerView.setNestedScrollingEnabled(false);
        // Checking if there is any watched anime
        Timber.d("Checking if there is any watched anime");
        if (mainViewModel.getCurrentlyWatching().size() != 0) {
            // There are watched animes, so checking what those are
            ArrayList<EpisodeTime> tempEpisodeList = new ArrayList<>();
            HashMap<AnimeModel, ArrayList<EpisodeTime>> tempList = new HashMap<>();
            Timber.d("There are watched animes, so checking what those are");
            for (Map.Entry<AnimeModel, TreeMap<Integer, EpisodeTime>> entry : mainViewModel.getCurrentlyWatching().entrySet()) {
                tempEpisodeList = new ArrayList<>();
                Timber.d("Anime title: %s", entry.getKey().getTitle());
                // Checking what is the latest watched episode
                Timber.d("Episodes watched: %s", entry.getValue());
                for (Map.Entry<Integer, EpisodeTime> value : entry.getValue().entrySet()) {
                    tempEpisodeList.add(value.getValue());
                }
                tempList.put(entry.getKey(), tempEpisodeList);
            }
            for (EpisodeTime episodeTime : tempEpisodeList) {
                Timber.d("Anime: " + episodeTime.getAnimeTitle() + ", episodio " + episodeTime.getEpisodeNumber());
            }
            Set<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> anime = tempList.entrySet();
            watchedAnimeList = new ArrayList<>(anime);
            // Init RecyclerView
            WatchedAdapter adapter = new WatchedAdapter(watchedAnimeList, this, this);
            watchedRecyclerView.setAdapter(adapter);
            if (adapter.getItemCount() != 0) {
                watchedRecyclerView.setVisibility(View.VISIBLE);
                watchedAnimeText.setVisibility(View.VISIBLE);
                watchedAnimeText.setText(R.string.watching);
            } else {
                watchedRecyclerView.setVisibility(View.GONE);
                watchedAnimeText.setVisibility(View.GONE);
            }
        }

        GridLayoutManager gridLayoutManager;
        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 4);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0 || position == 25 || position == 26 || position == 50 || position == 51 || position == 77) ? 4 : 1;
                }
            });
        } else {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
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
            mainViewModel.getMovieList(1).observe(getViewLifecycleOwner(), animeModels1 -> {
                mainViewModel.getOvaList(1).observe(getViewLifecycleOwner(), animeModels2 -> {
                    sectionedAdapter.addSection("TV", new AnimeSection(animeModels, "Series", RecentFragment.this));
                    if (animeModels == null || animeModels.size() < 1) {
                        sectionedAdapter.removeSection("TV");
                        tv = false;
                    }
                    sectionedAdapter.addSection("Movies", new AnimeSection(animeModels1, "Películas", RecentFragment.this));
                    if (animeModels1 == null || animeModels1.size() < 1) {
                        sectionedAdapter.removeSection("Movies");
                        movies = false;
                    }
                    sectionedAdapter.notifyItemRangeChanged(24, 24);
                    sectionedAdapter.addSection("OVA", new AnimeSection(animeModels2, "OVAs", RecentFragment.this));
                    if (animeModels2 == null || animeModels2.size() < 1) {
                        sectionedAdapter.removeSection("OVA");
                    }
                    sectionedAdapter.notifyItemRangeChanged(48, 24);
                    recyclerView.setAdapter(sectionedAdapter);
                    progressBar.setVisibility(View.GONE);
                });
            });
        });

        return rootView;
    }

    public void setCompleted(int position) {
        Timber.d("%s completada!", watchedAnimeList.get(position).getKey().getTitle());
        mainViewModel.getCurrentlyWatching().remove(watchedAnimeList.get(position).getKey());
        Timber.d(String.valueOf(mainViewModel.getCurrentlyWatching().remove(watchedAnimeList.get(position).getKey())));
    }

    @Override
    public void onItemRootViewClicked(String title, int itemAdapterPosition) {
        switch (title) {
            case "Series":
                mainViewModel.setSelectedAnime(mainViewModel.getTVList(1).getValue().get(itemAdapterPosition - 1));
                break;
            case "Películas":
                if (tv) {
                    mainViewModel.setSelectedAnime(mainViewModel.getMovieList(1).getValue().get(itemAdapterPosition - 27));
                } else {
                    mainViewModel.setSelectedAnime(mainViewModel.getMovieList(1).getValue().get(itemAdapterPosition - 1));
                }
                break;
            case "OVAs":
                if (tv && movies) {
                    mainViewModel.setSelectedAnime(mainViewModel.getOvaList(1).getValue().get(itemAdapterPosition - 53));
                } else if (tv || movies) {
                    mainViewModel.setSelectedAnime(mainViewModel.getOvaList(1).getValue().get(itemAdapterPosition - 27));
                } else {
                    mainViewModel.setSelectedAnime(mainViewModel.getOvaList(1).getValue().get(itemAdapterPosition - 1));
                }
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

    @Override
    public void onItemClick(int position) {
        mainViewModel.setSelectedAnime(watchedAnimeList.get(position).getKey());
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        AtomicInteger tries = new AtomicInteger();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (tries.get() == 1) {
                    return false;
                }
                tries.getAndIncrement();
                Toast.makeText(requireActivity(), "Pulsa de nuevo para salir", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}
