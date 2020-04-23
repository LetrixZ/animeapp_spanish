package com.letrix.animeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.HomeFragment;
import com.letrix.animeapp.MainActivity;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.MainAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.EpisodeTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import timber.log.Timber;

public class RecentFragment extends Fragment {

    private MainViewModel mainViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private boolean tv = true, movies = true;
    private ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> watchedAnimeList = new ArrayList<>();

    private MainAdapter adapter;

    public RecentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);

        Timber.d("Getting watched anime list");
        if (mainViewModel.getCurrentlyWatching().size() > 0) {
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
        }

        adapter = new MainAdapter(getActivity(), this, mainViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mainViewModel.getTVList(1).observe(getViewLifecycleOwner(), animeModels -> {
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            mainViewModel.getMovieList(1).observe(getViewLifecycleOwner(), animeModels1 -> {
                adapter.notifyDataSetChanged();
                mainViewModel.getOvaList(1).observe(getViewLifecycleOwner(), animeModels2 -> {
                    adapter.notifyDataSetChanged();
                });
            });
        });
        return rootView;
    }

    public void animeInfo(AnimeModel anime, String id, View animeImage, View animeTitle) {
        if (requireActivity() instanceof MainActivity) {
            mainViewModel.setSelectedAnime(anime);
            InfoFragment animeInfo = new InfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageTransitionName", "image-" + id);
            bundle.putString("titleTransitionName", "title-" + id);
            bundle.putSerializable("anime", anime);
            animeInfo.setArguments(bundle);
            ((MainActivity) requireActivity()).showFragmentWithTransition(this, animeInfo, "animeInfo", animeImage, animeTitle, "image-" + id, "title-" + id);
        }
    }

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

    public ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> getWatchedList() {
        return watchedAnimeList;
    }

}
