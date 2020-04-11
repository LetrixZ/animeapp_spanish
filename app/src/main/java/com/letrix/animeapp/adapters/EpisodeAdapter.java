package com.letrix.animeapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.models.AnimeModel;

import java.text.DecimalFormat;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private AnimeModel anime;
    private View view;
    private OnItemClickListener mOnItemClickListener;

    public EpisodeAdapter(AnimeModel anime, OnItemClickListener onItemClickListener) {
        this.anime = anime;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public EpisodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_episode_item, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(EpisodeAdapter.ViewHolder holder, int position) {
        final ViewHolder viewHolder = holder;
        DecimalFormat format = new DecimalFormat("0.#");
        viewHolder.episodeNumber.setText("EP " + format.format(anime.getEpisodes().get(position + 1).getEpisode()));
    }

    @Override
    public int getItemCount() {
        return anime.getEpisodes().size() - 1;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView episodeNumber;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            episodeNumber = itemView.findViewById(R.id.episodeNumber);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
