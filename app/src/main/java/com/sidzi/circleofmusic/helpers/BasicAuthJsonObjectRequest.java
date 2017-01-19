package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BasicAuthJsonObjectRequest extends JsonObjectRequest {
    private String AuthToken = null;
    private String UserName = null;

    public BasicAuthJsonObjectRequest(Context context, int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.AuthToken = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences settings = context.getSharedPreferences("com_prefs", 0);
        this.UserName = settings.getString("username", "");
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", AuthToken);
        params.put("Username", UserName);
        params.put("Content-Type", "application/json");
        params.put("Accept", "application/json");
        return params;
    }
}
