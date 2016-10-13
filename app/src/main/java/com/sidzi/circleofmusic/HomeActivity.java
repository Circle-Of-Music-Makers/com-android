package com.sidzi.circleofmusic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.entities.Track;
import com.sidzi.circleofmusic.helpers.AudioEventHandler;
import com.sidzi.circleofmusic.helpers.OrmHandler;
import com.sidzi.circleofmusic.helpers.VerticalSpaceDecorationHelper;

import net.gotev.uploadservice.UploadService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    BroadcastReceiver audioEventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreen);
        super.onCreate(savedInstanceState);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        if (isConnected) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest eosCheck = new JsonObjectRequest(Request.Method.GET, "http://circleofmusic-sidzi.rhcloud.com/checkEOSVersion", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ((int) response.get("eos_version") > BuildConfig.VERSION_CODE) {
                            startActivity(new Intent(HomeActivity.this, EosActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(eosCheck);
            JsonArrayRequest trackRequest = new JsonArrayRequest(Request.Method.GET, "http://circleofmusic-sidzi.rhcloud.com/getTrackList", null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    OrmHandler orm = OpenHelperManager.getHelper(HomeActivity.this, OrmHandler.class);
                    try {
                        Dao<Track, Integer> mTrack = orm.getDao(Track.class);
                        for (int i = 0; i < response.length(); i++) {
                            mTrack.createIfNotExists(new Track(response.get(i).toString(), ""));
                        }
                    } catch (SQLException | JSONException e) {
                        e.printStackTrace();
                    }
                    OpenHelperManager.releaseHelper();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(trackRequest);
        }
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home);

        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
                requestPermissions(perms, 202);
            }
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rVTrackList);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TrackListAdapter();
        FloatingActionButton floatingActionUploadButton = (FloatingActionButton) findViewById(R.id.fabUpload);
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibPlayPause);
        assert imageButton != null;
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        audioEventHandler = new AudioEventHandler();
        registerReceiver(audioEventHandler, new IntentFilter("com.sidzi.circleofmusic.PLAY_TRACK"));
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(this));
        }
        if (floatingActionUploadButton != null) {
            floatingActionUploadButton.setImageResource(R.drawable.ic_upload_icon);
            floatingActionUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ListFileActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(audioEventHandler);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

        private List<Track> mTrackList;

        TrackListAdapter() {
            OrmHandler orm = OpenHelperManager.getHelper(HomeActivity.this, OrmHandler.class);
            try {
                Dao<Track, Integer> mTrack = orm.getDao(Track.class);
                mTrackList = mTrack.queryForAll();
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
}
