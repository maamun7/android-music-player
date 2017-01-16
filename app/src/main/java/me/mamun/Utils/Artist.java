package me.mamun.Utils;

import android.graphics.Bitmap;

/**
 * Created by Mamun on 12-Apr-16.
 */
public class Artist {

    private String artistName;
    private int total_song;
    private Bitmap artistArtImage;

    public Artist(String artistName, int total_song, Bitmap artistArtImage) {
        this.artistName = artistName;
        this.total_song = total_song;
        this.artistArtImage = artistArtImage;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getTotalSong() {
        return total_song;
    }

    public Bitmap getArtistArtImage() {
        return artistArtImage;
    }
}
