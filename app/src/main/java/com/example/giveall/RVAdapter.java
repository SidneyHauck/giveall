package com.example.giveall;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class RVAdapter extends FirebaseRecyclerAdapter<Listing, RVAdapter.ListingViewHolder> {
    private static final String TAG = "RVAdapter";

    public RVAdapter(FirebaseRecyclerOptions<Listing> options){
        super(options);
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView listingTitles;
        TextView listingDescription;
        TextView listingDate;

        public ListingViewHolder(View itemView) {
            super(itemView);
            listingTitles = itemView.findViewById(R.id.listingTitle);
            listingDescription = itemView.findViewById(R.id.listingDesc);
            listingDate = itemView.findViewById(R.id.listingDate);
        }

    }

    @NotNull
    @Override
    public ListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListingViewHolder listingViewHolder, int position, @NonNull Listing listing) {
        listingViewHolder.listingTitles.setText(listing.getTitle());
        listingViewHolder.listingDescription.setText(listing.getDescription());
        listingViewHolder.listingDate.setText(listing.getDate());

        final String listingKey = listing.getKey();
        final String title = listing.getTitle();
        final String description = listing.getDescription();
        final String userID = listing.getUserID();
        final String userName = listing.getFirstName();
        final String[] retImage = {"default_image"};

        listingViewHolder.itemView.setOnClickListener(view -> {
            Log.d(TAG, "onClick: clicked on: " + title );
            Log.d(TAG, "onClick: clicked on: " + listingKey );

            Intent msgIntent = new Intent(view.getContext(), MessageActivity.class);
            msgIntent.putExtra("LISTING_ID", listingKey);
            msgIntent.putExtra("LISTING_TITLE", title);
            msgIntent.putExtra("USER_ID", userID);
            msgIntent.putExtra("USER_NAME", userName);
            msgIntent.putExtra("LISTING_DESC", description);

            view.getContext().startActivity(msgIntent);
        });
    }
}
