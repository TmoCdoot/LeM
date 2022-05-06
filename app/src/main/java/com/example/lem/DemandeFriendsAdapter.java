package com.example.lem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DemandeFriendsAdapter extends RecyclerView.Adapter<DemandeFriendsHolder> {
    private final List<FriendsClass> demandeFriends;

    public DemandeFriendsAdapter(List<FriendsClass> d) {
        demandeFriends = d;
    }

    @NonNull
    @Override
    public DemandeFriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View demandeFriendRow = inflater.inflate(R.layout.row_view_demande_friends, parent, false);

        DemandeFriendsHolder viewHolder = new DemandeFriendsHolder(demandeFriendRow);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DemandeFriendsHolder holder, int position) {
        FriendsClass friend = demandeFriends.get(position);
        holder.bindData(friend);
    }

    @Override
    public int getItemCount() {
        if (demandeFriends == null) {
            return 0;
        } else {
            return demandeFriends.size();
        }
    }
}
