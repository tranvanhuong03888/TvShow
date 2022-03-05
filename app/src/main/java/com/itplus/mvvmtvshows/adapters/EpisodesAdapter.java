package com.itplus.mvvmtvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.itplus.mvvmtvshows.R;
import com.itplus.mvvmtvshows.databinding.ItemContainerEpisodeBinding;
import com.itplus.mvvmtvshows.models.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHodel>{

    private List<Episode> episodeList;
    private LayoutInflater layoutInflater;

    public EpisodesAdapter(List<Episode> episodeList){
        this.episodeList = episodeList;
    }

    @NonNull
    @Override
    public EpisodeViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerEpisodeBinding itemContainerEpisodeBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_episode,parent,false
        );
        return new EpisodeViewHodel(itemContainerEpisodeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHodel holder, int position) {
        holder.bindEpisode(episodeList.get(position));
    }

    @Override
    public int getItemCount() {
        if (episodeList == null){
            return  0;
        }
        return episodeList.size();
    }

    static class EpisodeViewHodel extends RecyclerView.ViewHolder{

        private ItemContainerEpisodeBinding itemContainerEpisodeBinding;

        public EpisodeViewHodel(ItemContainerEpisodeBinding itemContainerEpisodeBinding) {
            super(itemContainerEpisodeBinding.getRoot());
            this.itemContainerEpisodeBinding = itemContainerEpisodeBinding;
        }

        public void bindEpisode(Episode episode) {
            String title = "S";
            String season = episode.getSeason();
            if (season.length() == 1){
                season = "0".concat(season);
            }
            String episodeNumber = episode.getEpisode();
            if (episodeNumber.length() == 1){
                episodeNumber = "0".concat(episodeNumber);
            }
            episodeNumber = "E".concat(episodeNumber);
            title = title.concat(season).concat(episodeNumber);
            itemContainerEpisodeBinding.setTitle(title);
            itemContainerEpisodeBinding.setName(episode.getName());
            itemContainerEpisodeBinding.setAirDate(episode.getAir_date());
        }

    }

}
