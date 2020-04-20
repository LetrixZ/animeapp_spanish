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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.EpisodeTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends FragmentActivity {

    private MainViewModel mainViewModel;
    public static Boolean saveFavorites = true, saveWatched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.restoreFavourite(loadFavorites());
        mainViewModel.restoreCurrentWatching(loadWatched(), loadWatchedNew());
        mainViewModel.restoreFLV(loadEnableFLV());
        HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> temp = loadWatchedNew();

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

    private void saveEnable(Boolean isFlvEnabled) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(isFlvEnabled);
        editor.putString("flv", json);
        editor.apply();
    }

    private Boolean loadEnableFLV() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("flv", null);
        Type type = new TypeToken<Boolean>() {
        }.getType();
        Boolean enableFLV = gson.fromJson(json, type);

        return enableFLV;
    }

    // NEW

    private HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> loadWatchedNew() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
        Gson gson = gsonBuilder.create();
        String json = sharedPreferences.getString("watched list new", null);
        Type type = new TypeToken<HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>>>() {
        }.getType();
        HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> watchedList = gson.fromJson(json, type);

        if (watchedList == null) {
            watchedList = new HashMap<>();
        }
        return watchedList;
    }

    private void saveWatchedNew(HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> watched) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(watched);
        editor.putString("watched list new", json);
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (saveFavorites) {
            saveFavorites(mainViewModel.getFavouriteList().getValue());
        }
        if (saveWatched) {
            saveWatched(mainViewModel.getWatchedEpisodesMap());
            saveWatchedNew(mainViewModel.getCurrentlyWatching());
        }
        saveEnable(mainViewModel.getEnableFLV());
        Timber.d("Data saved");
    }


}
