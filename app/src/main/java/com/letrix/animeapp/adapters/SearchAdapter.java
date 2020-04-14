package com.letrix.animeapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.MainActivity;
import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.fragments.SearchFragment;
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
    private Boolean isFavorite = false;

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
            viewHolder.episodeNumber.setText("Episodio " + format.format(anime.get(position).getEpisodes().get(1).getEpisode()));
        } else {
            viewHolder.episodeNumber.setText("Sin episodios");
        }
        byte[] decodedString = Base64.decode(anime.get(position).getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        viewHolder.animeImage.setImageBitmap(decodedByte);

        /*for (AnimeModel favorite : favoriteList) {
            if (anime.get(position).getTitle().equals(favorite.getTitle())) {
                Log.d(TAG, "onBindViewHolder: MATCH");
                viewHolder.favoriteButton.setImageResource(R.drawable.ic_favorite);
            }
        }
        if(clickedTextViewPos==position){
            if (isFavorite) {
                viewHolder.favoriteButton.setImageResource(R.drawable.ic_unfavorite);
                isFavorite = false;
            }
            else {
                isFavorite = true;
                viewHolder.favoriteButton.setImageResource(R.drawable.ic_favorite);
            }
        }*/

    }

    @Override
    public int getItemCount() {
        return anime.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        //void onFavoriteClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnItemClickListener onItemClickListener;
        private TextView episodeNumber, animeTitle;
        private ImageView animeImage;
        AppCompatImageView favoriteButton;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            episodeNumber = itemView.findViewById(R.id.episodeNumber);
            animeImage = itemView.findViewById(R.id.animeImage);
            animeTitle = itemView.findViewById(R.id.animeTitle);
            favoriteButton = itemView.findViewById(R.id.favourite);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);

            /*favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedTextViewPos = getAdapterPosition();
                    int position = getAdapterPosition();
                    mOnItemClickListener.onFavoriteClick(position);
                    notifyDataSetChanged();
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}
