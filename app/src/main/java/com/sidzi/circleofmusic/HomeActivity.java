package com.sidzi.circleofmusic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.adapters.TrackListAdapter;
import com.sidzi.circleofmusic.entities.Track;
import com.sidzi.circleofmusic.helpers.AudioEventHandler;
import com.sidzi.circleofmusic.helpers.OrmHandler;
import com.sidzi.circleofmusic.helpers.VerticalSpaceDecorationHelper;

import net.gotev.uploadservice.UploadService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

public class HomeActivity extends AppCompatActivity {
    BroadcastReceiver audioEventHandler;
    String COM_URL = "http://circleofmusic-sidzi.rhcloud.com/";

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
            JsonObjectRequest eosCheck = new JsonObjectRequest(Request.Method.GET, COM_URL + "checkEOSVersion", null, new Response.Listener<JSONObject>() {
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
            JsonArrayRequest trackRequest = new JsonArrayRequest(Request.Method.GET, COM_URL + "getTrackList", null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    OrmHandler orm = OpenHelperManager.getHelper(HomeActivity.this, OrmHandler.class);
                    try {
                        Dao<Track, String> mTrack = orm.getDao(Track.class);
                        for (int i = 0; i < response.length(); i++) {
                            mTrack.createIfNotExists(new Track(response.get(i).toString(), COM_URL + "streamTrack" + response.get(i).toString(), "remote"));
                        }
                        RecyclerView mRecyclerView;
                        RecyclerView.LayoutManager mLayoutManager;
                        mRecyclerView = (RecyclerView) findViewById(R.id.rVTrackList);
                        mLayoutManager = new LinearLayoutManager(HomeActivity.this);
                        TrackListAdapter mAdapter = new TrackListAdapter(HomeActivity.this, "remote");
                        if (mRecyclerView != null) {
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setHasFixedSize(true);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(HomeActivity.this));
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

        FloatingActionButton floatingActionUploadButton = (FloatingActionButton) findViewById(R.id.fabUpload);
        audioEventHandler = new AudioEventHandler();
        registerReceiver(audioEventHandler, new IntentFilter("com.sidzi.circleofmusic.PLAY_TRACK"));

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
}
