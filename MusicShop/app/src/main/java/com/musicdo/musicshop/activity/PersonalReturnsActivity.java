package com.musicdo.musicshop.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.AllOrderExpandableListViewAdapter;
import com.musicdo.musicshop.adapter.OrderUnpaidAdapter;
import com.musicdo.musicshop.adapter.PersonalReturnsListViewAdapter;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.PersonalReturnsBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.MySwipeRefreshLayout;
import com.musicdo.musicshop.view.OptionsPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/10/16.
 * 退换货
 */

public class PersonalReturnsActivity extends BaseActivity implements View.OnClickListener
        {

    private Context context;
    private final static String TAG = "OrderAllFragment";
    OrderUnpaidAdapter orderUnpaidAdapter;
    int page=3;
    private MySwipeRefreshLayout swipeRefreshLayout;
    List<Meizi> meizis=new ArrayList<>();
    private ArrayList<String> options1Items = new ArrayList<>();
    List<AllOrderBean> allOrderBeans=new ArrayList<>();
    List<PersonalReturnsBean> personalReturnsBeans=new ArrayList<>();
    private TextView tv_title,tv_empty;
    private LinearLayout ll_back;
    private RecyclerView rc_searchprod_overall;
    GridLayoutManager grid_tend_layoutManage;
    LoadingDialog dialog;
    private int lastVisibleItem;
    private int PageIndex=1;
    private int pageSize=5;
    LinearLayoutManager ms;
    private PersonalReturnsListViewAdapter selva;
    private int totalCount=0;
    OptionsPickerView pvOptions;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_returns);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
        setListener();
    }

    private void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allOrderBeans.clear();
                PageIndex=1;
                selva=null;
                doSth();
            }
        });
    }

    @Override
    public void onResume() {
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

        doSth();
    }

    private void initView() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        options1Items.add("我不想买了");
        options1Items.add("信息填写错误,重新拍");
        options1Items.add("卖家缺货");
        options1Items.add("其他原因");
        swipeRefreshLayout=(MySwipeRefreshLayout)  findViewById(R.id.grid_swipe_refresh) ;
//            swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setRefreshing(true);
        rc_searchprod_overall = (RecyclerView)  findViewById(R.id.rc_searchprod_overall);
        grid_tend_layoutManage= new GridLayoutManager(context, 1);
        rc_searchprod_overall.setLayoutManager(grid_tend_layoutManage);
        ll_back = (LinearLayout)  findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title = (TextView)  findViewById(R.id.tv_title);
        tv_title.setText("退/换货");
        tv_empty = (TextView)  findViewById(R.id.tv_empty);
//            ms= new LinearLayoutManager(context);
//            rc_order_unpaid.setLayoutManager(ms);
        rc_searchprod_overall.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (selva!=null){
                    if(personalReturnsBeans.size()!=0){
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == selva.getItemCount()) {
                            doSth();
                        }
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = grid_tend_layoutManage.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * demo
     */
    private void doSth() {
        OkHttpUtils.post(AppConstants.GetRefundList)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
//                .params("StatusID", StatusID)//订单编号
//                .params("Name", orderName)//搜索名称
                .params("PageIndex", PageIndex)//页数
                .params("pageSize", pageSize)//搜索名称
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
                                 if (swipeRefreshLayout.isRefreshing()){
                                     swipeRefreshLayout.setRefreshing(false);
                                 }
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
                                     if (PageIndex==1){
                                         rc_searchprod_overall.setVisibility(View.GONE);
                                         tv_empty.setVisibility(View.VISIBLE);
                                         tv_empty.setText("暂无数据");
                                     }
                                     return;
                                 } else {
                                     rc_searchprod_overall.setVisibility(View.VISIBLE);
                                     tv_empty.setVisibility(View.GONE);
                                     if(jsonData!=null) {
                                         if (!jsonData.equals("[]")) {
                                             PageIndex++;
                                         } else {
                                             return;
                                         }
                                     }
                                 }
                                 if (personalReturnsBeans!=null){
                                     if (PageIndex<=2){
//                                meizis= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                                         personalReturnsBeans= gs.fromJson(jsonData, new TypeToken<List<PersonalReturnsBean>>() {}.getType());
                                         setData();
                                     }else{
//                                List<Meizi>  more= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                                         List<PersonalReturnsBean> more= gs.fromJson(jsonData, new TypeToken<List<PersonalReturnsBean>>() {}.getType());
                                         personalReturnsBeans.addAll(more);
                                         setData();
                                     }
                                 }

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
                                 ToastUtil.showShort(context, "数据加载超时");
                                 if(dialog.isShowing()){
                                     new Handler().postDelayed(new Runnable(){
                                         public void run() {
                                             dialog.dismiss();
                                         }
                                     }, 1000);
                                 }
                                 if (swipeRefreshLayout.isRefreshing()){
                                     swipeRefreshLayout.setRefreshing(false);
                                 }
                             }

                         }
                );

        if(dialog.isShowing()){
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);
        }
    }

    private void setData() {
        totalCount=0;
        if(selva==null){
            selva = new PersonalReturnsListViewAdapter(context, (ArrayList<PersonalReturnsBean>) personalReturnsBeans,false);
            rc_searchprod_overall.setAdapter(selva);
        }else{

            selva.notifyDataSetChanged();//
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
