package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.letrix.animeapp.HomeFragment;
import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;

public class ConfigFragment extends Fragment {

    private Button deleteFavorites, deleteWatched;
    private ToggleButton toggleFlv;
    private Button toggleNight;
    private MainViewModel mainViewModel;

    public ConfigFragment() {
    }

    @SuppressLint("ApplySharedPref")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        deleteFavorites = view.findViewById(R.id.deleteFavorites);
        deleteWatched = view.findViewById(R.id.deleteWatched);
        toggleFlv = view.findViewById(R.id.toggleFlv);
        toggleNight = view.findViewById(R.id.toggleNight);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean nightMode = sharedPreferences.getBoolean("nightModeState", false);

        deleteFavorites.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "Borrando favoritos", Toast.LENGTH_SHORT).show();
            editor.remove("favourite list");
            editor.commit();
            mainViewModel.clearFavorites();
        });

        deleteWatched.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "Borrando vistos", Toast.LENGTH_SHORT).show();
            editor.remove("watched list").commit();
            editor.remove("watched list new").commit();
            mainViewModel.clearCurrentlyWatching();
        });

        if (mainViewModel.getEnableFLV() != null) {
            toggleFlv.setChecked(mainViewModel.getEnableFLV());
        }

        toggleFlv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mainViewModel.setEnableFLV(isChecked);
        });

        if (nightMode) {
            toggleNight.setText(requireActivity().getResources().getString(R.string.yes));
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            toggleNight.setText(requireActivity().getResources().getString(R.string.no));
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        toggleNight.setOnClickListener((v ->  {
            if (nightMode) {
                editor.putBoolean("nightModeState", false);
                editor.apply();
                toggleNight.setText(requireActivity().getResources().getString(R.string.yes));
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                editor.putBoolean("nightModeState", true);
                editor.apply();
                toggleNight.setText(requireActivity().getResources().getString(R.string.no));
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                HomeFragment.selectPage(2);

                return true;
            }
            return false;
        });
    }
}
