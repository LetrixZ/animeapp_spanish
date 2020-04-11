package com.letrix.animeapp.datamanager;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.ServerModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private MutableLiveData<ArrayList<AnimeModel>> ongoingsList;
    private MutableLiveData<ArrayList<AnimeModel>> finishedList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AnimeModel>> tvList;
    private MutableLiveData<ArrayList<AnimeModel>> ovaList;
    private MutableLiveData<ArrayList<AnimeModel>> movieList;
    private MutableLiveData<ArrayList<AnimeModel>> searchList;
    private MutableLiveData<ArrayList<ServerModel>> serverList = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> image = new MutableLiveData<>();
    private MutableLiveData<AnimeModel> selectedAnime = new MutableLiveData<>();

    public MutableLiveData<String> getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image.setValue(image);
    }

    public MutableLiveData<String> getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url.setValue(url);
    }

    public LiveData<ArrayList<AnimeModel>> getOngoingsList(int page) {
        if (ongoingsList == null) {
            ongoingsList = new MutableLiveData<>();
            requestOngoingsList(page);
        }
        return ongoingsList;
    }

    public LiveData<ArrayList<AnimeModel>> getFinishedList(int page) {
        if (finishedList == null) {

        }
        requestFinishedList(page);
        return finishedList;
    }

    public LiveData<ArrayList<AnimeModel>> getTvList() {
        if (tvList == null) {
            tvList = new MutableLiveData<>();
            requestTvList();
        }
        return tvList;
    }

    public LiveData<ArrayList<AnimeModel>> getOvaList() {
        if (ovaList == null) {
            ovaList = new MutableLiveData<>();
            requestOvaList();
        }
        return ovaList;
    }

    public LiveData<ArrayList<AnimeModel>> getMovieList() {
        if (movieList == null) {
            movieList = new MutableLiveData<>();
            requestMovieList();
        }
        return movieList;
    }

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

    private void requestOngoingsList(int page) {
        Client.getINSTANCE().getOngoingsList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / ONGOING");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / ONGOING");
                    ongoingsList.setValue(response.body().getAnimes());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / ONGOING, " + t);
            }
        });
    }

    private void requestFinishedList(int page) {
        Client.getINSTANCE().getFinishedList(page).enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / FINISHED");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / FINISHED");
                    if (finishedList != null && page > 1) {
                        finishedList.getValue().addAll(response.body().getAnimes());
                        Log.d(TAG, "onResponse: PAGE 2");
                    } else {
                        finishedList.setValue(response.body().getAnimes());
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / FINISHED, " + t);
            }
        });
    }

    private void requestTvList() {
        Client.getINSTANCE().getTVList().enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / TV");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / TV");
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

    private void requestMovieList() {
        Client.getINSTANCE().getMovieList().enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / MOVIES");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / MOVIES");
                    movieList.setValue(response.body().getMovies());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.d(TAG, "onFailure: API CALL FAILED / MOVIES, " + t);
            }
        });
    }

    private void requestOvaList() {
        Client.getINSTANCE().getOvaList().enqueue(new CallbackWithRetry<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.d(TAG, "onResponse: API CALL SUCCESSFUL / OVA");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: RESPONSE BODY @GET SUCCESSFUL / OVA");
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

    private void requestServerList(String id, String title) {
        Client.getINSTANCE().getServerList(id, title).enqueue(new CallbackWithRetry<JSONResponse>() {
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

    private void requestSearchList(String searchTerm) {
        Client.getINSTANCE().getSearchList(searchTerm).enqueue(new CallbackWithRetry<JSONResponse>() {
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

}
