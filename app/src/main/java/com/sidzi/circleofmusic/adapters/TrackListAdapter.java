package com.sidzi.circleofmusic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.Track;
import com.sidzi.circleofmusic.helpers.OrmHandler;

import java.sql.SQLException;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    private List<Track> mTrackList;

    public TrackListAdapter(Context mContext, String type) {
        OrmHandler orm = OpenHelperManager.getHelper(mContext, OrmHandler.class);
        try {

            Dao<Track, String> mTrack = orm.getDao(Track.class);
            mTrackList = mTrack.queryForEq("type", type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tnTextView.setText(mTrackList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tnTextView;
        TextView tdTextView;


        ViewHolder(View view) {
            super(view);
            this.tnTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.tdTextView = (TextView) view.findViewById(R.id.tvTrackInfo);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//                final dbHandler dbInstance = new dbHandler(mContext, null);
//                final String trackName = mTrackList[getAdapterPosition()];
//                if (dbInstance.fetchStatus(trackName) < 2) {
//                    if (isDownloading) {
//                        Toast.makeText(mContext, "Already Downloading", Toast.LENGTH_SHORT).show();
//                    } else {
//                        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//                        if (!isConnected) {
//                            Toast.makeText(mContext, "Please connect to the internet for downloading", Toast.LENGTH_SHORT).show();
//                        } else {
//                            final BroadcastReceiver onComplete = new BroadcastReceiver() {
//                                @Override
//                                public void onReceive(Context context, Intent intent) {
//                                    dbHandler dbInstance = new dbHandler(mContext, null);
//                                    dbInstance.updateStatusPath(trackName, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + trackName);
//                                    Toast.makeText(mContext, "Song downloaded", Toast.LENGTH_LONG).show();
//                                    dbInstance.addTrack(trackName, (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).toString() + "/" + trackName);
//                                    update();
//                                    unregisterReceiver(this);
//                                }
//                            };
//                            mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//                            String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + trackName;
//                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                            request.setDescription("Downloading");
//                            request.setTitle(trackName);
//                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, trackName);
//                            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//                            manager.enqueue(request);
//                            isDownloading = true;
//                        }
//                    }
//                } else {
//                    Intent ready_track = new Intent("com.sidzi.circleofmusic.PLAY_TRACK");
//                    ready_track.putExtra("track_path", new File(dbInstance.fetchTrackPath(trackName)).getAbsolutePath());
//                    ready_track.putExtra("track_name", trackName);
//                    mContext.sendBroadcast(ready_track);
//                }
        }
    }
}