package com.sidzi.circleofmusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sidzi.circleofmusic.helpers.dbHandler;

import java.io.File;
import java.util.Objects;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mTrackList;
    private String[] mTrackPathList;
    private int[] mTrackStatus;
    private Context mContext;
    private MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();


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
        return new ViewHolder(view, mContext, mTrackList);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch (mTrackStatus[holder.getAdapterPosition()]) {

            case 0:
                holder.tdTextView.setVisibility(View.GONE);
                holder.tnTextView.setText(mTrackList[holder.getAdapterPosition()]);
                break;
            case 1:
                holder.tdTextView.setVisibility(View.GONE);
                holder.tnTextView.setText(mTrackList[holder.getAdapterPosition()]);
                break;
            case 2:
            case 3:
                holder.tdTextView.setVisibility(View.VISIBLE);
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
        private Context mContext;
        private String[] mTrackList;


        public ViewHolder(View view, Context mContext, String[] mTrackList) {
            super(view);
            this.mContext = mContext;
            this.mTrackList = mTrackList;
            this.tnTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.tdTextView = (TextView) view.findViewById(R.id.tvTrackInfo);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final dbHandler dbInstance = new dbHandler(mContext, null);
            final String trackName = mTrackList[getAdapterPosition()];
            if (dbInstance.fetchStatus(trackName) < 2) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    Toast.makeText(mContext, "Please connect to the internet for downloading", Toast.LENGTH_SHORT).show();
                } else {
                    final BroadcastReceiver onComplete = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            dbHandler dbInstance = new dbHandler(mContext, null);
                            dbInstance.updateStatusPath(trackName, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + trackName);
                            Toast.makeText(mContext, "Song downloaded restart the app to synchronize", Toast.LENGTH_LONG).show();
                        }
                    };
                    mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + trackName;
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription("Downloading");
                    request.setTitle(trackName);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, trackName);
                    DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                }
            } else {
                Intent ready_track = new Intent("com.sidzi.circleofmusic.PLAY_TRACK");
                ready_track.putExtra("track_path", new File(dbInstance.fetchTrackPath(trackName)).getAbsolutePath());
                ready_track.putExtra("track_name", trackName);
                mContext.sendBroadcast(ready_track);
            }
        }
    }
}
