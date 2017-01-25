package com.sidzi.circleofmusic.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.IBinder;
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
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private ProgressBar pbTrackPlay;

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

        mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_statusbar)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(mainActivity)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        ServiceConnection musicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;
                final MusicPlayerService mpService = musicBinder.getService();
                ibPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        try {
                            if (mpService.mMediaPlayer.isPlaying()) {
                                mpService.pause();
                            } else {
                                mpService.unpause();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
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
                        mpService.next(MusicPlayerService.PLAYING_BUCKET);
                    }
                });
                context.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        context.bindService(new Intent(context, MusicPlayerService.class), musicServiceConnection, Context.BIND_AUTO_CREATE);
        final Track temp_track = MusicPlayerService.PLAYING_TRACK;
        final int notifyId = 1;

        switch (intent.getAction()) {
            case MusicPlayerService.ACTION_UPDATE_METADATA:
                tvPlayingTrackName.setText(temp_track.getName());
                tvPlayingArtistName.setText(temp_track.getArtist());
                if (!temp_track.getBucket()) {
                    ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
                } else {
                    ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
                }
                ibAddToBucket.setTag(temp_track.getBucket());
                mBuilder.setContentTitle(temp_track.getName())
                        .setContentText(temp_track.getArtist());
                mNotificationManager.notify(notifyId, mBuilder.build());
                pbTrackPlay.setIndeterminate(true);
                break;
            case MusicPlayerService.ACTION_PAUSE:
                ibPlay.setImageResource(R.drawable.ic_track_play);
                mBuilder.setOngoing(false);
                mNotificationManager.notify(notifyId, mBuilder.build());
                break;
            case MusicPlayerService.ACTION_PLAY:
                ibPlay.setImageResource(R.drawable.ic_track_stop);
                mBuilder.setOngoing(true);
                pbTrackPlay.setIndeterminate(false);
                mNotificationManager.notify(notifyId, mBuilder.build());
                break;
            case MusicPlayerService.ACTION_CLOSE:
                mNotificationManager.cancelAll();
                break;
            default:
                break;
        }
    }
}
