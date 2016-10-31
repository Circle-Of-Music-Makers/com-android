package com.sidzi.circleofmusic.adapters;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder> {

    private List<Track> mTrackList;
    private Context mContext;
    private boolean bucketBool = false;

    public TracksAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        mTrackList = new ArrayList<>();
    }

    public void updateTracks(ArrayList<Track> mTrackList) {
        this.mTrackList = mTrackList;
        notifyDataSetChanged();
    }

    public void getBucketedTracks() {
        bucketBool = true;
        OrmHandler orm = OpenHelperManager.getHelper(mContext, OrmHandler.class);
        try {
            Dao<Track, String> mTrack = orm.getDao(Track.class);
            mTrackList = mTrack.queryForEq("bucket", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
        notifyDataSetChanged();
    }


    @Override
    public TracksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tnTextView.setText(mTrackList.get(position).getName());
        holder.tdTextView.setText(mTrackList.get(position).getArtist());
        holder.itemView.setTag(R.id.tag_track_path, mTrackList.get(position).getPath());
        holder.itemView.setTag(R.id.tag_track_name, mTrackList.get(position).getName());
        holder.itemView.setTag(R.id.tag_track_artist, mTrackList.get(position).getArtist());
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
            Intent ready_track = new Intent("com.sidzi.circleofmusic.PLAY_TRACK");
            ready_track.putExtra("track_path", v.getTag(R.id.tag_track_path).toString());
            ready_track.putExtra("track_name", v.getTag(R.id.tag_track_name).toString());
            ready_track.putExtra("track_artist", v.getTag(R.id.tag_track_artist).toString());
            ready_track.putExtra("bucket", bucketBool);
            mContext.sendBroadcast(ready_track);
        }
    }
}