package com.sidzi.circleofmusic.helpers;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sidzi.circleofmusic.entities.Track;

import java.io.IOException;
import java.util.List;

public class MusicPlayerService extends Service {

    final public static String ACTION_PLAY_TRACK = "com.sidzi.circleofmusic.PLAY_TRACK";
    final public static String ACTION_PAUSE_TRACK = "com.sidzi.circleofmusic.PAUSE_TRACK";
    final public static String ACTION_NEXT_TRACK = "com.sidzi.circleofmusic.NEXT_TRACK";
    static MediaPlayer mMediaPlayer = null;
    static Track PLAYING_TRACK = null;
    private static boolean READY = false;
    private static List<Track> mTrackList = null;
    private static int PLAYING_TRACK_POSITION = -1;

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

    public void play(String track_path, final Context mContext) {
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
                        next(mContext);
                    }
                });
                mMediaPlayer.prepare();
                if (PLAYING_TRACK_POSITION == -1) {
                    PLAYING_TRACK_POSITION = mTrackList.indexOf(new Track(track_path));
                }
            }
            PLAYING_TRACK = mTrackList.get(PLAYING_TRACK_POSITION);
            mContext.sendBroadcast(new Intent(MusicPlayerService.ACTION_PLAY_TRACK));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void next(Context mContext) {
        play(mTrackList.get(++PLAYING_TRACK_POSITION).getPath(), mContext);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

