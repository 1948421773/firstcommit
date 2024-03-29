package com.musicdo.musicshop.fragments;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import com.musicdo.musicshop.adapter.AllOrderExpandableListViewAdapter;
import com.musicdo.musicshop.adapter.OrderUnpaidAdapter;
import com.musicdo.musicshop.adapter.UnDeliveryOrderExpandableListViewAdapter;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.MySwipeRefreshLayout;
import com.musicdo.musicshop.view.OrderDeliverGoodsWarningDialog;
import com.musicdo.musicshop.view.RefreshCompleteCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:
 * 作者：haiming on 2017/8/28 14:59
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class OrderUnDeliveryFragment extends BaseFragment implements RefreshCompleteCallBack,View.OnClickListener,UnDeliveryOrderExpandableListViewAdapter.DeliverGoodsWarning{
    View rootView;
    private final static String TAG = "OrderUnDeliveryFragment";
    OrderUnpaidAdapter orderUnpaidAdapter;
    int page=3;
    List<Meizi> meizis=new ArrayList<>();
    List<AllOrderBean> allOrderBeans=new ArrayList<>();
    private TextView textView,tv_empty;
    private ExpandableListView el_allorder;
    //    RecyclerView rc_order_unpaid;
    LoadingDialog dialog;
//    private MySwipeRefreshLayout swipeRefreshLayout;
    PtrClassicFrameLayout mPtrFrame;
    private int lastVisibleItem;
    private int PageIndex=1;
    LinearLayoutManager ms;
    private UnDeliveryOrderExpandableListViewAdapter selva;

    @Override
    public View initView() {
        if (rootView==null) {
            dialog = new LoadingDialog(mContext,R.style.LoadingDialog);
            rootView = LayoutInflater.from(mContext).inflate(R.layout.order_unpaid, null);
            mPtrFrame=(PtrClassicFrameLayout) rootView.findViewById(R.id.grid_swipe_refresh);
            mPtrFrame.setLastUpdateTimeRelateObject(this);
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
            el_allorder = (ExpandableListView) rootView.findViewById(R.id.el_allorder);
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
                    if (lastVisibleItem==totalItemCount&&totalItemCount!=0){
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
                .params("StatusID", "1")//订单编号
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
                                for (int i = 0; i < selva.getGroupCount(); i++){
                                    el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
                                }
                                selva.notifyDataSetChanged();
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
                });
        if(dialog.isShowing()){
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);

        }
    }

    private void setData() {
        if(selva==null){
            selva = new UnDeliveryOrderExpandableListViewAdapter(allOrderBeans, mContext);
            el_allorder.setAdapter(selva);
            selva.setWarningInterface(this);
            for (int i = 0; i < selva.getGroupCount(); i++){
                el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
            }
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
            selva.notifyDataSetChanged();//
        }
    }

    @Override
    public void initData() {
        super.initData();
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
        if (mPtrFrame!=null) {
            if (mPtrFrame.isRefreshing()) {
                mPtrFrame.refreshComplete();
            }
        }
    }

    @Override
    public void doWarning(String OrderNumber, int ShopID, String UserName) {
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
    }
}
