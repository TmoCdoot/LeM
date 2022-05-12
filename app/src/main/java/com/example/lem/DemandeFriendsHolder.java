package com.example.lem;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DemandeFriendsHolder extends RecyclerView.ViewHolder {
    TextView pseudoFriend;

    Button buttonAccept, buttonDecline;


    public DemandeFriendsHolder(View sous_vue, final DemandeFriendsInterface listener) {
        super(sous_vue);
        pseudoFriend = sous_vue.findViewById(R.id.pseudoDemandeur);

        buttonAccept = sous_vue.findViewById(R.id.buttonAccepterDemande);
        buttonDecline = sous_vue.findViewById(R.id.buttonRefuserDemande);

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.acceptDemande(getAdapterPosition(), v);
            }
        });

        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.declineDemande(getAdapterPosition(), v);
            }
        });
    }

    public void bindData(final FriendsClass friend) {
        pseudoFriend.setText(friend.getFriend_pseudo());
    }
}
