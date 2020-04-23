package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.EpisodeAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;
import com.letrix.animeapp.models.EpisodeTime;
import com.letrix.animeapp.models.ServerModel;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@SuppressWarnings("ALL")
public class InfoFragment extends Fragment implements EpisodeAdapter.OnItemClickListener {

    private static final String TAG = "InfoFragment";
    private static Boolean enableFLV = true;
    private MainViewModel mainViewModel;
    private View rootView;
    private AppCompatImageView backButton, favouriteButton;
    private AnimeModel selectedAnime;
    private boolean isFavourite;
    private ProgressBar progressBar;
    private GridLayoutManager gridLayoutManager;
    private List<Integer> watchedEpisodes = new ArrayList<>();
    private AnimeModel currentAnime;

    private TextView noEpisodeText;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        backButton = rootView.findViewById(R.id.back);
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        noEpisodeText = rootView.findViewById(R.id.noEpisodes);
        noEpisodeText.setVisibility(View.GONE);

        // Favorite
        favouriteButton = rootView.findViewById(R.id.favourite);

        mainViewModel.getSelectedAnime().observe(getViewLifecycleOwner(), animeModel -> {
            selectedAnime = animeModel;
            //infoAdapter(animeModel);
            if (mainViewModel.getFavouriteList() != null) {
                for (AnimeModel anime : mainViewModel.getFavouriteList().getValue()) {
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
                mainViewModel.addFavourite(selectedAnime);
                isFavourite = true;
                Toast.makeText(requireActivity(), selectedAnime.getTitle() + " añadido a favoritos", Toast.LENGTH_SHORT).show();
            } else {
                favouriteButton.setImageResource(R.drawable.ic_unfavorite);
                mainViewModel.removeFavourite(selectedAnime);
                isFavourite = false;
                Toast.makeText(requireActivity(), selectedAnime.getTitle() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle b = getArguments();
        if (b != null) {
            String imageTransitionName = b.getString("imageTransitionName");
            String titleTransitionName = b.getString("titleTransitionName");
            currentAnime = (AnimeModel) b.getSerializable("anime");
            infoAdapter(currentAnime, imageTransitionName, titleTransitionName);
        }

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableFLV = true;
        if (mainViewModel.getEnableFLV() != null) {
            enableFLV = mainViewModel.getEnableFLV();
        }
    }

    private void infoAdapter(AnimeModel anime, String imageTransitionName, String titleTransitionName) {
        TextView animeTitle, animeRating, animeStatus, animeType, animeNextEpisode;
        ExpandableTextView animeSynopsis;
        ImageView animeImage;
        ChipGroup chipGroup;
        RecyclerView recyclerView;
        LinearLayout nextEpisodeLayout;

        // Views
        chipGroup = rootView.findViewById(R.id.flowLayout);
        animeImage = rootView.findViewById(R.id.animeImage);
        animeStatus = rootView.findViewById(R.id.animeInfoStatus);
        animeType = rootView.findViewById(R.id.animeInfoType);
        animeRating = rootView.findViewById(R.id.animeInfoReleased);
        animeSynopsis = rootView.findViewById(R.id.animeInfoSummary);
        animeTitle = rootView.findViewById(R.id.animeTitle);
        animeNextEpisode = rootView.findViewById(R.id.animeInfoNextEpisode);
        nextEpisodeLayout = rootView.findViewById(R.id.nextEpisodeLayout);

        // Set
        animeImage.setTransitionName(imageTransitionName);
        animeTitle.setTransitionName(titleTransitionName);

        if (anime.getPoster().length() > 80) {
            byte[] decodedString = Base64.decode(anime.getPoster(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            animeImage.setImageBitmap(decodedByte);
        }
        else {
            Picasso.get().load(anime.getPoster()).into(animeImage);
        }
        animeStatus.setText(anime.getDebut());
        animeTitle.setText(anime.getTitle());
        animeType.setText(anime.getType());
        animeSynopsis.setText(anime.getSynopsis());
        animeRating.setText(anime.getRating());
        if (anime.getEpisodes().get(0).getNextEpisodeDate() != null) {
            animeNextEpisode.setText(anime.getEpisodes().get(0).getNextEpisodeDate());
        } else {
            nextEpisodeLayout.setVisibility(View.GONE);
        }

        // Genre Chips //
        for (String item : anime.getGenres()) {
            LayoutInflater inflater = LayoutInflater.from(requireActivity());
            @SuppressLint("InflateParams") Chip lChip = (Chip) inflater.inflate(R.layout.chip_style, null, false);
            item = item.replace("-", " ");
            item = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
            lChip.setText(item);
            lChip.setTextColor(getResources().getColor(R.color.mainText));
            chipGroup.addView(lChip, chipGroup.getChildCount() - 1);
        }

        // Episodes //
        recyclerView = rootView.findViewById(R.id.animeInfoRecyclerView);
        recyclerView.setHasFixedSize(true);
        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 5);
        } else {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        }
        recyclerView.setLayoutManager(gridLayoutManager);

        if (mainViewModel.checkWatched(anime)) {
            for (Map.Entry<Integer, EpisodeTime> entry : mainViewModel.getCurrentlyWatching().get(anime).entrySet()) {
                watchedEpisodes.add(entry.getKey());
            }
        }

        if (anime.getEpisodes() == null) {
            noEpisodeText.setVisibility(View.VISIBLE);
        } else {
            final EpisodeAdapter dataAdapter = new EpisodeAdapter(anime, InfoFragment.this, watchedEpisodes, requireActivity());
            recyclerView.setAdapter(dataAdapter);
        }
    }

    @Override
    public void onItemClick(int position) {
        AnimeModel.Episodes currentEpisode = currentAnime.getEpisodes().get(position + 1);
        initAlertDialog(currentAnime, currentEpisode);
    }

    @SuppressLint("SetTextI18n")
    private void initAlertDialog(AnimeModel currentAnime, AnimeModel.Episodes currentEpisode) {
        this.progressBar.setVisibility(View.VISIBLE);

        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.episode_alert_dialog, null);
        View topSeparator, secondSeparator, firstSeparator;
        TextView titleText, megaServer, natsukiServer, okruServer, desuServer;
        LinearLayout desuRow, animeFlvVServers;
        ProgressBar progressBar;

        // Views
        progressBar = dialogView.findViewById(R.id.progressBar);
        topSeparator = dialogView.findViewById(R.id.topSeparator);
        firstSeparator = dialogView.findViewById(R.id.firstSeparator);
        secondSeparator = dialogView.findViewById(R.id.secondSeparator);
        titleText = dialogView.findViewById(R.id.title);
        megaServer = dialogView.findViewById(R.id.megaServer);
        natsukiServer = dialogView.findViewById(R.id.natsukiServer);
        okruServer = dialogView.findViewById(R.id.okruServer);
        desuServer = dialogView.findViewById(R.id.desuServer);
        desuRow = dialogView.findViewById(R.id.desuRow);
        animeFlvVServers = dialogView.findViewById(R.id.animeFlvVServers);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireActivity(), R.style.alert_dialog).setCancelable(true);
        AlertDialog serverSelector = mBuilder.create();
        serverSelector.setView(dialogView);

        // Title parse
        String animeId = currentAnime.getTitle().replaceAll("\\s", "-");
        animeId = animeId.replaceAll("[.|:|!|/|(|)|']", "");
        animeId = animeId.toLowerCase();
        if (animeId.length() > 0 && animeId.charAt(animeId.length() - 1) == '-') {
            animeId = animeId.substring(0, animeId.length() - 1);
        }
        DecimalFormat format = new DecimalFormat("0.#");
        if (!enableFLV) {
            mainViewModel.getAnimeVideo(animeId, currentEpisode.getEpisode()).observe(getViewLifecycleOwner(), desuURL -> {
                if (desuURL != null) {
                    watchEpisodeDesu(desuURL, currentAnime, currentEpisode);
                } else {
                    desuServer.setText(R.string.not_available_desu);
                    serverSelector.show();
                }
            });
        } else {
            mainViewModel.getAnimeVideo(animeId, currentEpisode.getEpisode()).observe(getViewLifecycleOwner(), desuURL -> {
                // Desu
                this.progressBar.setVisibility(View.GONE);
                serverSelector.show();
                if (desuURL != null) {
                    if (mainViewModel.getCurrentlyWatching() != null && mainViewModel.getCurrentlyWatching().get(currentAnime) != null && mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()) != null) {
                        int time = (int) (mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()).getEpisodePosition() / 1000);
                        long hours = time / 3600;
                        long minutes = (time % 3600) / 60;
                        long seconds = (time % 60);
                        @SuppressLint("DefaultLocale") String timeString = String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
                        if (time != 0 && mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()).getProgressPosition() < 90) {
                            Timber.d("initAlertDialog: %s", (mainViewModel.getCurrentlyWatching().get(currentAnime).get((int) currentEpisode.getEpisode()).getProgressPosition() < 90));
                            desuServer.setText("Desu (" + timeString + ")");
                        }
                    }
                    desuServer.setOnClickListener(v -> {
                        serverSelector.dismiss();
                        watchEpisodeDesu(desuURL, currentAnime, currentEpisode);
                    });
                } else {
                    desuServer.setText(R.string.not_available_desu);
                }
                // AnimeFLV
                String[] episodeId = currentEpisode.getId().split("/");
                progressBar.setVisibility(View.VISIBLE);
                firstSeparator.setVisibility(View.GONE);
                secondSeparator.setVisibility(View.GONE);
                mainViewModel.getServerList(episodeId[0], episodeId[1]).observe(getViewLifecycleOwner(), serverModels -> {
                    firstSeparator.setVisibility(View.VISIBLE);
                    secondSeparator.setVisibility(View.VISIBLE);
                    String megaUrl = null, okruUrl = null;
                    for (ServerModel server : serverModels) {
                        if (server.getTitle().equals("MEGA")) {
                            megaServer.setText(server.getTitle());
                            megaUrl = server.getCode();
                        }
                        if (server.getTitle().equals("Okru")) {
                            okruServer.setText(server.getTitle());
                            okruUrl = server.getCode();
                        }
                    }
                    natsukiServer.setText(serverModels.get(0).getTitle());
                    natsukiServer.setOnClickListener(v -> {
                        serverSelector.dismiss();
                        watchEpisodeWeb(serverModels.get(0).getCode(), currentAnime, currentEpisode);
                        });
                    String finalMegaUrl = megaUrl;
                    if (megaUrl == null) {
                        megaServer.setVisibility(View.GONE);
                        firstSeparator.setVisibility(View.GONE);
                    } else {
                        megaServer.setOnClickListener(v -> {
                            serverSelector.dismiss();
                            watchEpisodeWeb(finalMegaUrl, currentAnime, currentEpisode);
                        });
                    }
                    String finalOkruUrl = okruUrl;
                    if (okruUrl == null) {
                        okruServer.setVisibility(View.GONE);
                        secondSeparator.setVisibility(View.GONE);
                    } else {
                        okruServer.setOnClickListener(v -> {
                            serverSelector.dismiss();
                            watchEpisodeWeb(finalOkruUrl, currentAnime, currentEpisode);
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                });
            });
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void watchEpisodeWeb(String videoUrl, AnimeModel currentAnime, AnimeModel.Episodes currentEpisode) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, WebView_VideoPlayer.newInstance(videoUrl, currentAnime, currentEpisode));
        transaction.addToBackStack("WebViewPlayer");
        transaction.commit();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void watchEpisodeDesu(String videoUrl, AnimeModel currentAnime, AnimeModel.Episodes currentEpisode) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, ExoPlayerFragment.newInstance(videoUrl, currentAnime, currentEpisode));
        transaction.addToBackStack("ExoPlayer");
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }
}
