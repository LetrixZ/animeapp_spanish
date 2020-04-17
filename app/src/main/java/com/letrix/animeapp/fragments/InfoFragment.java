package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.EpisodeAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.ServerModel;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class InfoFragment extends Fragment implements EpisodeAdapter.OnItemClickListener {

    private static final String TAG = "InfoFragment";
    private MainViewModel mViewModel;
    private static Boolean enableFLV = true;
    private View view, mView, separator, desuSeparator, mega_natsuki_separator, flv_jk_separator;
    private AppCompatImageView backButton, favouriteButton;
    private TextView noEpisodeText, megaText, secondServer, thirdServer, titleText, desuServer;
    private String okru;
    private AnimeModel selectedAnime;
    private boolean isFavourite;
    private ProgressBar progressBar;
    private GridLayoutManager gridLayoutManager;
    private List<String> watchedEpisodes = new ArrayList<>();
    private LinearLayout secondRow;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_info, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        favouriteButton = view.findViewById(R.id.favourite);
        mViewModel.getSelectedAnime().observe(getViewLifecycleOwner(), animeModel -> {
            selectedAnime = animeModel;
            infoAdapter(animeModel);
            if (mViewModel.getFavouriteList() != null) {
                for (AnimeModel anime : mViewModel.getFavouriteList().getValue()) {
                    if (anime.getTitle().equals(animeModel.getTitle())) {
                        favouriteButton.setImageResource(R.drawable.ic_favorite);
                        isFavourite = true;
                    }
                }
            }
        });

        favouriteButton.setOnClickListener(v -> {
            if (!isFavourite) {
                favouriteButton.setImageResource(R.drawable.ic_favorite);
                mViewModel.addFavourite(selectedAnime);
                isFavourite = true;
                Toast.makeText(getActivity(), selectedAnime.getTitle() + " añadido a favoritos", Toast.LENGTH_SHORT).show();
            } else {
                favouriteButton.setImageResource(R.drawable.ic_unfavorite);
                mViewModel.removeFavourite(selectedAnime);
                isFavourite = false;
                Toast.makeText(getActivity(), selectedAnime.getTitle() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
        noEpisodeText = view.findViewById(R.id.noEpisodes);
        noEpisodeText.setVisibility(View.GONE);

        if (mViewModel.getEnableFLV() != null) {
            enableFLV = mViewModel.getEnableFLV();
        }
    }

    private void infoAdapter(AnimeModel anime) {
        TextView animeTitle, animeRating, animeStatus, animeType;
        ExpandableTextView animeSynopsis;
        ImageView animeImage;
        ChipGroup chipGroup;
        RecyclerView recyclerView;
        chipGroup = view.findViewById(R.id.flowLayout);
        animeImage = view.findViewById(R.id.animeInfoImage);
        animeStatus = view.findViewById(R.id.animeInfoStatus);
        animeType = view.findViewById(R.id.animeInfoType);
        animeRating = view.findViewById(R.id.animeInfoReleased);
        animeSynopsis = view.findViewById(R.id.animeInfoSummary);
        animeTitle = view.findViewById(R.id.animeInfoTitle);
        byte[] decodedString = Base64.decode(anime.getPoster(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        animeImage.setImageBitmap(decodedByte);
        animeStatus.setText(anime.getDebut());
        animeTitle.setText(anime.getTitle());
        animeType.setText(anime.getType());
        animeSynopsis.setText(anime.getSynopsis());
        animeRating.setText(anime.getRating());

        recyclerView = view.findViewById(R.id.animeInfoRecyclerView);
        recyclerView.setHasFixedSize(true);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        }
        else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        if (mViewModel.getWatchedEpisodesMap() != null) {
            for (Map.Entry<String, Long> entry : mViewModel.getWatchedEpisodesMap().entrySet()) {
                watchedEpisodes.add(entry.getKey());
            }
        }
        final EpisodeAdapter dataAdapter = new EpisodeAdapter(anime, InfoFragment.this, noEpisodeText, watchedEpisodes, getActivity());
        recyclerView.setAdapter(dataAdapter);

        for (String item : anime.getGenres()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") Chip lChip = (Chip) inflater.inflate(R.layout.chip_style, null, false);
            item = item.replace("-", " ");
            item = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
            lChip.setText(item);
            lChip.setTextColor(getResources().getColor(R.color.main_text));
            chipGroup.addView(lChip, chipGroup.getChildCount() - 1);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(int position) {
        AtomicBoolean watched = new AtomicBoolean(false);
        AtomicLong time = new AtomicLong();
        String[] episodeId = mViewModel.getSelectedAnime().getValue().getEpisodes().get(position + 1).getId().split("/");
        progressBar.setVisibility(View.VISIBLE);
        String animeId = selectedAnime.getTitle().replaceAll("\\s", "-");
        animeId = animeId.replaceAll("[.|:]", "");
        animeId = animeId.toLowerCase();
        String finalAnimeId = animeId;
        mViewModel.getAnimeVideo(animeId, (int) selectedAnime.getEpisodes().get(position + 1).getEpisode()).observe(getViewLifecycleOwner(), new Observer<String>() {
            @SuppressLint("InflateParams")
            @Override
            public void onChanged(String s) {
                Log.d(TAG, finalAnimeId + "/" + (int) selectedAnime.getEpisodes().get(position + 1).getEpisode());
                Log.d(TAG, "onChanged: " + s);
                if (enableFLV) {
                    mViewModel.getServerList(episodeId[0], episodeId[1]).observe(getViewLifecycleOwner(), serverModels -> {
                        mView = getLayoutInflater().inflate(R.layout.episode_alert_dialog, null);
                        titleText = mView.findViewById(R.id.title);
                        megaText = mView.findViewById(R.id.mega_text);
                        desuServer = mView.findViewById(R.id.desu_server);
                        secondServer = mView.findViewById(R.id.second_server);
                        thirdServer = mView.findViewById(R.id.third_server);
                        separator = mView.findViewById(R.id.secondSeparator);
                        desuSeparator = mView.findViewById(R.id.view);
                        secondRow = mView.findViewById(R.id.secondRow);
                        mega_natsuki_separator = mView.findViewById(R.id.mega_natsuki_separator);
                        flv_jk_separator = mView.findViewById(R.id.view2);
                        LinearLayout firstRow = mView.findViewById(R.id.firstRow);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.alert_dialog).setCancelable(true);
                        megaText.setText("Mega");
                        desuServer.setText("Desu");
                        secondServer.setText(serverModels.get(0).getTitle());

                        if (s == null) {
                            desuServer.setVisibility(View.GONE);
                            desuSeparator.setVisibility(View.GONE);
                            firstRow.setVisibility(View.GONE);
                        }

                        for (ServerModel server : serverModels) {
                            if (server.getServer().equals("okru")) {
                                thirdServer.setVisibility(View.VISIBLE);
                                separator.setVisibility(View.VISIBLE);
                                thirdServer.setText("Okru");
                                okru = server.getCode();
                                break;
                            }
                        }

                        if (mViewModel.getWatchedEpisodesMap() != null) {
                            for (Map.Entry<String, Long> entry : mViewModel.getWatchedEpisodesMap().entrySet()) {
                                if (mViewModel.getSelectedAnime().getValue().getEpisodes().get(position + 1).getId().equals(entry.getKey()) && s != null) {
                                    long hours = entry.getValue() / 3600;
                                    long minutes = (entry.getValue() % 3600) / 60;
                                    long seconds = (entry.getValue() % 60);
                                    time.set(entry.getValue());
                                    @SuppressLint("DefaultLocale") String timeString = String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
                                    titleText.setText("Seleccione un servidor\n" + timeString);
                                    watched.set(true);
                                }
                            }
                        }

                        /*if (!enableFLV) {
                            megaText.setVisibility(View.GONE);
                            thirdServer.setVisibility(View.GONE);
                            secondServer.setVisibility(View.GONE);
                            separator.setVisibility(View.GONE);
                            mega_natsuki_separator.setVisibility(View.GONE);
                            flv_jk_separator.setVisibility(View.GONE);
                            secondRow.setVisibility(View.GONE);
                        }*/

                        AlertDialog serverSelector = mBuilder.create();
                        serverSelector.setView(mView);
                        progressBar.setVisibility(View.GONE);
                        serverSelector.show();
                        desuServer.setOnClickListener(v -> {
                            mViewModel.setUrl(s, position + 1);
                            serverSelector.dismiss();
                            watchEpisode(position);
                        });
                        megaText.setOnClickListener(v -> {
                            for (ServerModel server : serverModels) {
                                if (server.getServer().equals("mega")) {
                                    mViewModel.setUrl(server.getCode(), position + 1);
                                    serverSelector.dismiss();
                                    watchEpisode(position);
                                }
                            }
                        });
                        secondServer.setOnClickListener(v -> {
                            mViewModel.setUrl(serverModels.get(0).getCode(), position + 1);
                            serverSelector.dismiss();
                            watchEpisode(position);
                        });
                        thirdServer.setOnClickListener(v -> {
                            mViewModel.setUrl(okru, position + 1);
                            serverSelector.dismiss();
                            watchEpisode(position);
                        });
                    });

                } else {
                    if (mViewModel.getWatchedEpisodesMap() != null) {
                        for (Map.Entry<String, Long> entry : mViewModel.getWatchedEpisodesMap().entrySet()) {
                            if (mViewModel.getSelectedAnime().getValue().getEpisodes().get(position + 1).getId().equals(entry.getKey()) && s != null) {
                                long hours = entry.getValue() / 3600;
                                long minutes = (entry.getValue() % 3600) / 60;
                                long seconds = (entry.getValue() % 60);
                                time.set(entry.getValue());
                                @SuppressLint("DefaultLocale") String timeString = String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
                                //titleText.setText("Seleccione un servidor\n" + timeString);
                                watched.set(true);
                            }
                        }
                    }

                    mViewModel.setUrl(s, position + 1);
                    watchEpisode(position);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void watchEpisode(int position) {
        mViewModel.setImage(mViewModel.getSelectedAnime().getValue().getEpisodes().get(position + 1).getImagePreview());
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new WebView_VideoPlayer());
        transaction.addToBackStack("TAG2");
        transaction.commit();
    }

}
