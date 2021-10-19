package com.example.giveall;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class AddListingFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private DatabaseReference listingDatabase;
    private DatabaseReference childReference, RootRef;
    private FirebaseUser user;
    private String key;
    private EditText expirationDate, listingTitle;
    private ImageView listingImage;
    private final String newListingToast = "New listing added!";
    private final String emptyFieldToast = "Missing required field. Try again!";
    Uri ImageUri;
    private StorageReference ListingImageRef;
    private String listingImageURL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_add_listing,container,false);
        Button submitButton = view.findViewById(R.id.submitBtn);
        expirationDate = view.findViewById(R.id.itemExpirationDate);
        if(getActivity() != null && !getActivity().isFinishing()) {
            expirationDate.setOnClickListener(v -> showDatePickerDialog());
        }
        listingDatabase = FirebaseDatabase.getInstance().getReference("/listings");
        childReference = listingDatabase.push();
        key = childReference.getKey();
        assert key != null;
        user = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        ListingImageRef = FirebaseStorage.getInstance().getReference().child("Listing Images");

        CropImageContractOptions options = new CropImageContractOptions(ImageUri, new CropImageOptions());
        options.setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1);


        ActivityResultLauncher<CropImageContractOptions> cropActivityResultLauncher =
                registerForActivityResult(new CropImageContract(), result -> {
                    if (result.isSuccessful()) {
                        ImageUri = result.getUriContent();
                        StorageReference filePath = ListingImageRef.child(key + ".jpg");

                        filePath.putFile(ImageUri)
                                .addOnSuccessListener(taskSnapshot -> {

                                    filePath.getDownloadUrl().addOnCompleteListener(task -> {
                                        listingImageURL = task.getResult().toString();
                                        Log.i("URL",listingImageURL);

                                        RootRef.child("listings").child(key).child("image")
                                                .setValue(listingImageURL);
                                        Picasso.get().load(listingImageURL).into(listingImage);
                                    });

                                });
                    }
                });

        listingTitle = view.findViewById(R.id.itemTitle);
        listingImage = view.findViewById(R.id.listing_image_view);
        listingImage.setOnClickListener(cropView -> cropActivityResultLauncher.launch(options));

        submitButton.setOnClickListener(views -> {
            String newListingTitle = listingTitle.getText().toString();
            String newListingExpirationDate = (expirationDate).getText().toString();
            assert user != null;
            String currentUserID = user.getUid();

            if(newListingTitle.isEmpty() || newListingExpirationDate.isEmpty() || listingImageURL==null){
                Toast.makeText(getActivity(), emptyFieldToast, Toast.LENGTH_SHORT).show();
            } else{
                RootRef.child("Users").child(currentUserID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if((snapshot.exists()) && (snapshot.hasChild("image"))) {
                                    String profileImage = snapshot.child("image").getValue().toString();
                                    String displayName = snapshot.child("name").getValue().toString();
                                    Listing listing = new Listing(newListingTitle, newListingExpirationDate, displayName, profileImage, key, currentUserID, listingImageURL);
                                    listingDatabase.child(key).setValue(listing);
                                    Toast.makeText(getActivity(), newListingToast, Toast.LENGTH_SHORT).show();
                                    listingTitle.setText("");
                                    expirationDate.setText("");
                                    listingImage.setImageResource(R.drawable.ic_add_image_listing);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        return view;
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = month + "/" + dayOfMonth + "/" + year;
        expirationDate.setText(date);
    }
}