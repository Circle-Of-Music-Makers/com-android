package com.sidzi.circleofmusic.helpers;


import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MusicServiceConnection implements ServiceConnection {
    private MusicPlayerService mMusicPlayerService;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;
        mMusicPlayerService = musicBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public MusicPlayerService getmMusicPlayerService() {
        return mMusicPlayerService;
    }
}
