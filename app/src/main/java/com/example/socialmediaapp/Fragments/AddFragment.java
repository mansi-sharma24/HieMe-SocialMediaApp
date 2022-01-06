package com.example.socialmediaapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.FragmentAddBinding;
import com.example.socialmediaapp.models.Post;
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

import java.util.Date;

public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    public AddFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(binding.profileImage);
                    binding.nameSearch.setText(user.getName());
                    binding.professionSearch.setText(user.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description = binding.postDescription.getText().toString();
                if (!description.isEmpty()){
                    binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.follow_button));
                    binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postButton.setEnabled(true);
                }
                else{
                    binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.follow_active_btn));
                    binding.postButton.setTextColor(getContext().getResources().getColor(R.color.dark_grey));
                    binding.postButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final StorageReference reference = storage.getReference().child("posts")
                        .child(FirebaseAuth. getInstance().getUid()).child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Post post = new Post();
                                post.setPostImage(uri.toString());
                                post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                post.setPostDescription(binding.postDescription.getText().toString());
                                post.setPostedAt(new Date().getTime());
                                database.getReference().child("posts").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Posted Succesfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), HomeFragment.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        return  binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null){
            uri = data.getData();
            binding.postImage.setImageURI(uri );
            binding.postImage.setVisibility(View.VISIBLE);
            binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.follow_button));
            binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.postButton.setEnabled(true);
        }
    }
}