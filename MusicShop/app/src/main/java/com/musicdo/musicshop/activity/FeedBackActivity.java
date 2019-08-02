package com.musicdo.musicshop.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.EvaluateImageAdapter;
import com.musicdo.musicshop.adapter.FeebBackImageAdapter;
import com.musicdo.musicshop.bean.EvaluateBean;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能反馈
 * Created by Yuedu on 2017/9/19.
 */

public class FeedBackActivity extends BaseActivity implements View.OnClickListener,FeebBackImageAdapter.DeleteEvaluateImags{
    private Context context;
    private LinearLayout ll_back;
    private TextView tv_question_unusual,tv_question_experience,tv_question_newfunction,tv_question_other,tv_title,
            tv_evaluate_tips,tv_evaluate,im_adding,tv_question_commit;
    ArrayList<EvaluateBean> evaluateBeans=new ArrayList<>();
    int imageListPosition=0;
    FeebBackImageAdapter imageAdapter;
    RecyclerView rv_evaluate_photos;
    List<String> imgs=new ArrayList<>();
    String loginData="";
    List<File> imgFiles=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
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

        Authorization();//针对android 6.0 api-23及其以上的权限管理
        if (imgs!=null){
            if (imgs.size()==0) {
                imageAdapter = new FeebBackImageAdapter(context, imgs);
                rv_evaluate_photos.setLayoutManager(new GridLayoutManager(context, 3));
                imageAdapter.DoDeleteEvaluateImags(this);
                rv_evaluate_photos.setAdapter(imageAdapter);
//            specialUpdate(((MyViewHolder)holder).tv_evaluate_tips,textNumber+"/120");

            }else{
                imageAdapter.notifyDataSetChanged();
            }
            rv_evaluate_photos.setNestedScrollingEnabled(false);
        }
    }

    private void initView() {//feebback_question_unclick_bg
        rv_evaluate_photos=(RecyclerView)findViewById(R.id.rv_evaluate_photos);

        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_evaluate_tips=(TextView)findViewById(R.id.tv_evaluate_tips);
        im_adding=(TextView)findViewById(R.id.im_adding);
        tv_question_commit=(TextView)findViewById(R.id.tv_question_commit);
        tv_evaluate=(TextView)findViewById(R.id.tv_evaluate);
        tv_title.setText("我要反馈");
        tv_question_unusual=(TextView)findViewById(R.id.tv_question_unusual);
        tv_question_experience=(TextView)findViewById(R.id.tv_question_experience);
        tv_question_newfunction=(TextView)findViewById(R.id.tv_question_newfunction);
        tv_question_other=(TextView)findViewById(R.id.tv_question_other);
        tv_question_unusual.setOnClickListener(this);
        tv_question_experience.setOnClickListener(this);
        tv_question_newfunction.setOnClickListener(this);
        tv_question_other.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        im_adding.setOnClickListener(this);
        tv_question_commit.setOnClickListener(this);
        tv_evaluate.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private boolean isEdit = true;
            private int selectionStart ;
            private int selectionEnd ;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                    selectionStart = tv_evaluate.getSelectionStart();
                    selectionEnd = tv_evaluate.getSelectionEnd();
                    if (temp.length() > 120) {
                        s.delete(selectionStart - 1, selectionEnd);
                        int tempSelection = selectionStart;
                                tv_evaluate_tips.setText(String.valueOf(s.toString().length())+"/120");
//                                specialUpdate("120");
//                                datas.get(position).setEvaluateStringLength("120");
                    } else {
                        tv_evaluate_tips.setText(String.valueOf(s.toString().length())+"/120");
//                                datas.get(position).setEvaluateStringLength(String.valueOf(s.toString().length()));
                    }
                    if (s.toString().trim().equals("")) {
//                    return;
                    }
            }
        });
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
                    String fileNmae = AppConstants.SAVEIMAGE + name + ".jpg";
                    File myCaptureFile = new File(fileNmae);
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
                            Toast toast = Toast.makeText(FeedBackActivity.this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgs.add(fileNmae);//文件以时间命名，文件名可通过截取获得
                    imgFiles.add(myCaptureFile);
                    break;
                case 2:
                    //相册图片另存为
                    try{
                    Bitmap saveBitmap = null;
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    saveBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    saveBitmap=NativeUtil.compressBitmapUpload(saveBitmap,"");//图片压缩
                    if (saveBitmap != null) {
                        //文件以时间命名，文件名可通过截取获得
                        String imageName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                        String imageDir = AppConstants.SAVEIMAGE + imageName + ".jpg";
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
                                Toast toast = Toast.makeText(FeedBackActivity.this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        imgFiles.add(myFile);
                    }
                    } catch (Exception e) {
                    e.printStackTrace();
            }

                    break;
            }
        }
    }

    private void Authorization() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(FeedBackActivity.this, Manifest.permission.CAMERA);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_question_commit:
                ToastUtil.showLong(context,"提交成功!");
                finish();
                break;
            case R.id.im_adding:
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
            case R.id.tv_question_unusual:
                tv_question_unusual.setBackgroundResource(R.drawable.feebback_question_click_bg);
                tv_question_experience.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_newfunction.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_other.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                break;
            case R.id.tv_question_experience:
                tv_question_unusual.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_experience.setBackgroundResource(R.drawable.feebback_question_click_bg);
                tv_question_newfunction.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_other.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                break;
            case R.id.tv_question_newfunction:
                tv_question_unusual.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_experience.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_newfunction.setBackgroundResource(R.drawable.feebback_question_click_bg);
                tv_question_other.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                break;
            case R.id.tv_question_other:
                tv_question_unusual.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_experience.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_newfunction.setBackgroundResource(R.drawable.feebback_question_unclick_bg);
                tv_question_other.setBackgroundResource(R.drawable.feebback_question_click_bg);
                break;
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }



    @Override
    public void DeleteImgs(final String path, final String fileName, final int index) {
        AlertDialog alert;
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle("操作提示");
        alert.setMessage("是否删除图片？");
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SpUtils.deletePhotoAtPathAndName(path,fileName);
                if (imgFiles!=null){
                    if (imgFiles.size()!=0){
                        List<File> imgFileCaces=new ArrayList<>();
                        for (int i = 0; i <imgFiles.size() ; i++) {
                            if (i!=index){
                                imgFileCaces.add(imgFiles.get(i));
                            }
                        }
                        imgFiles.clear();
                        imgFiles.addAll(imgFileCaces);
                    }
                }
                for (int i = 0; i <imgs.size() ; i++) {
                    if (imgs.get(i).equals(path + fileName)) {
                        imgs.remove(i);
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }
        });
        alert.show();
    }
}
