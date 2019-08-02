package com.musicdo.musicshop.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import com.musicdo.musicshop.adapter.OrderTrackingLogisticsAdapter;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.Logistics;
import com.musicdo.musicshop.bean.TrackingLogisticsBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:物流追踪
 * 作者：haiming on 2017/8/30 17:56
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 */

public class TrackingLogisticsActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_title,tv_express_name,tv_order_number,tv_empty,tv_logistic_state;
    private ImageView sd_purchase_img;
    private RecyclerView rv_tracking_logistics_list;
    private RelativeLayout rl_purchaselist_item_title,rl_logistics_message;
    ArrayList<TrackingLogisticsBean> searchProdBeans=new ArrayList<>();
    ArrayList<Logistics> Logistics=new ArrayList<>();
    OrderTrackingLogisticsAdapter trackingLogisticsAdapter;
    GridLayoutManager list_layoutManage;
    private LinearLayout ll_back;
    private String orderName;
    private String iconUrl;
    private String[] address={"已签收，签收是本人，感谢是用顺丰快递,期待为你再次服务",
    "[广州]越秀区快递员:孙悟空1386686886,正在为你派件","[广州]快递已到越秀区","[海口]海南海口公司已发出",
            "[海口]顺丰快递 海南海口公司分部收件员已揽件","包裹等待揽收"};
    LoadingDialog dialog;
    String LogisticsName = null;
    String LogisticsCode = null;
    String OrderNumber = null;
    String loginData="";
    String StatusID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_logistics);
        MyApplication.getInstance().addActivity(this);
        context=this;
        orderName=getIntent().getStringExtra("orderName");
        iconUrl=getIntent().getStringExtra("iconUrl");
        OrderNumber=getIntent().getStringExtra("OrderNumber");
        StatusID=getIntent().getStringExtra("StatusID");
        LogisticsName=getIntent().getStringExtra("LogisticsName");
        LogisticsCode=getIntent().getStringExtra("LogisticsCode");
        initView();
    }

    @Override
    protected void onResume() {
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

    }

    private void initData() {

        doSth();
    }

    private void initView() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_express_name=(TextView) findViewById(R.id.tv_express_name);
        tv_order_number=(TextView) findViewById(R.id.tv_order_number);
        tv_logistic_state=(TextView) findViewById(R.id.tv_logistic_state);
        tv_empty=(TextView) findViewById(R.id.tv_empty);
        tv_empty.setText("暂无物流信息");
        sd_purchase_img=(ImageView) findViewById(R.id.sd_purchase_img);
        tv_title.setText("查看物流");
        ll_back.setOnClickListener(this);
        rv_tracking_logistics_list=(RecyclerView)findViewById(R.id.rv_tracking_logistics_list);
        rl_purchaselist_item_title=(RelativeLayout)findViewById(R.id.rl_purchaselist_item_title);
        rl_logistics_message=(RelativeLayout)findViewById(R.id.rl_logistics_message);
        list_layoutManage= new GridLayoutManager(context, 1);
        rv_tracking_logistics_list.setLayoutManager(list_layoutManage);

        initData();
    }

    private void doSth() {
        Picasso.with(context)
                .load(AppConstants.PhOTOADDRESS+iconUrl)
                .resize(200,200)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(sd_purchase_img);//加载网络图片
        if(LogisticsName==null||LogisticsCode==null){
            ToastUtil.showShort(context,"没有该订单物流信息");
            return;
        }
                if(LogisticsName!=""&&LogisticsCode!=""){
                    tv_order_number.setText(LogisticsCode);
                   //快递名称
                    if (LogisticsName.equals("yuantong") ) {
                        tv_express_name.setText("圆通速递");
                    } else if (LogisticsName.equals("shunfeng")) {
                        tv_express_name.setText("顺丰速运");
                    }else if (LogisticsName.equals("shentong")) {
                        tv_express_name.setText("申通快递");
                    }else if (LogisticsName.equals("zhongtong")) {
                        tv_express_name.setText("中通快递");
                    } else if (LogisticsName.equals("yunda")) {
                        tv_express_name.setText("韵达快递");
                    } else if (LogisticsName.equals("EMS")) {
                        tv_express_name.setText("EMS快递");
                    } else  {
                        tv_express_name.setText("其他快递");
                    }
                }
                    rl_purchaselist_item_title.setVisibility(View.VISIBLE);
                    rl_logistics_message.setVisibility(View.VISIBLE);
                    tv_empty.setVisibility(View.GONE);
                    getExpressInfo();

//        OkHttpUtils.post(AppConstants.QueryOrder)
//                .tag(this)
//                .params("UserName",AppConstants.USERNAME)
////                .params("Name", orderName)//搜索名称
//                .params("StatusID", StatusID)//搜索名称
//                .params("PageIndex", 1)//页数
//                .params("pageSize", 1)//搜索名称
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, okhttp3.Call call, Response response) {
//                        if(dialog.isShowing()){
//                            new Handler().postDelayed(new Runnable(){
//                                public void run() {
//                                    dialog.dismiss();
//                                }
//                            }, 1000);
//                        }
//                        JSONObject jsonObject;
//                        JSONArray Logistics;
//                        String jsonData = null;
//                        String Message = null;
//
//                        boolean Flag = false;
//                        Gson gson = new Gson();
//                        Gson gs =new GsonBuilder()
//                                .setPrettyPrinting()
//                                .disableHtmlEscaping()
//                                .create();
//                        try {
//                            jsonObject = new JSONObject(s);
//                            jsonData = jsonObject.getString("ReturnData");
//                            Logistics= new JSONArray(jsonData);
//                            LogisticsName =new JSONObject(Logistics.get(0).toString()).getString("LogisticsName");
//                            LogisticsCode =new JSONObject(Logistics.get(0).toString()).getString("LogisticsCode");
////                            OrderNumber =new JSONObject(Logistics.get(0).toString()).getString("OrderNumber");
//                            Message = jsonObject.getString("Message");
//                            Flag = jsonObject.getBoolean("Flag");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (!Flag) {
////                            ToastUtil.showShort(mContext, Message);
//                            return;
//                        } else {
//                            if(jsonData!=null) {
//                                if (!jsonData.equals("[]")) {
//                                    if (LogisticsName==null||LogisticsName.equals("null")||LogisticsCode==null||LogisticsCode.equals("null")){
//                                        tv_empty.setVisibility(View.VISIBLE);
//                                        rl_purchaselist_item_title.setVisibility(View.GONE);
//                                        rl_logistics_message.setVisibility(View.GONE);
//                                    }else{
//                                        tv_order_number.setText(LogisticsCode);
//                                        tv_express_name.setText(LogisticsName);
//                                        rl_purchaselist_item_title.setVisibility(View.VISIBLE);
//                                        rl_logistics_message.setVisibility(View.VISIBLE);
//                                        tv_empty.setVisibility(View.GONE);
//                                        getExpressInfo();
//                                    }
//
//                                } else {
//                                    tv_empty.setVisibility(View.VISIBLE);
//                                    rl_purchaselist_item_title.setVisibility(View.GONE);
//                                    rl_logistics_message.setVisibility(View.GONE);
//                                    return;
//                                }
//                            }else{
//                                tv_empty.setVisibility(View.VISIBLE);
//                                rl_purchaselist_item_title.setVisibility(View.GONE);
//                                rl_logistics_message.setVisibility(View.GONE);
//                            }
//                        }
//                        /*if (allOrderBeans!=null){
//                            if (PageIndex<=2){
////                                meizis= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
//                                allOrderBeans= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
//                                setData();
//                            }else{
////                                List<Meizi>  more= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
//                                List<AllOrderBean> more= gs.fromJson(jsonData, new TypeToken<List<AllOrderBean>>() {}.getType());
//                                allOrderBeans.addAll(more);
//                                for (int i = 0; i < selva.getGroupCount(); i++){
//                                    el_allorder.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
//                                }
//                                selva.notifyDataSetChanged();
//                            }
//                        }*/
//                    }
//
//
//
//                    @Override
//                    public void onBefore(BaseRequest request) {
//                        super.onBefore(request);
//                        if(!dialog.isShowing()){
//                            dialog.show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        ToastUtil.showShort(context, "数据加载超时");
//                        if(dialog.isShowing()){
//                            new Handler().postDelayed(new Runnable(){
//                                public void run() {
//                                    dialog.dismiss();
//                                }
//                            }, 1000);
//
//                        }
//                        tv_empty.setVisibility(View.VISIBLE);
//                        rv_tracking_logistics_list.setVisibility(View.GONE);
//                        rl_logistics_message.setVisibility(View.GONE);
//                    }
//                });
//
//        if(dialog.isShowing()){
//            new Handler().postDelayed(new Runnable(){
//                public void run() {
//                    dialog.dismiss();
//                }
//            }, 1000);
//
//        }
    }

    private void getExpressInfo() {
        OkHttpUtils.post(AppConstants.GetLogistics)
                .tag(this)
                .params("OrderNo",OrderNumber)
//                .params("OrderNo","17092717223026438")
                .params("Type",LogisticsName)
//                .params("Type", "yuantong")
                .params("Code", LogisticsCode)//固定物流单号
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(String s, okhttp3.Call call, Response response) {
//                                 if(dialog.isShowing()) {
//                                     dialog.dismiss();
//                                 }
                                 JSONObject jsonObject;
                                 JSONObject dataObject;
                                 String jsonData = null;
                                 String Data = null;
                                 int state = 0;
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
                                     dataObject=new JSONObject(jsonData);
                                     Data=dataObject.getString("data");
                                     state=dataObject.getInt("state");
                                     Message = jsonObject.getString("Message");
                                     Flag = jsonObject.getBoolean("Flag");
                                     Logistics= gson.fromJson(Data,new TypeToken<ArrayList<Logistics>>() {}.getType());
                                     if (Logistics!=null){
                                         for (int i = 0; i <Logistics.size(); i++) {
                                             TrackingLogisticsBean tlb=new TrackingLogisticsBean();
                                             tlb.setInfoMessage(Logistics.get(i).getContext());
                                             tlb.setInfoTime(Logistics.get(i).getTime());
                                             searchProdBeans.add(tlb);
                                         }
                                         if(trackingLogisticsAdapter==null){
                                             trackingLogisticsAdapter = new OrderTrackingLogisticsAdapter(context,searchProdBeans);
                                             rv_tracking_logistics_list.setAdapter(trackingLogisticsAdapter);//recyclerview设置适配器
//            trackingLogisticsAdapter.setNestedScrollingEnabled(false);
                                         }
                                         switch (state){
                                             case 0:
                                                 tv_logistic_state.setText("无");
                                                 break;
                                             case 1:
                                                 tv_logistic_state.setText("运输中");
                                                 break;
                                             case 2:
                                                 tv_logistic_state.setText("运输中");
                                                 break;
                                             case 3:
                                                 tv_logistic_state.setText("已签收");
                                                 break;
                                             case 4:
                                                 tv_logistic_state.setText("已签收");
                                                 break;
                                         }
                                     }
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                                 if (!Flag) {
                                     ToastUtil.showShort(context, Message);
                                     return;
                                 } else {

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
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.ll_back:
                finish();
                break;
        }
    }
}
