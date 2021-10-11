package com.example.giveall;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class MessagesFragment extends Fragment {
    private RecyclerView rvMessages;
    private View messagesView;
    private DatabaseReference msgRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        messagesView = inflater.inflate(R.layout.fragment_messages,container,false);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        msgRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        rvMessages = messagesView.findViewById(R.id.rv_messages_fragment);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        return messagesView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(msgRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, MessagingViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, MessagingViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessagingViewHolder holder, int position, @NonNull Contacts model) {
                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if(snapshot.hasChild("image")){
                                        retImage[0] = snapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }

                                    final String retName = snapshot.child("name").getValue().toString();

                                    holder.userName.setText(retName);

                                    holder.itemView.setOnClickListener(view -> {
                                        Intent msgIntent = new Intent(getContext(), MessageActivity.class);
                                        msgIntent.putExtra("USER_ID", usersIDs);
                                        msgIntent.putExtra("USER_NAME", retName);
                                        startActivity(msgIntent);
                                    });
                                }else{
                                    Toast.makeText(getActivity(), "You do not have any messages, go click on a listing to chat!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MessagingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        return new MessagingViewHolder(view);
                    }
                };

        rvMessages.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MessagingViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName;

        public MessagingViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}