package me.mamun.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.mamun.db.DBHelper;

/**
 * Created by Mamun on 4/6/2016.
 */
public class FilterAudioFile {
    Context context;
    Cursor aCursor;
    private String displayName, artistName, songDuration, songPath, albumName, folderName;
    private Bitmap songArtImage;
    DBHelper db;

    public FilterAudioFile(Context context) {
        this.context = context;
    }

    public ArrayList<Song> getAudioList() {

        ArrayList<Song> mSongsList = new ArrayList<Song>();
        String[] STAR = {"*"};  //it is projection you can modify it according to your need
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        String[] selectionArgs = null;
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";  // check for music files

        Cursor mCursor = context.getContentResolver().query(allSongsUri, STAR, selection, selectionArgs, sortOrder);


       // int count = mCursor.getCount();
       // System.out.println("total no of songs are=" + count);
        while (mCursor.moveToNext()) {
            displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            artistName = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            albumName = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            songDuration = convertDuration(mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            songArtImage = getAlbumArt(mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
            int column_index  = mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            songPath =  mCursor.getString(column_index);
            File pathFile = new File(songPath);
            folderName =  pathFile.getParent();
            folderName =  folderName.substring(folderName.lastIndexOf("/") + 1);
          //  Log.d("tag", folderName);
            mSongsList.add(new Song(displayName, artistName, albumName, songDuration, songArtImage, songPath, folderName));
            AppsHelper.artistList.add(artistName);
            AppsHelper.albumList.add(albumName);
            AppsHelper.folderList.add(folderName);
        }
        mCursor.close();
        return mSongsList;
    }

    public String convertDuration(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        if (hours > 0) {
            return String.format("%02d:%02d:%02d",
                    hours,
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        } else {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }

    }

    public Bitmap getAlbumArt(Long album_id) {
        Bitmap bm = null;
       // Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_identify);
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }
}
