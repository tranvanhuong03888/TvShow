package com.itplus.mvvmtvshows.responses;

import com.google.gson.annotations.SerializedName;
import com.itplus.mvvmtvshows.models.TVShowDetails;

public class TVShowDetailsResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
