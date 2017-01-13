package com.sidzi.circleofmusic.helpers;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BasicAuthJsonObjectRequest extends JsonObjectRequest {
    private String AuthToken = null;
    private String UserName = null;

    BasicAuthJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String AuthToken, String UserName) {
        super(method, url, jsonRequest, listener, errorListener);
        this.AuthToken = AuthToken;
        this.UserName = UserName;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/json");
        params.put("Accept", "application/json");
        params.put("Authorization", AuthToken);
        params.put("Username", UserName);
        return params;
    }
}
