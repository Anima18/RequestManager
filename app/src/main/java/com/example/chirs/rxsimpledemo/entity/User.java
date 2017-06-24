package com.example.chirs.rxsimpledemo.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by jianjianhong on 2016/6/1.
 */
public class User implements Serializable {
    private String name;
    private String email;
    private int state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
