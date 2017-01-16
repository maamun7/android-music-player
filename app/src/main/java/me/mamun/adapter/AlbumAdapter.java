package me.mamun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.mamun.Utils.Album;
import me.mamun.Utils.AppsHelper;
import me.mamun.fragment.DynamicListFragment;
import me.mamun.mplayer.R;

/**
 * Created by Mamun on 3/18/2016.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private ArrayList<Album> albumList;

    public AlbumAdapter(ArrayList<Album> albumList) {
        this.albumList = albumList;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView albumName, albumSongTotal;
        private ImageView albumArtImg;
        private LinearLayout lnlSecondPageList;

        public AlbumViewHolder(View v) {
            super(v);
            lnlSecondPageList = (LinearLayout) v.findViewById(R.id.second_page_list);
            albumName = (TextView) v.findViewById(R.id.list_top_text);
            albumSongTotal = (TextView) v.findViewById(R.id.list_bottom_text);
            albumArtImg = (ImageView) v.findViewById(R.id.art_img);
        }
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_page_list, parent, false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.albumName.setText(album.getAlbumName());
        holder.albumSongTotal.setText(album.getTotalSong() + " musics");
        holder.albumArtImg.setImageBitmap(album.getArtistArtImage());
        holder.lnlSecondPageList.setOnClickListener(clickListener);
        holder.lnlSecondPageList.setTag(holder);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlbumViewHolder vholder = (AlbumViewHolder) v.getTag();
            int position = vholder.getPosition();
            AppsHelper.searchKey = albumList.get(position).getAlbumName();
            AppsHelper.searchKeyType = "Album";
            if (AppsHelper.searchKeyType.equals("Album") && !AppsHelper.searchKey.equals("")) {
                AppsHelper.vpAdapter.repalceFragment(2, new DynamicListFragment());
                AppsHelper.vPager.setCurrentItem(2, true);
            }
        }
    };

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
