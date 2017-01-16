package me.mamun.Utils;

import android.graphics.Bitmap;

/**
 * Created by Mamun on 12-Apr-16.
 */
public class Folder {

    private String folderName;
    private int total_song;
    private Bitmap folderArtImage;

    public Folder(String folderName, int total_song, Bitmap folderArtImage) {
        this.folderName = folderName;
        this.total_song = total_song;
        this.folderArtImage = folderArtImage;
    }

    public String getFolderName() {
        return folderName;
    }

    public int getTotalSong() {
        return total_song;
    }

    public Bitmap getFolderArtImage() {
        return folderArtImage;
    }
}
