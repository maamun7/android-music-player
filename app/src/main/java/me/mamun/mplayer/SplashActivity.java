package me.mamun.mplayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import me.mamun.db.DBHelper;
import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.FilterAudioFile;
import me.mamun.Utils.SharedPreferrenceSong;
import me.mamun.Utils.Song;


public class SplashActivity extends AppCompatActivity {

    Context context;
    SharedPreferrenceSong sps;
    private final String SONG_LIST_KEY = "all_song_list";
    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the Status Bar on Android version is lower than Jellybean
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //Hide the Status Bar on Android 4.1 and Higher
        View decorView = getWindow().getDecorView();
        //Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.

        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        context = this;
        db = DBHelper.getDBInstance(context);

        //Transfer to another activity after filtering all audio file
        if (isSongInDB()) {
            setIntent();
        } else {
            new FilterAudioFilte().execute();
            // new TimerRun().start();
           // setIntent();
        }
    }

    private void filterAudioSong() {
        FilterAudioFile audioFile = new FilterAudioFile(this);
        AppsHelper.allSongList = audioFile.getAudioList();
        AppsHelper.defaultSongList = audioFile.getAudioList();

        //Save to database
        db = DBHelper.getDBInstance(AppsHelper.aContext);
        db.addSongItem(AppsHelper.allSongList);
    }

    private class FilterAudioFilte extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            //Filter audio song and save to array list
            filterAudioSong();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setIntent();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private void setIntent(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isSongInDB(){
        ArrayList<Song> songList;
        songList = db.getAllSongs();
        if (songList.size() > 0) {
            AppsHelper.allSongList = songList;
            AppsHelper.defaultSongList = songList;
            return true;
        } else {
            return false;
        }
    }
}
