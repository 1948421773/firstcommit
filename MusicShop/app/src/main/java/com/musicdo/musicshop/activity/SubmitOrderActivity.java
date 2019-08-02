package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.SubmitOrderAdapter;
import com.musicdo.musicshop.adapter.SubmitOrderExpandableListViewAdapter;
import com.musicdo.musicshop.bean.AddressBean;
import com.musicdo.musicshop.bean.CartBean;
import com.musicdo.musicshop.bean.SubmitOrder;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:提交订单界面
 * 作者：haiming on 2017/8/14 17:29
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SubmitOrderActivity extends BaseActivity implements View.OnClickListener,SubmitOrderAdapter.IncreaseCound,SubmitOrderAdapter.DecreaseCound{
    private Context context;
    private TextView tv_add_address,tv_title,tv_address_name,tv_address_phone,tv_detail_address,tv_total_num,tv_total_price_text,
    tv_score_text,tv_postage_price,tv_total_price,tv_calculate;
    private RelativeLayout rl_defaultaddress,rl_emptyaddress;
    AddressBean addressBean=new AddressBean();
    private LinearLayout ll_back;
    private int productID;
    private String Param,Count,ShopName;
    ArrayList<SubmitOrder> submitOrders=new ArrayList<>();
    private ArrayList<CartBean> CartBeans=new ArrayList<>();
    private ArrayList<CartBean> groupCarts=new ArrayList<>();
    SubmitOrder submitOrder=new SubmitOrder();
    private boolean isCartBuyNow=false;
    private RecyclerView rc_prod_buynow;
    private ExpandableListView el_car_buynow;
    SubmitOrderAdapter submitOrderAdapter;
//    private ShopcartExpandableListViewAdapter selva;
    private SubmitOrderExpandableListViewAdapter selva;
    LoadingDialog dialog;
    String loginData="";
    private long mLastClickTime = 0;
    public static final int TIME_INTERVAL = 1000;
    public String completeAddress="";
    public String myMoblie="";
    public int addressId=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitorder);
        MyApplication.getInstance().addActivity(this);
        context=this;
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        productID=getIntent().getIntExtra("ProductID",0);
        isCartBuyNow= productID==0?true:false;
        ShopName=getIntent().getStringExtra("ShopName");
        Param=getIntent().getStringExtra("Param");
        Count=getIntent().getStringExtra("Count");
        initView();
