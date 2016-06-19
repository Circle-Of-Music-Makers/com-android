package com.sidzi.circleofmusic.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class apiHelper extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;

    public apiHelper(Context mContext) {
        this.progressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog.setMessage("wait");
        this.progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    protected String doInBackground(String... url) {
        try {
            return loadFromURL(url[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String loadFromURL(String url) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(url);
            str = readIt(stream, 1000);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        return conn.getInputStream();
    }

    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
