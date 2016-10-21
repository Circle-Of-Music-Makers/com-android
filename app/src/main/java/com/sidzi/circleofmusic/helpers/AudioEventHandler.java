package com.sidzi.circleofmusic.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioEventHandler extends BroadcastReceiver {
    static private String mRunningTrackPath = null;
    static private List<Track> mTracksList = null;
    static private int playing_position = 0;
    private MediaPlayer mediaPlayer;
    private TextView tvPlayingTrackName = null;
    private TextView tvPlayingArtistName = null;
    private ImageButton ibPlay = null;
    private ImageButton ibAddToBucket = null;
    private ProgressBar pbTrackPlay = null;
    private TrackProgressObserver mTrackProgressObserver = null;

    public AudioEventHandler() {
        super();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mTrackProgressObserver = new TrackProgressObserver();
                new Thread(mTrackProgressObserver).start();
            }
        });
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals("com.sidzi.circleofmusic.PLAY_TRACK")) {


            tvPlayingTrackName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackName);
            tvPlayingArtistName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackArtist);
            ibPlay = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayPause);
            ibAddToBucket = (ImageButton) ((MainActivity) context).findViewById(R.id.ibAddToBucket);
            pbTrackPlay = (ProgressBar) ((MainActivity) context).findViewById(R.id.pbTrackPlay);

            pbTrackPlay.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.primaryInverted), PorterDuff.Mode.SRC_IN);

            final String track_path = intent.getStringExtra("track_path");
            final String track_name = intent.getStringExtra("track_name");
            final String track_artist = intent.getStringExtra("track_artist");

            mRunningTrackPath = track_path;
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
                public void onClick(View v) {
                    if (mediaPlayer != null) {
                        try {
                            final Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                            List<Track> lister = dbTrack.queryForEq("path", mRunningTrackPath);
                            Track temp_track = lister.get(0);
                            boolean bucket;
                            if (temp_track.getBucket() == null || !temp_track.getBucket()) {
                                bucket = true;
                                ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_added);
                                Toast.makeText(context, "Added to bucket", Toast.LENGTH_SHORT).show();
                            } else {
                                bucket = false;
                                ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_add);
                            }
                            dbTrack.createOrUpdate(new Track(temp_track.getName(), temp_track.getPath(), temp_track.getArtist(), bucket));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            boolean bucket = false;
            try {
                try {
                    Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                    mTracksList = dbTrack.queryForAll();
                    for (playing_position = 0; playing_position < mTracksList.size(); playing_position++) {
                        if (mTracksList.get(playing_position).getPath().equals(mRunningTrackPath)) {
                            break;
                        }
                    }
                    Track temp_track = dbTrack.queryForEq("path", track_path).get(0);
                    temp_track.setPlay_count(temp_track.getPlay_count() + 1);
                    try {
                        dbTrack.update(temp_track);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    bucket = !(temp_track.getBucket() == null || !temp_track.getBucket());
                } catch (IndexOutOfBoundsException e) {
                    ibAddToBucket.setVisibility(View.INVISIBLE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                playSong(track_path, track_name, track_artist, bucket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mRunningTrackPath != null) {
                final OrmHandler ormHandler = OpenHelperManager.getHelper(context, OrmHandler.class);
                try {
                    Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                    List<Track> lister = dbTrack.queryForEq("path", mRunningTrackPath);
                    Track temp_track = lister.get(0);
                    temp_track.setBucket(true);
                    dbTrack.update(temp_track);
                } catch (SQLException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void playNext() {
        try {
            Track temp_track = mTracksList.get(playing_position + 1);
            playing_position += 1;
            if (temp_track.getBucket() == null) {
                temp_track.setBucket(false);
            }
            playSong(temp_track.getPath(), temp_track.getName(), temp_track.getArtist(), temp_track.getBucket());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            playing_position = -1;
            playNext();
        }
    }

    void playSong(String track_path, String track_name, String track_artist, boolean bucket) throws IOException {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        ibPlay.setImageResource(R.drawable.ic_track_stop);
        tvPlayingTrackName.setText(track_name);
        tvPlayingArtistName.setText(track_artist);
        if (!bucket) {
            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
        } else {
            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(track_path);
        if (track_path.startsWith("http://")) {
            mediaPlayer.prepareAsync();
        } else {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    pbTrackPlay.setProgress(0);
                    mTrackProgressObserver.stop();
                    playNext();
                }
            });
            mediaPlayer.prepare();
        }
    }

    private class TrackProgressObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);
        private int totalDuration;

        TrackProgressObserver() {
            super();
            totalDuration = mediaPlayer.getDuration();
            if (totalDuration == -1)
                pbTrackPlay.setIndeterminate(true);
            else {
                pbTrackPlay.setIndeterminate(false);
                pbTrackPlay.setMax(totalDuration / 1000);
            }
        }

        void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                pbTrackPlay.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}