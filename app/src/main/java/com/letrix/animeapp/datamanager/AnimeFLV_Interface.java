package com.letrix.animeapp.datamanager;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface AnimeFLV_Interface {

    // Series (Ongoings & Finished)
    @GET("AnimeByState/{status}/{sorting}/{page}")
    Call<JSONResponse> requestSeriesList(@Path("sorting") String sortingOrder, @Path("status") String status, @Path("page") int page);

    // TV
    @GET("TV/{sorting}/{page}")
    Call<JSONResponse> requestRecentList(@Path("sorting") String sortingOrder, @Path("page") int page);

    // Specials
    @GET("Special/{sorting}/{page}")
    Call<JSONResponse> requestSpecialsList(@Path("sorting") String sortingOrder, @Path("page") int page);

    // Movies
    @GET("Movies/{sorting}/{page}")
    Call<JSONResponse> requestMovieList(@Path("sorting") String sortingOrder, @Path("page") int page);

    // OVAs
    @GET("Ova/{sorting}/{page}")
    Call<JSONResponse> requestOvaList(@Path("sorting") String sortingOrder, @Path("page") int page);

    // Get Server
    @GET("GetAnimeServers/{number_id}/{title}")
    Call<JSONResponse> requestServers(@Path("number_id") String numberId, @Path("title") String title);

    // Search
    @GET("Search/{searchTerm}")
    Call<JSONResponse> requestSearchList(@Path("searchTerm") String searchTerm);

    // Genre
    @GET("Genres/{genre}/{sortOrder}/{page}")
    Call<JSONResponse> requestGenreList(@Path("genre") String genre, @Path("sortOrder") String sortOrder, @Path("page") int page);
}