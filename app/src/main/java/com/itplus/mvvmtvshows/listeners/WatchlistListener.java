package com.itplus.mvvmtvshows.listeners;

import com.itplus.mvvmtvshows.models.TVShow;

public interface WatchlistListener {

    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchList(TVShow tvShow,int position);

}
