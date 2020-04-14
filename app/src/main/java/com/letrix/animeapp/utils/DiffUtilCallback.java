package com.letrix.animeapp.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.letrix.animeapp.models.AnimeModel;

import java.util.ArrayList;

public class DiffUtilCallback extends DiffUtil.Callback {

    private ArrayList<AnimeModel> oldList;
    private ArrayList<AnimeModel> newList;

    public DiffUtilCallback(ArrayList<AnimeModel> oldList, ArrayList<AnimeModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
