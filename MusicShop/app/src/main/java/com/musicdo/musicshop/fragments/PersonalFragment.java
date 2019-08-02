package com.musicdo.musicshop.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mylibrary.PtrClassicFrameLayout;
import com.example.mylibrary.PtrDefaultHandler;
import com.example.mylibrary.PtrFrameLayout;
import com.example.mylibrary.PtrHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AddressManagerActivity;
import com.musicdo.musicshop.activity.AllToolsActivity;
import com.musicdo.musicshop.activity.ImageShowActivity;
import com.musicdo.musicshop.activity.LoginActivity;
import com.musicdo.musicshop.activity.MyCommentActivity;
import com.musicdo.musicshop.activity.MyOrderListActivity;
import com.musicdo.musicshop.activity.MyShareListActivity;
import com.musicdo.musicshop.activity.OrderActivity;
import com.musicdo.musicshop.activity.PersinalCenterActivity;
import com.musicdo.musicshop.activity.PersonalReturnsActivity;
import com.musicdo.musicshop.activity.PersonalSettingActivity;
import com.musicdo.musicshop.activity.ProductCollectListActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.ProductLookActivity;
import com.musicdo.musicshop.activity.ShopCollectListActivity;
import com.musicdo.musicshop.adapter.HomeSecondItemRecylistAdapter;
import com.musicdo.musicshop.adapter.ItemEducationAdapter;
import com.musicdo.musicshop.adapter.MyRecyclerAdapter;
import com.musicdo.musicshop.adapter.PersonalAdapter;
import com.musicdo.musicshop.bean.HomeSecondBean;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.bean.PersonalRecommend;
import com.musicdo.musicshop.bean.QueryOrderNum;
import com.musicdo.musicshop.bean.UserInfoBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CircleTransform;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.PersonalScrollView;
import com.musicdo.musicshop.view.RefreshCompleteCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by A on 2017/7/12.
 */

public class PersonalFragment extends BaseFragment implements PersonalScrollView.OnScrollListener,View.OnClickListener,RefreshCompleteCallBack {
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private final static String TAG = HomeFragment.class.getSimpleName();
    RecyclerView rc_grid_education,function_recyclerview;
    private TextView textView,tv_all_tools;
    private ArrayList<PersonalRecommend> homeSecondBeans=new ArrayList<>();
    HomeSecondItemRecylistAdapter RecylistAdapter;
    ItemEducationAdapter itemEducationAdapter;
    PtrClassicFrameLayout mPtrFrame;
    ArrayList<MusicalBean> meizis=new ArrayList<>();
    int page=1;
    int[] function_ItemIcon={R.mipmap.personal_favorites,R.mipmap.personal_attention,R.mipmap.personal_footprints,R.mipmap.personal_share,R.mipmap.personal_group,
            R.mipmap.personal_comment,R.mipmap.personal_address,R.mipmap.personal_service};
    String[] function_ItemTitle={"收藏夹","店铺关注","我的足迹","我的分享","我的圈子","我的评论","我的地址","联系客服",};
    private List<Drawable> functionDatas;
    private List<String> functionDataTitle;
//    MyRecyclerAdapter function_recycleAdapter;
    PersonalAdapter personalAdapter;
    private GridLayoutManager function_gridLayoutManager;
    private LinearLayout rl_personal_top,ll_persinal_center;
    private PersonalScrollView sv_personal;
    private TextView tv_personal_title,tv_personal_unpaid,tv_personal_nodelivery,
            tv_personal_personal_noreceipt,tv_personal_personal_noevaluation,tv_personal_returned,
            tv_allorder,tv_persinal_name,tv_persinal_nickname,tv_personal_recommend;
    private TextView tv_personal_unpaid_tips,tv_personal_nodelivery_tips,tv_personal_personal_noreceipt_tips,tv_personal_personal_noevaluation_tips,tv_personal_returned_tips;
    private ImageView iv_psrsonal_setting;
    private UserInfoBean userInfoBean;
    private SimpleDraweeView  im_persinal_icon;
    LoadingDialog dialog;

