package me.mamun.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class ThirdPageAdapter extends RecyclerView.Adapter<ThirdPageAdapter.ThirdPageViewHolder> {
    Context context;
    LayoutInflater inflater;
    private ArrayList<Song> songList;
    MediaPlayer mPlayer;
    public ImageButton imgBtnPlayPause;

    public ThirdPageAdapter(Context context, ArrayList<Song> songList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.songList = songList;
    }

    public class ThirdPageViewHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv2, tv3;
        ImageView imageView;
        LinearLayout lvSongOnListItem;

        public ThirdPageViewHolder(View v) {
            super(v);
            lvSongOnListItem = (LinearLayout) v.findViewById(R.id.songOnListItem);
            tv1 = (TextView) v.findViewById(R.id.song_title);
            tv2 = (TextView) v.findViewById(R.id.song_artist);
            tv3 = (TextView) v.findViewById(R.id.song_duration);
            imageView = (ImageView) v.findViewById(R.id.list_avatar);
        }
    }

    @Override
    public ThirdPageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        //Initilize media player
        mPlayer = new MediaPlayer();
        return new ThirdPageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ThirdPageViewHolder holder, int position) {
        Bitmap songImg = songList.get(position).getSongArtImage();
        if (songImg == null) {
            songImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_identify);
        }
        holder.tv1.setText(songList.get(position).getSongDisplayName());
        holder.tv2.setText(songList.get(position).getSongArtist());
        holder.tv3.setText(songList.get(position).getSongDuration());
        holder.imageView.setImageBitmap(songImg);
        holder.lvSongOnListItem.setOnClickListener(clickListener);
        holder.lvSongOnListItem.setTag(holder);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ThirdPageViewHolder vholder = (ThirdPageViewHolder) v.getTag();
            int position = vholder.getPosition();
            AppsHelper.currentSongIndex = position;
         //   AppsHelper.playerControl.isPlaying = false;
        //    AppsHelper.playerControl.play();
            AppsHelper.aContext.startService(new Intent(PlayerServices.ACTION_PLAY_LIST));
        }
    };


    @Override
    public int getItemCount() {
        return songList.size();
    }
}
