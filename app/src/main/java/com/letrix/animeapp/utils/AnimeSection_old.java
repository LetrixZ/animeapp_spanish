package com.letrix.animeapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.viewholders.FooterViewHolder;
import com.letrix.animeapp.viewholders.HeaderViewHolder;
import com.letrix.animeapp.viewholders.ItemViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class AnimeSection_old extends Section {
    final public static int SERIES = 0;
    final public static int MOVIES = 1;
    final public static int OVA = 2;
    final public static int ONGOING = 3;
    final public static int FINISHED = 4;

    private final int type;
    private final String title;
    private final ClickListener clickListener;
    private final ArrayList<AnimeModel> list;

    public AnimeSection_old(int type, @NonNull String title, @NonNull ClickListener clickListener, ArrayList<AnimeModel> list) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.recycler_anime_home_item)
                .headerResourceId(R.layout.recycler_anime_mini_header)
                .footerResourceId(R.layout.recycler_footer)
                /*.failedResourceId(R.layout.section_ex3_failed)
                .loadingResourceId(R.layout.section_ex3_loading)*/
                .build());

        this.type = type;
        this.title = title;
        this.clickListener = clickListener;
        this.list = list;
    }


    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        AnimeModel anime = list.get(position);

        itemHolder.animeTitle.setText(anime.getTitle());
        DecimalFormat format = new DecimalFormat("#.#");
        itemHolder.animeEpisode.setText("Episodio " + format.format(anime.getEpisodes().get(1).getEpisode()));
        byte[] decodedString = Base64.decode(anime.getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        itemHolder.animeImage.setImageBitmap(decodedByte);

        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(title, itemHolder.getAdapterPosition())
        );
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.animeType.setText(title);
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(final View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(final RecyclerView.ViewHolder holder) {
        final FooterViewHolder footerHolder = (FooterViewHolder) holder;

        footerHolder.loadMore.setText("Ver más " + title);

        footerHolder.rootView.setOnClickListener(v ->
                clickListener.onFooterRootViewClicked(title)
        );
    }

    public interface ClickListener {

        void onItemRootViewClicked(final String title, final int itemAdapterPosition);

        void onFooterRootViewClicked(final String title);

    }
}
