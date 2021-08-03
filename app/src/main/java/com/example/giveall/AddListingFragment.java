package com.example.giveall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class AddListingFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_add_listing,container,false);
        Button submitButton = (Button) view.findViewById(R.id.submitBtn);
        submitButton.setOnClickListener(views -> {
            String newListingTitle = ((EditText) view.findViewById(R.id.itemTitle)).getText().toString();
            String newListingExpirationDate = ((EditText) view.findViewById(R.id.itemExpirationDate)).getText().toString();
            String newListingDetails = ((EditText) view.findViewById(R.id.itemDetails)).getText().toString();
            ((HomePageActivity) requireActivity()).listings.add(new Listing(newListingTitle, newListingDetails, newListingExpirationDate));

            Toast.makeText(getActivity(), "New listing added!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

}