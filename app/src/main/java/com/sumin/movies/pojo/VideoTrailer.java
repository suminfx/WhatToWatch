package com.sumin.movies.pojo;

import com.sumin.movies.utils.NetworkUtils;

public class VideoTrailer {

    private String name;
    private String key;
    private String uriToYoutube;

    public VideoTrailer(String name, String key) {
        this.name = name;
        this.key = key;
        this.uriToYoutube = NetworkUtils.buildUriToLoadVideoByKey(key);
    }

    public String getUriToYoutube() {
        return uriToYoutube;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
