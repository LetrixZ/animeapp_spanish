package com.letrix.animeapp.datamanager;

import com.google.gson.GsonBuilder;
import com.letrix.animeapp.models.AnimeVideo;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JKAnime_Client {
    private static final String BASE_URL = "https://ryuanime-api.chrismichael.now.sh/api/v1/";
    private static JKAnime_Client INSTANCE;
    private JKAnime_Interface jkAnime_interface;

    private JKAnime_Client() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        jkAnime_interface = retrofit.create(JKAnime_Interface.class);
    }

    public static JKAnime_Client getINSTANCE() {
        if (null == INSTANCE) {
            INSTANCE = new JKAnime_Client();
        }
        return INSTANCE;
    }

    Call<AnimeVideo> getAnimeVideo(String animeName, float episodeNumber) {
        return jkAnime_interface.requestAnimeVideo(animeName, episodeNumber);
    }
}

