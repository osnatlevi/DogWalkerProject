package com.example.dogwalker.models;

public class DogWalker extends User {
    private String experience;

    public DogWalker(String id, String fullName, String phoneNumber, Address address, String email, String imageAddress, String bio, int age, String experience) {
        super(id, fullName, phoneNumber, address, email, imageAddress, bio, age);
        this.experience = experience;
    }

    public DogWalker(String fullName, String phoneNumber, Address address, String email, String imageAddress, String bio, int age, String experience) {
        super(fullName, phoneNumber, address, email, imageAddress, bio, age);
        this.experience = experience;
    }

    public DogWalker() {

    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

}
