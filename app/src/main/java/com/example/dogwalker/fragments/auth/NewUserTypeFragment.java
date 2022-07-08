package com.example.dogwalker.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dogwalker.R;
import com.example.dogwalker.fragments.BaseFragment;


public class NewUserTypeFragment extends BaseFragment {


    Button dogOwnerBtn, dogWalkerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_user_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        attachListeners();
    }


    private void findViews(View view) {
        dogOwnerBtn = view.findViewById(R.id.toRegister_DogOwnerBtn);
        dogWalkerBtn = view.findViewById(R.id.toRegister_DogWalkerBtn);
    }

    private void attachListeners() {
        dogOwnerBtn.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putBoolean(RegisterFragment.DOG_WALKER, false);
            navigate(R.id.action_newUserTypeFragment_to_registerFragment, args);
        });
        dogWalkerBtn.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putBoolean(RegisterFragment.DOG_WALKER, true);
            navigate(R.id.action_newUserTypeFragment_to_registerFragment, args);
        });
    }
}