package com.musicdo.musicshop.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.libzxing.activity.CaptureActivity;
import com.musicdo.musicshop.activity.BrandRecommendtivity;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.BuyGoodsActivity;
import com.musicdo.musicshop.activity.ImageShowActivity;
import com.musicdo.musicshop.activity.MainActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.PurchaselistActivity;
import com.musicdo.musicshop.activity.QrcodeActivity;
import com.musicdo.musicshop.activity.SearchActivity;
import com.musicdo.musicshop.activity.SearchProuductActivity;
import com.musicdo.musicshop.adapter.BrandmuseumAdapter;
import com.musicdo.musicshop.adapter.GridAdapter;
import com.musicdo.musicshop.adapter.MyAdapter;
import com.musicdo.musicshop.adapter.MyExpandableListViewAdapter;
import com.musicdo.musicshop.adapter.MyRecyclerAdapter;
import com.musicdo.musicshop.adapter.NetworkImageHolderView;
import com.musicdo.musicshop.bean.AdInfoBean;
import com.musicdo.musicshop.bean.BrandRecommendBean;
import com.musicdo.musicshop.bean.HomeBannerBean;
import com.musicdo.musicshop.bean.HomeBrandBean;
import com.musicdo.musicshop.bean.HomeFirstAd;
import com.musicdo.musicshop.bean.HomeSecondBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.loopswitch.AutoSwitchAdapter;
import com.musicdo.musicshop.loopswitch.AutoSwitchView;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.Banner;
import com.musicdo.musicshop.view.CusPtrClassicFrameLayout;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.FreshScrollView;
import com.musicdo.musicshop.view.FullyGridLayoutManager;
import com.musicdo.musicshop.view.MySwipeRefreshLayout;
import com.musicdo.musicshop.view.RefreshCompleteCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.mylibrary.PtrClassicFrameLayout;
import com.example.mylibrary.PtrDefaultHandler;
import com.example.mylibrary.PtrFrameLayout;
import com.example.mylibrary.PtrHandler;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by adilsoomro on 8/19/16.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener,RefreshCompleteCallBack {
    private final static String TAG = HomeFragment.class.getSimpleName();
    ImageView Qrcode,iv;
    TextView tv_brandmuseum_more;
    SimpleDraweeView im_AdInfo2;
    ImageView im_AdInfo1;
    public View view;
    public FreshScrollView sv_home_layout;
    private EditText et_search;
    private List<String> recyclemDatas;
    private List<Drawable> functionDatas;
    private List<String> functionDataTitle;
    private List<Drawable> brandmuseumDatas;
    RecyclerView function_recyclerview,grid_recycler,brandmuseum_recyclerview,rc_grid_education;
    private FullyGridLayoutManager mLayoutManager;
    int page=1;
    MyRecyclerAdapter function_recycleAdapter;
    BrandmuseumAdapter brandmuseum_recyclerAdapter;
//    private MySwipeRefreshLayout swipeRefreshLayout;
    List<Meizi> meizis;
    ArrayList<HomeBannerBean> homeBanners=new ArrayList<>();
    ArrayList<HomeFirstAd> homefirstAds=new ArrayList<>();
    ArrayList<BrandRecommendBean> brandRecommendBeans=new ArrayList<>();
    ArrayList<MusicalBean> musicalBeans=new ArrayList<>();
    ArrayList<MusicalBean> educationBeans=new ArrayList<>();
    AdInfoBean adInfoFirst=new AdInfoBean();
    AdInfoBean adInfoSecond=new AdInfoBean();
    private GridAdapter gridAdapter;
    private MyExpandableListViewAdapter myExpandableAdapter;
    private GridLayoutManager function_gridLayoutManager;
    private GridLayoutManager brandmuseum_gridLayoutManager;
    int[] function_ItemIcon={R.mipmap.brand,R.mipmap.piano,R.mipmap.guitar,R.mipmap.windinstrument,
            R.mipmap.stringedinstruments,R.mipmap.nationalinstruments,R.mipmap.keyboardinstrument,R.mipmap.percussioninstruments,
            R.mipmap.bookgift,R.mipmap.category};
    String[] function_ItemTitle={"品牌","钢琴馆","吉他馆","管乐馆","弦乐馆","民乐馆","键盘乐馆","打击乐馆","配件馆","分类"};
    int[] brandmuseum_ItemIcon={};
    RecyclerView.LayoutManager mManager;
    private List<Object> mDatas;
    private MyAdapter mAdapter;
    public ConvenientBanner vp_item_goods_img;
    private AutoSwitchView mAutoSwitchView;
    private AutoSwitchAdapter autoSwitchAdapter;
    private LinearLayout ll_home_popularity,ll_home_purchaselist,ll_home_buygoods,ll_AdInfo1;
    RelativeLayout rl_loopswitch;
    private ArrayList<HomeSecondBean> homeSecondBeans=new ArrayList<>();
    //    private NestedExpandaleListView el_homesecond;
    onClickChangePageListner mChangeListener;
    PtrClassicFrameLayout mPtrFrame;//in.srain.cube.views.ptr
    List<String> imgUrls;
    List<String> videoUrls;

    SensorManager mSensorManager;
    JZVideoPlayer.JZAutoFullscreenListener mSensorEventListener;

    NetworkImageHolderView networkImageHolderView;
    CBViewHolderCreator cBViewHolderCreator;
    JZVideoPlayerStandard jzVideoPlayerStandard;
    public MainActivity activity;

    NetworkImageHolderView networkImageHolderView1;
    CBViewHolderCreator cBViewHolderCreator1;
    public Context myContext;
    @Override
    public View initView() {
        Fresco.initialize(mContext);
        view= LayoutInflater.from(mContext).inflate(R.layout.main_home_layout,null);

        cBViewHolderCreator1 = new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                networkImageHolderView1 = new NetworkImageHolderView(200, 200);//修改原图片宽高
                return networkImageHolderView1;
            }
        };
        return view;


    }


    @Override
    public void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(mContext);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(mContext);
        }
        if (vp_item_goods_img!=null)
        vp_item_goods_img.startTurning(4000);
