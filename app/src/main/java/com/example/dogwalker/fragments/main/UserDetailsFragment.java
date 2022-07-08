package com.example.dogwalker.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.DogWalker;
import com.example.dogwalker.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

public class UserDetailsFragment extends BaseFragment {


    TextView userTypeTv, userNameTv, userInfoTv, userPhoneTv, userAddressTv;
    Button showDogsBtn;
    ImageView userImage;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        attachListeners();
        if (getArguments() != null) {
            String userId = getArguments().getString("userId");
            boolean dogOwner = getArguments().getBoolean("dogOwner");
            if (!dogOwner) {
                showDogsBtn.setVisibility(View.GONE);
            }
            FirebaseManager.addListenerToUserById(userId, dogOwner,
                    userObject -> {
                        this.user = userObject;

                        if (!userObject.getImageAddress().equals("undefined") && getContext() != null)
                            Glide.with(getContext()).load(userObject.getImageAddress()).into(userImage);
                        userTypeTv.setText(dogOwner ? "Dog Owner" : "Dog Walker");
                        userNameTv.setText(user.getFullName());
                        userInfoTv.setText("Bio: " + user.getOtherInfo());
                        userAddressTv.setText("Address: " + user.getAddress().getName());
                        userPhoneTv.setText("Phone number: " + user.getPhoneNumber());
                    }, e -> showToast("There was a problem loading user details"));
        }
    }

    private void findViews(View view) {
        userTypeTv = view.findViewById(R.id.user_type_details_tv);
        userNameTv = view.findViewById(R.id.user_name_details_tv);
        userInfoTv = view.findViewById(R.id.user_info_details_tv);
        userPhoneTv = view.findViewById(R.id.user_phone_details_tv);
        userAddressTv = view.findViewById(R.id.user_address_details_tv);
        showDogsBtn = view.findViewById(R.id.show_dogs_user_detailsBtn);
        userImage = view.findViewById(R.id.user_image_details_iv);
    }

    private void attachListeners() {
        showDogsBtn.setOnClickListener(view -> {
            if (user == null) return;
            Bundle b = new Bundle();
            b.putString("userId", user.getId());
            navigate(R.id.action_userDetailsFragment_to_dogsListFragment, b);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (user != null) {
            boolean dogOwner = user instanceof DogOwner;
            FirebaseManager.removeListenerFromUser(user.getId(), dogOwner);
        }
    }
}
