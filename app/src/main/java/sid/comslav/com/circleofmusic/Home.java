package sid.comslav.com.circleofmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import sid.comslav.com.circleofmusic.helper.dbHandler;

public class Home extends AppCompatActivity {
    int count;
    String songs[];
    boolean newUploadIndicator[];
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
            APIHelper api = new APIHelper();
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
                    newUploadIndicator[i] = dbInstance.addTrack(obj.get("file" + i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        songs = dbInstance.fetchTracks();
        count = songs.length;
        if (newUploadIndicator == null) {
            newUploadIndicator = new boolean[count];
        }
        GridView gVTrackList = (GridView) findViewById(R.id.gVTrackList);
        assert gVTrackList != null;
        gVTrackList.setAdapter(new TrackAdapter());
        gVTrackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
                    requestPermissions(perms, 202);
                }
                String selectedItem = songs[position];
                //Code for Downloading
                String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Downloading");
                request.setTitle(selectedItem);
                // in order for this if to run, you must use the android 3.2 to compile your app
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

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


        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
                requestPermissions(perms, 200);
            }
            Intent intent = new Intent(this, ListFileActivity.class);
            startActivity(intent);
            return true;
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
