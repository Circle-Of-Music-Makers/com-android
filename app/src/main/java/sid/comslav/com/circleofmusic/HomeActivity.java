package sid.comslav.com.circleofmusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import sid.comslav.com.circleofmusic.helpers.apiHelper;
import sid.comslav.com.circleofmusic.helpers.dbHandler;

public class HomeActivity extends AppCompatActivity {
    int count;
    String songs[];
    boolean newUploadIndicator[];
    boolean download_status[];
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHandler dbInstance = new dbHandler(this, null);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        if (isConnected) {
            apiHelper api = new apiHelper();
            try {
                obj = new JSONObject(api.execute("http://circleofmusic-sidzi.rhcloud.com/getTrackList").get());
                count = (int) obj.get("count");
            } catch (JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            songs = new String[count];
            newUploadIndicator = new boolean[count];
            for (int i = 0; i < count; i++) {
                try {
                    songs[i] = obj.get("file" + new DecimalFormat("000").format(i)).toString();
                    newUploadIndicator[i] = dbInstance.addTrack(obj.get("file" + new DecimalFormat("000").format(i)).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            songs = dbInstance.fetchTracks();
            download_status = dbInstance.fetchDownloadStatus();
            count = songs.length;
            if (newUploadIndicator == null) {
                newUploadIndicator = new boolean[count];
            }
        }
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rVTrackList);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new TrackListAdapter(songs, newUploadIndicator, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
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


        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            Intent intent = new Intent(this, ListFileActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.update) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                boolean updateRequired = false;
                try {
                    apiHelper apiHelper = new apiHelper();
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
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
                                requestPermissions(perms, 202);
                            }
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
                                new File(Environment.getExternalStoragePublicDirectory(Environment.getDownloadCacheDirectory().getAbsolutePath()) + "/circle-of-music.apk").delete();
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

    private class TrackAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(songs[i]);
            if (newUploadIndicator[i]) {
                tv.setTextColor(Color.RED);
            } else {
                tv.setTextColor(Color.BLACK);
            }
            return tv;
        }
    }
}
