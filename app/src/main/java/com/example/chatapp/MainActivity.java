package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        TabLayout tabLayout = findViewById(R.id.tabLayoutId);
        ViewPager viewPager = findViewById(R.id.viewPagerId);
        FragmentPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors( Color.parseColor("#ffffff"),Color.parseColor("#dd6892"));


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    Log.d(MainActivity.class.getCanonicalName(), "Signed in");
                }
                else
                {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .setTheme(R.style.LoginTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }



    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(MainActivity.this, "Signed In!", Toast.LENGTH_SHORT).show();
                user = firebaseAuth.getCurrentUser();
                databaseReference = firebaseDatabase.getReference().child("Users").child(user.getUid());
                user.getUid();
                Map <String,String> userInfo = new HashMap<>();
                userInfo.put("display_name",user.getDisplayName());
                userInfo.put("status","Hello there...");
                userInfo.put("gender", "unspecified");
                userInfo.put("image","default");
                userInfo.put("thumb_image","default");

                databaseReference.setValue(userInfo);

            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Signed In Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logoutId:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.settingsId:
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
