package com.sidzi.circleofmusic.adapters;


import android.content.Context;
import android.content.Intent;
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
import com.sidzi.circleofmusic.entities.Potm;
import com.sidzi.circleofmusic.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import static com.sidzi.circleofmusic.ui.MainActivity.com_url;

public class PotmAdapter extends RecyclerView.Adapter<PotmAdapter.ViewHolder> {
    private ArrayList<Potm> potms = new ArrayList<>();
    private Context mContext;

    public PotmAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonArrayRequest trackRequest = new JsonArrayRequest(Request.Method.GET, com_url + "getPOTMs", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        potms.add(new Potm(response.getJSONObject(i)));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(trackRequest);
    }

    @Override
    public PotmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_potm, parent, false);
        return new PotmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PotmAdapter.ViewHolder holder, int position) {
        holder.tvPotmMonth.setText(new DateFormatSymbols().getMonths()[potms.get(position).getMonth() - 1]);
        holder.tvPotmTitle.setText(potms.get(position).getTitle());
        holder.tvPotmDescription.setText(potms.get(position).getDescription());
        holder.itemView.setTag(potms.get(position).getPath());
        holder.itemView.setTag(R.id.tag_track_path, potms.get(position).getPath());
        holder.itemView.setTag(R.id.tag_track_name, potms.get(position).getTitle());
        holder.itemView.setTag(R.id.tag_track_artist, potms.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return potms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPotmMonth;
        private TextView tvPotmTitle;
        private TextView tvPotmDescription;

        ViewHolder(View itemView) {
            super(itemView);
            tvPotmTitle = (TextView) itemView.findViewById(R.id.tvPotmTitle);
            tvPotmDescription = (TextView) itemView.findViewById(R.id.tvPotmDescription);
            tvPotmMonth = (TextView) itemView.findViewById(R.id.tvPotmMonth);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent ready_track = new Intent("com.sidzi.circleofmusic.PLAY_TRACK");
            ready_track.putExtra("track_path", MainActivity.com_url + v.getTag(R.id.tag_track_path).toString());
            ready_track.putExtra("track_name", v.getTag(R.id.tag_track_name).toString());
            ready_track.putExtra("track_artist", v.getTag(R.id.tag_track_artist).toString());
            ready_track.putExtra("bucket", false);
            mContext.sendBroadcast(ready_track);
        }
    }
}
