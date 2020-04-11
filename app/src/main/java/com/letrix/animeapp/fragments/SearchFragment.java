package com.letrix.animeapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.letrix.animeapp.R;
import com.letrix.animeapp.adapters.SearchAdapter;
import com.letrix.animeapp.datamanager.MainViewModel;
import com.letrix.animeapp.models.AnimeModel;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener {

    private MainViewModel mViewModel;
    private EditText searchBox;
    private View view;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private String searchTerm;
    private ImageView backButton;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private ArrayList<AnimeModel> searchList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
        }


        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        backButton = view.findViewById(R.id.backButton);
        searchBox = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.searchRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        relativeLayout = view.findViewById(R.id.notfound);

        searchBox.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchTerm = String.valueOf(searchBox.getText());
                    searchBox.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    performSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void performSearch() {
        recyclerView.setAdapter(null);
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        mViewModel.getSearchList(searchTerm).observe(getViewLifecycleOwner(), new Observer<ArrayList<AnimeModel>>() {
            @Override
            public void onChanged(ArrayList<AnimeModel> animeModels) {
                searchList = animeModels;
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                final SearchAdapter dataAdapter = new SearchAdapter(animeModels, SearchFragment.this);
                recyclerView.setAdapter(dataAdapter);
                progressBar.setVisibility(View.GONE);
                if (animeModels.size() == 0) {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        mViewModel.setSelectedAnime(searchList.get(position));

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_navigation_host, new InfoFragment());
        transaction.addToBackStack("TAG");
        transaction.commit();
    }
}
