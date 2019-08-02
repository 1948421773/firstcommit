package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.ItemEducationAdapter;
import com.musicdo.musicshop.adapter.OrderDetailExpandableListViewAdapter;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.OrderDetailInfoBean;
import com.musicdo.musicshop.bean.OrderDetailItemBean;
import com.musicdo.musicshop.bean.OrderProObjBean;
import com.musicdo.musicshop.bean.PersonalRecommend;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.OrderDeliverGoodsWarningDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 订单详情
 * Created by Administrator on 2017/9/4.
 */

public class OrderDtailActivity extends BaseActivity implements View.OnClickListener,
        OrderDetailExpandableListViewAdapter.DeleteInterface,
        OrderDetailExpandableListViewAdapter.DeliverGoodsWarning,
        OrderDetailExpandableListViewAdapter.AdditionalComments,
        OrderDetailExpandableListViewAdapter.ConfirmReceipt,
        OrderDetailExpandableListViewAdapter.TackingLogistics_tow,
        OrderDetailExpandableListViewAdapter.GotoEvaluate,
        OrderDetailExpandableListViewAdapter.TackingLogistics,
        OrderDetailExpandableListViewAdapter.DeleteOrder,
        OrderDetailExpandableListViewAdapter.ApplyforRefund
        {
    private Context context;
    private String Number="";
    private String NumberParam="";//退款详情传递参数key为：ID ，订单详情传递参数key为：Number
    private String URLParam="";//退款详情接口为：GetRefundDetail ，订单详情接口为：GetOrderDetail
    private LinearLayout ll_back;
    private RelativeLayout rl_payment_way;
    private int orderState=0;
    private int OrdProID=0;
    private String IsDealwith="";
    private TextView tv_title,tv_orderdetail_state,tv_orderdetail_time,tv_address_name,tv_address_phone,tv_tracking_Logistics,tv_confirm_receipt,
            tv_detail_address,tv_orderdetail_number,tv_orderdetail_pay_time,tv_orderdetail_create,tv_orderdetail_deliver_good_time;
            RecyclerView rc_grid_education;
    private ExpandableListView el_order_orderdetail;
            ItemEducationAdapter itemEducationAdapter;
    private OrderDetailExpandableListViewAdapter selva;
    List<OrderDetailItemBean> orderDetailItemBeans=new ArrayList<>();
    ArrayList<PersonalRecommend> homeSecondBeans=new ArrayList<>();
    AllOrderBean allOrderBeans=new AllOrderBean();
    private  String LogisticsName="";
    private  String LogisticsCode="";
    private LinearLayout ll_payfor_account,ll_deliver_good_time;
    private OrderDetailInfoBean OrderDetailInfos=new OrderDetailInfoBean();
            String loginData="";
            private final String LoginKey="LoginKey";
            private final String LoginFile="LoginFile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);
        MyApplication.getInstance().addActivity(this);
        context=this;
        Number=getIntent().getStringExtra("Number");
        orderState=getIntent().getIntExtra("orderState",0);
        IsDealwith=getIntent().getStringExtra("IsDealwith");
        OrdProID=getIntent().getIntExtra("OrdProID",0);
        LogisticsName=getIntent().getStringExtra("LogisticsName");
        LogisticsCode=getIntent().getStringExtra("LogisticsCode");
        initView();

        doSth();
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
                if(AppConstants.ScreenWidth==0||AppConstants.ScreenWidth==0){
                    AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
                    AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
                }

            initData();
            }

    private void initView() {
        rc_grid_education = (RecyclerView) findViewById(R.id.rc_grid_education );
        GridLayoutManager grid_education_layoutManage = new GridLayoutManager(context,2);
        rc_grid_education.setLayoutManager(grid_education_layoutManage);
        rl_payment_way=(RelativeLayout) findViewById(R.id.rl_payment_way) ;
        el_order_orderdetail=(ExpandableListView) findViewById(R.id.el_order_orderdetail) ;
        ll_back=(LinearLayout) findViewById(R.id.ll_back) ;
        ll_back.setOnClickListener(this);
        tv_tracking_Logistics=(TextView) findViewById(R.id.tv_tracking_Logistics) ;
        tv_tracking_Logistics.setOnClickListener(this);
        tv_confirm_receipt=(TextView) findViewById(R.id.tv_confirm_receipt) ;
        tv_confirm_receipt.setOnClickListener(this);
        tv_title=(TextView) findViewById(R.id.tv_title) ;
        tv_title.setText("订单详情");
        tv_orderdetail_state=(TextView) findViewById(R.id.tv_orderdetail_state) ;
        if(IsDealwith!=null){
            NumberParam="ID";
            URLParam=AppConstants.GetRefundDetail;
            switch (Integer.valueOf(IsDealwith)) {
                case 0:
//                    tv_orderdetail_state.setText("等待买家付款");
                    break;
                case 1:
                    tv_orderdetail_state.setText("退款申请中");
                    break;
                case 2:
                    tv_orderdetail_state.setText("被拒退款");
                    rl_payment_way.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_orderdetail_state.setText("退款完成");
                    break;
            }
        }else {
            NumberParam="Number";
            URLParam=AppConstants.GetOrderDetail;
            switch (Integer.valueOf(orderState)) {
                case 0:
                    tv_orderdetail_state.setText("等待买家付款");
                    break;
                case 1:
                    tv_orderdetail_state.setText("已付款");
                    break;
                case 2:
                    tv_orderdetail_state.setText("已发货");
                    rl_payment_way.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_orderdetail_state.setText("交易完成");
                    break;
                case 4:
                    tv_orderdetail_state.setText("退款处理中");
                    break;
                case 5:
                    tv_orderdetail_state.setText("退款申请中");
                    break;
                case 6:
                    tv_orderdetail_state.setText("退款完成");
                    break;
                case -1:
                    tv_orderdetail_state.setText("交易关闭");
                    break;
                case -2:
                    tv_orderdetail_state.setText("商家已取消");
                    break;
                case -3:
                    tv_orderdetail_state.setText("交易关闭");
                    break;
            }
        }
        tv_address_name=(TextView) findViewById(R.id.tv_address_name) ;
        tv_address_phone=(TextView) findViewById(R.id.tv_address_phone) ;
        tv_detail_address=(TextView) findViewById(R.id.tv_detail_address) ;
        tv_orderdetail_number=(TextView) findViewById(R.id.tv_orderdetail_number) ;
        tv_orderdetail_pay_time=(TextView) findViewById(R.id.tv_orderdetail_pay_time) ;
        tv_orderdetail_create=(TextView) findViewById(R.id.tv_orderdetail_create) ;
        tv_orderdetail_deliver_good_time=(TextView) findViewById(R.id.tv_orderdetail_deliver_good_time) ;

        ll_payfor_account=(LinearLayout) findViewById(R.id.ll_payfor_account) ;
        ll_deliver_good_time=(LinearLayout) findViewById(R.id.ll_deliver_good_time) ;

    }

    private void initData() {
        OkHttpUtils.post(URLParam)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
                .params(NumberParam,Number)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        HashMap<String,String> callBackWXPay=new HashMap<>();
                        JSONObject jsonObject;
                        String jsonData=null;
                        String returnData=null;
                        String Message=null;
                        boolean Flag=false;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
                            returnData = jsonObject.getString("ReturnData");
//                            jsonData = jsonObject.getString("Data");
                            JSONObject WXObject = new JSONObject(returnData);
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                                ToastUtil.showShort(context,s);
                        if (!Flag){
//                                        ToastUtil.showShort(context,Message);
                        }else{
//                            orderDetailItemBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<OrderDetailItemBean>>(){}.getType());
//                            OrderDetailInfos= gson.fromJson(returnData, OrderDetailInfoBean.class);
                            allOrderBeans=gson.fromJson(returnData, AllOrderBean.class);
                            /*for (int i = 0; i < CartBeans.size(); i++){
                                CartBean cb=new CartBean();
                                cb.setShopName(CartBeans.get(i).getShopName());
                                cb.setShopID(CartBeans.get(i).getShopID());
                                cb.setProductDetail(CartBeans.get(i).getProductDetail());
                                groupCarts.add(cb);
                                for(int j = 0; j < cb.getProductDetail().size(); j++){
                                    totalNumber=totalNumber+cb.getProductDetail().get(j).getCount();
                                    totalPrice=totalPrice+cb.getProductDetail().get(j).getCount()*cb.getProductDetail().get(j).getMemberPrice();
                                }

                            }*/
                            selva = new OrderDetailExpandableListViewAdapter(1,allOrderBeans, orderState,context);
                            selva.setDeteInterface(OrderDtailActivity.this);
                            selva.setApplyforRefund(OrderDtailActivity.this);
                            selva.setWarningInterface(OrderDtailActivity.this);
                            selva.setConfirmReceipt(OrderDtailActivity.this);
                            selva.setDeleteOrder(OrderDtailActivity.this);
                            selva.setGotoEvaluate(OrderDtailActivity.this);
                            selva.seTackingLogistics_tow(OrderDtailActivity.this);
                            selva.setAdditionalComments(OrderDtailActivity.this);
                            selva.setTackingLogistics(OrderDtailActivity.this);
                            el_order_orderdetail.setAdapter(selva);
                            for (int i = 0; i < selva.getGroupCount(); i++){
                                el_order_orderdetail.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
                            }
                            SpUtils.setListViewHeightBasedOnChildren(el_order_orderdetail);
                            setInfo();
                            //设置数量总价
                           /* tv_total_num.setText(String.valueOf(totalNumber));
                            tv_total_price_text.setText(SpUtils.doubleToString(totalPrice));
                            tv_total_price.setText(tv_total_price_text.getText().toString().trim());*/
                                /*
                                submitOrders.add(submitOrder);
                                if(submitOrderAdapter==null&&submitOrders!=null){
                                    submitOrderAdapter = new SubmitOrderAdapter(context,submitOrders);
                                    rc_prod_buynow.setAdapter(submitOrderAdapter);//recyclerview设置适配器
                                    submitOrderAdapter.setOnItemClickListener(new SubmitOrderAdapter.OnSearchItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
//                                        Toast.makeText(context, "点击了"+position, Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(context,ProductDetailActivity.class);
                                            intent.putExtra("prod_id",submitOrders.get(position).getID());
                                            startActivity(intent);
                                        }
                                    });
                                    rc_prod_buynow.setNestedScrollingEnabled(false);
                                }
                                setSubmitOrderPriceInfo();*/
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
//                                    ToastUtil.showShort(context,"数据加载超时");

                    }
                });
    }

            private void doSth() {
                OkHttpUtils.post(AppConstants.GetProductRecommend)
                        .params("ProductID",Number)
                        .params("Top",12)
                        .tag(this)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, okhttp3.Call call, Response response) {

//                                page++;
                                JSONObject jsonObject;
                                Gson gson=new Gson();
                                String jsonData=null;
                                try {
                                    jsonObject = new JSONObject(s);
                                    jsonData = jsonObject.getString("ReturnData");
//                                    Log.i(TAG, "onSuccess: "+jsonData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(homeSecondBeans==null||homeSecondBeans.size()==0){
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

//                                Log.i(TAG, "onBefore: 开始刷新");
                            }
                        });
            }


            private void setData() {
                if(itemEducationAdapter==null){
                    if (homeSecondBeans!=null){
                        itemEducationAdapter = new ItemEducationAdapter(context,homeSecondBeans);
                        rc_grid_education.setAdapter(itemEducationAdapter);//recyclerview设置适配器
                        itemEducationAdapter.setOnItemClickListener(new ItemEducationAdapter.OnSearchItemClickListener(){
                            @Override
                            public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(context,ProductDetailActivity.class);
                                intent.putExtra("prod_id",homeSecondBeans.get(position).getID());
                                startActivity(intent);
                            }
                        });

                        rc_grid_education.setNestedScrollingEnabled(false);
                    }
                }else{
                    //让适配器刷新数据
                    itemEducationAdapter.notifyDataSetChanged();
                }

            }
    private void setInfo() {//发货状态、时间、订单时间和订单编号

//        tv_orderdetail_time.setText(allOrderBeans.getCreateTime());
        tv_address_name.setText("收货人:"+allOrderBeans.getReceiveName());
        tv_address_phone.setText(allOrderBeans.getReceiveMobile());
        tv_detail_address.setText("收货地址:"+allOrderBeans.getReceiveAddress());

        tv_orderdetail_number.setText(allOrderBeans.getOrderNumber());
        tv_orderdetail_create.setText(allOrderBeans.getCreateTime());
        if (allOrderBeans!=null){
            if (allOrderBeans.getPayTime()!=null){
                tv_orderdetail_pay_time.setText(allOrderBeans.getPayTime());
            }else{
                ll_payfor_account.setVisibility(View.GONE);

            }
            if (allOrderBeans.getDeliverTime()!=null){
                tv_orderdetail_deliver_good_time.setText(allOrderBeans.getDeliverTime());
            }else{
                ll_deliver_good_time.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_tracking_Logistics:
            {
                Intent intent=new Intent(context, TrackingLogisticsActivity.class);
                intent.putExtra("iconUrl",allOrderBeans.getOrderProObj().get(0).getSrc());
                intent.putExtra("OrderNumber",Number);
                intent.putExtra("StatusID",String.valueOf(orderState));
                intent.putExtra("LogisticsName",LogisticsName);
                intent.putExtra("LogisticsCode",LogisticsCode);
                startActivity(intent);
            }
                break;
            case R.id.tv_confirm_receipt: {
                CustomDialog.Builder builder = new CustomDialog.Builder(context);
                builder.setMessage("确认收货");
                builder.setTitle("确认收货，款项将会划到卖家账户");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        changeOrderState(allOrderBeans.getOrderNumber());
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
                break;
        }
    }

    @Override
    public void delete(int groupPosition, int childPosition) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("确定取消订单?");
        builder.setTitle("订单取消不可恢复");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String orderidArray="";
                        for (OrderProObjBean OrderDetailBean:allOrderBeans.getOrderProObj()){
                            if (allOrderBeans.getOrderProObj().size()>1){
                                orderidArray=orderidArray+OrderDetailBean.getOrderNumber()+",";
                            }else{
                                orderidArray=orderidArray+OrderDetailBean.getOrderNumber();
                            }
                        }

                /*for (int i = 0; i < allOrderBeans.size(); i++){
                    if (groupPosition==i){
                        for (OrderDetailBean OrderDetailBean:allOrderBeans.get(groupPosition).getOrderDetail()){
                            if (allOrderBeans.get(groupPosition).getOrderDetail().size()>1){
                                orderidArray=orderidArray+OrderDetailBean.getOrderNumber()+",";
                            }else{
                                orderidArray=orderidArray+OrderDetailBean.getOrderNumber();
                            }
                        }
                    }
                }*/
                setCancelOrder(orderidArray);//传递订单id列表取消订单

            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
                                     return;
                                 } else {
//                                     ToastUtil.showShort(mContext, Message);
                                     finish();
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
                                             finish();
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

            private void deleteOrder(String Numbers) {
                OkHttpUtils.post(AppConstants.DeleteMyOrder)
                        .tag(this)
                        .params("UserName",AppConstants.USERNAME)
                        .params("Numbers", Numbers)//订单编号
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
                                ToastUtil.showShort(context, "数据加载超时");
                            }
                        });
            }

            @Override
            public void doApplyforRefund(String OrderNumber, int ProductId, String UserName, String icon, String title, String param, String price,int OrderProductID) {
                Intent intent=new Intent(context,ApplyRefundActivity.class);
                intent.putExtra("OrderNumber",OrderNumber);
                intent.putExtra("OrdProID",OrderProductID);//商品订单的ID);
                intent.putExtra("icon",icon);
                intent.putExtra("title",title);
                intent.putExtra("param",param);
                intent.putExtra("price",price);
                intent.putExtra("ProductId",ProductId);
                startActivity(intent);
            }

            @Override
            public void doTackingLogistics(String OrderNumber, int ShopID, String url) {
                Intent intent=new Intent(context, TrackingLogisticsActivity.class);
                intent.putExtra("iconUrl",url);
                startActivity(intent);
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
            public void doAdditionalComments(String OrderNumber, int groupPosition, String UserName) {
                List<String> imgs=new ArrayList<>();
                List<String> prodTitles=new ArrayList<>();
                List<Integer> prodIds=new ArrayList<>();
                int shopId=0;
                String shopName="";
                String orderNumber="";
                for (OrderProObjBean detailBean:allOrderBeans.getOrderProObj()){
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
            public void doGotoEvaluate(String OrderNumber, int groupPosition, String UserName) {
                List<String> imgs=new ArrayList<>();
                List<String> prodTitles=new ArrayList<>();
                List<Integer> prodIds=new ArrayList<>();
                int shopId=0;
                String shopName="";
                String orderNumber="";
                for (OrderProObjBean detailBean:allOrderBeans.getOrderProObj()){
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
            public void doTackingLogistics_tow(String OrderNumber, int ShopID, String UserName, String url) {
                Intent intent=new Intent(context, TrackingLogisticsActivity.class);
                intent.putExtra("orderName",OrderNumber);
                intent.putExtra("OrderNumber",OrderNumber);
                intent.putExtra("iconUrl",url);
                intent.putExtra("StatusID",String.valueOf(orderState));
                startActivity(intent);
            }
        }
