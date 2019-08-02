package com.musicdo.musicshop.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.libzxing.encoding.EncodingUtils;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AccountAssociationActivity;
import com.musicdo.musicshop.activity.LoginAccountAssociationActivity;
import com.musicdo.musicshop.activity.MainActivity;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.MyHttpClient;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;


public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private boolean isLoading=false;
    final byte[][] buf = new byte[1][1];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册API

        AppConstants.wx_api = WXAPIFactory.createWXAPI(this, AppConstants.APP_ID);
        AppConstants.wx_api.handleIntent(getIntent(), this);
        Log.i("savedInstanceState"," sacvsa"+AppConstants.wx_api.handleIntent(getIntent(), this));
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }
    //  发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            String code="";
            if(resp instanceof SendAuth.Resp){
                SendAuth.Resp newResp = (SendAuth.Resp) resp;
                //获取微信传回的code
                code= newResp.code;
                Log.i("newRespnewResp","   code    :"+code);
                if (newResp.openId!=null){
                    if (!newResp.openId.equals("")){
                        AppConstants.OPENID=newResp.openId;
//                    ToastUtil.showLong(getApplicationContext(),"OPENID不为空"+AppConstants.OPENID);
                    }
                }

            }

            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    //发送成功
                    if (!isLoading){
                        getOpenID(code);
//                    ToastUtil.showShort(getApplicationContext(),"检测");
                        isLoading=true;
                    }else{
//                    ToastUtil.showShort(getApplicationContext(),"不检测");
                    }

                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Log.i("savedInstanceState","发送取消ERR_USER_CANCEL");
                    //发送取消
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Log.i("savedInstanceState","发送取消ERR_AUTH_DENIEDERR_AUTH_DENIEDERR_AUTH_DENIED");
                    //发送被拒绝
                    break;
                default:
                    Log.i("savedInstanceState","发送返回breakbreakbreak");
                    //发送返回
                    break;
            }
