//package com.sidzi.circleofmusic.fragments;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//
//import com.sidzi.circleofmusic.R;
//
//
//public class ShoutboxFragment extends Fragment {
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        homeView = inflater.inflate(R.layout.fragment_chat_bot, container, false);
//                    final RecyclerView chatRecyclerView = (RecyclerView) homeView.findViewById(R.id.rvChatConsole);
//                    final Trebie mTrebie = new Trebie(getContext());
//                    final ChatAdapter chatAdapter = new ChatAdapter();
//                    final LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getContext());
//                    mTrebie.setmChatAdapter(chatAdapter);
//                    mTrebie.setmRecyclerView(chatRecyclerView);
//                    chatLayoutManager.setStackFromEnd(true);
//                    chatRecyclerView.setAdapter(chatAdapter);
//                    chatRecyclerView.setLayoutManager(chatLayoutManager);
//                    ImageButton ibSend = (ImageButton) homeView.findViewById(R.id.ibSendMessage);
//                    final EditText etChatMessage = (EditText) homeView.findViewById(R.id.etChatMessage);
//                    etChatMessage.setHint("Say \"help me\" to Trebie to get started");
//                    ibSend.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            etChatMessage.setHint("");
//                            String message = etChatMessage.getText().toString();
//                            if (!message.equals("")) {
//                                chatAdapter.addMessage(message, true);
//                                chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
//                                etChatMessage.setText("");
//                                mTrebie.converse(message, null);
//                            }
//                        }
//                    });
//    }
//}
