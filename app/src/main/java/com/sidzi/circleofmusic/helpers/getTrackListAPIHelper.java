package com.sidzi.circleofmusic.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class getTrackListAPIHelper extends AsyncTask<String, String, JSONObject> {

    private Context mContext;
    private ProgressDialog progressDialog;

    public getTrackListAPIHelper(Context mContext) {
        this.mContext = mContext;
        this.progressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog.setMessage("Syncing");
        this.progressDialog.show();
        this.progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        dbHandler dbInstance = new dbHandler(mContext, null);
        for (int i = 0; i < (obj != null ? obj.length() : 0); i++) {
            try {
                dbInstance.addTrack(obj.get("file" + new DecimalFormat("000").format(i)).toString(), "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected JSONObject doInBackground(String... url) {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(loadFromURL(url[0]));
            return jsonObj;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;

        }
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
