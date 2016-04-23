package sid.comslav.com.circleofmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class Home extends AppCompatActivity {
    int count;
    String songs[], songpath;
    JSONObject obj;
    Uri uri;
    private static final int READ_REQUEST_CODE = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        if (!isNetworkAvailable()) {
            // Internet Connection is not present
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connect to wifi or quit")
                    .setCancelable(false)
                    .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            //goes to wifi settings
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //closes the app and return to home screen
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    });
            AlertDialog alert = builder.create();
            alert.show();

            return;
        } else {
            APIHelper api = new APIHelper();

            try {
                obj = new JSONObject(api.execute("http://circleofmusic-sidzi.rhcloud.com/getTrackList").get());
                count = (int) obj.get("count");
            } catch (JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            songs = new String[count];
            for (int i = 0; i < count; i++) {
                try {
                    songs[i] = obj.get("file" + i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            GridView gVTrackList = (GridView) findViewById(R.id.gVTrackList);
            gVTrackList.setAdapter(new TrackAdapter());
            gVTrackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    String selectedItem = songs[position];
                    //DownloadKaCode
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
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

//            Intent intent_upload = new Intent();
//            intent_upload.setType("audio/*");
//            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent_upload, 1);
//            //doFileUpload();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            startActivityForResult(intent, READ_REQUEST_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Cursor cursor = null;
                try {
                    cursor = getApplicationContext().getContentResolver().query(uri, new String[]{MediaStore.Audio.Media.DATA}, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        Log.i("Cursor Out", cursor.getString(1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                try {
                    String uploadId =
                            new BinaryUploadRequest(getApplicationContext(), "http://circleofmusic-sidzi.rhcloud.com/upYourTrack")
                                    .setFileToUpload("")
                                    .setNotificationConfig(new UploadNotificationConfig())
                                    .setMaxRetries(2)
                                    .startUpload();
                } catch (Exception exc) {
                    Log.e("AndroidUploadService", exc.getMessage(), exc);
                }

            }
        }

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
            tv.setTextColor(Color.BLACK);
            return tv;
        }
    }


}
