package com.musicdo.musicshop.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.UserInfoBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.NativeUtil;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.DialogOrderEvaluateChooseImg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 个人中心
 * Created by Yuedu on 2017/9/23.
 */

public class PersinalCenterActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_addressmanager,tv_QRcode,tv_persinal_level,tv_persinal_member,tv_persinal_nickname,tv_member_text,tv_nickname_text,tv_persinal_icon,tv_title;
    private ImageView iv_back;
    private SimpleDraweeView im_persinal_icon;
    private Context context;
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private UserInfoBean  userInfoBean;
    List<String> imgs=new ArrayList<>();
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persinalcenter);
        MyApplication.getInstance().addActivity(this);
        userInfoBean=getIntent().getParcelableExtra("UserInfoBean");
        context=this;
        initview();
        Authorization();//针对android 6.0 api-23及其以上的权限管理
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

        getUserInfo();

    }

    private void initview() {
        im_persinal_icon=(SimpleDraweeView)findViewById(R.id.im_persinal_icon);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_addressmanager=(TextView)findViewById(R.id.tv_addressmanager);
        tv_addressmanager.setOnClickListener(this);
        tv_QRcode=(TextView)findViewById(R.id.tv_QRcode);
        tv_QRcode.setOnClickListener(this);
        tv_persinal_level=(TextView)findViewById(R.id.tv_persinal_level);
        tv_persinal_level.setOnClickListener(this);
        tv_persinal_member=(TextView)findViewById(R.id.tv_persinal_member);
        tv_persinal_member.setOnClickListener(this);
        tv_persinal_nickname=(TextView)findViewById(R.id.tv_persinal_nickname);
        tv_persinal_nickname.setOnClickListener(this);
        tv_member_text=(TextView)findViewById(R.id.tv_member_text);
        tv_nickname_text=(TextView)findViewById(R.id.tv_nickname_text);
        tv_persinal_icon=(TextView)findViewById(R.id.tv_persinal_icon);
        tv_persinal_icon.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("个人信息");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
                    b= NativeUtil.compressBitmapUpload(b,"");//图片压缩
                    String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    String fileNmae = AppConstants.SAVEIMAGE + "usericon.jpg";
//                    String fileNmae = AppConstants.SAVEIMAGE + name + ".jpg";
                    File myCaptureFile = new File(fileNmae);
                    if(myCaptureFile.exists()) {
                        myCaptureFile.delete();
                        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver mContentResolver = context.getContentResolver();
//                        String where = MediaStore.Images.Media.DATA + "='" + fileNmae + "'";
//删除图片
                        /*mContentResolver.delete(uri, fileNmae, null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            new MediaScanner(context, fileNmae);
                        } else {
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                    }*/
                    }
                    try {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            if (!myCaptureFile.getParentFile().exists()) {
                                myCaptureFile.getParentFile().mkdirs();
                            }
                            BufferedOutputStream bos;
                            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                            b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                            bos.flush();
                            bos.close();
                        } else {
                            ToastUtil.showLong(context,"保存失败，SD卡无效");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgs.add(fileNmae);//文件以时间命名，文件名可通过截取获得
                    final Map<String, String> param=new HashMap<String, String>();
                            param.put("UserID",String.valueOf(AppConstants.USERID));
//                            uploadFile(myCaptureFile, AppConstants.UpdateUserImgUrl,param);
                    UpdateUserImg(fileNmae);//上传头像图片
                    break;
                case 2:
                    //相册图片另存为
                    try{
                        Bitmap saveBitmap = null;
                        Uri uri = data.getData();
                        ContentResolver cr = this.getContentResolver();
                        saveBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        saveBitmap= NativeUtil.compressBitmapUpload(saveBitmap,"");//图片压缩
                        if (saveBitmap != null) {
                            //文件以时间命名，文件名可通过截取获得
//                            String imageName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//                            String imageDir = AppConstants.SAVEIMAGE + imageName + ".jpg";
                            String imageDir = AppConstants.SAVEIMAGE  + "usericon.jpg";
                            imgs.add(imageDir);
                            File myFile = new File(imageDir);
                            try {
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    if (!myFile.getParentFile().exists()) {
                                        myFile.getParentFile().mkdirs();
                                    }
                                    BufferedOutputStream bos;
                                    bos = new BufferedOutputStream(new FileOutputStream(myFile));
                                    saveBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                                    bos.flush();
                                    bos.close();
                                } else {
                                    ToastUtil.showLong(context,"保存失败，SD卡无效");
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            UpdateUserImg(imageDir);//上传头像图片
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void UpdateUserImg(String fileNmae) {
        OkHttpUtils.post(AppConstants.UpdateUserImgUrl)
                .tag(this)
                .params("file", new File(fileNmae))//
                .params("UserID", String.valueOf(AppConstants.USERID))//
                .execute(new StringCallback(){

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        String error=s;
                        String message="";
                        try {
                            JSONObject json=new JSONObject(s);
                            message=json.getString("Message");
                            getUserInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ToastUtil.showLong(context,message.toString());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_addressmanager:{
                Intent intent=new Intent(context,AddressManagerActivity.class);
                intent.putExtra("PersinalCenterActivity","PersinalCenterActivity");
                startActivity(intent);
            }
            break;
            case R.id.tv_QRcode:{
//                Intent intent=new Intent(context,FeedBackActivity.class);
//                startActivity(intent);
            }
            break;
            case R.id.tv_persinal_level:
//                finish();
                break;
            case R.id.tv_persinal_member: {
//                Intent intent=new Intent(context,AccountAssociationActivity.class);
//                startActivity(intent);
            }
            break;
            case R.id.tv_persinal_nickname: {
//                UpdateUserNickName();
                Intent intent=new Intent(context,UpdateNickNameActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.tv_persinal_icon:
                Authorization();//针对android 6.0 api-23及其以上的权限管理
                DialogOrderEvaluateChooseImg.Builder builder = new DialogOrderEvaluateChooseImg.Builder(context);
//                                builder.setMessage("账户在其他地方已登录");
//                                builder.setTitle("注意");
                builder.setPositiveButton("本地图片", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent local = new Intent();
                        local.setType("image/*");
                        local.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(local, 2);
                    }
                });
                builder.setNeutralButton("拍照上传", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(it, 1);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }

    }

    private void Authorization() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(PersinalCenterActivity.this, Manifest.permission.CAMERA);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},11);
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
                    builder.setMessage("需要开启权限后才能使用");
                    builder.setTitle("");
                    builder.setNegativeButton("设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                            }
                            startActivity(localIntent);
                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }}
    }

    private void UpdateUserNickName() {
        String NickName="";
        loginData = SpUtils.getString(context, LoginKey, LoginFile);
        try {
            AppConstants.USERNAME=new JSONObject(loginData).getString("Name");
            AppConstants.USERID=new JSONObject(loginData).getInt("ID");
            NickName=new JSONObject(loginData).getString("NickName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils.post(AppConstants.UpdateUserNickName)
                .tag(this)
                .params("NickName",NickName)//
                .params("UserID", String.valueOf(AppConstants.USERID))//
                .execute(new StringCallback(){

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        String error=s;
                        String message="";
                        try {
                            JSONObject json=new JSONObject(s);
                            message=json.getString("Message");
                            getUserInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ToastUtil.showLong(context,message.toString());
                    }
                });
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
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", "UTF-8");  //设置编码
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
//                        Log.i(TAG, key+"="+params+"##");
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
//                sb.append("Content-Type: image/pjpeg; charset="+CHARSET+LINE_END);
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
                    Toast.makeText(context,"上传成功",Toast.LENGTH_LONG).show();
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
//                    Log.i(TAG, "上传图片: 上传失败"+res+result);
                }
                else{
                    Toast.makeText(context,"上传失败",Toast.LENGTH_LONG).show();
//                    Log.i(TAG, "上传图片: 上传失败"+res+result);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取用户信息（头像昵称等）
     */
    private void getUserInfo() {
        OkHttpUtils.post(AppConstants.GetUserInfo)
                .params("UserID",AppConstants.USERID)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
//                        Log.i(TAG, "onBefore: 完成刷新");
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        boolean flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            flag = jsonObject.getBoolean("Flag");
//                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (flag) {
                            userInfoBean = gson.fromJson(jsonData, UserInfoBean.class);
                            tv_nickname_text.setText(userInfoBean.getNickName());
                            tv_member_text.setText(AppConstants.USERNAME);
                            if (!userInfoBean.getImageUrl().equals("")) {
                                im_persinal_icon.setImageURI(userInfoBean.getImageUrl());
                            }else{
                                im_persinal_icon.setActualImageResource(R.mipmap.ic_launcher);
                            }

                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);

//                        Log.i(TAG, "onBefore: 开始刷新");
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);

    }
}