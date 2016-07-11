package com.sidzi.circleofmusic;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sidzi.circleofmusic.helpers.audioEventHandler;
import com.sidzi.circleofmusic.helpers.dbHandler;
import com.sidzi.circleofmusic.helpers.getJSONHelper;
import com.sidzi.circleofmusic.helpers.getTrackListAPIHelper;
import com.sidzi.circleofmusic.helpers.verticalSpaceDecorationHelper;

import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
            try {
                if (((int) new JSONObject(new getJSONHelper().execute("http://circleofmusic-sidzi.rhcloud.com/checkEOSVersion").get()).get("eos_version")) > getPackageManager().getPackageInfo(getPackageName(), 0).versionCode) {
                    startActivity(new Intent(this, EosActivity.class));
                    finish();
                }
            } catch (JSONException | PackageManager.NameNotFoundException | InterruptedException | ExecutionException | NullPointerException
                    e) {
                e.printStackTrace();
            }

            getTrackListAPIHelper api = new getTrackListAPIHelper(this);
            try {
                api.execute("http://circleofmusic-sidzi.rhcloud.com/getTrackList");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
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
        mAdapter = new TrackListAdapter(HomeActivity.this);
        FloatingActionButton floatingActionUploadButton = (FloatingActionButton) findViewById(R.id.fabUpload);
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibPlayPause);
        assert imageButton != null;
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        audioEventHandler = new audioEventHandler();
        registerReceiver(audioEventHandler, new IntentFilter("com.sidzi.circleofmusic.PLAY_TRACK"));
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.update) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                boolean updateRequired = false;
                try {
                    JSONObject versionInfo = new JSONObject(new getJSONHelper().execute("http://circleofmusic-sidzi.rhcloud.com/updateCheck").get());
                    updateRequired = ((int) versionInfo.get("stable")) > getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                } catch (JSONException | InterruptedException | ExecutionException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (updateRequired) {
                    builder.setTitle("Update Service").setMessage("Would you like to update to the latest version ?");
                    builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BroadcastReceiver onComplete = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                            .setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.getDownloadCacheDirectory().getAbsolutePath()) + "/circle-of-music.apk")),
                                                    "application/vnd.android.package-archive");
                                    startActivity(promptInstall);
                                }
                            };
                            String url = "http://circleofmusic-sidzi.rhcloud.com/circle-of-music.apk";
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setDescription("Downloading");
                            request.setTitle("Circle of Music App");
                            request.setDestinationInExternalPublicDir(Environment.getDownloadCacheDirectory().getAbsolutePath(), "circle-of-music.apk");
                            try {
                                boolean deleteSuccess = new File(Environment.getExternalStoragePublicDirectory(Environment.getDownloadCacheDirectory().getAbsolutePath()) + "/circle-of-music.apk").delete();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }
                    });
                    builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    builder.setTitle("Update Service").setMessage("You are already using the latest version");
                }
            } else {
                builder.setTitle("No Network Access").setMessage("Please connect to an internet service");
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
        //        TODO implement delete
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

        public void update() {
            dbHandler dbInstance = new dbHandler(mContext, null);
            this.mTrackList = dbInstance.fetchTracks();
            this.mTrackStatus = dbInstance.fetchStatus();
            this.mTrackPathList = dbInstance.fetchTrackPaths();
            this.notifyDataSetChanged();
        }

        @Override
        public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
            return new ViewHolder(view, mContext, mTrackList);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            switch (mTrackStatus[position]) {

                case 0:
                    holder.tdTextView.setVisibility(View.GONE);
                    holder.tnTextView.setText(mTrackList[position]);
                    break;
                case 1:
                    holder.tdTextView.setVisibility(View.GONE);
                    holder.tnTextView.setText(mTrackList[position]);
                    break;
                case 2:
                case 3:
                    holder.tdTextView.setVisibility(View.VISIBLE);
                    try {
                        mediaMetadataRetriever.setDataSource(mTrackPathList[position]);
                        String tempTitle = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        if (!Objects.equals(tempTitle, null)) {
                            holder.tnTextView.setText(tempTitle);
                        } else {
                            holder.tnTextView.setText(mTrackList[position]);
                            holder.tdTextView.setVisibility(View.GONE);
                        }
                        holder.tdTextView.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    } catch (IllegalArgumentException e) {
                        holder.tnTextView.setText(mTrackList[position]);
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

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
                                Toast.makeText(mContext, "Song downloaded", Toast.LENGTH_LONG).show();
                                update();
                                unregisterReceiver(this);
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
}
