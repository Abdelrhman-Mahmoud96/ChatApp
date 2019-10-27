package com.example.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userId;
    private FirebaseUser currentUser;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        final TextView nameView = findViewById(R.id.profileName);
        final TextView statusView = findViewById(R.id.profileStatus);
        final TextView genderView = findViewById(R.id.profileGender);
        final CircleImageView imageView = findViewById(R.id.profilePicture);


        if(bundle!= null)
        {
            userId = bundle.getString("id");
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userId);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    nameView.setText(userInfo.getDisplay_name());
                    statusView.setText(userInfo.getStatus());
                    genderView.setText(userInfo.getGender());

                    if(userInfo.getImage().equals("default"))
                    {
                        if(userInfo.getGender().equals("unspecified") || userInfo.getGender().equals("male") ||userInfo.getGender().equals("Male") )
                        {
                            imageView.setImageResource(R.drawable.profile_male_img);
                        }
                        else if(userInfo.getGender().equals("female") ||userInfo.getGender().equals("Female") )

                        {
                            imageView.setImageResource(R.drawable.profile_female_img);
                        }
                    }
                    else
                    {
                        Uri imageUri = Uri.parse(userInfo.getImage());
                        Picasso.get().load(imageUri).placeholder(R.drawable.profile_img).into(imageView);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            databaseReference.addValueEventListener(valueEventListener);


        }
    }
}
