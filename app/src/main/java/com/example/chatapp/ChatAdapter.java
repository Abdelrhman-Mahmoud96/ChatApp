package com.example.chatapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirebaseRecyclerAdapter <ChatInfo, ChatAdapter.ViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */


    private String thumbLink;
    private String gender;
    private String receiver;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;



    public ChatAdapter(@NonNull FirebaseRecyclerOptions<ChatInfo> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatInfo model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup,false);
       return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(final ChatInfo chatInfo)
        {
            TextView messageView =  itemView.findViewById(R.id.messageTextview);
            final TextView messengerView =  itemView.findViewById(R.id.messengerTextview);
            final CircleImageView imageViewSend =  itemView.findViewById(R.id.messengerImageView);
            final CircleImageView imageViewReceive = itemView.findViewById(R.id.messengerImageViewRight);
            ImageView phototMsg = itemView.findViewById(R.id.photoMsgId);

            messageView.setText(chatInfo.getText());
            if(chatInfo.getPhotoUri() !=null)
            {
                phototMsg.setVisibility(View.VISIBLE);
                Uri photoUri = Uri.parse(chatInfo.getPhotoUri());
                Picasso.get().load(photoUri).placeholder(R.drawable.placeholderforimage).into(phototMsg);
            }

            final FirebaseUser currantUser = FirebaseAuth.getInstance().getCurrentUser();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

            if(currantUser.getUid().equals(chatInfo.getSender_id()))
            {
                imageViewReceive.setVisibility(View.GONE);
                imageViewSend.setVisibility(View.VISIBLE);
                params.gravity = Gravity.LEFT;
                messageView.setLayoutParams(params);
                messengerView.setLayoutParams(params);

                databaseReference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(currantUser.getUid());

                 valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        thumbLink = dataSnapshot.child("thumb_image").getValue().toString();
                        gender = dataSnapshot.child("gender").getValue().toString();


                        if(thumbLink.equals("default"))
                        {
                            if (gender == "unspecified" || gender == "male" || gender == "Male")
                            {
                                imageViewSend.setImageResource(R.drawable.profile_male_img);
                            }
                            else  if ( gender == "female" || gender == "Female")
                            {
                                imageViewSend.setImageResource(R.drawable.profile_female_img);
                            }
                        }
                        else
                        {
                            Uri thumb = Uri.parse(thumbLink);
                            Picasso.get().load(thumb).into(imageViewSend);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                } ;

                messengerView.setText(R.string.messageSenderName);

            }
            else if (!currantUser.getUid().equals(chatInfo.getSender_id()))
            {

                imageViewSend.setVisibility(View.GONE);
                imageViewReceive.setVisibility(View.VISIBLE);
                params.gravity = Gravity.RIGHT;
                messageView.setLayoutParams(params);
                messengerView.setLayoutParams(params);

                databaseReference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(chatInfo.getSender_id());

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        thumbLink = dataSnapshot.child("thumb_image").getValue().toString();
                        receiver = dataSnapshot.child("display_name").getValue().toString();
                        gender = dataSnapshot.child("gender").getValue().toString().trim();


                        if(thumbLink.equals("default"))
                        {
                            if (gender.equals("unspecified") || gender.equals("male") || gender.equals("Male"))
                            {
                                imageViewReceive.setImageResource(R.drawable.profile_male_img);
                            }
                            else  if ( gender.equals("female") || gender.equals("Female"))
                            {
                                imageViewReceive.setImageResource(R.drawable.profile_female_img);
                            }
                        }
                        else
                        {
                            Uri thumb = Uri.parse(thumbLink);
                            Picasso.get().load(thumb).into(imageViewReceive);
                        }
                        messengerView.setText(receiver+" wrote..");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                } ;

            }
            databaseReference.addValueEventListener(valueEventListener);
        }
    }
}
