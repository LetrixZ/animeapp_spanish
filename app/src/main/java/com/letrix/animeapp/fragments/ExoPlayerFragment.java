package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
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
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.utils.GSONParse;

import timber.log.Timber;

public class ExoPlayerFragment extends Fragment {

    private static final String TAG = "ExoPlayerFragment";

    private ProgressBar progressBar;
    private MainViewModel mainViewModel;
    private PlayerView playerView;
    private ExoPlayer player;

    private String videoURL;
    private AnimeModel currentAnime;
    private AnimeModel.Episodes currentEpisode;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    static ExoPlayerFragment newInstance(String videoURL, AnimeModel currentAnime, AnimeModel.Episodes currentEpisode) {
        Bundle args = new Bundle();
        args.putString("URL", videoURL);
        args.putString("Anime", GSONParse.getGsonParser().toJson(currentAnime));
        args.putString("Episode", GSONParse.getGsonParser().toJson(currentEpisode));
        ExoPlayerFragment f = new ExoPlayerFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exoplayer_fragment, container, false);
        playerView = rootView.findViewById(R.id.exoPlayerView);
        progressBar = rootView.findViewById(R.id.progressbar);

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

        LoadPlayer(videoURL);

        return rootView;
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(requireActivity(), "exoplayer");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void LoadPlayer(String url) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(requireActivity());
            playerView.setPlayer(player);
            Uri uri = Uri.parse(url);
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
            if (mainViewModel.getCurrentlyWatching() != null && mainViewModel.getCurrentlyWatching().get(currentAnime) != null && mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()) != null && mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()).getProgressPosition() < 90) {
                player.seekTo(currentWindow, mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()).getEpisodePosition());
            } else {
                player.seekTo(currentWindow, playbackPosition);
            }
            player.prepare(mediaSource, false, false);
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
        player.getCurrentPosition();
        if (player.getCurrentPosition() > 0) {
            mainViewModel.addCurrentlyWatching(currentAnime, currentEpisode.getId(), (int) currentEpisode.getEpisode(), player.getContentPosition(), player.getDuration());
            Timber.d("releasePlayer: ADDING DATA");
        }
        releasePlayer();
    }

}


