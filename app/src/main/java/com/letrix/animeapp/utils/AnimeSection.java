package com.letrix.animeapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.fragments.RecentFragment;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.viewholders.FooterViewHolder;
import com.letrix.animeapp.viewholders.HeaderViewHolder;
import com.letrix.animeapp.viewholders.ItemViewHolder;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class AnimeSection extends Section {

    private ArrayList<AnimeModel> animeList;
    private String title;
    private RecentFragment fragment;


    public AnimeSection(ArrayList<AnimeModel> animeLists, String title, RecentFragment fragment, RecyclerView recyclerView, Context context) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.recycler_anime_home_item)
                .headerResourceId(R.layout.recycler_anime_mini_header)
                .footerResourceId(R.layout.recycler_footer)
                .build());
        this.animeList = animeLists;
        this.title = title;
        this.fragment = fragment;
        GridLayoutManager gridLayoutManager;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(context, 4);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0 || position == animeList.size() + 1 || position == animeList.size() + 2 || position == (animeList.size() * 2) + 3 || position == (animeList.size() * 2) + 4 || position == (animeList.size() * 3) + 5) ? 4 : 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(context, 3);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0 || position == animeList.size() + 1 || position == animeList.size() + 2 || position == (animeList.size() * 2) + 3 || position == (animeList.size() * 2) + 4 || position == (animeList.size() * 3) + 5) ? 3 : 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    @Override
    public int getContentItemsTotal() {
        return animeList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        AnimeModel currentAnime = animeList.get(position);
        itemHolder.animeTitle.setText(currentAnime.getTitle());
        if (currentAnime.getEpisodes() != null) {
            itemHolder.animeEpisode.setText(String.format(itemHolder.itemView.getContext().getString(R.string.episode), String.valueOf(currentAnime.getEpisodes().size() - 1)));
        } else {
            itemHolder.animeEpisode.setText(itemHolder.itemView.getContext().getString(R.string.noepisodes));
        }
        byte[] decodedString = Base64.decode(currentAnime.getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        itemHolder.animeImage.setImageBitmap(decodedByte);

        itemHolder.animeImage.setTransitionName("image_" + currentAnime.getTitle());
        itemHolder.animeTitle.setTransitionName("title_" + currentAnime.getTitle());

        itemHolder.rootView.setOnClickListener(v -> {
            fragment.animeInfo(currentAnime, currentAnime.getTitle(), v.findViewById(R.id.animeImage), v.findViewById(R.id.animeTitle));
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
