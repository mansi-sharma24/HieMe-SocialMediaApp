package com.example.socialmediaapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.socialmediaapp.Adapter.FollowersAdapter;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.FragmentUserBinding;
import com.example.socialmediaapp.models.Follow;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserFragment extends Fragment {
    FragmentUserBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ArrayList<Follow> list;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false);
        //Fetching data from database
        database.getReference().child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getCover_img()).placeholder(R.drawable.empty_image).into(binding.coverImg);
                    Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(binding.profileImage);
                    binding.userName.setText(user.getName());
                    binding.aboutUser.setText(user.getProfession());
                    binding.bio.setText(user.getBio());
                    binding.followersNo.setText(user.getFollowerCount() + "");
                    binding.CommentNo.setText(user.getCommentCount() + "");
                    binding.likeNo.setText(user.getPostLike() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();

        FollowersAdapter adapter = new FollowersAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.friendsRv.setLayoutManager(linearLayoutManager);
        binding.friendsRv.setAdapter(adapter);
        database.getReference().child("users").child(auth.getUid()).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Follow follow = dataSnapshot.getValue(Follow.class);
                    list.add(follow);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.changeCoverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });
        binding.verifiedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 22);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.coverImg.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("cover_img").child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover Photo Saved", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("users").child(auth.getUid()).child("cover_img").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }
        else {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    binding.profileImage.setImageURI(uri);
                    final StorageReference reference = storage.getReference().child("profile_img").child(FirebaseAuth.getInstance().getUid());
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Profile Photo Saved", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("users").child(auth.getUid()).child("profile_img").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }
            }
        }
    }