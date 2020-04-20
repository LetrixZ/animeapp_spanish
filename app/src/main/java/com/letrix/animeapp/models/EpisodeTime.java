package com.letrix.animeapp.models;

public class EpisodeTime {

    private String animeTitle;
    private String episodeId;
    private Integer episodeNumber;
    private Long episodePosition;
    private Long episodeDuration;
    private long progressPosition;

    public EpisodeTime(String animeTitle, String episodeId, Integer episodeNumber, Long episodePosition, Long episodeDuration) {
        this.animeTitle = animeTitle;
        this.episodeId = episodeId;
        this.episodeNumber = episodeNumber;
        this.episodePosition = episodePosition;
        this.episodeDuration = episodeDuration;
        this.progressPosition = (episodePosition * 100 / episodeDuration);
    }

    public long getProgressPosition() {
        return progressPosition;
    }

    public void setProgressPosition(long progressPosition) {
        this.progressPosition = progressPosition;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public Long getEpisodePosition() {
        return episodePosition;
    }

    public void setEpisodePosition(Long episodePosition) {
        this.episodePosition = episodePosition;
    }

    public Long getEpisodeDuration() {
        return episodeDuration;
    }

    public void setEpisodeDuration(Long episodeDuration) {
        this.episodeDuration = episodeDuration;
    }
}
