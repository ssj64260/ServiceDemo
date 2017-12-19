package com.android.servicedemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.servicedemo.R;
import com.android.servicedemo.app.BaseActivity;
import com.android.servicedemo.service.LocationService;
import com.android.servicedemo.utils.PreferencesUtils;

import static com.android.servicedemo.config.Constants.FILE_APP_SETTING;
import static com.android.servicedemo.config.Constants.KEY_IS_UPLOAD_LOCATION;

/**
 * 定位
 */

public class LocationActivity extends BaseActivity {

    private Switch swUpload;

    private boolean mIsUpload;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initData();
        initView();

    }

    private void initData() {
        mIsUpload = PreferencesUtils.getBoolean(FILE_APP_SETTING, KEY_IS_UPLOAD_LOCATION, false);
        mServiceIntent = new Intent(this, LocationService.class);
    }

    private void initView() {
        swUpload = (Switch) findViewById(R.id.sw_upload);
        swUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.setData(FILE_APP_SETTING, KEY_IS_UPLOAD_LOCATION, isChecked);
                if (isChecked) {
                    startService(mServiceIntent);
                } else {
                    stopService(mServiceIntent);
                }
            }
        });
        swUpload.setChecked(mIsUpload);
    }
}
