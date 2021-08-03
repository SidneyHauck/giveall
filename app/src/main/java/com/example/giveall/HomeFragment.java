package com.example.giveall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;

        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        rv = view.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(getActivity());
        ArrayList<Listing> activityListings = ((HomePageActivity) requireActivity()).listings;
        adapter = new RVAdapter(activityListings);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        return view;
    }

}