package com.sidzi.circleofmusic.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.Track;
import com.sidzi.circleofmusic.ui.MainActivity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AudioEventHandler extends BroadcastReceiver {
    static public MediaPlayer mMediaPlayer = null;
    static public NotificationManager mNotificationManager = null;
    static public TrackProgressObserver mTrackProgressObserver = null;
    static public String mRunningTrackPath = null;

    static private List<Track> mTracksList = null;
    static private int mPlayingPosition = 0;
    NotificationCompat.Builder mBuilder = null;
    int notifyId = 1;
    private TextView tvPlayingTrackName = null;
    private TextView tvPlayingArtistName = null;
    private ImageButton ibPlay = null;
    private ImageButton ibAddToBucket = null;
    private ImageButton ibPlayNext = null;
    private ProgressBar pbTrackPlay = null;

    public AudioEventHandler() {
        super();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mTrackProgressObserver = new TrackProgressObserver();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    pbTrackPlay.setIndeterminate(false);
                }
            });
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        FrameLayout flPlayer = (FrameLayout) ((MainActivity) context).findViewById(R.id.flPlayer);

        flPlayer.setVisibility(View.VISIBLE);

        tvPlayingTrackName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackName);
        tvPlayingArtistName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackArtist);
        ibPlay = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayPause);
        ibAddToBucket = (ImageButton) ((MainActivity) context).findViewById(R.id.ibAddToBucket);
        ibPlayNext = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayNext);
        pbTrackPlay = (ProgressBar) ((MainActivity) context).findViewById(R.id.pbTrackPlay);

        pbTrackPlay.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.primaryInverted), PorterDuff.Mode.SRC_IN);

        final String track_path = intent.getStringExtra("track_path");
        final String track_name = intent.getStringExtra("track_name");
        final String track_artist = intent.getStringExtra("track_artist");
        final boolean bucketBoolean = intent.getBooleanExtra("bucket", false);
        mRunningTrackPath = track_path;

        //            Music Notification

        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent mainActivity = PendingIntent.getActivity(context, 101, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_statusbar)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(mainActivity)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        final OrmHandler ormHandler = OpenHelperManager.getHelper(context, OrmHandler.class);


        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_play);
                } else {
                    mMediaPlayer.start();
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_stop);
                }
            }
        });
        ibAddToBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null) {
                    Utils.bucketOps(mRunningTrackPath, !(Boolean) ibAddToBucket.getTag(), context);
                    if (!(Boolean) ibAddToBucket.getTag()) {
                        ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_added);
                        Toast.makeText(context, "Added to bucket", Toast.LENGTH_SHORT).show();
                    } else {
                        ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_add);
                    }
                    ibAddToBucket.setTag(!(Boolean) ibAddToBucket.getTag());
                }
            }
        });
        ibPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        boolean bucket = false;
        try {
            try {
                Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                if (bucketBoolean)
                    mTracksList = dbTrack.queryForEq("bucket", true);
                else
                    mTracksList = dbTrack.queryForAll();
                for (mPlayingPosition = 0; mPlayingPosition < mTracksList.size(); mPlayingPosition++) {
                    if (mTracksList.get(mPlayingPosition).getPath().equals(mRunningTrackPath)) {
                        break;
                    }
                }
                QueryBuilder<Track, String> queryBuilder = dbTrack.queryBuilder();
                SelectArg selectArg = new SelectArg();
                queryBuilder.where().eq("path", selectArg);
                PreparedQuery<Track> preparedQuery = queryBuilder.prepare();
                selectArg.setValue(track_path);
                Track temp_track = dbTrack.query(preparedQuery).get(0);
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
    }

    void playNext() {
        try {
            Track temp_track = mTracksList.get(mPlayingPosition + 1);
            mPlayingPosition += 1;
            if (temp_track.getBucket() == null) {
                temp_track.setBucket(false);
            }
            playSong(temp_track.getPath(), temp_track.getName(), temp_track.getArtist(), temp_track.getBucket());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            mPlayingPosition = -1;
            playNext();
        }
    }

    void playSong(String track_path, String track_name, String track_artist, boolean bucket) throws IOException {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        ibPlay.setImageResource(R.drawable.ic_track_stop);
        tvPlayingTrackName.setText(track_name);
        tvPlayingArtistName.setText(track_artist);
        if (!bucket) {
            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
        } else {
            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
        }
        ibAddToBucket.setTag(bucket);
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(track_path);
        mRunningTrackPath = track_path;
        if (track_path.startsWith("https://")) {
            mMediaPlayer.prepareAsync();
            pbTrackPlay.setIndeterminate(true);
        } else {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    pbTrackPlay.setProgress(0);
                    mTrackProgressObserver.stop();
                    playNext();
                }
            });
            mMediaPlayer.prepare();
            mTrackProgressObserver.setup();
            new Thread(mTrackProgressObserver).start();
        }
        mBuilder.setContentTitle(track_name)
                .setContentText(track_artist);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    public class TrackProgressObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);
        private int totalDuration;

        TrackProgressObserver() {
            super();
        }

        void setup() {
            totalDuration = mMediaPlayer.getDuration();
            if (totalDuration == -1)
                pbTrackPlay.setIndeterminate(true);
            else {
                pbTrackPlay.setIndeterminate(false);
                pbTrackPlay.setMax(totalDuration / 1000);
            }
        }

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                try {
                    pbTrackPlay.setProgress(mMediaPlayer.getCurrentPosition() / 1000);
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e) {
                    this.stop();
                }
            }
        }
    }
}