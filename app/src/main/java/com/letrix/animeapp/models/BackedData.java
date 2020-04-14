package com.letrix.animeapp.models;

import java.util.ArrayList;

public class BackedData {

    private String pageName;
    private ArrayList<AnimeModel> animeList;
    private int pageNumber;

    public BackedData(String pageName, ArrayList<AnimeModel> animeList, int pageNumber) {
        this.pageName = pageName;
        this.animeList = animeList;
        this.pageNumber = pageNumber;
    }

    public String getPageName() {
        return pageName;
    }

    public ArrayList<AnimeModel> getAnimeList() {
        return animeList;
    }

    public int getPageNumber() {
        return pageNumber;
    }

}

