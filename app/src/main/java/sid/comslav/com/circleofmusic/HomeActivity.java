package sid.comslav.com.circleofmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sid.comslav.com.circleofmusic.helpers.apiHelper;
import sid.comslav.com.circleofmusic.helpers.dbHandler;

public class HomeActivity extends AppCompatActivity {
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
            count = songs.length;
            if (newUploadIndicator == null) {
                newUploadIndicator = new boolean[count];
            }
        }
    }

    public void downloadFile(String selectedItem) {
        String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle(selectedItem);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

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
                            String url = "http://circleofmusic-sidzi.rhcloud.com/circle-of-music.apk";
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setDescription("Downloading");
                            request.setTitle("Circle of Music App");
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "circle-of-music.apk");
                            // get download service and enqueue file
                            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
//                            How to check when the download has finished
//                            After that following code is to be executed (will work only after signing the apk)
//                            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
//                                    .setDataAndType(Uri.parse(Environment.DIRECTORY_DOWNLOADS+"circle-of-music.apk"),
//                                            "application/vnd.android.package-archive");
//                            startActivity(promptInstall);
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

    private class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyViewHolder> {
        private List<String>

        @Override

        public void onBindViewHolder(TrackAdapter.MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public TrackAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }
    }
}
