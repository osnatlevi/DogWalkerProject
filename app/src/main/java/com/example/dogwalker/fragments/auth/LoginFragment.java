package com.example.dogwalker.fragments.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogwalker.R;
import com.example.dogwalker.activities.MainActivity;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.DogWalker;
import com.example.dogwalker.models.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;


public class LoginFragment extends BaseFragment {

    EditText emailEt, passwordEt;
    Button toRegisterBtn, submitLoginBtn, login_about_BTN, login_terms_of_use_BTN, login_privacy_policy_BTN;

    User user;
    private RewardedAd mRewardedAd;


    Button crash_BTN;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadVideoAd();
        findViews(view);
        attachListeners();
    }

    private void attachListeners() {
        toRegisterBtn.setOnClickListener(view ->
                navigate(R.id.action_loginFragment_to_newUserTypeFragment));

        submitLoginBtn.setOnClickListener(view -> {
            if (isValidFields()) {
                showLoading("Signing you in..");
                FirebaseManager.loginUser(emailEt.getText().toString(), passwordEt.getText().toString(),
                        user -> {
                            stopLoading();
                            fetchSignedInUserAndShowVideoAd();
                            if (getActivity() != null) {
                                Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                                mainActivityIntent.putExtra("dogOwner", user instanceof DogOwner);
                                startActivity(mainActivityIntent);
                                getActivity().finish();
                            }
                        }, exception -> {
                            stopLoading();
                            showToast("There was a problem signing you in " + exception.getMessage());
                        });
            } else {
                showToast("Please fill all the fields");
            }
        });



        login_privacy_policy_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHtmlTextDialog(getActivity(), "privacy policy.html");
                ;

            }
        });


        login_terms_of_use_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHtmlTextDialog(getActivity(), "terms of use.html");
            }
        });

        login_about_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHtmlTextDialog(getActivity(), "about.html");
            }
        });



///////////////////////

        crash_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

    }

    private void findViews(View view) {
        toRegisterBtn = view.findViewById(R.id.fromLogin_toRegisterBtn);
        submitLoginBtn = view.findViewById(R.id.login_submit_btn);
        emailEt = view.findViewById(R.id.email_et_login);
        passwordEt = view.findViewById(R.id.password_et_login);
        login_privacy_policy_BTN = view.findViewById(R.id.login_privacy_policy_BTN);
        login_terms_of_use_BTN = view.findViewById(R.id.login_terms_of_use_BTN);
        login_about_BTN = view.findViewById(R.id.login_about_BTN);


        //////
        crash_BTN = view.findViewById(R.id.crash_BTN);
    }


    private boolean isValidFields() {
        if (emailEt.getText().toString().isEmpty()) return false;
        if (passwordEt.getText().toString().isEmpty()) return false;
        return true;
    }



    private void fetchSignedInUserAndShowVideoAd() {
        FirebaseManager.fetchSignedInUser(FirebaseAuth.getInstance(),
                new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        LoginFragment.this.user = user;

                        if(!user.isPremium()) {
                            showVideoAd();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
//        FirebaseDatabase.getInstance().getReference().child("Users")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String uid = FirebaseAuth.getInstance().getUid();
//
//                        for(DataSnapshot snapshotChild :snapshot.child("DogOwners").getChildren()) {
//                            DogOwner dogOwner = snapshotChild.getValue(DogOwner.class);
//
//                            if(dogOwner.getId().equals(uid)) {
//                                user = dogOwner;
//                                break;
//                            }
//                        }
//
//                        if(user == null) { // not found in DogOwners
//                            for(DataSnapshot snapshotChild :snapshot.child("DogWalkers").getChildren()) {
//                                DogWalker dogWalker = snapshotChild.getValue(DogWalker.class);
//
//                                if(dogWalker.getId().equals(uid)) {
//                                    user = dogWalker;
//                                    break;
//                                }
//                            }
//                        }
//
//                        if(!user.isPremium()) {
//                            showVideoAd();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }

    private void loadVideoAd() {
        String UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
        if (BuildConfig.DEBUG) {
            UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(getActivity(), UNIT_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("pttt", loadAdError.toString());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("pttt", "Ad was loaded.");
                    }
                });
    }

    private void showVideoAd() {
        mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Toast.makeText(getContext(), "Congr... +1 Live", Toast.LENGTH_SHORT).show();
                loadVideoAd();
            }
        });
    }








    //26.2

    public static void openHtmlTextDialog(Activity activity, String fileNameInAssets) {
        String str = "";
        InputStream is = null;

        try {
            is = activity.getAssets().open(fileNameInAssets);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            str = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(activity);
        materialAlertDialogBuilder.setPositiveButton("Close", null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            materialAlertDialogBuilder.setMessage(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
        } else {
            materialAlertDialogBuilder.setMessage(Html.fromHtml(str));
        }

        AlertDialog al = materialAlertDialogBuilder.show();
        TextView alertTextView = al.findViewById(android.R.id.message);
        alertTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}