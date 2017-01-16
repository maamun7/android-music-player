package me.mamun.Utils;

import android.graphics.Bitmap;

/**
 * Created by Mamun on 12-Apr-16.
 */
public class Album {

    private String albumName;
    private int total_song;
    private Bitmap artistArtImage;

    public Album(String albumName, int total_song, Bitmap artistArtImage) {
        this.albumName = albumName;
        this.total_song = total_song;
        this.artistArtImage = artistArtImage;
    }

    public String getAlbumName() {
        return albumName;
    }

    public int getTotalSong() {
        return total_song;
    }

    public Bitmap getArtistArtImage() {
        return artistArtImage;
    }
}
