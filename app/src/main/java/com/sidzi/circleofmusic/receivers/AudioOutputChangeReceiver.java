package com.sidzi.circleofmusic.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.sidzi.circleofmusic.services.MusicPlayerService;

public class AudioOutputChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(
                AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // signal service to stop playback
            if (MusicPlayerService.PLAYING_TRACK != null) {
                Intent i = new Intent(context, MusicPlayerService.class);
                i.setAction(MusicPlayerService.ACTION_PAUSE);
                context.startService(i);
            }
        }
    }
}
