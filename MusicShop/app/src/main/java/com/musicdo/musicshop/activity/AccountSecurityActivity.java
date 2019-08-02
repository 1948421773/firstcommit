package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yuedu on 2017/9/26.
 */

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_title,tv_security_phone,tv_clear_cace,tv_account_association,tv_message_reminder,tv_account_security,tv_logout;
    private LinearLayout ll_back;
    private RelativeLayout rl_accountsecurity_verification,rl_accountsecurity_password;
    private Context context;
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    String loginData="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsecurity);
        MyApplication.getInstance().addActivity(this);
        context=this;
        intiView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
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
                    if(tv_security_phone!=null){
                        tv_security_phone.setText(AppConstants.PHONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void intiView() {
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_security_phone=(TextView) findViewById(R.id.tv_security_phone);
        tv_security_phone.setText(AppConstants.PHONE);
        tv_title.setText("账户安全");
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        rl_accountsecurity_password=(RelativeLayout) findViewById(R.id.rl_accountsecurity_password);
        rl_accountsecurity_verification=(RelativeLayout) findViewById(R.id.rl_accountsecurity_verification);
        ll_back.setOnClickListener(this);
        rl_accountsecurity_password.setOnClickListener(this);
        rl_accountsecurity_verification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_accountsecurity_password:{
                Intent intent=new Intent(context,ModifyPasswordActivity.class);
             startActivity(intent);
            }
                break;
            case R.id.rl_accountsecurity_verification://修改手机号码
                Intent intent=new Intent(context,UpdatePhoneActivity.class);
                startActivity(intent);
                break;
        }
    }
}
