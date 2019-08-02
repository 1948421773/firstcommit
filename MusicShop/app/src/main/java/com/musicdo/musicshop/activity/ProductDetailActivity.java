package com.musicdo.musicshop.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.gxz.PagerSlidingTabStrip;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.AssessmentAdapter;
import com.musicdo.musicshop.adapter.ItemTitlePagerAdapter;
import com.musicdo.musicshop.bean.CommentBean;
import com.musicdo.musicshop.bean.ProductDetailBean;
import com.musicdo.musicshop.bean.ShopDetailBean;
import com.musicdo.musicshop.bean.SpecBean;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.GoodsCommentFragment;
import com.musicdo.musicshop.fragments.GoodsDetailFragment;
import com.musicdo.musicshop.fragments.GoodsInfoFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomPopWindow;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.NoScrollViewPager;
import com.umeng.socialize.UMShareAPI;
import com.v5kf.client.lib.V5ClientAgent;
import com.v5kf.client.lib.V5ClientConfig;
import com.v5kf.client.lib.entity.V5ControlMessage;
import com.v5kf.client.lib.entity.V5Message;
import com.v5kf.client.ui.ClientChatActivity;
import com.v5kf.client.ui.callback.ChatActivityFuncIconClickListener;
import com.v5kf.client.ui.callback.OnChatActivityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 名 称 ：商品详情界面
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/5.
 * 版 本 ：
 * 备 注 ：
 */

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener,GoodsInfoFragment.FragmentInteraction,GoodsInfoFragment.FragmentaddcartInteraction,OnChatActivityListener {
    private final String LoginKey = "LoginKey";
    private final String LoginFile = "LoginFile";
    private int SpecOK = 1;//选择规格
    private int SpecAddCart = 2;//选择规格，加入购物车
    private int SpecSubmitOrder = 3;//选择规格，立即购买
    public final String TAG = "ProductDetailActivity";
    public PagerSlidingTabStrip psts_tabs;
    public NoScrollViewPager vp_content;
    public TextView tv_title, tv_add_cart, tv_submitorder, tv_count_cart_tips, tv_shop_attention, tv_collect_prod,tv_shop_Customer;
    public int prodID;
    RelativeLayout rl_detail_cart;
    private List<Fragment> fragmentList = new ArrayList<>();
    private GoodsInfoFragment goodsInfoFragment;
    private GoodsDetailFragment goodsDetailFragment;
    private GoodsCommentFragment goodsCommentFragment;
    private int pridId;
    private int CartCount = 0;//购物车数量
    private Context context;
    private LinearLayout ll_back;
    private AssessmentAdapter assessmentAdapter;
    ArrayList<CommentBean> data = new ArrayList<>();
    ProductDetailBean ProductDetailBeans = new ProductDetailBean();
    ShopDetailBean shopDetailBeans = new ShopDetailBean();
    private SpecBean specBeans = new SpecBean();
    private ArrayList<SpecItemBean> specCaceBeans = new ArrayList<>();
    CommentBean commentBeans = new CommentBean();
    private String speccount = "";
    ItemTitlePagerAdapter pagerAdapternew;
    private boolean notesAddingCartState = true;//是否允许添加购物车
    private boolean chooseSpecState = false;//是否允许添加购物车
    LoadingDialog dialog;
    CustomPopWindow mCustomPopWindow;
    ImageView im_setting;
    RelativeLayout rl_menu;
    String loginData = "";
    boolean isavedInstanceStateS = false;
    private long mLastClickTime = 0;
    public static final int TIME_INTERVAL = 1000;
    RelativeLayout coordinatorLayout;
    LinearLayout masking;
    boolean isPause = false;
    SensorManager mSensorManager;
    JZVideoPlayer.JZAutoFullscreenListener mSensorEventListener;

    boolean currentScreenstate = false;
    String kefuid="";
    String fangke="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);
        MyApplication.getInstance().addActivity(this);
        context = this;
        if (savedInstanceState != null) {
            isavedInstanceStateS = true;
            ProductDetailBeans = savedInstanceState.getParcelable("ProductDetailBeans");
//            ToastUtil.showShort(context,"恢复goodsInfoFragment"+ProductDetailBeans);
            goodsInfoFragment = (GoodsInfoFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragment");
//            ToastUtil.showShort(context,"恢复GoodsDetailFragment");
            goodsDetailFragment = (GoodsDetailFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "GoodsDetailFragment");
//            ToastUtil.showShort(context,"恢复GoodsCommentFragment");
            goodsCommentFragment = (GoodsCommentFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "GoodsCommentFragment");
//            List<android.support.v4.app.Fragment> fragmentList = getSupportFragmentManager().getFragments();
//            goodsInfoFragment=(GoodsInfoFragment) fragmentList.get(0);
//            return;
            /*Intent intent=new Intent(context,MainActivity.class);
            intent.putExtra("ShoppingCartFragment", "");
            intent.putExtra("specItemBeans", "");
            intent.putExtra("HomeFragment", "HomeFragment");
            startActivity(intent);
            finish();*/
        }
        dialog = new LoadingDialog(context, R.style.LoadingDialog);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        rl_menu.setOnClickListener(this);
        pridId = getIntent().getIntExtra("prod_id", 0);
        if (pridId != 0) {
            GetData();
            RecoreProduct_Look();
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new JZVideoPlayer.JZAutoFullscreenListener();


    }

    private void Customerconfiguration() {
        V5ClientConfig config = V5ClientConfig.getInstance(this);
// V5客服系统客户端配置
// config.setShowLog(true); // 显示日志，默认为true

/*** 客户信息设置 ***/
// 如果更改了用户信息，需要在设置前调用shouldUpdateUserInfo
// config.shouldUpdateUserInfo();
// 【建议】设置用户昵称
        config.setNickname(AppConstants.USERNAME);
// 设置用户性别: 0-未知 1-男 2-女
        config.setGender(1);
        if(!fangke.equals("")){
            config.setOpenId(fangke);
        }//查看https://github.com/V5KF/V5KFClientSDK-Android/blob/master/V5ClientDemo/src/main/java/com/v5kf/sdkdemo/MainActivity.java
// 【建议】设置用户头像URL
        config.setAvatar(AppConstants.USERICON);
/**
 *【建议】设置用户OpenId，以识别不同登录用户，不设置则默认由SDK生成，替代v1.2.0之前的uid,
 *  openId将透传到座席端(长度32字节以内，建议使用含字母数字和下划线的字符串，尽量不用特殊字符，若含特殊字符系统会进行URL encode处理，影响最终长度和座席端获得的结果)
 *	若您是旧版本SDK用户，只是想升级，为兼容旧版，避免客户信息改变可继续使用config.setUid，可不用openId
 */
//        config.setOpenId(AppConstants.USERNAME);
//config.setUid(uid); //【弃用】请使用setOpenId替代
// 设置用户VIP等级(0-5)
        config.setVip(0);
// 使用消息推送时需设置device_token:集成第三方推送(腾讯信鸽、百度云推)或自定义推送地址时设置此参数以在离开会话界面时接收推送消息
//config.setDeviceToken(XGPushConfig.getToken(getApplicationContext()));

// [1.3.0新增]设置V5系统内置的客户基本信息，区别于setUserInfo，这是V5系统内置字段

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getSupportFragmentManager().putFragment(outState, "fragment", goodsInfoFragment);
        getSupportFragmentManager().putFragment(outState, "GoodsDetailFragment", goodsDetailFragment);
        getSupportFragmentManager().putFragment(outState, "GoodsCommentFragment", goodsCommentFragment);
        outState.getParcelable("ProductDetailBeans");
//        ToastUtil.showShort(context,"保存goodsInfoFragment"+ProductDetailBeans);
    }

    private void RecoreProduct_Look() {
        //添加此商品到浏览记录
        OkHttpUtils.post(AppConstants.RecordProduct_Look)
                .tag(this)
                .params("ProductID", pridId)//固定地方便调试，其他ID没数据"26013"
                .params("UserID", AppConstants.USERID)//固定地方便调试，其他ID没数据"26013"
//                .params("ID","26013")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            /*jsonData = jsonObject.getString("ReturnData");
                            commentObject = new JSONObject(jsonData);
                            comment =  commentObject.getString("Data");
                            Message = jsonObject.getString("Message");*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isPause = false;
            }
        }, 500);
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
//        if (coordinatorLayout!=null&&masking!=null){
//            coordinatorLayout.setVisibility(View.VISIBLE);
//            masking.setVisibility(View.GONE);
//        }
        //注：回调 1
        Bugtags.onResume(this);
        JZVideoPlayer.goOnPlayOnResume();
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        isPause = true;
        Bugtags.onPause(this);
        JZVideoPlayer.releaseAllVideos();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    /**
     * 重力感应自动进入全屏
     */


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }


    private void GetData() {
        if (pridId == 0) {
            ToastUtil.showShort(context, "数据加载超时");
            return;
        }
        OkHttpUtils.post(AppConstants.GetProductDetail)
                .tag(this)
                .params("ID", pridId)//固定地方便调试，其他ID没数据"26013"
                .params("UserID", AppConstants.USERID)//固定地方便调试，其他ID没数据"26013"
//                .params("ID","26013")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {

                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            commentObject = new JSONObject(jsonData);
                            comment = commentObject.getString("Data");
                            Message = jsonObject.getString("Message");
                            kefuid = jsonObject.getString("kefuid");
                            fangke = jsonObject.getString("fangke");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //客服配置
                        Customerconfiguration();
                        //评论Data不是array
                        ProductDetailBeans = gson.fromJson(jsonData, ProductDetailBean.class);
//                        ProductDetailBeans= gson.fromJson(jsonData, new TypeToken<List<ProductDetailBean>>() {}.getType());
                        commentBeans = gson.fromJson(comment, CommentBean.class);
                        data.add(commentBeans);
                        getShopData();
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
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 50);
                        ToastUtil.showShort(context, "数据加载超时");
                    }
                });


    }

    private void getShopData() {
        if (ProductDetailBeans != null) {
            if (ProductDetailBeans.getShopID() == 0) {
                return;
            }
        } else {
            return;
        }
        OkHttpUtils.get(AppConstants.GetShop_ProductCount)
                .tag(this)
                .params("ID", ProductDetailBeans.getShopID())//固定地方便调试，其他ID没数据
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            commentObject = new JSONObject(jsonData);
//                            comment =  commentObject.getString("Data");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        shopDetailBeans = gson.fromJson(jsonData, ShopDetailBean.class);
                        getSpecData();

                        /*if (!jsonData.equals("[]")){
                            pageIndex++;
                        }
                        ToastUtil.showShort(context,Message);

//                        if(searchProdBeans==null||searchProdBeans.size()==0){
//                            searchProdBeans= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
//                        }else{
//                            List<SearchProdBean>  more= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
//                            searchProdBeans.addAll(more);
//                        }
                        if (pageIndex==2){
                            setData();
                        }else{
                            searchprodAdapter.notifyDataSetChanged();
                        }*/
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 50);

                    }
                });
    }

    private void getSpecData() {
        if (pridId == 0) {

            return;
        }
        OkHttpUtils.get(AppConstants.GetSpec)
                .tag(this)
                .params("ProductID", pridId)//ProductID固定地方便调试，其他ID没数据
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        specBeans = gson.fromJson(s, SpecBean.class);

                        getCartCount();
                        /*ProductDetailBeans= gson.fromJson(jsonData, new TypeToken<List<ProductDetailBean>>() {}.getType());
                        commentBeans= gson.fromJson(comment, CommentBean.class);
                        data.add(commentBeans);
                        getShopData();
                        if (!jsonData.equals("[]")){
                            pageIndex++;
                        }
                        ToastUtil.showShort(context,Message);

//                        if(searchProdBeans==null||searchProdBeans.size()==0){
//                            searchProdBeans= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
//                        }else{
//                            List<SearchProdBean>  more= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
//                            searchProdBeans.addAll(more);
//                        }
                        if (pageIndex==2){
                            setData();
                        }else{
                            searchprodAdapter.notifyDataSetChanged();
                        }*/
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 50);

                    }
                });
    }

    private void getCartCount() {
        if (pridId == 0) {
            return;
        }
        OkHttpUtils.get(AppConstants.GetCartCount)
                .tag(this)
                .params("UserName", AppConstants.USERNAME)//ProductID固定地方便调试，其他ID没数据
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            CartCount = jsonObject.getInt("ReturnData");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 50);
                        }
                        if (isavedInstanceStateS) {//恢复Fragment时直接显示购物车数量
                            if (tv_count_cart_tips != null) {
                                if (CartCount != 0) {
                                    tv_count_cart_tips.setText(String.valueOf(CartCount));
                                    tv_count_cart_tips.setVisibility(View.GONE);
                                } else {
                                    tv_count_cart_tips.setVisibility(View.GONE);
                                }
                            }
                            vp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
                            psts_tabs = (PagerSlidingTabStrip) findViewById(R.id.psts_tabs);
                            psts_tabs.setTabPaddingLeftRight(-20);
                            tv_title = (TextView) findViewById(R.id.tv_title);
                            tv_collect_prod = (TextView) findViewById(R.id.tv_collect_prod);
                            tv_collect_prod.setOnClickListener(ProductDetailActivity.this);
                            tv_collect_prod.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (isPause) {
                                        return true;
                                    }
                                    if (System.currentTimeMillis() - mLastClickTime >= TIME_INTERVAL) {//避免重复点击
                                        mLastClickTime = System.currentTimeMillis();
                                        if (ProductDetailBeans.getIsCollect() == 0) {
                                            CollectProduct(pridId);
                                        } else {
                                            DeleteCollectProduct(pridId);
                                        }
                                    } else {

                                    }

                                    return false;
                                }
                            });
                            if (goodsInfoFragment == null) {
                                goodsInfoFragment = new GoodsInfoFragment();
                            }
                            Bundle bundle = new Bundle();
                            bundle.putInt("ID", pridId);
                            bundle.putParcelable("DetailBean", ProductDetailBeans);
