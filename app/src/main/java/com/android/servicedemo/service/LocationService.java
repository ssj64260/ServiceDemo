package com.android.servicedemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.servicedemo.R;
import com.android.servicedemo.ui.LocationActivity;
import com.android.servicedemo.utils.DateTimeUtils;
import com.android.servicedemo.utils.ThreadPoolUtils;

import java.util.concurrent.TimeUnit;

/**
 * 定时发送定位
 */

public class LocationService extends Service {

    private static final int REQUEST_ID_LOCATION = 2333;//上传定位通知栏请求ID
    private static final int PERIOD = 5000;
    private static final String START_TIME = "9:00";
    private static final String END_TIME = "18:00";

    private int mStartId;
    private int mTimes = 0;
    private Handler mHandler;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(getMainLooper());
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setNotification();

        ThreadPoolUtils.getInstache().scheduledRate(new Runnable() {
            @Override
            public void run() {
                final float startTime = getFloatByTime(START_TIME);
                final float endTime = getFloatByTime(END_TIME);
                final float currentTime = getFloatByTime(DateTimeUtils.getEnShortTime());

                if (currentTime >= startTime && currentTime <= endTime) {
                    mTimes++;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setNotification();
                        }
                    });
                }
            }
        }, 100, PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ThreadPoolUtils.getInstache().scheduledShutDown(0);
        mHandler.removeCallbacksAndMessages(null);
        stopSelf(mStartId);
        mNotificationManager.cancelAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStartId = startId;
        return super.onStartCommand(intent, flags, startId);
    }

    private void setNotification() {
        Intent intent = new Intent(this, LocationActivity.class);
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle("定位（已上传" + mTimes + "次）")
                .setTicker("定位")
                .setContentText("正在上传定位...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH);
        Notification notification = mBuilder.build();
        startForeground(REQUEST_ID_LOCATION, notification);
    }

    private float getFloatByTime(String timeText) {
        float time = 0;
        try {
            time = Float.parseFloat(timeText.replace(":", "."));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return time;
    }
}
