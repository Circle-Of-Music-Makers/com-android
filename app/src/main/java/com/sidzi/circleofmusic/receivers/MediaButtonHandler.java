package com.sidzi.circleofmusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.helpers.Utils;
import com.sidzi.circleofmusic.services.MusicPlayerService;

import static android.os.Build.VERSION.SDK_INT;

public class MediaButtonHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MusicPlayerService.PLAYING_TRACK != null) {
            Utils.bucketOps(MusicPlayerService.PLAYING_TRACK.getPath(), true, context);
            if (SDK_INT >= 21) {
                SoundPool soundPool = new SoundPool.Builder().build();
                final int SoundID = soundPool.load(context, R.raw.fart, 0);
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                        soundPool.play(SoundID, 1, 1, 0, 0, 1);
                    }
                });
            }
        }
    }
}
