package com.example.giveall;

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

    public RVAdapter(FirebaseRecyclerOptions<Listing> options){
        super(options);
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView listingTitles;
        TextView listingDescription;
        TextView listingDate;

        public ListingViewHolder(View itemView) {
            super(itemView);
            listingTitles = (TextView)itemView.findViewById(R.id.listingTitle);
            listingDescription = (TextView)itemView.findViewById(R.id.listingDesc);
            listingDate = (TextView)itemView.findViewById(R.id.listingDate);
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
    }
}
