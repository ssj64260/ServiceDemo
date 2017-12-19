package com.android.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.servicedemo.utils.DateTimeUtils;
import com.android.servicedemo.utils.ThreadPoolUtils;
import com.android.servicedemo.utils.ToastMaster;

import java.util.concurrent.TimeUnit;

/**
 * 定时发送定位
 */

public class LocationService extends Service {

    private static final int PERIOD = 3000;
    private static final String START_TIME = "9:00";
    private static final String END_TIME = "18:00";

    private int mStartId;
    private int mTimes = 0;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(getMainLooper());

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
                            ToastMaster.toast(String.valueOf(mTimes));
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
