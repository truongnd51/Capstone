package com.example.capstone.model;

public class User {
    private String ID;
    private String Name;
    private String Password;
    private String Email;
    private String Phone;
    private String LibraryID;

    public User() {
    }

    public User(String ID, String name, String password, String email, String phone, String libraryID) {
        this.ID = ID;
        Name = name;
        Password = password;
        Email = email;
        Phone = phone;
        LibraryID = libraryID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getLibraryID() {
        return LibraryID;
    }

    public void setLibraryID(String libraryID) {
        LibraryID = libraryID;
    }
}
