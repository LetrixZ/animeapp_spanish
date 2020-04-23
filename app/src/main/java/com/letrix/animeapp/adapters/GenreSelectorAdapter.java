package com.letrix.animeapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.fragments.GenreSelectorFragment;

import java.util.List;

public class GenreSelectorAdapter extends RecyclerView.Adapter<GenreSelectorAdapter.ViewHolder> {

    private List<String> genreList;
    private View view;
    private OnItemClickListener mOnItemClickListener;
    private GenreSelectorFragment fragment;

    public GenreSelectorAdapter(List<String> genreList, OnItemClickListener mOnItemClickListener, GenreSelectorFragment fragment) {
        this.genreList = genreList;
        this.mOnItemClickListener = mOnItemClickListener;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public GenreSelectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_genre_item, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(GenreSelectorAdapter.ViewHolder holder, int position) {
        final ViewHolder viewHolder = holder;
        String currentGenre = genreList.get(position);
        currentGenre = currentGenre.replace("-", " ");
        currentGenre = currentGenre.substring(0, 1).toUpperCase() + currentGenre.substring(1).toLowerCase();
        holder.genreText.setText(currentGenre);

        // START
        holder.genreText.setTransitionName("genre_" + currentGenre);
        holder.cardView.setTransitionName("card_" + currentGenre);

        holder.cardView.setOnClickListener(v -> {
            fragment.searchGenre(genreList.get(position), v.findViewById(R.id.genreText), v.findViewById(R.id.cardView));
        });
        // END
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public interface OnItemClickListener {
        void onItemClickGenre(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView genreText;
        OnItemClickListener onItemClickListener;
        CardView cardView;

        ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            genreText = itemView.findViewById(R.id.genreText);
            this.onItemClickListener = onItemClickListener;
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClickGenre(getAdapterPosition());
        }
    }
}
