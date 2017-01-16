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

import me.mamun.Utils.Album;
import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.Artist;
import me.mamun.adapter.AlbumAdapter;
import me.mamun.adapter.ArtistAdapter;
import me.mamun.mplayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class AlbumFragment extends Fragment{
    private TextView artist, totalSong;
    RecyclerView albumList;
    AlbumAdapter albumAdapter;

    public AlbumFragment() {
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
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        ArrayList<Album> albumList = new ArrayList<Album>();
        Bitmap albumArtImg = null;
        Iterator itr = AppsHelper.albumList.iterator();
        while (itr.hasNext()) {
            String albumName = itr.next().toString();
            int totalSong = 0;
            for (int j = 0; j <= AppsHelper.allSongList.size() - 1; j++) {
                if (AppsHelper.allSongList.get(j).getSongAlbum().equals(albumName)) {
                    totalSong++;
                    if (albumArtImg == null) {
                        albumArtImg = AppsHelper.allSongList.get(j).getSongArtImage();
                    }
                }
            }
            if (albumArtImg == null) {
                albumArtImg = BitmapFactory.decodeResource(AppsHelper.aContext.getResources(), R.drawable.main_album_simple);
            }
            albumList.add(new Album(albumName, totalSong, albumArtImg));
            albumArtImg = null;
        }

        this.albumList = (RecyclerView) v.findViewById(R.id.rv_album_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        this.albumList.setHasFixedSize(true);

        // use a linear layout manager
        this.albumList.setLayoutManager(new LinearLayoutManager(AppsHelper.aContext));

        // specify an adapter (see also next example)
        albumAdapter = new AlbumAdapter(albumList);
        this.albumList.setAdapter(albumAdapter);
        return v;
    }
}
