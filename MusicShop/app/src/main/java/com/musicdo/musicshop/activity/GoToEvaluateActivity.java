package com.musicdo.musicshop.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.AllOrderExpandableListViewAdapter;
import com.musicdo.musicshop.adapter.OrderEvaluateAdapter;
import com.musicdo.musicshop.bean.EvaluateBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.NativeUtil;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.DialogOrderEvaluateChooseImg;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.OrderLongTimeDeliverDialog;
import com.musicdo.musicshop.view.WrapContentLinearLayoutManager;

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
 * 描述:商品评价
 * 作者：haiming on 2017/9/1 10:01
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class GoToEvaluateActivity extends BaseActivity implements View.OnClickListener,
        OrderEvaluateAdapter.OrderAddingImags, OrderEvaluateAdapter.OrderUpdateImags,
        OrderEvaluateAdapter.OrderEvaluate, OrderEvaluateAdapter.OrderRatingLevel,
        OrderEvaluateAdapter.OrderEvaluateNum, OrderEvaluateAdapter.OrderPro_DesLevel,
        OrderEvaluateAdapter.OrderLogistics_service,
        OrderEvaluateAdapter.DeleteEvaluateImags,
        OrderEvaluateAdapter.OrderPro_Service {
    private Context context;
    private EditText tv_evaluate;
    private LinearLayout ll_back;
    private TextView tv_evaluate_tips, tv_title;
    private RecyclerView rv_evaluate_product;
    ArrayList<EvaluateBean> evaluateBeans = new ArrayList<>();
    OrderEvaluateAdapter evaluateAdapter;
    List<String> imgs = new ArrayList<>();
    List<String> prodTitles = new ArrayList<>();
    List<Integer> prodIds = new ArrayList<>();
    GridLayoutManager list_layoutManage;
    List<File> imgFiles = new ArrayList<>();
    int imageListPosition = 0;
    private RelativeLayout rl_evaluate_commit;
    boolean ischange = false;
    LoadingDialog dialog;
    private int shopId = 0;
    private int ProductId = 0;
    private String shopName = "";
    private String orderNumber = "";
    String loginData = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gotoevaluate);
        MyApplication.getInstance().addActivity(this);
        context = this;
        imgs = getIntent().getStringArrayListExtra("pridImgs");
        prodTitles = getIntent().getStringArrayListExtra("prodTitles");
        prodIds = getIntent().getIntegerArrayListExtra("prodIds");
        shopId = getIntent().getIntExtra("shopId", 0);
        shopName = getIntent().getStringExtra("shopName");
        orderNumber = getIntent().getStringExtra("orderNumber");
        initData();
        Authorization();//针对android 6.0 api-23及其以上的权限管理
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initData() {
        if (prodIds.size() > 1) {
            for (int i = 0; i < imgs.size(); i++) {
                EvaluateBean evaluate = new EvaluateBean();
                evaluate.setSrc(imgs.get(i));
                evaluate.setName(prodTitles.get(i));
                evaluate.setOrderNumber(orderNumber);
                evaluate.setProductId(String.valueOf(prodIds.get(i)));
                evaluateBeans.add(evaluate);
            }
        } else {
            for (String img : imgs) {
                EvaluateBean evaluate = new EvaluateBean();
                evaluate.setSrc(img);
                evaluate.setOrderNumber(orderNumber);
                evaluate.setProductId(String.valueOf(prodIds.get(0)));
                evaluate.setName(prodTitles.get(0));
                evaluateBeans.add(evaluate);
            }
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight == 0 || AppConstants.ScreenHeight == 0) {
            AppConstants.ScreenWidth = ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight = ScreenUtil.getScreenHeight(context);
        }
        if (AppConstants.USERID == 0) {//当内存不足，activity被销毁了，信息没有保存恢复不了，只能重新回到首页
            loginData = SpUtils.getString(context, "LoginKey", "LoginFile");
            if (!loginData.equals("")) {//获取本地信息
                AppConstants.ISLOGIN = true;
                try {
                    AppConstants.USERNAME = new JSONObject(loginData).getString("Name");
                    AppConstants.USERID = new JSONObject(loginData).getInt("ID");
                    AppConstants.PHONE = new JSONObject(loginData).getString("Phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initView() {
        dialog = new LoadingDialog(context, R.style.LoadingDialog);
        rv_evaluate_product = (RecyclerView) findViewById(R.id.rv_evaluate_product);
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rv_evaluate_product.setLayoutManager(layoutManage);
//        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        evaluateAdapter = new OrderEvaluateAdapter(context, evaluateBeans);
        evaluateAdapter.setAddingImage(this);
        evaluateAdapter.setUpdateImage(this);
        evaluateAdapter.setEvaluate(this);
        evaluateAdapter.setEvaluateNum(this);
        evaluateAdapter.setRatingLevel(this);
        evaluateAdapter.setPro_DesLevel(this);
        evaluateAdapter.setPro_Service(this);
        evaluateAdapter.setLogistics_service(this);
        evaluateAdapter.DoDeleteEvaluateImags(this);
        rv_evaluate_product.setAdapter(evaluateAdapter);

        tv_evaluate = (EditText) findViewById(R.id.tv_evaluate);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_evaluate_tips = (TextView) findViewById(R.id.tv_evaluate_tips);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("商品评价");
        tv_evaluate.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private boolean isEdit = true;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = tv_evaluate.getSelectionStart();
                selectionEnd = tv_evaluate.getSelectionEnd();
//                Log.i("gongbiao1",""+selectionStart);
                if (temp.length() > 120) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    tv_evaluate.setText(s);
                    tv_evaluate.setSelection(tempSelection);
                    tv_evaluate_tips.setText("120/120");
                } else {
                    tv_evaluate_tips.setText(temp.length() + "/120");
                }
            }
        });
        rl_evaluate_commit = (RelativeLayout) findViewById(R.id.rl_evaluate_commit);
        rl_evaluate_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                CustomDialog.Builder builder = new CustomDialog.Builder(context);
                builder.setMessage("提醒");
                builder.setTitle("确定取消评价吗?");
                builder.setNegativeButton("取消发布", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setPositiveButton("继续发布", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
            case R.id.rl_evaluate_commit:
                //提交评论
                if (shopId == 0) {
                    ToastUtil.showShort(context, "请求超时");
                    return;
                }
                String OrderNoAndProID = "";
                String CommentLevel = "";
                String CommentContent = "";
                String ProductName = "";
                String Pro_Des = "";
                String Pro_Service = "";
                String Pro_Logistics = "";
                for (int i = 0; i < evaluateBeans.size(); i++) {
                    OrderNoAndProID = OrderNoAndProID + evaluateBeans.get(i).getOrderNumber() + "-" + evaluateBeans.get(i).getProductId() + ",";
                    if (evaluateBeans.get(i).getCommentLevel() != null) {
                        CommentLevel = CommentLevel + evaluateBeans.get(i).getCommentLevel() + ",";
                    } else {
                        ToastUtil.showShort(context, "请对商品评价");
                        return;
                    }
                    CommentContent = CommentContent + evaluateBeans.get(i).getCommentContent() + "&&&";
                    ProductName = ProductName + evaluateBeans.get(i).getName() + "&&&";
                    Pro_Des = evaluateBeans.get(i).getPro_Des();
                    Pro_Service = evaluateBeans.get(i).getPro_Service();
                    Pro_Logistics = evaluateBeans.get(i).getPro_Logistics();
                }
                OrderNoAndProID = OrderNoAndProID.substring(0, OrderNoAndProID.length() - 1);
                if (CommentLevel != null) {
                    CommentLevel = CommentLevel.substring(0, CommentLevel.length() - 1);
                } else {
                    ToastUtil.showShort(context, "请对商品评价");
                    return;
                }
                if (CommentContent != null) {
                    CommentContent = CommentContent.substring(0, CommentContent.length() - 3);
                }
                ProductName = ProductName.substring(0, ProductName.length() - 3);
                if (Pro_Des != null) {
                    Pro_Des = Pro_Des.substring(0, Pro_Des.length() - 2);
                } else {
                    ToastUtil.showShort(context, "请对店铺评价");
                    return;
                }
                if (Pro_Service != null) {
                    Pro_Service = Pro_Service.substring(0, Pro_Service.length() - 2);
                } else {
                    ToastUtil.showShort(context, "请对店铺评价");
                    return;
                }
                if (Pro_Logistics != null) {
                    Pro_Logistics = Pro_Logistics.substring(0, Pro_Logistics.length() - 2);
                } else {
                    ToastUtil.showShort(context, "请对店铺评价");
                    return;
                }
                int IsAdditional = 0;//是否追加评论
                /*evaluateBeans.get(evaluateBeans.size()-1).setOrderNoAndProID();
                evaluateBeans.get(evaluateBeans.size()-1).setCommentLevel();
                evaluateBeans.get(evaluateBeans.size()-1).setCommentContent();
                evaluateBeans.get(evaluateBeans.size()-1).setProductName();
                evaluateBeans.get(evaluateBeans.size()-1).setPro_Des();
                evaluateBeans.get(evaluateBeans.size()-1).setPro_Service();
                evaluateBeans.get(evaluateBeans.size()-1).setPro_Logistics();*/
                submitEvaluate(OrderNoAndProID, CommentLevel, CommentContent, ProductName, Pro_Des, Pro_Service, Pro_Logistics, IsAdditional);
                break;
        }
    }

    /**
     * 提交商品评论
     */
    private void submitEvaluate(String OrderNoAndProID, String CommentLevel, String CommentContent, String ProductName, String Pro_Des, String Pro_Service, String Pro_Logistics, int IsAdditional) {
        OkHttpUtils.post(AppConstants.PostFiles)
                .tag(this)
                .params("UserName", AppConstants.USERNAME)
                .params("ShopID", shopId)//订单编号
                .params("ShopName", shopName)//搜索名称
                .params("OrderNoAndProID", OrderNoAndProID)
                .params("CommentLevel", CommentLevel)
                .params("CommentContent", CommentContent)
                .params("ProductName", ProductName)
                .params("Pro_Des", Pro_Des)
                .params("Pro_Service", Pro_Service)
                .params("Pro_Logistics", Pro_Logistics)
                .params("IsAdditional", IsAdditional)
                .addFileParams("file", imgFiles)//多图上传

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);

                        }
                        JSONObject jsonObject;
                        String jsonData = null;
                        String Message = null;
                        boolean Flag = false;
                        Gson gson = new Gson();
                        Gson gs = new GsonBuilder()
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
                            ToastUtil.showShort(context, "评论成功");
                            finish();
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
                        ToastUtil.showShort(context, "数据加载超时");
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                    }
                });

        if (dialog.isShowing()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);
        }
    }

    /**
     * 拍照上传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
                    b = NativeUtil.compressBitmapUpload(b, "");//图片压缩
                    //转换bitmap成byte
//                    changeByte(b);
//                    encode=Bitmap2Bytes(b);
//                    evaluateBeans.get(imageListPosition).setDrawable(new BitmapDrawable(b));
                    if (evaluateAdapter != null)
                        evaluateAdapter.notifyDataSetChanged();
//                    img.setImageBitmap(b);
//                    String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    //拼接上传图片名称例如："订单号-商品ID_1.jpg"该商品的第一张评论，第二张评论图命名为"订单号-商品ID_2.jpg"，以此类推，保存在文件列表数字里面
                    String name = "";
                    if (evaluateBeans.get(imageListPosition).getImgpath() != null) {
                        int index = evaluateBeans.get(imageListPosition).getImgpath().size() + 1;
                        name = evaluateBeans.get(imageListPosition).getOrderNumber() + "-" + evaluateBeans.get(imageListPosition).getProductId() + "_" + index;
                    } else {
                        name = evaluateBeans.get(imageListPosition).getOrderNumber() + "-" + evaluateBeans.get(imageListPosition).getProductId() + "_" + 1;
                    }
                    String fileNmae = AppConstants.SAVEIMAGE + name + ".jpg";
//                    srcPath = fileNmae;
//                    System.out.println(srcPath+"----------保存路径1");
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
                            Toast toast = Toast.makeText(GoToEvaluateActivity.this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List<String> imgs = new ArrayList<>();
                    if (evaluateBeans.get(imageListPosition).getImgpath() != null) {
                        for (String imgpath : evaluateBeans.get(imageListPosition).getImgpath())
                            imgs.add(imgpath);
                    }
                    imgs.add(fileNmae);//文件以时间命名，文件名可通过截取获得
                    imgFiles.add(myCaptureFile);
                    evaluateBeans.get(imageListPosition).setImgpath(imgs);
                    break;
                case 2:
                    try {
                        final List<String> localImgs = new ArrayList<>();
                        Bitmap saveBitmap = null;
                        Uri uri = data.getData();
                        ContentResolver cr = this.getContentResolver();
                        saveBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        //获取的图片压缩
                        saveBitmap = NativeUtil.compressBitmapUpload(saveBitmap, "");
                        if (saveBitmap != null) {
                            //文件以时间命名，文件名可通过截取获得
                            String imageName_pre = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                            String imageName = "";
                            if (evaluateBeans.get(imageListPosition).getImgpath() != null) {
                                int index = evaluateBeans.get(imageListPosition).getImgpath().size() + 1;
                                imageName = evaluateBeans.get(imageListPosition).getOrderNumber()+"-"+evaluateBeans.get(imageListPosition).getProductId()+"_"+index;
//                                imageName = imageName_pre + "_" + index;
                            } else {
                                imageName = evaluateBeans.get(imageListPosition).getOrderNumber()+"-"+evaluateBeans.get(imageListPosition).getProductId()+"_"+1;
//                                imageName = imageName_pre + "_" + 1;
                            }
                            final String imageDir = AppConstants.SAVEIMAGE + imageName + ".jpg";
                            final File myFile = new File(imageDir);
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (evaluateBeans.get(imageListPosition).getImgpath() != null) {
                                        for (String imgpath : evaluateBeans.get(imageListPosition).getImgpath())
                                            localImgs.add(imgpath);
                                    }
                                    localImgs.add(imageDir);
                                    imgFiles.add(myFile);
                                    evaluateBeans.get(imageListPosition).setImgpath(localImgs);
                                    if (evaluateAdapter != null)
                                        evaluateAdapter.notifyDataSetChanged();
                                }
                            }, 500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /*Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        evaluateBeans.get(imageListPosition).setDrawable(new BitmapDrawable(bitmap));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (evaluateAdapter!=null)
                    evaluateAdapter.notifyDataSetChanged();*/
                    break;
                default:
                    break;
            }
            ;
        }
    }

    private void Authorization() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(GoToEvaluateActivity.this, Manifest.permission.CAMERA);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 11);
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
                                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
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

            }
        }
    }

    @Override
    public void AddingImages(Drawable drawable, int product, int position) {
        Authorization();//针对android 6.0 api-23及其以上的权限管理
        imageListPosition = position;
//        if (evaluateBeans.get(imageListPosition).getImgpath().size()==5)
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
    }

    @Override
    public void UpdateImags(ArrayList<EvaluateBean> datas) {//获取adapter更新图片
        /*evaluateBeans.clear();
        evaluateBeans.addAll(datas);*/
    }

    @Override
    public void Evaluate(String content, final int position) {
        evaluateBeans.get(position).setCommentLevel(content);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                evaluateAdapter.notifyItemChanged(position);
//            }
//        },100);

    }

    @Override
    public void ratingLevel(String content, final int position) {
        evaluateBeans.get(position).setCommentLevel(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                evaluateAdapter.notifyItemChanged(position);
            }
        }, 100);
    }

    @Override
    public void EvaluateNum(final String number, final int position) {
        if (evaluateBeans == null)
            return;
        if (number.equals("")) {
            return;
        }
//        evaluateAdapter.upData(position);
//        rv_evaluate_product.setAdapter(evaluateAdapter);
        String numberString=SpUtils.filterCharToarticle(number.toString().trim());
        evaluateBeans.get(position).setCommentContent(numberString);
        evaluateBeans.get(position).setEvaluateStringLength(String.valueOf(numberString.trim().length()));
        evaluateAdapter.notifyItemChanged(position);
//                evaluateAdapter.upData(position);
    }

    @Override
    public void Pro_DesLevel(String content, final int position) {
        evaluateBeans.get(position).setPro_Des(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                evaluateAdapter.notifyItemChanged(position);
            }
        }, 100);
    }

    @Override
    public void logistics_service(String content, final int position) {
        evaluateBeans.get(position).setPro_Logistics(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                evaluateAdapter.notifyItemChanged(position);
            }
        }, 100);
    }

    @Override
    public void pro_Service(String content, final int position) {
        evaluateBeans.get(position).setPro_Service(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                evaluateAdapter.notifyItemChanged(position);
            }
        }, 100);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            CustomDialog.Builder builder = new CustomDialog.Builder(context);
            builder.setMessage("提醒");
            builder.setTitle("确定取消评价吗?");
            builder.setNegativeButton("取消发布", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setPositiveButton("继续发布", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return true;
        }
        return false;
    }

    @Override
    public void DeleteImgs(String path, String fileName, int index) {
        if (imgFiles != null) {
            if (imgFiles.size() != 0) {
                List<File> imgFileCaces = new ArrayList<>();
                for (int i = 0; i < imgFiles.size(); i++) {
                    if (i != index) {
                        imgFileCaces.add(imgFiles.get(i));
                    }
                }
                imgFiles.clear();
                imgFiles.addAll(imgFileCaces);
            }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("GoToEvaluate Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
