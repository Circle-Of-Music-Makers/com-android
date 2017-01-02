//package com.sidzi.circleofmusic.views;
//
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sidzi.circleofmusic.R;
//import com.sidzi.circleofmusic.helpers.MusicPlayerService;
//import com.sidzi.circleofmusic.helpers.Utils;
//
//import static com.sidzi.circleofmusic.helpers.MusicPlayerService.mMediaPlayer;
//
//public class MusicPlaybackPanel extends FrameLayout {
//
//    private TextView tvPlayingTrackName = null;
//    private TextView tvPlayingArtistName = null;
//    private ImageButton ibPlay = null;
//    private ImageButton ibAddToBucket = null;
//    private ImageButton ibPlayNext = null;
//    private ProgressBar pbTrackPlay = null;
//    private Context mContext;
//
//    public MusicPlaybackPanel(Context context) {
//        super(context);
//        mContext = context;
//        inflate(context, R.layout.playback_panel, this);
//        init();
//    }
//
//    public MusicPlaybackPanel(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        inflate(context, R.layout.playback_panel, this);
//        init();
//    }
//
//
//    void init() {
//        tvPlayingTrackName = (TextView) findViewById(R.id.tvPlayingTrackName);
//        tvPlayingArtistName = (TextView) findViewById(R.id.tvPlayingTrackArtist);
//        ibPlay = (ImageButton) findViewById(R.id.ibPlayPause);
//        ibAddToBucket = (ImageButton) findViewById(R.id.ibAddToBucket);
//        ibPlayNext = (ImageButton) findViewById(R.id.ibPlayNext);
//        pbTrackPlay = (ProgressBar) findViewById(R.id.pbTrackPlay);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return false;
//    }
//
//    void setup() {
//        ibPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.pause();
//                } else {
//                    mMediaPlayer.start();
//                }
//            }
//        });
//        ibAddToBucket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mMediaPlayer != null) {
//                    Utils.bucketOps(MusicPlayerService.PLAYING_TRACK_PATH, !(Boolean) ibAddToBucket.getTag(), mContext);
//                    if (!(Boolean) ibAddToBucket.getTag()) {
//                        ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_added);
//                        Toast.makeText(mContext, "Added to bucket", Toast.LENGTH_SHORT).show();
//                    } else {
//                        ((ImageButton) v).setImageResource(R.drawable.ic_track_bucket_add);
//                    }
//                    ibAddToBucket.setTag(!(Boolean) ibAddToBucket.getTag());
//                }
//            }
//        });
//        ibPlayNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MusicPlayerService.next();
//            }
//        });
//    }
//
//    public void updateViews() {
//        ibPlay.setImageResource(R.drawable.ic_track_stop);
//        tvPlayingTrackName.setText(MusicPlayerService.PLAYING_TRACK.getName());
//        tvPlayingArtistName.setText(MusicPlayerService.PLAYING_TRACK.getAlbum());
//        if (!MusicPlayerService.PLAYING_TRACK.getBucket()) {
//            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_add);
//        } else {
//            ibAddToBucket.setImageResource(R.drawable.ic_track_bucket_added);
//        }
//        ibAddToBucket.setTag(MusicPlayerService.PLAYING_TRACK.getBucket());
//    }
//}
