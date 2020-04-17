package com.letrix.animeapp.datamanager;

import com.letrix.animeapp.models.AnimeVideo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface JKAnime_Interface {

    @GET("anime/{id}/{chapter}")
    Call<AnimeVideo> requestAnimeVideo(@Path("id") String animeName, @Path("chapter") float episodeNumber);

}
