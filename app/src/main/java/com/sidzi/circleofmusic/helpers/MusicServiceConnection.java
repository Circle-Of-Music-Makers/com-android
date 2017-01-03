package com.sidzi.circleofmusic.helpers;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.sidzi.circleofmusic.services.MusicPlayerService;

public class MusicServiceConnection implements ServiceConnection {
    private MusicPlayerService mMusicPlayerService;
    private Context mContext;

    public MusicServiceConnection(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;
        mMusicPlayerService = musicBinder.getService();
        if (MusicPlayerService.PLAYING_TRACK != null) {
            Intent intent = new Intent(MusicPlayerService.ACTION_UPDATE_METADATA);
            intent.putExtra("track_metadata", MusicPlayerService.PLAYING_TRACK);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public MusicPlayerService getmMusicPlayerService() {
        return mMusicPlayerService;
    }
}
