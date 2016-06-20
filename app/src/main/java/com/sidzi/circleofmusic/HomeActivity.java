package com.sidzi.circleofmusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rollbar.android.Rollbar;
import com.sidzi.circleofmusic.helpers.apiHelper;
import com.sidzi.circleofmusic.helpers.dbHandler;
import com.sidzi.circleofmusic.helpers.verticalSpaceDecorationHelper;

import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {
    int tracks_count;
    String track_name[];
    int track_status[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreen);
        super.onCreate(savedInstanceState);
        Rollbar.init(this, "d3ece0922a4b44718a20f8ea3f3a397b", "release");
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home);
        dbHandler dbInstance = new dbHandler(this, null);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        if (isConnected) {
            int counter = 0;
            apiHelper api = new apiHelper(this);
            JSONObject obj = null;
            try {
                obj = new JSONObject(api.execute("http://circleofmusic-sidzi.rhcloud.com/getTrackList").get());
                counter = (int) obj.get("count");
            } catch (JSONException | ExecutionException | InterruptedException | NullPointerException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < counter; i++) {
                try {
                    dbInstance.addTrack(obj.get("file" + new DecimalFormat("000").format(i)).toString(), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        track_name = dbInstance.fetchTracks();
        tracks_count = track_name.length;
        track_status = dbInstance.fetchStatus();

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
            requestPermissions(perms, 202);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rVTrackList);
        mLayoutManager = new LinearLayoutManager(this);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new TrackListAdapter(track_name, track_status, dbInstance.fetchTrackPaths(), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
        FloatingActionButton floatingActionUploadButton = (FloatingActionButton) findViewById(R.id.fabUpload);
        assert floatingActionUploadButton != null;
        floatingActionUploadButton.setImageResource(R.drawable.ic_upload_icon);
        floatingActionUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListFileActivity.class);
                startActivity(intent);
            }
        });
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
                    apiHelper apiHelper = new apiHelper(this);
                    JSONObject versionInfo = new JSONObject(apiHelper.execute("http://circleofmusic-sidzi.rhcloud.com/updateCheck").get());
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
}
