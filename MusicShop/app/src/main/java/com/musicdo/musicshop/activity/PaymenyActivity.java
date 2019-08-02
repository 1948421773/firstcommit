package com.musicdo.musicshop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.MyHttpClient;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.wxapi.AuthResult;
import com.musicdo.musicshop.wxapi.PayResult;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:支付界面
 * 作者：haiming on 2017/8/17 17:34
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class PaymenyActivity extends BaseActivity implements View.OnClickListener {
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private String price="";
    private String title="";
    private String OrderNumber="";
    private TextView tv_total_price,ck_wx,ck_zfb,tv_title,ck_yl;
    private LinearLayout ll_back;
    private RelativeLayout rl_ck_wx,rl_ck_zfb,rl_ck_yl;
    private Context context;
    private Button login;
    private String ipString="";//ip地址
    private int paymentchannel=0;//默认0 微信支付；1支付宝。
    String sign="";
    String xmlstring="";
    String prepay_id="";
    String RequestString="";
    Map<String,String> prepay_id_xml;
    String reqsign="";
    HashMap<String,String> callBackZFBPay=new HashMap<>();
    String loginData="";

    android.os.Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    try {
                        String message=String.valueOf(msg.obj);
                        prepay_id_xml=decodeXml(URLDecoder.decode(message, "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        sb.append(prepay_id_xml.get("prepay_id"));
                        prepay_id=sb.toString();
//                        reqMap.put("sign",reqsign);
//                        ToastUtil.showShort(context,prepay_id_xml.toString());
                        weChatPay();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case SDK_PAY_FLAG: {//此处只做了两种处理，返回9000成功和其他状态码
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PaymenyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, PaymentSuccessActivity.class);
                        intent.putExtra("price",price);
                        startActivity(intent);
                        finish();
                    } else if (TextUtils.equals(resultStatus, "6001")){
                        Toast.makeText(PaymenyActivity.this, "已取消支付", Toast.LENGTH_SHORT).show();
                    }else{
                        /*9000	订单支付成功
                        8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                        4000	订单支付失败
                        5000	重复请求
                        6001	用户中途取消
                        6002	网络连接出错
                        6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                        其它	其它支付错误*/
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PaymenyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(context, MyOrderListActivity.class);
                        intent.putExtra("TabIndex", 1);
                        startActivity(intent);*/
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(PaymenyActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(PaymenyActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        MyApplication.getInstance().addActivity(this);
        MyApplication.getInstance().addPayActivity(this);
        context=this;
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            ipString=getLocalIpAddress();
        }else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ipString = intToIp(ipAddress);
        }
        price=getIntent().getStringExtra("price");
        title=getIntent().getStringExtra("title");
        OrderNumber=getIntent().getStringExtra("OrderNumber");
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
        tv_total_price=(TextView)findViewById(R.id.tv_total_price);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("支付界面");
        login=(Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        rl_ck_wx=(RelativeLayout)findViewById(R.id.rl_ck_wx);
        rl_ck_zfb=(RelativeLayout)findViewById(R.id.rl_ck_zfb);
        rl_ck_yl=(RelativeLayout)findViewById(R.id.rl_ck_yl);
        ck_wx=(TextView)findViewById(R.id.ck_wx);
        ck_zfb=(TextView)findViewById(R.id.ck_zfb);
        ck_yl=(TextView)findViewById(R.id.ck_yl);
        rl_ck_wx.setOnClickListener(this);
        rl_ck_zfb.setOnClickListener(this);
        rl_ck_yl.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        if(price!=null) {
            if (price.length() != 0) {
                tv_total_price.setText(SpUtils.doubleToString(Double.valueOf(price)));
            } else {
                tv_total_price.setText("0.00");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back: {
                GoBackDialog();
            }
            break;
            case R.id.rl_ck_wx: {
                paymentchannel=0;
                ck_wx.setBackground(getResources().getDrawable(R.mipmap.payment_ok));
                ck_zfb.setBackground(getResources().getDrawable(R.mipmap.payment_uncheck));
                ck_yl.setBackground(getResources().getDrawable(R.mipmap.payment_uncheck));
            }
            break;
            case R.id.rl_ck_zfb: {
                paymentchannel=1;
                ck_wx.setBackground(getResources().getDrawable(R.mipmap.payment_uncheck));
                ck_zfb.setBackground(getResources().getDrawable(R.mipmap.payment_ok));
                ck_yl.setBackground(getResources().getDrawable(R.mipmap.payment_uncheck));
            }
            break;
            case R.id.rl_ck_yl: {
                paymentchannel=2;
                ck_zfb.setBackground(getResources().getDrawable(R.mipmap.payment_uncheck));
                ck_wx.setBackground(getResources().getDrawable(R.mipmap.payment_uncheck));
                ck_yl.setBackground(getResources().getDrawable(R.mipmap.payment_ok));
            }
            break;
            case R.id.login: {//调起支付

                if(paymentchannel==0) {//微信支付
                    //调用【统一下单API】生成预付单,获取到prepay_id后将参数再次签名传输给APP发起支付
                    final String[] reqsign = new String[1];
                    OkHttpUtils.post(AppConstants.WX_Generate)
                            .tag(this)
                            .params("UserName",AppConstants.USERNAME)
                            .params("OrderNumbers",OrderNumber)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, okhttp3.Call call, Response response) {
                                    HashMap<String,String> callBackWXPay=new HashMap<>();
                                    JSONObject jsonObject;
                                    String jsonData=null;
                                    String Message=null;
                                    boolean Flag=false;
                                    Gson gson=new Gson();
                                    try {
                                        jsonObject = new JSONObject(s);
                                        jsonData = jsonObject.getString("ReturnData");
                                        JSONObject WXObject = new JSONObject(jsonData);
                                        Message = jsonObject.getString("Message");
                                        Flag = jsonObject.getBoolean("Flag");
                                        callBackWXPay.put("appid",WXObject.getString("appid"));
                                        callBackWXPay.put("prepayid",WXObject.getString("prepay_id"));
                                        callBackWXPay.put("partnerid",WXObject.getString("mch_id"));
                                        callBackWXPay.put("package","prepay_id="+WXObject.getString("prepay_id"));
                                        callBackWXPay.put("noncestr",getRandomString(32));
                                        callBackWXPay.put("timestamp",String.valueOf(genTimeStamp()));
                                        reqsign[0] =genAppSign(callBackWXPay);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                ToastUtil.showShort(context,s);
                                    if (!Flag){
//                                        ToastUtil.showShort(context,Message);
                                    }else{
                                        IWXAPI api= WXAPIFactory.createWXAPI(context, AppConstants.APP_ID);
                                        api.registerApp(AppConstants.APP_ID);

                                        PayReq payRequest=new PayReq();
                                        payRequest.appId=callBackWXPay.get("appid");
                                        payRequest.partnerId=callBackWXPay.get("partnerid");
                                        payRequest.prepayId=callBackWXPay.get("prepayid");
                                        payRequest.packageValue=callBackWXPay.get("package");
                                        payRequest.nonceStr=callBackWXPay.get("noncestr");
                                        payRequest.timeStamp=callBackWXPay.get("timestamp");
                                        payRequest.sign=reqsign[0];
//        ToastUtil.showShort(context,payRequest.toString());
//        ToastUtil.showShort(context,String.valueOf(payRequest.checkArgs()));
                                        api.sendReq(payRequest);
                                    }
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
//                                    ToastUtil.showShort(context,"数据加载超时");

                                }
                            });
//                    try {
////                        getWXUnifiedOrder();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }else if (paymentchannel==1){//支付宝支付
                    OkHttpUtils.post(AppConstants.Alipay_Generate_Multiple)
                            .tag(this)
                            .params("UserName",AppConstants.USERNAME)
                            .params("OrderNumbers",OrderNumber)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, okhttp3.Call call, Response response) {
                                    JSONObject jsonObject;
                                    String jsonData=null;
                                    String Message=null;
                                    boolean Flag=false;
                                    Gson gson=new Gson();
                                    try {
                                        jsonObject = new JSONObject(s);
                                        jsonData = jsonObject.getString("ReturnData");
                                        /*JSONObject WXObject = new JSONObject(jsonData);
//                                        Message = jsonObject.getString("Message");
//                                        Flag = jsonObject.getBoolean("Flag");
                                        callBackZFBPay.put("app_id",WXObject.getString("app_id"));
                                        callBackZFBPay.put("biz_content",WXObject.getString("biz_content"));
                                        callBackZFBPay.put("charset",WXObject.getString("charset"));
                                        callBackZFBPay.put("format",WXObject.getString("format"));
                                        callBackZFBPay.put("method",WXObject.getString("method"));
                                        callBackZFBPay.put("notify_url",WXObject.getString("notify_url"));
                                        callBackZFBPay.put("sign_type",WXObject.getString("sign_type"));
                                        callBackZFBPay.put("timestamp",WXObject.getString("timestamp"));
                                        callBackZFBPay.put("version",WXObject.getString("version"));
                                        callBackZFBPay.put("sign",WXObject.getString("sign"));*/

//                                        reqsign[0] =genAppSign(callBackWXPay);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                    ToastUtil.showShort(context,s);
                                    if (!Flag){
//                                        ToastUtil.showShort(context,Message);
                                    }else{

                                    }
                                    payV2(jsonData);
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
//                                    ToastUtil.showShort(context,"数据加载超时");

                                }
                            });

                        /*Intent intent = new Intent(context, OrderActivity.class);
                        intent.putExtra("TabIndex", 0);
                        startActivity(intent);
                        finish();*/
                }else{//银联支付
                    OkHttpUtils.post(AppConstants.GetTns)
                            .tag(this)
                            .params("UserName",AppConstants.USERNAME)
                            .params("OrderNumbers",OrderNumber)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, okhttp3.Call call, Response response) {
                                    JSONObject jsonObject;
                                    String jsonData=null;
                                    String Message=null;
                                    boolean Flag=false;
                                    Gson gson=new Gson();
                                    try {
                                        jsonObject = new JSONObject(s);
                                        jsonData = jsonObject.getString("ReturnData");
                                        Message = jsonObject.getString("Message");
                                        Flag = jsonObject.getBoolean("Flag");
                                        /*JSONObject WXObject = new JSONObject(jsonData);
//                                        Message = jsonObject.getString("Message");
//                                        Flag = jsonObject.getBoolean("Flag");
                                        callBackZFBPay.put("app_id",WXObject.getString("app_id"));
                                        callBackZFBPay.put("biz_content",WXObject.getString("biz_content"));
                                        callBackZFBPay.put("charset",WXObject.getString("charset"));
                                        callBackZFBPay.put("format",WXObject.getString("format"));
                                        callBackZFBPay.put("method",WXObject.getString("method"));
                                        callBackZFBPay.put("notify_url",WXObject.getString("notify_url"));
                                        callBackZFBPay.put("sign_type",WXObject.getString("sign_type"));
                                        callBackZFBPay.put("timestamp",WXObject.getString("timestamp"));
                                        callBackZFBPay.put("version",WXObject.getString("version"));
                                        callBackZFBPay.put("sign",WXObject.getString("sign"));*/

//                                        reqsign[0] =genAppSign(callBackWXPay);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                    ToastUtil.showShort(context,s);
                                    if (!Flag){
                                        ToastUtil.showShort(context,Message);
                                    }else{
                                        ToUPPayAssistEx(jsonData);
                                    }
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
                                    ToastUtil.showShort(context,"数据加载超时");
                                }
                            });
                }
            }
            break;
        }
    }

    private void ToUPPayAssistEx(String tn) {
        UPPayAssistEx.startPay(context, null, null, tn, "00");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            Toast.makeText(PaymenyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, PaymentSuccessActivity.class);
            intent.putExtra("price",price);
            startActivity(intent);
            finish();
        } else if (str.equalsIgnoreCase("fail")) {
            Toast.makeText(PaymenyActivity.this, "支付失败！", Toast.LENGTH_SHORT).show();
        } else if (str.equalsIgnoreCase("cancel")) {
            Toast.makeText(PaymenyActivity.this, "用户取消了支付", Toast.LENGTH_SHORT).show();
        }
    }

    private void GoBackDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("注意");
        builder.setTitle("确认要离开当前支付界面");
        builder.setNegativeButton("取消支付", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                /*Intent intent = new Intent(context, MyOrderListActivity.class);
                intent.putExtra("TabIndex", 1);
                startActivity(intent);*/
                ToastUtil.showShort(context,"取消支付");
                finish();
            }
        });
        builder.setPositiveButton("继续支付", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    /**
     * 预支付提交订单，完成之后跳下一个调起确认支付方法
     */
    private void getWXUnifiedOrder() throws IOException {

        String StringA="appid=wx414bc9bba7078c6f&body=123456&mch_id=1487212422&nonce_str=5K8264ILTKCH16CQ2502SI8ZNMTM67VS&notify_url=https://www.baidu.com&out_trade_no=17081917221226221&spbill_create_ip=192.168.1.164&total_fee=0.01&trade_type=APP";
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("appid",AppConstants.APP_ID);
        hashMap.put("mch_id",AppConstants.mch_id);
        hashMap.put("nonce_str",getRandomString(32));
        hashMap.put("body",title);
        hashMap.put("out_trade_no",getOutTradeNo());
        hashMap.put("total_fee","1");
        hashMap.put("spbill_create_ip",ipString);
        hashMap.put("notify_url","https://www.baidu.com");
        hashMap.put("trade_type","APP");
        sign=getPackageSign(hashMap);//MD5加密后toUpperCase()变成大写
        hashMap.put("sign",sign);
        xmlstring =toXml(hashMap);//XML转译，拼接之后xml字符串格式
        final byte[][] buf = new byte[1][1];
        final String[] content = {""};
        new Thread(new Runnable(){
            @Override
            public void run() {
                if (AppConstants.WXUnifiedOrder == null || AppConstants.WXUnifiedOrder.length() == 0) {
                    return ;
                }

                HttpClient httpclient = MyHttpClient.getNewHttpClient(context);

                HttpPost httpPost = new HttpPost(AppConstants.WXUnifiedOrder);
                try {
                    httpPost.setEntity(new StringEntity(xmlstring, "UTF-8"));
                    httpPost.setHeader("Content-type", "text/html");
                    HttpResponse resp = httpclient.execute(httpPost);
                    if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    }
                    buf[0]= EntityUtils.toByteArray(resp.getEntity());
                    Message mg=new Message();
                    mg.obj=new String(buf[0]);
                    mg.what=0;
                    handler.sendMessage(mg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*OkHttpUtils.post(AppConstants.WXUnifiedOrder)
                .tag(this)
                .params("",URLDecoder.decode(xmlstring, "UTF-8"))
                *//*.params("mch_id",AppConstants.mch_id)
                .params("nonce_str","5K8264ILTKCH16CQ2502SI8ZNMTM67VS")
                .params("sign",sign)
                .params("body","腾讯充值中心-QQ会员充值")
                .params("out_trade_no","17081917221226221")
                .params("total_fee",0.01)
                .params("spbill_create_ip",ipString)//用户端实际ip)
                .params("notify_url","https://www.baidu.com")
                .params("trade_type","APP")*//*
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Message=null;
                        boolean Flag=false;
                        Gson gson=new Gson();
                        *//*try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*//*
                        ToastUtil.showShort(context,s);
                        if (!Flag){
//                            ToastUtil.showShort(context,Message);

                        }else{
//                            prepay_id
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");

                    }
                });*/
    }


    public Map<String,String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if("xml".equals(nodeName)==false){
                            //实例化student对象
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("Simon","----"+e.toString());
        }
        return null;

    }

    /*public static byte[] httpPost(String url, String entity) {
        if (url == null || url.length() == 0) {
            return null;
        }

        HttpClient httpclient = MyHttpClient.getNewHttpClient(context);

        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(entity, "UTF-8"));
            httpPost.setHeader("Content-type", "text/html");
            HttpResponse resp = httpclient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                HttpEntity httpEntity =resp.getEntity();
                byte[] data = EntityUtils.toByteArray(httpEntity);
                return data;
            }

            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/



    private void weChatPay() {
//        APP_ID换成自己的Appid
        IWXAPI api= WXAPIFactory.createWXAPI(context, AppConstants.APP_ID);
        api.registerApp(AppConstants.APP_ID);
        //        data根据服务器返回Json数据创建的实体类对象
        HashMap<String,String> reqMap=new HashMap<>();
        reqMap.put("appid",AppConstants.APP_ID);
        reqMap.put("prepayid",prepay_id_xml.get("prepay_id"));
        reqMap.put("partnerid",AppConstants.mch_id);
        reqMap.put("package","prepay_id="+prepay_id_xml.get("prepay_id"));
        reqMap.put("noncestr",getRandomString(32));
        reqMap.put("timestamp",String.valueOf(genTimeStamp()));
        reqsign=genAppSign(reqMap);

        PayReq payRequest=new PayReq();
        payRequest.appId=AppConstants.APP_ID;
        payRequest.partnerId=AppConstants.mch_id;
        payRequest.prepayId=prepay_id_xml.get("prepay_id");
        payRequest.packageValue=reqMap.get("package");
        payRequest.nonceStr=reqMap.get("noncestr");
        payRequest.timeStamp=reqMap.get("timestamp");
        payRequest.sign=reqsign;
//        ToastUtil.showShort(context,payRequest.toString());
//        ToastUtil.showShort(context,String.valueOf(payRequest.checkArgs()));
        api.sendReq(payRequest);
    }

    private String toXml(HashMap<String,String> params) {
        List<String> hashlist=new ArrayList<>();
        hashlist.add("appid");
        hashlist.add("mch_id");
        hashlist.add("nonce_str");
        hashlist.add("body");
        hashlist.add("out_trade_no");
        hashlist.add("total_fee");
        hashlist.add("spbill_create_ip");
        hashlist.add("notify_url");
        hashlist.add("trade_type");
        hashlist.add("sign");
        Collections.sort(hashlist, new SpellComparator());
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < hashlist.size(); i++) {
            for (String key : params.keySet()) {
                if (hashlist.get(i).equals(key)){
                    sb.append("<"+key+">");
                    sb.append(params.get(key));
                    sb.append("</"+key+">");
                }
            }
        }
        sb.append("</xml>");
        Log.e("orion",sb.toString());
        return sb.toString();
    }

    private String reqToXml(HashMap<String,String> params) {
        List<String> hashlist=new ArrayList<>();
        hashlist.add("appid");
        hashlist.add("mch_id");
        hashlist.add("nonce_str");
        hashlist.add("body");
        hashlist.add("out_trade_no");
        hashlist.add("total_fee");
        hashlist.add("spbill_create_ip");
        hashlist.add("notify_url");
        hashlist.add("trade_type");
        hashlist.add("sign");
        Collections.sort(hashlist, new SpellComparator());
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < hashlist.size(); i++) {
            for (String key : params.keySet()) {
                if (hashlist.get(i).equals(key)){
                    sb.append("<"+key+">");
                    sb.append(params.get(key));
                    sb.append("</"+key+">");
                }
            }
        }
        sb.append("</xml>");
        Log.e("orion",sb.toString());
        return sb.toString();
    }

    /**
     生成签名
     */

    private String getPackageSign(HashMap<String,String> params) {

        List<String> hashlist=new ArrayList<>();
        hashlist.add("appid");
        hashlist.add("mch_id");
        hashlist.add("nonce_str");
        hashlist.add("body");
        hashlist.add("out_trade_no");
        hashlist.add("total_fee");
        hashlist.add("spbill_create_ip");
        hashlist.add("notify_url");
        hashlist.add("trade_type");
        Collections.sort(hashlist, new SpellComparator());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashlist.size(); i++) {
            for (String key : params.keySet()) {
                if (hashlist.get(i).equals(key)){
                    sb.append(key);
                    sb.append("=");
                    sb.append(params.get(key));
                    sb.append("&");
                }
            }
        }
        sb.append("key=");
        sb.append(AppConstants.API_KEY);

//        String packageSign = sb.toString();
        String packageSign = SpUtils.MD5Encode(sb.toString(),"UTF-8").toUpperCase();//编码转换和MD5加密
        Log.e("Simon",">>>>"+packageSign);
        return packageSign;
    }
    /**
     生成支付签名
     */
    private String  genAppSign(HashMap<String, String> reqMap) {
        List<String> hashlist=new ArrayList<>();
        hashlist.add("appid");
        hashlist.add("prepayid");
        hashlist.add("partnerid");
        hashlist.add("package");
        hashlist.add("noncestr");
        hashlist.add("timestamp");
        Collections.sort(hashlist, new SpellComparator());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashlist.size(); i++) {
            for (String key : reqMap.keySet()) {
                if (hashlist.get(i).equals(key)){
                    sb.append(key);
                    sb.append("=");
                    sb.append(reqMap.get(key));
                    sb.append("&");
                }
            }
        }
        sb.append("key=");
        sb.append(AppConstants.API_KEY);
        String packageSign = SpUtils.MD5Encode(sb.toString(),"UTF-8");
        return packageSign;
    }


    //获取指定位数的随机字符串(包含小写字母、大写字母、数字,0<length)
    public  String getRandomString(int length) {
        //随机字符串的随机字符库
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    //    GPRS上网IP地址
    public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
//            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    /**
     * 汉字拼音排序比较器
     */
    class SpellComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            try {
                // 取得比较对象的汉字编码，并将其转换成字符串
                String s1 = new String(o1.toString().getBytes("GB2312"), "ISO-8859-1");
                String s2 = new String(o2.toString().getBytes("GB2312"), "ISO-8859-1");
                // 运用String类的 compareTo（）方法对两对象进行比较
                return s1.compareTo(s2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    static class SSLSocketFactoryEx extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws
                NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException,
                UnrecoverableKeyException {

            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void
                checkClientTrusted(
                        java.security.cert.X509Certificate[]
                                chain, String authType)
                        throws java.security.cert.CertificateException {
                }
                @Override
                public void
                checkServerTrusted(java.security.cert.X509Certificate[]
                                           chain, String authType)
                        throws java.security.cert.CertificateException {
                }
            };

            sslContext.init(null,new TrustManager[]{tm}, null);
        }
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            GoBackDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 支付宝支付业务
     *
     * @param
     */
    public void payV2(final String orderStr) {
        if (TextUtils.isEmpty(AppConstants.zfb_APP_ID) || (TextUtils.isEmpty(AppConstants.RSA2_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */

        /*boolean rsa2 = (AppConstants.RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(AppConstants.zfb_APP_ID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);



        String privateKey =AppConstants.RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;*/

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PaymenyActivity.this);
                Map<String, String> result = alipay.payV2(orderStr, true);
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 要求外部订单号必须唯一。
     * @return
     */
    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }
}
