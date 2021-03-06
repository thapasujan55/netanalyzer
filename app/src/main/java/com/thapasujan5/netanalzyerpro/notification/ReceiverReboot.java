package com.thapasujan5.netanalzyerpro.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Weather.WeatherUpdater;

import java.util.Date;
import java.util.Timer;

/**
 * Created by Suzan on 11/7/2015.
 */


public class ReceiverReboot extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = null;
        if (intent == null) {
            serviceIntent = new Intent(context, Service.class);
            serviceIntent.putExtra("receiver", "reboot");
        } else {
            serviceIntent = intent;
        }
        if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_NOT_CONNECTED) {
            Log.i("RebootReceiver", "To Default notify");
            new NotificationISP(context);
        } else if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_WIFI || NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_MOBILE) {
            Log.i("RebootReceiver", "To Service");
            context.startService(serviceIntent);
            try {
                new Timer().scheduleAtFixedRate(new WeatherUpdater(context), new Date(), AppConstants.WEATHER_UPDATE_DURATION); //1Hour
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }
}
