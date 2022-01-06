package com.example.socialmediaapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.socialmediaapp.Fragments.AddFragment;
import com.example.socialmediaapp.Fragments.HomeFragment;
import com.example.socialmediaapp.Fragments.SearchFragment;
import com.example.socialmediaapp.Fragments.UserFragment;
import com.example.socialmediaapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");
        auth = FirebaseAuth.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.fragment_container,new HomeFragment());
        transaction.commit();

        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i){
                    case 0:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.fragment_container,new HomeFragment());
                        break;
                    case 1:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.fragment_container,new SearchFragment());
                        break;
                    case 2:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.fragment_container,new AddFragment());
                        break;
                    case 3:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.fragment_container,new UserFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}