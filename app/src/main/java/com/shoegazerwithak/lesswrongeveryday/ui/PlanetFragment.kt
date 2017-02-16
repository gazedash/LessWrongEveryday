package com.shoegazerwithak.lesswrongeveryday.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.shoegazerwithak.lesswrongeveryday.MainActivity
import com.shoegazerwithak.lesswrongeveryday.R
import com.shoegazerwithak.lesswrongeveryday.receivers.AlarmReceiver

import java.util.Calendar

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
class PlanetFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val activity = activity
        val rootView = inflater.inflate(R.layout.fragment_planet, container, false)
        MainActivity.setOnBackPressedListener(activity as MainActivity, MainActivity.BaseBackPressedListener(activity))
        val i = arguments.getInt(ARG_PLANET_NUMBER)
        val title = resources.getStringArray(R.array.drawer_items)[i]
        //            int imageId = getResources().getIdentifier(title.toLowerCase(Locale.getDefault()),
        //                    "drawable", getActivity().getPackageName());

        // TODO: Remove?
        val lv = rootView.findViewById(android.R.id.list) as ListView
        // Change
        getActivity().title = title
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_planet)
        preferenceScreen
                .sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen
                .sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.d("tick", "1" + key)
        if (key == "pref_sync") {
            Log.d("tick", "1" + sharedPreferences.getBoolean(key, false).toString())
            val pref = findPreference(key)
            pref.summary = sharedPreferences.getBoolean(key, false).toString()
        } else {
            Log.d("key", "1" + key)
            Log.d("key", "1" + sharedPreferences.all)
            //        if (sharedPreferences.getBoolean(key, false)) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            //                if (!prefs.getBoolean("firstTime", false)) {
            val alarmIntent = Intent(activity, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0)

            val manager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = prefs.getLong(key, System.currentTimeMillis())

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY, pendingIntent)

            val editor = prefs.edit()
            editor.putBoolean("firstTime", true)
            editor.apply()
            //                }
            //        }
        }
    }

    companion object {
        val ARG_PLANET_NUMBER = "planet_number"
        fun newInstance(): PlanetFragment = PlanetFragment()
    }
}// Empty constructor required for fragment subclasses