    QueryOrderNum queryOrderNum=new QueryOrderNum();
    @Override
    public View initView() {
        Fresco.initialize(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_personal_layout, null);
        dialog = new LoadingDialog(getContext(),R.style.LoadingDialog);
            mPtrFrame=(PtrClassicFrameLayout) view.findViewById(R.id.grid_swipe_refresh);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (AppConstants.USERID==0){
                    mPtrFrame.refreshComplete();
                    tv_persinal_name.setText("未登录");
                    tv_persinal_nickname.setText("");
                    im_persinal_icon.setActualImageResource(R.mipmap.ic_launcher);
                }else{
                    if (homeSecondBeans==null){
                        doSth();
                    }else if (homeSecondBeans.size()==0){
                        doSth();
                    }
                    getUserInfo();
                    getQueryOrderNum();
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
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
        sv_personal=(PersonalScrollView)view.findViewById(R.id.sv_home_layout);
        sv_personal.setScrolListener(this);
        rl_personal_top=(LinearLayout)view.findViewById(R.id.rl_personal_top);
            ll_persinal_center=(LinearLayout)view.findViewById(R.id.ll_persinal_center);
            rc_grid_education = (RecyclerView) view.findViewById(R.id.rc_grid_education );
            rl_personal_top.getBackground().mutate().setAlpha(0);
        tv_all_tools = (TextView) view.findViewById(R.id.tv_all_tools );
        tv_all_tools.setOnClickListener(this);
            tv_personal_title = (TextView) view.findViewById(R.id.tv_personal_title );
            tv_persinal_name = (TextView) view.findViewById(R.id.tv_persinal_name );
            tv_persinal_nickname = (TextView) view.findViewById(R.id.tv_persinal_nickname );
            tv_personal_recommend = (TextView) view.findViewById(R.id.tv_personal_recommend );
            tv_personal_unpaid = (TextView) view.findViewById(R.id.tv_personal_unpaid );
            tv_personal_unpaid.setOnClickListener(this);
            ll_persinal_center.setOnClickListener(this);
            tv_personal_nodelivery = (TextView) view.findViewById(R.id.tv_personal_nodelivery );
            tv_personal_nodelivery.setOnClickListener(this);
            tv_personal_personal_noreceipt = (TextView) view.findViewById(R.id.tv_personal_personal_noreceipt );
            tv_personal_personal_noreceipt.setOnClickListener(this);
            tv_personal_personal_noevaluation = (TextView) view.findViewById(R.id.tv_personal_personal_noevaluation );
            tv_personal_personal_noevaluation.setOnClickListener(this);
            tv_personal_returned = (TextView) view.findViewById(R.id.tv_personal_returned );
            tv_personal_returned.setOnClickListener(this);
            //不同订单状态下的订单数量
            tv_personal_unpaid_tips = (TextView) view.findViewById(R.id.tv_personal_unpaid_tips );
            tv_personal_nodelivery_tips = (TextView) view.findViewById(R.id.tv_personal_nodelivery_tips );
            tv_personal_personal_noreceipt_tips = (TextView) view.findViewById(R.id.tv_personal_personal_noreceipt_tips );
            tv_personal_personal_noevaluation_tips = (TextView) view.findViewById(R.id.tv_personal_personal_noevaluation_tips );
            tv_personal_returned_tips = (TextView) view.findViewById(R.id.tv_personal_returned_tips );

        tv_allorder = (TextView) view.findViewById(R.id.tv_allorder );
            tv_allorder.setOnClickListener(this);
            iv_psrsonal_setting = (ImageView) view.findViewById(R.id.iv_psrsonal_setting );
            im_persinal_icon = (SimpleDraweeView ) view.findViewById(R.id.im_persinal_icon );
            iv_psrsonal_setting.setOnClickListener(this);
            im_persinal_icon.setOnClickListener(this);
            tv_personal_title.setTextColor(Color.argb(00, 255,255,255));
            GridLayoutManager grid_education_layoutManage = new GridLayoutManager(mContext,2);
            rc_grid_education.setLayoutManager(grid_education_layoutManage);

            makeFunctionData();
            function_recyclerview = (RecyclerView) view.findViewById(R.id.rv_home_function_list);
            personalAdapter= new PersonalAdapter(mContext , functionDatas,functionDataTitle);
            personalAdapter.setOnItemClickListener(new PersonalAdapter.OnItemClickListener(){
                @Override
                public void onItemClick(View view , int position){
                    switch (position){
                        case 0:{//收藏夹
                            if (AppConstants.USERID==0){
                                return;
                            }
                            Intent intent=new Intent(mContext,ProductCollectListActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 1:{//店铺关注
                            if (AppConstants.USERID==0){
                                return;
                            }
                            Intent intent=new Intent(mContext,ShopCollectListActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 2:{//足迹
                            if (AppConstants.USERID==0){
                                return;
                            }
                            Intent intent=new Intent(mContext,ProductLookActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 3:{//分享
                            if (!AppConstants.ISLOGIN) {//没有登录提示登录
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent=new Intent(mContext,MyShareListActivity.class);
                                startActivity(intent);
                            }
                            }
                        break;
                        case 4:{//圈子

                        }
                        break;
                        case 5:{//我的评论
                            if (AppConstants.USERID==0){
                                return;
                            }
                            Intent intent=new Intent(mContext,MyCommentActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 6:{//地址
                            Intent intent=new Intent(mContext,AddressManagerActivity.class);
                            intent.putExtra("PersinalCenterActivity","PersonalFragment");
                            startActivity(intent);
                        }
                        break;
                        case 7:{//联系客服

                        }
                        break;
                    }
                }
            });
            function_gridLayoutManager=new GridLayoutManager(mContext,4,GridLayoutManager.VERTICAL,false);//设置为一个3列的纵向网格布局
            function_recyclerview.setLayoutManager(function_gridLayoutManager);
            function_recyclerview.setAdapter(personalAdapter);
//
            return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(AppConstants.ScreenWidth==0||AppConstants.ScreenWidth==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(mContext);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(mContext);
        }
        if (isVisibleToUser) {
            if (!AppConstants.ISLOGIN){
//            mPtrFrame.refreshComplete();
                tv_persinal_name.setText("未登录");
                tv_persinal_nickname.setText("");
                im_persinal_icon.setActualImageResource(R.mipmap.ic_launcher);
            }else{
                if (homeSecondBeans==null){
                    doSth();
                }else if (homeSecondBeans.size()==0){
                    doSth();
                }
                getUserInfo();
                getQueryOrderNum();
            }
        } else {
            //相当于Fragment的onPause
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AppConstants.ISLOGIN){
//            mPtrFrame.refreshComplete();
            tv_persinal_name.setText("未登录");
            tv_persinal_nickname.setText("");
            im_persinal_icon.setActualImageResource(R.mipmap.ic_launcher);
        }else{
            if (homeSecondBeans==null){
                doSth();
            }else if (homeSecondBeans.size()==0){
                doSth();
            }
            getUserInfo();
            getQueryOrderNum();
        }
    }

    @Override
    public void refreshComplete() {
        mPtrFrame.refreshComplete();
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
                        mPtrFrame.refreshComplete();
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        boolean flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            flag = jsonObject.getBoolean("Flag");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (flag) {
                            userInfoBean = gson.fromJson(jsonData, UserInfoBean.class);
                            tv_persinal_name.setText(userInfoBean.getName());
                            tv_persinal_nickname.setText(userInfoBean.getNickName());
                            if (!userInfoBean.getImageUrl().equals("")) {
                                im_persinal_icon.setImageURI(userInfoBean.getImageUrl());
                                AppConstants.USERICON=userInfoBean.getImageUrl();//当次打开保存用户头像
                            }else{
                                im_persinal_icon.setActualImageResource(R.mipmap.ic_launcher);
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Log.i(TAG, "onBefore: 开始刷新");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPtrFrame.refreshComplete();
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });
    }
    /**
     * 获取不同状态下订单数量小圆点
     */
    private void getQueryOrderNum() {
        OkHttpUtils.post(AppConstants.QueryOrderNum)
                .params("UserName",AppConstants.USERNAME)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        boolean flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            flag = jsonObject.getBoolean("Flag");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!flag){
                            return;
                        }
                            queryOrderNum = gson.fromJson(s, QueryOrderNum.class);
                            System.out.print(queryOrderNum);
                        if (queryOrderNum!=null){
                            if (queryOrderNum.getDaifukuan()==0){
                                tv_personal_unpaid_tips.setVisibility(View.GONE);
                            }else{
                                tv_personal_unpaid_tips.setText(String.valueOf(queryOrderNum.getDaifukuan()));
                            }
                            if (queryOrderNum.getDaifahuo()==0){
                                tv_personal_nodelivery_tips.setVisibility(View.GONE);
                            }else{
                                tv_personal_nodelivery_tips.setText(String.valueOf(queryOrderNum.getDaifahuo()));
                            }
                            if (queryOrderNum.getDaishouhuo()==0){
                                tv_personal_personal_noreceipt_tips.setVisibility(View.GONE);
                            }else{
                                tv_personal_personal_noreceipt_tips.setText(String.valueOf(queryOrderNum.getDaishouhuo()));
                            }
                            if (queryOrderNum.getDaipingjia()==0){
                                tv_personal_personal_noevaluation_tips.setVisibility(View.GONE);
                            }else{
                                tv_personal_personal_noevaluation_tips.setText(String.valueOf(queryOrderNum.getDaipingjia()));
                            }
                            if (queryOrderNum.getTuihuanhuo()==0){
                                tv_personal_returned_tips.setVisibility(View.GONE);
                            }else{
                                tv_personal_returned_tips.setText(String.valueOf(queryOrderNum.getTuihuanhuo()));
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Log.i(TAG, "onBefore: 开始刷新");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });
    }

    @Override
    public void initData() {
        super.initData();
        Log.e(TAG, "搜索面的Fragment的数据被初始化了");
    }

    /**
     * demo
     */
    private void doSth() {
        OkHttpUtils.post(AppConstants.GetProductRecommend)
                .params("UserName",AppConstants.USERNAME)
                .params("UserID",AppConstants.USERID)
                .params("Top",12)
                .tag(this)
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
                        mPtrFrame.refreshComplete();
                        page++;
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(meizis==null||meizis.size()==0){
                            homeSecondBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<PersonalRecommend>>() {}.getType());
//                            meizis= gson.fromJson(jsonData, new TypeToken<ArrayList<MusicalBean>>() {}.getType());


                        }else{
//                            ArrayList<MusicalBean>  more= gson.fromJson(jsonData, new TypeToken<ArrayList<MusicalBean>>() {}.getType());
//                            meizis.addAll(more);
                        }
                        setData();
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
                        mPtrFrame.refreshComplete();
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(mContext,"数据加载超时");
                    }
                });
    }



    private void setData() {
        if(itemEducationAdapter==null){
            if(homeSecondBeans!=null){
                if (homeSecondBeans.size()!=0){
                    tv_personal_recommend.setVisibility(View.VISIBLE);
                    itemEducationAdapter = new ItemEducationAdapter(mContext,homeSecondBeans);
                    rc_grid_education.setAdapter(itemEducationAdapter);//recyclerview设置适配器
                    itemEducationAdapter.setOnItemClickListener(new ItemEducationAdapter.OnSearchItemClickListener(){
                        @Override
                        public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(mContext,ProductDetailActivity.class);
                            intent.putExtra("prod_id",homeSecondBeans.get(position).getID());
                            startActivity(intent);
                        }
                    });
                    rc_grid_education.setNestedScrollingEnabled(false);
                }else{
                    tv_personal_recommend.setVisibility(View.GONE);
                }
            }else{
                tv_personal_recommend.setVisibility(View.GONE);
            }
        }else{
            //让适配器刷新数据
            itemEducationAdapter.notifyDataSetChanged();
        }

    }

    private void makeFunctionData() {
        functionDatas=new ArrayList<>();
        for ( int i=0; i < function_ItemIcon.length; i++) {
            functionDatas.add(getResources().getDrawable(function_ItemIcon[i]));
        }
        functionDataTitle=new ArrayList<>();
        for ( int i=0; i < 8; i++) {
            functionDataTitle.add(function_ItemTitle[i]);
        }
    }

    @Override
    public void onScroll(int scrollY) {
        if(scrollY < 100){
            rl_personal_top.getBackground().setAlpha(0);
            tv_personal_title.setTextColor(Color.argb(00, 255,255,255));
        }else if(scrollY >= 100 && scrollY < 860){
            rl_personal_top.getBackground().setAlpha((scrollY-100)/3);
            tv_personal_title.setTextColor(Color.argb((scrollY-100)/3,255,255,255));
        }else{
            rl_personal_top.getBackground().setAlpha(255);
            tv_personal_title.setTextColor(Color.argb(0xff, 255,255,255));
        }
    }

    @Override
    public void onClick(View v) {
        if (!AppConstants.ISLOGIN){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            return;
        }
        switch (v.getId()){
            case R.id.tv_personal_unpaid:{
                Intent intent=new Intent(mContext, MyOrderListActivity.class);
                intent.putExtra("TabIndex","0");
                startActivity(intent);
            }
                break;
            case R.id.tv_all_tools:{//全部必备工具
                Intent intent=new Intent(mContext, AllToolsActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.ll_persinal_center:{
                Intent intent=new Intent(mContext, PersinalCenterActivity.class);
                UserInfoBean uf=userInfoBean;
                intent.putExtra("UserInfoBean",uf);
                startActivity(intent);

            }
                break;
            case R.id.tv_personal_nodelivery:{
                Intent intent=new Intent(mContext, MyOrderListActivity.class);
                intent.putExtra("TabIndex","1");
                startActivity(intent);
            }
                break;
            case R.id.tv_personal_personal_noreceipt:{
                Intent intent=new Intent(mContext, MyOrderListActivity.class);
                intent.putExtra("TabIndex","2");
                startActivity(intent);
            }
                break;
            case R.id.tv_personal_personal_noevaluation:{
                Intent intent=new Intent(mContext, MyOrderListActivity.class);
                intent.putExtra("TabIndex","3");
                startActivity(intent);
            }
                break;
            case R.id.tv_personal_returned:{
                Intent personal_returned=new Intent(mContext,PersonalReturnsActivity.class);
                startActivity(personal_returned);
            }
                break;
            case R.id.tv_allorder:{
                Intent intent=new Intent(mContext, MyOrderListActivity.class);
                intent.putExtra("TabIndex","");
                startActivity(intent);
            }
                break;
            case R.id.iv_psrsonal_setting:{
                Intent intent=new Intent(mContext, PersonalSettingActivity.class);
                intent.putExtra("TabIndex",0);
                startActivity(intent);
            }
                break;
            case R.id.im_persinal_icon:{
                if(userInfoBean.getImageUrl()!=null&&!userInfoBean.getImageUrl().equals("")){
                Intent intent=new Intent(mContext, ImageShowActivity.class);
                intent.putExtra("PersonalFragment","PersonalFragment");
                    ArrayList<String> imgUrls= new ArrayList<String>();
                    imgUrls.add(userInfoBean.getImageUrl());
                intent.putStringArrayListExtra("imgUrls",  imgUrls);
                startActivity(intent);
                }else{
                    Intent intent=new Intent(mContext, PersinalCenterActivity.class);
                    UserInfoBean uf=userInfoBean;
                    intent.putExtra("UserInfoBean",uf);
                    startActivity(intent);
                }
            }
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
