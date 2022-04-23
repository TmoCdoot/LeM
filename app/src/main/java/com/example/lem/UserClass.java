package com.example.lem;

import java.io.Serializable;

public class UserClass implements Serializable {

    private String id_user;
    private String user_email;
    private String user_geolocalisation;
    private String user_activity_id_join;
    private String user_identifiant_add_friends;
    private String user_pseudo;
    private String user_activity_id_create ;
    private String user_statut;

    public UserClass(String id_user, String  user_email, String user_activity_id_join, String user_identifiant_add_friends, String user_pseudo, String user_activity_id_create , String user_statut) {
        this.id_user = id_user;
        this.user_email = user_email;
        this.user_activity_id_join = user_activity_id_join;
        this.user_identifiant_add_friends = user_identifiant_add_friends;
        this.user_pseudo = user_pseudo;
        this.user_activity_id_create  = user_activity_id_create ;
        this.user_statut = user_statut;
    }

    public String getId_user() {
        return this.id_user;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public String getUser_geolocalisation() {
        return user_geolocalisation;
    }

    public String getUser_activity_id_join() {
        return user_activity_id_join;
    }

    public String getUser_identifiant_add_friends() {
        return user_identifiant_add_friends;
    }

    public String getUser_pseudo() {
        return user_pseudo;
    }

    public String getUser_activity_id_create() {
        return user_activity_id_create;
    }

    public String getUser_statut() {
        return user_statut;
    }
}
