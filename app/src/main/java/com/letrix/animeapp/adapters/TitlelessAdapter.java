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
import com.letrix.animeapp.models.AnimeModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TitlelessAdapter extends RecyclerView.Adapter<TitlelessAdapter.ViewHolder> {

    private ArrayList<AnimeModel> animeList;
    private View view;
    private EpisodeAdapter.OnItemClickListener mOnItemClickListener;

    public TitlelessAdapter(ArrayList<AnimeModel> animeList, EpisodeAdapter.OnItemClickListener onItemClickListener) {
        this.animeList = animeList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TitlelessAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_anime_home_item, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(TitlelessAdapter.ViewHolder holder, int position) {
        final TitlelessAdapter.ViewHolder viewHolder = holder;
        viewHolder.animeTitle.setText(animeList.get(position).getTitle());
        DecimalFormat format = new DecimalFormat("0.#");
        viewHolder.episodeNumber.setText("Episode " + format.format(animeList.get(position).getEpisodes().get(1).getEpisode()));
        byte[] decodedString = Base64.decode(animeList.get(position).getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        viewHolder.animeImage.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView episodeNumber, animeTitle;
        ImageView animeImage;
        EpisodeAdapter.OnItemClickListener onItemClickListener;

        ViewHolder(@NonNull View itemView, EpisodeAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);
            animeTitle = itemView.findViewById(R.id.animeTitleHome);
            animeImage = itemView.findViewById(R.id.animeImageHome);
            episodeNumber = itemView.findViewById(R.id.lastEpisodeHome);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
