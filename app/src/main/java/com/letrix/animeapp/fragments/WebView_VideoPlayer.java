package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.utils.GSONParse;

import im.delight.android.webview.AdvancedWebView;

public class WebView_VideoPlayer extends Fragment {

    private static final String TAG = "WebView_VideoPlayer";

    private View view;
    private MainViewModel mainViewModel;

    private AdvancedWebView webView;
    private ProgressBar progressBar;

    private String videoURL;
    private AnimeModel currentAnime;
    private AnimeModel.Episodes currentEpisode;

    public WebView_VideoPlayer() {
        // Required empty public constructor
    }

    static WebView_VideoPlayer newInstance(String videoURL, AnimeModel currentAnime, AnimeModel.Episodes currentEpisode) {
        Bundle args = new Bundle();
        args.putString("URL", videoURL);
        args.putString("Anime", GSONParse.getGsonParser().toJson(currentAnime));
        args.putString("Episode", GSONParse.getGsonParser().toJson(currentEpisode));
        WebView_VideoPlayer f = new WebView_VideoPlayer();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.web_video_fragment, container, false);
        webView = view.findViewById(R.id.webView);
        Bundle args = getArguments();
        if (args != null) {
            videoURL = args.getString("URL");
            currentAnime = GSONParse.getGsonParser().fromJson(args.getString("Anime"), AnimeModel.class);
            currentEpisode = GSONParse.getGsonParser().fromJson(args.getString("Episode"), AnimeModel.Episodes.class);
        }
        View decorView = requireActivity().getWindow().getDecorView();
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
        progressBar = view.findViewById(R.id.progressBar);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        LoadPlayer(videoURL);

        return view;
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void LoadPlayer(String url) {
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
        mainViewModel.addCurrentlyWatching(currentAnime, currentEpisode.getId(), (int) currentEpisode.getEpisode(), (long) 0, (long) 1);
    }
}
