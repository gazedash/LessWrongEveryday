package com.shoegazerwithak.lesswrongeveryday;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentError;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;
import com.shoegazerwithak.lesswrongeveryday.utils.ConnectivityBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements FragmentPost.ArtistsFragmentInteractionListener {
    private FragmentManager mFragmentManager;
    private FragmentPost mFragmentPost;
    private TextView mOfflineModeBanner;
    private ConnectivityBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOfflineModeBanner = (TextView) findViewById(R.id.textview_offline_mode_main);
        mFragmentManager = getSupportFragmentManager();
        mFragmentPost = FragmentPost.newInstance();
        setFragment(mFragmentPost);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new ConnectivityBroadcastReceiver();
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mReceiver.setConnectivityListener(new ConnectivityBroadcastReceiver.ConnectivityListener() {
            @Override
            public void onConnectionChecked(boolean available) {
                onOfflineModeEnabled(!available, false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void setFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Boolean value = data != null && data.getExtras().getBoolean("Filter", false);
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

    @Override
    public void onOfflineModeEnabled(boolean enabled, boolean performConnectivityCheck) {
        enabled = performConnectivityCheck ? enabled && mReceiver.isConnectionAvailable() : enabled;
        mOfflineModeBanner.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoadingFail() {
        FragmentError fragmentError = FragmentError.newInstance();
        setFragment(fragmentError);
        fragmentError.setOnRetryListener(new FragmentError.OnRetryListener() {
            @Override
            public void onRetry() {
                setFragment(mFragmentPost);
            }
        });
    }
}
