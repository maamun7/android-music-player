package me.mamun.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.mamun.Utils.AppsHelper;
import me.mamun.adapter.ThirdPageAdapter;
import me.mamun.mplayer.R;

public class ThirdPageFragment extends Fragment {
    RecyclerView recyclerView;
   // File file;
    ArrayList<String> absolutepath;

    public ThirdPageFragment() {
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
        View v = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.rcvAllSongList);

        ThirdPageAdapter adapter = new ThirdPageAdapter(getActivity(), AppsHelper.allSongList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }
}
