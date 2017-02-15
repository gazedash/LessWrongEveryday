package com.shoegazerwithak.lesswrongeveryday.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shoegazerwithak.lesswrongeveryday.MainActivity;
import com.shoegazerwithak.lesswrongeveryday.R;
import com.shoegazerwithak.lesswrongeveryday.receivers.AlarmReceiver;

import java.util.Calendar;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class PlanetFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    public PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static PlanetFragment newInstance() {
        return new PlanetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        MainActivity.Companion.setOnBackPressedListener(((MainActivity) activity), new MainActivity.BaseBackPressedListener(activity));
        int i = getArguments().getInt(ARG_PLANET_NUMBER);
        String title = getResources().getStringArray(R.array.drawer_items)[i];
//            int imageId = getResources().getIdentifier(title.toLowerCase(Locale.getDefault()),
//                    "drawable", getActivity().getPackageName());

        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        // Change
        getActivity().setTitle(title);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_planet);
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("tick", "1" + key);
        if (key.equals("pref_sync")) {
            Log.d("tick", "1" + String.valueOf(sharedPreferences.getBoolean(key, false)));
            Preference pref = findPreference(key);
            pref.setSummary(String.valueOf(sharedPreferences.getBoolean(key, false)));
        } else {
            Log.d("key", "1" + key);
            Log.d("key", "1" + sharedPreferences.getAll());
            //        if (sharedPreferences.getBoolean(key, false)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                if (!prefs.getBoolean("firstTime", false)) {
            Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(prefs.getLong(key, System.currentTimeMillis()));

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
//                }
//        }
        }
    }
}
