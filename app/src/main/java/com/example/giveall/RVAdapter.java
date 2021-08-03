package com.example.giveall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ListingViewHolder>{
    private ArrayList<Listing> myListings;

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

    public RVAdapter(ArrayList<Listing> listings){
        myListings = listings;
    }

    @NotNull
    @Override
    public ListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        ListingViewHolder lvh = new ListingViewHolder(v);
        return lvh;
    }

    @Override
    public int getItemCount() {
        return myListings.size();
    }


    @Override
    public void onBindViewHolder(ListingViewHolder listingViewHolder, int position) {
        Listing currentItem = myListings.get(position);

        listingViewHolder.listingTitles.setText(currentItem.getTitle());
        listingViewHolder.listingDescription.setText(currentItem.getDescription());
        listingViewHolder.listingDate.setText(currentItem.getDate());
    }
}
