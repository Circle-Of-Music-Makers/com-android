//package com.sidzi.circleofmusic.adapters;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.sidzi.circleofmusic.R;
//import com.sidzi.circleofmusic.entities.ChatMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
//
//    private List<ChatMessage> chatEntries = new ArrayList<>();
//
//    public void addMessage(String messageString, boolean chatMe) {
//        if (!"".equals(messageString)) {
//            ChatMessage chatMessage = new ChatMessage(messageString, chatMe);
//            chatEntries.add(chatMessage);
//            notifyItemInserted(getItemCount());
//        }
//    }
//
//
//    @Override
//    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
//        if (chatEntries.get(holder.getAdapterPosition()).isSelf_flag()) {
//            holder.tvChatMessageLocal.setVisibility(View.VISIBLE);
//            holder.tvChatMessageLocal.setText(chatEntries.get(holder.getAdapterPosition()).getBody());
//            holder.tvChatMessageRemote.setVisibility(View.GONE);
//
//        } else {
//            holder.tvChatMessageRemote.setVisibility(View.VISIBLE);
//            holder.tvChatMessageRemote.setText(chatEntries.get(holder.getAdapterPosition()).getBody());
//            holder.tvChatMessageLocal.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatEntries.size();
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private TextView tvChatMessageLocal;
//        private TextView tvChatMessageRemote;
//
//        ViewHolder(View view) {
//            super(view);
//            this.tvChatMessageLocal = (TextView) view.findViewById(R.id.tvChatMessageLocal);
//            this.tvChatMessageRemote = (TextView) view.findViewById(R.id.tvChatMessageRemote);
//            view.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//
//        }
//    }
//}
