package me.mamun.player_control;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import me.mamun.Utils.AppsHelper;
import me.mamun.mplayer.MainActivity;
import me.mamun.mplayer.R;

public class PlayerServices extends Service implements OnCompletionListener, OnErrorListener, OnPreparedListener, MusicFocusable {

    private final String LOG_TAG = "LOGG_TAGG";
    public static final String ACTION_TOGGLE_PLAYBACK = "me.mamun.player_control.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "me.mamun.player_control.action.PLAY";
    public static final String ACTION_PLAY_LIST = "me.mamun.player_control.action.PLAY_FROM_LIST";
    public static final String ACTION_PAUSE = "me.mamun.player_control.action.PAUSE";
    public static final String ACTION_STOP = "me.mamun.player_control.action.STOP";
    public static final String ACTION_NEXT = "me.mamun.player_control.action.NEXT";
    public static final String ACTION_PREVIOUS = "me.mamun.player_control.action.PREVIOUS";
    public static final String ACTION_REPEAT = "me.mamun.player_control.action.REPEAT";
    public static final String ACTION_SHUFFLE = "me.mamun.player_control.action.SHUFFLE";
    public static final String ACTION_SET_PROGRESS = "me.mamun.player_control.action.SET_PROGRESS";
    public static final String BROADCAST_ACTION = "me.mamun.player_control.action.DISPLAYEVENT";
    public static final String ACTION_URL = "me.mamun.player_control.action.action.URL";

    MediaPlayer mPlayer = null;
    final int NOTIFICATION_ID = 101;

    // indicates the state our service:
    enum State { Retrieving, Stopped, Preparing, Playing, Paused };
    State mState = State.Stopped;
    boolean isRepeat = false;
    boolean isShuffle = false;

    enum PauseReason {UserRequest, FocusLoss };
    PauseReason mPauseReason = PauseReason.UserRequest;

    // do we have audio focus?
    enum AudioFocus {NoFocusNoDuck, NoFocusCanDuck,Focused };
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

    // our AudioFocusHelper object, if it's available (it's available on SDK level >= 8)
    // If not available, this will be null. Always check for null before using!
    AudioFocusHelper mAudioFocusHelper = null;

    Notification mBuilder;

