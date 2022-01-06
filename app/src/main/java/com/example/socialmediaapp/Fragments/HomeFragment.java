package com.example.socialmediaapp.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Adapter.PostAdapter;
import com.example.socialmediaapp.Adapter.StoryAdapter;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.FragmentAddBinding;
import com.example.socialmediaapp.databinding.FragmentHomeBinding;
import com.example.socialmediaapp.models.Post;
import com.example.socialmediaapp.models.Story;
import com.example.socialmediaapp.models.UserStory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    RecyclerView storyRV,dashboardRV;
    ArrayList<Story> storyList;
    ArrayList<Post> PostList;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    RoundedImageView story;
    ProgressDialog dialog;
    ActivityResultLauncher<String> galleryLauncher;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        storyRV = view.findViewById(R.id.storyRV);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storyList = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);


        StoryAdapter adapter = new StoryAdapter(storyList,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        storyRV.setLayoutManager(linearLayoutManager);
        storyRV.setNestedScrollingEnabled(false);
        storyRV.setAdapter(adapter);
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()){
                  storyList.clear();
                  for (DataSnapshot storySnapshot : snapshot.getChildren()){
                      Story story = new Story();
                      story.setStoryBy(storySnapshot.getKey());
                      story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));
                      ArrayList<UserStory> stories = new ArrayList<>();
                      for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
                          UserStory userStory = snapshot1.getValue(UserStory.class);
                          stories.add(userStory);
                      }
                      story.setStories(stories);
                      storyList.add(story);
                  }
                  adapter.notifyDataSetChanged();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dashboardRV = view.findViewById(R.id.dashboardRV);
        PostList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(PostList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        dashboardRV.setLayoutManager(layoutManager);
        dashboardRV.setNestedScrollingEnabled(false);
        dashboardRV.setAdapter(postAdapter);



        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    PostList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        story = view.findViewById(R.id.story);
        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                story.setImageURI(result );
                dialog.show();
                final StorageReference reference = storage.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child(new Date().getTime()+"");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Story story = new Story();
                                story.setStoryAt(new Date().getTime());
                                database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("postedBy")
                                        .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        UserStory stories = new UserStory(uri.toString(),story.getStoryAt());
                                        database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("userStories")
                                                .push().setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
            });
        return view;
    }
}