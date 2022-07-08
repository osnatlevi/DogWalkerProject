package com.example.dogwalker.fragments.main;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.PermissionsManager;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class SettingsFragment extends BaseFragment {


    EditText phoneEt, addressEt, infoEt, nameEt;
    ImageView userImage;
    Button saveChangesBtn;
    Uri selectedImageUri;

    protected ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageData = result.getData().getData();
                    userImage.setImageURI(imageData);
                    selectedImageUri = imageData;
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        attachListeners();
        if (FirebaseManager.currentUser != null) {
            phoneEt.setText(FirebaseManager.currentUser.getPhoneNumber());
            infoEt.setText(FirebaseManager.currentUser.getOtherInfo());
            nameEt.setText(FirebaseManager.currentUser.getFullName());
            addressEt.setText(FirebaseManager.currentUser.getAddress().getName());
            if (!FirebaseManager.currentUser.getImageAddress().equals("undefined")) {
                Glide.with(this).load(FirebaseManager.currentUser.getImageAddress()).into(userImage);
            }
        }
    }

    private void findViews(View view) {
        phoneEt = view.findViewById(R.id.user_phone_settings_et);
        infoEt = view.findViewById(R.id.user_info_settings_et);
        nameEt = view.findViewById(R.id.user_name_settings_et);
        addressEt = view.findViewById(R.id.user_address_settings_et);
        userImage = view.findViewById(R.id.user_image_settings_iv);
        saveChangesBtn = view.findViewById(R.id.save_changes_settings_btn);
    }

    private boolean isValidFields() {
        if (addressEt.getText().toString().isEmpty()) return false;
        if (nameEt.getText().toString().isEmpty()) return false;
        if (phoneEt.getText().toString().isEmpty()) return false;
        return true;
    }


    private void attachListeners() {
        userImage.setOnClickListener(view -> {
            assert getActivity() != null;
            boolean hasPermissions = PermissionsManager.askPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasPermissions) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
                imagePickerIntent.setType("image/*");
                activityResultLauncher.launch(imagePickerIntent);
            }
        });
        saveChangesBtn.setOnClickListener(view -> {
            User currentUser = FirebaseManager.currentUser;
            if (isValidFields()) {
                currentUser.getAddress().setName(addressEt.getText().toString());
                currentUser.setFullName(nameEt.getText().toString());
                currentUser.setOtherInfo(infoEt.getText().toString());
                currentUser.setPhoneNumber(phoneEt.getText().toString());
                showLoading("Saving changes..");
                FirebaseManager.saveUser(currentUser, selectedImageUri, unused -> {
                    stopLoading();
                    showToast("Successfully saved changes");
                    back();
                }, e -> {
                    stopLoading();
                    showToast("There was a problem saving the changes..");
                });
            }
        });
    }
}