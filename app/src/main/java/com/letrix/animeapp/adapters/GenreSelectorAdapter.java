package com.letrix.animeapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.fragments.GenreSelectorFragment;

import java.util.List;

public class GenreSelectorAdapter extends RecyclerView.Adapter<GenreSelectorAdapter.ViewHolder> {

    private List<String> genreList;
    private View view;
    private GenreSelectorFragment fragment;

    public GenreSelectorAdapter(List<String> genreList, GenreSelectorFragment fragment) {
        this.genreList = genreList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public GenreSelectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_genre_item, parent, false);
        return new ViewHolder(view);
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

        holder.genreText.setOnClickListener(v -> {
            fragment.searchGenre(genreList.get(position), v.findViewById(R.id.genreText), null);
        });
        // END
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView genreText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            genreText = itemView.findViewById(R.id.genreText);

        }

    }
}
