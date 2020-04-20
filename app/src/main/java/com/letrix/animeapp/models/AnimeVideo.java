package com.letrix.animeapp.models;

import com.google.gson.annotations.SerializedName;

public class AnimeVideo {

    @SerializedName("video")
    private String video;
    @SerializedName("message")
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}