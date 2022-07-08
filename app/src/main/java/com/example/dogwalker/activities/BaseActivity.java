package com.example.dogwalker.activities;

import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {


    private ProgressDialog progressDialog;

    protected void showLoading(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
