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
        HttpPost httpPost = new HttpPost("http://posttestserver.com/post.php");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "lol.m4a");
        try {
            InputStream musicInput = cR.openInputStream(uri);
            if (musicInput != null) {
                InputStreamEntity reqEntity = new InputStreamEntity(
                        new FileInputStream(file), -1
                );
                reqEntity.setContentType("multipart/form-data");
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
