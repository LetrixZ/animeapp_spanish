package com.letrix.animeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.fragments.RecentFragment;
import com.letrix.animeapp.utils.AnimeSection;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int WATCHED = 1;
    private final int VERTICAL = 2;
    private Context context;
    private RecentFragment fragment;
    private MainViewModel mainViewModel;

    public MainAdapter(Context context, RecentFragment fragment, MainViewModel mainViewModel) {
        this.context = context;
        this.fragment = fragment;
        this.mainViewModel = mainViewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case WATCHED:
                view = inflater.inflate(R.layout.watched, parent, false);
                holder = new WatchedViewHolder(view);
                break;
            case VERTICAL:
                view = inflater.inflate(R.layout.vertical, parent, false);
                holder = new VerticalViewHolder(view);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VERTICAL)
            verticalView((VerticalViewHolder) holder);
        else if (holder.getItemViewType() == WATCHED)
            watchedView((WatchedViewHolder) holder);
    }

    private void verticalView(VerticalViewHolder holder) {
        SectionedRecyclerViewAdapter adapter = new SectionedRecyclerViewAdapter();
        holder.recyclerView.setHasFixedSize(true);
        mainViewModel.getTVList(1).observe(fragment.getViewLifecycleOwner(), animeModels -> {
            adapter.addSection("tv", new AnimeSection(animeModels, "Series", fragment, holder.recyclerView, context));
            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setVisibility(View.VISIBLE);
            mainViewModel.getMovieList(1).observe(fragment.getViewLifecycleOwner(), animeModels1 -> {
                adapter.addSection("movies", new AnimeSection(animeModels1, "Películas", fragment, holder.recyclerView, context));
                mainViewModel.getOvaList(1).observe(fragment.getViewLifecycleOwner(), animeModels2 -> {
                    adapter.addSection("ovas", new AnimeSection(animeModels2, "OVAs", fragment, holder.recyclerView, context));
                });
            });
        });
    }

    private void watchedView(WatchedViewHolder holder) {
        WatchedAdapter adapter = new WatchedAdapter(fragment.getWatchedList(), fragment);
        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return WATCHED;
        if (position == 1)
            return VERTICAL;
        return -1;
    }

    public class WatchedViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        WatchedViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
        }
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        VerticalViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
        }
    }
}
