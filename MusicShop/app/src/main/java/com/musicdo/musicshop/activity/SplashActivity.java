package com.musicdo.musicshop.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/6/14.
 * 版 本 ：
 * 备 注 ：
 */

public class SplashActivity extends BaseActivity {
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MyApplication.getInstance().addActivity(this);
        context=this;
        // 判断是否是第一次开启应用
        boolean isFirstOpen = SpUtils.getBoolean(SplashActivity.this, AppConstants.FIRST_OPEN, "app");
        AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
        AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // 如果不是第一次启动app，则正常显示启动屏
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHomeActivity();
            }
        }, 2000);
//        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        //测试银联支付
//        getData();
    }

    private void getData() {
        OkHttpUtils.get("http://192.168.1.60:8083/api/UnionPay/GetTn?orderid=201709130321")
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {

                        ToastUtil.showShort(context,s.toString());
                    }



                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
