package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sidzi.circleofmusic.config;
import com.sidzi.circleofmusic.entities.Track;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.ArrayList;

public class FileUploader extends AsyncTask<Void, Void, Boolean> {
    private String path;
    private Context mContext;

    public FileUploader(Context context, String filename) {
        path = filename;
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SharedPreferences settings = mContext.getSharedPreferences("com_prefs", 0);
        if (!settings.getBoolean("registered", false))
            return false;
        else {
            String url = config.com_url + "uploadTrack";
            ArrayList<Track> mArrayList = Utils.musicLoader(mContext);
            Track _temp = new Track("", "", "");
            try {
                _temp = mArrayList.get(mArrayList.indexOf(new Track(path)));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            try {
                final String uploadIdentity =
                        new MultipartUploadRequest(mContext, url)
                                .addFileToUpload(path, "file")
                                .setNotificationConfig(new UploadNotificationConfig())
                                .setMaxRetries(2)
                                .addParameter("username", settings.getString("username", ""))
                                .addParameter("artist", _temp.getArtist())
                                .addParameter("title", _temp.getName())
                                .addParameter("filename", _temp.getPath().substring(_temp.getPath().lastIndexOf("/")))
                                .startUpload();
            } catch (Exception exc) {
                Log.e("AndroidUploadService", exc.getMessage(), exc);
            }
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean)
            Toast.makeText(mContext, "Register first", Toast.LENGTH_LONG).show();
    }
}