package com.example.giveall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            TextView textView = view.findViewById(R.id.nameName);
            String displayName = name + "!";
            textView.setText(displayName);
        }
        return view;
    }


}