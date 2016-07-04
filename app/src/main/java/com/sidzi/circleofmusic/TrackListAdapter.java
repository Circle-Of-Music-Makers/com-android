package com.sidzi.circleofmusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sidzi.circleofmusic.helpers.dbHandler;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mTrackList;
    private String[] mTrackPathList;
    private int[] mTrackStatus;
    private Context mContext;


    public TrackListAdapter(Context mContext) {
        this.mContext = mContext;
        dbHandler dbInstance = new dbHandler(mContext, null);
        this.mTrackList = dbInstance.fetchTracks();
        this.mTrackStatus = dbInstance.fetchStatus();
        this.mTrackPathList = dbInstance.fetchTrackPaths();
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
        return new ViewHolder(view, new ViewHolder.ImViewHolderClick() {
            @Override
            public void downTrack(View track) {
                dbHandler dbInstance = new dbHandler(mContext, null);
                final String tempTrackName = track.getTag().toString();
                if (dbInstance.fetchStatus(tempTrackName) < 2) {
                    downloadMusicTrack(tempTrackName);
                }
//                TODO Add custom iv dialog to show album art
            }
        });
    }

    void downloadMusicTrack(final String selectedItem) {
        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dbHandler dbInstance = new dbHandler(mContext, null);
                dbInstance.updateStatusPath(selectedItem, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + selectedItem);
            }
        };
        mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle(selectedItem);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setTag(mTrackList[holder.getAdapterPosition()]);
        switch (mTrackStatus[holder.getAdapterPosition()]) {
            case 1:
                holder.tnTextView.setTextColor(Color.parseColor("#FFFFFF"));
                holder.itemView.setBackgroundColor(Color.parseColor("#009688"));
            case 0:
                holder.tdTextView.setVisibility(View.GONE);
                holder.ppImageButton.setVisibility(View.GONE);
                holder.tnTextView.setText(mTrackList[holder.getAdapterPosition()]);
                break;
            case 2:
            case 3:
                holder.tdTextView.setVisibility(View.VISIBLE);
                holder.ppImageButton.setVisibility(View.VISIBLE);
                holder.ppImageButton.setImageResource(R.drawable.ic_track_play);
                holder.ppImageButton.setTag("stopped");
                holder.ppImageButton.setBackgroundColor(Color.TRANSPARENT);
                holder.ppImageButton.setOnClickListener(new View.OnClickListener() {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    String trackName = mTrackList[holder.getAdapterPosition()];
                    dbHandler dbInstance = new dbHandler(mContext, null);

                    @Override
                    public void onClick(View v) {
                        if (Objects.equals(v.getTag().toString(), "stopped")) {
                            try {
                                String temp = new File(dbInstance.fetchTrackPath(trackName)).getAbsolutePath();
                                mediaPlayer.setDataSource(temp);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                ((ImageButton) v).setImageResource(R.drawable.ic_track_stop);
                                v.setTag("playing");
                            } catch (IOException | IllegalStateException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            ((ImageButton) v).setImageResource(R.drawable.ic_track_play);
                            v.setTag("stopped");
                        }
                    }
                });
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                try {
                    mediaMetadataRetriever.setDataSource(mTrackPathList[holder.getAdapterPosition()]);
                    String tempTitle = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    if (!Objects.equals(tempTitle, null)) {
                        holder.tnTextView.setText(tempTitle);
                    } else {
                        holder.tnTextView.setText(mTrackList[holder.getAdapterPosition()]);
                        holder.tdTextView.setVisibility(View.GONE);
                    }
                    holder.tdTextView.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                } catch (IllegalArgumentException e) {
                    holder.tnTextView.setText(mTrackList[holder.getAdapterPosition()]);
                    holder.tdTextView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTrackList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tnTextView;
        public TextView tdTextView;
        public ImageButton ppImageButton;
        public ImViewHolderClick mListener;

        public ViewHolder(View view, ImViewHolderClick listener) {
            super(view);
            this.tnTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.tdTextView = (TextView) view.findViewById(R.id.tvTrackInfo);
            this.ppImageButton = (ImageButton) view.findViewById(R.id.ibPlayPause);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.downTrack(v);
        }

        public interface ImViewHolderClick {
            void downTrack(View track);
        }
    }
}
