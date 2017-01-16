package me.mamun.db;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.Song;

public class DBHelper extends SQLiteOpenHelper {

	// me.mamun.db instance
	private static DBHelper sDBInstance;
	// DB name
	public static final String DB_NAME = "song_list_db";
	public static String DB_PATH;
	public static final int DB_VERSION = 1;

	// Category Table and field
	public static final String MUSIC_TRUCK_TABLE = "music_trucks";
	public static final String KEY_ID_FIELD = "_id";
	public static final String DISPLAY_NAME_FIELD = "display_name";
	public static final String ARTIST_NAME_FIELD = "artist_name";
	public static final String ALBUM_NAME_FIELD = "album_name";
	public static final String FOLDER_NAME_FIELD = "folder_name";
	public static final String SONG_PATH_FIELD = "song_path";
	public static final String SONG_ART_IMAGE_FIELD = "song_art_image";
	public static final String SONG_DURATION_FIELD = "song_duration";
	public static final String SONG_STATUS_FIELD = "song_status";

	SQLiteDatabase database;
	Context context;

	private DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		this.database = this.getWritableDatabase();
	}
	public static DBHelper getDBInstance(Context context) {
		if (sDBInstance == null) {
			sDBInstance = new DBHelper(context);
		}
		return sDBInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String QUERY_STRING_CREATE_TABLE = "CREATE TABLE " + MUSIC_TRUCK_TABLE + "("
				+ KEY_ID_FIELD + " INTEGER PRIMARY KEY,"
				+ DISPLAY_NAME_FIELD + " TEXT,"
				+ ARTIST_NAME_FIELD + " TEXT,"
				+ ALBUM_NAME_FIELD + " TEXT,"
				+ FOLDER_NAME_FIELD + " TEXT,"
				+ SONG_PATH_FIELD + " TEXT,"
				+ SONG_ART_IMAGE_FIELD + " BLOB,"
				+ SONG_DURATION_FIELD + " TEXT,"
				+ SONG_STATUS_FIELD + " TEXT"
				+ ")";
		db.execSQL(QUERY_STRING_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String QUERY_STRING_DROP_TABLE = "DROP TABLE IF EXISTS " + MUSIC_TRUCK_TABLE;
		db.execSQL(QUERY_STRING_DROP_TABLE);
		onCreate(db);
	}

	// Adding new Song
	public void addSongItem(ArrayList<Song> songList) {
		Bitmap artImsge = null;
		if (songList != null) {
			for (int i = 0;i < songList.size();i++) {
				//Bitmap convert to byte type
				artImsge = songList.get(i).getSongArtImage();

				ContentValues values = new ContentValues();
				values.put(DISPLAY_NAME_FIELD, songList.get(i).getSongDisplayName());
				values.put(ARTIST_NAME_FIELD, songList.get(i).getSongArtist());
				values.put(ALBUM_NAME_FIELD, songList.get(i).getSongAlbum());
				values.put(FOLDER_NAME_FIELD, songList.get(i).getSongFolderName());
				values.put(SONG_PATH_FIELD, songList.get(i).getSongPath());
				if (artImsge != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					artImsge.compress(Bitmap.CompressFormat.PNG, 100, bos);
					byte[] artImsgeToByte = bos.toByteArray();
					values.put(SONG_ART_IMAGE_FIELD, artImsgeToByte);
				} else {
					values.put(SONG_ART_IMAGE_FIELD, "");
				}
				values.put(SONG_DURATION_FIELD, songList.get(i).getSongDuration());
				values.put(SONG_STATUS_FIELD,1);
				geDatabase().insert(MUSIC_TRUCK_TABLE, null, values);
			}
			closeDatabase();
		}
	}

	private SQLiteDatabase geDatabase() {
		return this.database;
	}

	// Closing database connection
	private void closeDatabase() {
		if (this.database != null) {
			this.database.close();
		}
	}

	public ArrayList<Song> getAllSongs() {
		String displayName, artistName, songDuration, songPath, albumName, folderName;
		Bitmap songArtImage;
		ArrayList<Song> songList = new ArrayList<Song>();
		String QUERY_STRING = "SELECT * FROM " + MUSIC_TRUCK_TABLE;

		Cursor cursor = geDatabase().rawQuery(QUERY_STRING, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(SONG_ART_IMAGE_FIELD));
				Bitmap blobToBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

				displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_FIELD));
				artistName = cursor.getString(cursor.getColumnIndex(ARTIST_NAME_FIELD));
				albumName = cursor.getString(cursor.getColumnIndex(ALBUM_NAME_FIELD));
				songDuration = cursor.getString(cursor.getColumnIndex(SONG_DURATION_FIELD));
				songArtImage = blobToBitmap;
				songPath = cursor.getString(cursor.getColumnIndex(SONG_PATH_FIELD));
				folderName =  cursor.getString(cursor.getColumnIndex(FOLDER_NAME_FIELD));

				songList.add(new Song(displayName, artistName, albumName, songDuration, songArtImage, songPath, folderName));
				AppsHelper.artistList.add(artistName);
				AppsHelper.albumList.add(albumName);
				AppsHelper.folderList.add(folderName);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return songList;
	}
}
