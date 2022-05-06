package com.example.lem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DemandeFriendsHolder extends RecyclerView.ViewHolder {
    TextView pseudoFriend;

    public DemandeFriendsHolder(View sous_vue) {
        super(sous_vue);
        pseudoFriend = sous_vue.findViewById(R.id.pseudoDemandeur);
    }

    public void bindData(final FriendsClass friend) {
        pseudoFriend.setText(friend.getFriend_pseudo());
    }
}
