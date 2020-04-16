package com.letrix.animeapp.models;

public class WatchedEpisodes {

    private String episodeId;
    private long watchedTime;

    public WatchedEpisodes(String episodeId, long watchedTime) {
        this.episodeId = episodeId;
        this.watchedTime = watchedTime;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(long watchedTime) {
        this.watchedTime = watchedTime;
    }
}
