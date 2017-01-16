package me.mamun.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.Song;
import me.mamun.adapter.DynamicListAdapter;
import me.mamun.mplayer.R;

public class DynamicListFragment extends Fragment {
    private ArrayList<Song> mSongsList = new ArrayList<Song>();
    private String displayName, artistName, songDuration, songPath, albumName;
    private Bitmap songArtImage;
    RecyclerView rvDynamicSongList;
    DynamicListAdapter dynamicSongListAdapter;
    public DynamicListFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if search key item and search key type is empty then go to 2nd page agin
        if (AppsHelper.searchKeyType.equals("") && AppsHelper.searchKey.equals("")) {
            AppsHelper.vPager.setCurrentItem(1, true);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dynamic_song_list, container, false);

        switch (AppsHelper.searchKeyType) {
            case "Artist":
                for (int i = 0; i <= AppsHelper.allSongList.size() - 1; i++) {
                    if (AppsHelper.allSongList.get(i).getSongArtist().equals(AppsHelper.searchKey)) {
                        add_to_list(i);
                    }
                }
                break;
            case "Album":
                for (int j = 0; j <= AppsHelper.allSongList.size() - 1; j++) {
                    if (AppsHelper.allSongList.get(j).getSongAlbum().equals(AppsHelper.searchKey)) {
                        add_to_list(j);
                    }
                }
                break;
            case "Folder":
                for (int j = 0; j <= AppsHelper.allSongList.size() - 1; j++) {
                    if (AppsHelper.allSongList.get(j).getSongFolderName().equals(AppsHelper.searchKey)) {
                        add_to_list(j);
                    }
                }
                break;
            default:
                break;
        }
        rvDynamicSongList = (RecyclerView) view.findViewById(R.id.dynamic_song_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvDynamicSongList.setHasFixedSize(true);
        // use a linear layout manager
        rvDynamicSongList.setLayoutManager(new LinearLayoutManager(AppsHelper.aContext));

        // specify an adapter (see also next example)
        dynamicSongListAdapter = new DynamicListAdapter(mSongsList);
        rvDynamicSongList.setAdapter(dynamicSongListAdapter);
        return view;
    }

    private void add_to_list(int position){
        displayName = AppsHelper.allSongList.get(position).getSongDisplayName();
        artistName = AppsHelper.allSongList.get(position).getSongArtist();
        albumName = AppsHelper.allSongList.get(position).getSongAlbum();
        songDuration = AppsHelper.allSongList.get(position).getSongDuration();
        songArtImage = AppsHelper.allSongList.get(position).getSongArtImage();
        if (songArtImage == null) {
            songArtImage = BitmapFactory.decodeResource(AppsHelper.aContext.getResources(), R.drawable.default_album_identify);
        }
        songPath =  AppsHelper.allSongList.get(position).getSongPath();
        mSongsList.add(new Song(displayName, artistName, albumName, songDuration, songArtImage, songPath, ""));
    }
}
