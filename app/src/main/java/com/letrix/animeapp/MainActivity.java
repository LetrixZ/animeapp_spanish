package com.letrix.animeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.restoreFavourite(loadFavorites());
        mainViewModel.restoreWatched(loadWatched());

        if (savedInstanceState == null) {
            FragmentManager fManager = getSupportFragmentManager();
            FragmentTransaction fTransaction = fManager.beginTransaction();
            fTransaction.add(R.id.fragment_navigation_host, new HomeFragment());
            fTransaction.commit();
        }

    }

    private ArrayList<AnimeModel> loadFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favourite list", null);
        Type type = new TypeToken<ArrayList<AnimeModel>>() {
        }.getType();
        ArrayList<AnimeModel> favouriteList = gson.fromJson(json, type);

        if (favouriteList == null) {
            favouriteList = new ArrayList<>();
        }
        return favouriteList;
    }

    private void saveFavorites(List<AnimeModel> animeModels) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(animeModels);
        editor.putString("favourite list", json);
        editor.apply();
    }

    private HashMap<String, Long> loadWatched() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("watched list", null);
        Type type = new TypeToken<HashMap<String, Long>>() {
        }.getType();
        HashMap<String, Long> watchedList = gson.fromJson(json, type);

        if (watchedList == null) {
            watchedList = new HashMap<>();
        }
        return watchedList;
    }

    private void saveWatched(HashMap<String, Long> watched) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(watched);
        editor.putString("watched list", json);
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveFavorites(mainViewModel.getFavouriteList().getValue());
        saveWatched(mainViewModel.getWatchedEpisodesMap());
        Log.d("info", "data saved");
        mainViewModel.getFavouriteList().observe(this, animeModels -> {
            saveFavorites(animeModels);
            Log.d("info", "data saved");
        });
    }


}
