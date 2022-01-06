package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.FriendsRvSampleBinding;
import com.example.socialmediaapp.models.Follow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder>  {
    ArrayList<Follow> list;
    Context context;

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_rv_sample,parent,false);
        return new viewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Follow follow = list.get(position);

        FirebaseDatabase.getInstance().getReference().child("users").child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(holder.binding.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder {
        FriendsRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FriendsRvSampleBinding.bind(itemView);
        }
    }
}
