package me.mamun.mplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;

import me.mamun.Utils.AppsHelper;
import me.mamun.adapter.ViewPagerAdapter;
import me.mamun.fragment.FixedSpeedScroller;
import me.mamun.fragment.FirstPageFragment;
import me.mamun.fragment.SecondPageFragment;
import me.mamun.fragment.ThirdPageFragment;
import me.mamun.player_control.Constants;
import me.mamun.player_control.PlayerIntentReceiver;
import me.mamun.player_control.PlayerServices;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    public static Context context;
    private ImageButton imgBtnPlayPause, imgBtnNext, imgBtnPrevious, imgBtnRepeat, imgBtnShuffle;
    TextView totalFolder;
    LayoutInflater inflater;
    PlayerIntentReceiver playerIntentReceiver;
    private Intent serviceIntent;

    private ImageView truckImage;
    private TextView songTitleInPlayer, songArtistInPlayer, songTotalDurationLabel, songCurrentDurationLabel;
    private SeekBar songProgressBar;
    int totalDuration = 0;
    final private String LOG_TAG = "LOGG_TAGG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();
        setupViewPager();
        //set context publicly
        setContext();
        //Change music progress
        changeMusicProgress();
    }

    private void setContext() {
        AppsHelper.aContext = this;
    }
    private void initControls() {
        imgBtnPlayPause = (ImageButton) findViewById(R.id.imgBtnPlay);
        imgBtnNext = (ImageButton) findViewById(R.id.imgBtnNext);
        imgBtnPrevious = (ImageButton) findViewById(R.id.imgBtnPrevious);
        imgBtnRepeat = (ImageButton) findViewById(R.id.imgBtnRepeat);
        imgBtnShuffle = (ImageButton) findViewById(R.id.imgBtnShuffle);

        truckImage = (ImageView) findViewById(R.id.ivTruckImg);
        songTitleInPlayer = (TextView) findViewById(R.id.tv_control_panel_title_main);
        songArtistInPlayer = (TextView) findViewById(R.id.tv_control_panel_artist_main);
        songTotalDurationLabel = (TextView) findViewById(R.id.tvDurationTotalValue);
        songCurrentDurationLabel = (TextView) findViewById(R.id.tvDurationChangeValue);

        songProgressBar = (SeekBar) findViewById(R.id.skb_song_progess_view);
    }

    private void setupViewPager() {
        AppsHelper.vPager = (ViewPager) findViewById(R.id.vp_container);
        AppsHelper.vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //First clear the adapter and then add new fragment
        AppsHelper.vpAdapter.listClear();
        AppsHelper.vpAdapter.addFragment(new FirstPageFragment());
        AppsHelper.vpAdapter.addFragment(new SecondPageFragment());
        AppsHelper.vpAdapter.addFragment(new ThirdPageFragment());
        AppsHelper.vPager.setAdapter(AppsHelper.vpAdapter);

        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(this);
            mScroller.set(AppsHelper.vPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        //Bind the title indicator to the adapter
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circleIndicator);
        circlePageIndicator.setViewPager(AppsHelper.vPager);

        imgBtnPlayPause.setOnClickListener(this);
        imgBtnNext.setOnClickListener(this);
        imgBtnPrevious.setOnClickListener(this);
        imgBtnRepeat.setOnClickListener(this);
        imgBtnShuffle.setOnClickListener(this);
    }

    private void changeMusicProgress() {
        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               // Log.d(LOG_TAG, "onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
               // Log.d(LOG_TAG, "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (totalDuration > 0) {
                Log.d(LOG_TAG, "onStopTrackingTouch -- inside");
                int currentPosition = AppsHelper.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                // mPlayer.seekTo(currentPosition);
                    Intent intent = new Intent(PlayerServices.ACTION_SET_PROGRESS).putExtra("current_pos", currentPosition);
                startService(intent);
                // update timer progress again
                updateProgressBar(intent);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent playerIntent) {
           // Log.d(LOG_TAG,"Progress:---");
            updateUI(playerIntent);
        }
    };


    private void updateUI(Intent playerIntent) {
        String songTitle = playerIntent.getStringExtra("SongTitle");
        String songArtist = playerIntent.getStringExtra("artistName");
        String isPlaying = playerIntent.getStringExtra("isPlaying");
        String duration = playerIntent.getStringExtra("duration");
        Bitmap songArtImg = (Bitmap) playerIntent.getParcelableExtra("songArtImg");

        if (isPlaying.equals("yes")) {
            imgBtnPlayPause.setImageResource(R.drawable.img_btn_control_pause);
        } else if (isPlaying.equals("no")) {
            imgBtnPlayPause.setImageResource(R.drawable.img_btn_control_play);
        }

        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
        songTitleInPlayer.setText(songTitle);
        songArtistInPlayer.setText(songArtist);
        truckImage.setImageBitmap(songArtImg);
        songTotalDurationLabel.setText(duration);
        updateProgressBar(playerIntent);
    }

    public void updateProgressBar(Intent progressIntent) {
        totalDuration = (int) progressIntent.getLongExtra("total_duration",0);
        long currentDuration = progressIntent.getLongExtra("curr_duration",0);
        int progress = (int) (AppsHelper.getProgressPercentage(currentDuration, totalDuration));
        // Log.d(LOG_TAG, "Progress:" + progress);
        songProgressBar.setProgress(progress);
        songCurrentDurationLabel.setText(""+AppsHelper.milliSecondsToTimer(currentDuration));
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(PlayerServices.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
       // unregisterReceiver(broadcastReceiver);
        //unregisterReceiver(brodcReceiver);
       //stopService(new Intent(this, PlayerServices.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnPlay:
                startService(new Intent(PlayerServices.ACTION_PLAY));
                break;
            case R.id.imgBtnNext:
                startService(new Intent(PlayerServices.ACTION_NEXT));
                break;
            case R.id.imgBtnPrevious:
                startService(new Intent(PlayerServices.ACTION_PREVIOUS));
                break;
            case R.id.imgBtnRepeat:
                startService(new Intent(PlayerServices.ACTION_REPEAT));
                break;
            case R.id.imgBtnShuffle:
                startService(new Intent(PlayerServices.ACTION_SHUFFLE));
                break;
        }
    }
}
