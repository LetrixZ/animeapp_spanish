package com.letrix.animeapp.datamanager;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Client {
    private static final String BASE_URL = "https://animeflv.chrismichael.now.sh/api/v1/";
    private static Client INSTANCE;
    private RequestInterface requestInterface;

    private Client() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestInterface = retrofit.create(RequestInterface.class);
    }

    static Client getINSTANCE() {
        if (null == INSTANCE) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    // Series

    Call<JSONResponse> getOngoingsList() {
        return requestInterface.requestSeriesList("default", "1");
    }

    Call<JSONResponse> getFinishedList() {
        return requestInterface.requestSeriesList("default", "2");
    }

    // Recientes
    Call<JSONResponse> getTVList() {
        return requestInterface.requestRecentList("default");
    }

    Call<JSONResponse> getMovieList() {
        return requestInterface.requestMovieList("default");
    }

    Call<JSONResponse> getOvaList() {
        return requestInterface.requestOvaList("default");
    }

    Call<JSONResponse> getServerList(String id, String title) {
        return requestInterface.requestServers(id, title);
    }

    Call<JSONResponse> getSearchList(String searchTerm) {
        return requestInterface.requestSearchList(searchTerm);
    }
}

