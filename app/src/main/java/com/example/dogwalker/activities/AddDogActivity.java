package com.example.dogwalker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.models.Dog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class AddDogActivity extends BaseActivity {


    ImageView dogIv;
    EditText dogNameEt, ageEt, typeEt, otherEt;
    Button addDogSubmitBtn;
    Uri dogImage;
    Dog existingDog;

    protected ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageData = result.getData().getData();
                    dogIv.setImageURI(imageData);
                    dogImage = imageData;
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);
        findViews();
        attachListeners();

        if (getIntent() != null) {
            existingDog = new Gson().fromJson(getIntent().getStringExtra("dog"), Dog.class);
            if (existingDog == null) return;
            dogNameEt.setText(existingDog.getName());
            ageEt.setText(existingDog.getAge());
            typeEt.setText(existingDog.getKind());
            otherEt.setText(existingDog.getInfo());
            if (!existingDog.getImageAddress().equals("undefined"))
                Glide.with(this).load(existingDog.getImageAddress()).into(dogIv);
            addDogSubmitBtn.setText("Save changes");
        }
    }


    private void findViews() {
        dogIv = findViewById(R.id.dog_iv_addDog);
        dogNameEt = findViewById(R.id.dog_name_et_addDog);
        ageEt = findViewById(R.id.dog_age_et_addDog);
        typeEt = findViewById(R.id.dog_type_et_addDog);
        otherEt = findViewById(R.id.dog_other_et_addDog);
        addDogSubmitBtn = findViewById(R.id.addNewDogBtnSubmit);
    }

    private void attachListeners() {
        addDogSubmitBtn.setOnClickListener(view -> {
            if (isValidFields()) {
                showLoading(existingDog == null ? "Adding new dog.." : "Saving changes..");
                if (existingDog == null) {
                    existingDog = new Dog(
                            FirebaseAuth.getInstance().getUid(),
                            dogNameEt.getText().toString(),
                            typeEt.getText().toString(),
                            otherEt.getText().toString(),
                            ageEt.getText().toString(),
                            null
                    );
                } else {
                    existingDog.setAge(ageEt.getText().toString());
                    existingDog.setKind(typeEt.getText().toString());
                    existingDog.setInfo(otherEt.getText().toString());
                    existingDog.setName(dogNameEt.getText().toString());
                }

                FirebaseManager.DogOwnerManager.saveDog(existingDog, dogImage, unused -> {
                    stopLoading();
                    Toast.makeText(AddDogActivity.this.getApplicationContext(), "Successfully submitted " + existingDog.getName() + " to dog list", Toast.LENGTH_SHORT).show();
                    finish();
                }, e ->  {
                    stopLoading();
                    Toast.makeText(AddDogActivity.this, "There was an error adding the dog, please try again later", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(AddDogActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });

        dogIv.setOnClickListener(view -> {
            Intent imageSelectIntent = new Intent(Intent.ACTION_PICK);
            imageSelectIntent.setType("image/*");
            activityResultLauncher.launch(imageSelectIntent);
        });
    }

    private boolean isValidFields() {
        if (dogNameEt.getText().toString().isEmpty()) return false;
        if (ageEt.getText().toString().isEmpty()) return false;
        if (typeEt.getText().toString().isEmpty()) return false;
        return true;
    }
}
