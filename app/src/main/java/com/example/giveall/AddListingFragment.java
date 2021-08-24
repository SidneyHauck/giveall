package com.example.giveall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class AddListingFragment extends Fragment {
    private DatabaseReference listingDatabase;
    private DatabaseReference childReference;
    private String key;
    private final String newListingToast = "New listing added!";
    private final String emptyFieldToast = "Missing required field. Try again!";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_add_listing,container,false);
        Button submitButton = (Button) view.findViewById(R.id.submitBtn);
        listingDatabase = FirebaseDatabase.getInstance().getReference("/listings");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        submitButton.setOnClickListener(views -> {
            String newListingTitle = ((EditText) view.findViewById(R.id.itemTitle)).getText().toString();
            String newListingExpirationDate = ((EditText) view.findViewById(R.id.itemExpirationDate)).getText().toString();
            String newListingDetails = ((EditText) view.findViewById(R.id.itemDetails)).getText().toString();
            assert user != null;
            String displayName = user.getDisplayName();

            if(newListingTitle.isEmpty() || newListingExpirationDate.isEmpty()){
                Toast.makeText(getActivity(), emptyFieldToast, Toast.LENGTH_SHORT).show();
            } else{
                Listing listing = new Listing(newListingTitle, newListingDetails, newListingExpirationDate, displayName);
                childReference = listingDatabase.push();
                key = childReference.getKey();
                assert key != null;
                listingDatabase.child(key).setValue(listing);
                Toast.makeText(getActivity(), newListingToast, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}