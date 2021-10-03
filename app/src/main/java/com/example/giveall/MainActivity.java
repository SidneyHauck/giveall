package com.example.giveall;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final String TAG = "EmailPassword";
    private DatabaseReference RootRef;
    private EditText UserEmail, UserPassword, UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(MainActivity.this, HomePageActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
        auth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        InitializeFields();
    }
    /**
     *@param myView
     *This function transitions to HomePageActivity
     *Called in content_main.xml button
     */
    public void createAccount(View myView) {
        String name = UserName.getText().toString();
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update user profile
                            String currentUserID = auth.getCurrentUser().getUid();
                            HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("uid", currentUserID);
                                profileMap.put("name", name);

                            RootRef.child("Users").child(currentUserID).updateChildren(profileMap);
                            //RootRef.child("Users").child(currentUserID).setValue("");

                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            if (user != null) {
                                user.updateProfile(profileUpdates);
                            }

                            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void InitializeFields()
    {
        UserEmail = findViewById(R.id.emailAddressField);
        UserPassword = findViewById(R.id.passwordField);
        UserName = findViewById(R.id.nameField);
    }

    /**
     *@param myView
     *This function transitions to LogIn
     *Called in content_main.xml button
     */
    public void switchToLogin(View myView){
        Intent myIntent = new Intent(this, LogIn.class);
        startActivity(myIntent);
    }
}