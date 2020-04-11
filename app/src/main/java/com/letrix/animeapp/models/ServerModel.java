package com.letrix.animeapp.models;

public class ServerModel {
    private String server;
    private String title;
    private String url;
    private boolean allow_mobile;
    private String code;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAllow_mobile() {
        return allow_mobile;
    }

    public void setAllow_mobile(boolean allow_mobile) {
        this.allow_mobile = allow_mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
