package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.entities.Track;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.List;

import static com.sidzi.circleofmusic.config.com_url;


public class BucketSaver {
    private Context mContext;
    private List<Track> blcom_list;
    private File blcom_file;

    public BucketSaver(Context mContext) {
        this.mContext = mContext;
        String blcom_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com_backup.txt";
        blcom_file = new File(blcom_path);
    }

    public void saveFile() {
        if (Utils.BUCKET_OPS) {
            try {
                OrmHandler orm = OpenHelperManager.getHelper(mContext, OrmHandler.class);
                try {
                    Dao<Track, String> mTrack = orm.getDao(Track.class);
                    blcom_list = mTrack.queryForEq("bucket", true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                OpenHelperManager.releaseHelper();
                blcom_file.createNewFile();
                OutputStream outputStream = new FileOutputStream(blcom_file, false);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                for (Track track :
                        blcom_list) {
                    outputStreamWriter.write(track.getPath() + "\n");
                }
                outputStream.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            final String uploadIdentity =
                    new MultipartUploadRequest(mContext, com_url + "backup")
                            .addFileToUpload(blcom_file.getPath(), "file")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    public boolean importFile() {
        try {
            if (blcom_file.exists()) {
                InputStream inputStream = new FileInputStream(blcom_file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String path;
                while ((path = bufferedReader.readLine()) != null) {
                    Utils.bucketOps(path, true, mContext);
                }
                inputStream.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
