package com.letrix.animeapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.fragments.SearchFragment;
import com.letrix.animeapp.models.AnimeModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<AnimeModel> animeList;
    private List<AnimeModel> favoriteList;
    private SearchFragment fragment;

    public SearchAdapter(ArrayList<AnimeModel> anime, SearchFragment fragment) {
        this.animeList = anime;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        final ViewHolder viewHolder = holder;
        AnimeModel anime = this.animeList.get(position);
        viewHolder.animeTitle.setText(anime.getTitle());
        DecimalFormat format = new DecimalFormat("0.#");
        if (anime.getEpisodes() != null) {
            viewHolder.episodeNumber.setText(String.format(holder.itemView.getContext().getString(R.string.episode), format.format(anime.getEpisodes().get(1).getEpisode())));
        } else {
            viewHolder.episodeNumber.setText(holder.itemView.getContext().getText(R.string.noepisodes));
        }
        if (anime.getPoster().length() > 80) {
            byte[] decodedString = Base64.decode(anime.getPoster(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHolder.animeImage.setImageBitmap(decodedByte);
        } /*else {
            Picasso.get().load(anime.getPoster()).into(viewHolder.animeImage);
        }*/
        viewHolder.animeType.setText(anime.getType());
        if (anime.getType().equals("Anime")) {
            viewHolder.animeType.setVisibility(View.GONE);
            viewHolder.episodeNumber.setVisibility(View.VISIBLE);
        } else {
            viewHolder.animeType.setVisibility(View.VISIBLE);
            viewHolder.episodeNumber.setVisibility(View.GONE);
        }

        viewHolder.animeImage.setTransitionName("image_" + anime.getId());
        viewHolder.animeTitle.setTransitionName("title_" + anime.getId());

        viewHolder.itemView.setOnClickListener(v -> {
            fragment.animeInfo(anime, anime.getId(), v.findViewById(R.id.animeImage), v.findViewById(R.id.animeTitle));
        });
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView episodeNumber, animeTitle, animeType;
        private ImageView animeImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            episodeNumber = itemView.findViewById(R.id.episodeNumber);
            animeImage = itemView.findViewById(R.id.animeImage);
            animeTitle = itemView.findViewById(R.id.animeTitle);
            animeType = itemView.findViewById(R.id.animeType);

        }
    }
}