//        Authorization();//针对android 6.0 api-23及其以上的权限管理
        jzVideoPlayerStandard= (JZVideoPlayerStandard) view.findViewById(R.id.videoplayer);
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) jzVideoPlayerStandard.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.width = AppConstants.ScreenWidth;// 控件的宽强制设成30
        linearParams.height = (AppConstants.ScreenWidth*188)/375;// 控件的宽强制设成30
        jzVideoPlayerStandard.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        jzVideoPlayerStandard.SAVE_PROGRESS = false;//不保存视频进度
        videoUrls=new ArrayList<>();
        videoUrls.add("http://www.musicdo.cn/img/mp4.mp4");
        jzVideoPlayerStandard.setUp(videoUrls.get(0), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
//        loadVideoScreenshot(mContext,url,jzVideoPlayerStandard.thumbImageView,1000);
//http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp
        Picasso.with(mContext).load("http://www.musicdo.cn/img/up-img.jpg")
                .resize(AppConstants.ScreenWidth,(AppConstants.ScreenWidth*188)/375)
//                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
//                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(jzVideoPlayerStandard.thumbImageView);


    }

    private void Authorization() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) mContext,new String[]{
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("imgUrls",(ArrayList<String>) imgUrls);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            imgUrls=savedInstanceState.getStringArrayList("imgUrls");
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
                    /*ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},OPENCAMERA);*/
//                    ToastUtil.showLong(context,"请打开打开全部权限");
                    CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                    builder.setMessage("需要开启权限后才能使用");
                    builder.setTitle("");
                    builder.setNegativeButton("设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());
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
    public void onPause() {
        super.onPause();
        if (vp_item_goods_img!=null)
        vp_item_goods_img.stopTurning();
        JZVideoPlayer.goOnPlayOnPause();
    }

    /**
     * 音乐馆数据
     */
    private void GetHomeThird() {
        OkHttpUtils.get(AppConstants.GetHomeThird)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 加载成功");
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            if (jsonObject.isNull("Data")){
                                jsonData = jsonObject.getString("Data");//
                            }else{
                                return;
                            }
                            message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                            ToastUtil.showShort(mContext,message);
                        if (educationBeans!=null){
                            educationBeans.clear();
                        }
                        educationBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<MusicalBean>>() {}.getType());
                        //显示乐器馆
                            /*if(itemEducationAdapter==null&&educationBeans!=null){
                                itemEducationAdapter = new ItemEducationAdapter(mContext,educationBeans);
                                rc_grid_education.setAdapter(itemEducationAdapter);//recyclerview设置适配器
                                itemEducationAdapter.setOnItemClickListener(new ItemEducationAdapter.OnRecyclerViewItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(mContext,ProductDetailActivity.class);
                                        intent.putExtra("prod_id",educationBeans.get(position).getID());
                                        startActivity(intent);
                                    }
                                });
                                rc_grid_education.setNestedScrollingEnabled(false);
                            }*/
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        swipeRefreshLayout.setRefreshing(true);
//                            Log.i(TAG, "onBefore: 开始加载");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 音乐馆数据
     */
    private void GetHomeSecond() {
        OkHttpUtils.get(AppConstants.GetHomeSecond)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 加载成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                        mPtrFrame.refreshComplete();
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(mContext,message);
                        if (musicalBeans!=null){
                            musicalBeans.clear();
                        }
                        homeSecondBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<HomeSecondBean>>() {}.getType());
//                        musicalBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<MusicalBean>>() {}.getType());
                        //加载第一部分接口数据

                        //加载第二部分接口数据
                        if(gridAdapter==null&&homeSecondBeans!=null){
                            gridAdapter = new GridAdapter(mContext,homeSecondBeans);
                            grid_recycler.setAdapter(gridAdapter);//recyclerview设置适配器
                            /*gridAdapter.setOnItemClickListener(new GridAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                                    intent.putExtra("prod_id",homeSecondBeans.get(position).);
                                    startActivity(intent);
                                }
                            });*/

                            grid_recycler.setNestedScrollingEnabled(false);
                        }



                        /*if(gridAdapter==null&&musicalBeans!=null){
                            gridAdapter = new GridAdapter(mContext,musicalBeans);
                            grid_recycler.setAdapter(gridAdapter);//recyclerview设置适配器
                            gridAdapter.setOnItemClickListener(new GridAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                                    intent.putExtra("prod_id",musicalBeans.get(position).getID());
                                    startActivity(intent);
                                }
                            });
                            grid_recycler.setNestedScrollingEnabled(false);
                        }*/
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        if (!swipeRefreshLayout.isRefreshing()) {
//                            swipeRefreshLayout.setRefreshing(true);
//                        }
                        Log.i(TAG, "onBefore: 开始加载");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.i(TAG, "onError: 音乐馆数据");
                        mPtrFrame.refreshComplete();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });

    }

    /**
     * 广告Banner、品牌馆数据
     */
    private void GetHomeFirst() {
        OkHttpUtils.get(AppConstants.GetHomeFirst)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 加载成功");
                        mPtrFrame.refreshComplete();
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String bannerData=null;
                        String AdInfo1=null;
                        String AdInfo2=null;
                        String Brand_Top10_Recommend=null;
                        String message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                            bannerObject = new JSONObject(jsonData);
                            bannerData = bannerObject.getString("AdInfoRun");//banner图
                            AdInfo1 = bannerObject.getString("AdInfo1");//横屏广告图
                            AdInfo2 = bannerObject.getString("AdInfo2");//横屏广告图
                            Brand_Top10_Recommend = bannerObject.getString("Brand_Top10_Recommend");//品牌馆
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        homeBanners.clear();
                        brandRecommendBeans.clear();
                        homeBanners= gson.fromJson(bannerData, new TypeToken<ArrayList<HomeBannerBean>>() {}.getType());
                        brandRecommendBeans= gson.fromJson(Brand_Top10_Recommend, new TypeToken<ArrayList<BrandRecommendBean>>() {}.getType());
                        adInfoFirst= gson.fromJson(AdInfo1,AdInfoBean.class);
                        adInfoSecond= gson.fromJson(AdInfo2,AdInfoBean.class);
                        //显示Banner轮播图
                        //显示横屏广告
//                        im_AdInfo1.setImageURI(AppConstants.PhOTOADDRESS+adInfoFirst.getQueryArray());
//                            im_AdInfo2.setImageURI(AppConstants.PhOTOADDRESS+adInfoSecond.getQueryArray());
//                        ll_AdInfo1
//                        Picasso.with(mContext)
//                                .load(AppConstants.PhOTOADDRESS+adInfoFirst.getQueryArray())
//                .resize(AppConstants.ScreenWidth,(AppConstants.ScreenWidth*180)/691)
////                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
////                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
//                                .config(Bitmap.Config.RGB_565)
//                                .placeholder(R.mipmap.img_start_loading)
//                                .error(R.mipmap.img_load_error)
//                                .into(im_AdInfo1);
                        //八大品牌馆
                        brandmuseum_recyclerAdapter=new BrandmuseumAdapter(mContext , brandRecommendBeans);
                        brandmuseum_recyclerview.setAdapter(brandmuseum_recyclerAdapter);
                        brandmuseum_recyclerAdapter.setOnItemClickListener(new BrandmuseumAdapter.OnItemClickListener(){
                            @Override
                            public void onItemClick(View view , int position){
                                Intent intent=new Intent(mContext, SearchProuductActivity.class);
                                intent.putExtra("keyword",brandRecommendBeans.get(position).getName());
                                startActivity(intent);
                            }
                        });

                        if (homeBanners!=null) {
                            if (homeBanners.size() != 0) {
                                if (autoSwitchAdapter!=null){
                                }else{
                                    autoSwitchAdapter = new AutoSwitchAdapter(mContext.getApplicationContext(), homeBanners);
                                    mAutoSwitchView.setAdapter(autoSwitchAdapter);
//                            mAutoSwitchView.setStop();
                                    mAutoSwitchView.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            v.getParent().requestDisallowInterceptTouchEvent(true);
                                            return false;
                                        }
                                    });

                                    if (imgUrls==null){
                                    imgUrls = new ArrayList<String>();
                                        for(HomeBannerBean o : homeBanners) {
                                            imgUrls.add(AppConstants.PhOTOADDRESS+(String)o.getImgUrl());
                                            if (imgUrls.size()==5){
                                                break;
                                            }
                                        }
                                    }else{
                                        if (imgUrls.size()==0){
                                            imgUrls = new ArrayList<String>();
                                            for(HomeBannerBean o : homeBanners) {
                                                imgUrls.add(AppConstants.PhOTOADDRESS+(String)o.getImgUrl());
                                                if (imgUrls.size()==5){
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    //初始化商品图片轮播
                                    vp_item_goods_img.setPages(new CBViewHolderCreator() {
                                        @Override
                                        public Object createHolder() {
                                            return new NetworkImageHolderView(AppConstants.ScreenWidth,AppConstants.ScreenHeight/4);
                                        }
                                    }, imgUrls);
                                    vp_item_goods_img.setScrollDuration(400);
                                    if (imgUrls!=null){
                                        if (imgUrls.size()==1){
                                            vp_item_goods_img.stopTurning();
                                            vp_item_goods_img.setManualPageable(false);
                                        }
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                            if (!swipeRefreshLayout.isRefreshing()) {
//                                swipeRefreshLayout.setRefreshing(true);
//                            }
                        Log.i(TAG, "onBefore: 开始加载");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.i(TAG, "onError: 音乐馆数据");
                        mPtrFrame.refreshComplete();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });
    }

    /**
     * 主页三张广告图
     * */
    private void GetHomeFirstAd() {
        OkHttpUtils.get(AppConstants.GetHomeFirstAd)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 加载成功");
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String bannerData=null;
                        String AdInfo1=null;
                        String AdInfo2=null;
                        String Brand_Top10_Recommend=null;
                        String message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");//
                            message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ll_AdInfo1= (LinearLayout) view.findViewById(R.id.ll_AdInfo1);//主页三张广告图
                        ll_AdInfo1.removeAllViews();
                        homefirstAds.clear();
                        homefirstAds= gson.fromJson(jsonData, new TypeToken<ArrayList<HomeFirstAd>>() {}.getType());
                        for (int i = 0; i <homefirstAds.size() ; i++) {
                            View rootview = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.goods_item_head_img, null);
                            SimpleDraweeView imageView=(SimpleDraweeView) rootview.findViewById(R.id.sdv_item_head_img);
                            if (i==1){
                                Picasso.with(mContext)
                                        .load(AppConstants.PhOTOADDRESS+homefirstAds.get(i).getImage())
                                        .resize(AppConstants.ScreenWidth,AppConstants.ScreenWidth*158/704)
//                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
//                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
                                        .config(Bitmap.Config.RGB_565)
                                        .placeholder(R.mipmap.img_start_loading)
                                        .error(R.mipmap.img_load_error)
                                        .into(imageView);
                                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp1.setMargins(10,6,10,6);
                                ll_AdInfo1.addView(rootview,lp1);
                            }else{
                                Picasso.with(mContext)
                                        .load(AppConstants.PhOTOADDRESS+homefirstAds.get(i).getImage())
                                        .resize(AppConstants.ScreenWidth,AppConstants.ScreenWidth)
//                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
//                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
                                        .config(Bitmap.Config.RGB_565)
                                        .placeholder(R.mipmap.img_start_loading)
                                        .error(R.mipmap.img_load_error)
                                        .into(imageView);
                                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp1.setMargins(0,0,0,0);
                                ll_AdInfo1.addView(rootview,lp1);
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                            if (!swipeRefreshLayout.isRefreshing()) {
//                                swipeRefreshLayout.setRefreshing(true);
//                            }
                        Log.i(TAG, "onBefore: 开始加载");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.i(TAG, "onError: 音乐馆数据");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });
    }
    private void makeBrandmuseumData() {
        brandmuseumDatas=new ArrayList<>();
        for ( int i=0; i < brandmuseum_ItemIcon.length; i++) {
            brandmuseumDatas.add(getResources().getDrawable(brandmuseum_ItemIcon[i]));
        }
    }

    private void makeFunctionData() {
        functionDatas=new ArrayList<>();
        for ( int i=0; i < function_ItemIcon.length; i++) {
            int position=function_ItemIcon[i];
            functionDatas.add(getResources().getDrawable(function_ItemIcon[i]));
        }
        functionDataTitle=new ArrayList<>();
        for ( int i=0; i < 10; i++) {
            functionDataTitle.add(function_ItemTitle[i]);
        }
    }



    /**
     * demo
     */
    private void doSth() {
        OkHttpUtils.get("http://gank.io/api/data/福利/10/"+page)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onBefore: 完成刷新");

                        page++;
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("results");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(meizis==null||meizis.size()==0){
                            meizis= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                        }else{
                            List<Meizi>  more= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                            meizis.addAll(more);
                        }
                        setData();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        if (!swipeRefreshLayout.isRefreshing()) {
//                            swipeRefreshLayout.setRefreshing(true);
//                        }
//                        Log.i(TAG, "onBefore: 开始刷新");
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }

                });
    }

    private void setData() {

        if(gridAdapter==null){
//                    gridAdapter = new GridAdapter(mContext,meizis);
            grid_recycler.setAdapter(gridAdapter);//recyclerview设置适配器
            gridAdapter.setOnItemClickListener(new GridAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                    startActivity(intent);
                }
            });
            grid_recycler.setNestedScrollingEnabled(false);
        }else{
            gridAdapter.notifyDataSetChanged();
        }

                /*if(itemEducationAdapter==null&&educationBeans!=null){
//                    itemEducationAdapter = new ItemEducationAdapter(mContext,meizis);
                    rc_grid_education.setAdapter(itemEducationAdapter);//recyclerview设置适配器
                    itemEducationAdapter.setOnItemClickListener(new ItemEducationAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(mContext,ProductDetailActivity.class);
                            startActivity(intent);
                        }
                    });
                            rc_grid_education.setNestedScrollingEnabled(false);
                }else{
                    //让适配器刷新数据
                    itemEducationAdapter.notifyDataSetChanged();
                }*/

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        },500);

    }

    @Override
    public void initData() {
        super.initData();
//        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) view.findViewById(R.id.videoplayer);
//        jzVideoPlayerStandard.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
//                , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "嫂子闭眼睛");
//        jzVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"));
        startView();
    }

    private void startView() {
        mPtrFrame=(PtrClassicFrameLayout) view.findViewById(R.id.grid_swipe_refresh);
        rl_loopswitch=(RelativeLayout) view.findViewById(R.id.rl_loopswitch);
        rl_loopswitch.getLayoutParams().width=AppConstants.ScreenWidth;
        rl_loopswitch.getLayoutParams().height=AppConstants.ScreenHeight/4;
//        自带刷新控件
//        swipeRefreshLayout=(MySwipeRefreshLayout) view.findViewById(R.id.grid_swipe_refresh) ;
//        swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//        swipeRefreshLayout.setRefreshing(true);
        //另一个刷新控件
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                GetHomeFirst();
                GetHomeSecond();
                GetHomeFirstAd();//新增主页三张广告
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
        //加载数据
        GetHomeFirst();
        GetHomeSecond();
//        GetHomeFirstAd();
        //播放视频
//        View root = LayoutInflater.from(mContext).inflate(R.layout.layout, null);


//        fl_goods_image1.addView(root);


        Log.e(TAG,"主页面的Fragment的UI被初始化了");
        vp_item_goods_img = (ConvenientBanner) view.findViewById(R.id.vp_item_goods_img);
        vp_item_goods_img.getLayoutParams().width=AppConstants.ScreenWidth;
        vp_item_goods_img.getLayoutParams().height=AppConstants.ScreenHeight/4;
        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        vp_item_goods_img.setPageIndicator(new int[]{R.mipmap.tracking_logistics_item2, R.mipmap.loopswitch_page_current});
        vp_item_goods_img.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        vp_item_goods_img.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                if (getActivity().isPause())
//                    return;
                if (position==0){//首页轮播图问题
                Intent intent=new Intent(mContext,QrcodeActivity.class);
                intent.putExtra("url",AppConstants.ABOUTMUSICDU);
                startActivity(intent);
                }
            }
        });
        //初始化轮播图
        mAutoSwitchView = (AutoSwitchView) view.findViewById(R.id.loopswitch);
        RelativeLayout.LayoutParams linearParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        linearParams.width=AppConstants.ScreenWidth;
        linearParams.height=AppConstants.ScreenHeight/4;
        mAutoSwitchView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

        //人气产品
        ll_home_popularity= (LinearLayout) view.findViewById(R.id.ll_home_popularity);
