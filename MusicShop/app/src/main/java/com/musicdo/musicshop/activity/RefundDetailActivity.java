package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.PersonalReturnsBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.view.CustomDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 退款详情
 * Created by Yuedu on 2017/10/17.
 */

public class RefundDetailActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private String Number="";
    private String NumberParam="";//退款详情传递参数key为：ID ，订单详情传递参数key为：Number
    private LinearLayout ll_back;
    private RelativeLayout rl_payment_way;
    private String IsDealwith="";
    private ImageView sd_purchase_img;
    private TextView tv_title,tv_intro,tv_shop_name,tv_orderdetail_state,tv_tracking_Logistics,tv_confirm_receipt,
            tv_orderdetail_number,tv_refunddetail_ordernumber,tv_orderdetail_pay_time,tv_orderdetail_create,tv_choose_parameter,tv_price,tv_number;
    String loginData="";
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    PersonalReturnsBean personalReturnsBeans=new PersonalReturnsBean();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refunddetail);
        MyApplication.getInstance().addActivity(this);
        context=this;
        Number=getIntent().getStringExtra("Number");
        IsDealwith=getIntent().getStringExtra("IsDealwith");
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
        initData();
    }

    private void initView() {
        rl_payment_way=(RelativeLayout) findViewById(R.id.rl_payment_way) ;
        ll_back=(LinearLayout) findViewById(R.id.ll_back) ;
        ll_back.setOnClickListener(this);
        tv_tracking_Logistics=(TextView) findViewById(R.id.tv_tracking_Logistics) ;
        tv_tracking_Logistics.setOnClickListener(this);
        tv_confirm_receipt=(TextView) findViewById(R.id.tv_confirm_receipt) ;
        tv_confirm_receipt.setOnClickListener(this);
        sd_purchase_img=(ImageView) findViewById(R.id.sd_purchase_img) ;
        tv_intro=(TextView) findViewById(R.id.tv_intro) ;
        tv_choose_parameter=(TextView) findViewById(R.id.tv_choose_parameter) ;
        tv_number=(TextView) findViewById(R.id.tv_number) ;
        tv_price=(TextView) findViewById(R.id.tv_number) ;
        tv_shop_name=(TextView) findViewById(R.id.tv_shop_name) ;
        tv_title=(TextView) findViewById(R.id.tv_title) ;
        tv_title.setText("退款详情");
        tv_orderdetail_state=(TextView) findViewById(R.id.tv_orderdetail_state) ;
            NumberParam="";
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
        tv_orderdetail_number=(TextView) findViewById(R.id.tv_orderdetail_number) ;
        tv_refunddetail_ordernumber=(TextView) findViewById(R.id.tv_refunddetail_ordernumber) ;
        tv_orderdetail_pay_time=(TextView) findViewById(R.id.tv_orderdetail_pay_time) ;
        tv_orderdetail_create=(TextView) findViewById(R.id.tv_orderdetail_create) ;
    }

    private void initData() {
        OkHttpUtils.post(AppConstants.GetRefundDetail)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
                .params("ID",Number)
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
//
                            personalReturnsBeans=gson.fromJson(returnData, PersonalReturnsBean.class);
                            tv_shop_name.setText(personalReturnsBeans.getShopName());
                            tv_intro.setText(personalReturnsBeans.getProductName());
                            tv_choose_parameter.setText(personalReturnsBeans.getPropertysText());
                            tv_price.setText(context.getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(personalReturnsBeans.getPrice()));
                            tv_number.setText("退款金额:"+context.getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(personalReturnsBeans.getPrice()));
                            Picasso.with(context)
                                    .load(AppConstants.PhOTOADDRESS+personalReturnsBeans.getProductImgUrl())
                                    .resize(200,200)
                                    .centerCrop()
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.mipmap.img_start_loading)
                                    .error(R.mipmap.img_load_error)
                                    .into(sd_purchase_img);//加载网络图片
                            setInfo();
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

    private void setInfo() {//发货状态、时间、订单时间和订单编号
        tv_orderdetail_number.setText(personalReturnsBeans.getReason());
        tv_refunddetail_ordernumber.setText(Number);
        tv_orderdetail_create.setText(getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(personalReturnsBeans.getPrice()));
        tv_orderdetail_pay_time.setText(personalReturnsBeans.getApplyDate());
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
