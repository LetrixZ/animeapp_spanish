package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;

import im.delight.android.webview.AdvancedWebView;

public class WebView_VideoPlayer extends Fragment {

    private static final String TAG = "WebView_VideoPlayer";

    private View view;

    private AdvancedWebView webView;
    private ProgressBar progressBar;
    private MainViewModel mainViewModel;
    private PlayerView playerView;
    private ExoPlayer player;

    private boolean isExoPlayer;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

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
        playerView = view.findViewById(R.id.exoplayer_view);
        webView = view.findViewById(R.id.webView);

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

        mainViewModel.getUrl().observe(getViewLifecycleOwner(), this::LoadPlayer);
        Log.d(TAG, "ID: " + mainViewModel.getSelectedAnime().getValue().getEpisodes().get(mainViewModel.getEpisodePosition().getValue()).getId());

        Log.d(TAG, "SAVED TIME: " + mainViewModel.getWatchedEpisodesMap().get(mainViewModel.getSelectedAnime().getValue().getEpisodes().get(mainViewModel.getEpisodePosition().getValue()).getId()));

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

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getActivity(), "exoplayer");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void LoadPlayer(String url) {

        if (url.contains("storage.googleapis.com")) {
            //progressBar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(getActivity());
                playerView.setPlayer(player);
                Uri uri = Uri.parse(url);
                Log.d(TAG, "LoadPlayer: " + url);
                Log.d(TAG, "LoadPlayer: " + Uri.parse(url));
                MediaSource mediaSource = buildMediaSource(uri);
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {
                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == ExoPlayer.STATE_BUFFERING) {
                            progressBar.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    }
                });
                player.setPlayWhenReady(playWhenReady);
                player.seekTo(currentWindow, (mainViewModel.getWatchedEpisodesMap().get(mainViewModel.getSelectedAnime().getValue().getEpisodes().get(mainViewModel.getEpisodePosition().getValue()).getId())) * 1000 - 30);
                player.prepare(mediaSource, false, false);
                isExoPlayer = true;
            }
            //progressBar.setVisibility(View.GONE);
        } else {
            isExoPlayer = false;
            playerView.setVisibility(View.GONE);
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

    }

    private class WebChromeClientCustomPoster extends WebChromeClient {

        @Override
        public Bitmap getDefaultVideoPoster() {
            byte[] decodedString = Base64.decode(mainViewModel.getSelectedAnime().getValue().getPoster(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isExoPlayer) {
            mainViewModel.addWatchedEpisode(mainViewModel.getEpisodePosition().getValue(), player.getCurrentPosition() / 1000);
            Log.d(TAG, "onDestroyView: " + mainViewModel.getEpisodePosition().getValue());
            releasePlayer();
        }
        else {
            mainViewModel.addWatchedEpisode(mainViewModel.getEpisodePosition().getValue(), 0);
        }
    }
}
