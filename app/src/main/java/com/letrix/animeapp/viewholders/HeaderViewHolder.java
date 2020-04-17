package com.letrix.animeapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView animeType;
    private View rootView;

    public HeaderViewHolder(@NonNull final View itemView) {
        super(itemView);
        rootView = itemView;
        animeType = itemView.findViewById(R.id.animeType);
    }
}
