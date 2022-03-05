package com.itplus.mvvmtvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itplus.mvvmtvshows.R;
import com.itplus.mvvmtvshows.adapters.WatchlistAdapter;
import com.itplus.mvvmtvshows.databinding.ActivityWatchListBinding;
import com.itplus.mvvmtvshows.listeners.WatchlistListener;
import com.itplus.mvvmtvshows.models.TVShow;
import com.itplus.mvvmtvshows.utilities.TempDataHoder;
import com.itplus.mvvmtvshows.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchList_Activity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchListBinding activityWatchListBinding;
    private WatchListViewModel viewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchListBinding = DataBindingUtil.setContentView(this,R.layout.activity_watch_list);
        doInitializaion();
    }

    private void doInitializaion() {
        viewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        activityWatchListBinding.imgBack.setOnClickListener(view -> onBackPressed());
        watchList = new ArrayList<>();
        loadWatchList();
    }

    private void loadWatchList(){
        activityWatchListBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchList().subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShows -> {
            activityWatchListBinding.setIsLoading(false);
            if (watchList.size() > 0){
                watchList.clear();
            }
            watchList.addAll(tvShows);
            watchlistAdapter = new WatchlistAdapter(watchList,this);
            activityWatchListBinding.watchListRCV.setAdapter(watchlistAdapter);
            activityWatchListBinding.watchListRCV.setVisibility(View.VISIBLE);
            compositeDisposable.dispose();
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHoder.IS_WATCHLIST_UPDATED){
            loadWatchList();
            TempDataHoder.IS_WATCHLIST_UPDATED = false;
        }

    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(),TVShowDetails_Activity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchList(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();
        compositeDisposableForDelete.add(viewModel.removeTVShowFromWatchList(tvShow)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> {
            watchList.remove(position);
            watchlistAdapter.notifyItemRemoved(position);
            watchlistAdapter.notifyItemRangeChanged(position,watchlistAdapter.getItemCount());
            compositeDisposableForDelete.dispose();
        }));
    }
}