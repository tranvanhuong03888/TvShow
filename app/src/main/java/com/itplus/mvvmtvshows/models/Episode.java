package com.itplus.mvvmtvshows.models;

import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("season")
    private String season;

    @SerializedName("episode")
    private String episode;

    @SerializedName("name")
    private String name;

    @SerializedName("air_date")
    private String air_date;

    public String getSeason() {
        return season;
    }

    public String getEpisode() {
        return episode;
    }

    public String getName() {
        return name;
    }

    public String getAir_date() {
        return air_date;
    }
}
