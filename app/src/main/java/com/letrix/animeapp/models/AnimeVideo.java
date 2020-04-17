package com.letrix.animeapp.models;

import com.google.gson.annotations.SerializedName;

public class AnimeVideo {

    @SerializedName("video")
    private String video;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}