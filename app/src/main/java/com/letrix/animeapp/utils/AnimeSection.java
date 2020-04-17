package com.letrix.animeapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
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
    private ClickListener clickListener;

    public AnimeSection(ArrayList<AnimeModel> animeLists, String title, ClickListener clickListener) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.recycler_anime_home_item)
                .headerResourceId(R.layout.recycler_anime_mini_header)
                .footerResourceId(R.layout.recycler_footer)
                .build());
        this.animeList = animeLists;
        this.title = title;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return animeList.size(); // number of items of this section
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

        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(title, itemHolder.getAdapterPosition()));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.animeType.setText(title);
        /*headerHolder.rootView.setOnClickListener(v -> {
            clickListener.onHeaderRootViewclicked();
        });*/
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        footerViewHolder.loadMore.setText(String.format(holder.itemView.getContext().getString(R.string.seemore), title));

        footerViewHolder.rootView.setOnClickListener(v ->
                clickListener.onFooterRootViewClicked(title)
        );
    }

    public interface ClickListener {

        void onItemRootViewClicked(final String title, final int itemAdapterPosition);

        void onFooterRootViewClicked(final String title);

    }

}
