package com.letrix.animeapp.fragments;

import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.AnimeSection;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.TitlelessAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class FinishedFragment extends Fragment implements TitlelessAdapter.OnItemClickListener {

    private MainViewModel mViewModel;
    private RecyclerView mRecyclerView;
    //private SectionedRecyclerViewAdapter sectionedAdapter;
    private TitlelessAdapter adapter;
    private ProgressBar progressBar;
    private ViewGroup rootView;
    private GridLayoutManager gridLayoutManager;

    public FinishedFragment() {
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
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_finished, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        progressBar = rootView.findViewById(R.id.progressbar);
        mRecyclerView = rootView.findViewById(R.id.finishedRecyclerView);

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);

        return rootView;
    }

    private void initRecycler() {
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mViewModel.getFinishedList(1).observe(getViewLifecycleOwner(), new Observer<ArrayList<AnimeModel>>() {
            @Override
            public void onChanged(ArrayList<AnimeModel> animeModels) {
                adapter = new TitlelessAdapter(animeModels, FinishedFragment.this::onItemClick);
                mRecyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

                mViewModel.getFinishedList(2).observe(getViewLifecycleOwner(), new Observer<ArrayList<AnimeModel>>() {
                    @Override
                    public void onChanged(ArrayList<AnimeModel> animeModels) {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        mViewModel.setSelectedAnime(mViewModel.getFinishedList(1).getValue().get(position));

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }
}
