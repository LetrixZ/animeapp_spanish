package com.letrix.animeapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.letrix.animeapp.R;
import com.letrix.animeapp.datamanager.MainViewModel;

public class WebView_VideoPlayer extends Fragment {

    private View view;

    private WebView webView;
    private ProgressBar progressBar;
    private MainViewModel mainViewModel;

    public WebView_VideoPlayer() {
        // Required empty public constructor
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.web_video_fragment, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressbar);

        mainViewModel.getUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                LoadWeb(s);
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                Toast.makeText(getContext(), "ERROR LOADING WEB", Toast.LENGTH_SHORT).show();
            }

            public void onPageFinished(WebView view, String url) {

                progressBar.setVisibility(View.GONE);
            }

        });

        return view;
    }

    public void LoadWeb(String url) {

        webView = view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setBackgroundColor(Color.BLACK);
        webView.loadUrl(url);
    }
}
