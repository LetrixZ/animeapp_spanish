package com.letrix.animeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.restoreFavourite(loadData());

        if (savedInstanceState == null) {
            FragmentManager fManager = getSupportFragmentManager();
            FragmentTransaction fTransaction = fManager.beginTransaction();
            fTransaction.add(R.id.fragment_navigation_host, new HomeFragment());
            fTransaction.commit();
        }

    }

    private ArrayList<AnimeModel> loadData() {
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

    private void saveData(List<AnimeModel> animeModels) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(animeModels);
        editor.putString("favourite list", json);
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData(mainViewModel.getFavouriteList().getValue());
        Log.d("info", "data saved");
        mainViewModel.getFavouriteList().observe(this, new Observer<List<AnimeModel>>() {
            @Override
            public void onChanged(List<AnimeModel> animeModels) {
                saveData(animeModels);
                Log.d("info", "data saved");
            }
        });
    }


}
