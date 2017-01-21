package com.sidzi.circleofmusic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.config;
import com.sidzi.circleofmusic.entities.ChatMessage;
import com.sidzi.circleofmusic.helpers.BasicAuthJsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> chatMessageList;
    private Context mContext;

    public ChatAdapter(Context context) {
        super();
        mContext = context;
        chatMessageList = new ArrayList<>();
        populate();
    }

    void populate() {
        RequestQueue rq = Volley.newRequestQueue(mContext);
        BasicAuthJsonObjectRequest request = new BasicAuthJsonObjectRequest(mContext, Request.Method.GET, config.com_url + "getMessages", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addToArrayList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        rq.add(request);
    }

    public void sendMessage(String message) {
        JSONObject params = new JSONObject();
        try {
            params.put("message_text", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(mContext);
        BasicAuthJsonObjectRequest request = new BasicAuthJsonObjectRequest(mContext, Request.Method.POST, config.com_url + "postMessage", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addToArrayList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        rq.add(request);
    }

    private void addToArrayList(JSONObject response) {
        try {
            JSONArray messages_list = response.getJSONArray("messages");
            chatMessageList.clear();
            for (int i = 0; i < messages_list.length(); i++) {
                chatMessageList.add(new ChatMessage(messages_list.getJSONObject(i).getString("username"), messages_list.getJSONObject(i).getString("message_text")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        holder.tvChatMessageSender.setText(chatMessageList.get(position).getSender());
        holder.tvChatMessageBody.setText(chatMessageList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChatMessageSender;
        private TextView tvChatMessageBody;

        ViewHolder(View view) {
            super(view);
            this.tvChatMessageSender = (TextView) view.findViewById(R.id.tvChatMessageSender);
            this.tvChatMessageBody = (TextView) view.findViewById(R.id.tvChatMessageBody);
        }
    }
}
