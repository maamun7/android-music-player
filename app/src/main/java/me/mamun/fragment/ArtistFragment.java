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
import me.mamun.adapter.ArtistAdapter;
import me.mamun.mplayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class ArtistFragment extends Fragment {
    private TextView artist, totalSong;
    RecyclerView artistList;
    ArtistAdapter arTistAdapter;

    public ArtistFragment() {
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
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        ArrayList<Artist> arTistList = new ArrayList<Artist>();
        Bitmap artistArtImg = null;
        Iterator itr = AppsHelper.artistList.iterator();
        while (itr.hasNext()) {
            String artistName = itr.next().toString();
            int totalSong = 0;
            for (int j = 0; j <= AppsHelper.allSongList.size() - 1; j++) {
                if (AppsHelper.allSongList.get(j).getSongArtist().equals(artistName)) {
                    totalSong++;
                    if (artistArtImg == null) {
                        artistArtImg = AppsHelper.allSongList.get(j).getSongArtImage();
                    }
                }
            }
            if (artistArtImg == null) {
                artistArtImg = BitmapFactory.decodeResource(AppsHelper.aContext.getResources(), R.drawable.main_artist_simple);
            }
            arTistList.add(new Artist(artistName, totalSong, artistArtImg));
            artistArtImg = null;
        }

        artistList = (RecyclerView) v.findViewById(R.id.rv_artist_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        artistList.setHasFixedSize(true);
        // use a linear layout manager
        artistList.setLayoutManager(new LinearLayoutManager(AppsHelper.aContext));

        // specify an adapter (see also next example)
        arTistAdapter = new ArtistAdapter(arTistList);
        artistList.setAdapter(arTistAdapter);
        return v;
    }
}
