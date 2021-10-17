package com.example.giveall;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    private TextView msgUserName;
    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;
    private DatabaseReference RootRef, ContactsRef;
    private String messageReceiverId, listingTitle, listingId, listingDesc, listingAutofillMsg, messageReceiverName, messageSenderId;
    private CircleImageView userImage;
    private final List<Messages> messagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private RecyclerView userMessagesList;
    private Uri fileUri;
    private String myUrl="";


    ActivityResultLauncher<String> imageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            fileUri = uri;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

            final String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
            final String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

            final String messagePushId = userMessageKeyRef.getKey();

            StorageReference filePath = storageReference.child(messagePushId + ".jpg");

            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(fileUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(completeTask -> {
                if (completeTask.isSuccessful()) {
                    Uri downloadURI = completeTask.getResult();
                    myUrl = downloadURI.toString();

                    Map<String, String> messagePictureBody = new HashMap<>();
                    messagePictureBody.put("message", myUrl);
                    messagePictureBody.put("name", fileUri.getLastPathSegment());
                    messagePictureBody.put("from", messageSenderId);
                    messagePictureBody.put("to", messageReceiverId);
                    messagePictureBody.put("type", "image");
                    messagePictureBody.put("messageID", messagePushId);

                    Map<String, Object> messageBodyDetails = new HashMap<>();
                    messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messagePictureBody);
                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messagePictureBody);

                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(messageTask -> {
                        if (!messageTask.isSuccessful()) {
                            Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                        MessageInputText.setText("");
                    });
                }
            });
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        messageSenderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        final String[] retImage = {"default_image"};
        Bundle extras = getIntent().getExtras();
        messageReceiverId = extras.getString("USER_ID");
        listingTitle = extras.getString("LISTING_TITLE");
        listingId = extras.getString("LISTING_ID");
        listingDesc = extras.getString("LISTING_DESC");

        messageReceiverName = extras.getString("USER_NAME");

        RootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("image")){
                        retImage[0] = snapshot.child("image").getValue().toString();
                        Picasso.get().load(retImage[0]).placeholder(R.drawable.profile_image).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        InitializeControllers();

        msgUserName.setText(messageReceiverName);

        if(messageSenderId.equals(messageReceiverId)){
            Toast.makeText(this, "This is your listing.", Toast.LENGTH_LONG).show();
        }

        SendMessageButton.setOnClickListener(view -> SendMessage());

        SendFilesButton.setOnClickListener(view -> {
            imageActivityResultLauncher.launch("image/*");
        });

        if(listingTitle != null){
            listingAutofillMsg = "Listing Title: "+ listingTitle + "\n Details: " + listingDesc;
            MessageInputText.setText(listingAutofillMsg);
        }

        RootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
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
        userImage = findViewById(R.id.custom_profile_image);

        SendMessageButton = findViewById(R.id.send_btn);
        SendFilesButton = findViewById(R.id.add_image_button);
        MessageInputText = findViewById(R.id.input_message);

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesList = findViewById(R.id.rv_messages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);
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

            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

            String messagePushId = userMessageKeyRef.getKey();

            Map<String, String> messageTextBody = new HashMap<>();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderId);
            messageTextBody.put("to", messageReceiverId);
            messageTextBody.put("messageID", messagePushId);

            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                MessageInputText.setText("");
            });
        }
    }

}