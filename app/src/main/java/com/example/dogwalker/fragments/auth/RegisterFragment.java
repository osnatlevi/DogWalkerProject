package com.example.dogwalker.fragments.auth;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogwalker.PermissionsManager;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.AddressSelectionDialog;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.Address;
import com.example.dogwalker.models.Dog;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.DogWalker;
import com.example.dogwalker.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class RegisterFragment extends BaseFragment {


    public static final String DOG_WALKER = "dogWalker";
    EditText emailEt, passwordEt, fullNameEt, phoneEt, ageEt, expEt, otherEt;
    Button registerSubmitBtn;
    TextView selectedAddressTv;
    Button selectAddressBtn;
    ImageView profilePhotoIv;
    Uri selectedImageUri;
    Address selectedAddress;
    boolean dogWalkerRegistration;

    protected ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageData = result.getData().getData();
                    profilePhotoIv.setImageURI(imageData);
                    selectedImageUri = imageData;
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        attachListeners();

        if (getArguments() != null) {
            dogWalkerRegistration = getArguments().getBoolean(DOG_WALKER);
            if (!dogWalkerRegistration)
                expEt.setVisibility(View.GONE);
        }

    }

    private void findViews(View view) {
        emailEt = view.findViewById(R.id.email_et_register);
        passwordEt = view.findViewById(R.id.password_et_register);
        fullNameEt = view.findViewById(R.id.name_et_register);
        phoneEt = view.findViewById(R.id.phone_et_register);
        ageEt = view.findViewById(R.id.age_et_register);
        expEt = view.findViewById(R.id.experience_et_register);
        otherEt = view.findViewById(R.id.other_et_register);
        registerSubmitBtn = view.findViewById(R.id.register_submit_btn);
        profilePhotoIv = view.findViewById(R.id.photo_iv_register);
        selectAddressBtn = view.findViewById(R.id.selectAddressBtn);
        selectedAddressTv = view.findViewById(R.id.register_selected_address_tv);
    }

    private void attachListeners() {
        profilePhotoIv.setOnClickListener(view -> {
            assert getActivity() != null;
            boolean hasPermissions = PermissionsManager.askPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasPermissions) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
                imagePickerIntent.setType("image/*");
                activityResultLauncher.launch(imagePickerIntent);
            }
        });


        selectAddressBtn.setOnClickListener(view -> {
            if (PermissionsManager.askPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
                new AddressSelectionDialog(address -> {
                    selectedAddress = address;
                    selectedAddressTv.setText("Selected address: " + address.getName());
                }).show(getChildFragmentManager(), "addressSelect");
            }
        });
        registerSubmitBtn.setOnClickListener(view -> {
            User user;
            if (isValidFields()) {
                if (dogWalkerRegistration) {
                    user = new DogWalker(
                            fullNameEt.getText().toString(),
                            phoneEt.getText().toString(),
                            selectedAddress,
                            emailEt.getText().toString(),
                            null,
                            otherEt.getText().toString(),
                            Integer.parseInt(ageEt.getText().toString()),
                            expEt.getText().toString()
                    );
                } else {
                    user = new DogOwner(
                            fullNameEt.getText().toString(),
                            phoneEt.getText().toString(),
                            selectedAddress,
                            emailEt.getText().toString(),
                            null,
                            otherEt.getText().toString(),
                            Integer.parseInt(ageEt.getText().toString())
                    );
                }
                showLoading("Signing you up...");
                FirebaseManager.registerNewUser(user, passwordEt.getText().toString(), selectedImageUri, nothing -> {
                    stopLoading();
                    showToast("Successfully registered! you may login");
                    back();
                    back();
                }, exception -> showToast("Problem while signing up: " + exception.getMessage()));
            } else {
                showToast("Please fill all the fields");
            }

        });
    }

    private boolean isValidFields() {
        if (emailEt.getText().toString().isEmpty()) return false;
        if (passwordEt.getText().toString().isEmpty()) return false;
        if (fullNameEt.getText().toString().isEmpty()) return false;
        if (selectedAddress == null) return false;
        if (phoneEt.getText().toString().isEmpty()) return false;
        if (ageEt.getText().toString().isEmpty()) return false;
        return true;
    }


}