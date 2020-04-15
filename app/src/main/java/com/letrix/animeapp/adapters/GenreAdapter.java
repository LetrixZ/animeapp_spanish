package com.letrix.animeapp.adapters;

import android.content.Context;
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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.utils.DiffUtilCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_PROGRESS = 1;
    private static final int VIEWTYPE_ANIMELIST = 2;
    public Boolean isLoading = false;
    private ArrayList<AnimeModel> dataSource = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private Context context;
    private int pageNumber;
    private GenreAdapter.OnItemClickListener mOnItemClickListener;

    public GenreAdapter(Context context, GridLayoutManager gridLayoutManager, RecyclerView recyclerView, int pageNumber, GenreAdapter.OnItemClickListener onItemClickListener) {
        this.pageNumber = pageNumber;
        this.context = context;
        this.gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        this.recyclerView = recyclerView;
        this.mOnItemClickListener = onItemClickListener;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getItemViewType(position) == VIEWTYPE_PROGRESS ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    public void insertData(ArrayList<AnimeModel> insertList) {
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(dataSource, insertList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        dataSource.addAll(insertList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void updateData(ArrayList<AnimeModel> newList) {
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(dataSource, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        dataSource.clear();
        dataSource.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_ANIMELIST) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_genrelist_item, parent, false);
            return new GenreAdapter.ItemViewHolder(v, mOnItemClickListener);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            return new GenreAdapter.ProgressHolder(v);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GenreAdapter.ItemViewHolder) {
            AnimeModel currentItem = dataSource.get(position);
            GenreAdapter.ItemViewHolder itemViewHolder = (GenreAdapter.ItemViewHolder) holder;
            itemViewHolder.animeTitle.setText(currentItem.getTitle());
            itemViewHolder.animeType.setText(currentItem.getType());
            byte[] decodedString = Base64.decode(currentItem.getPoster(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            itemViewHolder.animeImage.setImageBitmap(decodedByte);
        } else {
            if (isLoading) {
                ((GenreAdapter.ProgressHolder) holder).progressBar.setVisibility(View.VISIBLE);
            } else {
                ((GenreAdapter.ProgressHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dataSource.size()) {
            return VIEWTYPE_PROGRESS;
        } else {
            return VIEWTYPE_ANIMELIST;
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size() + 1;
    }

    public void showProgressBar() {
        isLoading = true;
    }

    public void hideProgressBar() {
        isLoading = false;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ProgressHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView animeImage;
        TextView animeTitle, animeType;
        GenreAdapter.OnItemClickListener onItemClickListener;

        ItemViewHolder(@NonNull View itemView, GenreAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);
            animeImage = itemView.findViewById(R.id.animeImage);
            animeTitle = itemView.findViewById(R.id.animeTitle);
            animeType = itemView.findViewById(R.id.animeType);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}