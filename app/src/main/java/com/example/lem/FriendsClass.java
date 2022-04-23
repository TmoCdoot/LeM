package com.example.lem;

import java.io.Serializable;

public class FriendsClass implements Serializable {

    private String friend_user_id;
    private String friend_pseudo;
    private String friend_activity_id_create;
    private String friend_activity_id_join;

    public FriendsClass(String friend_user_id, String friend_pseudo, String friend_activity_id_create, String friend_activity_id_join) {
        this.friend_user_id = friend_user_id;
        this.friend_pseudo = friend_pseudo;
        this.friend_activity_id_create = friend_activity_id_create;
        this.friend_activity_id_join = friend_activity_id_join;
    }

    public String getFriend_user_id() {
        return friend_user_id;
    }

    public String getFriend_pseudo() {
        return friend_pseudo;
    }

    public String getFriend_activity_id_create() {
        return friend_activity_id_create;
    }

    public String getFriend_activity_id_join() {
        return friend_activity_id_join;
    }

}
