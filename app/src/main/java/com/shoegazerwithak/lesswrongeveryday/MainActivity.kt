package com.shoegazerwithak.lesswrongeveryday

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

import com.shoegazerwithak.lesswrongeveryday.constants.Constants
import com.shoegazerwithak.lesswrongeveryday.model.Article
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentError
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost
import com.shoegazerwithak.lesswrongeveryday.ui.PlanetFragment
import com.shoegazerwithak.lesswrongeveryday.utils.ConnectivityBroadcastReceiver

class MainActivity : AppCompatActivity(), FragmentPost.ArtistsFragmentInteractionListener {
    private var mFragmentManager: android.app.FragmentManager? = null
    private var mFragmentPost: FragmentPost? = null
    private var mPlanetFragment: PlanetFragment? = null
    private var mOfflineModeBanner: TextView? = null

    private var mReceiver: ConnectivityBroadcastReceiver? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerList: ListView? = null

    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerTitle: CharSequence? = null
    private var mTitle: CharSequence? = null
    private var mPlanetTitles: Array<String>? = null

    private var onBackPressedListener: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        mOfflineModeBanner = findViewById(R.id.textview_offline_mode_main) as TextView
        mFragmentManager = fragmentManager
        mFragmentPost = FragmentPost.newInstance()

        mDrawerTitle = title
        mTitle = mDrawerTitle
        mPlanetTitles = resources.getStringArray(R.array.drawer_items)
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerList = findViewById(R.id.left_drawer) as ListView

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout!!.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START)
        // set up the drawer's list view with items and click listener
        mDrawerList!!.adapter = ArrayAdapter(this,
                R.layout.drawer_list_item, mPlanetTitles!!)
        mDrawerList!!.onItemClickListener = DrawerItemClickListener()

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = object : ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                Toolbar(this@MainActivity), /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            override fun onDrawerClosed(view: View?) {
                supportActionBar!!.title = mTitle
            }

            override fun onDrawerOpened(drawerView: View?) {
                supportActionBar!!.title = mDrawerTitle
            }
        }
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)

        setFragment(mFragmentPost as Fragment)
    }

    override fun onResume() {
        super.onResume()
        mReceiver = ConnectivityBroadcastReceiver()
        registerReceiver(mReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        mReceiver!!.setConnectivityListener { available -> onOfflineModeEnabled(!available, false) }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }

    private fun setFragment(fragment: android.app.Fragment) {
        mFragmentManager!!.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val value = data != null && data.extras.getBoolean("Filter", false)
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK && value) {
            mFragmentPost!!.doneCallback()
        }
    }

    override fun onListItemClick(article: Article, nextTitle: String) {
        //    public void onListItemClick(Article article) {
        val articleActivity = Intent(this, ArticleViewActivity::class.java)
        articleActivity.putExtra(Constants.BUNDLE_ARTICLE_NAME, article)
        articleActivity.putExtra(Constants.BUNDLE_NEXT_INDEX, nextTitle)
        startActivityForResult(articleActivity, 1)
    }

    override fun onOfflineModeEnabled(enabled: Boolean, performConnectivityCheck: Boolean) {
        val enabled2 = if (performConnectivityCheck) enabled && mReceiver!!.isConnectionAvailable else enabled
        if (enabled2) mOfflineModeBanner!!.visibility = View.VISIBLE else mOfflineModeBanner!!.visibility = View.GONE
    }

    override fun onLoadingFail() {
        val fragmentError = FragmentError.newInstance()
        setFragment(fragmentError)
        fragmentError.setOnRetryListener { setFragment(mFragmentPost as Fragment) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        // Handle action buttons
        return super.onOptionsItemSelected(item)
    }

    /* The click listner for ListView in the navigation drawer */
    private inner class DrawerItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            selectItem(position)
        }
    }

    private fun selectItem(position: Int) {
        // update the main content by replacing fragments
        mPlanetFragment = PlanetFragment.newInstance()
        val args = Bundle()
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position)
        mPlanetFragment!!.arguments = args
        mFragmentManager!!.beginTransaction().replace(R.id.container, mPlanetFragment).addToBackStack(null).commit()
        // update selected item and title, then close the drawer
        mDrawerList!!.setItemChecked(position, true)
        title = mPlanetTitles!![position]
        mDrawerLayout!!.closeDrawer(mDrawerList)
    }

    override fun setTitle(title: CharSequence) {
        mTitle = title
        supportActionBar!!.title = mTitle
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener!!.onBackPressed()
        else
            super.onBackPressed()
    }

    interface OnBackPressedListener {
        fun onBackPressed()
    }

    class BaseBackPressedListener(private val activity: Activity) : OnBackPressedListener {
        override fun onBackPressed() {
            activity.fragmentManager.popBackStackImmediate()
            activity.setTitle(R.string.app_name)
        }
    }

    companion object {
        fun setOnBackPressedListener(mainActivity: MainActivity, onBackPressedListener: OnBackPressedListener) {
            mainActivity.onBackPressedListener = onBackPressedListener
        }
    }
}
