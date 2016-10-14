package com.sidzi.circleofmusic.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sidzi.circleofmusic.MainActivity;
import com.sidzi.circleofmusic.R;

import java.io.IOException;

public class AudioEventHandler extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        final ImageButton ibPlay = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayPause);

        final TextView tvPlayingTrackName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackName);
        final TextView tdPlayingArtistName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackArtist);
        assert ibPlay != null;


        assert tvPlayingTrackName != null;
        final String track_path = intent.getStringExtra("track_path");
        final String track_name = intent.getStringExtra("track_name");
        final String track_artist = intent.getStringExtra("track_artist");

        try {
            if (!mediaPlayer.isPlaying()) {
                ibPlay.setImageResource(R.drawable.ic_track_play);
                tvPlayingTrackName.setText(track_name);
                tdPlayingArtistName.setText(track_artist);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(track_path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                ibPlay.setImageResource(R.drawable.ic_track_play);
                tvPlayingTrackName.setText(track_name);
                tdPlayingArtistName.setText(track_artist);
                mediaPlayer.setDataSource(track_path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            }
        } catch (NullPointerException e) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    ibPlay.setImageResource(R.drawable.ic_track_play);
                }
            });
            ibPlay.setImageResource(R.drawable.ic_track_play);
            tvPlayingTrackName.setText(track_name);
            tdPlayingArtistName.setText(track_artist);
            try {
                mediaPlayer.setDataSource(track_path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_play);
                } else {
                    mediaPlayer.start();
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_stop);
                }
            }
        });
    }
}