package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.Logininfobean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/9/21.
 */

public class LoginAccountAssociationActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener,CompoundButton.OnCheckedChangeListener{
    private String TAG="LoginAccountAssociationActivity";
    private Context context;
    private LinearLayout ll_back;
    private TextView tv_title,tv_regist_Code,tv_login_next;
    private EditText et_phone,et_code,et_password;
    private Button bt_phone_clear,bt_code_clear,bt_regist_next,bt_password_clear;
    CheckBox ck_pwd_eye;
    private String ReturnData=null;//注册token
    HashMap<String , String> map = new HashMap<String , String>();
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    LoadingDialog dialog;
    String loginData="";

    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_regist_Code.setText(millisUntilFinished/1000 + "秒");
        }

        @Override
        public void onFinish() {
            tv_regist_Code.setEnabled(true);
            tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_click));
            tv_regist_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
        }
    };
    CountDownTimer timerError = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
//            tv_regist_Code.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            tv_regist_Code.setEnabled(true);
            tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_click));
            tv_regist_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_accountassociation);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
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

    private void initView() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("注册绑定");

        et_phone=(EditText)findViewById(R.id.et_phone);
        et_phone.setOnFocusChangeListener(this);
        et_phone.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 0) {
                    bt_phone_clear.setVisibility(View.VISIBLE);
                    if (et_code.getText().toString().trim().length()>0){
                        if (et_password.getText().toString().trim().length()>0){
                            bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                        }
                    }

                }else{
                    bt_phone_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });

        bt_phone_clear=(Button)findViewById(R.id.bt_phone_clear);
        bt_phone_clear.setOnClickListener(this);

        et_code=(EditText)findViewById(R.id.et_code);
        et_code.setOnFocusChangeListener(this);
        et_code.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 0) {
                    bt_code_clear.setVisibility(View.VISIBLE);
                    if (et_phone.getText().toString().trim().length()>0){
                        if (et_password.getText().toString().trim().length()>0){
                            bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                        }
                    }
                }else{
                    bt_code_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });

        tv_login_next=(TextView)findViewById(R.id.tv_login_next);
        tv_login_next.setOnClickListener(this);

        tv_regist_Code=(TextView)findViewById(R.id.tv_regist_Code);
        tv_regist_Code.setOnClickListener(this);

        bt_code_clear=(Button)findViewById(R.id.bt_code_clear);
        bt_code_clear.setOnClickListener(this);

        bt_regist_next=(Button)findViewById(R.id.bt_regist_next);
        bt_regist_next.setOnClickListener(this);

        ck_pwd_eye=(CheckBox)findViewById(R.id.ck_pwd_eye);
        ck_pwd_eye.setOnCheckedChangeListener(this);

        et_password=(EditText)findViewById(R.id.et_password);
        et_password.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (temp.length() > 0) {
                    bt_password_clear.setVisibility(View.VISIBLE);
                    if (et_phone.getText().toString().trim().length()>0){
                        if (et_code.getText().toString().trim().length()>0){
                            bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                        }
                    }
                }else{
                    bt_password_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        et_password.setOnFocusChangeListener(this);
        bt_password_clear=(Button)findViewById(R.id.bt_password_clear);
        bt_password_clear.setOnClickListener(this);
    }

    /**
     * 获取验证码*/
    private void getVerificationCode(String phoneNumber) {
        OkHttpUtils.get(AppConstants.GetVerificationCode)
                .tag(this)
                .params("phoneNumber",phoneNumber)
                .params("actionType","register")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
//                        Log.i(TAG, "onBefore: 获取验证码"+s+">>>>>>>"+response);
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        String jsonData=null;
                        int Code=0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code=jsonObject.getInt("Code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch(Code){
                            case 1:
                                ToastUtil.showShort(context,jsonData);
                                break;
                            default:
                                timer.cancel();
                                tv_regist_Code.setEnabled(false);
                                tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                                tv_regist_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
                                ToastUtil.showShort(context, "验证码时间间隔小于60s,请稍后再试");
                                timerError.start();
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context,"请求超时");
                    }
                });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.et_phone:
                if (hasFocus){
                    if (et_phone.getText().length()!=0){
                        bt_phone_clear.setVisibility(View.VISIBLE);
                    }
                }else{
                    bt_phone_clear.setVisibility(View.GONE);
                }
                break;
            case R.id.et_code:
                if (hasFocus){
                    if (et_code.getText().length()!=0){
                        bt_code_clear.setVisibility(View.VISIBLE);
                    }
                }else{
                    bt_code_clear.setVisibility(View.GONE);
                }
                break;
            case R.id.et_password:
                if (hasFocus){
                    if (et_password.getText().length()!=0){
                        bt_password_clear.setVisibility(View.VISIBLE);
                    }
                }else{
                    bt_password_clear.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.bt_regist_next:
                if (et_code.getText().toString().trim().length()!=0){
                    if (et_password.getText().toString().trim().length()!=0){
                        chckVerificationCode(et_phone.getText().toString().trim(),et_code.getText().toString().trim());
                    }else{
                        ToastUtil.showShort(context,"请设置密码");
                    }
                }else{
                    ToastUtil.showShort(context,"请输入验证码");
                }
                break;
            case R.id.bt_password_clear:
                et_password.setText("");
                break;
            case R.id.bt_phone_clear:
                et_phone.setText("");
                break;
            case R.id.tv_login_next: {
                    Intent intent = new Intent(context, LoginHasAccountAssociationActivity.class);
                    startActivity(intent);
                finish();
            }
                break;
            case R.id.tv_regist_Code:
                if (et_phone.getText().toString().trim().length()!=0&&et_phone.getText().toString().trim().length()==11){
                    v.setEnabled(false);
                    tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                    timer.start();
                    getVerificationCode(et_phone.getText().toString().trim());
                }else{
                    ToastUtil.showShort(context,"手机号码不正确,请重新输入");
                }
                break;
            case R.id.bt_code_clear:
                et_code.setText("");
                break;
        }
    }


    /**
     * 校验验证码*/
    private void chckVerificationCode(final String phone, String Code) {
        OkHttpUtils.post(AppConstants.CheckCodeAndPhoneNumber)
                .tag(this)
                .params("phoneNumber",phone)
                .params("code",Code)
                .params("actionType","register")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        String jsonData=null;
                        int Code=0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code=jsonObject.getInt("Code");
                            ReturnData=jsonObject.getString("ReturnData");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch(Code){
                            case 1:
//                                ToastUtil.showShort(context,jsonData);
                                map.clear();
                                map.put("phone",phone);
                                map.put("token",ReturnData);
                                Register(map.get("phone"),et_password.getText().toString().trim(),map.get("token"));
                                break;
                            default:
                                ReturnData=null;
                                map.clear();
                                ToastUtil.showShort(context,jsonData);
                                CustomDialog.Builder builder = new CustomDialog.Builder(context);
                                builder.setMessage(jsonData);
                                builder.setTitle("");
                                builder.setNegativeButton("重新注册", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        et_phone.setText("");
                                        et_code.setText("");
                                        et_password.setText("");
                                        timer.cancel();
                                        tv_regist_Code.setEnabled(true);
                                        tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                                        tv_regist_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
                                    }
                                });
                                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        ll_regist_getcode.setVisibility(View.VISIBLE);
