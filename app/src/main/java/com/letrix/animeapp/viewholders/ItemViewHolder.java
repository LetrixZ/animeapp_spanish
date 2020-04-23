package com.letrix.animeapp.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView animeTitle, animeEpisode;
    public ImageView animeImage;
    public View rootView;
    public ProgressBar progressBar;

    public ItemViewHolder(View view) {
        super(view);

        rootView = view;
        animeTitle = view.findViewById(R.id.animeTitle);
        animeEpisode = view.findViewById(R.id.lastEpisode);
        animeImage = view.findViewById(R.id.animeImage);
        progressBar = view.findViewById(R.id.watchedBar);
    }
}
