package com.letrix.animeapp.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class AnimeWatched implements Serializable {

    private AnimeModel anime;
    private Set<Episode> episodes;

    public AnimeWatched(AnimeModel anime, Set<Episode> episodes) {
        this.anime = anime;
        this.episodes = episodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimeWatched that = (AnimeWatched) o;
        return Objects.equals(anime, that.anime) &&
                Objects.equals(episodes, that.episodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anime, episodes);
    }

    public AnimeModel getAnime() {
        return anime;
    }

    public void setAnime(AnimeModel anime) {
        this.anime = anime;
    }

    public Set<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Set<Episode> episodes) {
        this.episodes = episodes;
    }

    public static class Episode implements Serializable{
        private String episodeId;
        private Float episodeNumber;
        private Long episodePosition;

        public Episode(String episodeId, Float episodeNumber, Long episodePosition) {
            this.episodeId = episodeId;
            this.episodeNumber = episodeNumber;
            this.episodePosition = episodePosition;
        }

        public String getEpisodeId() {
            return episodeId;
        }

        public void setEpisodeId(String episodeId) {
            this.episodeId = episodeId;
        }

        public Float getEpisodeNumber() {
            return episodeNumber;
        }

        public void setEpisodeNumber(Float episodeNumber) {
            this.episodeNumber = episodeNumber;
        }

        public Long getEpisodePosition() {
            return episodePosition;
        }

        public void setEpisodePosition(Long episodePosition) {
            this.episodePosition = episodePosition;
        }
    }
}
