package com.example.giveall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName, userAddress;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final int GalleryPick=1;
    private StorageReference UserProfileImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        UpdateAccountSettings.setOnClickListener(view -> UpdateSettings());

        RetrieveUserInfo();

    }



    private void InitializeFields() {

        UpdateAccountSettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userAddress = findViewById(R.id.set_address);
        userProfileImage = findViewById(R.id.set_profile_image);
    }


    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setUserAddress = userAddress.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please write your user name.", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(setUserAddress)){
            Toast.makeText(this, "Please write your pickup address for listings.", Toast.LENGTH_LONG).show();
        }
        else{
            HashMap<String, String> profileMap = new HashMap<>();
                profileMap.put("uid", currentUserID);
                profileMap.put("name", setUserName);
                profileMap.put("address", setUserAddress);

            RootRef.child("Users").child(currentUserID).setValue(profileMap)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    SendUserToHomeActivity();
                }else{
                    String message = task.getException().toString();
                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")) && (snapshot.hasChild("address")))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveUserAddress = snapshot.child("address").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();

                            userName.setText(retrieveUserName);
                            userAddress.setText(retrieveUserAddress);
                        }else if((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("address"))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveUserAddress = snapshot.child("address").getValue().toString();

                            userName.setText(retrieveUserName);
                            userAddress.setText(retrieveUserAddress);
                        }else if((snapshot.exists()) && (snapshot.hasChild("name"))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();

                            userName.setText(retrieveUserName);
                        }else{
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendUserToHomeActivity(){
        Intent mainIntent = new Intent(SettingsActivity.this, HomePageActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}