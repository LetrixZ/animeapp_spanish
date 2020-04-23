package com.letrix.animeapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.fragments.RecentFragment;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.EpisodeTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class WatchedAdapter extends RecyclerView.Adapter<WatchedAdapter.ViewHolder> {

    private ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> animeList;
    private RecentFragment fragment;

    public WatchedAdapter(ArrayList<Map.Entry<AnimeModel, ArrayList<EpisodeTime>>> animeList, RecentFragment fragment) {
        this.animeList = animeList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public WatchedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_anime_watched_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WatchedAdapter.ViewHolder holder, int position) {
        AnimeModel currentItem = animeList.get(position).getKey();
        ArrayList<EpisodeTime> currentEpisode = animeList.get(position).getValue();
        holder.animeTitle.setText(currentItem.getTitle());
        byte[] decodedString = Base64.decode(currentItem.getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.animeImage.setImageBitmap(decodedByte);
        //Timber.d("Anime title: %s", currentItem.getTitle());
        int size = currentItem.getEpisodes().size();
        // Checking the latest watched episode
        if (currentEpisode.get(currentEpisode.size() - 1).getProgressPosition() < 90) {
            holder.lastEpisode.setText(String.format(holder.itemView.getResources().getString(R.string.episode), String.valueOf(currentEpisode.get(currentEpisode.size() - 1).getEpisodeNumber())));
            holder.progressBar.setProgress((int) currentEpisode.get(currentEpisode.size() - 1).getProgressPosition());
            if (currentEpisode.get(currentEpisode.size() - 1).getEpisodePosition() == 0 && currentEpisode.get(currentEpisode.size() - 1).getEpisodeDuration() == 1) {
                holder.lastEpisode.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
            }
        } else if (currentEpisode.get(0).getProgressPosition() < 90) {
            Iterator<EpisodeTime> iterator = animeList.get(position).getValue().iterator();
            while (iterator.hasNext()) {
                EpisodeTime episodeTime = iterator.next();
                if (episodeTime.getProgressPosition() > 90) {
                    iterator.remove();
                    //Timber.d(String.valueOf(episodeTime.getEpisodeNumber()));
                } else {
                    holder.lastEpisode.setText(String.format(holder.itemView.getResources().getString(R.string.episode), String.valueOf(episodeTime.getEpisodeNumber())));
                    holder.progressBar.setProgress((int) episodeTime.getProgressPosition());
                }
            }
        } else if (currentItem.getEpisodes().get(size - currentEpisode.size() - 1).getId() == null) {
            holder.lastEpisode.setText(R.string.completed);
            holder.progressBar.setProgress(0);
        }
        else if (currentEpisode.size() != currentItem.getEpisodes().size() - 1){
            holder.lastEpisode.setText(String.format(holder.itemView.getResources().getString(R.string.episode), String.valueOf((int) currentItem.getEpisodes().get(currentItem.getEpisodes().size() - currentEpisode.size() - 1).getEpisode())));
            holder.progressBar.setProgress(0);
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
            holder.lastEpisode.setVisibility(View.VISIBLE);
            holder.lastEpisode.setText(R.string.completed);
            holder.progressBar.setVisibility(View.GONE);
        }

        String animeId = currentItem.getTitle().replaceAll("\\s", "-");
        animeId = animeId.replaceAll("[.|:|!|/|(|)|']", "");
        animeId = animeId.toLowerCase();

        holder.animeImage.setTransitionName("image-" + animeId + "-horizontal");
        holder.animeTitle.setTransitionName("title-" + animeId + "-horizontal");

        String finalAnimeId = animeId;
        holder.itemView.setOnClickListener(v -> {
            fragment.animeInfo(currentItem, finalAnimeId + "-horizontal", v.findViewById(R.id.animeImage), v.findViewById(R.id.animeTitle));
        });
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView animeTitle, lastEpisode;
        ImageView animeImage;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            animeImage = itemView.findViewById(R.id.animeImage);
            animeTitle = itemView.findViewById(R.id.animeTitle);
            lastEpisode = itemView.findViewById(R.id.lastEpisode);
            progressBar = itemView.findViewById(R.id.watchedBar);
        }
    }
}
