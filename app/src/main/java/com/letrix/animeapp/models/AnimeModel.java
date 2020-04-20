package com.letrix.animeapp.models;

import java.util.List;
import java.util.Objects;

public final class AnimeModel {

    private final String id;
    private final String title;
    private final String poster;
    private final String banner;
    private final String synopsis;
    private final String debut;
    private final String type;
    private final String rating;
    private final List<String> genres;
    private final List<Episodes> episodes;

    public AnimeModel(String id, String title, String poster, String banner, String synopsis, String debut, String type, String rating, List<String> genres, List<Episodes> episodes) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.banner = banner;
        this.synopsis = synopsis;
        this.debut = debut;
        this.type = type;
        this.rating = rating;
        this.genres = genres;
        this.episodes = episodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimeModel that = (AnimeModel) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(type, that.type) &&
                Objects.equals(genres, that.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, type, genres);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getBanner() {
        return banner;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getDebut() {
        return debut;
    }

    public String getType() {
        return type;
    }

    public String getRating() {
        return rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<Episodes> getEpisodes() {
        return episodes;
    }

    public final class Episodes {
        private final String nextEpisodeDate;
        private final Float episode;
        private final String id;
        private final String imagePreview;

        public Episodes(String nextEpisodeDate, float episode, String id, String imagePreview) {
            this.nextEpisodeDate = nextEpisodeDate;
            this.episode = episode;
            this.id = id;
            this.imagePreview = imagePreview;
        }

        public String getNextEpisodeDate() {
            return nextEpisodeDate;
        }

        public float getEpisode() {
            return episode;
        }

        public String getId() {
            return id;
        }

        public String getImagePreview() {
            return imagePreview;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Episodes episodes = (Episodes) o;
            return Objects.equals(nextEpisodeDate, episodes.nextEpisodeDate) &&
                    Objects.equals(episode, episodes.episode) &&
                    Objects.equals(id, episodes.id) &&
                    Objects.equals(imagePreview, episodes.imagePreview);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nextEpisodeDate, episode, id, imagePreview);
        }
    }
}
