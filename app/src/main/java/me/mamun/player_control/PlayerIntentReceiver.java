package me.mamun.player_control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by Mamun on 22-May-16.
 */
public class PlayerIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Toast.makeText(context, "Headphones disconnected.", Toast.LENGTH_SHORT).show();
            // send an intent to our MusicService to telling it to pause the audio
            context.startService(new Intent(PlayerServices.ACTION_PAUSE));
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    context.startService(new Intent(PlayerServices.ACTION_TOGGLE_PLAYBACK));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    context.startService(new Intent(PlayerServices.ACTION_PLAY));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    context.startService(new Intent(PlayerServices.ACTION_PAUSE));
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    context.startService(new Intent(PlayerServices.ACTION_STOP));
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    context.startService(new Intent(PlayerServices.ACTION_NEXT));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    // TODO: ensure that doing this in rapid succession actually plays the
                    // previous song
                    context.startService(new Intent(PlayerServices.ACTION_PREVIOUS));
                    break;
            }
        }
    }
}
