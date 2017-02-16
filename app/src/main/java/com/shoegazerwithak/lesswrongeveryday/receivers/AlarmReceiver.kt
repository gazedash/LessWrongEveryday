package com.shoegazerwithak.lesswrongeveryday.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.shoegazerwithak.lesswrongeveryday.MainActivity
import com.shoegazerwithak.lesswrongeveryday.R
import com.shoegazerwithak.lesswrongeveryday.constants.Constants
import com.shoegazerwithak.lesswrongeveryday.utils.JsonCacheHelper

import android.content.Context.NOTIFICATION_SERVICE

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // show toast
        Log.d("shss", "dfafasdf" + System.currentTimeMillis())
        Toast.makeText(context, "Alarm running", Toast.LENGTH_SHORT).show()
        val resultIntent = Intent(context, MainActivity::class.java)
        val nextTitle = JsonCacheHelper.getCachedJson(context, Constants.NEXT_ARTICLE_FILENAME, false)
        val resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val noti = Notification.Builder(context)
                .setContentTitle("Alarm Running")
                .setContentText("Read $nextTitle next")
                .setSmallIcon(R.drawable.ic_done)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build()

        val mNotificationId = 1
        // Gets an instance of the NotificationManager service
        val mNotifyMgr = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, noti)
    }
}