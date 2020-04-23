package com.letrix.animeapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.fragments.RecentFragment;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.EpisodeTime;
import com.letrix.animeapp.viewholders.FooterViewHolder;
import com.letrix.animeapp.viewholders.HeaderViewHolder;
import com.letrix.animeapp.viewholders.ItemViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class WatchedSection extends Section {

    private ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> watchedAnimeList;
    private String title;
    private RecentFragment fragment;

    public WatchedSection(ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> watchedAnimeList, String title, RecentFragment fragment) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.recycler_anime_watched_item)
                .headerResourceId(R.layout.recycler_anime_mini_header)
                .loadingResourceId(R.layout.loading)
                .build());
        this.watchedAnimeList = watchedAnimeList;
        this.title = title;
        this.fragment = fragment;
    }

    @Override
    public int getContentItemsTotal() {
        return watchedAnimeList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        AnimeModel currentItem = watchedAnimeList.get(position).getKey();
        ArrayList<EpisodeTime> currentEpisode = watchedAnimeList.get(position).getValue();
        itemHolder.animeTitle.setText(currentItem.getTitle());
        byte[] decodedString = Base64.decode(currentItem.getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        itemHolder.animeImage.setImageBitmap(decodedByte);
        //Timber.d("Anime title: %s", currentItem.getTitle());
        int size = currentItem.getEpisodes().size();
        // Checking the latest watched episode
        if (currentEpisode.get(currentEpisode.size() - 1).getProgressPosition() < 90) {
            itemHolder.animeEpisode.setText(String.format(itemHolder.itemView.getResources().getString(R.string.episode), String.valueOf(currentEpisode.get(currentEpisode.size() - 1).getEpisodeNumber())));
            itemHolder.progressBar.setProgress((int) currentEpisode.get(currentEpisode.size() - 1).getProgressPosition());
            if (currentEpisode.get(currentEpisode.size() - 1).getEpisodePosition() == 0 && currentEpisode.get(currentEpisode.size() - 1).getEpisodeDuration() == 1) {
                itemHolder.animeEpisode.setVisibility(View.GONE);
                itemHolder.progressBar.setVisibility(View.GONE);
            }
        } else if (currentEpisode.get(0).getProgressPosition() < 90) {
            Iterator<EpisodeTime> iterator = watchedAnimeList.get(position).getValue().iterator();
            while (iterator.hasNext()) {
                EpisodeTime episodeTime = iterator.next();
                if (episodeTime.getProgressPosition() > 90) {
                    iterator.remove();
                    //Timber.d(String.valueOf(episodeTime.getEpisodeNumber()));
                } else {
                    itemHolder.animeEpisode.setText(String.format(itemHolder.itemView.getResources().getString(R.string.episode), String.valueOf(episodeTime.getEpisodeNumber())));
                    itemHolder.progressBar.setProgress((int) episodeTime.getProgressPosition());
                }
            }
        } else if (currentItem.getEpisodes().get(size - currentEpisode.size() - 1).getId() == null) {
            itemHolder.animeEpisode.setText(R.string.completed);
            itemHolder.progressBar.setProgress(0);
        }
        else if (currentEpisode.size() != currentItem.getEpisodes().size() - 1){
            itemHolder.animeEpisode.setText(String.format(itemHolder.itemView.getResources().getString(R.string.episode), String.valueOf((int) currentItem.getEpisodes().get(currentItem.getEpisodes().size() - currentEpisode.size() - 1).getEpisode())));
            itemHolder.progressBar.setProgress(0);
        }
        int sum = 0;
        for (EpisodeTime episodeTime : currentEpisode) {
            //Timber.d("Episodio: %s", episodeTime.getEpisodeNumber());
            //Timber.d("Progreso: %s", episodeTime.getProgressPosition());
            if (episodeTime.getEpisodeDuration() == 1 && episodeTime.getEpisodePosition() == 0) {
                sum++;
            }
        }
        if (sum == currentItem.getEpisodes().size() - 1) {
            itemHolder.animeEpisode.setVisibility(View.VISIBLE);
            itemHolder.animeEpisode.setText(R.string.completed);
            itemHolder.progressBar.setVisibility(View.GONE);
        }

        itemHolder.animeImage.setTransitionName("image_" + currentItem.getId());
        itemHolder.animeTitle.setTransitionName("title_" + currentItem.getId());

        itemHolder.itemView.setOnClickListener(v -> {
            fragment.animeInfo(currentItem, currentItem.getId(), v.findViewById(R.id.animeImage), v.findViewById(R.id.animeTitle));
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.animeType.setText(title);
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        footerViewHolder.loadMore.setText(String.format(holder.itemView.getContext().getString(R.string.seemore), title));

        footerViewHolder.rootView.setOnClickListener(v -> {
            fragment.onFooterRootViewClicked(title);
        });
    }
}
