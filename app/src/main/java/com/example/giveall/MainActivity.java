package com.example.giveall;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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



public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
    }
    /**
     *@param myView
     *This function transitions to HomePageActivity
     *Called in content_main.xml button
     */
    public void createAccount(View myView) {
        String name = ((EditText) findViewById(R.id.nameField)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailAddressField)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordField)).getText().toString();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update user profile
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
                    }
                });
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