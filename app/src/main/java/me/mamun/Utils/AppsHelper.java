package me.mamun.Utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.view.ViewPager;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;

import me.mamun.adapter.ViewPagerAdapter;

/**
 * Created by Mamun on 4/8/2016.
 */
public class AppsHelper {
    public static final MediaPlayer mPlayer = new MediaPlayer();
    public AppsHelper() {}
    public static ViewPagerAdapter vpAdapter;
    public static ViewPager vPager;
    public List<String> myList = new ArrayList<String>();
    public static ArrayList<Song> allSongList;
    public static ArrayList<Song> defaultSongList;
    public static Context aContext;
    public static int currentSongIndex = 0;
    public static PlayerButtonControl playerControl;

    public static HashSet artistList = new HashSet();
    public static HashSet albumList = new HashSet();
    public static HashSet folderList = new HashSet();

    //3rd page
    //public static ArrayList<Song> thirdPageList;
    public static String searchKey;
    public static String searchKeyType;


    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


}
