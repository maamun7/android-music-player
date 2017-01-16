package me.mamun.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

import me.mamun.mplayer.R;

/**
 * Created by Mamun on 10-Apr-16.
 */
public class PlayerButtonControl implements MediaPlayer.OnCompletionListener{
    public boolean isPlaying = false;
    public boolean isRepeat = false;
    public boolean isShuffle = false;
    private ImageButton imgBtnPlayPause,imgBtnRepeat,imgBtnShuffle;
    private ImageView truckImage;
    private TextView songTitleInPlayer, songArtistInPlayer, songTotalDurationLabel, songCurrentDurationLabel;
    private SeekBar songProgressBar;
    private Handler mHandler = new Handler();
    MediaPlayer mPlayer;

    public PlayerButtonControl() {
        mPlayer = AppsHelper.mPlayer;
        Activity mActivity = (Activity) AppsHelper.aContext;
        imgBtnPlayPause = (ImageButton) mActivity.findViewById(R.id.imgBtnPlay);
        imgBtnRepeat = (ImageButton) mActivity.findViewById(R.id.imgBtnRepeat);
        imgBtnShuffle = (ImageButton) mActivity.findViewById(R.id.imgBtnShuffle);
        truckImage = (ImageView) mActivity.findViewById(R.id.ivTruckImg);
        songTitleInPlayer = (TextView) mActivity.findViewById(R.id.tv_control_panel_title_main);
        songArtistInPlayer = (TextView) mActivity.findViewById(R.id.tv_control_panel_artist_main);
        songProgressBar = (SeekBar) mActivity.findViewById(R.id.skb_song_progess_view);
        songTotalDurationLabel = (TextView) mActivity.findViewById(R.id.tvDurationTotalValue);
        songCurrentDurationLabel = (TextView) mActivity.findViewById(R.id.tvDurationChangeValue);
        mPlayer.setOnCompletionListener(this); // Important
        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mPlayer.getDuration();
                int currentPosition = AppsHelper.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mPlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });
    }

    public void play() {
        String songPath = "";
        if (AppsHelper.defaultSongList.size() > 0) {
            songPath = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongPath();
        }
        if (isPlaying == false) {
            try {
                mPlayer.reset();
                mPlayer.setScreenOnWhilePlaying(true);
                mPlayer.setDataSource(songPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPlayer != null) {
                mPlayer.start();
            }
            setPlayedState();
            // Updating progress bar
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            updateProgressBar();
        } else {
            mPlayer.pause();
            setPauseState();
        }

    }

    public void stop() {
        mPlayer.stop();
        mPlayer.release();
        setPauseState();
    }

    public void next() {
        if (AppsHelper.currentSongIndex < (AppsHelper.defaultSongList.size() - 1)) {
            AppsHelper.currentSongIndex = AppsHelper.currentSongIndex + 1;
            AppsHelper.playerControl.isPlaying = false;
            play();
        } else {
            // AppsHelper.currentSongIndex = 0;
            AppsHelper.playerControl.isPlaying = true;
            play();
        }
    }

    public void previous() {
        if (AppsHelper.currentSongIndex > 0) {
            AppsHelper.currentSongIndex = AppsHelper.currentSongIndex - 1;
            AppsHelper.playerControl.isPlaying = false;
            play();
        } else {
            AppsHelper.currentSongIndex = AppsHelper.defaultSongList.size() - 1;
            AppsHelper.playerControl.isPlaying = false;
            play();
        }
    }

    public void repeat() {
        if (isRepeat) {
            isRepeat = false;
            //Set button pressed
            // imgBtnRepeat.setImageResource(R.drawable.btn_repeat);
        } else {
            // make repeat to true
            isRepeat = true;
            // make shuffle to false
            isShuffle = false;
            //Set button
          //  imgBtnRepeat.setImageResource(R.drawable.btn_repeat_focused);
          //  imgBtnShuffle.setImageResource(R.drawable.btn_shuffle);
        }
    }

    public void shuffle() {
        if(isShuffle){
            isShuffle = false;
           // btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }else{
            // make repeat to true
            isShuffle= true;

            // make shuffle to false
            isRepeat = false;
          //  btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
           // btnRepeat.setImageResource(R.drawable.btn_repeat);
        }
    }

    private void setPlayedState() {
        isPlaying = true;
        String songPath = "";
        String songTitle = "";
        String songArtist = "";
        String songDuration = "";
        Bitmap songImage = null;
        if (AppsHelper.defaultSongList.size() > 0) {
            songTitle = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongDisplayName();
            songArtist = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongArtist();
            songImage = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongArtImage();
            songDuration = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongDuration();
            imgBtnPlayPause.setImageResource(R.drawable.img_btn_control_pause);
            songTitleInPlayer.setText(songTitle);
            songArtistInPlayer.setText(songArtist);
            truckImage.setImageBitmap(songImage);
            songTotalDurationLabel.setText(songDuration);
        }
    }

    private void setPauseState() {
        isPlaying = false;
        imgBtnPlayPause.setImageResource(R.drawable.img_btn_control_play);
    }

    /**
     * Update timer on seekbar
     */

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();

            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+AppsHelper.milliSecondsToTimer(currentDuration));
            // Updating progress bar
            int progress = (int) (AppsHelper.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if(isRepeat){
            AppsHelper.currentSongIndex = AppsHelper.currentSongIndex;
            AppsHelper.playerControl.isPlaying = false;
            play();
        } else if(isShuffle){
            AppsHelper.playerControl.isPlaying = false;
            // shuffle is on - play a random song
            Random rand = new Random();
            AppsHelper.currentSongIndex = rand.nextInt((AppsHelper.defaultSongList.size() - 1) - 0 + 1) + 0;
            play();
        } else{
            next();
        }
    }
}
