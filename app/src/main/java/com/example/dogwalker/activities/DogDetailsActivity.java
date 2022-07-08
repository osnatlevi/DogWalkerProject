package com.example.dogwalker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.models.Dog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class DogDetailsActivity extends BaseActivity {

    ImageView dogIv;
    TextView dogNameTv, ageTv, typeTv, otherTv;

    FloatingActionButton editDogBtn;
    Dog dog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_details);
        findViews();
        attachListeners();
        if (getIntent() != null) {
            String dogJson = getIntent().getStringExtra("dog");
            Gson g = new Gson();
            dog = g.fromJson(dogJson, Dog.class);
            if (dog.getImageAddress().equals("undefined")) {
                dogIv.setImageResource(R.drawable.dog);
            } else {
                Glide.with(this).load(dog.getImageAddress()).into(dogIv);
            }
            dogNameTv.setText(dog.getName());
            ageTv.setText(dog.getAge());
            typeTv.setText(dog.getKind());
            otherTv.setText(dog.getInfo());
            FirebaseManager.addListenerToDogInfo(dog, dog -> {
                if (dog.getImageAddress().equals("undefined")) {
                    dogIv.setImageResource(R.drawable.dog);
                } else {
                    Glide.with(this).load(dog.getImageAddress()).into(dogIv);
                }
                dogNameTv.setText(dog.getName());
                ageTv.setText(dog.getAge());
                typeTv.setText(dog.getKind());
                otherTv.setText(dog.getInfo());
            }, e -> Toast.makeText(this,"Could not load dog info.. check your internet connection",Toast.LENGTH_SHORT).show());
        }
        if (dog == null || !dog.getOwnerId().equals(FirebaseAuth.getInstance().getUid())) {
            editDogBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dog!=null) {
            FirebaseManager.removeDogValueEventListener(dog);
        }
    }

    private void attachListeners() {
        editDogBtn.setOnClickListener(view -> {
            if (dog != null && dog.getOwnerId().equals(FirebaseAuth.getInstance().getUid())) {
                Intent editDogIntent = new Intent(this,AddDogActivity.class);
                editDogIntent.putExtra("dog",new Gson().toJson(dog));
                startActivity(editDogIntent);
            }
        });
    }

    private void findViews() {
        dogIv = findViewById(R.id.dog_iv_dogDetails);
        dogNameTv = findViewById(R.id.dog_name_tv_dogDetails);
        ageTv = findViewById(R.id.dog_age_tv_dogDetails);
        typeTv = findViewById(R.id.dog_type_tv_dogDetails);
        otherTv = findViewById(R.id.dog_other_tv_dogDetails);
        editDogBtn = findViewById(R.id.edit_dog_details_btn);
    }
}