//        ll_home_popularity.setOnClickListener(this);
        ll_home_purchaselist= (LinearLayout) view.findViewById(R.id.ll_home_purchaselist);
//        ll_home_purchaselist.setOnClickListener(this);
        ll_home_buygoods= (LinearLayout) view.findViewById(R.id.ll_home_buygoods);
//        ll_home_buygoods.setOnClickListener(this);
        //乐器馆
//        el_homesecond = (NestedExpandaleListView) view.findViewById(R.id.el_homesecond);
        grid_recycler = (RecyclerView) view.findViewById(R.id.grid_recycler);
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 1);
        grid_recycler.setLayoutManager(layoutManage);
        rc_grid_education = (RecyclerView) view.findViewById(R.id.rc_grid_education );
        GridLayoutManager grid_education_layoutManage = new GridLayoutManager(mContext,2);
        rc_grid_education.setLayoutManager(grid_education_layoutManage);

        //八大品牌馆
//        makeBrandmuseumData();
        brandmuseum_recyclerview = (RecyclerView) view.findViewById(R.id.rv_home_brandmuseum_list );
        brandmuseum_gridLayoutManager=new GridLayoutManager(mContext,5,GridLayoutManager.VERTICAL,false);
        brandmuseum_recyclerview.setLayoutManager(brandmuseum_gridLayoutManager);
        sv_home_layout=(FreshScrollView)view.findViewById(R.id.sv_home_layout);
        im_AdInfo1=(ImageView)view.findViewById(R.id.im_AdInfo1);
        im_AdInfo2=(SimpleDraweeView)view.findViewById(R.id.im_AdInfo2);
        im_AdInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,QrcodeActivity.class);
                intent.putExtra("url",AppConstants.ABOUTMUSICDU);
                startActivity(intent);
            }
        });
        iv=(ImageView)view.findViewById(R.id.iv);
        et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.setOnClickListener(this);
        iv.setOnClickListener(this);

        tv_brandmuseum_more=(TextView) view.findViewById(R.id.tv_brandmuseum_more);
        tv_brandmuseum_more.setOnClickListener(this);
        //扫一扫
        Qrcode=(ImageView)view.findViewById(R.id.iv_qrcode);
        Qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开扫描界面扫描条形码或二维码

                Intent openCameraIntent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                //生成二维码
