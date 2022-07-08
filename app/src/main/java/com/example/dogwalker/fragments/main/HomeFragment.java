package com.example.dogwalker.fragments.main;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dogwalker.PermissionsManager;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends BaseFragment {


    Button homeOption1Btn, homeOption2Btn, homeOption3Btn;
    Button signOutBtn;

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
                navigate(R.id.action_homeFragment_to_searchFragment);
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
    }


}