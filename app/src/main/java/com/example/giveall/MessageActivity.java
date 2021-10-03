package com.example.giveall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MessageActivity extends AppCompatActivity {

    private TextView msgUserName;
    private ImageButton SendMessageButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, MessagesRef, ContactsRef;
    private String messageReceiverId, listingTitle, listingId, messageReceiverName, messageSenderId;
    private Toolbar MessageToolbar;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    private RecyclerView userMessagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        MessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(messageSenderId);
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        Bundle extras = getIntent().getExtras();
        messageReceiverId = extras.getString("USER_ID");
        listingTitle = extras.getString("LISTING_TITLE");
        listingId = extras.getString("LISTING_ID");
        messageReceiverName = extras.getString("USER_NAME");

        InitializeControllers();

        msgUserName.setText(messageReceiverName);

        if(messageSenderId.equals(messageReceiverId)){
            Toast.makeText(this, "This is your listing.", Toast.LENGTH_LONG).show();
        }

        SendMessageButton.setOnClickListener(view -> SendMessage());

    }


    private void InitializeControllers() {

        Toolbar messageToolbar = findViewById(R.id.msg_toolbar);
        setSupportActionBar(messageToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        messageToolbar.setTitle("");
        messageToolbar.setSubtitle("");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_message_bar, null);
        actionBar.setCustomView(actionBarView);

        msgUserName = findViewById(R.id.profile_name);

        SendMessageButton = findViewById(R.id.send_btn);
        MessageInputText = findViewById(R.id.input_message);

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesList = findViewById(R.id.rv_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(previousChildName == null){
                            ContactsRef.child(messageSenderId).child(messageReceiverId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            ContactsRef.child(messageReceiverId).child(messageSenderId)
                                                    .child("Contacts").setValue("Saved");
                                        }
                                    });
                        }
                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messagesAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendMessage(){
        String messageText = MessageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Empty message!", Toast.LENGTH_SHORT).show();
        } else if(messageSenderId.equals(messageReceiverId)){
            Toast.makeText(this, "This is your listing.", Toast.LENGTH_LONG).show();
        }
        else{
            String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
            String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

            String messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener((OnCompleteListener<Void>) task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

                MessageInputText.setText("");
            });
        }
    }

}