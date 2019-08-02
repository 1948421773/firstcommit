package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;

/**
 * 关于积分
 * Created by Yuedu on 2018/1/24.
 */

public class AboutPointActivity extends BaseActivity implements View.OnClickListener {
    public TextView tv_title;
    private Context context;
    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutpoint);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("关于积分");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
