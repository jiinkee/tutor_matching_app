package com.edu.monash.fit3077.viewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.ChatMessage;

import java.util.ArrayList;


public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageViewHolder> {
    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    public ChatMessagesAdapter() {
    }

    public void setChatMessages(ArrayList<ChatMessage> messages) {
        chatMessages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_chat_message_item, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        
        // display message
        holder.msgRecipient.setText(chatMessage.getRecipientFullName());
        holder.msgSender.setText(chatMessage.getSenderFullName());
        holder.msgPostDate.setText(chatMessage.getPostDateString());
        holder.msgContent.setText(chatMessage.getMessageContent());
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ChatMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView msgSender, msgRecipient, msgPostDate, msgContent;
        ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            msgSender = itemView.findViewById(R.id.txtChatMsgSender);
            msgRecipient = itemView.findViewById(R.id.txtChatMsgRecipient);
            msgPostDate = itemView.findViewById(R.id.txtChatMsgPostDate);
            msgContent = itemView.findViewById(R.id.txtChatMsgContent);

        }
    }
}
