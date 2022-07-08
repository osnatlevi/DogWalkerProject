package com.example.dogwalker.models;

public class Dog {

    private String id;
    private String name;
    private String kind;
    private String info;
    private String imageAddress;
    private String age;
    private String ownerId;

    public Dog(String id, String ownerId, String name, String kind, String info, String age, String imageAddress) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.info = info;
        this.imageAddress = imageAddress;
        this.age = age;
        this.ownerId = ownerId;
    }

    public Dog(String ownerId, String name, String kind, String info, String age, String imageAddress) {
        this.name = name;
        this.kind = kind;
        this.info = info;
        this.imageAddress = imageAddress;
        this.age = age;
        this.ownerId = ownerId;
    }

    //firebase constructor
    public Dog() {

    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public String getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }
}
