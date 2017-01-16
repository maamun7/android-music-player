package me.mamun.Utils;

import android.graphics.Bitmap;

/**
 * Created by Mamun on 4/6/2016.
 */
public class Song {
    private String songDisplayName;
    private String songArtist;
    private String songAlbum;
    private String songDuration;
    private String songPath;
    private String folder_name;
    private Bitmap songArtImage;

    public Song(String songDisplayName, String songArtist, String songAlbum, String songDuration, Bitmap songArtImage, String songPath, String folder_name) {
        this.songDisplayName = songDisplayName;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songDuration = songDuration;
        this.songArtImage = songArtImage;
        this.songPath = songPath;
        this.folder_name = folder_name;
    }

    public String getSongDisplayName() {
        return songDisplayName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public Bitmap getSongArtImage() {
        return songArtImage;
    }

    public String getSongPath() {
        return songPath;
    }

    public String getSongFolderName() {
        return folder_name;
    }

    public void setSongDisplayName(String songDisplayName) {
        this.songDisplayName = songDisplayName;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }
}
