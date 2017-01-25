package com.sidzi.circleofmusic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.adapters.ChatAdapter;


public class ShoutboxFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_chat_bot, container, false);
        final RecyclerView chatRecyclerView = (RecyclerView) homeView.findViewById(R.id.rvChatConsole);
        final ChatAdapter chatAdapter = new ChatAdapter(getContext());
        final LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getContext());

        chatLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(chatLayoutManager);
        ImageButton ibSend = (ImageButton) homeView.findViewById(R.id.ibSendMessage);
        final EditText etChatMessage = (EditText) homeView.findViewById(R.id.etChatMessage);
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etChatMessage.getText().toString();
                if (!message.equals("")) {
                    etChatMessage.setText("");
//                    Send message to server here
                    chatAdapter.sendMessage(message);
                }
            }
        });
        return homeView;
    }
}
