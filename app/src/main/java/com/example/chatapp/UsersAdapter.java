package com.example.chatapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends FirebaseRecyclerAdapter<UserInfo, UsersAdapter.ViewHolder> {




    private String[] options = new String[] {"see profile"};
    private String userId;
    private String currentUsername;
    private String nameText ;
    private String statusText ;
    private String picLinkText ;
    private FirebaseUser firebaseUser;


    TextView name ;
    CircleImageView image;
    TextView status ;



    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UsersAdapter(@NonNull FirebaseRecyclerOptions<UserInfo> options) {
        super(options);

    }



    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UserInfo model) {
        currentUsername = getRef(position).getKey();
        holder.bind(model);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_row, viewGroup,false);
        return new ViewHolder(view,viewGroup);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull final View itemView, final ViewGroup parent) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userId = getRef(getAdapterPosition()).getKey();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(!firebaseUser.getUid().equals(userId)) {
                        AlertDialog alertDialog = new AlertDialog.Builder(parent.getContext())
                                .setTitle("select option")
                                .setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(parent.getContext(), ProfileActivity.class);
                                        intent.putExtra("id", userId);
                                        parent.getContext().startActivity(intent);

                                    }
                                })
                                .create();
                        alertDialog.show();
                    } else
                    {
                        Toast.makeText(parent.getContext(), "it's your account!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        public void bind(UserInfo userInfo)
        {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

           name = itemView.findViewById(R.id.userName);
           image = itemView.findViewById(R.id.usersProfile);
           status = itemView.findViewById(R.id.userStatus);

           if(firebaseUser.getUid().equals(currentUsername))
           {
               nameText = "You";
           }
           else
           {
               nameText = userInfo.getDisplay_name();
           }

            if(picLinkText.equals("default"))
            {
                if (userInfo.getGender().equals("unspecified") || userInfo.getGender().equals("male") || userInfo.getGender().equals("Male"))
                {
                    image.setImageResource(R.drawable.profile_male_img);
                }
                else  if ( userInfo.getGender().equals("female") || userInfo.getGender().equals("Female"))
                {
                    image.setImageResource(R.drawable.profile_female_img);
                }
            }
            else
            {
                Uri thumb = Uri.parse(picLinkText);
                Picasso.get().load(thumb).placeholder(R.drawable.profile_img).into(image);
            }

           statusText = userInfo.getStatus();
           picLinkText = userInfo.getThumb_image();

            name.setText(nameText);
            status.setText(statusText);

        }
    }


}
