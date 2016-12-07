package com.sidzi.circleofmusic.ai;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sidzi.circleofmusic.adapters.ChatAdapter;
import com.sidzi.circleofmusic.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Trebie {
    private String converse_url = null;
    private RequestQueue trebieQueue = null;
    private ChatAdapter mChatAdapter = null;
    private RecyclerView mRecyclerView = null;
    private Context mContext = null;


    public Trebie(Context mContext) {
        super();
        this.mContext = mContext;
        converse_url = "https://api.wit.ai/" + "converse?session_id=" + new BigInteger(16, new SecureRandom()).toString();
        trebieQueue = Volley.newRequestQueue(mContext);
    }

    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public void setmChatAdapter(ChatAdapter mChatAdapter) {
        this.mChatAdapter = mChatAdapter;
    }

    public void converse(String message, JSONObject conversation_context) {
        JsonObjectRequest botChatRequest;
        String temp_url = converse_url;
        if (message != null) {
            try {
                temp_url += "&q=" + URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        botChatRequest = new JsonObjectAuthRequest(Request.Method.POST, temp_url, conversation_context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    switch (response.get("type").toString()) {
                        case "msg":
                            //      update message to adapter
                            mChatAdapter.addMessage(response.get("msg").toString(), false);
                            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
                            converse(null, null);
                            break;
                        case "action":
                            executeAction(response.get("action").toString(), response.getJSONObject("entities"));
                            break;
                        case "stop":
                            break;
                        default:
                            break;
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        trebieQueue.add(botChatRequest);
    }

    private void executeAction(String action, JSONObject entities) throws JSONException, IOException {
        switch (TrebieActions.valueOf(action)) {
            case get_music:
                String emotion = entities.getJSONArray("emotion").getJSONObject(0).get("value").toString();
                JsonObjectRequest recommendationRequest = new JsonObjectRequest(Request.Method.GET, "http://circleofmusic-sidzi.rhcloud.com/recommend" + emotion, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            converse(null, new JSONObject().put("song", response.get("song").toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                trebieQueue.add(recommendationRequest);
                break;
            case play_music:
                String genre = entities.getJSONArray("genre").getJSONObject(0).get("value").toString();
                Intent ready_track = new Intent("com.sidzi.circleofmusic.PLAY_TRACK");
                ready_track.putExtra("track_path", MainActivity.com_url + "stream" + genre);
                ready_track.putExtra("track_name", genre);
                ready_track.putExtra("track_artist", "some " + genre + " artist");
                mContext.sendBroadcast(ready_track);
                converse(null, null);
                break;
            case get_random:
                JsonObjectRequest randomRequest = new JsonObjectRequest(Request.Method.GET, "http://circleofmusic-sidzi.rhcloud.com/random", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            converse(null, new JSONObject().put("random_song", response.get("song").toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                trebieQueue.add(randomRequest);
            case workout_emotion:
                converse(null, null);
        }
    }

    private enum TrebieActions {
        play_music("play_music"),
        get_music("get_music"),
        workout_emotion("workout_emotion"),
        get_random("get_random");

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
//        TODO remove key before commit
            String auth = "Bearer " + MainActivity.wit_ai_key;
            params.put("Content-Type", "application/json");
            params.put("Accept", "application/json");
            params.put("Authorization", auth);
            return params;
        }
    }
}
