package com.shoegazerwithak.lesswrongeveryday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Boolean value = data.getExtras().getBoolean("Filter");
        if (requestCode == RESULT_FIRST_USER && resultCode == RESULT_OK && value) {
            mFragmentPost.doneCallback();
        }
    }

    @Override
//    public void onListItemClick(Article article, View view) {
    public void onListItemClick(Article article) {
        Intent articleActivity = new Intent(this, ArticleViewActivity.class);
        articleActivity.putExtra(Constants.BUNDLE_ARTICLE_NAME, article);
        startActivityForResult(articleActivity, 1);
    }
}
