package me.mamun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.Artist;
import me.mamun.fragment.DynamicListFragment;
import me.mamun.mplayer.R;

/**
 * Created by Mamun on 3/18/2016.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private ArrayList<Artist> artistList;

    public ArtistAdapter(ArrayList<Artist> artistList) {
        this.artistList = artistList;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName, artistSongTotal;
        private ImageView artistArtImg;
        private LinearLayout lnlSecondPageList;

        public ArtistViewHolder(View v) {
            super(v);
            lnlSecondPageList = (LinearLayout) v.findViewById(R.id.second_page_list);
            artistName = (TextView) v.findViewById(R.id.list_top_text);
            artistSongTotal = (TextView) v.findViewById(R.id.list_bottom_text);
            artistArtImg = (ImageView) v.findViewById(R.id.art_img);
        }
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_page_list, parent, false);
        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.artistName.setText(artist.getArtistName());
        holder.artistSongTotal.setText(artist.getTotalSong() + " musics");
        holder.artistArtImg.setImageBitmap(artist.getArtistArtImage());
        holder.lnlSecondPageList.setOnClickListener(clickListener);
        holder.lnlSecondPageList.setTag(holder);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArtistViewHolder vholder = (ArtistViewHolder) v.getTag();
            int position = vholder.getPosition();
            AppsHelper.searchKey = artistList.get(position).getArtistName();
            AppsHelper.searchKeyType = "Artist";
            if (AppsHelper.searchKeyType.equals("Artist") && !AppsHelper.searchKey.equals("")) {
                AppsHelper.vpAdapter.repalceFragment(2, new DynamicListFragment());
                AppsHelper.vPager.setCurrentItem(2, true);
            }
        }
    };

    @Override
    public int getItemCount() {
        return artistList.size();
    }

}
