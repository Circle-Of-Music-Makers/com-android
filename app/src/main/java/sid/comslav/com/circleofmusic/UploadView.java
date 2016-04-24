package sid.comslav.com.circleofmusic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class UploadView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(getApplicationContext());
        setContentView(webView);
        webView.loadUrl("http://circleofmusic-sidzi.rhcloud.com/upYourTrack");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
