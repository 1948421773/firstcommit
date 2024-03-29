package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2018/7/27.
 */

public class UpdatePhoneActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private Context context;
    private final String TAG = "UpdatePhoneActivity";
    private TextView tv_regist_Code,tv_newphone_Code,et_phone;
    private Button bt_phone_clear,bt_newphone_clear, bt_code_clear,bt_newphonecode_clear, bt_regist_next;
    private Button bt_new_password_clear, bt_confirm_password_clear, bt_confirm_password_submit;
    private ImageView iv_back;
    private EditText  et_newphone,et_code,et_newphonecode;
    private EditText et_new_password, et_confirm_password;
    private LinearLayout ll_regist_getcode, ll_regist_setting_password;
    private CheckBox ck_remember;
    private String ReturnData = null;//注册token
    HashMap<String, String> map = new HashMap<String, String>();
    String findPwd = "register";//找回密码
    LoadingDialog dialog;
    CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tv_regist_Code.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            tv_regist_Code.setEnabled(true);
            tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_click));
            tv_regist_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
        }
    };

    CountDownTimer newtimer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tv_newphone_Code.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            tv_newphone_Code.setEnabled(true);
            tv_newphone_Code.setBackground(getResources().getDrawable(R.drawable.login_login_click));
            tv_newphone_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
        }
    };

    CountDownTimer newtimerError = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
