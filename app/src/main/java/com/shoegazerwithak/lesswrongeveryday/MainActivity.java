package com.shoegazerwithak.lesswrongeveryday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentPost.ArtistsFragmentInteractionListener {
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
    public void onListItemClick(Article article) {
        Intent articleActivity = new Intent(this, ArticleViewActivity.class);
        articleActivity.putExtra(Constants.BUNDLE_ARTICLE_NAME, article);
        Log.d("Item Click", String.valueOf(article));
        startActivity(articleActivity);
    }
}
