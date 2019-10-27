package com.example.chatapp;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Executor;

import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private ChatAdapter adapter;
    private int RC_SEND_IMAGE = 1;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button sendMessage = view.findViewById(R.id.sendButton);
        final EditText messageText = view.findViewById(R.id.messageEdt);
        RecyclerView messages = view.findViewById(R.id.messageRecyclerView);
        Button sendImage = view.findViewById(R.id.sendImageView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference().child("Messages");
        storageReference = firebaseStorage.getReference().child("chat_photos");

        Query query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Messages");

        FirebaseRecyclerOptions<ChatInfo> options = new FirebaseRecyclerOptions
                .Builder<ChatInfo>()
                .setQuery(query, ChatInfo.class)
                .build();

        messages.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(options);
        messages.setAdapter(adapter);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser.getDisplayName();
                ChatInfo chatInfo = new ChatInfo(firebaseUser.getUid(), firebaseUser.getDisplayName(), messageText.getText().toString().trim(),null);
                databaseReference.push().setValue(chatInfo);
                messageText.setText("");

            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,RC_SEND_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SEND_IMAGE && resultCode == Activity.RESULT_OK)
        {
            Uri imageUri = data.getData();

                    final StorageReference imageRef = storageReference.child(imageUri.getLastPathSegment());
                    UploadTask uploadTask = imageRef.putFile(imageUri);

                    Task<Uri> downloadTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                ChatInfo friendlyMessage = new ChatInfo(firebaseUser.getUid(), firebaseUser.getDisplayName(), null,downloadUri.toString());
                                databaseReference.push().setValue(friendlyMessage);
                            } else {
                                Log.e(MainActivity.class.getCanonicalName(), "downloading the image failed");
                            }
                        }
                    });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }


}
