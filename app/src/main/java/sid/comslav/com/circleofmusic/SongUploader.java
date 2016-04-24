package sid.comslav.com.circleofmusic;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SongUploader extends AsyncTask {
    private ContentResolver cR;
    private Uri uri;

    SongUploader(ContentResolver contentResolver, Uri url) {
        cR = contentResolver;
        uri = url;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://circleofmusic-sidzi.rhcloud.com/upYourTrack");
        try {
            InputStream musicInput = cR.openInputStream(uri);
            if (musicInput != null) {
                InputStreamEntity reqEntity = new InputStreamEntity(
                        new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "lol.mp3")), -1
                );
                reqEntity.setContentType("audio/*");
                reqEntity.setChunked(true);
                httpPost.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(httpPost);
                Log.i("Response", String.valueOf(response.getStatusLine()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