//        initData();
        getSubmitOrder();
    }

    private void initData() {
        getDefaultAddress();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

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

        addressBean=null;
        AddressBean chooseAddressBean=new AddressBean();
        chooseAddressBean=getIntent().getParcelableExtra("address");//
        addressId=getIntent().getIntExtra("addressId",0);//
        addressBean=chooseAddressBean;
        if(addressBean==null){
            getDefaultAddress();
        }else{
            completeAddress=addressBean.getReachDate()+addressBean.getAreaId()+addressBean.getUserName()+addressBean.getName();
            myMoblie=addressBean.getAddress();
            showAddress();
            rl_defaultaddress.setVisibility(View.VISIBLE);
            rl_emptyaddress.setVisibility(View.GONE);
        }

    }

    /**
     * 两种状态，显示不同ListView
     * 1.购物车立即购买
     * 2.单个商品商品详情立即购买
     */
    private void getSubmitOrder() {
        if (isCartBuyNow){//购物车立即购买
            el_car_buynow.setVisibility(View.VISIBLE);
            rc_prod_buynow.setVisibility(View.GONE);
            OkHttpUtils.post(AppConstants.BuyFromCart)
                    .tag(this)
                    .params("UserName",AppConstants.USERNAME)
                    .params("Params", Param)
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
                            try {
                                jsonObject = new JSONObject(s);
                                jsonData = jsonObject.getString("ReturnData");
                                Message = jsonObject.getString("Message");
                                Flag = jsonObject.getBoolean("Flag");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (!Flag) {
//                                ToastUtil.showShort(context, Message);
//                            rl_defaultaddress.setVisibility(View.GONE);
//                            rl_emptyaddress.setVisibility(View.VISIBLE);
                            } else {
                                CartBeans.clear();
                                groupCarts.clear();
                                int totalNumber=0;
                                double totalPrice=0;
                                CartBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<CartBean>>(){}.getType());
                                for (int i = 0; i < CartBeans.size(); i++){
                                    CartBean cb=new CartBean();
                                    cb.setShopName(CartBeans.get(i).getShopName());
                                    cb.setShopID(CartBeans.get(i).getShopID());
                                    cb.setProductDetail(CartBeans.get(i).getProductDetail());
                                    groupCarts.add(cb);
                                    for(int j = 0; j < cb.getProductDetail().size(); j++){
                                        totalNumber=totalNumber+cb.getProductDetail().get(j).getCount();
                                        totalPrice=totalPrice+cb.getProductDetail().get(j).getCount()*cb.getProductDetail().get(j).getMemberPrice();
                                    }

                                }
                                selva = new SubmitOrderExpandableListViewAdapter(groupCarts, context);
                                el_car_buynow.setAdapter(selva);
                                for (int i = 0; i < selva.getGroupCount(); i++){
                                    el_car_buynow.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
                                }
                                SpUtils.setListViewHeightBasedOnChildren(el_car_buynow);
                                //设置数量总价
                                    tv_total_num.setText(String.valueOf(totalNumber));
                                    tv_total_price_text.setText(SpUtils.doubleToString(totalPrice));
                                    tv_total_price.setText(tv_total_price_text.getText().toString().trim());
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
                            if(!dialog.isShowing()){
                                dialog.show();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ToastUtil.showShort(context, "数据加载超时");
                            rl_defaultaddress.setVisibility(View.GONE);
                            rl_emptyaddress.setVisibility(View.VISIBLE);
                            if(dialog.isShowing()){
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 1000);
                            }
                        }
                    });
        }else {//商品详情立即购买
            el_car_buynow.setVisibility(View.GONE);
            rc_prod_buynow.setVisibility(View.VISIBLE);
            OkHttpUtils.post(AppConstants.BuyNow)
                    .tag(this)
                    .params("ProductID", productID)
                    .params("Param", Param)
                    .params("Count", Count)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, okhttp3.Call call, Response response) {
                            JSONObject jsonObject;
                            String jsonData = null;
                            String Message = null;
                            boolean Flag = false;
                            Gson gson = new Gson();
                            try {
                                jsonObject = new JSONObject(s);
                                jsonData = jsonObject.getString("ReturnData");
                                Message = jsonObject.getString("Message");
                                Flag = jsonObject.getBoolean("Flag");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (!Flag) {
//                                ToastUtil.showShort(context, Message);
//                            rl_defaultaddress.setVisibility(View.GONE);
//                            rl_emptyaddress.setVisibility(View.VISIBLE);
                            } else {
                                submitOrder = null;
                                submitOrders.clear();
                                submitOrder = gson.fromJson(jsonData, SubmitOrder.class);
                                submitOrders.add(submitOrder);
                            if(submitOrderAdapter==null&&submitOrders!=null){
                                submitOrderAdapter = new SubmitOrderAdapter(context,submitOrders,ShopName);
                                rc_prod_buynow.setAdapter(submitOrderAdapter);//recyclerview设置适配器
                                submitOrderAdapter.setDecreaseCound(SubmitOrderActivity.this);
                                submitOrderAdapter.setIncreaseCound(SubmitOrderActivity.this);
                                /*submitOrderAdapter.setOnItemClickListener(new SubmitOrderAdapter.OnSearchItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
//                                        Toast.makeText(context, "点击了"+position, Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(context,ProductDetailActivity.class);
                                        intent.putExtra("prod_id",submitOrders.get(position).getID());
                                        startActivity(intent);
                                        finish();
                                    }
                                });*/
                                rc_prod_buynow.setNestedScrollingEnabled(false);
                            }
                            setSubmitOrderPriceInfo();//设置数量总价
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
                            rl_defaultaddress.setVisibility(View.GONE);
                            rl_emptyaddress.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void setSubmitOrderPriceInfo() {
        if (isCartBuyNow){

        }else{
            for(SubmitOrder so:submitOrders){
                tv_total_num.setText(String.valueOf(so.getCount()));

                tv_total_price_text.setText(SpUtils.doubleToString(so.getMemberPrice()*so.getCount()));
                tv_total_price.setText(tv_total_price_text.getText().toString());
            }
        }
    }

    private void showAddress() {
        if (addressBean!=null){
            tv_address_name.setText("收货人:"+addressBean.getName());
            tv_address_phone.setText(myMoblie);
            tv_detail_address.setText("收货地址:"+completeAddress);
        }
    }

    private void getDefaultAddress() {
        OkHttpUtils.post(AppConstants.GetUserAddress_Default)
                .tag(this)
                .params("UserID",AppConstants.USERID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Message=null;
                        boolean Flag=false;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!Flag){
//                            ToastUtil.showShort(context,Message);
                            rl_defaultaddress.setVisibility(View.GONE);
                            rl_emptyaddress.setVisibility(View.VISIBLE);
                        }else{
                            addressBean=null;
                            addressBean= gson.fromJson(jsonData, AddressBean.class);
                            completeAddress=addressBean.getProvinceName()+addressBean.getCityName()+addressBean.getCountyName()+addressBean.getAddress();
                            addressId=addressBean.getID();
                            myMoblie=addressBean.getMoblie();
                            showAddress();
                            rl_defaultaddress.setVisibility(View.VISIBLE);
                            rl_emptyaddress.setVisibility(View.GONE);
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
                        ToastUtil.showShort(context,"数据加载超时");
                        rl_defaultaddress.setVisibility(View.GONE);
                        rl_emptyaddress.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setSubmitOrder() {
        if (AppConstants.USERID==0){
            return;
        }
        if (AppConstants.USERNAME!=null){
            if (AppConstants.USERNAME.equals("")){
                return;
            }
        }else{
            return;
        }
        if (isCartBuyNow){//购物车立即购买
            if (AppConstants.USERID==0){
                return;
            }
            if (addressBean==null){
                ToastUtil.showShort(context,"收货地址未填写");
                return;
            }
            if (addressBean.getID()==0){
                return;
            }
            if (Param==null){
                return;
            }else{
                if (Param.length()==0){
                    return;
                }
            }
            OkHttpUtils.post(AppConstants.BuyByCart)
                    .tag(this)
                    .params("UserID",AppConstants.USERID)
                    .params("UserName",AppConstants.USERNAME)
                    .params("UserAddressID",addressId)
                    .params("Params",Param)
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
                            String jsonData=null;
                            String Message=null;
                            boolean Flag=false;
                            Gson gson=new Gson();
                            try {
                                jsonObject = new JSONObject(s);
                                jsonData = jsonObject.getString("ReturnData");
                                Message = jsonObject.getString("Message");
                                Flag = jsonObject.getBoolean("Flag");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (!Flag){
//                                ToastUtil.showShort(context,Message);
                            }else{
                                Intent intent=new Intent(context,PaymenyActivity.class);
                                String price=tv_total_price.getText().toString().trim();
                                String title=CartBeans.get(0).getProductDetail().get(0).getName();
                                intent.putExtra("price",price);
                                intent.putExtra("title",title);
                                intent.putExtra("OrderNumber",jsonData.substring(2,jsonData.length()-2));//此购物车订单号有"[]"需要截取获取订单号
                                startActivity(intent);
                                finish();
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
                            ToastUtil.showShort(context,"数据加载超时");
                            rl_defaultaddress.setVisibility(View.GONE);
                            rl_emptyaddress.setVisibility(View.VISIBLE);
                            if(dialog.isShowing()){
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 1000);
                            }
                        }
                    });
        }else{
        if (productID==0){
            return;
        }
            if (addressBean==null){
                return;
            }
            if (addressBean.getID()==0){
                return;
            }
        if (submitOrders==null){
            return;
        }else{
            if (submitOrders.size()==0){
                return;
            }
        }
        OkHttpUtils.post(AppConstants.SubmitOrder)
                .tag(this)
                .params("UserID",AppConstants.USERID)
                .params("UserName",AppConstants.USERNAME)
                .params("UserAddressID",addressId)
                .params("ProductID",productID)
                .params("Param",Param)
                .params("Count",submitOrders.get(0).getCount())
                //.params("Param", Param)
                //.params("Count", Count)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData=null;
                        String Message=null;
                        boolean Flag=false;
                        Gson gson=new Gson();
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!Flag){
//                            ToastUtil.showShort(context,Message);
                        }else{
                            Intent intent=new Intent(context,PaymenyActivity.class);
                            String price=tv_total_price.getText().toString().trim();
                            String title=submitOrders.get(0).getName();
                            String orderNunber=String.valueOf(submitOrders.get(0).getID());
                            intent.putExtra("price",price);
                            intent.putExtra("title",title);
                            intent.putExtra("OrderNumber",jsonData);//此传递订单号
                            startActivity(intent);
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
                        ToastUtil.showShort(context,"数据加载超时");
                        rl_defaultaddress.setVisibility(View.GONE);
                        rl_emptyaddress.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    private void initView() {
        rc_prod_buynow=(RecyclerView)findViewById(R.id.rc_prod_buynow) ;
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rc_prod_buynow.setLayoutManager(layoutManage);
        el_car_buynow=(ExpandableListView) findViewById(R.id.el_car_buynow) ;
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        rl_defaultaddress=(RelativeLayout)findViewById(R.id.rl_defaultaddress);
        rl_defaultaddress.setOnClickListener(this);
        rl_emptyaddress=(RelativeLayout)findViewById(R.id.rl_emptyaddress);
        tv_address_name=(TextView) findViewById(R.id.tv_address_name);
        tv_address_phone=(TextView) findViewById(R.id.tv_address_phone);
        tv_detail_address=(TextView) findViewById(R.id.tv_detail_address);
        tv_total_num=(TextView) findViewById(R.id.tv_total_num);
        tv_total_price_text=(TextView) findViewById(R.id.tv_total_price_text);
        tv_postage_price=(TextView) findViewById(R.id.tv_postage_price);
        tv_total_price=(TextView) findViewById(R.id.tv_total_price);
        tv_calculate=(TextView) findViewById(R.id.tv_calculate);
        tv_score_text=(TextView) findViewById(R.id.tv_score_text);
        tv_add_address=(TextView) findViewById(R.id.tv_add_address);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_title.setText("确认订单");
        tv_add_address.setOnClickListener(this);
        tv_calculate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_add_address: {
                Intent intent=new Intent(context,AddressManagerActivity.class);
                startActivity(intent);
            }
            case R.id.tv_calculate: {
                /*Intent intent=new Intent(context,AddingAddressActivity.class);
                startActivity(intent);*/
                if (addressBean!=null){
                }else{
                    ToastUtil.showShort(context,"请选择收货地址");
                    return;
                }
                if (System.currentTimeMillis() - mLastClickTime >= TIME_INTERVAL) {//避免重复点击
                    mLastClickTime = System.currentTimeMillis();
                }else{
                    return;
                }
                setSubmitOrder();
            }
                break;
            case R.id.rl_defaultaddress: {
                Intent intent=new Intent(context,AddressManagerActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }



    public static void setExpandableListViewHeightBasedOnChildren(ExpandableListView expandableListView) {
        // 获取ListView对应的Adapter
        SimpleExpandableListAdapter expandableListAdapter = (SimpleExpandableListAdapter) expandableListView.getExpandableListAdapter();
        if (expandableListAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int num=0;
        int groupCount=expandableListAdapter.getGroupCount(),count;
        num=groupCount;
        for(int i=0;i<groupCount;i++)
        {
            count=expandableListAdapter.getChildrenCount(i);
            View groupListItem=expandableListAdapter.getGroupView(i, false, null, expandableListView);
            groupListItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += groupListItem.getMeasuredHeight(); // 统计所有子项的总高度
            num=num+count;
            for (int j = 0; j<count;  j++) { // listAdapter.getCount()返回数据项的数目

                View listItem = expandableListAdapter.getChildView(i, j, false, null, expandableListView);

                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
                //          Log.i("test", ""+totalHeight);
            }
        }
        //  Log.i("test", "num:"+num);
        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        params.height = totalHeight
                + (expandableListView.getDividerHeight() * (num - 1));
        //  Log.i("test", "height:"+params.height);
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        expandableListView.setLayoutParams(params);
    }

    @Override
    public void doDecreaseCound(String OrderNumber, int groupPosition, String UserName) {
        if (groupPosition!=1) {
            submitOrders.get(0).setCount(groupPosition - 1);
            submitOrderAdapter.notifyDataSetChanged();
            tv_total_num.setText(String.valueOf(submitOrders.get(0).getCount()));
            tv_total_price_text.setText(SpUtils.doubleToString(submitOrders.get(0).getCount()*submitOrders.get(0).getMemberPrice()));
            tv_total_price.setText(tv_total_price_text.getText().toString().trim());
        }
    }

    @Override
    public void doIncreaseCound(String OrderNumber, int groupPosition, String UserName) {
        if (groupPosition!=200) {
            submitOrders.get(0).setCount(groupPosition + 1);
            submitOrderAdapter.notifyDataSetChanged();
            tv_total_num.setText(String.valueOf(submitOrders.get(0).getCount()));
            tv_total_price_text.setText(SpUtils.doubleToString(submitOrders.get(0).getCount()*submitOrders.get(0).getMemberPrice()));
            tv_total_price.setText(tv_total_price_text.getText().toString().trim());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
