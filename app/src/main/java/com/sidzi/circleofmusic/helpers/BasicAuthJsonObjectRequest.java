package com.sidzi.circleofmusic.helpers;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BasicAuthJsonObjectRequest extends JsonObjectRequest {
    private String AuthToken = null;

    BasicAuthJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String AuthToken) {
        super(method, url, jsonRequest, listener, errorListener);
        this.AuthToken = AuthToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
//        TODO remove key before commit
        String auth = "Basic " + AuthToken;
        params.put("Content-Type", "application/json");
        params.put("Accept", "application/json");
        params.put("Authorization", auth);
        return params;
    }
}
