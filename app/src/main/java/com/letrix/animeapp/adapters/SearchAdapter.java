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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.models.AnimeModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String TAG = "SearchAdapter";
    private ArrayList<AnimeModel> anime;
    private List<AnimeModel> favoriteList;
    private View view;
    private OnItemClickListener mOnItemClickListener;
    private int clickedTextViewPos=-1;

    public SearchAdapter(ArrayList<AnimeModel> anime, List<AnimeModel> favoriteList, OnItemClickListener onItemClickListener) {
        this.anime = anime;
        this.favoriteList = favoriteList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_search_item, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        final ViewHolder viewHolder = holder;
        viewHolder.animeTitle.setText(anime.get(position).getTitle());
        DecimalFormat format = new DecimalFormat("0.#");
        if (anime.get(position).getEpisodes() != null) {
            viewHolder.episodeNumber.setText(String.format(holder.itemView.getContext().getString(R.string.episode), format.format(anime.get(position).getEpisodes().get(1).getEpisode())));
        } else {
            viewHolder.episodeNumber.setText(holder.itemView.getContext().getText(R.string.noepisodes));
        }
        byte[] decodedString = Base64.decode(anime.get(position).getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        viewHolder.animeImage.setImageBitmap(decodedByte);
        viewHolder.animeType.setText(anime.get(position).getType());
        if (anime.get(position).getType().equals("Anime")) {
            viewHolder.animeType.setVisibility(View.GONE);
            viewHolder.episodeNumber.setVisibility(View.VISIBLE);
        } else {
            viewHolder.animeType.setVisibility(View.VISIBLE);
            viewHolder.episodeNumber.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return anime.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnItemClickListener onItemClickListener;
        private TextView episodeNumber, animeTitle, animeType;
        private ImageView animeImage;
        AppCompatImageView favoriteButton;

        ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            episodeNumber = itemView.findViewById(R.id.episodeNumber);
            animeImage = itemView.findViewById(R.id.animeImage);
            animeTitle = itemView.findViewById(R.id.animeTitle);
            animeType = itemView.findViewById(R.id.animeType);
            favoriteButton = itemView.findViewById(R.id.favourite);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}
