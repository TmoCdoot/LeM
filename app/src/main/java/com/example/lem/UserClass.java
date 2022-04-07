package com.example.lem;

import java.io.Serializable;

public class UserClass implements Serializable {

    private String id_user;
    private String user_first_name;
    private String user_last_name;
    private String user_age;
    private String user_phone;
    private String user_email;

    public UserClass(String id_user, String user_first_name, String user_last_name, String user_age, String user_phone, String  user_email) {
        this.id_user = id_user;
        this.user_first_name = user_first_name;
        this.user_last_name = user_last_name;
        this.user_age = user_age;
        this.user_phone = user_phone;
        this.user_email = user_email;
    }

    public String getId_user() {
        return this.id_user;
    }

    public String getUser_first_name() {
        return this.user_first_name;
    }

    public String getUser_last_name() {
        return this.user_last_name;
    }

    public String getUser_age() {
        return this.user_age;
    }

    public String getUser_phone() {
        return this.user_phone;
    }

    public String getUser_email() {
        return this.user_email;
    }
}
