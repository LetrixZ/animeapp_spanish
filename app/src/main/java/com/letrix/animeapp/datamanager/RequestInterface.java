package com.letrix.animeapp.datamanager;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RequestInterface {

    // Series (Ongoings & Finished)
    @GET("AnimeByState/{status}/{sorting}/{page}")
    Call<JSONResponse> requestSeriesList(@Path("sorting") String sortingOrder, @Path("status") String status, @Path("page") int page);

    // TV
    @GET("TV/{sorting}/1")
    Call<JSONResponse> requestRecentList(@Path("sorting") String sortingOrder);

    // Películas
    @GET("Movies/{sorting}/1")
    Call<JSONResponse> requestMovieList(@Path("sorting") String sortingOrder);

    // OVAs
    @GET("Ova/{sorting}/1")
    Call<JSONResponse> requestOvaList(@Path("sorting") String sortingOrder);

    // Get Server
    @GET("GetAnimeServers/{number_id}/{title}")
    Call<JSONResponse> requestServers(@Path("number_id") String numberId, @Path("title") String title);

    // Search
    @GET("Search/{searchTerm}")
    Call<JSONResponse> requestSearchList(@Path("searchTerm") String searchTerm);

}