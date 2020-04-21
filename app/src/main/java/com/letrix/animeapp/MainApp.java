package com.letrix.animeapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        boolean nightMode = sharedPreferences.getBoolean("nightModeState", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }
}
