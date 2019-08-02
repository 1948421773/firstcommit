package com.musicdo.musicshop.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.wxapi.WXEntryActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/9/22.
 */

public class LoginHasAccountAssociationActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener,CompoundButton.OnCheckedChangeListener{
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private ImageView iv_back;
    byte[] encode;
    private MyApplication application;
    String TAG = "LoginHasAccountAssociationActivity";
    private ImageView img;
    private EditText img_content;
    private Button nati,pai,submit;
    LinearLayout photo_full;
    private static String srcPath;
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private String loginKey;
    private EditText et_username,et_password;
    private Button bt_username_clear,bt_password_clear,login;
    private CheckBox ck_pwd_eye;
    private TextView login_forgot,tv_title;
    private Logininfobean logininfos=new Logininfobean();
    private ArrayList<Logininfobean> infos=new ArrayList<>();
    private Context context;
    Tencent mTencent;
    private IWXAPI mWXapi;
    String type;
    private UserInfo userInfo; //qq用户信息
    BaseUiListener baseUiListener=new BaseUiListener();
    LoadingDialog dialog;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_hasaccountassociation);
        MyApplication.getInstance().addActivity(this);
        context=this;
        init();
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

    private void init() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_title.setText("登录绑定");
        iv_back=(ImageView)findViewById(R.id.iv_back);
        et_username=(EditText)findViewById(R.id.et_username);
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
                        if (et_password.getText().toString().trim().length()>0){
                            login.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                        }
                }else{
                    bt_username_clear.setVisibility(View.GONE);
                    login.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
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
                        if (et_password.getText().toString().trim().length()>0){
                            login.setBackground(getResources().getDrawable(R.drawable.login_login_click));
                        }
                }else{
                    bt_password_clear.setVisibility(View.GONE);
                    login.setBackground(getResources().getDrawable(R.drawable.login_login_unclick));
                }
            }
        });
        bt_username_clear=(Button)findViewById(R.id.bt_username_clear);
        bt_password_clear=(Button)findViewById(R.id.bt_password_clear);
        login=(Button)findViewById(R.id.login);
        ck_pwd_eye=(CheckBox)findViewById(R.id.ck_pwd_eye);
        login_forgot=(TextView)findViewById(R.id.login_forgot);
//        im_wx_account=(ImageView)findViewById(R.id.im_wx_account);
//        im_qq_account=(ImageView)findViewById(R.id.im_qq_account);
        login_forgot=(TextView)findViewById(R.id.login_forgot);
        iv_back.setOnClickListener(this);
//        im_wx_account.setOnClickListener(this);
//        im_qq_account.setOnClickListener(this);
        login_forgot.setOnClickListener(this);
        bt_username_clear.setOnClickListener(this);
        bt_password_clear.setOnClickListener(this);
        login.setOnClickListener(this);
        et_username.setOnFocusChangeListener(this);
        et_password.setOnFocusChangeListener(this);
        ck_pwd_eye.setOnCheckedChangeListener(this);
        //测试图片上传
