package com.sidzi.circleofmusic.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.MainActivity;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.Track;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AudioEventHandler extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    public AudioEventHandler() {
        super();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final ImageButton ibPlay = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayPause);
        final ImageButton ibAddToBucket = (ImageButton) ((MainActivity) context).findViewById(R.id.ibAddToBucket);

        final TextView tvPlayingTrackName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackName);
        final TextView tvPlayingArtistName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackArtist);


        final String track_path = intent.getStringExtra("track_path");
        final String track_name = intent.getStringExtra("track_name");
        final String track_artist = intent.getStringExtra("track_artist");

        final OrmHandler ormHandler = OpenHelperManager.getHelper(context, OrmHandler.class);

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_play);
                } else {
                    mediaPlayer.start();
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_stop);
                }
            }
        });
        ibAddToBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    try {
                        final Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                        List<Track> lister = dbTrack.queryForEq("path", track_path);
                        if (lister.get(0).getBucket() == null || !lister.get(0).getBucket()) {
                            dbTrack.createOrUpdate(new Track(track_name, track_path, track_artist, true));
                            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
                            Toast.makeText(context, "Added to bucket", Toast.LENGTH_SHORT).show();
                        } else {
                            dbTrack.createOrUpdate(new Track(track_name, track_path, track_artist, false));
                            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            final Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
            final List<Track> lister = dbTrack.queryForEq("path", track_path);
            ibPlay.setImageResource(R.drawable.ic_track_stop);
            tvPlayingTrackName.setText(track_name);
            tvPlayingArtistName.setText(track_artist);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(track_path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            try {
                final Track temp_track = lister.get(0);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        temp_track.setPlay_count(temp_track.getPlay_count() + 1);
                        try {
                            dbTrack.update(temp_track);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        mp.reset();
                        ibPlay.setImageResource(R.drawable.ic_track_play);
                    }
                });
                if (temp_track.getBucket() == null || !temp_track.getBucket()) {
                    ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
                } else {
                    ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
                }
                mediaPlayer.prepare();
            } catch (IndexOutOfBoundsException e) {
                ibAddToBucket.setVisibility(View.INVISIBLE);
                mediaPlayer.prepareAsync();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}