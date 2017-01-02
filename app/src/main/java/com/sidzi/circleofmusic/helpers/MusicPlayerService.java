package com.sidzi.circleofmusic.helpers;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.entities.Track;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MusicPlayerService extends Service {

    final public static String ACTION_UPDATE_METADATA = "com.sidzi.circleofmusic.UPDATE_METADATA";
    final public static String ACTION_PAUSE_TRACK = "com.sidzi.circleofmusic.PAUSE_TRACK";
    final public static String ACTION_NEXT_TRACK = "com.sidzi.circleofmusic.NEXT_TRACK";
    private final IBinder mMIBinder = new MusicBinder();
    MediaPlayer mMediaPlayer = null;
    Track PLAYING_TRACK = null;
    private int PLAYING_TRACK_POSITION = -1;
    private LocalBroadcastManager localBroadcastManager;
    private List<Track> mTrackList;


    public MusicPlayerService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        final OrmHandler ormHandler = OpenHelperManager.getHelper(this, OrmHandler.class);
        Dao<Track, String> dbTrack;
        try {
            dbTrack = ormHandler.getDao(Track.class);
            mTrackList = dbTrack.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void play(String track_path) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(track_path);
            if (track_path.startsWith("https://")) {
                mMediaPlayer.prepareAsync();
            } else {
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        next();
                    }
                });
                mMediaPlayer.prepare();
                if (PLAYING_TRACK_POSITION == -1) {
                    PLAYING_TRACK_POSITION = mTrackList.indexOf(new Track(track_path));
                }
            }
            PLAYING_TRACK = mTrackList.get(PLAYING_TRACK_POSITION);
            uiUpdate(PLAYING_TRACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void next() {
        play(mTrackList.get(++PLAYING_TRACK_POSITION).getPath());
    }

    void uiUpdate(Track track) {
        Intent intent = new Intent(ACTION_UPDATE_METADATA);
        intent.putExtra("track_metadata", track);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMIBinder;
    }

    public class MusicBinder extends Binder {
        MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
}

