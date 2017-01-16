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
import android.widget.LinearLayout;
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
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SecondPageFragment extends Fragment {

    private TextView tvMusicNoOfMusic, tvRecentPlayNoOfMusic;
    private LinearLayout lnlAllMusics;
    public SecondPageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_second_page, container, false);
        initilizeControls(view);
        //Set text
        setAllText();
        lnlAllMusics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppsHelper.vpAdapter.repalceFragment(2, new ThirdPageFragment());
                AppsHelper.vPager.setCurrentItem(2, true);
            }
        });
        return view;
    }

    private void initilizeControls(View view){
        lnlAllMusics = (LinearLayout) view.findViewById(R.id.lnlAllMusics);
        tvMusicNoOfMusic = (TextView) view.findViewById(R.id.second_page_music_bottom_text);
        tvRecentPlayNoOfMusic = (TextView) view.findViewById(R.id.second_page_recent_play_bottom_text);
    }

    private void setAllText(){
        tvMusicNoOfMusic.setText(AppsHelper.allSongList.size() + " musics");
        tvRecentPlayNoOfMusic.setText(0+ " musics");
    }

}
