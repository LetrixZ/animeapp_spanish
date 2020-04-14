package com.letrix.animeapp.datamanager;

import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static final String BASE_URL = "https://animeflv.chrismichael.now.sh/api/v1/";
    //private static final String BASE_URL = "http://192.168.1.40:8080/";
    private static Client INSTANCE;
    private RequestInterface requestInterface;


    private Client() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        requestInterface = retrofit.create(RequestInterface.class);
    }

    public static Client getINSTANCE() {
        if (null == INSTANCE) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    // Series

    Call<JSONResponse> getOngoingsList(int page) {
        return requestInterface.requestSeriesList("rating", "1", page);
    }

    Call<JSONResponse> getFinishedList(int page) {
        return requestInterface.requestSeriesList("rating", "2", page);
    }

    Call<JSONResponse> getTVList(int page) {
        return requestInterface.requestRecentList("default", page);
    }

    Call<JSONResponse> getSpecialsList(int page) {
        return requestInterface.requestSpecialsList("default", page);
    }

    Call<JSONResponse> getMovieList(int page) {
        return requestInterface.requestMovieList("default", page);
    }

    Call<JSONResponse> getOvaList(int page) {
        return requestInterface.requestOvaList("default", page);
    }

    Call<JSONResponse> getServerList(String id, String title) {
        return requestInterface.requestServers(id, title);
    }

    Call<JSONResponse> getSearchList(String searchTerm) {
        return requestInterface.requestSearchList(searchTerm);
    }
}