    public static final float DUCK_VOLUME = 0.1f;
    WifiLock mWifiLock;
    boolean mIsStreaming = false;
    RemoteViews notifySmallView;
    RemoteViews notifyExtendView;
    ComponentName mMediaButtonReceiverComponent;
    AudioManager mAudioManager;
    NotificationManager mNotificationManager;
    Bitmap defaultSongArt;
    String songTitle, artistName, albumName, songDuration;
    private Handler notifiChangeHandler = new Handler();
    private Handler UIhandler = new Handler();
    private Handler progressHandler = new Handler();
    Intent UiIntent;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_TOGGLE_PLAYBACK)) processTogglePlaybackRequest();
        else if (action.equals(ACTION_PLAY)) processPlayRequest();
        else if (action.equals(ACTION_PLAY_LIST)) processPlayFromListRequest();
        else if (action.equals(ACTION_PAUSE)) processPauseRequest();
        else if (action.equals(ACTION_NEXT)) processNextRequest();
        else if (action.equals(ACTION_STOP)) processStopRequest();
        else if (action.equals(ACTION_PREVIOUS)) processPreviousRequest();
        else if (action.equals(ACTION_REPEAT)) processRepeatRequest();
        else if (action.equals(ACTION_SHUFFLE)) processShuffleRequest();
        else if (action.equals(ACTION_SET_PROGRESS)) processSetProgressRequest();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
      //  Log.i(LOG_TAG, "debug: Creating service");

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // create the Audio Focus Helper, if the Audio Focus feature is available (SDK 8 or above)
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);
        } else {
            mAudioFocus = AudioFocus.Focused;
            mMediaButtonReceiverComponent = new ComponentName(this, PlayerIntentReceiver.class);
        }

        //Initialize remote view
        notifySmallView = new RemoteViews(getPackageName(),R.layout.status_bar);
        notifyExtendView = new RemoteViews(getPackageName(),R.layout.status_bar_expanded);

        UiIntent = new Intent(BROADCAST_ACTION);
    }

    void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        } else {
            mPlayer.reset();
        }

    }

    private void processTogglePlaybackRequest() {
        if (mState == State.Paused || mState == State.Stopped) {
            processPlayRequest();
        } else {
            processPauseRequest();
        }
    }

    private void processPlayRequest() {
        String stsString = "";
        tryToGetAudioFocus();
        if (mState == State.Stopped) {
            mState = State.Playing;
            playRequest();
            stsString = "Playing";
        } else if (mState == State.Paused) {
            mState = State.Playing;
            configAndStartMediaPlayer();
            stsString = "Playing";
        } else if (mState == State.Playing) {
            processPauseRequest();
            stsString = "Paused";
        }
        Toast.makeText(this, "Music Player " + stsString, Toast.LENGTH_LONG).show();
    }

    private void processPlayFromListRequest() {
        playRequest();
    }

    private void playRequest() {
        relaxResources(false);
        mIsStreaming = false;
        if (AppsHelper.defaultSongList == null) {
            Toast.makeText(this, "No available music to play.", Toast.LENGTH_LONG).show();
            processStopRequest();
            return;
        }
        mState = State.Playing;

        createMediaPlayerIfNeeded();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // set the source of the media player a a content URI
        String songPath = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongPath();
        try {
            mPlayer.setDataSource(songPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Setting progressbar player
        /*

        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);


        //Set player control states
        setPlayedState();

        */

        // Until the media player is prepared, we *cannot* call start() on it!
        mPlayer.prepareAsync();

        //Set notifications
        defaultSongArt = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongArtImage();
        songTitle = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongDisplayName();
        artistName = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongArtist();
        albumName = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongAlbum();
        songDuration = AppsHelper.defaultSongList.get(AppsHelper.currentSongIndex).getSongDuration();

        //Set notification bar
        updateNotification();
        //Update player controls
        callUiPlayerControl();
        if (mIsStreaming) {
            mWifiLock.acquire();
        } else if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    private void processPauseRequest() {
        if (mState == State.Playing) {
            // Pause media player and cancel the 'foreground service' state.
            mState = State.Paused;
            mPlayer.pause();
            //Set notification status
            setNotificationButton();
        }
    }

    private void processStopRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            mState = State.Stopped;
            // let go of all resources...
            relaxResources(true);
            giveUpAudioFocus();
            stopSelf();
        }
    }

    private void processPreviousRequest() {
       // Log.d(LOG_TAG, "Clicked pre");
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            if (AppsHelper.currentSongIndex > 0) {
                AppsHelper.currentSongIndex = AppsHelper.currentSongIndex - 1;
                playRequest();
            } else {
               // Log.d(LOG_TAG, "Clicked pre & stop here index is : "+AppsHelper.currentSongIndex);
                AppsHelper.currentSongIndex = 0;
                playRequest();
            }
        }
    }

    private void processNextRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            if (AppsHelper.defaultSongList != null) {
                if (AppsHelper.currentSongIndex < (AppsHelper.defaultSongList.size() - 1)) {
                    AppsHelper.currentSongIndex = AppsHelper.currentSongIndex + 1;
                   // Log.d(LOG_TAG,"Completed-clcuNext- and size: "+AppsHelper.defaultSongList.size());
                    playRequest();
                } else {
                    AppsHelper.currentSongIndex = 0;
                    processStopRequest();
                }
            } else {
               // Log.d(LOG_TAG, "Empty log");
            }
        }
    }

    private void processRepeatRequest() {
        if (isRepeat) {
            isRepeat = false;
        } else {
            isRepeat = true;
        }
    }

    private void processShuffleRequest() {
        if (isShuffle) {
            isShuffle = false;
        } else {
            isShuffle = true;
        }
    }

    private void processSetProgressRequest() {
        Log.d(LOG_TAG,"Call progress request");
        if (mPlayer != null) {

            Log.d(LOG_TAG,"mPlayer value isnt null: ");
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int currentPosition = intent.getIntExtra("current_pos",0);
                    Log.d(LOG_TAG,"Get  progress value: "+currentPosition);
                    mPlayer.seekTo(currentPosition);
                }
            };
        }
    }

    private void updateNotification() {
      //  Log.d(LOG_TAG, "Clicked");

        // Using RemoteViews to bind custom layouts into Notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(ACTION_TOGGLE_PLAYBACK);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, PlayerServices.class);
        previousIntent.setAction(ACTION_PREVIOUS);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerServices.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerServices.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent pauseIntent = new Intent(this, PlayerServices.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent closeIntent = new Intent(this, PlayerServices.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        notifySmallView.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        notifyExtendView.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        notifySmallView.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        notifyExtendView.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        notifySmallView.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        notifyExtendView.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        notifySmallView.setOnClickPendingIntent(R.id.status_bar_collapse, ppauseIntent);
        notifyExtendView.setOnClickPendingIntent(R.id.status_bar_collapse, ppauseIntent);

        notifySmallView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        notifyExtendView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        updateNotificationControls();

        mBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Playing : "+songTitle)
                    .setContentText("Artist : "+artistName)
                    .build();
        mBuilder.contentView = notifySmallView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.bigContentView = notifyExtendView;
        }
        mBuilder.flags = Notification.FLAG_ONGOING_EVENT;
      //  mBuilder.icon = R.drawable.logo;
        mBuilder.contentIntent = pendingIntent;
        startForeground(NOTIFICATION_ID, mBuilder);
    }

    private void updateNotificationControls() {
        notifySmallView.setTextViewText(R.id.status_bar_track_name, songTitle);
        notifyExtendView.setTextViewText(R.id.status_bar_track_name, songTitle);

        notifySmallView.setTextViewText(R.id.status_bar_artist_name, artistName);
        notifyExtendView.setTextViewText(R.id.status_bar_artist_name, artistName);
        notifyExtendView.setTextViewText(R.id.status_bar_album_name, albumName);

        // showing default album image
        if (defaultSongArt == null) {
            defaultSongArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_album_identify);
        }
        notifySmallView.setImageViewBitmap(R.id.status_bar_icon, defaultSongArt);
        notifyExtendView.setImageViewBitmap(R.id.status_bar_album_art, defaultSongArt);

        if (mState == State.Playing) {
            notifySmallView.setImageViewResource(R.id.status_bar_play, R.drawable.md_btn_pause);
            notifyExtendView.setImageViewResource(R.id.status_bar_play, R.drawable.md_btn_pause);
        } else {
            notifySmallView.setImageViewResource(R.id.status_bar_play, R.drawable.md_btn_play);
            notifyExtendView.setImageViewResource(R.id.status_bar_play, R.drawable.md_btn_play);
        }
    }
    private void setNotificationButton(){
        notifySmallView.setImageViewResource(R.id.status_bar_play, R.drawable.md_btn_play);
        notifyExtendView.setImageViewResource(R.id.status_bar_play, R.drawable.md_btn_play);
    }

    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }
    void relaxResources(boolean releaseMediaPlayer) {
        // stop being a foreground service
        stopForeground(true);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();
    }

    public void onGainedAudioFocus() {
        Toast.makeText(getApplicationContext(), "gained audio focus.", Toast.LENGTH_SHORT).show();
        mAudioFocus = AudioFocus.Focused;
        // restart media player with new focus settings
        if (mState == State.Playing)
            configAndStartMediaPlayer();
    }

    public void onLostAudioFocus(boolean canDuck) {
        Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" : "no duck"), Toast.LENGTH_SHORT).show();
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
        // start/restart/pause media player with new focus settings
        if (mPlayer != null && mPlayer.isPlaying()) {
            configAndStartMediaPlayer();
        }
    }

    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            if (mPlayer.isPlaying()) mPlayer.pause();
            return;
        } else if (mAudioFocus == AudioFocus.NoFocusCanDuck) {
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);  // we'll be relatively quiet
        } else {
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud
        }
        if (!mPlayer.isPlaying()) mPlayer.start();
    }

    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null && mAudioFocusHelper.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = State.Playing;
        configAndStartMediaPlayer();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
       // Log.d(LOG_TAG, "Completed and size: " + AppsHelper.defaultSongList.size());
        if (isRepeat) {
            AppsHelper.currentSongIndex = AppsHelper.currentSongIndex;
            playRequest();
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            AppsHelper.currentSongIndex = rand.nextInt((AppsHelper.defaultSongList.size() - 1) - 0 + 1) + 0;
            playRequest();
        } else {
          //  Log.d(LOG_TAG,"Completed and size: here ");
           processNextRequest();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Media player error! Resetting.", Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));
        mState = State.Stopped;
        relaxResources(true);
        giveUpAudioFocus();
        return true;
    }
    private void callUiPlayerControl(){
        UIhandler.removeCallbacks(sendUpdatesToUI);
        UIhandler.postDelayed(sendUpdatesToUI, 1000);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            updateUiPlayerControl();
            UIhandler.postDelayed(this, 1000); // 10 seconds
        }
    };

    private void updateUiPlayerControl() {
        UiIntent.putExtra("SongTitle", songTitle);
        UiIntent.putExtra("artistName", artistName);
        UiIntent.putExtra("duration", songDuration);
        UiIntent.putExtra("songArtImg", defaultSongArt);
        if (mState == State.Playing) {
            UiIntent.putExtra("isPlaying", "yes");
        } else {
            UiIntent.putExtra("isPlaying", "no");
        }
        if(mState == State.Playing || mState == State.Paused) {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();
            UiIntent.putExtra("total_duration", totalDuration);
            UiIntent.putExtra("curr_duration", currentDuration);
        }
        sendBroadcast(UiIntent);
    }
}