package com.musicdo.musicshop.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.TextView;

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
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AdditionalCommentsActivity;
import com.musicdo.musicshop.activity.ApplyRefundActivity;
import com.musicdo.musicshop.activity.GoToEvaluateActivity;
import com.musicdo.musicshop.activity.TrackingLogisticsActivity;
import com.musicdo.musicshop.adapter.AllOrderExpandableListViewAdapter;
import com.musicdo.musicshop.adapter.OrderUnpaidAdapter;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.OrderDetailBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.MySwipeRefreshLayout;
import com.musicdo.musicshop.view.NestedExpandaleListView;
import com.musicdo.musicshop.view.OptionsPickerView;
import com.musicdo.musicshop.view.OrderDeliverGoodsWarningDialog;
import com.musicdo.musicshop.view.OrderLongTimeDeliverDialog;
import com.musicdo.musicshop.view.RefreshCompleteCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 全部订单
 * Created by A on 2017/7/18.
 */

public class OrderAllFragment extends BaseFragment implements View.OnClickListener,
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
        RefreshCompleteCallBack {
    View rootView;
    private final static String TAG = "OrderAllFragment";
    OrderUnpaidAdapter orderUnpaidAdapter;
    int page=3;
//    private MySwipeRefreshLayout swipeRefreshLayout;
    PtrClassicFrameLayout mPtrFrame;
    List<Meizi> meizis=new ArrayList<>();
    private ArrayList<String> options1Items = new ArrayList<>();
    List<AllOrderBean> allOrderBeans=new ArrayList<>();
    private TextView textView,tv_empty;
    private NestedExpandaleListView el_allorder;
    //    RecyclerView rc_order_unpaid;
    LoadingDialog dialog;
    private int lastVisibleItem;
    private int PageIndex=1;
    LinearLayoutManager ms;
    private AllOrderExpandableListViewAdapter selva;
    private int totalCount=0;
    OptionsPickerView pvOptions;

    @Override
    public View initView() {
        if (rootView==null) {
            dialog = new LoadingDialog(mContext,R.style.LoadingDialog);
            options1Items.add("我不想买了");
            options1Items.add("信息填写错误,重新拍");
            options1Items.add("卖家缺货");
            options1Items.add("其他原因");
            rootView = LayoutInflater.from(mContext).inflate(R.layout.order_unpaid, null);
            /*swipeRefreshLayout=(MySwipeRefreshLayout) rootView.findViewById(R.id.grid_swipe_refresh) ;
//            swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            swipeRefreshLayout.setRefreshing(true);*/

            mPtrFrame=(PtrClassicFrameLayout) rootView.findViewById(R.id.grid_swipe_refresh);
            mPtrFrame.setLastUpdateTimeRelateObject(this);


        /*swipeRefreshLayout=(MySwipeRefreshLayout) view.findViewById(R.id.grid_swipe_refresh) ;
//            swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setRefreshing(true);*/
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

            el_allorder = (NestedExpandaleListView) rootView.findViewById(R.id.el_allorder);
            tv_empty = (TextView) rootView.findViewById(R.id.tv_empty);
//            ms= new LinearLayoutManager(mContext);
//            rc_order_unpaid.setLayoutManager(ms);
            el_allorder.setOnScrollListener(new AbsListView.OnScrollListener() {
                boolean isChangePage=false;
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(allOrderBeans.size()!=0){
                        int aaa=selva.getGroupCount();
                        if (scrollState == RecyclerView.SCROLL_STATE_IDLE && isChangePage) {
                            doSth();
                            isChangePage=false;
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastVisibleItem = firstVisibleItem+visibleItemCount;
                    if (lastVisibleItem==totalItemCount&&totalItemCount!=0){//此处bug，不满一屏幕的时候会自动加载下一页数据，即会执行加载更多，可用加载更多插件
                        isChangePage=true;
                    }else{
                        isChangePage=false;
                    }


                }
            });
            el_allorder.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return true;
                }
            });

        }

        return rootView;
    }

    @Override
    public void refreshComplete() {
        mPtrFrame.refreshComplete();
    }

    /**
     * demo
     */
    private void doSth() {
        OkHttpUtils.post(AppConstants.QueryOrder)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
//                .params("StatusID", StatusID)//订单编号
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
//                                     swipeRefreshLayout.setRefreshing(false);
//                                 }
                                 mPtrFrame.refreshComplete();
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
                                         el_allorder.setVisibility(View.GONE);
                                         tv_empty.setVisibility(View.VISIBLE);
                                         tv_empty.setText(Message);
                                     }
                                     return;
                                 } else {

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
//                                meizis= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                                         allOrderBeans= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                         setData();
                                     }else{
//                                List<Meizi>  more= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                                         List<AllOrderBean> more= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
                                         allOrderBeans.addAll(more);
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
                                 ToastUtil.showShort(mContext, "数据加载超时");
                                 if(dialog.isShowing()){
                                     new Handler().postDelayed(new Runnable(){
                                         public void run() {
                                             dialog.dismiss();
                                         }
                                     }, 1000);
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
                                     ToastUtil.showShort(mContext, Message);
                                     el_allorder.setVisibility(View.GONE);
                                     tv_empty.setVisibility(View.VISIBLE);
                                     return;
                                 } else {
                                     el_allorder.setVisibility(View.VISIBLE);
                                     tv_empty.setVisibility(View.GONE);
                                     allOrderBeans.clear();
                                     PageIndex=1;
                                     selva=null;
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
                                 ToastUtil.showShort(mContext, "数据加载超时");
                             }
                         }
                );
    }

    private void setData() {
        totalCount=0;
        if(selva==null){
            selva = new AllOrderExpandableListViewAdapter(allOrderBeans, mContext);
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
            el_allorder.setAdapter(selva);
            for (int i = 0; i < selva.getGroupCount(); i++){
                el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
//                totalCount++;
//                for (OrderDetailBean allOrder:allOrderBeans.get(i).getOrderDetail()){
//                    totalCount++;
//                }
            }
            el_allorder.setNestedScrollingEnabled(false);
//            SpUtils.setListViewHeightBasedOnChildren(el_allorder);
            /*orderUnpaidAdapter = new OrderUnpaidAdapter(mContext,meizis);
            rc_order_unpaid.setAdapter(orderUnpaidAdapter);//recyclerview设置适配器
            orderUnpaidAdapter.setOnItemClickListener(new OrderUnpaidAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                    startActivity(intent);
                }
            });
            rc_order_unpaid.setNestedScrollingEnabled(false);*/
        }else{
            //让适配器刷新数据
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

    @Override
    public void initData() {
        super.initData();
//        doSth();
        setListener();//刷新数据
        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if(selva!=null){//第一次不刷新加载
                    allOrderBeans.clear();
                PageIndex=1;
                selva=null;
                doSth();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        doSth();
    }

    private void setListener() {
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                allOrderBeans.clear();
//                PageIndex=1;
//                selva=null;
//                doSth();
//            }
//        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
//        if (swipeRefreshLayout!=null) {
//            if (swipeRefreshLayout.isRefreshing()) {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        }
        if (mPtrFrame!=null) {
            if (mPtrFrame.isRefreshing()) {
                mPtrFrame.refreshComplete();
            }
        }
    }

    @Override
    public void delete(final int groupPosition, int childPosition) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setMessage("确定取消订单?");
        builder.setTitle("订单取消不可恢复");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String orderidArray="";
                for (int i = 0; i < allOrderBeans.size(); i++){
                    if (groupPosition==i){
                        for (OrderDetailBean OrderDetailBean:allOrderBeans.get(groupPosition).getOrderDetail()){
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
        /*pvOptions= new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                ToastUtil.showShort(mContext,options1Items.get(options1));
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
                .setBgColor(mContext.getResources().getColor(R.color.home_top_reflash_bg))
                .setTitleBgColor(mContext.getResources().getColor(R.color.home_top_reflash_bg))
//                .isDialog(false)
                .build();

        pvOptions.setPicker(options1Items);//一级选择器
        *//*pvOptions.setPicker(options1Items, options2Items);//二级选择器
//        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*//*
        pvOptions.show();
        InputMethodManager manager= (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/
    }


    @Override
    public void doWarning(String OrderNumber, int ShopID, String UserName) {
//        dialog = new LoadingDialog(mContext,R.style.LoadingDialog);
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
                                     ToastUtil.showShort(mContext, Message);
                                     return;
                                 } else {
                                     OrderDeliverGoodsWarningDialog.Builder builder = new OrderDeliverGoodsWarningDialog.Builder(mContext);
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
                                 ToastUtil.showShort(mContext, "数据加载超时");
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
                            ToastUtil.showShort(mContext, Message);
                            return;
                        } else {
                            allOrderBeans.clear();
                            PageIndex=1;
                            selva=null;
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
                        if(!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "数据加载超时");
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
                                     ToastUtil.showShort(mContext, Message);
                                     return;
                                 } else {
                                     allOrderBeans.clear();
                                     PageIndex=1;
                                     selva=null;
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
                                 ToastUtil.showShort(mContext, "数据加载超时");
                             }
                         }
                );
    }

    @Override
    public void dolongTimeDeliver(String OrderNumber, int ShopID, String UserName) {
        OrderLongTimeDeliverDialog.Builder builder = new OrderLongTimeDeliverDialog.Builder(mContext);
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
        Intent intent=new Intent(mContext, TrackingLogisticsActivity.class);
        intent.putExtra("iconUrl",url);
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
        Intent intent=new Intent(mContext, GoToEvaluateActivity.class);
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
        Intent intent=new Intent(mContext, TrackingLogisticsActivity.class);
        intent.putExtra("orderName",orderName);
        intent.putExtra("OrderNumber",OrderNumber);
        intent.putExtra("iconUrl",url);
        startActivity(intent);
    }

    @Override
    public void doDeleteOrder(final String OrderNumber, int groupPosition, String UserName) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
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
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
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
        Intent intent=new Intent(mContext,ApplyRefundActivity.class);
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
        Intent intent=new Intent(mContext, AdditionalCommentsActivity.class);
        intent.putStringArrayListExtra("pridImgs", (ArrayList<String>) imgs);
        intent.putStringArrayListExtra("prodTitles", (ArrayList<String>) prodTitles);
        intent.putExtra("shopId",shopId);
        intent.putExtra("shopName",shopName);
        intent.putExtra("orderNumber",orderNumber);
        intent.putIntegerArrayListExtra("prodIds", (ArrayList<Integer>) prodIds);
        startActivity(intent);
    }
}
