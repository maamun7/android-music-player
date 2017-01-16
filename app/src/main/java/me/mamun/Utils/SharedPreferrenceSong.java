package me.mamun.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mamun on 5/20/2016.
 */
public class SharedPreferrenceSong {

    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public static final String PREFS_NAME = "MUSIC_APP";
    public static final String SONG_LIST = "Song_List";

    public SharedPreferrenceSong() {}

    /*
    public SharedPreferrenceSong(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    *//**
     * Converts the provided ArrayList<String>
     * into a JSONArray and saves it as a single
     * string in the apps shared preferences
     * @param String key Preference key for SharedPreferences
     * @param array ArrayList<String> containing the list items
     *//*
    public void saveArray(String key, ArrayList<Song> array) {
        JSONArray jArray = new JSONArray(array);
        editor.remove(key);
        editor.putString(key, jArray.toString());
        editor.commit();
    }

    public void saveToSharedprefference(String key, ArrayList<Song> songList) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(songList);
        editor.putString(key, jsonString);
        editor.commit();

        String jsonKey = prefs.getString(key, "");
        Log.d("keyy", " >> " + jsonKey);
    }

    public ArrayList<Song> getSongList(String key) {
        ArrayList<Song> songList;
        Gson gson = new Gson();
        String jsonKey = prefs.getString(key, "");
        Type type = new TypeToken <ArrayList<Song>> () {}.getType();
        songList = gson.fromJson(jsonKey, type);
        return songList;
    }

    // Get a default array in the event that there is no array
    // saved or a JSONException occurred
    private ArrayList<String> getDefaultArray() {
        ArrayList<String> array = new ArrayList<String>();
        array.add("Example 1");
        array.add("Example 2");
        array.add("Example 3");
        return array;
    }
*/
    public void saveSongList(Context context, ArrayList<Song> songList) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonSongListString = gson.toJson(songList);
        editor.putString(SONG_LIST, jsonSongListString);
        editor.commit();
    }

    public ArrayList<Song> getSongList(Context context) {
        SharedPreferences settings;
        List<Song> songList;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(SONG_LIST)) {
            String jsonString = settings.getString(SONG_LIST, null);
            Gson gson = new Gson();
            Song[] songItems = gson.fromJson(jsonString, Song[].class);
            songList = Arrays.asList(songItems);
            songList = new ArrayList<Song>(songList);
        } else
            return null;
        return (ArrayList<Song>) songList;
    }
}
