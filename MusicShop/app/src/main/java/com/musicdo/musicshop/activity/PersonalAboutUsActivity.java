package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yuedu on 2017/9/18.
 */

public class PersonalAboutUsActivity extends BaseActivity implements View.OnClickListener{
    private Context context;
    private LinearLayout ll_back;
    private TextView tv_service_agreement,tv_copyright_notice,tv_title,tv_version_code,tv_update;
    String versionName;
    private String docPath = "/mnt/sdcard/documents/";
    private String docName = "test.doc";
    private String savePath = "/mnt/sdcard/documents/";
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalaboutus);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
//        setDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0|| AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
        if (AppConstants.USERID==0){//当内存不足，activity被销毁了，信息没有保存恢复不了，只能重新回到首页
            loginData= SpUtils.getString(context, "LoginKey", "LoginFile");
            if (!loginData.equals("")){//获取本地信息
                AppConstants.ISLOGIN=true;
                try {
                    AppConstants.USERNAME=new JSONObject(loginData).getString("Name");
                    AppConstants.USERID=new JSONObject(loginData).getInt("ID");
                    AppConstants.PHONE=new JSONObject(loginData).getString("Phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getDate() {
        try {
            versionName= "";
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
//                return "";
            }

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        tv_version_code.setText(versionName);
    }

    private void initView() {
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("关于我们");
        tv_service_agreement=(TextView)findViewById(R.id.tv_service_agreement);
        tv_copyright_notice=(TextView)findViewById(R.id.tv_copyright_notice);
//        tv_rl_aboutus=(TextView)findViewById(R.id.tv_rl_aboutus);
        tv_version_code=(TextView)findViewById(R.id.tv_version_code);
        tv_update=(TextView)findViewById(R.id.tv_update);
        ll_back.setOnClickListener(this);
        tv_update.setOnClickListener(this);
        tv_service_agreement.setOnClickListener(this);
        tv_copyright_notice.setOnClickListener(this);
//        tv_rl_aboutus.setOnClickListener(this);
        getDate();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_service_agreement:{
                Intent intent=new Intent(context,ServiceAgreementActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.tv_copyright_notice:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("版权声明");
                builder.setMessage("乐都城是专业乐器电商。专注乐器、乐器配件、音乐礼品、图书音像及乐器跨境电商的移动互联网电商平台，乐都城就是乐器行业里的天猫。乐都城属于乐都城国际音乐谷O2O六大平台之一，乐都城国际音乐谷是在党的大力发展与扶持文化产业的国家政策的号召下，由乐器与音乐行业互联网新锐媒体-Musicdu乐度音乐联合投资10亿人民币共同打造，总建筑面积12万平方米，将打造成世界无双的O2O全球乐器体验平台。\n");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.show();
                break;
            case R.id.tv_rl_aboutus:
                finish();
                break;
            case R.id.tv_update:
                new Handler().postDelayed(new Runnable(){//更新版本
                    @Override
                    public void run() {
                        SpUtils.UpdateVersionCode(PersonalAboutUsActivity.this);
                    }
                }, 500);
                break;
        }
    }
}
