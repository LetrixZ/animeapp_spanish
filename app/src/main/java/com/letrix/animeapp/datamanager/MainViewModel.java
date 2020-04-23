package com.letrix.animeapp.datamanager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.AnimeVideo;
import com.letrix.animeapp.models.BackedData;
import com.letrix.animeapp.models.EpisodeTime;
import com.letrix.animeapp.models.ServerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private float motionLayoutState = (float) 0;

    private MutableLiveData<ArrayList<AnimeModel>> ongoingsList, finishedList, seriesList, moviesList, ovasList, specialsList, searchList, genreList, tvList, ovaList, movieList;
    private MutableLiveData<ArrayList<ServerModel>> serverList = new MutableLiveData<>();
    private MutableLiveData<AnimeModel> selectedAnime = new MutableLiveData<>();

    private ArrayList<BackedData> backedData = new ArrayList<>(Collections.nCopies(10, new BackedData(null, null, 0)));
    private MutableLiveData<List<BackedData>> backedDataLiveData = new MutableLiveData<>();
    private int counter = 0;

    // Config data
    private Boolean enableFLV = true;
    private ArrayList<AnimeModel> favouriteList = new ArrayList<>();
    private MutableLiveData<List<AnimeModel>> favouriteLiveData = new MutableLiveData<>();

    // Anime Watch Time

    // Watched Time
    private HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> watchedAnimes = null;
    private HashMap<String, Long> watchedEpisodesMap = new HashMap<>();
    private MutableLiveData<String> animeVideo;
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<Integer> episodePosition = new MutableLiveData<>();
    private MutableLiveData<String> image = new MutableLiveData<>();
    private MutableLiveData<Integer> code = new MutableLiveData<>();

    public float getLayoutState() {
        return motionLayoutState;
    }

    public void setLayoutState(float state) {
        motionLayoutState = state;
    }

    public boolean checkWatched(AnimeModel selectedAnime) {
        return watchedAnimes.get(selectedAnime) != null;
    }

    public HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> getCurrentlyWatching() {
        return watchedAnimes;
    }

    public void clearFavorites() {
        favouriteList.clear();
    }

    public void clearCurrentlyWatching() {
        watchedAnimes.clear();
    }

    public void addCurrentlyWatching(AnimeModel anime, String episodeId, Integer episodeNumber, Long episodePosition, Long episodeDuration) {
        if (watchedAnimes == null) {
            watchedAnimes = new HashMap<>();
        }
        Log.d(TAG, "Recibido: " + anime.getTitle() + " Ep: " + episodeNumber + " time: " + episodePosition);
        // Checking if anime exists in HashMap
        Log.d(TAG, "Checking if anime exists in HashMap");
        if (watchedAnimes.get(anime) != null) {
            // Exists, so now checking if the episode exists in the inner Map
            Log.d(TAG, "Exists, so now checking if the episode exists in the inner Map");
            if (watchedAnimes.get(anime).get(episodeNumber) != null) {
                // Exists, so updating its time position
                Log.d(TAG, "Exists, so updating its time position");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    watchedAnimes.get(anime).replace(episodeNumber, new EpisodeTime(anime.getTitle(), episodeId, episodeNumber, episodePosition, episodeDuration));
                    Log.d(TAG, "Using replace() method");
                } else {
                    watchedAnimes.get(anime).put(episodeNumber, new EpisodeTime(anime.getTitle(), episodeId, episodeNumber, episodePosition, episodeDuration));
                    Log.d(TAG, "Using put() method");
                }
                Log.d(TAG, "Episode ID: " + episodeId + " episode duration: " + episodeDuration);
            } else {
                // It doesn't exists, so adding new episode
                Log.d(TAG, "It doesn't exists, so adding new episode");
                watchedAnimes.get(anime).put(episodeNumber, new EpisodeTime(anime.getTitle(), episodeId, episodeNumber, episodePosition, episodeDuration));
                Log.d(TAG, "Episode ID: " + episodeId + " episode duration: " + episodeDuration);
            }
        } else {
            // Doesn't exist in map, so adding new anime to Map
            Log.d(TAG, "Doesn't exist in map, so adding new anime to Map and adding watched episode");
            TreeMap<Integer, EpisodeTime> episode = new TreeMap<>();
            episode.put(episodeNumber, new EpisodeTime(anime.getTitle(), episodeId, episodeNumber, episodePosition, episodeDuration));
            Log.d(TAG, "Episode ID: " + episodeId + " episode duration: " + episodeDuration);
            watchedAnimes.put(anime, episode);
        }
    }

    public HashMap<String, Long> getWatchedEpisodesMap() {
        return watchedEpisodesMap;
    }

    public void restoreCurrentWatching(HashMap<String, Long> watchedList, HashMap<AnimeModel, TreeMap<Integer, EpisodeTime>> newWatchedList) {
        this.watchedAnimes = newWatchedList;
        this.watchedEpisodesMap = watchedList;
    }

    public void addBackedData(BackedData backedData, int TYPE) {
        Log.d(TAG, "addBackedData: RECEIVED: " + backedData.getAnimeList().size() + " " + TYPE);
        this.backedData.set(TYPE, backedData);
        backedDataLiveData.setValue(this.backedData);
    }

    public Boolean getEnableFLV() {
        return enableFLV;
    }

    // JKAnime

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

    // EXTRAS

    public void restoreFavourite(ArrayList<AnimeModel> animeList) {
        this.favouriteList = animeList;
        favouriteLiveData.setValue(this.favouriteList);
    }

    public LiveData<String> getAnimeVideo(String name, float number) {
        animeVideo = new MutableLiveData<>();
        requestAnimeVideo(name, number);
        return animeVideo;
    }

    public void addWatchedEpisode() {
    }

    private void requestAnimeVideo(String name, float number) {
        Log.d(TAG, "requestAnimeVideo: Anime Video REQUESTED");
        JKAnime_Client.getINSTANCE().getAnimeVideo(name, number).enqueue(new CallbackWithRetry<AnimeVideo>() {
            @Override
            public void onResponse(Call<AnimeVideo> call, Response<AnimeVideo> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / Anime Video");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / Anime Video");
                    if (response.body().getVideo() != null) {
                        animeVideo.setValue(response.body().getVideo());
                        counter = 0;
                    } else if (response.body().getVideo() == null && counter < 3) {
                        String tempName = name.replaceAll("-season|nd|st|rd|th", "");
                        requestAnimeVideo(tempName, number);
                        counter++;
                    } else {
                        animeVideo.setValue(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<AnimeVideo> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / Anime Video, " + t);
            }
        });
    }

    public MutableLiveData<String> getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image.setValue(image);
    }

    public MutableLiveData<String> getUrl() {
        return url;
    }

    public void restoreFLV(Boolean enableFLV) {
        this.enableFLV = enableFLV;
    }

    public MutableLiveData<List<AnimeModel>> getFavouriteList() {
        return favouriteLiveData;
    }

    // FRAGMENTS

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

    public LiveData<ArrayList<AnimeModel>> getSeries(int page) {
        seriesList = new MutableLiveData<>();
        requestSeries(page);
        return seriesList;
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
        if (specialsList == null) {
            specialsList = new MutableLiveData<>();
            requestSpecials(page);
        }
        return specialsList;
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


    ////////////////
    //// RECENT ////
    ////////////////

    // TV

    public LiveData<ArrayList<AnimeModel>> getTVList(int page) {
        if (tvList == null) {
            tvList = new MutableLiveData<>();
            requestTVList(page);
            return tvList;
        } else {
            return tvList;
        }
    }

    private void requestTVList(int page) {
        Log.d(TAG, "requestTVList: TV REQUESTED");
        AnimeFLV_Client.getINSTANCE().getTVList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / TV");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / TV");
                    if (response.body().getTv().size() >= 23) {
                        response.body().getTv().subList(11, 23).clear();
                    }
                    tvList.setValue(response.body().getTv());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / TV, " + t);
            }
        });
    }

    // Movies

    public LiveData<ArrayList<AnimeModel>> getMovieList(int page) {
        if (movieList == null) {
            movieList = new MutableLiveData<>();
            requestMovieList(page);
        }
        return movieList;
    }

    private void requestMovieList(int page) {
        Log.d(TAG, "requestFinishedList: MOVIE REQUESTED");
        AnimeFLV_Client.getINSTANCE().getMovieList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / MOVIE");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / MOVIE");
                    response.body().getMovies().subList(11, 23).clear();
                    movieList.setValue(response.body().getMovies());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / MOVIE, " + t);
            }
        });
    }

    // OVAs

    public LiveData<ArrayList<AnimeModel>> getOvaList(int page) {
        if (ovaList == null) {
            ovaList = new MutableLiveData<>();
            requestOVAList(page);
        }
        return ovaList;
    }

    private void requestOVAList(int page) {
        Log.d(TAG, "requestFinishedList: OVA REQUESTED");
        AnimeFLV_Client.getINSTANCE().getOvaList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / OVA");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / OVA");
                    response.body().getOva().subList(11, 23).clear();
                    ovaList.setValue(response.body().getOva());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / OVA, " + t);
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
            @SuppressLint("LogNotTimber")
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
