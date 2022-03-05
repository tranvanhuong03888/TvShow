package com.itplus.mvvmtvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.itplus.mvvmtvshows.reporitories.SearchTVShowRepository;
import com.itplus.mvvmtvshows.responses.TVShowsResponse;

public class SearchViewModel  extends ViewModel {

    private SearchTVShowRepository searchTVShowRepository;

    public SearchViewModel(){
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowsResponse> searchTVShow(String query,int page) {
        return searchTVShowRepository.searchTVShow(query, page);
    }

}
