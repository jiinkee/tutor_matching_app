package com.edu.monash.fit3077.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.viewAdapter.ChatMessagesAdapter;
import com.edu.monash.fit3077.viewModel.ChatMessageViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * This class represents the private chat room that can be used by the users to communicate with each other.
 * In this class, we fetch and display the messages in the chat room every 3 seconds to simulate real-time conversation between two users.
 */
public class ChatActivity extends AppCompatActivity {
    public static final String CHAT_BID_REQUEST = "CHAT_BID_REQUEST_ID";
    public static final String CHAT_PARTICIPANTS = "CHAT_PARTICIPANTS";
    private ChatMessageViewModel chatViewModel;
    private String bidRequestId;
    private User sender, recipient;
    private ArrayList<String> chatParticipantsId = new ArrayList<>();
    private final int INTERVAL = 3000;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;
    RecyclerView chatRecyclerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // retrieve chat view model
        chatViewModel = new ViewModelProvider(this).get(ChatMessageViewModel.class);

        // get intent from bid activity
        // retrieve chat room details: bid request ID, the student & the tutor
        Intent chatIntent = getIntent();
        bidRequestId = chatIntent.getStringExtra(CHAT_BID_REQUEST);

        ArrayList<User> chatParticipants = (ArrayList<User>) chatIntent.getSerializableExtra(CHAT_PARTICIPANTS);
        // the logged in user is naturally the sender of any new message, hence the other user will be the recipient
        sender = chatViewModel.getLoggedInUser();
        for (User chatParticipant : chatParticipants) {
            chatParticipantsId.add(chatParticipant.getId());
            if (!chatParticipant.getId().equals(sender.getId())) {
                recipient = chatParticipant;
            }
        }

        // set up UP button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConstraintLayout layout = findViewById(R.id.chatLayout);

        // set chat room name
        TextView chatRoomName = findViewById(R.id.txtChatRoomName);
        chatRoomName.setText(chatParticipants.get(0).getFullName() + " & " + chatParticipants.get(1).getFullName());

        // initialize recycler view
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatMessagesAdapter adapter = new ChatMessagesAdapter();
        chatRecyclerView.setAdapter(adapter);

        // observe any new chat messages
        chatViewModel.getChatMessages().observe(this, response -> {
            switch (response.status) {
                case SUCCESS:
                    // update the chat message list with the latest messages
                    adapter.setChatMessages(response.data);
                    if (adapter.getItemCount() > 0) {
                        chatRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                    break;
                case ERROR:
                    Snackbar.make(layout, response.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });

        // observe the sending of new message (observe the failure only)
        chatViewModel.getMessageSendingStatus().observe(this, response -> {
            if (response.status == MyResponse.ResponseStatus.ERROR) {
                Snackbar.make(layout, response.errorMsg, Snackbar.LENGTH_SHORT).show();
            }
        });

        // add Send button click listener
        Button sendButton = findViewById(R.id.btnSendChatMsg);
        TextView chatTextArea = findViewById(R.id.editChatMsgContent);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatTextArea.getText().toString();
                if (!message.equals("")) {
                    chatViewModel.sendChatMessage(bidRequestId, sender, recipient, message);
                    chatTextArea.setText(""); // clear up current text
                } else {
                    Snackbar.make(layout, "Please enter some message.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // get chat messages
        chatViewModel.getChatMessages(bidRequestId, chatParticipantsId);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        // fetch messages every 3 seconds
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, INTERVAL);
                chatViewModel.getChatMessages(bidRequestId, chatParticipantsId);
            }
        }, INTERVAL);
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        // stop the message fetching
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}
