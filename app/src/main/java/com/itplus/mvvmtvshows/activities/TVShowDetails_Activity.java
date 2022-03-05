package com.itplus.mvvmtvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itplus.mvvmtvshows.R;
import com.itplus.mvvmtvshows.adapters.EpisodesAdapter;
import com.itplus.mvvmtvshows.adapters.ImageSlider_Adapter;
import com.itplus.mvvmtvshows.databinding.ActivityTvshowDetailsBinding;
import com.itplus.mvvmtvshows.databinding.LayoutEpisodesBottomSheetBinding;
import com.itplus.mvvmtvshows.models.TVShow;
import com.itplus.mvvmtvshows.utilities.TempDataHoder;
import com.itplus.mvvmtvshows.viewmodels.TVShowDetailsViewModel;

import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetails_Activity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding tvshowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvshowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        tvshowDetailsBinding.imgBack.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTVShowDetails();
    }

    private void checkTVShowInWatchlist(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchList(String.valueOf(tvShow.getId()))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShow -> {
            isTVShowAvailableInWatchList = true;
            tvshowDetailsBinding.imgWatchList.setImageResource(R.drawable.ic_check);
            compositeDisposable.dispose();
        }));
    }

    private void getTVShowDetails() {
        tvshowDetailsBinding.setIdLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(
                this, tvShowDetailsResponse -> {
                    tvshowDetailsBinding.setIdLoading(false);
                    if (tvShowDetailsResponse.getTvShowDetails() != null) {
                        if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                        }
                        tvshowDetailsBinding.setTvShowImageURL(
                                tvShowDetailsResponse.getTvShowDetails().getImage_path()
                        );
                        tvshowDetailsBinding.imgTVShow.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.setDescription(
                                String.valueOf(
                                        HtmlCompat.fromHtml(
                                                tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                )
                        );
                        tvshowDetailsBinding.txtvDescription.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.txtvReadMore.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.txtvReadMore.setOnClickListener(view -> {
                            if (tvshowDetailsBinding.txtvReadMore.getText().toString().equals("Read More")) {
                                tvshowDetailsBinding.txtvDescription.setMaxLines(Integer.MAX_VALUE);
                                tvshowDetailsBinding.txtvDescription.setEllipsize(null);
                                tvshowDetailsBinding.txtvReadMore.setText(R.string.read_less);
                            } else {
                                tvshowDetailsBinding.txtvDescription.setMaxLines(4);
                                tvshowDetailsBinding.txtvDescription.setEllipsize(TextUtils.TruncateAt.END);
                                tvshowDetailsBinding.txtvReadMore.setText(R.string.read_more);
                            }
                        });
                        tvshowDetailsBinding.setRating(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                                )
                        );
                        if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                            tvshowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                        } else {
                            tvshowDetailsBinding.setGenre("N/A");
                        }
                        tvshowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                        tvshowDetailsBinding.viewDiviDer1.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.viewDiviDer2.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.buttonWebsite.setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                            startActivity(intent);
                        });
                        tvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                        tvshowDetailsBinding.buttonEpisodes.setOnClickListener(view -> {
                            if (episodesBottomSheetDialog == null) {
                                episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetails_Activity.this);
                                layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                        LayoutInflater.from(TVShowDetails_Activity.this),
                                        R.layout.layout_episodes_bottom_sheet,
                                        findViewById(R.id.episodesContainer),
                                        false
                                );
                                episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                        new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                                );
                                layoutEpisodesBottomSheetBinding.txtvTitle.setText(
                                        String.format("Episodes | %s", tvShow.getName())
                                );
                                layoutEpisodesBottomSheetBinding.imgClose.setOnClickListener(view1 -> episodesBottomSheetDialog.dismiss());
                            }

                            //optional section start
                            FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                    com.google.android.material.R.id.design_bottom_sheet
                            );
                            if (frameLayout != null) {
                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            //optional section end
                            episodesBottomSheetDialog.show();
                        });

                        tvshowDetailsBinding.imgWatchList.setOnClickListener(view -> {
                            CompositeDisposable compositeDisposable = new CompositeDisposable();
                            if (isTVShowAvailableInWatchList){
                                compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchList(tvShow)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    isTVShowAvailableInWatchList = false;
                                    TempDataHoder.IS_WATCHLIST_UPDATED = true;
                                    tvshowDetailsBinding.imgWatchList.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                                    Toast.makeText(getApplicationContext(),"remove from watchlist",Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));
                            }else {
                                compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            TempDataHoder.IS_WATCHLIST_UPDATED = true;
                                            tvshowDetailsBinding.imgWatchList.setImageResource(R.drawable.ic_check);
                                            Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                                );
                            }
                        });
                        tvshowDetailsBinding.imgWatchList.setVisibility(View.VISIBLE);
                        loadBasicTVShowDetails();
                    }
                }
        );
    }

    private void loadImageSlider(String[] sliderImages) {
        tvshowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        tvshowDetailsBinding.sliderViewPager.setAdapter(new ImageSlider_Adapter(sliderImages));
        tvshowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        tvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImages.length);
        tvshowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            tvshowDetailsBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        tvshowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = tvshowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) tvshowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }

    private void loadBasicTVShowDetails() {
        tvshowDetailsBinding.setTvShowName(tvShow.getName());
        tvshowDetailsBinding.setNetworkCountry(
                tvShow.getNetwork() + " (" + tvShow.getCountry() + ")"
        );
        tvshowDetailsBinding.setStatus(tvShow.getStatus());
        tvshowDetailsBinding.setStartedDate(tvShow.getStart_date());
        tvshowDetailsBinding.txtvName.setVisibility(View.VISIBLE);
        tvshowDetailsBinding.txtvNetworkCountry.setVisibility(View.VISIBLE);
        tvshowDetailsBinding.txtvStarted.setVisibility(View.VISIBLE);
    }

}