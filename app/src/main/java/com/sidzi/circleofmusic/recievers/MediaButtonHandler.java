package com.sidzi.circleofmusic.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sidzi.circleofmusic.helpers.Utils;
import com.sidzi.circleofmusic.services.MusicPlayerService;

public class MediaButtonHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MusicPlayerService.PLAYING_TRACK != null)
            Utils.bucketOps(MusicPlayerService.PLAYING_TRACK.getPath(), true, context);
    }
}
