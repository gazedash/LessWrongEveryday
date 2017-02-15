package com.shoegazerwithak.lesswrongeveryday.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.shoegazerwithak.lesswrongeveryday.ArticleViewActivity;
import com.shoegazerwithak.lesswrongeveryday.MainActivity;
import com.shoegazerwithak.lesswrongeveryday.R;
import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.utils.JsonCacheHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // show toast
        Log.d("shss", "dfafasdf" + System.currentTimeMillis());
        Toast.makeText(context, "Alarm running", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent(context, MainActivity.class);
        String nextTitle = JsonCacheHelper.getCachedJson(context, Constants.NEXT_ARTICLE_FILENAME, false);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Alarm Running")
                .setContentText("Read " + nextTitle + " next")
                .setSmallIcon(R.drawable.ic_done)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, noti);
    }
}