package com.sidzi.circleofmusic.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaButtonHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.bucketOps(AudioEventHandler.mRunningTrackPath, true, context);
    }
}
