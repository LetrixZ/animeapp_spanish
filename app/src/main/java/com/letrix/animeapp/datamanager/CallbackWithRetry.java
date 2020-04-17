package com.letrix.animeapp.datamanager;

import android.util.Log;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

abstract class CallbackWithRetry<T> implements Callback<T> {

    private static final int TOTAL_RETRIES = 3;
    private static final String TAG = CallbackWithRetry.class.getSimpleName();
    private int retryCount = 0;

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, Objects.requireNonNull(t.getLocalizedMessage()));
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v(TAG, "Retrying... (" + retryCount + " out of " + TOTAL_RETRIES + ")");
            retry(call);
        }
    }

    private void retry(Call<T> call) {
        call.clone().enqueue(this);
    }
}