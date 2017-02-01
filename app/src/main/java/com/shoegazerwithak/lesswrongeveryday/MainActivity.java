package com.shoegazerwithak.lesswrongeveryday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentPost.ArtistsFragmentInteractionListener {
    Context context;
    private FragmentManager mFragmentManager;
    private FragmentPost mFragmentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        mFragmentPost = FragmentPost.newInstance();
        setFragment(mFragmentPost);
    }

    private void setFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onListItemClick(Map<String, String> item) {
        Intent detailsActivity = new Intent(this, ArticleViewActivity.class);
        detailsActivity.putExtra("text", item.get("text"));
        detailsActivity.putExtra("link", item.get("link"));
        startActivity(detailsActivity);
//        Toast.makeText(context, String.valueOf(item), Toast.LENGTH_LONG).show();
//        Log.e("Item Click Position", String.valueOf(item));
    }
}
