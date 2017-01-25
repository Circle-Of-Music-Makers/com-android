package com.sidzi.circleofmusic.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.ComTrack;
import com.sidzi.circleofmusic.services.MusicPlayerService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.sidzi.circleofmusic.config.com_url;

public class ComTracksAdapter extends RecyclerView.Adapter<ComTracksAdapter.ViewHolder> {

    private List<ComTrack> mTrackList;
    private Context mContext;

    public ComTracksAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        mTrackList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonArrayRequest trackRequest = new JsonArrayRequest(Request.Method.GET, com_url + "getTracks", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        mTrackList.add(new ComTrack(response.getJSONObject(i)));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Couldn't fetch data
            }
        });
        requestQueue.add(trackRequest);
    }


    @Override
    public ComTracksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_com_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvComTrackName.setText(mTrackList.get(position).getTitle());
        holder.tvComTrackInfo.setText(mTrackList.get(position).getArtist());
        holder.tvComTrackUploader.setText(mTrackList.get(position).getUsername());
        String temp_path = mTrackList.get(position).getPath();

        holder.itemView.setTag(R.id.tag_track_path, temp_path);
        holder.itemView.setTag(R.id.tag_track_name, mTrackList.get(position).getTitle());
        holder.itemView.setTag(R.id.tag_track_artist, mTrackList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tvComTrackName;
        private TextView tvComTrackInfo;
        private TextView tvComTrackUploader;

        ViewHolder(View view) {
            super(view);
            this.tvComTrackName = (TextView) view.findViewById(R.id.tvComTrackName);
            this.tvComTrackInfo = (TextView) view.findViewById(R.id.tvComTrackInfo);
            this.tvComTrackUploader = (TextView) view.findViewById(R.id.tvComTrackUploader);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            Intent intent = new Intent(mContext, MusicPlayerService.class);
            ServiceConnection mMusicServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;
                    musicBinder.getService().play(v.getTag(R.id.tag_track_path).toString(), v.getTag(R.id.tag_track_artist).toString(), v.getTag(R.id.tag_track_name).toString());
                    mContext.unbindService(this);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
            mContext.bindService(intent, mMusicServiceConnection, Context.BIND_AUTO_CREATE);
        }

        @Override
        public boolean onLongClick(View v) {
            //            Show who uploaded it
            return true;
        }
    }
}