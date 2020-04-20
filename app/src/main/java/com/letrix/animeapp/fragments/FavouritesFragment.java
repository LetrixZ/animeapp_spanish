package com.letrix.animeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.AnimeAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment implements AnimeAdapter.OnItemClickListener {

    private MainViewModel mainViewModel;
    private List<AnimeModel> favoriteList;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AnimeAdapter adapter;
    private AppCompatImageView backButton;

    public FavouritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        backButton = view.findViewById(R.id.back);
        recyclerView = view.findViewById(R.id.favouritesRecyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.getFavouriteList().observe(getViewLifecycleOwner(), animeModels -> {
            if (animeModels != null) {
                favoriteList = animeModels;
                initRecycler(animeModels);
            }
        });

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void initRecycler(List<AnimeModel> animeList) {
        adapter = new AnimeAdapter(requireActivity(), gridLayoutManager, recyclerView, 0, FavouritesFragment.this);
        recyclerView.setAdapter(adapter);
        ArrayList<AnimeModel> insertList = new ArrayList<>(animeList);
        adapter.insertData(insertList);
    }

    @Override
    public void onItemClick(int position) {
        mainViewModel.setSelectedAnime(favoriteList.get(position));
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }
}
