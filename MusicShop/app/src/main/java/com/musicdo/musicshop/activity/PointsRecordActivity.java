package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.ProductLookAdapter;
import com.musicdo.musicshop.bean.Product_LookBean;
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
 * Created by Yuedu on 2018/1/24.
 */

public class PointsRecordActivity extends BaseActivity implements View.OnClickListener {
    public TextView tv_title,tv_right_tow_bt;
    private Context context;
    private LinearLayout ll_back;
    String loginData="";
    LoadingDialog dialog;
    ArrayList<Product_LookBean> product_LookBeans=new ArrayList<>();
    ProductLookAdapter productLookAdapter;
    private RecyclerView rc_addresss_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointsrecord);
        context=this;
        MyApplication.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("积分纪录");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_right_tow_bt = (TextView) findViewById(R.id.tv_right_tow_bt);
        tv_right_tow_bt.setText("筛选");
        tv_right_tow_bt.setTextSize(12);
        tv_right_tow_bt.setTextColor(getResources().getColor(R.color.home_popularity_text_color));

        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        rc_addresss_list = (RecyclerView) findViewById(R.id.rc_addresss_list);
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rc_addresss_list.setLayoutManager(layoutManage);
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
        if (AppConstants.USERID!=0){
            product_LookBeans.clear();
            initData();
        }
    }

    private void initData() {
        OkHttpUtils.post(AppConstants.GetProduct_Look)
                .tag(this)
                .params("UserID",AppConstants.USERID)
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
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        boolean Flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!Flag){
//                            //为空
//                            tv_empty.setVisibility(View.VISIBLE);
                            rc_addresss_list.setVisibility(View.GONE);
                            return;
                        }else{
//                            tv_empty.setVisibility(View.GONE);
                            rc_addresss_list.setVisibility(View.VISIBLE);
                            product_LookBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<Product_LookBean>>() {}.getType());
                            //显示乐器馆
                            if(product_LookBeans!=null){
                                productLookAdapter = new ProductLookAdapter(context,product_LookBeans);
//                                productLookAdapter.setDeteInterface(ProductLookActivity.this);
                                rc_addresss_list.setAdapter(productLookAdapter);//recyclerview设置适配器
//                                productLookAdapter.setOnItemClickListener(new ProductLookAdapter.OnSearchItemClickListener() {
//                                    @Override
//                                    public void onItemClick(View view, int position) {
////                                                Toast.makeText(context, "点击了"+position, Toast.LENGTH_SHORT).show();
//                                        /*Intent intent = new Intent(context, ShopIndexBaseActivity.class);
//                                        intent.putExtra("ShopID", product_LookBeans.get(position).getShopID());
//                                        startActivity(intent);*/
//                                    }
//                                });
                                rc_addresss_list.setNestedScrollingEnabled(false);
                            }else{
//                                tv_empty.setVisibility(View.VISIBLE);
                                rc_addresss_list.setVisibility(View.GONE);
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
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        ToastUtil.showShort(context,"数据加载超时");
                    }
                });
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
