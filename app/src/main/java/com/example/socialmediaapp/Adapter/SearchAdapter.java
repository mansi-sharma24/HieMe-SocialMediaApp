package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.SearchSampleRvBinding;
import com.example.socialmediaapp.models.Follow;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder> {
    Context context;
    ArrayList<User> list;

    public SearchAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_sample_rv,parent,false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = list.get(position);
        Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(holder.binding.profileImage);
        holder.binding.nameSearch.setText(user.getName());
        holder.binding.professionSearch.setText(user.getProfession());
        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserID()).child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
                    holder.binding.followBtn.setText("FOLLOWING");
                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.black));
                    holder.binding.followBtn.setEnabled(false);
                }
                else {
                    holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Follow follow = new Follow();
                            follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                            follow.setFollowedAt(new Date().getTime());

                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserID())
                                    .child("followers").child(FirebaseAuth.getInstance().getUid())
                                    .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserID())
                                            .child("followerCount").setValue(user.getFollowerCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
                                            holder.binding.followBtn.setText("FOLLOWING");
                                            holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.black));
                                            holder.binding.followBtn.setEnabled(false);
                                            Toast.makeText(context, "You Followed "+ user.getName(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                    });
                }
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

    public class viewHolder extends RecyclerView.ViewHolder{
        SearchSampleRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchSampleRvBinding.bind(itemView);
        }
    }
}
