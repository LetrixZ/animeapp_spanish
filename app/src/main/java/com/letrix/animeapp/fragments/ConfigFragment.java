package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.letrix.animeapp.HomeFragment;
import com.letrix.animeapp.MainActivity;
import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;

public class ConfigFragment extends Fragment {

    private Button deleteFavorites, deleteWatched;
    private ToggleButton toggleFlv;
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

        deleteFavorites.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "Borrando favoritos", Toast.LENGTH_SHORT).show();
            SharedPreferences settings = requireActivity().getSharedPreferences("shared preferences", requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("favourite list");
            editor.commit();
            mainViewModel.clearFavorites();
        });

        deleteWatched.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "Borrando vistos", Toast.LENGTH_SHORT).show();
            SharedPreferences settings = requireActivity().getSharedPreferences("shared preferences", requireActivity().MODE_PRIVATE);
            settings.edit().remove("watched list").commit();
            settings.edit().remove("watched list new").commit();
            mainViewModel.clearCurrentlyWatching();
            //ProcessPhoenix.triggerRebirth(requireActivity());
        });

        if (mainViewModel.getEnableFLV() != null) {
            toggleFlv.setChecked(mainViewModel.getEnableFLV());
        }

        toggleFlv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mainViewModel.setEnableFLV(isChecked);
        });

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
