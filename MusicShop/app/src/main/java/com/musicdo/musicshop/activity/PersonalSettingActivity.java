package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.view.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**个人中心--设置
 * Created by Yuedu on 2017/9/18.
 */

public class PersonalSettingActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_rl_aboutus,tv_feedback,tv_clear_cace,tv_account_association,tv_message_reminder,tv_account_security,tv_logout,tv_clear_size_text;
    private ImageView iv_back;
    private Context context;
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private final String SearchHistoryKey="searchkey";
    private final String SearchHistoryFile="searchhistory";
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initview();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initview() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_rl_aboutus=(TextView)findViewById(R.id.tv_rl_aboutus);
        tv_rl_aboutus.setOnClickListener(this);
        tv_feedback=(TextView)findViewById(R.id.tv_feedback);
        tv_feedback.setOnClickListener(this);
        tv_clear_cace=(TextView)findViewById(R.id.tv_clear_cace);
        tv_clear_cace.setOnClickListener(this);
        tv_account_association=(TextView)findViewById(R.id.tv_account_association);
        tv_account_association.setOnClickListener(this);
        tv_message_reminder=(TextView)findViewById(R.id.tv_message_reminder);
        tv_message_reminder.setOnClickListener(this);
        tv_account_security=(TextView)findViewById(R.id.tv_account_security);
        tv_account_security.setOnClickListener(this);
        tv_logout=(TextView)findViewById(R.id.tv_logout);
        tv_clear_size_text=(TextView)findViewById(R.id.tv_clear_size_text);
        tv_logout.setOnClickListener(this);
        getCaceSize();
    }

    private void getCaceSize() {
        String caceSize="";
        try {
            caceSize=SpUtils.getTotalCacheSize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_clear_size_text.setText(caceSize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_rl_aboutus:{
                Intent intent=new Intent(context,PersonalAboutUsActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.tv_logout:{
                String loginData = SpUtils.getString(context, LoginKey, LoginFile);
                if (!loginData.equals("")){//获取本地信息
                    SpUtils.remove(context,LoginFile);//退出帐号清理登录时保存的信息和登录状态
                    AppConstants.ISLOGIN=false;
                    AppConstants.USERID=0;
                    AppConstants.PHONE="";
                    AppConstants.OPENID="";
                    SpUtils.remove(context,SearchHistoryFile);//退出帐号清理搜索记录
                }
                Intent intent=new Intent(context,MainActivity.class);
                intent.putExtra("ShoppingCartFragment", "");
                intent.putExtra("HomeFragment", "HomeFragment");
                intent.putExtra("specItemBeans", "");
                startActivity(intent);
            }
                break;
            case R.id.tv_feedback:{
                Intent intent=new Intent(context,FeedBackActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.tv_clear_cace:
                CustomDialog.Builder builder = new CustomDialog.Builder(context);
                builder.setMessage("提醒");
                builder.setTitle("确定清除缓存");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpUtils.clearAllCache(context);
                        getCaceSize();
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.tv_account_association: {
                Intent intent=new Intent(context,AccountAssociationActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.tv_message_reminder:
                finish();
                break;
            case R.id.tv_account_security:{
                Intent intent=new Intent(context,AccountSecurityActivity.class);
                startActivity(intent);
            }
                break;
        }

    }
}
