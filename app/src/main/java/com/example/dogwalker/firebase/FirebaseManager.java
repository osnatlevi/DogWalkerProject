package com.example.dogwalker.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.dogwalker.models.Dog;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.DogWalker;
import com.example.dogwalker.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    //storage
    private static final String USERS_IMAGES_PATH = "/UserImages/";
    private static final String DOGS_IMAGES_PATH = "/DogsImages/";

    // database
    private static final String USERS_PATH = "Users";
    private static final String USERS_DOGWALKER_PATH = "DogWalkers";
    private static final String USERS_DOGOWNER_PATH = "DogOwners";
    private static final String DOGS_PATH = "Dogs";

    public static User currentUser;

    private static ValueEventListener currentUserValueEventListener;
    private static ValueEventListener userValueEventListener;
    private static ValueEventListener dogWalkersEventListener;
    private static ValueEventListener dogOwnersEventListener;
    private static ValueEventListener userDogsValueEventListener;
    private static ValueEventListener dogValueEventListener;


    public static void addListenerToCurrentUser(boolean dogOwner) {
        String userId = FirebaseAuth.getInstance().getUid();
        assert userId != null;

        currentUserValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (dogOwner)
                    currentUser = snapshot.getValue(DogOwner.class);
                else
                    currentUser = snapshot.getValue(DogWalker.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance()
                .getReference(USERS_PATH)
                .child(dogOwner ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                .child(userId)
                .addValueEventListener(currentUserValueEventListener);
    }

    public static void removeListenerFromCurrentUser() {
        if (currentUserValueEventListener != null && currentUser != null) {
            FirebaseDatabase.getInstance()
                    .getReference(USERS_PATH)
                    .child((currentUser instanceof DogOwner) ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                    .child(currentUser.getId())
                    .removeEventListener(currentUserValueEventListener);
        }
    }


    public static void removeListenerFromUser(String uid, boolean dogOwner) {
        if (userValueEventListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(USERS_PATH)
                    .child(dogOwner ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                    .child(uid)
                    .removeEventListener(userValueEventListener);
        }
    }

    public static void addListenerToUserById(String uid, boolean dogOwner,
                                             OnSuccessListener<User> userOnSuccessListener, OnFailureListener onFailureListener) {
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (dogOwner)
                    userOnSuccessListener.onSuccess(snapshot.getValue(DogOwner.class));
                else
                    userOnSuccessListener.onSuccess(snapshot.getValue(DogWalker.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onFailureListener.onFailure(error.toException());
            }
        };

        FirebaseDatabase.getInstance()
                .getReference(USERS_PATH)
                .child(dogOwner ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                .child(uid)
                .addValueEventListener(userValueEventListener);
    }


    // register new user
    public static void registerNewUser(User user,
                                       String password,
                                       Uri userImage,
                                       OnSuccessListener<Void> onSuccessListener,
                                       OnFailureListener onFailureListener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                user.getEmail(),
                password
        ).addOnSuccessListener(authResult -> {
            assert authResult.getUser() != null;
            boolean isDogOwner = user instanceof DogOwner;
            // create new reference for the user
            DatabaseReference newUserRef = FirebaseDatabase.getInstance()
                    .getReference(USERS_PATH)
                    .child(isDogOwner ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                    .child(authResult.getUser().getUid());

            // set the new id for the user from firebase
            user.setId(authResult.getUser().getUid());

            if (userImage != null) {

                // create new image reference
                StorageReference storageRef = FirebaseStorage.getInstance()
                        .getReference(USERS_IMAGES_PATH + user.getId());

                // upload image to new storage reference
                storageRef.putFile(userImage)
                        .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    // set new image address to user
                                    user.setImageAddress(uri.toString());
                                    // insert the user to the new reference
                                    newUserRef.setValue(user)
                                            .addOnSuccessListener(onSuccessListener)
                                            .addOnFailureListener(onFailureListener);
                                }).addOnFailureListener(onFailureListener))
                        .addOnFailureListener(onFailureListener);
            } else {
                // no image
                user.setImageAddress("undefined");
                // insert the user to the new reference
                newUserRef.setValue(user)
                        .addOnSuccessListener(onSuccessListener)
                        .addOnFailureListener(onFailureListener);
            }
            FirebaseManager.currentUser = user;
        }).addOnFailureListener(onFailureListener);
    }


    public static void loginUser(String email,
                                 String password,
                                 OnSuccessListener<User> onSuccessListener,
                                 OnFailureListener onFailureListener) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(USERS_PATH);
                    usersRef.child(USERS_DOGOWNER_PATH)
                            .child(authResult.getUser().getUid())
                            .get()
                            .addOnSuccessListener(dataSnapshot -> {
                                if (!dataSnapshot.exists()) {
                                    usersRef.child(USERS_DOGWALKER_PATH)
                                            .child(authResult.getUser().getUid())
                                            .get()
                                            .addOnSuccessListener(dataSnapshot1 -> {
                                                if (!dataSnapshot1.exists()) {
                                                    onFailureListener.onFailure(new Exception("User not found"));
                                                } else {

                                                    DogWalker user = dataSnapshot1.getValue(DogWalker.class);
                                                    onSuccessListener.onSuccess(user);
                                                    FirebaseManager.currentUser = user;
                                                }
                                            }).addOnFailureListener(onFailureListener);

                                } else {
                                    DogOwner user = dataSnapshot.getValue(DogOwner.class);
                                    onSuccessListener.onSuccess(user);
                                    FirebaseManager.currentUser = user;
                                }
                            }).addOnFailureListener(onFailureListener);
                }).addOnFailureListener(onFailureListener);
    }


    // save existing user data
    public static void saveUser(User user,
                                Uri imageUri,
                                OnSuccessListener<Void> onSuccessListener,
                                OnFailureListener onFailureListener) {
        boolean isDogOwner = user instanceof DogOwner;
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(USERS_PATH)
                .child(isDogOwner ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                .child(user.getId());
        if (imageUri != null) {
            // create new image reference
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference(USERS_IMAGES_PATH + user.getId());

            // upload image to new storage reference
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // set new image address to user
                                user.setImageAddress(uri.toString());
                                // insert the user to the new reference
                                userRef.setValue(user)
                                        .addOnSuccessListener(onSuccessListener)
                                        .addOnFailureListener(onFailureListener);
                            }).addOnFailureListener(onFailureListener))
                    .addOnFailureListener(onFailureListener);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(USERS_PATH)
                    .child(isDogOwner ? USERS_DOGOWNER_PATH : USERS_DOGWALKER_PATH)
                    .child(user.getId())
                    .setValue(user)
                    .addOnFailureListener(onFailureListener)
                    .addOnSuccessListener(onSuccessListener);
        }

    }


    public static void addListenerToDogInfo(Dog dog, OnSuccessListener<Dog> dogOnSuccessListener, OnFailureListener onFailureListener) {
        dogValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dogOnSuccessListener.onSuccess(snapshot.getValue(Dog.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onFailureListener.onFailure(error.toException());
            }
        };
        FirebaseDatabase.getInstance()
                .getReference(DOGS_PATH)
                .child(dog.getOwnerId())
                .child(dog.getId())
                .addValueEventListener(dogValueEventListener);
    }

    public static void removeDogValueEventListener(Dog dog) {
        if (dogValueEventListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(DOGS_PATH)
                    .child(dog.getOwnerId())
                    .child(dog.getId())
                    .removeEventListener(dogValueEventListener);
        }
    }

    public static void addListenerToDogOwners(OnSuccessListener<List<DogOwner>> onSuccessListener,
                                              OnFailureListener onFailureListener) {

        dogOwnersEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DogOwner> dogOwners = new ArrayList<>();
                for (DataSnapshot dogOwnerSnapshot : snapshot.getChildren())
                    dogOwners.add(dogOwnerSnapshot.getValue(DogOwner.class));
                onSuccessListener.onSuccess(dogOwners);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onFailureListener.onFailure(error.toException());
            }
        };

        FirebaseDatabase.getInstance()
                .getReference(USERS_PATH)
                .child(USERS_DOGOWNER_PATH)
                .addValueEventListener(dogOwnersEventListener);
    }

    public static void addListenerToDogWalkers(OnSuccessListener<List<DogWalker>> onSuccessListener,
                                               OnFailureListener onFailureListener) {
        dogWalkersEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DogWalker> dogWalkers = new ArrayList<>();
                for (DataSnapshot dogWalkerSnapshot : snapshot.getChildren())
                    dogWalkers.add(dogWalkerSnapshot.getValue(DogWalker.class));
                onSuccessListener.onSuccess(dogWalkers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onFailureListener.onFailure(error.toException());
            }
        };

        FirebaseDatabase.getInstance()
                .getReference(USERS_PATH)
                .child(USERS_DOGWALKER_PATH)
                .addValueEventListener(dogWalkersEventListener);
    }

    public static void removeDogOwnersValueEventListener() {
        if (dogOwnersEventListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(USERS_PATH)
                    .child(USERS_DOGOWNER_PATH)
                    .removeEventListener(dogOwnersEventListener);
        }
    }

    public static void removeDogWalkersValueEventListener() {
        if (dogWalkersEventListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(USERS_PATH)
                    .child(USERS_DOGWALKER_PATH)
                    .removeEventListener(dogWalkersEventListener);
        }
    }

    public static void addListenerToUserDogs(String userId,
                                             OnSuccessListener<List<Dog>> onSuccessListener,
                                             OnFailureListener onFailureListener) {
        userDogsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Dog> dogs = new ArrayList<>();
                for (DataSnapshot dogSnapshot : snapshot.getChildren())
                    dogs.add(dogSnapshot.getValue(Dog.class));
                onSuccessListener.onSuccess(dogs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onFailureListener.onFailure(error.toException());
            }
        };
        FirebaseDatabase.getInstance()
                .getReference(DOGS_PATH)
                .child(userId)
                .addValueEventListener(userDogsValueEventListener);
    }

    public static void removeUserDogsValueEventListener(String userId) {
        if (userDogsValueEventListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(DOGS_PATH)
                    .child(userId)
                    .removeEventListener(userDogsValueEventListener);
        }
    }

    public static class DogOwnerManager {


        // save new dog
        public static void deleteDog(String dogId,
                                     OnSuccessListener<Void> onSuccessListener,
                                     OnFailureListener onFailureListener) {
            String userId = FirebaseAuth.getInstance().getUid();
            assert userId != null;
            FirebaseDatabase.getInstance()
                    .getReference(DOGS_PATH)
                    .child(userId)
                    .child(dogId)
                    .removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }


        // save new dog
        public static void saveDog(Dog dog,
                                   Uri dogImage,
                                   OnSuccessListener<Void> onSuccessListener,
                                   OnFailureListener onFailureListener) {
            String userId = FirebaseAuth.getInstance().getUid();
            assert userId != null;
            DatabaseReference dogRef;
            if (dog.getId() == null) {
                dogRef = FirebaseDatabase.getInstance()
                        .getReference(DOGS_PATH)
                        .child(userId)
                        .push();
                dog.setId(dogRef.getKey());
            } else {
                dogRef = FirebaseDatabase.getInstance()
                        .getReference(DOGS_PATH)
                        .child(userId)
                        .child(dog.getId());

            }

            if (dogImage != null) {
                StorageReference newDogImageRef = FirebaseStorage.getInstance()
                        .getReference(DOGS_IMAGES_PATH + dog.getId());

                newDogImageRef.putFile(dogImage)
                        .addOnSuccessListener(taskSnapshot -> newDogImageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    dog.setImageAddress(uri.toString());
                                    dogRef.setValue(dog)
                                            .addOnSuccessListener(onSuccessListener)
                                            .addOnFailureListener(onFailureListener);
                                }).addOnFailureListener(onFailureListener)).addOnFailureListener(onFailureListener);

            } else {
                if (dog.getImageAddress() == null)
                    dog.setImageAddress("undefined");
                dogRef.setValue(dog)
                        .addOnSuccessListener(onSuccessListener)
                        .addOnFailureListener(onFailureListener);
            }
        }
    }

    public static boolean isDogOwner() {
        return currentUser instanceof DogOwner;
    }

    public static boolean isDogWalker() {
        return !isDogOwner();
    }

}
