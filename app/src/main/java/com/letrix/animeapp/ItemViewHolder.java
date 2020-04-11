package com.letrix.animeapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView animeTitle, animeEpisode;
    ImageView animeImage;
    View rootView;

    ItemViewHolder(View view) {
        super(view);

        rootView = view;
        animeTitle = view.findViewById(R.id.animeTitleHome);
        animeEpisode = view.findViewById(R.id.lastEpisodeHome);
        animeImage = view.findViewById(R.id.animeImageHome);
    }
}
