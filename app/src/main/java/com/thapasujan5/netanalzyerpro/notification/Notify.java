package com.thapasujan5.netanalzyerpro.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.MainActivity;

/**
 * Created by Suzan on 11/7/2015.
 */
public class Notify {
    SharedPreferences sp;

    public Notify(Context context, String extIPAdd, String org, String city, String country) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);


        //Older Devices
        NotificationCompat.Builder n = new NotificationCompat.Builder(context);
        //For api>15
        Notification noti = null;
        if (extIPAdd != null && org != null && city != null && country != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setContentTitle("External IP: " + extIPAdd)
                        .setContentText(org + " "
                                + city + ", " + country).build();
            } else {
                n.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent)
                        .setContentTitle("External IP: " + extIPAdd)
                        .setContentText(org + " "
                                + city + ", " + country);
            }


        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setContentTitle("Check Network Access !")
                        .setContentText("Touch to reload.").build();
            } else {
                n.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent).setContentTitle("Check Network Access !")
                        .setContentText("Touch to reload.");
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Adjust OnGoing based on pref
        if (sp.getBoolean(context.getString(R.string.key_notification_ongoing), true) == true) {
            if (noti != null) {
                noti.flags |= Notification.FLAG_ONGOING_EVENT;
            } else {
                n.setAutoCancel(false);
                n.setOngoing(true);
            }
            Log.i("Notify", "OnGoing On");
        }
        if (noti != null) {
            notificationManager.notify(0, noti);
        } else {
            notificationManager.notify(0, n.build());
        }
    }
}