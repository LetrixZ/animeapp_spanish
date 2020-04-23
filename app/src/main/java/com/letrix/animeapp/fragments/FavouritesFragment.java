package com.letrix.animeapp.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.MainActivity;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.AnimeAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment{

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
        gridLayoutManager = new GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false);
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
        adapter = new AnimeAdapter(gridLayoutManager, recyclerView, 0, this);
        recyclerView.setAdapter(adapter);
        ArrayList<AnimeModel> insertList = new ArrayList<>(animeList);
        adapter.insertData(insertList);
    }

    public void animeInfo(AnimeModel anime, String animeName, View animeImage, View animeTitle) {
        if (requireActivity() instanceof MainActivity) {
            mainViewModel.setSelectedAnime(anime);
            InfoFragment animeInfo = new InfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageTransitionName", "image_" + animeName);
            bundle.putString("titleTransitionName", "title_" + animeName);
            bundle.putSerializable("anime", anime);
            animeInfo.setArguments(bundle);
            ((MainActivity)requireActivity()).showFragmentWithTransition(this, animeInfo, "animeInfo", animeImage, animeTitle, "image_" + animeName, "title_" + animeName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().getSupportFragmentManager().popBackStack();
                return false;
            }
            return false;
        });
    }
}
