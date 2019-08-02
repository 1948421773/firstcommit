package com.musicdo.musicshop.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;

/**
 * 描述:找人代付
 * 作者：haiming on 2017/8/29 13:51
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AllOrderPayForActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allorder_payfor);
        MyApplication.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
