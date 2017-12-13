package com.example.peter.onlinequiz.Model;

/**
 * Created by Peter on 29.11.2017.
 */

public class User {

    private String userName;
    private String password;
    private  String email;

    public User(){



    }
    public User(String userName,String password, String email)
    {
        this.userName=userName;
        this.password=password;
        this.email=email;


    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

