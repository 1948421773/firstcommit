package com.musicdo.musicshop.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/9/26.
 */

public class UpdateNickNameActivity extends BaseActivity implements View.OnClickListener,TextView.OnEditorActionListener{
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private LinearLayout ll_back;
    private TextView tv_title;
    private EditText et_username;
    private Button bt_username_clear,bt_regist_next;
    private Context context;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatenickname);
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
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_title.setText("昵称");
        ll_back.setOnClickListener(this);
        et_username=(EditText)findViewById(R.id.et_username);
        et_username.setOnEditorActionListener(this);
        bt_regist_next=(Button)findViewById(R.id.bt_regist_next);
        bt_regist_next.setOnClickListener(this);
        et_username.addTextChangedListener(new TextWatcher() {
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
                    bt_username_clear.setVisibility(View.VISIBLE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                }else{
                    bt_username_clear.setVisibility(View.GONE);
                    bt_regist_next.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        bt_username_clear=(Button)findViewById(R.id.bt_username_clear);
        bt_username_clear.setOnClickListener(this);


    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //以下方法防止两次发送请求
        System.out.println("执行onEditorAction");
        if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_UP:
                    //发送请求
                    String keyWord = et_username.getText().toString().trim();
                    if (null == keyWord)
                    System.out.println("输入"+keyWord);
                    return true;
                default:
                    return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.bt_username_clear:
                et_username.setText("");
                break;
            case R.id.bt_regist_next:
                String NickName="";
                loginData = SpUtils.getString(context, LoginKey, LoginFile);
                try {
                    AppConstants.USERNAME=new JSONObject(loginData).getString("Name");
                    AppConstants.USERID=new JSONObject(loginData).getInt("ID");
                    NickName=new JSONObject(loginData).getString("NickName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String nickname=SpUtils.filterCharToNormal(et_username.getText().toString().trim());
                if (NickName==null){
                    ToastUtil.showShort(context,"昵称不能为空");
                    return;
                }
                if(NickName.equals("")){
                    ToastUtil.showShort(context,"昵称不能为空");
                    return;
                }
                if(nickname.length()<4){
                    ToastUtil.showShort(context,"昵称长度不能少于4个");
                    return;
                }
                et_username.setText(nickname);
                if (nickname!=""){
                    et_username.setSelection(nickname.length());
                }else{
//                    et_username.setSelection(0);
                }
                if(nickname==""){
                    ToastUtil.showShort(context,"昵称不能为空");
                    return;
                }
                OkHttpUtils.post(AppConstants.UpdateUserNickName)
                        .tag(this)
                        .params("NickName",nickname)//
                        .params("UserID", String.valueOf(AppConstants.USERID))//
                        .execute(new StringCallback(){

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                String error=s;
                                String message="";
                                try {
                                    JSONObject json=new JSONObject(s);
                                    message=json.getString("Message");
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ToastUtil.showLong(context,message.toString());
                            }
                        });
                break;
        }
    }
}
