package com.shoegazerwithak.lesswrongeveryday;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentError;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;
import com.shoegazerwithak.lesswrongeveryday.ui.PlanetFragment;
import com.shoegazerwithak.lesswrongeveryday.utils.ConnectivityBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements FragmentPost.ArtistsFragmentInteractionListener {
    private android.app.FragmentManager mFragmentManager;
    private FragmentPost mFragmentPost;
    private PlanetFragment mPlanetFragment;
    private TextView mOfflineModeBanner;

    private ConnectivityBroadcastReceiver mReceiver;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    protected OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        mOfflineModeBanner = (TextView) findViewById(R.id.textview_offline_mode_main);
        mFragmentManager = getFragmentManager();
        mFragmentPost = FragmentPost.newInstance();

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new MainActivity.DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                new Toolbar(MainActivity.this),  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

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

    private void setFragment(android.app.Fragment fragment) {
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
    public void onListItemClick(Article article, String nextTitle) {
        Intent articleActivity = new Intent(this, ArticleViewActivity.class);
        articleActivity.putExtra(Constants.BUNDLE_ARTICLE_NAME, article);
        articleActivity.putExtra(Constants.BUNDLE_NEXT_INDEX, nextTitle);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        mPlanetFragment = com.shoegazerwithak.lesswrongeveryday.ui.PlanetFragment.newInstance();
        Bundle args = new Bundle();
        args.putInt(com.shoegazerwithak.lesswrongeveryday.ui.PlanetFragment.ARG_PLANET_NUMBER, position);
        mPlanetFragment.setArguments(args);
        mFragmentManager.beginTransaction().replace(R.id.container, mPlanetFragment).addToBackStack(null).commit();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.onBackPressed();
        else
            super.onBackPressed();
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public static class BaseBackPressedListener implements OnBackPressedListener {
        private final Activity activity;

        public BaseBackPressedListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onBackPressed() {
            activity.getFragmentManager().popBackStackImmediate();
            activity.setTitle(R.string.app_name);
        }
    }

}
