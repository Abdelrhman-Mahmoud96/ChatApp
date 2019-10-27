package com.example.chatapp;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.reflect.Array;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment  {


    UsersAdapter usersAdapter;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView users  = view.findViewById(R.id.usersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        Query query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users");

        FirebaseRecyclerOptions<UserInfo> options = new FirebaseRecyclerOptions
                .Builder<UserInfo>()
                .setQuery(query,UserInfo.class)
                .build();

        users.setLayoutManager(linearLayoutManager);
        usersAdapter = new UsersAdapter(options);
        users.setAdapter(usersAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        usersAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        usersAdapter.stopListening();
    }
}
