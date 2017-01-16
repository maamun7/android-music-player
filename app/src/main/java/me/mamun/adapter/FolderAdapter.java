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
import me.mamun.Utils.Folder;
import me.mamun.fragment.DynamicListFragment;
import me.mamun.mplayer.R;

/**
 * Created by Mamun on 3/18/2016.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private ArrayList<Folder> folderList;

    public FolderAdapter(ArrayList<Folder> folderList) {
        this.folderList = folderList;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        private TextView folderName, folderSongTotal;
        private ImageView folderArtImg;
        private LinearLayout lnlSecondPageList;

        public FolderViewHolder(View v) {
            super(v);
            lnlSecondPageList = (LinearLayout) v.findViewById(R.id.second_page_list);
            folderName = (TextView) v.findViewById(R.id.list_top_text);
            folderSongTotal = (TextView) v.findViewById(R.id.list_bottom_text);
            folderArtImg = (ImageView) v.findViewById(R.id.art_img);
        }
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_page_list, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.folderName.setText(folder.getFolderName());
        holder.folderSongTotal.setText(folder.getTotalSong() + " musics");
        holder.folderArtImg.setImageBitmap(folder.getFolderArtImage());
        holder.lnlSecondPageList.setOnClickListener(clickListener);
        holder.lnlSecondPageList.setTag(holder);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FolderViewHolder vholder = (FolderViewHolder) v.getTag();
            int position = vholder.getPosition();
            AppsHelper.searchKey = folderList.get(position).getFolderName();
            AppsHelper.searchKeyType = "Folder";
            if (AppsHelper.searchKeyType.equals("Folder") && !AppsHelper.searchKey.equals("")) {
                AppsHelper.vpAdapter.repalceFragment(2, new DynamicListFragment());
                AppsHelper.vPager.setCurrentItem(2, true);
            }
        }
    };

    @Override
    public int getItemCount() {
        return folderList.size();
    }

}
