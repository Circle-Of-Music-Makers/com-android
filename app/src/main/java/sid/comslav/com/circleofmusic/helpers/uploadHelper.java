package sid.comslav.com.circleofmusic.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.File;
import java.util.Objects;

public class uploadHelper extends AsyncTask {
    private String path;
    private Context mContext;

    public uploadHelper(Context context, String filename) {
        path = filename;
        mContext = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String url = "http://circleofmusic-sidzi.rhcloud.com/upYourTrack";
        try {
            final String uploadIdentity =
                    new MultipartUploadRequest(mContext, url)
                            .addFileToUpload(path, "file")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
            UploadServiceBroadcastReceiver uploadServiceBroadcastReceiver = new UploadServiceBroadcastReceiver() {
                @Override
                public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
                    if (Objects.equals(uploadId, uploadIdentity)) {
                        dbHandler dbInstance = new dbHandler(mContext, null);
                        dbInstance.addTrack(new File(path).getName(), path, 3);
                    }
                    super.onCompleted(uploadId, serverResponseCode, serverResponseBody);
                }
            };
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
        return null;
    }
}