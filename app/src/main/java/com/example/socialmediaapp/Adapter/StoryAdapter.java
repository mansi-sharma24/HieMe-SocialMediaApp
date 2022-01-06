package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.User;
import com.example.socialmediaapp.databinding.StoryRvDesignBinding;
import com.example.socialmediaapp.models.Story;
import com.example.socialmediaapp.models.UserStory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    ArrayList<Story> list;
    Context context;

    public StoryAdapter(ArrayList<Story> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = list.get(position);
        if (story.getStories().size() > 0) {
            UserStory lastStory = story.getStories().get(story.getStories().size() - 1);
            Picasso.get().load(lastStory.getImage()).into(holder.binding.storyImg);
            holder.binding.statusCircleView.setPortionsCount(story.getStories().size());
            FirebaseDatabase.getInstance().getReference().child("users").child(story.getStoryBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.empty_image).into(holder.binding.profileImage);
                    holder.binding.userName.setText(user.getName());
                    holder.binding.storyImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<MyStory> myStories = new ArrayList<>();

                            for (UserStory story : story.getStories()) {
                                myStories.add(new MyStory(
                                        story.getImage()
                                ));
                            }
                            new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                    .setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(user.getName()) // Default is Hidden
                                    .setSubtitleText("") // Default is Hidden
                                    .setTitleLogoUrl(user.getProfile_img()) // Default is Hidden
                                    .setStoryClickListeners(new StoryClickListeners() {
                                        @Override
                                        public void onDescriptionClickListener(int position) {
                                            //your action
                                        }

                                        @Override
                                        public void onTitleIconClickListener(int position) {
                                            //your action
                                        }
                                    }) // Optional Listeners
                                    .build() // Must be called before calling show method
                                    .show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StoryRvDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoryRvDesignBinding.bind(itemView);
        }
    }

}
