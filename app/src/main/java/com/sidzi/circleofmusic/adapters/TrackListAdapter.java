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
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    private List<Track> mTrackList;
    private Context mContext;

    public TrackListAdapter(Context mContext, String type) {
        this.mContext = mContext;
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
        holder.itemView.setTag(mTrackList.get(position).getPath());
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
            ready_track.putExtra("track_path", v.getTag().toString());
            ready_track.putExtra("track_name", v.getTag().toString());
            mContext.sendBroadcast(ready_track);
        }

    }
}