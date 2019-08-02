package com.musicdo.musicshop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.FeebBackImageAdapter;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.EvaluateBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.DialogOrderEvaluateChooseImg;
import com.musicdo.musicshop.view.OptionsPickerView;
import com.squareup.picasso.Picasso;

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

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/9/30.
 */

public class ApplyRefundActivity extends BaseActivity implements View.OnClickListener,FeebBackImageAdapter.DeleteEvaluateImags{
    private Context context;
    private LinearLayout ll_back;
    private TextView tv_title,tv_refound_reason,
            tv_evaluate_tips,tv_evaluate,im_adding,tv_question_commit,tv_refundable_amount,tv_clear_size_text;
    ArrayList<EvaluateBean> evaluateBeans=new ArrayList<>();
    int imageListPosition=0;
    FeebBackImageAdapter imageAdapter;
    RecyclerView rv_evaluate_photos;
    List<String> imgs=new ArrayList<>();
    List<File> imgFiles=new ArrayList<>();
    OptionsPickerView pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private String OrderNumber,UserName,icon,title,param,price;
    private int ProductId,OrdProID;
    private ImageView im_evaluate_img;
    private TextView tv_intro,tv_intro_param,tv_refound_price_text;
    String loginData="";
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyforrefund);
        MyApplication.getInstance().addActivity(this);
        context=this;
        OrderNumber=getIntent().getStringExtra("OrderNumber");
        UserName=getIntent().getStringExtra("UserName");
        icon=getIntent().getStringExtra("icon");
        title=getIntent().getStringExtra("title");
        param=getIntent().getStringExtra("param");
        price=getIntent().getStringExtra("price");
        ProductId=getIntent().getIntExtra("ProductId",0);
        OrdProID=getIntent().getIntExtra("OrdProID",0);
        initView();
    }

    private void setData() {
        Picasso.with(context)
                                             .load(AppConstants.PhOTOADDRESS+icon)
                                             .resize(200,200)
                                             .centerCrop()
                                             .config(Bitmap.Config.RGB_565)
                                             .placeholder(R.mipmap.img_start_loading)
                                             .error(R.mipmap.img_load_error)
                                             .into(im_evaluate_img);//加载网络图片
        tv_intro.setText(title);
        tv_intro_param.setText(param);
        tv_refound_price_text.setText(getResources().getString(R.string.pricesymbol)+price);
        tv_refundable_amount.setText(getResources().getString(R.string.pricesymbol)+price);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.USERID==0){//当内存不足，activity被销毁了，信息没有保存恢复不了，只能重新回到首页
            loginData= SpUtils.getString(context, LoginKey, LoginFile);
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
        options1Items.add("多拍/错拍/不想要");
        options1Items.add("快递一直未得送达");
        options1Items.add("未按约定时间发货");
        options1Items.add("快递无跟踪记录");
        options1Items.add("质量问题");
        options1Items.add("其他原因");
        im_evaluate_img=(ImageView) findViewById(R.id.im_evaluate_img);
        tv_intro=(TextView) findViewById(R.id.tv_intro);
        tv_intro_param=(TextView) findViewById(R.id.tv_intro_param);
        tv_refound_price_text=(TextView) findViewById(R.id.tv_refound_price_text);
        rv_evaluate_photos=(RecyclerView)findViewById(R.id.rv_evaluate_photos);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_refound_reason=(TextView)findViewById(R.id.tv_refound_reason);
        tv_evaluate_tips=(TextView)findViewById(R.id.tv_evaluate_tips);
        im_adding=(TextView)findViewById(R.id.im_adding);
        tv_question_commit=(TextView)findViewById(R.id.tv_question_commit);
        tv_refundable_amount=(TextView)findViewById(R.id.tv_refundable_amount);
        tv_clear_size_text=(TextView)findViewById(R.id.tv_clear_size_text);
        tv_evaluate=(TextView)findViewById(R.id.tv_evaluate);
        tv_title.setText("申请退款");
        ll_back.setOnClickListener(this);
        im_adding.setOnClickListener(this);
        tv_refound_reason.setOnClickListener(this);
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
        setData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
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
                            Toast toast = Toast.makeText(context, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
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
                                    Toast toast = Toast.makeText(context, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_refound_reason:
                pvOptions= new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        tv_clear_size_text.setText(options1Items.get(options1));
                        //返回的分别是三个级别的选中位置
//                ToastUtil.showShort(mContext,options1Items.get(options1));
                        /*for (int i = 0; i < allOrderBeans.size(); i++){
                            if (groupPosition==i){
                                allOrderBeans.remove(i);
                            }
                        }
                        for (int i = 0; i < selva.getGroupCount(); i++){
                            el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
//                totalCount++;
//                for (OrderDetailBean allOrder:allOrderBeans.get(i).getOrderDetail()){
//                    totalCount++;
//                }
                        }
                        selva.notifyDataSetChanged();//*/
                    }
                })
                        .setTitleText("")
                        .setDividerColor(Color.parseColor("#F5F5F5"))
                        .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                        .setContentTextSize(20)
                        .setSubmitColor(Color.BLACK)
                        .setCancelColor(Color.BLACK)
                        .setLineSpacingMultiplier(2.0f)
                        .setBackgroundId(Color.parseColor("#0Fcccccc"))
//                .setBackgroundId(0x808080)
                        .setBgColor(context.getResources().getColor(R.color.home_top_reflash_bg))
                        .setTitleBgColor(context.getResources().getColor(R.color.home_top_reflash_bg))
//                .isDialog(false)
                        .build();

                pvOptions.setPicker(options1Items);//一级选择器
        /*pvOptions.setPicker(options1Items, options2Items);//二级选择器
//        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
                pvOptions.show();
                InputMethodManager manager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(ApplyRefundActivity.this.getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.tv_question_commit:
                submitApplyRefund();
                break;
            case R.id.im_adding:
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

    private void submitApplyRefund() {
        OkHttpUtils.post(AppConstants.ApplyRefund)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
                .params("OrdProID",OrdProID)
                .params("OrderNumber", OrderNumber)
                .params("ProductId", String.valueOf(ProductId))
                .params("Reason",tv_clear_size_text.getText().toString())
                .params("RefundRemark",SpUtils.filterCharToarticle(tv_evaluate.getText().toString().toString().trim()))
                .addFileParams("file",imgFiles)//多图上传
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(String s, okhttp3.Call call, Response response) {

                                 JSONObject jsonObject;
                                 String jsonData = null;
                                 String Message = null;
                                 boolean Flag = false;
                                 Gson gson = new Gson();
                                 Gson gs =new GsonBuilder()
                                         .setPrettyPrinting()
                                         .disableHtmlEscaping()
                                         .create();
                                 try {
                                     jsonObject = new JSONObject(s);
                                     jsonData = jsonObject.getString("ReturnData");
                                     Message = jsonObject.getString("Message");
                                     Flag = jsonObject.getBoolean("Flag");
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }

                                 if (!Flag) {
                            ToastUtil.showShort(context, Message);
                                     return;
                                 } else {
                                     ToastUtil.showShort(context, Message);
                                     finish();
                                 }
                             }

                             @Override
                             public void onBefore(BaseRequest request) {
                                 super.onBefore(request);

                             }

                             @Override
                             public void onError(Call call, Response response, Exception e) {
                                 super.onError(call, response, e);
                                 ToastUtil.showShort(context, "数据加载超时"+e);
                             }
                         }
                );
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
