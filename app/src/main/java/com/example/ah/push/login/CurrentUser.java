package com.example.ah.push.login;


public class CurrentUser {
    public String email;
    public String devices;

    public  CurrentUser(){

    }

    public CurrentUser(String email, String devices){
        this.email = email;
        this.devices = devices;
    }
}
