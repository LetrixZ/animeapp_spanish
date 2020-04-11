package com.letrix.animeapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.AnimeSection;
import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class RecentAddedFragment extends Fragment implements AnimeSection.ClickListener {

    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private MainViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private ProgressBar progressBar;
    private ViewGroup rootView;
    private Parcelable mListState = null;
    private GridLayoutManager gridLayoutManager;

    public RecentAddedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
        initRecycler();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_recent_anime, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        progressBar = rootView.findViewById(R.id.progressbar);
        mRecyclerView = rootView.findViewById(R.id.recentRecyclerView);

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);

        //view = inflater.inflate(R.layout.fragment_recent_anime, container, false);
        return rootView;
    }

    private void initRecycler() {
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 || position == 25 || position == 50) ? 3 : 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mViewModel.getTvList().observe(getViewLifecycleOwner(), new Observer<ArrayList<AnimeModel>>() {
            @Override
            public void onChanged(ArrayList<AnimeModel> animeModels) {
                sectionedAdapter = new SectionedRecyclerViewAdapter();
                AnimeSection tvSeries = new AnimeSection(AnimeSection.SERIES, "Series", RecentAddedFragment.this::onItemRootViewClicked, animeModels);
                sectionedAdapter.addSection(tvSeries);
                mRecyclerView.setAdapter(sectionedAdapter);
                progressBar.setVisibility(View.GONE);

                mViewModel.getMovieList().observe(getViewLifecycleOwner(), new Observer<ArrayList<AnimeModel>>() {
                    @Override
                    public void onChanged(ArrayList<AnimeModel> animeModels) {
                        AnimeSection movies = new AnimeSection(AnimeSection.MOVIES, "Películas", RecentAddedFragment.this::onItemRootViewClicked, animeModels);
                        sectionedAdapter.addSection(movies);
                        sectionedAdapter.notifyDataSetChanged();

                        mViewModel.getOvaList().observe(getViewLifecycleOwner(), new Observer<ArrayList<AnimeModel>>() {
                            @Override
                            public void onChanged(ArrayList<AnimeModel> animeModels) {
                                AnimeSection ovas = new AnimeSection(AnimeSection.OVA, "OVAs", RecentAddedFragment.this::onItemRootViewClicked, animeModels);
                                sectionedAdapter.addSection(ovas);
                                sectionedAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });

            }
        });
    }

    @Override
    public void onItemRootViewClicked(final String title, final int itemAdapterPosition) {
        if (title.equals("Series")) {
            mViewModel.setSelectedAnime(mViewModel.getTvList().getValue().get(itemAdapterPosition - 1));
        }
        if (title.equals("Películas")) {
            mViewModel.setSelectedAnime(mViewModel.getMovieList().getValue().get(itemAdapterPosition - 26));
        }
        if (title.equals("OVAs")) {
            mViewModel.setSelectedAnime(mViewModel.getOvaList().getValue().get(itemAdapterPosition - 51));
        }

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                }
            }, 50);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 || position == 25 || position == 50) ? 3 : 1;
            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);

    }
}