//        bundle.putParcelable("comment",commentBeans);//商品中的全部评价是此activity实现GoodsInfoFragment回调接口，所以不传值
                            bundle.putParcelable("spec", specBeans);
                            goodsInfoFragment.setArguments(bundle);
                            goodsInfoFragment.setOnButtonClick(new GoodsInfoFragment.OnButtonClick() {
                                @Override
                                public void onAllComment(String comment) {
                                    if (isPause) {
                                        return;
                                    }
                                    vp_content.setCurrentItem(2);
                                }
                            });

                            if (goodsDetailFragment == null) {
                                goodsDetailFragment = new GoodsDetailFragment();
                            }
                            Bundle Detailbundle = new Bundle();
                            Detailbundle.putInt("ID", pridId);
                            goodsDetailFragment.setArguments(Detailbundle);

                            if (goodsCommentFragment == null) {
                                goodsCommentFragment = new GoodsCommentFragment();
                            }
                            Bundle Commentbundle = new Bundle();
                            Commentbundle.putInt("ID", pridId);
                            Commentbundle.putInt("ShopID", ProductDetailBeans.getShopID());
                            Commentbundle.putParcelable("comment", commentBeans);
                            goodsCommentFragment.setArguments(Commentbundle);
                            fragmentList.add(goodsInfoFragment);
                            fragmentList.add(goodsDetailFragment);
                            fragmentList.add(goodsCommentFragment);
                            pagerAdapternew = new ItemTitlePagerAdapter(getSupportFragmentManager(),
                                    fragmentList, new String[]{"商品", "详情", "评价"});
                            vp_content.setAdapter(pagerAdapternew);
                            vp_content.setOffscreenPageLimit(3);
                            psts_tabs.setViewPager(vp_content);
                            psts_tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {

                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });
                            if (ProductDetailBeans.getIsCollect() == 0) {
                                Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_unclick);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                                ProductDetailBeans.setIsCollect(0);
                            } else {
                                Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_click);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                                ProductDetailBeans.setIsCollect(1);
                            }
                            isavedInstanceStateS = false;
                        } else {//获取全部数据后显示界面
                            if (tv_count_cart_tips == null) {
                                init();//界面初始化
                            } else {
                                if (CartCount != 0) {
                                    tv_count_cart_tips.setText(String.valueOf(CartCount));
                                    tv_count_cart_tips.setVisibility(View.GONE);
                                } else {
                                    tv_count_cart_tips.setVisibility(View.GONE);
                                }
                            }
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
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 50);
                        }
                    }
                });
    }

    private void init() {
        coordinatorLayout = (RelativeLayout) findViewById(R.id.coordinatorLayout);
        masking = (LinearLayout) findViewById(R.id.masking);
        coordinatorLayout.setVisibility(View.VISIBLE);
        masking.setVisibility(View.GONE);
        im_setting = (ImageView) findViewById(R.id.im_setting);
        psts_tabs = (PagerSlidingTabStrip) findViewById(R.id.psts_tabs);
        psts_tabs.setTabPaddingLeftRight(-20);
        vp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add_cart = (TextView) findViewById(R.id.tv_add_cart);
        tv_add_cart.setOnClickListener(this);
        tv_submitorder = (TextView) findViewById(R.id.tv_submitorder);
        tv_submitorder.setOnClickListener(this);
        tv_count_cart_tips = (TextView) findViewById(R.id.tv_count_cart_tips);
        tv_shop_attention = (TextView) findViewById(R.id.tv_shop_attention);
        tv_shop_Customer = (TextView) findViewById(R.id.tv_shop_Customer);
        tv_shop_attention.setOnClickListener(this);
        tv_shop_Customer.setOnClickListener(this);
        tv_collect_prod = (TextView) findViewById(R.id.tv_collect_prod);
        tv_collect_prod.setOnClickListener(this);
        tv_collect_prod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (System.currentTimeMillis() - mLastClickTime >= TIME_INTERVAL) {//避免重复点击
                    mLastClickTime = System.currentTimeMillis();
                    if (ProductDetailBeans.getIsCollect() == 0) {
                        CollectProduct(pridId);
                    } else {
                        DeleteCollectProduct(pridId);
                    }
                } else {
                }
                return false;
            }
        });
        if (CartCount != 0) {
            tv_count_cart_tips.setText(String.valueOf(CartCount));
            tv_count_cart_tips.setVisibility(View.GONE);
        } else {
            tv_count_cart_tips.setVisibility(View.GONE);
        }
        rl_detail_cart = (RelativeLayout) findViewById(R.id.rl_detail_cart);
        rl_detail_cart.setOnClickListener(this);
        if (goodsInfoFragment == null) {
            goodsInfoFragment = new GoodsInfoFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("ID", pridId);
        bundle.putParcelable("DetailBean", ProductDetailBeans);
//        bundle.putParcelable("comment",commentBeans);//商品中的全部评价是此activity实现GoodsInfoFragment回调接口，所以不传值
        bundle.putParcelable("spec", specBeans);
        goodsInfoFragment.setArguments(bundle);
        goodsInfoFragment.setOnButtonClick(new GoodsInfoFragment.OnButtonClick() {
            @Override
            public void onAllComment(String comment) {
                if (isPause) {
                    return;
                }
                vp_content.setCurrentItem(2);
            }
        });

        if (goodsDetailFragment == null) {
            goodsDetailFragment = new GoodsDetailFragment();
        }
        Bundle Detailbundle = new Bundle();
        Detailbundle.putInt("ID", pridId);
        goodsDetailFragment.setArguments(Detailbundle);

        if (goodsCommentFragment == null) {
            goodsCommentFragment = new GoodsCommentFragment();
        }
        Bundle Commentbundle = new Bundle();
        Commentbundle.putInt("ID", pridId);
        Commentbundle.putInt("ShopID", ProductDetailBeans.getShopID());
        Commentbundle.putParcelable("comment", commentBeans);
        goodsCommentFragment.setArguments(Commentbundle);
        fragmentList.add(goodsInfoFragment);
        fragmentList.add(goodsDetailFragment);
        fragmentList.add(goodsCommentFragment);
        pagerAdapternew = new ItemTitlePagerAdapter(getSupportFragmentManager(),
                fragmentList, new String[]{"商品", "详情", "评价"});
        vp_content.setAdapter(pagerAdapternew);
        vp_content.setOffscreenPageLimit(3);
        psts_tabs.setViewPager(vp_content);
        if (ProductDetailBeans.getIsCollect() == 0) {
            Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_unclick);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
            ProductDetailBeans.setIsCollect(0);
        } else {
            Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_click);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
            ProductDetailBeans.setIsCollect(1);
        }

        View contentView = LayoutInflater.from(this).inflate(R.layout.menu_pop, null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setAnimationStyle(R.style.mypopwindow_anim_style)
                .create();
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                String showContent = "";
                switch (v.getId()) {
                    case R.id.menu1:
                        break;
                    case R.id.menu2: {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("ShoppingCartFragment", "");
                        intent.putExtra("specItemBeans", "");
                        intent.putExtra("HomeFragment", "HomeFragment");
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu3:
//                        showContent = "点击 Item菜单3";
                        break;
                    case R.id.menu4: {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("PersonalFragment", "PersonalFragment");
                        intent.putExtra("specItemBeans", "");
                        startActivity(intent);
                    }
                    break;

                }
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
    }

    //获取规格选取后的数据
    public void setParamList(ArrayList<SpecItemBean> CaceBeans, String count) {
        chooseSpecState = true;
        specCaceBeans.clear();
        speccount = "0";
        specCaceBeans = CaceBeans;
        speccount = count;
    }

    public String getSpecString() {
        if (specCaceBeans == null) {
            return null;
        } else {
            StringBuffer testStrBuff = new StringBuffer("已选:");
            for (SpecItemBean cs : specCaceBeans) {
                testStrBuff.append("“" + cs.getData().get(0).getSpecValue() + "”  ");
            }
            String spec = "";
            if (!speccount.equals("0")) {
                spec = testStrBuff.toString() + " ," + speccount + "件";
            } else {
                spec = speccount + "件";
            }
            return spec;
        }
    }

    private void CollectProduct(int shopId) {
        OkHttpUtils.post(AppConstants.CollectProduct)
                .tag(this)
                .params("ProductID", shopId)//固定地方便调试，其他ID没数据"26013"
                .params("UserID", AppConstants.USERID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();

                                }
                            }, 50);
                        }
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        boolean Flag = false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Flag = jsonObject.getBoolean("Flag");
                            Message = jsonObject.getString("Message");
                            commentObject = new JSONObject(jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag) {
                            Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_click);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                            ProductDetailBeans.setIsCollect(1);
                            ToastUtil.showShort(context, Message);
                        } else {
                            ToastUtil.showShort(context, Message);
                            Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_unclick);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                            ProductDetailBeans.setIsCollect(0);
                        }
                        //评论Data不是array
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
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();

                                }
                            }, 50);
                            ToastUtil.showShort(context, "数据加载超时");

                        }
                        Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_unclick);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                        ProductDetailBeans.setIsCollect(0);
                    }
                });
    }

    private void DeleteCollectProduct(int pridId) {
        OkHttpUtils.post(AppConstants.DeleteCollectFromProDetail)
                .tag(this)
                .params("ID", pridId)
                .params("UserID", AppConstants.USERID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 50);
                        }
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String DeleteMessage = null;
                        boolean Flag = false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Flag = jsonObject.getBoolean("Flag");
                            DeleteMessage = jsonObject.getString("Message");
                            commentObject = new JSONObject(jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag) {
                            Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_unclick);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                            ProductDetailBeans.setIsCollect(0);
                            ToastUtil.showShort(context, DeleteMessage);
                        } else {
                            Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_click);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                            ProductDetailBeans.setIsCollect(1);
                            ToastUtil.showShort(context, DeleteMessage);
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
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 50);
                            ToastUtil.showShort(context, "数据加载超时");

                        }
                        Drawable drawable = getResources().getDrawable(R.mipmap.prodetail_keep_click);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        tv_collect_prod.setCompoundDrawables(null, drawable, null, null);
                        ProductDetailBeans.setIsCollect(1);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {//选择规格之后回调
            case 1:
                if (resultCode == SpecOK) {
                    chooseSpecState = true;
                    specCaceBeans.clear();
                    speccount = "0";
                    specCaceBeans = data.getParcelableArrayListExtra("paramlist");
                    speccount = data.getStringExtra("paramNumber");

                } else if (resultCode == SpecAddCart) {
                    chooseSpecState = true;
                    specCaceBeans.clear();
                    speccount = "0";
                    specCaceBeans = data.getParcelableArrayListExtra("paramlist");
                    ;
                    speccount = data.getStringExtra("paramNumber");
                    if (data.getStringExtra("paramAddCart").equals("paramAddCart")) {//规格选择直接加入购物车
                        setAddCart();
                    }
                } else if (resultCode == SpecSubmitOrder) {
                    chooseSpecState = true;
                    specCaceBeans.clear();
                    speccount = "0";
                    specCaceBeans = data.getParcelableArrayListExtra("paramlist");
                    speccount = data.getStringExtra("paramNumber");
                    if (data.getStringExtra("paramSubmitOrder").equals("paramSubmitOrder")) {//规格选择直接加入购物车
                        setOrderData();
                    }
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (isPause) {
            return;
        }
        if (System.currentTimeMillis() - mLastClickTime >= TIME_INTERVAL) {//避免重复点击
            mLastClickTime = System.currentTimeMillis();
        } else {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_back: {
                finish();
            }
            break;
            case R.id.rl_menu: {
                if(im_setting!=null){
                    mCustomPopWindow.showAsDropDown(im_setting, 0, 0);
                }
            }
            break;
            case R.id.tv_shop_attention: {
                if (ProductDetailBeans != null) {
                    if (ProductDetailBeans.getShopID() == 0) {
                        return;
                    }
                } else {
                    return;
                }
                Intent intent = new Intent(context, ShopIndexBaseActivity.class);
                intent.putExtra("ShopID", ProductDetailBeans.getShopID());
                startActivity(intent);
            }
            break;
            case R.id.tv_shop_Customer: {//联系客服先登录
                if (AppConstants.USERID == 0) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    V5ClientAgent.getInstance().startV5ChatActivity(getApplicationContext());
                    /**
                     * 点击对话输入框底部功能按钮
                     */
                    V5ClientAgent.getInstance().setChatActivityFuncIconClickListener(new ChatActivityFuncIconClickListener() {

                        /**
                         * Activity点击底部功能按钮事件，icon参数值及含义如下：
                         * 		v5_icon_ques			//常见问题
                         * 		v5_icon_relative_ques	//相关问题
                         * 		v5_icon_photo			//图片
                         * 		v5_icon_camera			//拍照
                         * 		v5_icon_worker			//人工客服
                         * 返回值代表是否消费了此事件
                         * @param icon 点击的图标名称(对应SDK目录下res/values/v5_arrays中v5_chat_func_icon的值)
                         * @return boolean 是否消费事件(返回true则不响应默认点击效果，由此回调处理)
                         */
                        @Override
                        public boolean onChatActivityFuncIconClick(String icon) {
                            // TODO Auto-generated method stub
                            if (icon.equals("v5_icon_worker")&&!kefuid.equals("")) {
                                // 转到指定客服,参数：(组id, 客服id),参数为0则不指定客服组或者客服,获取组id请咨询客服
                                V5ClientAgent.getInstance().transferHumanService(Integer.valueOf(kefuid), 0);
                                // 返回true来拦截SDK内默认的实现
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }
            break;
            case R.id.tv_collect_prod: {
                if (System.currentTimeMillis() - mLastClickTime >= TIME_INTERVAL) {//避免重复点击
                    mLastClickTime = System.currentTimeMillis();
                    if (ProductDetailBeans.getIsCollect() == 0) {
                        CollectProduct(pridId);
                    } else {
                        DeleteCollectProduct(pridId);
                    }
                } else {
                    return;
                }
            }
            break;
            case R.id.tv_add_cart: {

                //加入购物车
                String loginData = SpUtils.getString(context, LoginKey, LoginFile);
                if (!loginData.equals("")) {
                    AppConstants.ISLOGIN = true;
                }
                if (!AppConstants.ISLOGIN) {//没有登录提示登录
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                } else {//已登录提示选择商品规格
//                    if (notesAddingCartState && chooseSpecState) {//已选规格直接跳生成订单
//                        setAddCart();
//                    } else {//未选规格则提示选择
                    if (specBeans != null) {
                        if (specBeans.getData() != null) {
                            if (specBeans.getData().size() != 0) {
                                Intent intent = new Intent(context, GoodsInfoSpecActivity.class);
                                intent.putExtra("ProductID", pridId);
                                intent.putExtra("price", ProductDetailBeans.getMemberPrice());
                                intent.putExtra("spec", specBeans);
                                startActivityForResult(intent, SpecOK);
                            } else {
                                speccount = "1";
                                setAddCart();
                            }
                        } else {
                            speccount = "1";
                            setAddCart();
                        }
                    } else {

                    }

//                    }
                }
            }

            break;
            case R.id.rl_detail_cart: {
                /*Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("ShoppingCartFragment", "ShoppingCartFragment");
                intent.putExtra("specItemBeans", "");
                startActivity(intent);*/
                if (AppConstants.USERID == 0) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ShoppingCartActivity.class);
//                intent.putExtra("ShoppingCartFragment", "ShoppingCartFragment");
//                intent.putExtra("specItemBeans", "");
                    startActivity(intent);
                }
            }
            break;
            case R.id.tv_submitorder: {
                String loginData = SpUtils.getString(context, LoginKey, LoginFile);
                if (!loginData.equals("")) {
                    AppConstants.ISLOGIN = true;
                }
                if (!AppConstants.ISLOGIN) {//没有登录提示登录
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    if (specBeans != null) {
                        if (specBeans.getData() != null) {
                            if (specBeans.getData().size() != 0) {
                                Intent intent = new Intent(context, GoodsInfoSpecActivity.class);
                                intent.putExtra("ProductID", pridId);
                                intent.putExtra("price", ProductDetailBeans.getMemberPrice());
                                intent.putExtra("spec", specBeans);
                                startActivityForResult(intent, SpecOK);
                            } else {
                                speccount = "1";
                                setOrderData();
                            }
                        } else {
                            speccount = "1";
                            setOrderData();
                        }
                    }
                }
                break;
            }
        }
    }

    private void setAddCart() {//生成加入购物车参数，并提交加入购物车
        StringBuffer paramStrBuff = new StringBuffer("");
        for (SpecItemBean cs : specCaceBeans) {
            paramStrBuff.append("" + cs.getData().get(0).getSpecValueID() + ",");

        }
        String param = "";
        if (!paramStrBuff.toString().equals("")) {
            param = paramStrBuff.toString().substring(0, paramStrBuff.toString().length() - 1);
        }
        OkHttpUtils.post(AppConstants.AddCart)
                .tag(this)
                .params("Count", speccount)
                .params("ProductID", pridId)
                .params("Param", param)
                .params("UserName", AppConstants.USERNAME)//固定地方便调试，其他ID没数据"26013"
//                .params("PageIndex","")
//                .params("PageSize","")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if (dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 50);
                        }
                        notesAddingCartState = false;
                        JSONObject jsonObject;
                        String jsonData = null;
                        String Message = null;
                        boolean Flag = false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag) {
                            ToastUtil.showShort(context, Message);
                            getCartCount();
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
                            }, 50);
                        }
                    }
                });
    }

    private void setOrderData() {//生成提交订单参数，并跳转生成订单界面
        StringBuffer paramStrBuff = new StringBuffer("");
        for (SpecItemBean cs : specCaceBeans) {
            paramStrBuff.append("" + cs.getData().get(0).getSpecValueID() + ",");
        }
        String param = "";
        if (!paramStrBuff.toString().equals("")) {
            param = paramStrBuff.toString().substring(0, paramStrBuff.toString().length() - 1);
        }

        Intent intent = new Intent(context, SubmitOrderActivity.class);
        intent.putExtra("ProductID", pridId);
        intent.putExtra("Param", param);
        intent.putExtra("ShopName", ProductDetailBeans.getShopName());
        intent.putExtra("Count", speccount);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (mCustomPopWindow != null) {
            mCustomPopWindow.dissmiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            JZVideoPlayerStandard jz=new JZVideoPlayerStandard(context);
            boolean aaa=jz.backPress();
            if (aaa) {
                JZVideoPlayer.backPress();
                Log.i("TAG",""+"关闭全屏");
                return true;
            }
            if (mCustomPopWindow != null) {
                mCustomPopWindow.dissmiss();
            }
                finish();

        }
        return false;
    }

    @Override
    public void onChatActivityCreate(ClientChatActivity clientChatActivity) {

    }

    @Override
    public void onChatActivityStart(ClientChatActivity clientChatActivity) {

    }

    @Override
    public void onChatActivityStop(ClientChatActivity clientChatActivity) {

    }

    @Override
    public void onChatActivityDestroy(ClientChatActivity clientChatActivity) {

    }

    @Override
    public void onChatActivityConnect(ClientChatActivity clientChatActivity) {
// 【转】指定人工客服（调用时立即转），参数: 客服组id,客服id （以下数字仅作为示例，具体ID请前往V5后台查看客服信息）
        V5ClientAgent.getInstance().transferHumanService(Integer.valueOf(kefuid), 0);
//        V5ClientAgent.getInstance().sendMessage(new V5ControlMessage(4, 2, +Integer.valueOf(kefuid)+" 0"), null);
        // 【指定人工客服】点击转人工按钮或者问题触发转人工时会转到指定人工，参数"0 132916"中两个数字先后对应需要转的客服组ID和客服ID
        //V5ClientAgent.getInstance().sendMessage(new V5ControlMessage(4, 2, "0 114052"), null);

        // 发送图文消息
//		V5ArticlesMessage articleMsg = new V5ArticlesMessage();
//		V5ArticleBean article = new V5ArticleBean(
//				"V5KF",
//				"http://rs.v5kf.com/upload/10000/14568171024.png",
//				"http://www.v5kf.com/public/weixin/page.html?site_id=10000&id=218833&uid=3657455033351629359",
//				"V5KF是围绕核心技术“V5智能机器人”研发的高品质在线客服系统。可以运用到各种领域，目前的主要产品有：微信智能云平台、网页智能客服系统...");
//		ArrayList<V5ArticleBean> articlesList = new ArrayList<V5ArticleBean>();
//		articlesList.add(article);
//		articleMsg.setArticles(articlesList);
//		V5ClientAgent.getInstance().sendMessage(articleMsg, null);
    }

    @Override
    public void onChatActivityReceiveMessage(ClientChatActivity clientChatActivity, V5Message v5Message) {

    }

    @Override
    public void onChatActivityServingStatusChange(ClientChatActivity clientChatActivity, V5ClientAgent.ClientServingStatus clientServingStatus) {

        // TODO Auto-generated method stub
        switch (clientServingStatus) {
            case clientServingStatusRobot:
            case clientServingStatusQueue:
                clientChatActivity.setChatTitle("机器人服务中");
                break;
            case clientServingStatusWorker:
                clientChatActivity.setChatTitle(V5ClientConfig.getInstance(getApplicationContext()).getWorkerName() + "为您服务");
                break;
            case clientServingStatusInTrust:
                clientChatActivity.setChatTitle("机器人托管中");
                break;
        }
    }

    //创建一个类集成JZVideoPlayerStandard 并在XML设置
     public class JZVideoPlayerStandardVolumeAfterFullscreen extends JZVideoPlayerStandard {
        public JZVideoPlayerStandardVolumeAfterFullscreen(Context context) {
            super(context);
        } public JZVideoPlayerStandardVolumeAfterFullscreen(Context context, AttributeSet attrs) {
            super(context, attrs); } @Override public void onPrepared() {
            super.onPrepared();
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                JZMediaManager.instance().jzMediaInterface.setVolume(1f, 1f);
            } else { JZMediaManager.instance().jzMediaInterface.setVolume(0f, 0f);
            }
        }


}
    public boolean isPause(){
        return isPause;
    }

    // 实现GoodsInfoFragment回调
    @Override
    public void process(String paramString, String Propertys, String paramNumber, ArrayList<SpecItemBean> paramlist) {
        chooseSpecState=true;
        specCaceBeans.clear();
        speccount="0";
        specCaceBeans=paramlist;
        speccount=paramNumber;
            setOrderData();
    }

    @Override
    public void addcartprocess(String paramString, String Propertys, String paramNumber, ArrayList<SpecItemBean> paramlist) {
        chooseSpecState=true;
        specCaceBeans.clear();
        speccount="0";
        specCaceBeans=paramlist;
        speccount=paramNumber;
            setAddCart();
    }
}
