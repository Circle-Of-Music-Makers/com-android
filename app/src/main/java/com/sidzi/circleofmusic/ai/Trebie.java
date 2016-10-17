package com.sidzi.circleofmusic.ai;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sidzi.circleofmusic.adapters.ChatAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Trebie {
    private String converse_url = null;
    private RequestQueue botChatQueue = null;
    private ChatAdapter mChatAdapter = null;

    public Trebie(Context mContext) {
        super();
        converse_url = "https://api.wit.ai/" + "converse?session_id=" + new BigInteger(16, new SecureRandom()).toString();
        botChatQueue = Volley.newRequestQueue(mContext);
    }

    public void setmChatAdapter(ChatAdapter mChatAdapter) {
        this.mChatAdapter = mChatAdapter;
    }

    public void converse(String message, JSONObject conversation_context) {
        JsonObjectRequest botChatRequest;
        String temp_url = converse_url;
        if (message != null) {
            temp_url += "&q=" + message;
        }
        botChatRequest = new JsonObjectAuthRequest(Request.Method.POST, temp_url, conversation_context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    switch (response.get("type").toString()) {
                        case "msg":
                            //                        update message to adapter
                            mChatAdapter.addMessage(response.get("msg").toString(), false);
                            break;
                        case "action":
                            executeAction(response.get("action").toString(), response.getJSONObject("entities"));
                            break;
                        case "stop":
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        botChatQueue.add(botChatRequest);
    }

    private void executeAction(String action, JSONObject entities) throws JSONException {
        switch (TrebieActions.valueOf(action)) {
            case get_music:
                String emotion = entities.getJSONArray("emotion").getJSONObject(0).get("value").toString();
//                get emotion specific request
                String song = "Hailey Bailey!!!";
                converse(null, new JSONObject().put("song", song));
        }
    }

    private enum TrebieActions {
        get_music("get_music");

        public final String action_name;

        TrebieActions(String action_name) {
            this.action_name = action_name;
        }
    }

    private class JsonObjectAuthRequest extends JsonObjectRequest {
        JsonObjectAuthRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
//            TODO remove this before committing
            String auth = "Bearer " + "";
            params.put("Content-Type", "application/json");
            params.put("Accept", "application/json");
            params.put("Authorization", auth);
            return params;
        }
    }
}
