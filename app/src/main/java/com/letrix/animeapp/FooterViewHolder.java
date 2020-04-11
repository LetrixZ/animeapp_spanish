package com.letrix.animeapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class FooterViewHolder extends RecyclerView.ViewHolder {

    final View rootView;
    public TextView loadMore;

    public FooterViewHolder(@NonNull View view) {
        super(view);
        loadMore = view.findViewById(R.id.loadMore);
        rootView = view;
    }
}
