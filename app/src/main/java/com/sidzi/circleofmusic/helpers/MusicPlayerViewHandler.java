package com.sidzi.circleofmusic.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.Track;
import com.sidzi.circleofmusic.services.MusicPlayerService;
import com.sidzi.circleofmusic.ui.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MusicPlayerViewHandler extends BroadcastReceiver {

    private Context mContext;
    private TextView tvPlayingTrackName;
    private TextView tvPlayingArtistName;
    private ImageButton ibPlay;
    private ImageButton ibAddToBucket;
    private ImageButton ibPlayNext;
    private ProgressBar pbTrackPlay;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private MusicServiceConnection mMusicServiceConnection;

    public MusicPlayerViewHandler(Context mContext) {
        super();
        this.mContext = mContext;

        tvPlayingTrackName = (TextView) ((MainActivity) mContext).findViewById(R.id.tvPlayingTrackName);
        tvPlayingArtistName = (TextView) ((MainActivity) mContext).findViewById(R.id.tvPlayingTrackArtist);
        ibPlay = (ImageButton) ((MainActivity) mContext).findViewById(R.id.ibPlayPause);
        ibAddToBucket = (ImageButton) ((MainActivity) mContext).findViewById(R.id.ibAddToBucket);
        ibPlayNext = (ImageButton) ((MainActivity) mContext).findViewById(R.id.ibPlayNext);
        pbTrackPlay = (ProgressBar) ((MainActivity) mContext).findViewById(R.id.pbTrackPlay);
        pbTrackPlay.getProgressDrawable().setColorFilter(mContext.getResources().getColor(R.color.primaryInverted), PorterDuff.Mode.SRC_IN);

        //            Music Notification

        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent mainActivity = PendingIntent.getActivity(mContext, 101, new Intent(mContext, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopService = PendingIntent.getBroadcast(mContext, 4123, new Intent(MusicPlayerService.ACTION_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action stopAction = new NotificationCompat.Action(R.drawable.ic_play_next, "Stop", stopService);
        mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_statusbar)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(mainActivity)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_HIGH).addAction(stopAction);

        mMusicServiceConnection = MainActivity.mMusicServiceConnection;

    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final Track temp_track = (Track) intent.getSerializableExtra("track_metadata");
        context.bindService(new Intent(mContext, MusicPlayerService.class), mMusicServiceConnection, Context.BIND_AUTO_CREATE);
        final MusicPlayerService mpService = mMusicServiceConnection.getmMusicPlayerService();

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mpService.mMediaPlayer.isPlaying()) {
                    mpService.pause();
                } else {
                    mpService.unpause();
                }
            }
        });
        ibAddToBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpService.bucketOperation()) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_added);
                    Toast.makeText(context, "Added to bucket", Toast.LENGTH_SHORT).show();
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_add);
                }
            }
        });
        ibPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpService.next();
            }
        });
        switch (intent.getAction()) {
            case MusicPlayerService.ACTION_UPDATE_METADATA:
                tvPlayingTrackName.setText(temp_track.getName());
                tvPlayingArtistName.setText(temp_track.getAlbum());
                if (!temp_track.getBucket()) {
                    ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
                } else {
                    ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
                }
                ibAddToBucket.setTag(temp_track.getBucket());
                ibPlay.setImageResource(R.drawable.ic_track_stop);
                mBuilder.setContentTitle(temp_track.getName())
                        .setContentText(temp_track.getAlbum());
                int notifyId = 1;
                mNotificationManager.notify(notifyId, mBuilder.build());
                break;
            case MusicPlayerService.ACTION_PAUSE:
                ibPlay.setImageResource(R.drawable.ic_track_play);
                break;
            case MusicPlayerService.ACTION_PLAY:
                ibPlay.setImageResource(R.drawable.ic_track_stop);
                break;
            case MusicPlayerService.ACTION_CLOSE:
                mpService.stopService(new Intent(context, MusicPlayerService.class));
                mNotificationManager.cancelAll();
                ((MainActivity) mContext).finish();
                break;
        }
    }
}
