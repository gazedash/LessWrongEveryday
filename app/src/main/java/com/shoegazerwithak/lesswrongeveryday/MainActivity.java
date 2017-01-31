package com.shoegazerwithak.lesswrongeveryday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;

import java.util.Map;

import okhttp3.OkHttpClient;

// IMPLEMENTS INTERFACE
public class MainActivity extends AppCompatActivity implements FragmentPost.ArtistsFragmentInteractionListener {
    private FragmentManager mFragmentManager;
    private FragmentPost mFragmentArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        mFragmentArtists = FragmentPost.newInstance();
        setFragment(mFragmentArtists);
    }

    @Override
    public void onListItemClick(Map<String, String> data) {
        Intent detailsActivity = new Intent(this, ArticleViewActivity.class);
        detailsActivity.putExtra("link", data.get("link"));
        // Start the activity without animation
        startActivity(detailsActivity);
    }

    private void setFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.recyclerview1, fragment).commit();
    }
}
