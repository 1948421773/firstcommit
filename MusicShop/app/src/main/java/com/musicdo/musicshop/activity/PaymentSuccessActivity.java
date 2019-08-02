package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;

/**
 * 描述:
 * 作者：haiming on 2017/8/23 17:52
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class PaymentSuccessActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_zfb,tv_score,tv_payment_price;
    private Context context;
    String price;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentsuccess);
        MyApplication.getInstance().addActivity(this);
        price=getIntent().getStringExtra("price");
        context=this;
        tv_payment_price=(TextView)findViewById(R.id.tv_payment_price);
        if (price!=null){
            tv_payment_price.setText(getResources().getString(R.string.pricesymbol)+price);
        }
        tv_zfb=(TextView)findViewById(R.id.tv_zfb);
        tv_zfb.setOnClickListener(this);
        tv_score=(TextView)findViewById(R.id.tv_score);
        tv_score.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_zfb: {
                Intent intent = new Intent(context, MyOrderListActivity.class);
                intent.putExtra("TabIndex", 1);
                startActivity(intent);
                finish();
            }
                break;
            case R.id.tv_score: {
                finish();
            }
                break;
        }
    }
}