//            tv_regist_Code.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            tv_newphone_Code.setEnabled(true);
            tv_newphone_Code.setBackground(getResources().getDrawable(R.drawable.login_login_click));
            tv_newphone_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
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
        setContentView(R.layout.activity_updataphone);
        MyApplication.getInstance().addActivity(this);
        context = this;
        initview();
    }


    private void initview() {
        dialog = new LoadingDialog(context, R.style.LoadingDialog);
        ll_regist_getcode = (LinearLayout) findViewById(R.id.ll_regist_getcode);
        ll_regist_setting_password = (LinearLayout) findViewById(R.id.ll_regist_setting_password);
        tv_regist_Code = (TextView) findViewById(R.id.tv_regist_Code);
        tv_regist_Code.setOnClickListener(this);
        tv_newphone_Code = (TextView) findViewById(R.id.tv_newphone_Code);
        tv_newphone_Code.setOnClickListener(this);
        bt_newphone_clear = (Button) findViewById(R.id.bt_newphone_clear);
        bt_newphone_clear.setOnClickListener(this);
        bt_phone_clear = (Button) findViewById(R.id.bt_phone_clear);
        bt_phone_clear.setOnClickListener(this);
        bt_new_password_clear = (Button) findViewById(R.id.bt_new_password_clear);
        bt_new_password_clear.setOnClickListener(this);
        bt_code_clear = (Button) findViewById(R.id.bt_code_clear);
        bt_code_clear.setOnClickListener(this);
        bt_newphonecode_clear = (Button) findViewById(R.id.bt_newphonecode_clear);
        bt_newphonecode_clear.setOnClickListener(this);
        bt_confirm_password_clear = (Button) findViewById(R.id.bt_confirm_password_clear);
        bt_confirm_password_clear.setOnClickListener(this);
        bt_regist_next = (Button) findViewById(R.id.bt_regist_next);
        bt_regist_next.setOnClickListener(this);
        bt_confirm_password_submit = (Button) findViewById(R.id.bt_confirm_password_submit);
        bt_confirm_password_submit.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        et_phone = (TextView) findViewById(R.id.et_phone);
        et_newphone = (EditText) findViewById(R.id.et_newphone);
        et_phone.setText(AppConstants.PHONE);
        et_newphone.setOnFocusChangeListener(this);
        et_newphone.addTextChangedListener(new TextWatcher() {
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
                    bt_newphone_clear.setVisibility(View.VISIBLE);
                    if (et_code.getText().toString().trim().length() > 0&&et_newphonecode.getText().toString().trim().length()> 0) {
                        bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                    }
                } else {
                    bt_newphone_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_new_password.setOnFocusChangeListener(this);
        et_new_password.addTextChangedListener(new TextWatcher() {
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
                    bt_new_password_clear.setVisibility(View.VISIBLE);
                    if (et_code.getText().toString().trim().length() > 0&&et_newphonecode.getText().toString().trim().length()> 0){
                        bt_confirm_password_submit.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                    }
                } else {
                    bt_new_password_clear.setVisibility(View.GONE);
                    bt_confirm_password_submit.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        et_code = (EditText) findViewById(R.id.et_code);
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
                    if (et_phone.getText().toString().trim().length() > 0&&et_newphonecode.getText().toString().trim().length()> 0){
                        bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                    }
                } else {
                    bt_code_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        et_newphonecode= (EditText) findViewById(R.id.et_newphonecode);
        et_newphonecode.setOnFocusChangeListener(this);
        et_newphonecode.addTextChangedListener(new TextWatcher() {
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
                    bt_newphonecode_clear.setVisibility(View.VISIBLE);
                    if (et_code.getText().toString().trim().length() > 0&&et_newphonecode.getText().toString().trim().length()> 0){
                        bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                    }
                } else {
                    bt_newphonecode_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        et_confirm_password.setOnFocusChangeListener(this);
        et_confirm_password.addTextChangedListener(new TextWatcher() {
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
                    bt_confirm_password_clear.setVisibility(View.VISIBLE);
                    if (et_new_password.getText().toString().trim().length() > 0) {
                        bt_confirm_password_submit.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                    }
                } else {
                    bt_confirm_password_clear.setVisibility(View.GONE);
                    bt_confirm_password_submit.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_regist_Code:
                if (et_phone.getText().toString().trim().length() != 0 && et_phone.getText().toString().trim().length() == 11) {
                    v.setEnabled(false);
                    tv_regist_Code.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                    timer.start();
                    getVerificationCode(et_phone.getText().toString().trim());
                } else {
                    ToastUtil.showShort(context, "手机号码不正确,请重新输入");
                }
                break;
            case R.id.tv_newphone_Code:
                if (et_newphone.getText().toString().trim().length() != 0 && et_newphone.getText().toString().trim().length() == 11) {
                    v.setEnabled(false);
                    tv_newphone_Code.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                    newtimer.start();
                    getnewVerificationCode(et_newphone.getText().toString().trim());
                } else {
                    ToastUtil.showShort(context, "手机号码不正确,请重新输入");
                }
                break;
            case R.id.bt_regist_next:
                if (et_code.getText().toString().trim().length()==0&&et_newphonecode.getText().toString().trim().length()==0&&et_newphonecode.getText().toString().trim().length()==0){
                    return;
                }
                if (et_code.getText().toString().trim().length() != 0&&et_newphonecode.getText().toString().trim().length() != 0) {
                    chckVerificationCode(et_phone.getText().toString().trim(), et_code.getText().toString().trim(),et_newphone.getText().toString().trim(),et_newphonecode.getText().toString().trim());
                } else {
                    ToastUtil.showShort(context, "请输入验证码");
                }
                break;
            case R.id.bt_confirm_password_submit:
                if (et_new_password.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(context, "请输密码不一致");
                    return;
                }
                if (et_confirm_password.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(context, "请输再次密码");
                    return;
                }
                if (et_new_password.getText().toString().trim().equals(et_confirm_password.getText().toString().trim())) {
                    FindPwd(map.get("phone"), et_new_password.getText().toString().trim(), map.get("token"));
                } else {
                    ToastUtil.showShort(context, "密码不一致");
                }
                break;
            case R.id.bt_phone_clear:
                et_phone.setText("");
                break;
            case R.id.bt_code_clear:
                et_code.setText("");
                break;
            case R.id.bt_newphone_clear:
                et_newphone.setText("");
                break;
            case R.id.bt_newphonecode_clear:
                et_newphonecode.setText("");
                break;
            case R.id.bt_new_password_clear:
                et_new_password.setText("");
                break;
            case R.id.bt_confirm_password_clear:
                et_confirm_password.setText("");
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getVerificationCode(String phoneNumber) {
        OkHttpUtils.get(AppConstants.GetVerificationCode)
                .tag(this)
                .params("phoneNumber", phoneNumber)
                .params("actionType", "register")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onBefore: 获取验证码" + s + ">>>>>>>" + response);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        String jsonData = null;
                        int Code = 0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code = jsonObject.getInt("Code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (Code) {
                            case 1:
                                ToastUtil.showShort(context, jsonData);
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
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context, "请求超时");
                    }
                });
    }
    /**
     * 获取验证码
     */
    private void getnewVerificationCode(String phoneNumber) {
        OkHttpUtils.get(AppConstants.GetVerificationCode)
                .tag(this)
                .params("phoneNumber", phoneNumber)
                .params("actionType", "register")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onBefore: 获取验证码" + s + ">>>>>>>" + response);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        String jsonData = null;
                        int Code = 0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code = jsonObject.getInt("Code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (Code) {
                            case 1:
                                ToastUtil.showShort(context, jsonData);
                                break;
                            default:
                                newtimer.cancel();
                                tv_newphone_Code.setEnabled(false);
                                tv_newphone_Code.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                                tv_newphone_Code.setText(getResources().getString(R.string.register_get_verificationCode_again));
                                ToastUtil.showShort(context, "验证码时间间隔小于60s,请稍后再试");
                                newtimerError.start();
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context, "请求超时");
                    }
                });
    }
    /**
     * 校验验证码
     */
    private void chckVerificationCode(final String phone, String Code,final String newphone, String newCode) {
        OkHttpUtils.post(AppConstants.UpdatePhone)
                .tag(this)
                .params("userid", AppConstants.USERID)
                .params("phoneNumberOld", phone)
                .params("codeOld", Code)
                .params("phoneNumberNew", newphone)
                .params("codeNew", newCode)
                .params("actionType", findPwd)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 手机号码修改成功" + s + ">>>>>>>" + response);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        String jsonData = null;
                        int Code = 0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code = jsonObject.getInt("Code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (Code) {
                            case 1:
                                ToastUtil.showShort(context, jsonData);
                                AppConstants.PHONE=newphone;
                                finish();
                                break;
                            case 0:
                                ToastUtil.showShort(context, jsonData);
                                break;
                            default:
                                ToastUtil.showShort(context, jsonData);
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context, "请求超时");
                    }
                });
    }

    /**
     * 校验验证码
     */
    private void FindPwd(String phone, String password, String token) {
        OkHttpUtils.post(AppConstants.FindPwd)
                .tag(this)
                .params("phoneNumber", phone)
                .params("pwd", password)
                .params("token", token)
                .params("type", "findPwd")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 注册成功" + s + ">>>>>>>" + response);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        String jsonData = null;
                        int Code = 0;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code = jsonObject.getInt("Code");
                            ReturnData = jsonObject.getString("ReturnData");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (Code) {
                            case 1:
                                ToastUtil.showShort(context, jsonData);
                                finish();
                                break;
                            default:
                                ReturnData = null;
                                map.clear();
                                ToastUtil.showShort(context, jsonData);
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context, "请求超时");
                    }
                });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_newphone:
                if (hasFocus) {
                    if (et_newphone.getText().length() != 0) {
                        bt_newphone_clear.setVisibility(View.VISIBLE);
                    }
                } else {
                    bt_newphone_clear.setVisibility(View.GONE);
                }
                break;

            case R.id.et_code:
                if (hasFocus) {
                    if (et_code.getText().length() != 0) {
                        bt_code_clear.setVisibility(View.VISIBLE);
                    }
                } else {
                    bt_code_clear.setVisibility(View.GONE);
                }
                break;
            case R.id.et_newphonecode:
                if (hasFocus) {
                    if (et_newphonecode.getText().length() != 0) {
                        bt_newphonecode_clear.setVisibility(View.VISIBLE);
                    }
                } else {
                    bt_newphonecode_clear.setVisibility(View.GONE);
                }
                break;
            case R.id.et_new_password:
                if (hasFocus) {
                    if (et_new_password.getText().length() != 0) {
                        bt_new_password_clear.setVisibility(View.VISIBLE);
                    }
                } else {
                    bt_new_password_clear.setVisibility(View.GONE);
                }
                break;
            case R.id.et_confirm_password:
                if (hasFocus) {
                    if (et_confirm_password.getText().length() != 0) {
                        bt_confirm_password_clear.setVisibility(View.VISIBLE);
                    }
                } else {
                    bt_confirm_password_clear.setVisibility(View.GONE);
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


}
