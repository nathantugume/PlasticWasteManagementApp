package com.ugwebstudio.plasticwastemanagementapp.classes;

public class User {
    private String fullName;
    private String email;
    private String phone;
    private String accountType;
    private String id;

    private boolean isFirstLogin;


    public User() {
        //empty for firebase use
    }

    public User(String fullName, String email, String phone, String accountType, boolean isFirstLogin) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.accountType = accountType;
        this.isFirstLogin = isFirstLogin;
    }

    //constructor to update users

    public User(String fullName, String email, String phone, String accountType, String id, boolean isFirstLogin) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.accountType = accountType;
        this.id = id;
        this.isFirstLogin = isFirstLogin;
    }


    // Add getters and setters as needed


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }
}
