package com.letrix.animeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import java.util.List;
import java.util.TreeMap;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static Boolean saveFavorites = true, saveWatched = true;
    boolean doubleBackToExitPressedOnce = false;
    private MainViewModel mainViewModel;

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

    // NEW

    private Boolean loadEnableFLV() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("flv", null);
        Type type = new TypeToken<Boolean>() {
        }.getType();
        Boolean enableFLV = gson.fromJson(json, type);

        return enableFLV;
    }

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

    public void showFragmentWithTransition(Fragment current, Fragment newFragment, String tag, View sharedView, View sharedView2, String sharedElementName, String sharedElementName2) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // check if the fragment is in back stack
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(tag, 0);
        if (fragmentPopped) {
            // fragment is pop from backStack
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
                current.setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));

                Transition titleExit = TransitionInflater.from(this).inflateTransition(R.transition.title_transition);
                Transition image = TransitionInflater.from(this).inflateTransition(R.transition.default_transition).excludeTarget(sharedElementName2, true);
                TransitionSet set = new TransitionSet();
                set.addTransition(titleExit).addTransition(image);
                newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
                newFragment.setSharedElementReturnTransition(set);
                newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.fade));
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (sharedView2 != null) {
                fragmentTransaction.replace(R.id.fragment_navigation_host, newFragment, tag)
                        .addToBackStack(tag)
                        .addSharedElement(sharedView, sharedElementName)
                        .addSharedElement(sharedView2, sharedElementName2)
                        .commit();
            } else {
                fragmentTransaction.replace(R.id.fragment_navigation_host, newFragment, tag)
                        .addToBackStack(tag)
                        .addSharedElement(sharedView, sharedElementName)
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

}
