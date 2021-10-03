package com.example.giveall;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        if (user != null) {
            //display user's name in profile
            String name = user.getDisplayName();
            TextView textView = view.findViewById(R.id.name);
            String displayName = name + "!";
            textView.setText(displayName);
        }

        Button signOutButton = view.findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(views -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        return view;
    }


}