//        testUpLoadImg();
    }

    private void testUpLoadImg() {
        img = (ImageView) findViewById(R.id.img);
        nati = (Button) findViewById(R.id.natives);
        pai = (Button) findViewById(R.id.pai);
        submit = (Button) findViewById(R.id.submit);
        img_content=(EditText)findViewById(R.id.img_content);
        nati.setOnClickListener(this);
        pai.setOnClickListener(this);
        submit.setOnClickListener(this);

        View.OnClickListener keyboard_hide = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) LoginHasAccountAssociationActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

        };
        photo_full=(LinearLayout)findViewById(R.id.photo_full);
        photo_full.setClickable(true);
        photo_full.setOnClickListener(keyboard_hide);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.natives:
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);
                break;
            case R.id.pai:
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, 1);
                break;
            case R.id.submit:
                if (srcPath == null || srcPath=="") {
//                    showToast("文件不存在");
                }else{
                    submitUploadFile();
                }
                break;
            case R.id.bt_password_clear:
                et_password.setText("");
                break;
            case R.id.login:
                if (et_username.getText().toString().trim().length()==0||et_password.getText().toString().trim().length()==0) {
                    ToastUtil.showShort(getApplicationContext(),"账号或密码错误");
                }else{
                    Login(et_username.getText().toString().trim(), et_password.getText().toString().trim());
                }
                break;
            case R.id.bt_username_clear:
                et_username.setText("");
                break;
            case R.id.login_forgot: {
                Intent intent = new Intent(LoginHasAccountAssociationActivity.this, ForgotPasswordaActivity.class);
                startActivity(intent);
            }
            break;

            /*case R.id.im_qq_account: {
                type="QQ";
                QQAccountAssociation();
            }
            break;
            case R.id.im_wx_account: {
                type="WeiXin";
                weChatlogin();
            }
            break;*/
        }
    }


    private void weChatlogin() {
        /**
         微信授权登录
         * */
        Log.i("space",">>>>>>>>>>wxLogin()");
        //初始化微信相关
        mWXapi = WXAPIFactory.createWXAPI(LoginHasAccountAssociationActivity.this, AppConstants.APP_ID, true);
        if(!mWXapi.isWXAppInstalled())
        {
//                JsCallbackExecutor.onSocialWXLoginResult(CallbackCode.NO_INSTALL_WX, "weixin no install");
            Log.i("space","weixin no install");
            return ;
        }
//        WXEntryActivity.mHaveCode = false;
        mWXapi.registerApp(AppConstants.APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWXapi.sendReq(req);
    }

    private void QQAccountAssociation() {
        mTencent = Tencent.createInstance(AppConstants.QQ_APP_ID, LoginHasAccountAssociationActivity.this);
        if (!mTencent.isSessionValid()){
            mTencent.login(this, "all",baseUiListener);
            Log.i("all",">>>>>>>>>>login");
        }
    }



    /*IUiListener userInfoListener = new LoginHasAccountAssociationActivity.BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
//            ToastUtil.showLong(context,"IUiListener"+values);
            if(values == null){
                return;
            }
            try {
                JSONObject jo = values;
                int ret = jo.getInt("ret");
                System.out.println("json=" + String.valueOf(jo));
                String nickName = jo.getString("nickname");
                String gender = jo.getString("gender");

                Toast.makeText(LoginHasAccountAssociationActivity.this, "你好，" + nickName,
                        Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };*/

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            String openid = null;
            try {
                JSONObject jsonResponse = (JSONObject) response;
                openid=(String) jsonResponse.get("openid");
//                ToastUtil.showLong(context,"BaseUiListener"+openid);
                if ((int) jsonResponse.get("ret") == 0) {
                    openid = jsonResponse.getString("openid");
                    String accessToken = jsonResponse.getString("access_token");
                    String expires = jsonResponse.getString("expires_in");
                    mTencent.setOpenId(openid);
                    mTencent.setAccessToken(accessToken, expires);
                    /*userInfo = new UserInfo(LoginHasAccountAssociationActivity.this, mTencent.getQQToken());
                    userInfo.getUserInfo(userInfoListener);*/
                }
                CheckBangDing(openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
//            ToastUtil.showLong(context, "onError: " +e.errorDetail);
//            Toast.makeText(context,"授权失败",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
//            ToastUtil.showLong(context, "onCancel:");
//            Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
//            startActivity(intent);
        }

    }

//    public void getUserInfoInThread()
//    {
//        new Thread(){
//            @Override
//            public void run() {
//                JSONObject json = mTencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET);
//                System.out.println(json);
//            }
//        }.start();
//    }

    private void CheckBangDing(String openid) {
//        ToastUtil.showLong(context, "执行CheckBangDing");
        OkHttpUtils.post(AppConstants.CheckUserBingding)
                .tag(this)
                .params("openid",openid)
                .params("type",type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        logininfos=null;
                        infos.clear();
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Data=null;
                        boolean Flag=false;
                        int Code=0;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
//                            ToastUtil.showLong(context, "检查是否绑定"+s.toString());
                            jsonData = jsonObject.getString("Message");
                            Code=jsonObject.getInt("Code");
                            Data=jsonObject.getString("ReturnData");
                            Flag=jsonObject.getBoolean("Flag");
//                            logininfos=gson.fromJson(Data,Logininfobean.class);
//                            ToastUtil.showLong(context, "Flag="+Flag);
                            if (Flag){//已经绑定 跳转登录绑定
                                if (Code==1) {
                                    AppConstants.ISLOGIN = Flag;
                                    AppConstants.USERNAME = new JSONObject(Data).getString("Name");
                                    AppConstants.USERID = new JSONObject(Data).getInt("ID");
                                    SpUtils.putString(context, LoginKey, Data, LoginFile);
                                    ToastUtil.showLong(context, "登录成功");
                                }
                                finish();
                            }else{//已经绑定 跳转注册绑定
                                Intent intent=new Intent(context,LoginAccountAssociationActivity.class);
                                startActivity(intent);
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
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"请求超时"+e);
                    }
                });
    }

    /**
     * 登录*/
    private void Login(String phoneNumber,String password) {
//        ToastUtil.showLong(context,"提交信息"+"/n"+"userName"+phoneNumber+"'/n'"+"pwd"+password+"Type"+AppConstants.AccountAssociationTYPE+"openid"+AppConstants.OPENID);
        OkHttpUtils.post(AppConstants.LoginByCheck)
                .tag(this)
                .params("UserName",phoneNumber)
                .params("Pwd",password)
                .params("Type",AppConstants.AccountAssociationTYPE)
                .params("OpenId",AppConstants.OPENID)
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
                        logininfos=null;
                        infos.clear();
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Data=null;
                        boolean Flag=false;
                        int Code=0;
                        Gson gson=new Gson();

                        try {
                            jsonObject = new JSONObject(s);
//                            ToastUtil.showLong(context,"已有帐号登录返回"+s);
                            jsonData = jsonObject.getString("Message");
                            Code=jsonObject.getInt("Code");
                            Data=jsonObject.getString("ReturnData");
                            JSONObject jsonUserName = new JSONObject(Data);
                            Flag=jsonObject.getBoolean("Flag");
                            logininfos=gson.fromJson(Data,Logininfobean.class);
                            if (Flag){
                                    if (SpUtils.isEmpty(Data)){
//                                        ToastUtil.showShort(context,"登录绑定失败");
                                    }else{
                                        AppConstants.ISLOGIN=Flag;
                                        AppConstants.USERNAME=new JSONObject(Data).getString("Name");
                                        AppConstants.USERID=new JSONObject(Data).getInt("ID");
                                        AppConstants.PHONE=new JSONObject(Data).getString("Phone");
                                        SpUtils.putString(context,LoginKey,Data,LoginFile);

                                        switch(Code){
                                            case 1: {
                                                ToastUtil.showShort(context, jsonData);
                                                Intent intent = new Intent(context, MainActivity.class);
                                                intent.putExtra("ShoppingCartFragment", "");
                                                intent.putExtra("HomeFragment", "HomeFragment");
                                                intent.putExtra("specItemBeans", "");
                                                startActivity(intent);
                                            }
                                                break;
                                            case 2:
                                                ToastUtil.showShort(context,jsonData);

                                                break;
                                            case 3: {
                                                ToastUtil.showLong(context, jsonData);
                                                Intent intent = new Intent(context, MainActivity.class);
                                                intent.putExtra("ShoppingCartFragment", "");
                                                intent.putExtra("HomeFragment", "HomeFragment");
                                                intent.putExtra("specItemBeans", "");
                                                startActivity(intent);
                                            }
                                                break;
                                            default:
                                                ToastUtil.showShort(context,jsonData);
                                                break;
                                        }
                                    }

                            }else{
                                ToastUtil.showShort(context,jsonData);
                            }
//                            infos=gson.fromJson(Data,new TypeToken<ArrayList<Logininfobean>>(){}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AppConstants.OPENID="";
                        AppConstants.AccountAssociationTYPE="";

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
                        ToastUtil.showShort(context,"登录超时");
                    }
                });
    }
    /**
     * 拍照上传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Tencent.onActivityResultData(requestCode, resultCode, data,baseUiListener);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN ||requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,baseUiListener);
        }
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
                    //转换bitmap成byte
//                    changeByte(b);
//                    encode=Bitmap2Bytes(b);
                    img.setImageBitmap(b);
                    String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    String fileNmae = Environment.getExternalStorageDirectory().toString()+ File.separator+"dong/image/"+name+".jpg";
                    srcPath = fileNmae;
                    System.out.println(srcPath+"----------保存路径1");
                    File myCaptureFile =new File(fileNmae);
                    try {
                        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            if(!myCaptureFile.getParentFile().exists()){
                                myCaptureFile.getParentFile().mkdirs();
                            }
                            BufferedOutputStream bos;
                            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                            b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                            bos.flush();
                            bos.close();
                        }else{

                            Toast toast= Toast.makeText(LoginHasAccountAssociationActivity.this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Uri uri = data.getData();
                    img.setImageURI(uri);
                    ContentResolver cr = this.getContentResolver();
                    Cursor c = cr.query(uri, null, null, null, null);
                    c.moveToFirst();
                    //这是获取的图片保存在sdcard中的位置
                    srcPath = c.getString(c.getColumnIndex("_data"));
                    System.out.println(srcPath+"----------保存路径2");
                    break;
                default:
                    break;
            };
        }
//      n =1;
    }

    private void changeByte(Bitmap bitmap) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            out.flush();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);//转换为png格式的
            out.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] buffer=out.toByteArray();
        encode= Base64.decode(buffer, Base64.DEFAULT);

    }


    private void submitUploadFile(){
        OkHttpUtils.get("http://192.168.1.125:8080/Member/UploadImg")
                .tag(this)
                .params("file", String.valueOf(new File(srcPath)))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {


                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);

                    }
                });
        /*OkHttpUtils.post("http://192.168.1.125:8080/Member/UploadImg"+encode)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "上传图片: 上传成功"+s);
                        Toast.makeText(LoginHasAccountAssociationActivity.this,"上传成功",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Toast.makeText(LoginHasAccountAssociationActivity.this,"上传成功",Toast.LENGTH_LONG).show();
                        Log.i(TAG, "上传图片: 上传失败"+request);
                    }
                });*/
        /*final File file=new File(srcPath);
        final String RequestURL="http://192.168.1.125:8080/Member/UploadImg";
        if (file == null || (!file.exists())) {
            return;
        }

        Log.i(TAG, "请求的URL=" + RequestURL);
        Log.i(TAG, "请求的fileName=" + file.getName());
        final Map<String, String> params = new HashMap<String, String>();
//        params.put("user_id", bytes );
//        params.put("user_id", loginKey);
//        params.put("file_type", "1");
//        params.put("content", img_content.getText().toString());
//        showProgressDialog();
        new Thread(new Runnable() { //开启线程上传文件
            @Override
            public void run() {
                uploadFile(file, RequestURL,params);
            }
        }).start();*/
    }

    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @return  返回响应的内容
     */
    private String uploadFile(File file, String RequestURL, Map<String, String> param){
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        // 显示进度框
//      showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null){
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                String params = "";
                if (param != null && param.size() > 0) {
                    Iterator<String> it = param.keySet().iterator();
                    while (it.hasNext()) {
                        sb = null;
                        sb = new StringBuffer();
                        String key = it.next();
                        String value = param.get(key);
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                        sb.append(value).append(LINE_END);
                        params = sb.toString();
                        dos.write(params.getBytes());
//                      dos.flush();
                    }
                }
                sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"upfile\";filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: image/pjpeg; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1){
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);

                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                System.out.println("res========="+res);
                if(res==200){
                    Toast.makeText(LoginHasAccountAssociationActivity.this,"上传成功",Toast.LENGTH_LONG).show();
                    InputStream input =  conn.getInputStream();
                    StringBuffer sb1= new StringBuffer();
                    int ss ;
                    while((ss=input.read())!=-1){
                        sb1.append((char)ss);
                    }
                    result = sb1.toString();
//                 // 移除进度框
//                  removeProgressDialog();
                    finish();
                }
                else{
                    Toast.makeText(LoginHasAccountAssociationActivity.this,"上传失败",Toast.LENGTH_LONG).show();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public  byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    private byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.et_username:
                if (hasFocus&&et_username.getText().length()!=0){
                    bt_username_clear.setVisibility(View.VISIBLE);
                }else{
                    bt_username_clear.setVisibility(View.GONE);
                }
                break;
            case R.id.et_password:
                if (hasFocus&&et_password.getText().length()!=0){
                    bt_password_clear.setVisibility(View.VISIBLE);
                }else{
                    bt_password_clear.setVisibility(View.GONE);
                }
                break;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
