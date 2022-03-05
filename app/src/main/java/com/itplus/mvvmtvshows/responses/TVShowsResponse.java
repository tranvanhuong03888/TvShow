package com.itplus.mvvmtvshows.responses;

import com.google.gson.annotations.SerializedName;
import com.itplus.mvvmtvshows.models.TVShow;

import java.util.List;

public class TVShowsResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("pages")
    private int totalPages;

    @SerializedName("tv_shows")
    private List<TVShow> tv_shows;

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<TVShow> getTv_shows() {
        return tv_shows;
    }
}
