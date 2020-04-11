package com.letrix.animeapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HeaderViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;

    HeaderViewHolder(@NonNull final View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.viewType_text);
    }
}
