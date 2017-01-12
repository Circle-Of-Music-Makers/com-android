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
            if (mMusicPlayerService.mMediaPlayer.isPlaying())
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(MusicPlayerService.ACTION_PLAY));
            else
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(MusicPlayerService.ACTION_PAUSE));
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
//        When the client disconnects with the service
    }

    public MusicPlayerService getMusicPlayerService() {
        return mMusicPlayerService;
    }
}
