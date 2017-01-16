package me.mamun.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.mamun.Utils.AppsHelper;
import me.mamun.mplayer.EquilizerActivity;
import me.mamun.mplayer.MainActivity;
import me.mamun.mplayer.R;
import me.mamun.mplayer.SplashActivity;
import me.mamun.mplayer.VisualizerActivity;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FirstPageFragment extends Fragment implements View.OnClickListener {

    TextView tvCountArtistItem, tvCountMusicItem, tvCountAlbumItem, tvCountFolderItem;
    public LinearLayout lyAllMusic, lnlArtist, lnlAlbum, lnlFolder, lnlEquilizer, lnlMediaScan;
    FragmentManager fManager;
    SecondPageFragment pFragment;

    public FirstPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        initilizeControls(view);
        //Set text
        setAllText();
        //Click listener
        lyAllMusic.setOnClickListener(this);
        lnlArtist.setOnClickListener(this);
        lnlAlbum.setOnClickListener(this);
        lnlFolder.setOnClickListener(this);
        lnlEquilizer.setOnClickListener(this);
        lnlMediaScan.setOnClickListener(this);

        return view;
    }

    private void initilizeControls(View view){
        lyAllMusic = (LinearLayout) view.findViewById(R.id.allMusic);
        lnlArtist = (LinearLayout) view.findViewById(R.id.artistPortion);
        lnlAlbum = (LinearLayout) view.findViewById(R.id.albumPortion);
        lnlFolder = (LinearLayout) view.findViewById(R.id.folderPortion);
        lnlEquilizer = (LinearLayout) view.findViewById(R.id.lnlEquilizer);
        lnlMediaScan = (LinearLayout) view.findViewById(R.id.lnlMediaScan);
        tvCountMusicItem = (TextView) view.findViewById(R.id.tvCountMusicItem);
        tvCountArtistItem = (TextView) view.findViewById(R.id.countedArtistValue);
        tvCountAlbumItem = (TextView) view.findViewById(R.id.countedAlbumValue);
        tvCountFolderItem = (TextView) view.findViewById(R.id.tvCountedFolderValue);
    }

    private void setAllText() {
        tvCountMusicItem.setText(AppsHelper.allSongList.size() + " musics");
        tvCountArtistItem.setText(AppsHelper.artistList.size() + " artists");
        tvCountAlbumItem.setText(AppsHelper.albumList.size() + " albums");
        tvCountFolderItem.setText(AppsHelper.folderList.size() + " folders");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.allMusic:
                AppsHelper.vpAdapter.repalceFragment(2, new ThirdPageFragment());
                AppsHelper.vPager.setCurrentItem(2, true);
             //  Toast.makeText(getContext(), "msg msg "+AppsHelper.vpAdapter.getItem(2), Toast.LENGTH_SHORT).show();
                break;
            case R.id.artistPortion:
                AppsHelper.vpAdapter.repalceFragment(1, new ArtistFragment());
                AppsHelper.vPager.setCurrentItem(1, true);
                break;
            case R.id.albumPortion:
                AppsHelper.vpAdapter.repalceFragment(1, new AlbumFragment());
                AppsHelper.vPager.setCurrentItem(1, true);
                break;
            case R.id.folderPortion:
                AppsHelper.vpAdapter.repalceFragment(1, new FolderFragment());
                AppsHelper.vPager.setCurrentItem(1, true);
                break;
            case R.id.lnlEquilizer:
                Intent intent = new Intent(getActivity(),EquilizerActivity.class);
                startActivity(intent);
                break;
            case R.id.lnlMediaScan:
                Intent intn = new Intent(getActivity(),VisualizerActivity.class);
                startActivity(intn);
                break;
            default:
                MainActivity.viewPager.setCurrentItem(0, false);
                break;
        }

    }
}