//                Bitmap qrBitmap = generateBitmap("http://www.csdn.net",400, 400);
//                Bitmap addBitmap=addLogo(qrBitmap,BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//                iv.setImageBitmap(addBitmap);
            }
        });
        //十大功能列表
        makeFunctionData();
        function_recyclerview = (RecyclerView) view.findViewById(R.id.rv_home_function_list );
        function_recycleAdapter= new MyRecyclerAdapter(mContext , function_ItemIcon,functionDataTitle);
        function_recycleAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
//                Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
            }
        });
        function_gridLayoutManager=new GridLayoutManager(mContext,5,GridLayoutManager.VERTICAL,false);//设置为一个3列的纵向网格布局
        function_recyclerview.setLayoutManager(function_gridLayoutManager);
        function_recyclerview.setAdapter(function_recycleAdapter);
        function_recycleAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(homeSecondBeans==null){
                    return;
                }else if(homeSecondBeans.size()==0){
                    return;
                }
                switch (position){
                    case 0: {//品牌
                        Intent intent = new Intent(mContext, BrandRecommendtivity.class);
                        startActivity(intent);
                    }
                        break;
                    case 1: {//钢琴
                        if(homeSecondBeans.get(0)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(0).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 2:{//吉他
                        if(homeSecondBeans.get(2)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(2).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 3: {//管乐器
                        if(homeSecondBeans.get(3)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(3).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 4: {//弦乐器
                        if(homeSecondBeans.get(6)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(6).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 5:{//民族乐器
                        if(homeSecondBeans.get(5)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(5).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 6:{//键盘乐器
                        if(homeSecondBeans.get(1)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(1).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 7:{//打击乐器
                        if(homeSecondBeans.get(4)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(4).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 8:{//配件
                        if(homeSecondBeans.get(8)==null){
                            return;
                        }
                        Intent intent = new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("categoryID", homeSecondBeans.get(8).getCategoryID());
                        startActivity(intent);
                    }
                    break;
                    case 9://分类
                        mChangeListener.setOnClickChangePageListner(1);
                        break;
                }
                if (position!=9) {

                }else{

                }
            }
        });
        setListener();//刷新数据
        //轮播图
        function_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于两个item时，自动加载下一页
//                if (newState == RecyclerView.SCROLL_STATE_IDLE
//                        && lastVisibleItem +2>=mLayoutManager.getItemCount()) {
//                    new GetData().execute("http://gank.io/api/data/福利/10/"+(++page));
//                }
                final Picasso picasso = Picasso.with(mContext);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    picasso.resumeTag(mContext);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取加载的最后一个可见视图在适配器的位置。

            }
        });
//        swipeRefreshLayout.setRefreshing(false);
//        handler.post(runnable);

    }



    private void setListener(){
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                homeSecondBeans.clear();
//                homeBanners.clear();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                GetHomeFirst();//加载数据
//                            }
//                        },800);
                GetHomeFirst();//加载数据
                GetHomeSecond();//加载数据
                *//*if (autoSwitchAdapter!=null){
                    autoSwitchAdapter.notifyDataSetChanged();
                }*//*

//                GetHomeThird();//加载数据
//                doSth();
            }
        });*/
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        if (activity instanceof onClickChangePageListner) {
            mChangeListener = (onClickChangePageListner) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void refreshComplete() {
        mPtrFrame.refreshComplete();
    }

    /**
     * 定义地接口，用于fragment和activity之间的数据传递
     */
    public interface onClickChangePageListner{
        public void setOnClickChangePageListner(int index);
    }
    public void setChangePage(onClickChangePageListner mChangeListener){
        this.mChangeListener=mChangeListener;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_home_popularity: {
                /*Intent intent = new Intent(mContext, PopularityActivity.class);
                startActivity(intent);*/
            }
            break;
            case R.id.ll_home_purchaselist: {
                Intent intent = new Intent(mContext, PurchaselistActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.ll_home_buygoods: {
                Intent intent = new Intent(mContext, BuyGoodsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.et_search: {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.tv_brandmuseum_more: {
                if (brandRecommendBeans == null) {
                    return;
                }else if (brandRecommendBeans.size()==0){
                    return;
                }
                Intent intent = new Intent(mContext, BrandRecommendtivity.class);
                startActivity(intent);
            }
            break;
            case R.id.iv: {
                /*Intent intent=new Intent(mContext, GoToEvaluateActivity.class);
//                intent.putStringArrayListExtra("pridImgs", (ArrayList<String>) imgs);
                startActivity(intent);*/
            }
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (swipeRefreshLayout.isRefreshing()){
//            swipeRefreshLayout.setRefreshing(false);
//        }

    }
}
