package com.letrix.animeapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.Common;

import java.util.ArrayList;

class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<AnimeModel> animeList;
    private GridLayoutManager gridLayoutManager;

    public RecentAdapter(Context context, ArrayList<AnimeModel> animeList, GridLayoutManager gridLayoutManager) {
        this.context = context;
        this.animeList = animeList;
        this.gridLayoutManager = gridLayoutManager;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getItemViewType(position) == Common.VIEWTYPE_HEADER ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == Common.VIEWTYPE_HEADER) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.recycler_anime_mini_header, parent, false);
            HeaderViewHolder headerViewHolder = new HeaderViewHolder(group);
            return headerViewHolder;
        } else if (viewType == Common.VIEWTYPE_ANIME) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.recycler_anime_home_item, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(group);
            return itemViewHolder;
        } else {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.recycler_anime_mini_header, parent, false);
            HeaderViewHolder headerViewHolder = new HeaderViewHolder(group);
            return headerViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
        if (position == 0 || position == 25 || position == 48) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            switch (position) {
                case 0:
                    headerViewHolder.sectionName.setText("Series");
                    break;
                case 25:
                    headerViewHolder.sectionName.setText("Películas");
                    break;
                case 48:
                    headerViewHolder.sectionName.setText("OVAs");
                    break;
            }
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            AnimeModel currentAnime = animeList.get(position - 1);
            itemViewHolder.animeTitle.setText(currentAnime.getTitle());
            if (currentAnime.getEpisodes() != null) {
                itemViewHolder.lastEpisode.setText("Episodio " + (currentAnime.getEpisodes().size() - 1));
            } else {
                itemViewHolder.lastEpisode.setText("Sin episodios");
            }
            byte[] decodedString = Base64.decode(currentAnime.getPoster(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            itemViewHolder.animeImage.setImageBitmap(decodedByte);
        }
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionName;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.animeType);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView animeTitle, lastEpisode;
        ImageView animeImage;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            animeTitle = itemView.findViewById(R.id.animeTitle);
            lastEpisode = itemView.findViewById(R.id.lastEpisode);
            animeImage = itemView.findViewById(R.id.animeImage);
        }
    }
}
