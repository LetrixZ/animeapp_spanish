package com.letrix.animeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.AnimeAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.BackedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommonFragment extends Fragment implements AnimeAdapter.OnItemClickListener {

    private static int TYPE = 1;
    private RecyclerView recyclerView;
    private String TAG = "CommonFragment" + TYPE;
    private Map<Integer, String> TYPE_MAP;
    private MainViewModel mainViewModel;
    private AnimeAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;
    private int pageNumber = 1;
    private boolean isLoading = true;
    private boolean blockClick = false;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 1;
    private int paginationLimit = 5;

    private ArrayList<AnimeModel> dataSource = new ArrayList<>();
    private int code = 200;

    public static CommonFragment newInstance(int TYPE) {
        Bundle args = new Bundle();
        args.putInt("TYPE", TYPE);
        CommonFragment f = new CommonFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put(1, "ONGOINGS");
        TYPE_MAP.put(2, "FINISHED");
        TYPE_MAP.put(3, "SERIES");
        TYPE_MAP.put(4, "MOVIES");
        TYPE_MAP.put(5, "OVAS");
        TYPE_MAP.put(6, "SPECIALS");
        if (getArguments() != null) {
            TYPE = getArguments().getInt("TYPE");
            TAG = "CommonFragment " + TYPE_MAP.get(TYPE);
        }
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        TYPE = getArguments().getInt("TYPE");
        /*mainViewModel.getBackedData().observe(requireActivity(), backedData -> {
            TYPE = getArguments().getInt("TYPE");
            dataSource = backedData.get(TYPE).getAnimeList();
            pageNumber = backedData.get(TYPE).getPageNumber();
            Log.d(TAG, "onCreate: GETTING DATA! " + TYPE_MAP.get(TYPE));
            Log.d(TAG, "onCreate: page: " + pageNumber);
        });*/
        if (mainViewModel.getBackedData(TYPE).getAnimeList() != null) {
            if (mainViewModel.getBackedData(TYPE).getAnimeList().size() > dataSource.size()) {
                dataSource = mainViewModel.getBackedData(TYPE).getAnimeList();
                pageNumber = mainViewModel.getBackedData(TYPE).getPageNumber();
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
        View view = inflater.inflate(R.layout.fragment_common, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        if (dataSource != null) {
            Log.d(TAG, "onViewCreated: DataSource != null");
            initData();
        } else {
            Log.d(TAG, "onViewCreated: DataSource == null");
            Log.d(TAG, "onCreateView: " + TYPE_MAP.get(TYPE));
            //initData();
        }

        return view;
    }

    private void initData() {
        if (dataSource.size() < pageNumber * 12) {
            progressBar.setVisibility(View.VISIBLE);
            blockClick = true;
            assert getArguments() != null;
            TYPE = getArguments().getInt("TYPE");
            switch (TYPE_MAP.get(TYPE)) {
                case "FINISHED":
                    mainViewModel.getFinished(1).observe(getViewLifecycleOwner(), this::insertData);
                    break;
                case "ONGOINGS":
                    mainViewModel.getOngoings(1).observe(getViewLifecycleOwner(), this::insertData);
                    break;
                case "SERIES":
                    mainViewModel.getSeries(1).observe(getViewLifecycleOwner(), this::insertData);
                    break;
                case "MOVIES":
                    mainViewModel.getMovies(1).observe(getViewLifecycleOwner(), this::insertData);
                    break;
                case "OVAS":
                    mainViewModel.getOvas(1).observe(getViewLifecycleOwner(), this::insertData);
                    break;
                case "SPECIALS":
                    paginationLimit = 1;
                    mainViewModel.getSpecials(1).observe(getViewLifecycleOwner(), this::insertData);
                    break;

            }
        } else {
            Log.d(TAG, "initData: USING OLD DATA");
            adapter = new AnimeAdapter(getActivity(), gridLayoutManager, recyclerView, pageNumber, CommonFragment.this);
            recyclerView.setAdapter(adapter);
            ArrayList<AnimeModel> insertList = new ArrayList<>(dataSource);
            adapter.insertData(insertList);
        }
    }

    private void insertData(ArrayList<AnimeModel> animeModels) {
        Log.d(TAG, "initData: REQUESTING NEW DATA");
        dataSource = animeModels;
        adapter = new AnimeAdapter(getActivity(), gridLayoutManager, recyclerView, pageNumber, CommonFragment.this);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        blockClick = false;
        ArrayList<AnimeModel> insertList = new ArrayList<>(dataSource);
        adapter.insertData(insertList);
    }

    private void performPagination() {
        if (code == 200) {
            adapter.showProgressBar();
            if (dataSource.size() != pageNumber * 24) {
                if (getArguments() != null) {
                    TYPE = getArguments().getInt("TYPE");
                }
                switch (Objects.requireNonNull(TYPE_MAP.get(TYPE))) {
                    case "FINISHED":
                        mainViewModel.getFinished(pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
                        break;
                    case "ONGOINGS":
                        mainViewModel.getOngoings(pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
                        break;
                    case "SERIES":
                        mainViewModel.getSeries(pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
                        break;
                    case "MOVIES":
                        mainViewModel.getMovies(pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
                        break;
                    case "OVAS":
                        mainViewModel.getOvas(pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
                        break;
                    case "SPECIALS":
                        //mainViewModel.getSpecials(pageNumber).observe(getViewLifecycleOwner(), this::updateRecycler);
                        paginationLimit = 1;
                        adapter.hideProgressBar();
                        break;
                }
            } else {
                adapter.updateData(dataSource);
                adapter.hideProgressBar();
            }
        }
    }

    private void updateRecycler(ArrayList<AnimeModel> animeModels) {
        mainViewModel.getCode().observe(getViewLifecycleOwner(), integer -> {
            code = integer;
            if (integer != 200) {
                Toast.makeText(getActivity(), "404, ¡No pudimos obtener más anime :C!", Toast.LENGTH_SHORT).show();
                adapter.hideProgressBar();
            }
        });

        if (animeModels.size() == 24) {
            dataSource.addAll(animeModels);
            adapter.updateData(dataSource);
            Log.d(TAG, "performPagination: " + animeModels.size());
            if (paginationLimit < 8) {
                paginationLimit++;
            }
        } else {
            paginationLimit = 0;
            dataSource.addAll(animeModels);
            adapter.updateData(dataSource);
            Log.d(TAG, "performPagination: " + animeModels.size());
            Toast.makeText(getActivity(), "¡No hay más animes!", Toast.LENGTH_SHORT).show();
        }
        adapter.hideProgressBar();
    }

    @Override
    public void onItemClick(int position) {
        if (!adapter.isLoading || !blockClick) {
            TYPE = getArguments().getInt("TYPE");
            Log.d(TAG, "onItemClick: " + TYPE);
            Log.d(TAG, "onItemClick: " + position);
            if (dataSource != null) {
                mainViewModel.setSelectedAnime(dataSource.get(position));
            } else {
                dataSource = mainViewModel.getBackedData(TYPE).getAnimeList();
                Log.d(TAG, "onItemClick: DATASOURCE = NULL");
            }
            final FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
            transaction.addToBackStack("TAG");
            transaction.commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dataSource != null) {
            if (dataSource.size() > 1) {
                assert getArguments() != null;
                TYPE = getArguments().getInt("TYPE");
                Log.d(TAG, "onDestroy: Backing up data");
                Log.d(TAG, "onDestroy: " + dataSource.size() + " " + pageNumber);
                mainViewModel.addBackedData(new BackedData(TYPE_MAP.get(TYPE), dataSource, pageNumber), TYPE);
            }
        }
    }

}

