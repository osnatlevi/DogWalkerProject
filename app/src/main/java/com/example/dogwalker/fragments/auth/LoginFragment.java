package com.example.dogwalker.fragments.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.dogwalker.R;
import com.example.dogwalker.activities.MainActivity;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class LoginFragment extends BaseFragment {

    EditText emailEt, passwordEt;
    Button toRegisterBtn, submitLoginBtn;

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
    }

    private void findViews(View view) {
        toRegisterBtn = view.findViewById(R.id.fromLogin_toRegisterBtn);
        submitLoginBtn = view.findViewById(R.id.login_submit_btn);
        emailEt = view.findViewById(R.id.email_et_login);
        passwordEt = view.findViewById(R.id.password_et_login);
    }


    private boolean isValidFields() {
        if (emailEt.getText().toString().isEmpty()) return false;
        if (passwordEt.getText().toString().isEmpty()) return false;
        return true;
    }
}