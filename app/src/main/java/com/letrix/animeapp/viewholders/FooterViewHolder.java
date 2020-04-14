package com.letrix.animeapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;

public class FooterViewHolder extends RecyclerView.ViewHolder {

    public final View rootView;
    public TextView loadMore;

    public FooterViewHolder(@NonNull View view) {
        super(view);
        loadMore = view.findViewById(R.id.loadMore);
        rootView = view;
    }
}
