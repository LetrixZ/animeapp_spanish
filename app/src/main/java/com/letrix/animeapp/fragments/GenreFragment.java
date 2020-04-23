package com.letrix.animeapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.MainActivity;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.GenreAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.BackedData;

import java.util.ArrayList;

import timber.log.Timber;

public class GenreFragment extends Fragment {

    private static final String TAG = "GenreFragment";
    private static String searchTerm;
    private RecyclerView recyclerView;
    private MainViewModel mainViewModel;
    private GenreAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;
    private int pageNumber = 1;
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 1;
    private int paginationLimit = 5;
    private ArrayList<AnimeModel> dataSource = new ArrayList<>();
    private int code = 200;
    private TextView genreTitle;
    private CardView genreCard;

    public GenreFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchTerm = getArguments().getString("genre");
        Timber.d("onCreate: %s", searchTerm);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (mainViewModel.getBackedData(7).getAnimeList() != null && mainViewModel.getBackedData(7).getPageName().equals(searchTerm)) {
            if (mainViewModel.getBackedData(7).getAnimeList().size() > dataSource.size()) {
                dataSource = mainViewModel.getBackedData(7).getAnimeList();
                pageNumber = mainViewModel.getBackedData(7).getPageNumber();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems + viewThreshold) && pageNumber <= paginationLimit) {
                        pageNumber++;
                        performPagination();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.genre_fragment, container, false);
        String titleTransitionName = null, genreTransitionName = null;

        Bundle b = getArguments();
        if (b != null) {
            titleTransitionName = b.getString("genreTransitionName");
            genreTransitionName = b.getString("cardTransitionName");
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBarGenre);
        genreTitle = view.findViewById(R.id.genreText);
        genreCard = view.findViewById(R.id.cardView);
        genreCard.setTransitionName(genreTransitionName);
        genreTitle.setTransitionName(titleTransitionName);
        String genre = searchTerm.replace("-", " ");
        genre = genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase();
        genreTitle.setText(genre);
        recyclerView.setHasFixedSize(true);
        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 6);
        } else {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        }
        recyclerView.setLayoutManager(gridLayoutManager);


        initData();

        return view;
    }

    private void initData() {
        progressBar.setVisibility(View.VISIBLE);
        if (dataSource.size() < pageNumber * 12) {
            mainViewModel.getGenreList(searchTerm, "default", 1).observe(getViewLifecycleOwner(), this::insertData);
        } else {
            Timber.d("initData: USING OLD DATA");
            adapter = new GenreAdapter(requireActivity(), gridLayoutManager, recyclerView, pageNumber, this);
            recyclerView.setAdapter(adapter);
            ArrayList<AnimeModel> insertList = new ArrayList<>(dataSource);
            adapter.insertData(insertList);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void insertData(ArrayList<AnimeModel> animeModels) {
        Timber.tag(TAG).d("initData: REQUESTING NEW DATA");
        Timber.d("insertData: HIDING ProgressBar");
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);
        dataSource = animeModels;
        adapter = new GenreAdapter(requireActivity(), gridLayoutManager, recyclerView, pageNumber, this);
        recyclerView.setAdapter(adapter);
        ArrayList<AnimeModel> insertList = new ArrayList<>(dataSource);
        adapter.insertData(insertList);
    }

    private void performPagination() {
        if (code == 200) {
            adapter.showProgressBar();
            mainViewModel.getGenreList(searchTerm, "default", pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
        }
    }

    private void updateRecycler(ArrayList<AnimeModel> animeModels) {
        mainViewModel.getCode().observe(getViewLifecycleOwner(), integer -> {
            code = integer;
            if (integer != 200) {
                Toast.makeText(requireActivity(), "404, ¡No pudimos obtener más anime :C!", Toast.LENGTH_SHORT).show();
                adapter.hideProgressBar();
            }
        });

        if (animeModels.size() == 24) {
            dataSource.addAll(animeModels);
            adapter.updateData(dataSource);
            Timber.d("performPagination: " + animeModels.size());
            if (paginationLimit <= 10) {
                paginationLimit++;
            }
        } else {
            paginationLimit = 0;
            dataSource.addAll(animeModels);
            adapter.updateData(dataSource);
            Timber.d("performPagination: " + animeModels.size());
            Toast.makeText(requireActivity(), "¡No hay más animes!", Toast.LENGTH_SHORT).show();
        }
        adapter.hideProgressBar();
    }

    /*@Override
    public void onItemClick(int position) {
        mainViewModel.setSelectedAnime(dataSource.get(position));
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainViewModel.addBackedData(new BackedData(searchTerm, dataSource, pageNumber), 7);
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
            ((MainActivity) requireActivity()).showFragmentWithTransition(this, animeInfo, "animeInfo", animeImage, animeTitle, "image_" + animeName, "title_" + animeName);
        }
    }
}

