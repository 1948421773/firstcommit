package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;

/**
 * 我的积分
 * Created by Yuedu on 2018/1/23.
 */

public class PointsMallActivity extends BaseActivity implements View.OnClickListener {
    public TextView tv_title;
    private Context context;
    private LinearLayout ll_back;
    private RelativeLayout rl_recording,rl_about_points;
    private LinearLayout ll_exchange_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointsmall);
        context=this;
        MyApplication.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的积分");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        rl_recording= (RelativeLayout) findViewById(R.id.rl_recording);
        rl_recording.setOnClickListener(this);
        rl_about_points= (RelativeLayout) findViewById(R.id.rl_about_points);
        ll_exchange_point= (LinearLayout) findViewById(R.id.ll_exchange_point);
        ll_exchange_point.setOnClickListener(this);
        rl_about_points.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_recording:{
                Intent intent=new Intent(context,PointsRecordActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.rl_about_points:{
                Intent intent=new Intent(context,AboutPointActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.ll_exchange_point:{
                Intent intent=new Intent(context,ExchangePointActivity.class);
                startActivity(intent);
            }
                break;
        }
    }
}
