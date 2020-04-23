package com.letrix.animeapp.datamanager;

import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnimeFLV_Client {
    private static final String BASE_URL = "https://animeflv.chrismichael.now.sh/api/v1/";
    //private static final String BASE_URL = "http://aflv.jeluchu.xyz/api/v1/";
    //private static final String BASE_URL = "http://192.168.1.40:8080";
    private static AnimeFLV_Client INSTANCE;
    private AnimeFLV_Interface animeFLVInterface;

    private AnimeFLV_Client() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        animeFLVInterface = retrofit.create(AnimeFLV_Interface.class);
    }

    public static AnimeFLV_Client getINSTANCE() {
        if (null == INSTANCE) {
            INSTANCE = new AnimeFLV_Client();
        }
        return INSTANCE;
    }

    // Series

    Call<JSONResponse> getOngoingsList(int page) {
        return animeFLVInterface.requestSeriesList("rating", "1", page);
    }

    Call<JSONResponse> getFinishedList(int page) {
        return animeFLVInterface.requestSeriesList("rating", "2", page);
    }

    Call<JSONResponse> getTVList(int page) {
        return animeFLVInterface.requestRecentList("default", page);
    }

    Call<JSONResponse> getSpecialsList(int page) {
        return animeFLVInterface.requestSpecialsList("default", page);
    }

    Call<JSONResponse> getMovieList(int page) {
        return animeFLVInterface.requestMovieList("default", page);
    }

    Call<JSONResponse> getOvaList(int page) {
        return animeFLVInterface.requestOvaList("default", page);
    }

    Call<JSONResponse> getServerList(String id, String title) {
        return animeFLVInterface.requestServers(id, title);
    }

    Call<JSONResponse> getSearchList(String searchTerm) {
        return animeFLVInterface.requestSearchList(searchTerm);
    }

    Call<JSONResponse> getGenreList(String genre, String sortOrder, int page) {
        return animeFLVInterface.requestGenreList(genre, sortOrder, page);
    }
}

