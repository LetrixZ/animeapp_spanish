package com.letrix.animeapp.models;

import java.util.List;

public class AnimeModel {

    private String id;
    private String title;
    private String poster;
    private String banner;
    private String synopsis;
    private String debut;
    private String type;
    private String rating;
    private List<String> genres;
    private List<Episodes> episodes;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<Episodes> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episodes> episodes) {
        this.episodes = episodes;
    }

    public class Episodes {
        private String nextEpisodeDate;
        private float episode;
        private String id;
        private String imagePreview;

        public Episodes(String nextEpisodeDate, float episode, String id, String imagePreview) {
            this.nextEpisodeDate = nextEpisodeDate;
            this.episode = episode;
            this.id = id;
            this.imagePreview = imagePreview;
        }

        public String getNextEpisodeDate() {
            return nextEpisodeDate;
        }

        public void setNextEpisodeDate(String nextEpisodeDate) {
            this.nextEpisodeDate = nextEpisodeDate;
        }

        public float getEpisode() {
            return episode;
        }

        public void setEpisode(int episode) {
            this.episode = episode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImagePreview() {
            return imagePreview;
        }

        public void setImagePreview(String imagePreview) {
            this.imagePreview = imagePreview;
        }
    }
}
