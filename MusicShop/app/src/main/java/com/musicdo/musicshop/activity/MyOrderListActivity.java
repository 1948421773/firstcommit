package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylibrary.PtrClassicFrameLayout;
import com.example.mylibrary.PtrDefaultHandler;
import com.example.mylibrary.PtrFrameLayout;
import com.example.mylibrary.PtrHandler;
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
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.OrderDetailBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.NestedExpandaleListView;
import com.musicdo.musicshop.view.NestedPageExpandaleListView;
import com.musicdo.musicshop.view.OptionsPickerView;
import com.musicdo.musicshop.view.OrderDeliverGoodsWarningDialog;
import com.musicdo.musicshop.view.OrderLongTimeDeliverDialog;
import com.musicdo.musicshop.view.RefreshCompleteCallBack;
import com.musicdo.musicshop.view.SExpandableListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**我的订单 没有ViewPage
 * Created by Yuedu on 2017/10/12.
 */

public class MyOrderListActivity extends BaseActivity implements View.OnClickListener,
        AllOrderExpandableListViewAdapter.DeleteInterface,
        AllOrderExpandableListViewAdapter.DeliverGoodsWarning,
        AllOrderExpandableListViewAdapter.LongTimeDeliver,
        AllOrderExpandableListViewAdapter.TackingLogistics,
        AllOrderExpandableListViewAdapter.TackingLogistics_tow,
        AllOrderExpandableListViewAdapter.DeleteOrder,
        AllOrderExpandableListViewAdapter.ConfirmReceipt,
        AllOrderExpandableListViewAdapter.ApplyforRefund,
        AllOrderExpandableListViewAdapter.AdditionalComments,
        AllOrderExpandableListViewAdapter.GotoEvaluate,
        RefreshCompleteCallBack,NestedPageExpandaleListView.OnPageLoadListener {
    private final static String TAG = "OrderAllFragment";
    OrderUnpaidAdapter orderUnpaidAdapter;
    int page=3;
    private int loadCount;
    private boolean isPull;
    //    private MySwipeRefreshLayout swipeRefreshLayout;
//    PtrClassicFrameLayout mPtrFrame;
    List<Meizi> meizis=new ArrayList<>();
    private ArrayList<String> options1Items = new ArrayList<>();
    List<AllOrderBean> allOrderBeans=new ArrayList<>();
    private TextView tv_title,tv_empty;
    private LinearLayout ll_back;
    List<AllOrderBean> myallOrderBeans=new ArrayList<>();
    private SExpandableListView el_allorder;
    //    RecyclerView rc_order_unpaid;
    LoadingDialog dialog;
    private int lastVisibleItem;
    private int PageIndex=1;
    LinearLayoutManager ms;
    private AllOrderExpandableListViewAdapter selva;
    private int totalCount=0;
    OptionsPickerView pvOptions;
    private Context context;
    RadioGroup rg_searchtab;
    String StatusID;
    private boolean isEnd;
    String loginData="";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isEnd) {
                if (el_allorder!=null) {
                    el_allorder.refreshComplete();
                    el_allorder.setNoMore(true);
                }
            } else {
                addLoadMoreData();
                el_allorder.setNoMore(false);
                if (isPull) {
                    if (el_allorder!=null) {
                        el_allorder.refreshComplete();
                    }
                }
                if (selva!=null){
                    selva.notifyDataSetChanged();//
                }
//                expanedAll();
            }
        }
    };

        @Override
        protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorderlist);
            MyApplication.getInstance().addActivity(this);
            context=this;
            StatusID=getIntent().getStringExtra("TabIndex");
            initView();
            initDate();        
    }

    private void initDate() {
    }
    /**
     * 模拟加载更多数据
     */
    private void addLoadMoreData() {
        doSth();
    }

    private void initView() {
        rg_searchtab=(RadioGroup) findViewById(R.id.rg_searchtab);
        el_allorder = (SExpandableListView) findViewById(R.id.el_allorder);
        if (StatusID==null){//微信支付界面未支付直接返回导致StatusID为空的处理
            StatusID="0";
        }
        if (StatusID.equals("")){
            rg_searchtab.check(R.id.rb_all_order);
        }else if (StatusID.equals("0")){
            rg_searchtab.check(R.id.rb_unpay);
        }else if (StatusID.equals("1")){
            rg_searchtab.check(R.id.rb_shipment_pending);
        }else if (StatusID.equals("2")){
            rg_searchtab.check(R.id.rb_unreceived);
        }else if (StatusID.equals("3")){
            rg_searchtab.check(R.id.rb_pending_evaluation);
        }
        rg_searchtab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int j) {
                el_allorder.setVisibility(View.GONE);
                if (selva!=null){
                for (int i = 0; i < selva.getGroupCount(); i++) {
                    if (el_allorder.isGroupExpanded(i)) {
                        el_allorder.collapseGroup(i);
                    }
                }
                    selva.notifyDataSetChanged();
                }
                el_allorder.requestFocusFromTouch();
                el_allorder.setSelection(0);
                isEnd = false;
                el_allorder.setNoMore(false);
                switch (j) {
                    case R.id.rb_all_order:
                        if (!StatusID.equals("")){
                            if(selva!=null) {
                                allOrderBeans.clear();
                                selva.notifyDataSetChanged();
                            }
                        }
                        StatusID = "";
                        rg_searchtab.check(R.id.rb_all_order);
                        break;
                    case R.id.rb_unpay: //销量
                        if (!StatusID.equals("0")){
                            if(selva!=null) {
                                allOrderBeans.clear();
                                selva.notifyDataSetChanged();
                            }
                        }
                        StatusID = "0";
                        rg_searchtab.check(R.id.rb_unpay);
                        break;
                    case R.id.rb_shipment_pending: //价格
                        if (!StatusID.equals("1")){
                            if(selva!=null) {
                                allOrderBeans.clear();
                                selva.notifyDataSetChanged();
                            }
                        }
                        StatusID = "1";
                        rg_searchtab.check(R.id.rb_shipment_pending);
                        break;
                    case R.id.rb_unreceived: //筛选
                        if (!StatusID.equals("2")){
                            if(selva!=null) {
                                allOrderBeans.clear();
                                selva.notifyDataSetChanged();
                            }
                        }
                        StatusID = "2";
                        rg_searchtab.check(R.id.rb_unreceived);
                        break;
                    case R.id.rb_pending_evaluation: //筛选
                        if (!StatusID.equals("3")){
                            if(selva!=null) {
                                allOrderBeans.clear();
                                selva.notifyDataSetChanged();
                            }
                        }
                        StatusID = "3";
                        rg_searchtab.check(R.id.rb_pending_evaluation);
                        break;
                }
                PageIndex = 1;
//                selva=null;
                doSth();
            }

        });
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        options1Items.add("我不想买了");
        options1Items.add("信息填写错误,重新拍");
        options1Items.add("卖家缺货");
        options1Items.add("其他原因");


        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的订单");
        tv_empty = (TextView) findViewById(R.id.tv_empty);

        el_allorder.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

    
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppConstants.ScreenWidth==0||AppConstants.ScreenWidth==0){
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
        PageIndex=1;
        if(selva!=null) {
            allOrderBeans.clear();
            selva.notifyDataSetChanged();
        }
        doSth();
    }
    
    /**
     * demo
     */
    private void doSth() {
        OkHttpUtils.post(AppConstants.QueryOrder)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
                .params("StatusID", StatusID)//订单编号
//                .params("Name", orderName)//搜索名称
                .params("PageIndex", PageIndex)//页数
                .params("pageSize", AppConstants.ORDER_PAGESIZE)//搜索名称
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
//                                 if (swipeRefreshLayout.isRefreshing()){
//                                 if (swipeRefreshLayout.isRefreshing()){
//                                     swipeRefreshLayout.setRefreshing(false);
//                                 }
//                                 mPtrFrame.refreshComplete();
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
                                     isEnd=true;
                                     el_allorder.setNoMore(true);
                                     if (PageIndex==1){
                                         el_allorder.setVisibility(View.VISIBLE);
                                         tv_empty.setVisibility(View.GONE);
                                         tv_empty.setText(Message);
                                     }else{
                                         if (el_allorder!=null){
                                         el_allorder.refreshComplete();
                                         el_allorder.setNoMore(true);
                                         }
                                     }


                                     return;
                                 } else {
                                     isEnd=false;
                                     el_allorder.setNoMore(false);
                                     el_allorder.setVisibility(View.VISIBLE);
                                     tv_empty.setVisibility(View.GONE);
                                     if(jsonData!=null) {
                                         if (!jsonData.equals("[]")) {
                                             PageIndex++;
                                         } else {
                                             return;
                                         }
                                     }
                                 }
                                 if (allOrderBeans!=null){
                                     if (PageIndex<=2){
                                         if(selva!=null) {
                                             allOrderBeans.clear();
                                             selva.notifyDataSetChanged();
                                         }
                                         List<AllOrderBean> more= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                         setfootLoadMoreState(more);
                                         allOrderBeans.clear();
                                         allOrderBeans.addAll(more);
                                         setData();
                                     }else{
                                         List<AllOrderBean> more= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                         setfootLoadMoreState(more);
                                         allOrderBeans.addAll(more);
                                         setData();
                                     }
                                 }else{
                                     List<AllOrderBean> more= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                     setfootLoadMoreState(more);
                                     allOrderBeans.addAll(more);
                                     setData();
                                 }

                             }




                             @Override
                             public void onBefore(BaseRequest request) {
                                 super.onBefore(request);
                                 if (!MyOrderListActivity.this.isFinishing()){
                                     if(!dialog.isShowing()){
                                         dialog.show();
                                     }
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
                                 if (el_allorder!=null) {
                                     el_allorder.refreshComplete();
                                     el_allorder.setNoMore(true);
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

    private void setfootLoadMoreState(List<AllOrderBean> more) {
        if(more!=null){
            int i=0;
            for (int j = 0; j <more.size() ; j++) {
                if (more.get(j).getOrderDetail()!=null){
                    i=i+more.get(j).getOrderDetail().size();
                }
            }
            if (i<AppConstants.ORDER_PAGESIZE){
                el_allorder.setNoMore(true);
            }
        }
    }

    private void setCancelOrder(String Numbers) {
        OkHttpUtils.post(AppConstants.CancelMyOrder)
                .tag(this)
                .params("UserName", AppConstants.USERNAME)
                .params("Numbers",Numbers)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(String s, okhttp3.Call call, Response response) {
//                                 if(dialog.isShowing()) {
//                                     dialog.dismiss();
//                                 }
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
//                                     el_allorder.setVisibility(View.GONE);
//                                     tv_empty.setVisibility(View.VISIBLE);
                                     return;
                                 } else {
                                     el_allorder.setVisibility(View.VISIBLE);
                                     tv_empty.setVisibility(View.GONE);
                                     allOrderBeans.clear();
                                     PageIndex=1;
                                     if (selva!=null){
                                         selva.notifyDataSetChanged();
                                     }
//                                     selva=null;
                                     doSth();
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
                                 ToastUtil.showShort(context, "数据加载超时");
                             }
                         }
                );
    }

    private void setData() {
        System.out.println("setData");
        totalCount=0;
        if(selva==null){
            selva = new AllOrderExpandableListViewAdapter(allOrderBeans, context);
            selva.setDeteInterface(this);
            selva.setWarningInterface(this);
            selva.setLongTimeDeliver(this);
            selva.setTackingLogistics(this);
            selva.seTackingLogistics_tow(this);
            selva.setGotoEvaluate(this);
            selva.setConfirmReceipt(this);
            selva.setAdditionalComments(this);
            selva.setApplyforRefund(this);
            selva.setDeleteOrder(this);

            // 在设置适配器之前设置是否支持下拉刷新
            el_allorder.setLoadingMoreEnabled(true);
            el_allorder.setPullRefreshEnabled(true);
            el_allorder.setAdapter(selva);
            for (int i = 0; i < selva.getGroupCount(); i++){
                el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
//                totalCount++;
//                for (OrderDetailBean allOrder:allOrderBeans.get(i).getOrderDetail()){
//                    totalCount++;
//                }
            }

            if (el_allorder!=null){
                el_allorder.setmLoadingListener(new SExpandableListView.LoadingListener() {
                    @Override
                    public void onLoadMore() {
                        isPull = false;
                        loadCount++;
                        Message msg = handler.obtainMessage();
                        msg.arg1 = loadCount;
                        handler.sendMessageDelayed(msg, 2000);
                        Log.e("TAG---HANDLER:", loadCount + "-->");
                    }

                    @Override
                    public void onRefresh() {
                        isEnd=false;
                        PageIndex=1;
                        isPull = true;
                        loadCount++;
                        Message msg = handler.obtainMessage();
                        msg.arg1 = loadCount;
                        handler.sendMessageDelayed(msg, 2000);
                        Log.e("TAG---HANDLER:", loadCount + "-->");
                    }
                });
            }
        }else{

            for (int i = 0; i < selva.getGroupCount(); i++){
                el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
//                totalCount++;
//                for (OrderDetailBean allOrder:allOrderBeans.get(i).getOrderDetail()){
//                    totalCount++;
//                }
            }
            if (selva!=null){
                selva.notifyDataSetChanged();//
            }
            el_allorder.setVisibility(View.VISIBLE);
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

    @Override
    public void refreshComplete() {
//        mPtrFrame.refreshComplete();
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

    @Override
    public void delete(final int groupPosition, int childPosition) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("确定取消订单?");
        builder.setTitle("订单取消不可恢复");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String orderidArray="";
                for (int i = 0; i < allOrderBeans.size(); i++){
                    if (groupPosition==i){
                        for (com.musicdo.musicshop.bean.OrderDetailBean OrderDetailBean:allOrderBeans.get(groupPosition).getOrderDetail()){
                            if (allOrderBeans.get(groupPosition).getOrderDetail().size()>1){
                                orderidArray=orderidArray+OrderDetailBean.getOrderNumber()+",";
                            }else{
                                orderidArray=orderidArray+OrderDetailBean.getOrderNumber();
                            }
                        }
                    }
                }
                setCancelOrder(orderidArray);//传递订单id列表取消订单

            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        /*pvOptions= new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                ToastUtil.showShort(context,options1Items.get(options1));
                for (int i = 0; i < allOrderBeans.size(); i++){
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
                selva.notifyDataSetChanged();//
            }
        }
        )
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
        *//*pvOptions.setPicker(options1Items, options2Items);//二级选择器
//        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*//*
        pvOptions.show();
        InputMethodManager manager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/
    }


    @Override
    public void doWarning(String OrderNumber, int ShopID, String UserName) {
//        dialog = new LoadingDialog(context,R.style.LoadingDialog);
//        if(!dialog.isShowing()){
//            dialog.show();
//        }
        OkHttpUtils.post(AppConstants.DeliverGoods_Warning)
                .tag(this)
                .params("OrderNumber",OrderNumber)
                .params("ShopID", ShopID)
                .params("UserName", AppConstants.USERNAME)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(String s, okhttp3.Call call, Response response) {
//                                 if(dialog.isShowing()) {
//                                     dialog.dismiss();
//                                 }
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
                                     OrderDeliverGoodsWarningDialog.Builder builder = new OrderDeliverGoodsWarningDialog.Builder(context);
                                     builder.setMessage(Message);
//                                  builder.setTitle("注意");
                                     builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int which) {
                                             dialog.dismiss();
                                         }
                                     });
                                     builder.create().show();
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
                                 ToastUtil.showShort(context, "数据加载超时");
                             }
                         }
                );

//        if(dialog.isShowing()){
//            new Handler().postDelayed(new Runnable(){
//                public void run() {
//                    dialog.dismiss();
//                }
//            }, 1000);
//        }
    }

    private void deleteOrder(String Numbers) {
        OkHttpUtils.post(AppConstants.DeleteMyOrder)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
                .params("Numbers", Numbers)//订单编号
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
                            allOrderBeans.clear();
                            PageIndex=1;
                            if (selva!=null){
                                selva.notifyDataSetChanged();
                            }
//                            selva=null;
                            doSth();
                        }
                        /*if (allOrderBeans!=null){
                            if (PageIndex<=2){
//                                meizis= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                                allOrderBeans= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                setData();
                            }else{
//                                List<Meizi>  more= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                                List<AllOrderBean> more= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                allOrderBeans.addAll(more);
                                for (int i = 0; i < selva.getGroupCount(); i++){
                                    el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
                                }
                                selva.notifyDataSetChanged();
                            }
                        }*/
                    }



                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (!MyOrderListActivity.this.isFinishing()){
                        if(!dialog.isShowing()){
                            dialog.show();
                        }}
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
                    }
                });

        if(dialog.isShowing()){
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);
        }
    }

    private void changeOrderState(String OrderNumber) {
        OkHttpUtils.post(AppConstants.ChangeOrderStatus)
                .tag(this)
                .params("Number",OrderNumber)
                .params("UserName", AppConstants.USERNAME)
                .params("Status", 3)//更改为确认收货
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(String s, okhttp3.Call call, Response response) {
//                                 if(dialog.isShowing()) {
//                                     dialog.dismiss();
//                                 }
                                 JSONObject jsonObject;
                                 String jsonData = null;
                                 String Data = null;
                                 String nu = null;
                                 String com = null;
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
                                     allOrderBeans.clear();
                                     PageIndex=1;
                                     if (selva!=null){
                                         selva.notifyDataSetChanged();
                                     }
//                                     selva=null;
                                     doSth();
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
                                 ToastUtil.showShort(context, "数据加载超时");
                             }
                         }
                );
    }

    @Override
    public void dolongTimeDeliver(String OrderNumber, int ShopID, String UserName) {
        OrderLongTimeDeliverDialog.Builder builder = new OrderLongTimeDeliverDialog.Builder(context);
//        builder.setMessage(Message);
//                                  builder.setTitle("注意");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
    public void doTackingLogistics(String OrderNumber, int ShopID, String url,int position) {
        Intent intent=new Intent(context, TrackingLogisticsActivity.class);
        intent.putExtra("OrderNumber",OrderNumber);
        intent.putExtra("iconUrl",url);
        intent.putExtra("StatusID",StatusID);
        intent.putExtra("LogisticsName",allOrderBeans.get(position).getLogisticsName());
        intent.putExtra("LogisticsCode",allOrderBeans.get(position).getLogisticsCode());
        startActivity(intent);
    }

    @Override
    public void doGotoEvaluate(String OrderNumber, int ShopID, String UserName) {
        List<String> imgs=new ArrayList<>();
        List<String> prodTitles=new ArrayList<>();
        List<Integer> prodIds=new ArrayList<>();
        int shopId=0;
        String shopName="";
        String orderNumber="";
        for (OrderDetailBean detailBean:allOrderBeans.get(ShopID).getOrderDetail()){
            imgs.add(detailBean.getSrc());
            prodTitles.add(detailBean.getName());
            shopId=detailBean.getShopID();
            shopName=detailBean.getShopName();
            orderNumber=detailBean.getOrderNumber();
            prodIds.add(detailBean.getProductID());
        }
        Intent intent=new Intent(context, GoToEvaluateActivity.class);
        intent.putStringArrayListExtra("pridImgs", (ArrayList<String>) imgs);
        intent.putStringArrayListExtra("prodTitles", (ArrayList<String>) prodTitles);
        intent.putExtra("shopId",shopId);
        intent.putExtra("shopName",shopName);
        intent.putExtra("orderNumber",orderNumber);
        intent.putIntegerArrayListExtra("prodIds", (ArrayList<Integer>) prodIds);
        startActivity(intent);
    }

    @Override
    public void doTackingLogistics_tow(String OrderNumber, int ShopID, String orderName,String url,int position) {
        Intent intent=new Intent(context, TrackingLogisticsActivity.class);
        intent.putExtra("orderName",orderName);
        intent.putExtra("OrderNumber",OrderNumber);
        intent.putExtra("iconUrl",url);
        intent.putExtra("StatusID",StatusID);
        intent.putExtra("LogisticsName",allOrderBeans.get(position).getLogisticsName());
        intent.putExtra("LogisticsCode",allOrderBeans.get(position).getLogisticsCode());
        startActivity(intent);
    }

    @Override
    public void doDeleteOrder(final String OrderNumber, int groupPosition, String UserName) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("确认删除订单");
        builder.setTitle("订单删除后将不可恢复!");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteOrder(OrderNumber);
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    @Override
    public void doConfirmReceipt(final String OrderNumber, int groupPosition, String UserName) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("确认收货");
        builder.setTitle("确认收货，款项将会划到卖家账户");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                changeOrderState(OrderNumber);
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void doApplyforRefund(String OrderNumber, int ProductId, String UserName, String icon, String title, String param, String price) {
        Intent intent=new Intent(context,ApplyRefundActivity.class);
        intent.putExtra("OrderNumber",OrderNumber);
        intent.putExtra("icon",icon);
        intent.putExtra("title",title);
        intent.putExtra("param",param);
        intent.putExtra("price",price);
        intent.putExtra("ProductId",ProductId);
        startActivity(intent);
    }

    @Override
    public void doAdditionalComments(String OrderNumber, int ShopID, String UserName) {
        List<String> imgs=new ArrayList<>();
        List<String> prodTitles=new ArrayList<>();
        List<Integer> prodIds=new ArrayList<>();
        int shopId=0;
        String shopName="";
        String orderNumber="";
        for (OrderDetailBean detailBean:allOrderBeans.get(ShopID).getOrderDetail()){
            imgs.add(detailBean.getSrc());
            prodTitles.add(detailBean.getName());
            shopId=detailBean.getShopID();
            shopName=detailBean.getShopName();
            orderNumber=detailBean.getOrderNumber();
            prodIds.add(detailBean.getProductID());
        }
        Intent intent=new Intent(context, AdditionalCommentsActivity.class);
        intent.putStringArrayListExtra("pridImgs", (ArrayList<String>) imgs);
        intent.putStringArrayListExtra("prodTitles", (ArrayList<String>) prodTitles);
        intent.putExtra("shopId",shopId);
        intent.putExtra("shopName",shopName);
        intent.putExtra("orderNumber",orderNumber);
        intent.putIntegerArrayListExtra("prodIds", (ArrayList<Integer>) prodIds);
        startActivity(intent);
    }

    @Override
    public void onPageChanging(int pageSize, int pageIndex) {
        doSth();
    }

    @Override
    public boolean canLoadData() {
        if (selva!=null){
            return (selva.getGroupCount() >= 0) ? true : false;
//            return true;
        }else{
            return false;
        }
    }
}
