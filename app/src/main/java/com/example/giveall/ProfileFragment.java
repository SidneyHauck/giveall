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
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    private Button signOutButton, settingsButton;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private CircleImageView userProfileImage;
    private TextView nameTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        signOutButton = view.findViewById(R.id.sign_out);
        settingsButton = view.findViewById(R.id.settings_btn);
        userProfileImage = view.findViewById(R.id.visit_profile_image);
        nameTextView = view.findViewById(R.id.name);

        /*if (user != null) {
            //display user's name in profile
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String retrieveUserName = snapshot.child("name").getValue().toString();
                    TextView textView = view.findViewById(R.id.name);
                    String displayName = retrieveUserName + "!";
                    textView.setText(displayName);
                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }*/
        RetrieveUserInfo();

        signOutButton.setOnClickListener(views -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        settingsButton.setOnClickListener(views -> {
            Intent i = new Intent(getContext(), SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        return view;
    }

    private void RetrieveUserInfo() {
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())  &&  (snapshot.hasChild("image")))
                {
                    String userImage = snapshot.child("image").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();
                    String displayName = userName + "!";

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);

                    nameTextView.setText(displayName);
                }
                else
                {
                    String userName = snapshot.child("name").getValue().toString();
                    String displayName = userName + "!";
                    nameTextView.setText(displayName);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}