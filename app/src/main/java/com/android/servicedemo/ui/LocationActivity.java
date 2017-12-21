package com.android.servicedemo.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.servicedemo.R;
import com.android.servicedemo.app.BaseActivity;
import com.android.servicedemo.service.LocationService;
import com.android.servicedemo.ui.dialog.DateTimePickerDialog;
import com.android.servicedemo.ui.dialog.DialogListener;
import com.android.servicedemo.utils.DateTimeUtils;
import com.android.servicedemo.utils.PreferencesUtils;
import com.android.servicedemo.utils.ToastMaster;

import java.util.Calendar;

import static com.android.servicedemo.config.Constants.FILE_APP_SETTING;
import static com.android.servicedemo.config.Constants.KEY_IS_UPLOAD_LOCATION;

/**
 * 定位
 */

public class LocationActivity extends BaseActivity {

    private Switch swUpload;
    private TextView tvTime;
    private TextView tvStartTime;
    private TextView tvEndTime;

    private DateTimePickerDialog mTimePickerDialog;

    private boolean mIsUpload;
    private Intent mServiceIntent;
    private LocationService.LocationBinder mBinder;
    private boolean mBound = false;

    private String mStartTime = "9:00";
    private String mEndTime = "18:00";

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (LocationService.LocationBinder) service;
            mBound = true;
            ToastMaster.toast("绑定服务成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            ToastMaster.toast("与服务断开连接");
        }
    };

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar time = Calendar.getInstance();
            switch (v.getId()) {
                case R.id.tv_start_time:
                    time.setTime(DateTimeUtils.StringToShortTimeIgnoreDate(mStartTime));
                    showPickerDialog(v.getId(), time, true);
                    break;
                case R.id.tv_end_time:
                    time.setTime(DateTimeUtils.StringToShortTimeIgnoreDate(mEndTime));
                    showPickerDialog(v.getId(), time, true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initData();
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindLoactionService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindLoactionService();
    }

    @Override
    protected void onDestroy() {
        if (mTimePickerDialog != null) {
            mTimePickerDialog.dismiss();
        }
        super.onDestroy();
    }

    private void initData() {
        mIsUpload = PreferencesUtils.getBoolean(FILE_APP_SETTING, KEY_IS_UPLOAD_LOCATION, false);
        mServiceIntent = new Intent(this, LocationService.class);
    }

    private void initView() {
        swUpload = (Switch) findViewById(R.id.sw_upload);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);

        swUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsUpload = isChecked;
                PreferencesUtils.setData(FILE_APP_SETTING, KEY_IS_UPLOAD_LOCATION, mIsUpload);
                if (mIsUpload) {
                    startService(mServiceIntent);
                    bindLoactionService();
                } else {
                    unBindLoactionService();
                    stopService(mServiceIntent);
                }
            }
        });
        swUpload.setChecked(mIsUpload);

        tvStartTime.setOnClickListener(mClick);
        tvEndTime.setOnClickListener(mClick);

        final String time = "(" + mStartTime + "-" + mEndTime + ")";
        tvTime.setText(time);
    }

    private void bindLoactionService() {
        if (mIsUpload && !mBound && mBinder == null) {
            bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unBindLoactionService() {
        if (mBound) {
            unbindService(mConnection);
            mBinder = null;
            mBound = false;
        }
    }

    private void showPickerDialog(int tag, Calendar calendar, boolean isTimePicker) {
        if (mTimePickerDialog == null) {
            mTimePickerDialog = new DateTimePickerDialog(this);
            mTimePickerDialog.setDialogClickListener(new DialogListener() {
                @Override
                public void onConfirmListener(int tag, String content) {
                    switch (tag) {
                        case R.id.tv_start_time:
                            mStartTime = content;
                            if (mBinder != null) {
                                mBinder.setStartTime(mStartTime);
                            }
                            break;
                        case R.id.tv_end_time:
                            mEndTime = content;
                            if (mBinder != null) {
                                mBinder.setEndTime(mEndTime);
                            }
                            break;
                    }
                    final String time = "(" + mStartTime + "-" + mEndTime + ")";
                    tvTime.setText(time);
                }
            });
        }
        mTimePickerDialog.setDate(tag, calendar, isTimePicker);
        mTimePickerDialog.show();
    }
}
