package com.example.dogwalker.fragments.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dogwalker.R;
import com.example.dogwalker.activities.AddDogActivity;
import com.example.dogwalker.activities.DogDetailsActivity;
import com.example.dogwalker.adapters.DogClickListener;
import com.example.dogwalker.adapters.DogsRvAdapter;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.Dog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;


public class DogsListFragment extends BaseFragment implements DogClickListener {

    RecyclerView rvDogs;
    DogsRvAdapter rvAdapter;

    FloatingActionButton addNewDogBtn;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dogs_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        attachListeners();
        rvDogs.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            FirebaseManager.addListenerToUserDogs(userId, dogs -> {
                rvAdapter = new DogsRvAdapter(dogs, this);
                rvDogs.setAdapter(rvAdapter);
            }, e -> showToast("Could not load dogs.."));
        }

        if (FirebaseManager.isDogWalker()) {
            addNewDogBtn.setVisibility(View.GONE);
        }
    }


    private void findViews(View view) {
        rvDogs = view.findViewById(R.id.dogsRv);
        addNewDogBtn = view.findViewById(R.id.addNewDogBtn);
    }

    private void attachListeners() {
        addNewDogBtn.setOnClickListener(view -> {
            Intent addDogActivity = new Intent(getContext(), AddDogActivity.class);
            startActivity(addDogActivity);
        });
    }

    @Override
    public void onClick(Dog dog) {
        Gson g = new Gson();
        String dogJson = g.toJson(dog);
        Intent dogDetailsIntent = new Intent(getContext(), DogDetailsActivity.class);
        dogDetailsIntent.putExtra("dog", dogJson);
        startActivity(dogDetailsIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userId != null)
            FirebaseManager.removeUserDogsValueEventListener(userId);
    }
}