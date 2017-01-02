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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.Track;
import com.sidzi.circleofmusic.ui.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MusicPlayerViewHandler extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        FrameLayout flPlayer = (FrameLayout) ((MainActivity) context).findViewById(R.id.flPlayer);

        flPlayer.setVisibility(View.VISIBLE);

        TextView tvPlayingTrackName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackName);
        TextView tvPlayingArtistName = (TextView) ((MainActivity) context).findViewById(R.id.tvPlayingTrackArtist);
        ImageButton ibPlay = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayPause);
        final ImageButton ibAddToBucket = (ImageButton) ((MainActivity) context).findViewById(R.id.ibAddToBucket);
        ImageButton ibPlayNext = (ImageButton) ((MainActivity) context).findViewById(R.id.ibPlayNext);
        ProgressBar pbTrackPlay = (ProgressBar) ((MainActivity) context).findViewById(R.id.pbTrackPlay);

        pbTrackPlay.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.primaryInverted), PorterDuff.Mode.SRC_IN);

        //            Music Notification

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent mainActivity = PendingIntent.getActivity(context, 101, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_statusbar)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(mainActivity)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

//        ibPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (MusicPlayerService.mMediaPlayer.isPlaying()) {
//                    MusicPlayerService.mMediaPlayer.pause();
//                } else {
//                    MusicPlayerService.mMediaPlayer.start();
//                }
//            }
//        });
        final Track temp_track = (Track) intent.getSerializableExtra("track_metadata");
        ibAddToBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.bucketOps(temp_track.getPath(), !(Boolean) ibAddToBucket.getTag(), context);
                if (temp_track.getBucket()) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_added);
                    Toast.makeText(context, "Added to bucket", Toast.LENGTH_SHORT).show();
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_add);
                }
            }
        });
//        ibPlayNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                MusicPlayerService.next(context);
//            }
//        });
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
                mBuilder.setContentTitle(temp_track.getName())
                        .setContentText(temp_track.getAlbum());
                int notifyId = 1;
                mNotificationManager.notify(notifyId, mBuilder.build());
                break;
//            case MusicPlayerService.ACTION_PAUSE_TRACK:
//
//                break;
//            case MusicPlayerService.ACTION_NEXT_TRACK:
//                break;
        }

    }
}
