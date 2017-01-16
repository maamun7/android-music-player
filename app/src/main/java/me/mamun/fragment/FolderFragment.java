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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.Artist;
import me.mamun.Utils.Folder;
import me.mamun.adapter.ArtistAdapter;
import me.mamun.adapter.FolderAdapter;
import me.mamun.mplayer.MainActivity;
import me.mamun.mplayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class FolderFragment extends Fragment {
    private TextView folder, totalSong;
    RecyclerView folderList;
    FolderAdapter folderAdapter;

    public FolderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_folder, container, false);
        ArrayList<Folder> folderList = new ArrayList<Folder>();
        Bitmap folderArtImg = null;
        Iterator itr = AppsHelper.folderList.iterator();
        while (itr.hasNext()) {
            String folderName = itr.next().toString();
            int totalSong = 0;
            for (int j = 0; j <= AppsHelper.allSongList.size() - 1; j++) {
                if (AppsHelper.allSongList.get(j).getSongFolderName().equals(folderName)) {
                    totalSong++;
                    if (folderArtImg == null) {
                        folderArtImg = AppsHelper.allSongList.get(j).getSongArtImage();
                    }
                }
            }
            if (folderArtImg == null) {
                folderArtImg = BitmapFactory.decodeResource(AppsHelper.aContext.getResources(), R.drawable.main_folder_simple);
            }
            folderList.add(new Folder(folderName, totalSong, folderArtImg));
            folderArtImg = null;
        }

        this.folderList = (RecyclerView) v.findViewById(R.id.rv_folder_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        this.folderList.setHasFixedSize(true);
        // use a linear layout manager
        this.folderList.setLayoutManager(new LinearLayoutManager(AppsHelper.aContext));

        // specify an adapter (see also next example)
        folderAdapter = new FolderAdapter(folderList);
        this.folderList.setAdapter(folderAdapter);
        return v;
    }

}

