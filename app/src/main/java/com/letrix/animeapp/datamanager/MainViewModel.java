package com.letrix.animeapp.datamanager;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.AnimeVideo;
import com.letrix.animeapp.models.BackedData;
import com.letrix.animeapp.models.ServerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private MutableLiveData<ArrayList<AnimeModel>> tvList, ovaList, movieList;

    private Boolean enableFLV = true;

    private MutableLiveData<ArrayList<AnimeModel>> searchList, genreList;
    private MutableLiveData<ArrayList<ServerModel>> serverList = new MutableLiveData<>();
    private MutableLiveData<AnimeModel> selectedAnime = new MutableLiveData<>();

    private ArrayList<BackedData> backedData = new ArrayList<>(Collections.nCopies(10, new BackedData(null, null, 0)));
    private MutableLiveData<List<BackedData>> backedDataLiveData = new MutableLiveData<>();

    private ArrayList<AnimeModel> favouriteList = new ArrayList<>();
    private MutableLiveData<List<AnimeModel>> favouriteLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AnimeModel>> ongoingsList, finishedList, seriesList, moviesList, ovasList, specialsList;

    public void addBackedData(BackedData backedData, int TYPE) {
        Log.d(TAG, "addBackedData: RECEIVED: " + backedData.getAnimeList().size() + " " + TYPE );
        this.backedData.set(TYPE, backedData);
        backedDataLiveData.setValue(this.backedData);
    }

    public Boolean getEnableFLV() {
        return enableFLV;
    }

    public void setEnableFLV(boolean value) {
        enableFLV = value;
    }

    public BackedData getBackedData(int TYPE) {
        return backedData.get(TYPE);
    }

    public void addFavourite(AnimeModel anime) {
        this.favouriteList.add(anime);
        favouriteLiveData.setValue(this.favouriteList);
    }

    public void removeFavourite(AnimeModel anime) {
        for (int i = 0; i < favouriteList.size(); i++) {
            if (favouriteList.get(i).getTitle().equals(anime.getTitle())) {
                favouriteList.remove(i);
                Log.d(TAG, "removeFavourite: " + i);
            }
        }
        Log.d(TAG, "removeFavourite: " + anime.getTitle());
        favouriteLiveData.setValue(this.favouriteList);
    }

    public void restoreFavourite(ArrayList<AnimeModel> animeList) {
        this.favouriteList = animeList;
        favouriteLiveData.setValue(this.favouriteList);
    }

    // JKAnime

    private MutableLiveData<String> animeVideo;

    public LiveData<String> getAnimeVideo(String name, float number) {
        animeVideo = new MutableLiveData<>();
        requestAnimeVideo(name, number);
        return animeVideo;
    }

    private void requestAnimeVideo(String name, float number) {
        Log.d(TAG, "requestAnimeVideo: Anime Video REQUESTED");
        JKAnime_Client.getINSTANCE().getAnimeVideo(name, number).enqueue(new CallbackWithRetry<AnimeVideo>() {
            @Override
            public void onResponse(Call<AnimeVideo> call, Response<AnimeVideo> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / Anime Video");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / Anime Video");
                    animeVideo.setValue(response.body().getVideo());
                }
            }

            @Override
            public void onFailure(Call<AnimeVideo> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / Anime Video, " + t);
            }
        });
    }

    ////////////////
    //// EXTRAS ////
    ////////////////

    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<Integer> episodePosition = new MutableLiveData<>();
    private MutableLiveData<String> image = new MutableLiveData<>();
    private MutableLiveData<Integer> code = new MutableLiveData<>();

    public MutableLiveData<String> getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image.setValue(image);
    }

    public MutableLiveData<String> getUrl() {
        return url;
    }

    // Watched Time
    private HashMap<String, Long> watchedEpisodesMap = new HashMap<>();

    public MutableLiveData<Integer> getEpisodePosition() {
        return episodePosition;
    }

    public void addWatchedEpisode(int position, long time) {
            watchedEpisodesMap.put(selectedAnime.getValue().getEpisodes().get(position).getId(), time);
    }

    public HashMap<String, Long> getWatchedEpisodesMap() {
        return watchedEpisodesMap;
    }

    public void restoreWatched(HashMap<String, Long> watchedList) {
        this.watchedEpisodesMap = watchedList;
    }

    public void restoreFLV(Boolean enableFLV) {
        this.enableFLV = enableFLV;
    }

    public MutableLiveData<List<AnimeModel>> getFavouriteList() {
        return favouriteLiveData;
    }

    ///////////////////
    //// FRAGMENTS ////
    ///////////////////

    public MutableLiveData<Integer> getCode() {
        return code;
    }

    public LiveData<ArrayList<AnimeModel>> getOngoings(int page) {
        ongoingsList = new MutableLiveData<>();
        requestOngoings(page);
        return ongoingsList;
    }

    public LiveData<ArrayList<AnimeModel>> getFinished(int page) {
        finishedList = new MutableLiveData<>();
        requestFinished(page);
        return finishedList;
    }

    private void requestOngoings(int page) {
        Log.d(TAG, "ONGOINGS REQUESTED");
        AnimeFLV_Client.getINSTANCE().getOngoingsList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / ONGOINGS");
                code.setValue(response.code());
                if (response.body() != null && response.code() == 200) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / ONGOINGS");
                    ongoingsList.setValue(response.body().getAnimes());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / ONGOINGS, " + t);
            }
        });

    }

    public LiveData<ArrayList<AnimeModel>> getMovies(int page) {
        moviesList = new MutableLiveData<>();
        requestMovies(page);
        return moviesList;
    }

    private void requestFinished(int page) {
        Log.d(TAG, "FINISHED REQUESTED");
        AnimeFLV_Client.getINSTANCE().getFinishedList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / FINISHED");
                code.setValue(response.code());
                if (response.body() != null && response.code() == 200) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / FINISHED");
                    finishedList.setValue(response.body().getAnimes());
                } else {

                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / FINISHED, " + t);
            }
        });
    }

    public LiveData<ArrayList<AnimeModel>> getSeries(int page) {
        seriesList = new MutableLiveData<>();
        requestSeries(page);
        return seriesList;
    }

    private void requestMovies(int page) {
        Log.d(TAG, "MOVIES REQUESTED");
        AnimeFLV_Client.getINSTANCE().getMovieList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / MOVIES");
                code.setValue(response.code());
                if (response.body() != null && response.code() == 200) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / MOVIES");
                    moviesList.setValue(response.body().getMovies());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / MOVIES, " + t);
            }
        });
    }

    public LiveData<ArrayList<AnimeModel>> getOvas(int page) {
        ovasList = new MutableLiveData<>();
        requestOvas(page);
        return ovasList;
    }

    private void requestSeries(int page) {
        Log.d(TAG, "SERIES REQUESTED");
        AnimeFLV_Client.getINSTANCE().getTVList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / SERIES");
                code.setValue(response.code());
                if (response.body() != null && response.code() == 200) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / SERIES");
                    seriesList.setValue(response.body().getTv());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / SERIES, " + t);
            }
        });
    }

    public LiveData<ArrayList<AnimeModel>> getSpecials(int page) {
        specialsList = new MutableLiveData<>();
        requestSpecials(page);
        return specialsList;
    }

    private void requestOvas(int page) {
        Log.d(TAG, "OVAS REQUESTED");
        AnimeFLV_Client.getINSTANCE().getOvaList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / OVAS");
                code.setValue(response.code());
                if (response.body() != null && response.code() == 200) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / OVAS");
                    ovasList.setValue(response.body().getOva());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / OVAS, " + t);
            }
        });
    }

    ////////////////
    //// RECENT ////
    ////////////////

    // TV

    public LiveData<ArrayList<AnimeModel>> getTVList(int page) {
        if (tvList == null) {
            tvList = new MutableLiveData<>();
            requestTVList(page);
        }
        if (page > 1 && tvList.getValue().size() == 24 * (page - 1)) {
            requestTVList(page);
        }
        return tvList;
    }

    private void requestSpecials(int page) {
        Log.d(TAG, "SPECIALS REQUESTED");
        AnimeFLV_Client.getINSTANCE().getSpecialsList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / SPECIALS");
                code.setValue(response.code());
                if (response.body() != null && response.code() == 200) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / SPECIALS");
                    specialsList.setValue(response.body().getSpecial());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / SPECIALS, " + t);
            }
        });
    }

    // Movies

    public LiveData<ArrayList<AnimeModel>> getMovieList(int page) {
        if (movieList == null) {
            movieList = new MutableLiveData<>();
            requestMovieList(page);
        }
        if (page > 1 && movieList.getValue().size() == 24 * (page - 1)) {
            requestMovieList(page);
        }
        return movieList;
    }

    private void requestTVList(int page) {
        Log.d(TAG, "requestTVList: TV REQUESTED");
        AnimeFLV_Client.getINSTANCE().getTVList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / TV");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / TV");
                    if (tvList != null && page > 1) {
                        tvList.getValue().addAll(response.body().getTv());
                        Log.d(TAG, "onResponse: PAGE 2");
                    } else {
                        tvList.setValue(response.body().getTv());
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / TV, " + t);
            }
        });
    }

    // OVAs

    public LiveData<ArrayList<AnimeModel>> getOvaList(int page) {
        if (ovaList == null) {
            ovaList = new MutableLiveData<>();
            requestOVAList(page);
        }
        if (page > 1 && ovaList.getValue().size() == 24 * (page - 1)) {
            requestOVAList(page);
        }
        return ovaList;
    }

    private void requestMovieList(int page) {
        Log.d(TAG, "requestFinishedList: MOVIE REQUESTED");
        AnimeFLV_Client.getINSTANCE().getMovieList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / MOVIE");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / MOVIE");
                    if (movieList != null && page > 1) {
                        movieList.getValue().addAll(response.body().getMovies());
                        Log.d(TAG, "onResponse: PAGE 2");
                    } else {
                        movieList.setValue(response.body().getMovies());
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / MOVIE, " + t);
            }
        });
    }

    // Extras

    public LiveData<ArrayList<ServerModel>> getServerList(String id, String title) {
        serverList = new MutableLiveData<>();
        requestServerList(id, title);
        return serverList;
    }

    public MutableLiveData<ArrayList<AnimeModel>> getSearchList(String searchTerm) {
        searchList = new MutableLiveData<>();
        requestSearchList(searchTerm);
        return searchList;
    }

    public MutableLiveData<AnimeModel> getSelectedAnime() {
        return selectedAnime;
    }

    public void setSelectedAnime(AnimeModel selectedAnime) {
        this.selectedAnime.setValue(selectedAnime);
    }

    private void requestOVAList(int page) {
        Log.d(TAG, "requestFinishedList: OVA REQUESTED");
        AnimeFLV_Client.getINSTANCE().getOvaList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / OVA");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / OVA");
                    if (ovaList != null && page > 1) {
                        ovaList.getValue().addAll(response.body().getOva());
                        Log.d(TAG, "onResponse: PAGE 2");
                    } else {
                        ovaList.setValue(response.body().getOva());
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / OVA, " + t);
            }
        });
    }

    private void requestServerList(String id, String title) {
        AnimeFLV_Client.getINSTANCE().getServerList(id, title).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / SERVERS");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / SERVERS");
                    serverList.setValue(response.body().getServers());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / SERVERS, " + t);
            }
        });
    }

    public MutableLiveData<ArrayList<AnimeModel>> getGenreList(String genre, String sortOrder, int page) {
        genreList = new MutableLiveData<>();
        requestGenreList(genre, sortOrder, page);
        return genreList;
    }

    private void requestSearchList(String searchTerm) {
        AnimeFLV_Client.getINSTANCE().getSearchList(searchTerm).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / SEARCH");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / SEARCH");
                    searchList.setValue(response.body().getSearch());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / SEARCH, " + t);
            }
        });
    }

    public void setUrl(String url, int position) {
        this.episodePosition.setValue(position);
        this.url.setValue(url);
    }

    private void requestGenreList(String genre, String sortOrder, int page) {
        AnimeFLV_Client.getINSTANCE().getGenreList(genre, sortOrder, page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / SEARCH");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / SEARCH");
                    genreList.setValue(response.body().getAnimes());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / SEARCH, " + t);
            }
        });
    }


}
