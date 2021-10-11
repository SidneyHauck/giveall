package com.example.giveall;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.CropImageContract;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName, userAddress;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserProfileImagesRef;
    private String userProfileImageURL, setUserName, setUserAddress;
    Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        Toolbar messageToolbar = findViewById(R.id.msg_toolbar);
        setSupportActionBar(messageToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        messageToolbar.setTitle("");
        messageToolbar.setSubtitle("");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        messageToolbar.setNavigationOnClickListener(v -> SendUserToHomeActivity());

        InitializeFields();


        RetrieveUserInfo();

        CropImageContractOptions options = new CropImageContractOptions(ImageUri, new CropImageOptions());
        options.setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1);


        ActivityResultLauncher<CropImageContractOptions> activityResultLauncher =
                registerForActivityResult(new CropImageContract(), result -> {
                            if (result.isSuccessful()) {
                                ImageUri = result.getUriContent();
                                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                                filePath.putFile(ImageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            Toast.makeText(SettingsActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                                            filePath.getDownloadUrl().addOnCompleteListener(task -> {
                                                String profileImageUrl = task.getResult().toString();
                                                Log.i("URL",profileImageUrl);

                                                RootRef.child("Users").child(currentUserID).child("image")
                                                        .setValue(profileImageUrl)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful())
                                                            {
                                                                Toast.makeText(SettingsActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                                                SaveProfileImage(profileImageUrl);
                                                            }
                                                            else
                                                            {
                                                                String message = task1.getException().toString();
                                                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            });

                                        })
                                        .addOnFailureListener(e -> {
                                            String message = e.toString();
                                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });

        userProfileImage.setOnClickListener(view -> {
            activityResultLauncher.launch(options);
        });

        UpdateAccountSettings.setOnClickListener(view -> UpdateSettings());
    }

    private void SaveProfileImage(String profileImageUrl){
        userProfileImageURL = profileImageUrl;
    }

    private void InitializeFields() {

        UpdateAccountSettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userAddress = findViewById(R.id.set_address);
        userProfileImage = findViewById(R.id.set_profile_image);
    }


    private void UpdateSettings() {
        setUserName = userName.getText().toString();
        setUserAddress = userAddress.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please write your user name.", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(setUserAddress)){
            Toast.makeText(this, "Please write your pickup address for listings.", Toast.LENGTH_LONG).show();
        }else{
            HashMap<String, String> profileMap = new HashMap<>();
                profileMap.put("uid", currentUserID);
                profileMap.put("name", setUserName);
                profileMap.put("address", setUserAddress);
                profileMap.put("image", userProfileImageURL);

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
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);

                        }else if((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();

                            userName.setText(retrieveUserName);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)   {
            SendUserToHomeActivity();
        }
        return true;
    }
}