package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference imageReference;
    private StorageReference thumbReference;
    private ValueEventListener valueEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private int IMG_PICK = 1;

    TextView settingGender ;
    EditText settingStatus;
    CircleImageView settingImg ;
    Button changeStatus ;
    Button changeImage ;
    Button changeGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

         settingGender = findViewById(R.id.settingsDisplayGender);
         settingStatus = findViewById(R.id.settingsStatusText);
         settingImg = findViewById(R.id.settingsProfileImage);
         changeStatus = findViewById(R.id.settingsChangeStatus);
         changeImage = findViewById(R.id.settingsChangeImgBtn);
         changeGender = findViewById(R.id.settingsChangeGender);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference().child("Users").child(user.getUid());

        settingStatus.setEnabled(false);
        settingGender.setEnabled(false);

        settingStatus.setTag(1);
        settingGender.setTag(1);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                settingGender.setText(userInfo.getGender());
                settingStatus.setText(userInfo.getStatus());

                if(userInfo.getImage().equals("default"))
                {
                    if (userInfo.getGender().equals("unspecified") || userInfo.getGender().equals("male") ||
                            userInfo.getGender().equals("Male"))
                    {
                        settingImg.setImageResource(R.drawable.profile_male_img);
                    }
                    else
                    {
                        settingImg.setImageResource(R.drawable.profile_female_img);
                    }
                }
                else
                {
                    Uri image = Uri.parse(userInfo.getImage());
                    Picasso.get().load(image).placeholder(R.drawable.profile_img).into(settingImg);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int status = (int) settingStatus.getTag();
                if(status == 1)
                {
                    settingStatus.setTag(0);
                    settingStatus.setEnabled(true);
                    changeStatus.setText("save status");

                }
                else
                {
                    settingStatus.setTag(1);
                    settingStatus.setEnabled(false);
                    changeStatus.setText("change status");
                    databaseReference.child("status").setValue(settingStatus.getText().toString());

                }

            }
        });

        changeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int status = (int) settingGender.getTag();
                if(status == 1)
                {
                    settingGender.setTag(0);
                    settingGender.setEnabled(true);
                    changeGender.setText("save gender");

                }
                else
                {
                    settingGender.setTag(1);
                    settingGender.setEnabled(false);
                    changeGender.setText("change gender");
                    databaseReference.child("gender").setValue(settingGender.getText().toString().trim());

                }

            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMG_PICK);
            }
        });

        databaseReference.addValueEventListener(valueEventListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            if(requestCode == IMG_PICK && resultCode == Activity.RESULT_OK)
            {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(this);

              }

            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
               CropImage.ActivityResult resultImage = CropImage.getActivityResult(data);
               if(resultCode == Activity.RESULT_OK)
               {
                   Uri croppedImageUri = resultImage.getUri();

                   File thumbImage = new File(croppedImageUri.getPath());
                   try {
                       Bitmap thumbBitmap = new Compressor(this).setMaxWidth(200)
                               .setMaxHeight(200)
                               .setQuality(65)
                               .compressToBitmap(thumbImage);

                       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                       thumbBitmap.compress(Bitmap.CompressFormat.JPEG,100 ,byteArrayOutputStream );
                       byte[] thumbArray = byteArrayOutputStream.toByteArray();

                       imageReference = firebaseStorage.getReference().child("chat_profile_images").child(user.getUid()+".jpg");
                       thumbReference = firebaseStorage.getReference().child("chat_profile_images").child("thumb").child(user.getUid()+".jpg");

                       UploadTask imageTask = thumbReference.putFile(croppedImageUri);
                       final UploadTask thumbTask = imageReference.putBytes(thumbArray);


                       Task<Uri> task = imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                           @Override
                           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                               if (!task.isSuccessful())
                               {
                                   throw task.getException();
                               }
                               return imageReference.getDownloadUrl();
                           }
                       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                           @Override
                           public void onComplete(@NonNull Task<Uri> task) {
                               if(task.isSuccessful())
                               {
                                   Uri downloadUri = task.getResult();
                                   databaseReference.child("image").setValue(downloadUri.toString());

                                   Task<Uri> task1 = thumbTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                       @Override
                                       public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                           if (!task.isSuccessful())
                                           {
                                               throw task.getException();
                                           }
                                           return thumbReference.getDownloadUrl();
                                       }
                                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Uri> task) {
                                           if(task.isSuccessful())
                                           {
                                               Uri downloadUri = task.getResult();
                                               databaseReference.child("thumb_image").setValue(downloadUri.toString());
                                           }
                                       }
                                   });
                               }
                           }
                       });


                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }
    }
}