//        ToastUtil.showLong(getApplicationContext(),"WXEntryActivity resp.errCode="+resp.errCode+"-----+code="+code);
            finish();
        }else{
//这里的意思就是如果不是微信支付， 就继续走原来的逻辑，那就是给super处理
            super.onResp(resp);//一定要加super，实现我们的方法，否则不能回调
        }

    }

    private void getOpenID(final String code) {
        // APP_ID和APP_Secret在微信开发平台添加应用的时候会生成，grant_type 用默认的"authorization_code"即可

        /*new Thread(new Runnable(){
            @Override
            public void run() {
                String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                        + AppConstants.APP_ID
                        + "&secret="
                        + AppConstants.mch_id
                        + "&code="
                        + code
                        + "&grant_type=authorization_code";

                HttpClient httpclient = MyHttpClient.getNewHttpClient(WXEntryActivity.this);

                HttpPost httpPost = new HttpPost(path);
                try {
                    HttpResponse resp = httpclient.execute(httpPost);
                    if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    }
                    buf[0]= EntityUtils.toByteArray(resp.getEntity());
//                    JSONObject jsonObject=new JSONObject(String.valueOf(buf[0]));
//                    String openid = jsonObject.getString("openid").toString().trim();
//                    String access_token = jsonObject.getString("access_token").toString().trim();
                    ToastUtil.showShort(WXEntryActivity.this,String.valueOf(buf[0]));//已经绑定，直接获取用户名登录成功
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + AppConstants.APP_ID
                + "&secret="
                + AppConstants.AppSecret
                + "&code="
                + code
                + "&grant_type=authorization_code";
//        ToastUtil.showShort(WXEntryActivity.this,path);
        OkHttpUtils.post(path)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Data=null;
                        boolean Flag=false;
                        int Code=0;
                        Gson gson=new Gson();
                        try {
//                            ToastUtil.showLong(WXEntryActivity.this, "----------"+s.toString());
                            jsonObject = new JSONObject(s);
                            String unionid = jsonObject.getString("unionid").toString().trim();
                            String access_token = jsonObject.getString("access_token").toString().trim();
                            AppConstants.OPENID=jsonObject.getString("unionid").toString().trim();
//                            ToastUtil.showShort(WXEntryActivity.this,"openid"+AppConstants.OPENID);
                            if (!unionid.equals("")) {
                                if (!AppConstants.wWEIXINTYPE.equals("WeiXinAccountAssociation")){
                                    CheckBangDing(unionid);
                                }else{
                                    BindOrNotThirdParty(AppConstants.USERID, AppConstants.OPENID, "WeiXin", "binding");
                                }

                            }else{
                                ToastUtil.showShort(WXEntryActivity.this,"请求超时");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        ToastUtil.showShort(context,jsonData);//已经绑定，直接获取用户名登录成功
//                        switch(Code){
//                            case 0:
//                                Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
//                                startActivity(intent);
//                                break;
//                            case 1:
//                                ToastUtil.showLong(context, "返回已绑定数据"+s.toString());
//
//                                finish();
//                            default:
//                                break;
//                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        Log.i(TAG, "onBefore: 登录");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
//                        ToastUtil.showShort(context,"请求超时"+e);
                    }
                });
    }

    private void BindOrNotThirdParty(int UserId, String openid, final String Type, String Action) {
//        ToastUtil.showLong(context, "BindOrNotThirdParty");
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
//                        ToastUtil.showShort(context,jsonData);//已经绑定，直接获取用户名登录成功
//                        AppConstants.OPENID="";
                        switch(Code) {
                            case 0:
                                break;
                            case 1:
                                if (Type.equals("QQ")){
//                                    tv_qq_account_text_association.setText("已绑定");
//                                    tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                }else{
//                                    ToastUtil.showLong(getApplicationContext(), "微信已绑定");
//                                    tv_wx_account_association.setText("已绑定");
//                                    tv_wx_account_association.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                }
                                break;
                            case 2:
                                if (Type.equals("QQ")){
//                                    tv_qq_account_text_association.setText("未绑定");
//                                    tv_qq_account_text_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
                                }else{
//                                    ToastUtil.showLong(getApplicationContext(), "微信未绑定");
//                                    tv_wx_account_association.setText("未绑定");
//                                    tv_wx_account_association.setTextColor(context.getResources().getColor(R.color.prodetail_freight));
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
                        ToastUtil.showShort(getApplicationContext(),"请求超时"+e);
                    }
                });
    }

    private void CheckBangDing(final String openid) {
//        ToastUtil.showLong(getApplicationContext(), "执行CheckBangDing");
        OkHttpUtils.post(AppConstants.CheckUserBingding)
                .tag(this)
                .params("openid",openid)
                .params("type",AppConstants.AccountAssociationTYPE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Data=null;
                        boolean Flag=false;
                        int Code=0;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
//                            ToastUtil.showLong(getApplicationContext(), "检查是否绑定" + s.toString());
                            jsonData = jsonObject.getString("Message");
                            Code = jsonObject.getInt("Code");
                            Data = jsonObject.getString("ReturnData");
                            Flag = jsonObject.getBoolean("Flag");
//                            logininfos=gson.fromJson(Data,Logininfobean.class);
//                            ToastUtil.showLong(context, "Flag="+Flag);
                            if (AppConstants.wWEIXINTYPE.equals("WeiXinLogin")){

                                if (Flag) {//已经绑定 跳转登录绑定
                                    if (Code == 1) {
                                        AppConstants.ISLOGIN = Flag;
                                        AppConstants.USERNAME = new JSONObject(Data).getString("Name");
                                        AppConstants.USERID = new JSONObject(Data).getInt("ID");
                                        SpUtils.putString(getApplicationContext(), LoginKey, Data, LoginFile);
                                        ToastUtil.showLong(getApplicationContext(), "登录成功");
                                    }

                                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                    intent.putExtra("ShoppingCartFragment", "");
                                    intent.putExtra("specItemBeans", "");
                                    startActivity(intent);
                                } else {//未绑定 跳转注册绑定或者登录绑定
                                    Intent intent = new Intent(getApplicationContext(), LoginAccountAssociationActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else{
                                if (Code == 0) {
//                                    ToastUtil.showLong(getApplicationContext(), "微信绑定操作" + s.toString());
                                    Intent intent=new Intent(getApplicationContext(),AccountAssociationActivity.class);
                                    startActivity(intent);
                                    finish();
//                                    BindOrNotThirdParty(AppConstants.USERID,openid,AppConstants.AccountAssociationTYPE,"binding");
                                }
                            }
//                            infos=gson.fromJson(Data,new TypeToken<ArrayList<Logininfobean>>(){}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            ToastUtil.showLong(context, "e="+e);
                        }

//                        ToastUtil.showShort(context,jsonData);//已经绑定，直接获取用户名登录成功
//                        switch(Code){
//                            case 0:
//                                Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
//                                startActivity(intent);
//                                break;
//                            case 1:
//                                ToastUtil.showLong(context, "返回已绑定数据"+s.toString());
//
//                                finish();
//                            default:
//                                break;
//                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        Log.i(TAG, "onBefore: 登录");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
//                        ToastUtil.showShort(context,"请求超时"+e);
                    }
                });
    }
}