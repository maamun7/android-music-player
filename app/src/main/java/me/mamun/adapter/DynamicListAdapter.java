package me.mamun.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.Song;
import me.mamun.mplayer.R;
import me.mamun.player_control.PlayerServices;

/**
 * Created by kundan on 10/26/2015.
 */
public class DynamicListAdapter extends RecyclerView.Adapter<DynamicListAdapter.DynamicListViewHolder> {
    Context context;
    LayoutInflater inflater;
    private ArrayList<Song> songList;
    MediaPlayer mPlayer;
    public ImageButton imgBtnPlayPause;

    public DynamicListAdapter(ArrayList<Song> songList) {
        this.context = AppsHelper.aContext;
        inflater = LayoutInflater.from(this.context);
        this.songList = songList;
    }

    public class DynamicListViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongTitle, tvSongArtist, tvSongDuration;
        ImageView ivSongArtImage;
        LinearLayout lvSongOnListItem;

        public DynamicListViewHolder(View v) {
            super(v);
            lvSongOnListItem = (LinearLayout) v.findViewById(R.id.songOnListItem);
            tvSongTitle = (TextView) v.findViewById(R.id.song_title);
            tvSongArtist = (TextView) v.findViewById(R.id.song_artist);
            tvSongDuration = (TextView) v.findViewById(R.id.song_duration);
            ivSongArtImage = (ImageView) v.findViewById(R.id.list_avatar);
        }
    }

    @Override
    public DynamicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        //Initilize media player
        mPlayer = new MediaPlayer();
        return new DynamicListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DynamicListViewHolder holder, int position) {
        holder.tvSongTitle.setText(songList.get(position).getSongDisplayName());
        holder.tvSongArtist.setText(songList.get(position).getSongArtist());
        holder.tvSongDuration.setText(songList.get(position).getSongDuration());
        holder.ivSongArtImage.setImageBitmap(songList.get(position).getSongArtImage());
        holder.lvSongOnListItem.setOnClickListener(clickListener);
        holder.lvSongOnListItem.setTag(holder);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DynamicListViewHolder vholder = (DynamicListViewHolder) v.getTag();
            int position = vholder.getPosition();
            //Set song list
            AppsHelper.defaultSongList = songList;
            AppsHelper.currentSongIndex = position;
            //AppsHelper.playerControl.isPlaying = false;
           // AppsHelper.playerControl.play();
            AppsHelper.aContext.startService(new Intent(PlayerServices.ACTION_PLAY_LIST));
        }
    };


    @Override
    public int getItemCount() {
        return songList.size();
    }
}
