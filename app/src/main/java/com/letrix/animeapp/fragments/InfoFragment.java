package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.letrix.animeapp.models.ServerModel;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class InfoFragment extends Fragment implements EpisodeAdapter.OnItemClickListener {

    private MainViewModel mViewModel;
    private View view;
    private AppCompatImageView backButton;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_info, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getSelectedAnime().observe(getViewLifecycleOwner(), animeModel -> {
            infoAdapter(animeModel);
        });
    }

    private void infoAdapter(AnimeModel anime) {
        TextView animeTitle, animeRating, animeStatus, animeType;
        ExpandableTextView animeSynopsis;
        ImageView animeImage;
        ChipGroup chipGroup;
        RecyclerView recyclerView;

        recyclerView = view.findViewById(R.id.animeInfoRecyclerView);
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        final EpisodeAdapter dataAdapter = new EpisodeAdapter(anime, InfoFragment.this);
        recyclerView.setAdapter(dataAdapter);

        for (String item : anime.getGenres()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Chip lChip = (Chip) inflater.inflate(R.layout.chip_style, null, false);
            item = item.replace("-", " ");
            item = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
            lChip.setText(item);
            lChip.setTextColor(getResources().getColor(R.color.main_text));
            chipGroup.addView(lChip, chipGroup.getChildCount() - 1);
        }

    }

    @Override
    public void onItemClick(int position) {
        String[] episodeId = mViewModel.getSelectedAnime().getValue().getEpisodes().get(position + 1).getId().split("/");
        mViewModel.getServerList(episodeId[0], episodeId[1]).observe(getViewLifecycleOwner(), serverModels -> {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.alert_dialog).setCancelable(true);
            View mView = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            TextView megaText = mView.findViewById(R.id.mega_text);
            TextView natsukiText = mView.findViewById(R.id.natsuki_text);
            AlertDialog serverSelector = mBuilder.create();
            serverSelector.setView(mView);
            serverSelector.show();

            megaText.setOnClickListener(v -> {
                for (ServerModel server : serverModels) {
                    if (server.getServer().equals("mega")) {
                        String url = server.getCode();
                        String[] newURL = url.split("nz/");
                        newURL[0] = newURL[0] + "nz/embed";
                        mViewModel.setUrl(newURL[0] + newURL[1]);
                        getInfo(position);
                    }
                }
            });
            natsukiText.setOnClickListener(v -> {
                for (ServerModel server : serverModels) {
                    if (server.getServer().equals("natsuki")) {
                        String url = server.getCode();
                        mViewModel.setUrl(url);
                        getInfo(position);
                    }
                }
            });
        });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void getInfo(int position) {
        mViewModel.setImage(mViewModel.getSelectedAnime().getValue().getEpisodes().get(position + 1).getImagePreview());

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new WebView_VideoPlayer());
        transaction.addToBackStack("TAG2");
        transaction.commit();
    }
}
