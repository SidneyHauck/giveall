package com.example.giveall;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

    }
    /**
     *@param myView
     *This function transitions to SignUp MainActivity
     *Called in activity_log_in.xml button
     */
    public void switchToSignUp(View myView){
        Intent myIntent = new Intent(LogIn.this, MainActivity.class);
        startActivity(myIntent);
    }

    /**
     *@param myView
     *This function transitions to LogIn
     *Called in activity_log_in.xml button
     */
    public void LogInUser(View myView) {
        EditText inputEmail, inputPassword;
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                                Toast.makeText(LogIn.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(LogIn.this, HomePageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

}