package com.letrix.animeapp.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.HomeFragment;
import com.letrix.animeapp.MainActivity;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.GenreSelectorAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.Arrays;
import java.util.List;

public class GenreSelectorFragment extends Fragment {

    private static final String TAG = "GenreSelectorFragment";
    private List<String> genreList = Arrays.asList("accion", "artes-marciales", "aventura", "carreras", "ciencia-ficcion", "comedia", "demencia", "demonios", "deportes", "drama", "ecchi", "escolares", "espacial", "fantasia", "harem", "historico", "infantil", "josei", "juegos", "magia", "mecha", "militar", "misterio", "musica", "parodia", "psicologico", "recuentos-de-la-vida", "romance", "samurai", "seinen", "shoujo", "shounen", "sobrenatural", "superpoderes", "suspenso", "terror", "vampiros", "yaoi", "yuri");
    private RecyclerView recyclerView;
    private FlowLayoutManager flowLayoutManager;
    private GenreSelectorAdapter adapter;
    private MainViewModel mainViewModel;

    public GenreSelectorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_selector, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        flowLayoutManager.removeItemPerLineLimit();
        flowLayoutManager.setAlignment(Alignment.LEFT);
        recyclerView.setLayoutManager(flowLayoutManager);
        adapter = new GenreSelectorAdapter(genreList, this);
        recyclerView.setAdapter(adapter);

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

    public void searchGenre(String genre, View genreText, View cardView) {
        if (requireActivity() instanceof MainActivity) {
            GenreFragment genreFragment = new GenreFragment();
            Bundle bundle = new Bundle();
            bundle.putString("genreTransitionName", "genre_" + genre);
            bundle.putString("cardTransitionName", "card_" + genre);
            bundle.putString("genre", genre);
            genreFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).showFragmentWithTransition(this, genreFragment, "genreFragment", genreText, cardView, "genre_" + genre, "card_" + genre);
        }
    }
}
