package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.wxapi.WXEntryActivity;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;


/**
 * qq和微信账号关联
 * 注意：qq绑定登录之后解绑再绑定会有问题，京东也是如此
 * Created by Yuedu on 2017/9/20.
 */

public class AccountAssociationActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout rl_wx_account,rl_qq_account;
    private ImageView iv_back;
    private Context context;
    private TextView tv_title,tv_qq_account_text_association,tv_wx_account_association;
    Tencent mTencent;
    String loginData="";
    String mAppid="1105987034";
    String type;
    String loginType;
    String bingDingType;
    BaseUiListener baseUiListener=new BaseUiListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountassociation);
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
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (tv_qq_account_text_association!=null&&AppConstants.USERID!=0&&tv_wx_account_association!=null){
                    BoolBinding(AppConstants.USERID);
                }
            }
        }, 1000);
        /*new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                ToastUtil.showLong(context,AppConstants.OPENID+"==="+AppConstants.AccountAssociationTYPE+"==="+AppConstants.USERID);
                if (AppConstants.wWEIXINTYPE.equals("WeiXinAccountAssociation")&&AppConstants.OPENID.length()!=0){
                    BindOrNotThirdParty(AppConstants.USERID, AppConstants.OPENID, AppConstants.AccountAssociationTYPE, "binding");
                }
            }
        }, 1000);*/
    }

    private void initView() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_qq_account_text_association=(TextView) findViewById(R.id.tv_qq_account_text_association);
        tv_wx_account_association=(TextView) findViewById(R.id.tv_wx_account_association);
        rl_wx_account=(RelativeLayout)findViewById(R.id.rl_wx_account);
        rl_qq_account=(RelativeLayout)findViewById(R.id.rl_qq_account);
        rl_qq_account.setOnClickListener(this);
        rl_wx_account.setOnClickListener(this);
        tv_title.setText("关联帐号");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back://
                finish();
                break;
            case R.id.rl_wx_account://微信授权登陆
                AppConstants.wWEIXINTYPE="WeiXinAccountAssociation";
                AppConstants.AccountAssociationTYPE="WeiXin";
                type = "WeiXin";
                if (tv_wx_account_association.getText().equals("已绑定")){
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
                    builder.setMessage("是否解除关联");
                    builder.setTitle("解除后将不能同步订单/物流信息");
                    builder.setNegativeButton("解除绑定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            BindOrNotThirdParty(AppConstants.USERID,null,AppConstants.AccountAssociationTYPE,"unbinding");
                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }else {
                    if (!AppConstants.OPENID.equals("")){
//                        ToastUtil.showLong(context,"直接授权");
                        BindOrNotThirdParty(AppConstants.USERID, AppConstants.OPENID, AppConstants.AccountAssociationTYPE, "binding");
                    }else{
//                        ToastUtil.showLong(context,"启动微信授权");
                        weChatlogin();
                    }
//                    ToastUtil.showLong(context,"点击微信授权");
                }
                break;
            case R.id.rl_qq_account://qq授权登陆
                AppConstants.wWEIXINTYPE="";
                AppConstants.AccountAssociationTYPE="QQ";
                type = "QQ";
                if (tv_qq_account_text_association.getText().equals("已绑定")){
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
                                builder.setMessage("是否解除关联");
                                builder.setTitle("解除后将不能同步订单/物流信息");
                    builder.setNegativeButton("解除绑定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            BindOrNotThirdParty(AppConstants.USERID,null,type,"unbinding");

                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();


                }else {
//                    ToastUtil.showShort(context,"开始绑定");
                    QQAccountAssociation();
//                    if (SpUtils.QQAccountAssociation(AccountAssociationActivity.this).equals("")){
//
//                    }
                }
                break;
        }
    }
    /**
     微信授权登录
     * */
    private void weChatlogin() {
        Log.i("space",">>>>>>>>>>wxLogin()");
        //初始化微信相关
        AppConstants.wx_api = WXAPIFactory.createWXAPI(AccountAssociationActivity.this, AppConstants.APP_ID, true);
        if(!AppConstants.wx_api.isWXAppInstalled())
        {
//                JsCallbackExecutor.onSocialWXLoginResult(CallbackCode.NO_INSTALL_WX, "weixin no install");
            Log.i("space","weixin no install");
//            ToastUtil.showLong(context,"weixin no install");
            return ;
        }
//        WXEntryActivity.mHaveCode = false;
        AppConstants.wx_api.registerApp(AppConstants.APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        AppConstants.wx_api.sendReq(req);
//        ToastUtil.showLong(context,"正在启动微信");
    }

    private void QQAccountAssociation() {
        mTencent = Tencent.createInstance(AppConstants.QQ_APP_ID, AccountAssociationActivity.this);
        if (!mTencent.isSessionValid()){
//            ToastUtil.showShort(context,"初始化QQ");
            mTencent.login(this, "all", baseUiListener);
        }else{

        }
    }

//    IUiListener loginListener = new BaseUiListener() {
//        @Override
//        protected void doComplete(JSONObject values) {
//            ToastUtil.showLong(AccountAssociationActivity.this, "执行IUiListener--1");
//
//        }
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Tencent.onActivityResultData(requestCode, resultCode, data, baseUiListener);
        Log.d("TAG", "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,baseUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
//            ToastUtil.showShort(context,"返回"+response.toString());
//            ToastUtil.showLong(AccountAssociationActivity.this,response.toString());
            if (null == response) {
                ToastUtil.showLong(AccountAssociationActivity.this,"登录失败");
                return;
            }
//            ToastUtil.showLong(AccountAssociationActivity.this, "登录成功");
//            startActivity(MainActivity.newIntent(mContext));//进入教师界面
            // 有奖分享处理
//            handlePrizeShare();
            String openid = null;
            try {
                JSONObject jsonResponse = (JSONObject) response;
                openid=(String) jsonResponse.get("openid");
//                ToastUtil.showLong(context,"openid="+openid);
                if ((int) jsonResponse.get("ret") == 0) {

                    openid = jsonResponse.getString("openid");
                    String accessToken = jsonResponse.getString("access_token");
                    String expires = jsonResponse.getString("expires_in");
                    mTencent.setOpenId(openid);
                    mTencent.setAccessToken(accessToken, expires);
                    /*userInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                    userInfo.getUserInfo(userInfoListener);*/
                }
                CheckBangDing(openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            doComplete((JSONObject)response);
        }
        protected void doComplete(JSONObject values) {

        }
        @Override
        public void onError(UiError e) {
//            ToastUtil.showLong(AccountAssociationActivity.this, "onError: " +e.errorDetail);
            ToastUtil.showShort(context,"登录错误");
        }

        @Override
        public void onCancel() {
//            ToastUtil.showLong(AccountAssociationActivity.this, "onCancel:");
            ToastUtil.showShort(context,"取消登录");
        }

    }
    private void CheckBangDing(final String openid) {
//        ToastUtil.showLong(context, "执行CheckBangDing"+openid);
        OkHttpUtils.post(AppConstants.CheckUserBingding)
                .tag(this)
                .params("openid", openid)
                .params("type", AppConstants.AccountAssociationTYPE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
//                        Log.i(TAG, "onBefore: 登录"+s+">>>>>>>"+response);
//                        logininfos=null;
//                        infos.clear();
                        JSONObject jsonObject;
                        String jsonData = null;
                        String Data = null;
                        boolean Flag = false;
                        int Code = 0;
                        Gson gson = new Gson();
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Message");
                            Code = jsonObject.getInt("Code");
                            Data = jsonObject.getString("Data");
                            JSONObject jsonUserName = new JSONObject(Data);
                            Flag = jsonObject.getBoolean("Flag");
//                            logininfos=gson.fromJson(Data,Logininfobean.class);
//                            ToastUtil.showLong(context, "检测返回" + s.toString());
                            /*if (Flag){//已经绑定 跳转登录绑定
                                Intent intent=new Intent(context,);
                                startActivity(intent);
//                                AppConstants.ISLOGIN=Flag;
//                                AppConstants.USERNAME=new JSONObject(Data).getString("Name");
//                                AppConstants.USERID=new JSONObject(Data).getInt("ID");
//                                SpUtils.putString(context,LoginKey,Data,LoginFile);
                            }else{//已经绑定 跳转注册绑定
                                Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
                                startActivity(intent);
                            }*/
//                            infos=gson.fromJson(Data,new TypeToken<ArrayList<Logininfobean>>(){}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(context, jsonData);//已经绑定，直接获取用户名登录成功
                        switch (Code) {
                            case 0:
                                    BindOrNotThirdParty(AppConstants.USERID, openid, AppConstants.AccountAssociationTYPE, "binding");
                                break;
                            case 1://解绑

                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        Log.i(TAG, "onBefore: 登录");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context, "请求超时" + e);
                    }
                });
    }

    private void BindOrNotThirdParty(int UserId, String openid, final String Type, String Action) {
//        ToastUtil.showLong(context, "BindOrNotThirdParty");
        String aaa=AppConstants.BindOrNotThirdParty+"?UserId="+UserId+"&openid="+openid+"&Type="+Type+"&Action="+Action;
        Log.i("提交绑定获取",aaa);
        OkHttpUtils.post(AppConstants.BindOrNotThirdParty)
                .tag(this)
                .params("UserId",UserId)
                .params("openid",openid)
                .params("Type",Type)
                .params("Action",Action)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
//                        Log.i(TAG, "onBefore: 登录"+s+">>>>>>>"+response);
//                        logininfos=null;
//                        infos.clear();
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Data=null;
                        boolean Flag=false;
                        int Code=0;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
//                            ToastUtil.showLong(context, "qq绑定返回"+s.toString());
                            jsonData = jsonObject.getString("Message");
                            Code=jsonObject.getInt("Code");
                            Flag=jsonObject.getBoolean("Flag");
//                            logininfos=gson.fromJson(Data,Logininfobean.class);

                            /*if (Flag){//已经绑定 跳转登录绑定
                                Intent intent=new Intent(context,);
                                Intent intent=new Intent(context,);
                                startActivity(intent);
//                                AppConstants.ISLOGIN=Flag;
//                                AppConstants.USERNAME=new JSONObject(Data).getString("Name");
//                                AppConstants.USERID=new JSONObject(Data).getInt("ID");
//                                SpUtils.putString(context,LoginKey,Data,LoginFile);
                            }else{//已经绑定 跳转注册绑定
                                Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
                                startActivity(intent);
                            }*/
//                            infos=gson.fromJson(Data,new TypeToken<ArrayList<Logininfobean>>(){}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ToastUtil.showShort(context,jsonData);//已经绑定，直接获取用户名登录成功
                        AppConstants.OPENID="";
                        switch(Code) {
                            case 0:
                                break;
                            case 1:
                                if (Type.equals("QQ")){
                                    tv_qq_account_text_association.setText("已绑定");
                                    tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
//                                    mTencent.logout(getApplicationContext());
//                                    finish();
                                    }else{
                                    tv_wx_account_association.setText("已绑定");
                                    tv_wx_account_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                }

                                break;
                            case 2:
                                if (Type.equals("QQ")){
                                    tv_qq_account_text_association.setText("未绑定");
                                    tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
//                                    finish();
                                }else{
                                    tv_wx_account_association.setText("未绑定");
                                    tv_wx_account_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
                                }

                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        Log.i(TAG, "onBefore: 登录");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"请求超时"+e);
                    }
                });
    }

    private void BoolBinding(int UserId) {
//        ToastUtil.showLong(context, "执行CheckBangDing");
        OkHttpUtils.post(AppConstants.BoolBinding)
                .tag(this)
                .params("UserId",UserId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
//                       ToastUtil.showShort(context,"查看绑定信息"+s);
//                        logininfos=null;
//                        infos.clear();
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Data=null;
                        boolean Flag=false;
                        int Code=0;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
//                            ToastUtil.showLong(context, "返回"+s.toString());
                            jsonData = jsonObject.getString("ReturnData");
                            JSONObject jsonReturnData= new JSONObject(jsonData);
                            Flag=jsonObject.getBoolean("Flag");
//                            logininfos=gson.fromJson(Data,Logininfobean.class);
                            if (jsonReturnData.getString("qqbinding").equals("")){
                                tv_qq_account_text_association.setText("未绑定");
                                tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
                            }else{
                                tv_qq_account_text_association.setText("已绑定");
                                tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
                            }
                            if (jsonReturnData.getString("weixinbinding").equals("")){
                                tv_wx_account_association.setText("未绑定");
                                tv_wx_account_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
                            }else{
                                tv_wx_account_association.setText("已绑定");
                                tv_wx_account_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
                            }
                            /*if (Flag){//已经绑定 跳转登录绑定
                                Intent intent=new Intent(context,);
                                startActivity(intent);
//                                A
ppConstants.ISLOGIN=Flag;
//                                AppConstants.USERNAME=new JSONObject(Data).getString("Name");
//                                AppConstants.USERID=new JSONObject(Data).getInt("ID");
//                                SpUtils.putString(context,LoginKey,Data,LoginFile);
                            }else{//已经绑定 跳转注册绑定
                                Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
                                startActivity(intent);
                            }*/
//                            infos=gson.fromJson(Data,new TypeToken<ArrayList<Logininfobean>>(){}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(context,jsonData);//已经绑定，直接获取用户名登录成功
                        /*switch(Code){
                            case 0:
                                tv_qq_account_text_association.setText("未绑定");
                                tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
                                break;
                            case 1:
                                tv_qq_account_text_association.setText("已绑定");
                                tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                break;
                            default:
                                break;
                        }*/
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        Log.i(TAG, "onBefore: 登录");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"请求超时"+e);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
