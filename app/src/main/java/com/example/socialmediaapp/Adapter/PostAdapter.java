package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.CommentActivity;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.DashboardRvSampleBinding;
import com.example.socialmediaapp.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.viewHolder> {
    ArrayList<Post> list;
    Context context;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_sample,parent,false);
        return new viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Post model = list.get(position);
        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.empty_image).into(holder.binding.postImage);
        holder.binding.likePost.setText(model.getPostLike()+"");
        holder.binding.commentPost.setText(model.getCommentCount()+"");
        String description = model.getPostDescription();
        if ( description.equals("")){
            holder.binding.postAbout.setVisibility(View.GONE);
        }
        else {
            holder.binding.postAbout.setText(model.getPostDescription());
            holder.binding.postAbout.setVisibility(View.VISIBLE);
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(holder.binding.profileImage);
                holder.binding.userNamePost.setText(user.getName());
                holder.binding.aboutPost.setText(user.getProfession());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).child("likes").child(FirebaseAuth.getInstance().getUid())
                 .addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()) {
                     holder.binding.likePost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_post_red, 0, 0, 0);
                 }
                 else
                 {
                     holder.binding.likePost.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             FirebaseDatabase.getInstance().getReference().child("posts")
                                     .child(model.getPostId()).child("likes").child(FirebaseAuth.getInstance().getUid())
                                     .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).child("postLike").setValue(model.getPostLike()+1)
                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void unused) {
                                                     holder.binding.likePost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_post_red,0,0,0);
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
         holder.binding.commentPost.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(context, CommentActivity.class);
                 intent.putExtra("postId",model.getPostId());
                 intent.putExtra("postedBy",model.getPostedBy());
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 context.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder  extends RecyclerView.ViewHolder{
        DashboardRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvSampleBinding.bind(itemView);
        }
    }
}
