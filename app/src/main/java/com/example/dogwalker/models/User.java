package com.example.dogwalker.models;

public abstract class User {

    private String id;
    private String fullName;
    private String phoneNumber;
    private Address address;
    private String email;
    private String otherInfo;
    private String imageAddress;
    private int age;
    //15.2
    private boolean premium;
    private boolean extraPurchased;


    public User(String id, String fullName, String phoneNumber, Address address,
                String email, String imageAddress, String otherInfo, int age,
                boolean extraPurchased) {
        this.fullName = fullName;
        this.id = id;
        this.imageAddress = imageAddress;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.otherInfo = otherInfo;
        this.age = age;
        this.extraPurchased = extraPurchased;
    }

    //15.2 add  this.premium = premium;
    public User(String fullName, String phoneNumber, Address address, String email,
                String imageAddress, String otherInfo, int age, boolean premium,
                boolean extraPurchased) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.imageAddress = imageAddress;
        this.address = address;
        this.email = email;
        this.otherInfo = otherInfo;
        this.age = age;
        this.premium = premium;
        this.extraPurchased = extraPurchased;
    }

    // Firebase constructor
    public User() {

    }
    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isExtraPurchased() {
        return extraPurchased;
    }

    public void setExtraPurchased(boolean extraPurchased) {
        this.extraPurchased = extraPurchased;
    }
}
