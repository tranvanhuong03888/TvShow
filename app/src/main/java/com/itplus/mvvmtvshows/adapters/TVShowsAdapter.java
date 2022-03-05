package com.itplus.mvvmtvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.itplus.mvvmtvshows.R;
import com.itplus.mvvmtvshows.databinding.ItemContainerTvShowBinding;
import com.itplus.mvvmtvshows.listeners.TVShowsListener;
import com.itplus.mvvmtvshows.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowViewHodelr>{
    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private TVShowsListener tvShowsListener;


    public TVShowsAdapter(List<TVShow> tvShows,TVShowsListener tvShowsListener){
        this.tvShows = tvShows;
        this.tvShowsListener = tvShowsListener;
    }

    @NonNull
    @Override
    public TVShowViewHodelr onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerTvShowBinding tvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_tv_show,parent,false
        );
        return new TVShowViewHodelr(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHodelr holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        if (tvShows == null){
            return 0;
        }
        return tvShows.size();
    }

     class TVShowViewHodelr extends RecyclerView.ViewHolder{
        private ItemContainerTvShowBinding itemContainerTvShowBinding;

        public TVShowViewHodelr(ItemContainerTvShowBinding itemContainerTvShowBinding){
            super(itemContainerTvShowBinding.getRoot());
            this.itemContainerTvShowBinding = itemContainerTvShowBinding;
        }
        public void bindTVShow(TVShow tvShow){
            itemContainerTvShowBinding.setTvShow(tvShow);
            itemContainerTvShowBinding.executePendingBindings();
            itemContainerTvShowBinding.getRoot().setOnClickListener(view -> tvShowsListener.onTVShowClicked(tvShow));
        }
    }

}