//                                        ll_regist_setting_password.setVisibility(View.GONE);
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context,"请求超时");
                    }
                });
    }



    /**
     * 验证码校验成功后后注册*/
    private void Register(String phone,String password,String token) {
        OkHttpUtils.post(AppConstants.Register)
                .tag(this)
                .params("phoneNumber",phone)
                .params("pwd",password)
                .params("token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        ToastUtil.showShort(context,"注册成功");
                        AppConstants.OPENID="";
                        JSONObject jsonObject;
                        String jsonData="";
                        String Data="";
                        String name="";
                        int id=0;
                        String PHONE="";
                        boolean flag=false;
                        int Code=0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Data = jsonObject.getString("Data");
                            flag = jsonObject.getBoolean("Flag");
                            name=new JSONObject(Data).getString("Name");
                            id=new JSONObject(Data).getInt("ID");
                            Code=jsonObject.getInt("Code");
                            PHONE=new JSONObject(Data).getString("Phone");
                            ReturnData=jsonObject.getString("ReturnData");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch(Code){
                            case 1:
                                ToastUtil.showShort(context,jsonData);
                                /*ReturnData=null;
                                map.clear();
                                AppConstants.ISLOGIN=flag;
                                AppConstants.USERNAME=name;
                                AppConstants.USERID=id;
                                AppConstants.PHONE=PHONE;
                                SpUtils.putString(context,LoginKey,Data,LoginFile);

                                finish();
                                Intent intent=new Intent(context,MainActivity.class);
                                intent.putExtra("ShoppingCartFragment", "");
                                intent.putExtra("HomeFragment", "HomeFragment");
                                intent.putExtra("specItemBeans", "");
                                startActivity(intent);*/
                                /*Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);*/
                                finish();
                                break;
                            default:
                                ReturnData=null;
                                map.clear();
                                ToastUtil.showShort(context,jsonData);
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);

//                        Log.i(TAG, "onBefore: 校验验证码");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"请求超时");
                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
//选择状态 显示明文--设置为可见的密码
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            et_password.setSelection(et_password.getText().length());
        }else {
//默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_password.setSelection(et_password.getText().length());
        }
    }
}
