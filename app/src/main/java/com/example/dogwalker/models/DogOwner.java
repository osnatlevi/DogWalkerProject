package com.example.dogwalker.models;

public class DogOwner extends User {

    public DogOwner(String id, String fullName, String phoneNumber, Address address,
                    String email, String imageAddress, String otherInfo, int age,
                    boolean extraPurchased) {
        super(id, fullName, phoneNumber, address, email, imageAddress, otherInfo, age,
                extraPurchased);
    }
    public DogOwner(String fullName, String phoneNumber, Address address, String email,
                    String imageAddress, String otherInfo, int age,boolean premium,
                    boolean extraPurchased) {
        super(fullName, phoneNumber, address, email, imageAddress, otherInfo, age,premium,
                extraPurchased);
    }

    public DogOwner() {

    }
}
