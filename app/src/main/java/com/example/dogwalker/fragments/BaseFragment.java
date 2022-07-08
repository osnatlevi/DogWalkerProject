package com.example.dogwalker.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;

    protected void showLoading(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Dog Walker");
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    protected void stopLoading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    protected void showToast(String message) {
        assert getContext() != null;
        Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void navigate(int actionId, Bundle b) {
        NavHostFragment.findNavController(this)
                .navigate(actionId, b);
    }

    protected void navigate(int actionId) {
        NavHostFragment.findNavController(this)
                .navigate(actionId);
    }

    protected void back() {
        NavHostFragment.findNavController(this)
                .popBackStack();
    }

}
