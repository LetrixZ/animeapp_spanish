package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;

import im.delight.android.webview.AdvancedWebView;

public class WebView_VideoPlayer extends Fragment {

    private static final String TAG = "WebView_VideoPlayer";

    private View view;

    private AdvancedWebView webView;
    private ProgressBar progressBar;
    private MainViewModel mainViewModel;

    private long startTime, endTime;

    public WebView_VideoPlayer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.web_video_fragment, container, false);

        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressbar);

        mainViewModel.getUrl().observe(getViewLifecycleOwner(), this::LoadWeb);
        Log.d(TAG, "onCreateView: " + mainViewModel.getEpisodePosition().getValue());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                startTime = System.currentTimeMillis();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            public void onPageFinished(WebView view, String url) {

                progressBar.setVisibility(View.GONE);
            }

        });

        return view;
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void LoadWeb(String url) {

        webView = view.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebChromeClient(new WebChromeClientCustomPoster());
        webView.setBackgroundColor(Color.BLACK);
        webView.setDesktopMode(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(url);
    }

    private class WebChromeClientCustomPoster extends WebChromeClient {

        @Override
        public Bitmap getDefaultVideoPoster() {
            byte[] decodedString = Base64.decode(mainViewModel.getSelectedAnime().getValue().getPoster(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        endTime = System.currentTimeMillis();
        Log.d(TAG, "onDestroyView: " + (endTime - startTime)/1000);
        int time = (int) ((endTime - startTime)/1000);
        if (time > 60) {
            mainViewModel.addWatchedEpisode(mainViewModel.getEpisodePosition().getValue(), ((endTime - startTime)/1000)-30);
        }
        else {
            Log.d(TAG, "onDestroyView: NOT WATCHED");
        }
    }
}
