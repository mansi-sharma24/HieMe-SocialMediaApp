package com.example.socialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.socialmediaapp.Adapter.CommentAdapter;
import com.example.socialmediaapp.databinding.ActivityCommentBinding;
import com.example.socialmediaapp.models.Comment;
import com.example.socialmediaapp.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
ActivityCommentBinding binding;
Intent intent;
String postId;
String postedBy;
FirebaseAuth auth;
FirebaseDatabase database;
ArrayList<Comment> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get().load(post.getPostImage()).placeholder(R.drawable.empty_image).into(binding.postImage);
                binding.postDescription.setText(post.getPostDescription());
                binding.likePost.setText(post.getPostLike()+"");
                binding.commentPost.setText(post.getCommentCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("users").child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(binding.profileImage);
                binding.userName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.commentSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setCommentBody(binding.commentET.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
                database.getReference().child("posts").child(postId).child("comments").push().setValue(comment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("posts").child(postId).child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int commentCount = 0 ;
                                        if (snapshot.exists()){
                                            commentCount = snapshot.getValue(Integer.class);
                                        }
                                        database.getReference().child("posts").child(postId).child("commentCount").setValue(commentCount+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.commentET.setText("");
                                                Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
            }
        });
        CommentAdapter adapter = new CommentAdapter(this,list);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        binding.commentRV.setLayoutManager(layoutManager);
        binding.commentRV.setAdapter(adapter);

        database.getReference().child("posts").child(postId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
               for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                   Comment comment = dataSnapshot.getValue(Comment.class);
                   list.add(comment);
               }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}