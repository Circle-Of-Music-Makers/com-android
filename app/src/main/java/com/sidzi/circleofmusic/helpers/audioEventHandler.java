package com.sidzi.circleofmusic.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sidzi.circleofmusic.HomeActivity;
import com.sidzi.circleofmusic.R;

import java.io.IOException;

public class audioEventHandler extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        final RelativeLayout rlPlayer = (RelativeLayout) ((HomeActivity) context).findViewById(R.id.rlPlayer);
        ImageButton ibPlay = (ImageButton) ((HomeActivity) context).findViewById(R.id.ibPlayPause);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) ((HomeActivity) context).findViewById(R.id.fabUpload);
        final TextView tvPlayingTrackName = (TextView) ((HomeActivity) context).findViewById(R.id.tvPlayingTrackName);
        assert rlPlayer != null;
        assert ibPlay != null;
        assert floatingActionButton != null;
        assert tvPlayingTrackName != null;
        floatingActionButton.setVisibility(View.GONE);
        rlPlayer.setVisibility(View.VISIBLE);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            ibPlay.setImageResource(R.drawable.ic_track_stop);
        } else {
            ibPlay.setImageResource(R.drawable.ic_track_play);
        }
        final String track_path = intent.getStringExtra("track_path");
        final String track_name = intent.getStringExtra("track_name");
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            ((ImageButton) v).setImageResource(R.drawable.ic_track_play);
                        }
                    });
                    try {
                        mediaPlayer.setDataSource(track_path);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        ((ImageButton) v).setImageResource(R.drawable.ic_track_stop);
                        tvPlayingTrackName.setText(track_name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        ((ImageButton) v).setImageResource(R.drawable.ic_track_play);
                        floatingActionButton.setVisibility(View.VISIBLE);
                        rlPlayer.setVisibility(View.GONE);
                    } else {
                        try {
                            mediaPlayer.setDataSource(track_path);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            ((ImageButton) v).setImageResource(R.drawable.ic_track_stop);
                            tvPlayingTrackName.setText(track_name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}