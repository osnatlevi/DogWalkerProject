package com.example.dogwalker.fragments.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dogwalker.PermissionsManager;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.DogWalker;
import com.example.dogwalker.models.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends BaseFragment {


    Button homeOption1Btn, homeOption2Btn, homeOption3Btn;
    Button signOutBtn;




    //15.2
    private FrameLayout main_LAY_banner;


    User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchSignedInUserAndShowBanner();
        findViews(view);
        attachListeners();


        if (FirebaseManager.isDogWalker()) {
            homeOption2Btn.setVisibility(View.GONE);
            homeOption1Btn.setText("Dog Owner Searching");





        }
        System.out.println(FirebaseManager.currentUser);
    }


    private void attachListeners() {
        signOutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

        });
        homeOption1Btn.setOnClickListener(view -> {
            if (PermissionsManager.askPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION})) {
                if(user == null) {
                    return;
                }
///
                if(//!user.isPremium() &&
                        !user.isExtraPurchased()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle("You have no purchased extra credits!")
                            .setMessage("Would you like to purchase now?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseManager.setExtraPurchasedForUser(user,
                                            new OnSuccessListener<User>() {
                                                @Override
                                                public void onSuccess(User user) {
                                                    Toast.makeText(getContext(),
                                                            "Purchase complete!",
                                                            Toast.LENGTH_LONG).show();
                                                    navigate(R.id.action_homeFragment_to_searchFragment);
                                                    dialogInterface.dismiss();
                                                }
                                            }, new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(),
                                                            "Could not purchase at this time",
                                                            Toast.LENGTH_LONG).show();
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    navigate(R.id.action_homeFragment_to_searchFragment);
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();

                }

            }
        });
        homeOption2Btn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("userId", FirebaseAuth.getInstance().getUid());
            navigate(R.id.action_homeFragment_to_dogsListFragment, bundle);

        });
        homeOption3Btn.setOnClickListener(view ->
                navigate(R.id.action_homeFragment_to_settingsFragment));
    }

    private void findViews(View view) {
        homeOption1Btn = view.findViewById(R.id.btn_option1_home);
        homeOption2Btn = view.findViewById(R.id.btn_option2_home);
        homeOption3Btn = view.findViewById(R.id.btn_option3_home);
        signOutBtn = view.findViewById(R.id.signout_btn);

        //15.2
        main_LAY_banner = view.findViewById(R.id.main_LAY_banner);
    }


    private void fetchSignedInUserAndShowBanner() {

        FirebaseManager.fetchSignedInUser(FirebaseAuth.getInstance(),
                new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        HomeFragment.this.user = user;

                        if(!user.isPremium()) {
                            showBanner();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = FirebaseAuth.getInstance().getUid();

                for(DataSnapshot snapshotChild :snapshot.child("DogOwners").getChildren()) {
                    DogOwner dogOwner = snapshotChild.getValue(DogOwner.class);

                    if(dogOwner.getId().equals(uid)) {
                        user = dogOwner;
                        break;
                    }
                }

                if(user == null) { // not found in DogOwners
                    for(DataSnapshot snapshotChild :snapshot.child("DogWalkers").getChildren()) {
                        DogWalker dogWalker = snapshotChild.getValue(DogWalker.class);

                        if(dogWalker.getId().equals(uid)) {
                            user = dogWalker;
                            break;
                        }
                    }
                }

                if(!user.isPremium()) {
                    showBanner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //////////////////



    private void showBanner() {
        String UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
        if (BuildConfig.DEBUG) {
            UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
        }

        AdView adView = new AdView(getActivity());
        adView.setAdUnitId(UNIT_ID);
        main_LAY_banner.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
    }


}