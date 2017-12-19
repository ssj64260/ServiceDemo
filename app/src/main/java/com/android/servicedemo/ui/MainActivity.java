package com.android.servicedemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.servicedemo.R;
import com.android.servicedemo.app.BaseActivity;

public class MainActivity extends BaseActivity {

    private TextView tvLocation;

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_location:
                    startActivity(new Intent(MainActivity.this, LocationActivity.class));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();

    }

    private void initData() {

    }

    private void initView() {
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvLocation.setOnClickListener(mClick);
    }

}
