package com.example.dogwalker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {


    FirebaseAuth.AuthStateListener authStateListener;


    //////////
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() != null) {
            boolean dogOwner = getIntent().getBooleanExtra("dogOwner", false);
            System.out.println(dogOwner);
            FirebaseManager.addListenerToCurrentUser(dogOwner);
        }

        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(this, AuthActivity.class));
                finish();
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);


        /////////////////
        //15.2


        initAnalytics();



    }



    ////27.2
    private void initAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setUserId("test0");
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseManager.removeListenerFromCurrentUser();
        if (authStateListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }
}