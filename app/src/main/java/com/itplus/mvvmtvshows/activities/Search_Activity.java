package com.itplus.mvvmtvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.itplus.mvvmtvshows.R;
import com.itplus.mvvmtvshows.adapters.TVShowsAdapter;
import com.itplus.mvvmtvshows.databinding.ActivitySearchBinding;
import com.itplus.mvvmtvshows.listeners.TVShowsListener;
import com.itplus.mvvmtvshows.models.TVShow;
import com.itplus.mvvmtvshows.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Search_Activity extends AppCompatActivity implements TVShowsListener {

    private ActivitySearchBinding activitySearchBinding;
    private SearchViewModel searchViewModel;
    private List<TVShow> tvShowList = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        doInitialization();
    }

    private void doInitialization() {
        activitySearchBinding.imgBack.setOnClickListener(view -> onBackPressed());
        activitySearchBinding.tvShowRCV.setHasFixedSize(true);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShowList, this);
        activitySearchBinding.tvShowRCV.setAdapter(tvShowsAdapter);
        activitySearchBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null){
                    timer.cancel();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty()){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalAvailablePages = 1;
                                searchTVShow(editable.toString());
                            });
                        }
                    },800);
                }else{
                    tvShowList.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
        activitySearchBinding.tvShowRCV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activitySearchBinding.tvShowRCV.canScrollVertically(1)){
                    if (!activitySearchBinding.edtSearch.getText().toString().isEmpty()){
                        if (currentPage < totalAvailablePages){
                            currentPage += 1;
                            searchTVShow(activitySearchBinding.edtSearch.getText().toString());
                        }
                    }
                }
            }
        });
        activitySearchBinding.edtSearch.requestFocus();
    }

    private void searchTVShow(String query) {
        toggleLoading();
        searchViewModel.searchTVShow(query, currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalAvailablePages = tvShowsResponse.getTotalPages();
                if (tvShowsResponse.getTv_shows() != null) {
                    int oldCount = tvShowList.size();
                    tvShowList.addAll(tvShowsResponse.getTv_shows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShowList.size());

                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (activitySearchBinding.getIsLoading() != null && activitySearchBinding.getIsLoading()) {
                activitySearchBinding.setIsLoading(false);
            } else {
                activitySearchBinding.setIsLoading(true);
            }
        } else {
            if (activitySearchBinding.getIsLoadingMore() != null && activitySearchBinding.getIsLoadingMore()) {
                activitySearchBinding.setIsLoadingMore(false);
            } else {
                activitySearchBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetails_Activity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}