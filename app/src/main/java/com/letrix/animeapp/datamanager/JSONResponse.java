package com.letrix.animeapp.datamanager;

import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.ServerModel;

import java.util.ArrayList;

class JSONResponse {
    private ArrayList<AnimeModel> animes;
    private ArrayList<AnimeModel> tv;
    private ArrayList<AnimeModel> ova;
    private ArrayList<AnimeModel> movies;
    private ArrayList<ServerModel> servers;
    private ArrayList<AnimeModel> search;
    private ArrayList<AnimeModel> special;

    ArrayList<AnimeModel> getSpecial() {
        return special;
    }

    ArrayList<AnimeModel> getSearch() {
        return search;
    }

    ArrayList<AnimeModel> getAnimes() {
        return animes;
    }

    ArrayList<AnimeModel> getTv() {
        return tv;
    }

    ArrayList<AnimeModel> getOva() {
        return ova;
    }

    ArrayList<AnimeModel> getMovies() {
        return movies;
    }

    ArrayList<ServerModel> getServers() {
        return servers;
    }

}
