package com.letrix.animeapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.models.AnimeModel;

import java.text.DecimalFormat;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private AnimeModel anime;
    private View view;
    private OnItemClickListener mOnItemClickListener;
    private TextView noEpisodesText;
    private List<String> episodeId;
    private Context context;

    public EpisodeAdapter(AnimeModel anime, OnItemClickListener onItemClickListener, TextView noEpisodesText, List<String> episodeId, Context context) {
        this.anime = anime;
        this.mOnItemClickListener = onItemClickListener;
        this.noEpisodesText = noEpisodesText;
        this.episodeId = episodeId;
        this.context = context;
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
        if (episodeId != null) {
            for (String episode : episodeId) {
                Log.d("EpisodeAdapter", "onBindViewHolder: TEST");
                if (anime.getEpisodes().get(position + 1).getId().equals(episode)) {
                    viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.episode_background_watched));
                }
            }
        }
        else {
            Log.d("EpisodeAdapter", "onBindViewHolder: NULL");
        }
    }

    @Override
    public int getItemCount() {
        if (anime.getEpisodes() != null) {
            noEpisodesText.setVisibility(View.GONE);
            return anime.getEpisodes().size() - 1;
        } else {
            noEpisodesText.setVisibility(View.VISIBLE);
            return 0;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView episodeNumber;
        CardView cardView;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            episodeNumber = itemView.findViewById(R.id.episodeNumber);
            cardView = itemView.findViewById(R.id.cardView);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
