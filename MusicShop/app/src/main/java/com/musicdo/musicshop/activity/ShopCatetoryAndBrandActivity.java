package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.MyItemClickAdapter;
import com.musicdo.musicshop.adapter.ShopCatetoryAndBrandAdapter;
import com.musicdo.musicshop.bean.CategoryLevelOneBean;
import com.musicdo.musicshop.bean.ShopCatetoryAndBrandBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/11/17.
 */

public class ShopCatetoryAndBrandActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private TextView tv_title,tv_shop_newprod,tv_shop_hotsell;
    private Context context;
    int ShopID;
    String ShopIndexBaseActivity;
    private ShopCatetoryAndBrandAdapter mAdapter;
    private RecyclerView rc_grid_education,rc_shop_hotsell;
    ShopCatetoryAndBrandBean shopListBeans=new ShopCatetoryAndBrandBean();
    String loginData="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcatetortandbrand);
        MyApplication.getInstance().addActivity(this);
        ShopID=getIntent().getIntExtra("ShopID",0);
        context=this;
        initView();
        initData();
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
        if (ShopID == 0) {
            ToastUtil.showShort(context, "数据加载超时");
            return;
        }
        OkHttpUtils.get(AppConstants.GetShopCatetoryAndBrand)
                .tag(this)
                .params("ShopID", ShopID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            commentObject = new JSONObject(jsonData);
                            comment = commentObject.getString("Data");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //评论Data不是array
                        shopListBeans = gson.fromJson(jsonData, ShopCatetoryAndBrandBean.class);
                        if (shopListBeans != null) {
                            if (shopListBeans.getBrand() != null) {
                                ArrayList<CategoryLevelOneBean> categortBeans=new ArrayList<CategoryLevelOneBean>();
                                categortBeans.addAll(shopListBeans.getBrand());
                                mAdapter = new ShopCatetoryAndBrandAdapter(context, categortBeans);
                                rc_grid_education.setAdapter(mAdapter);//recyclerview设置适配器
                                mAdapter.setOnItemClickListener(new ShopCatetoryAndBrandAdapter.OnItemClickLitener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                                        intent.putExtra("ShopID",ShopID);
                                        intent.putExtra("categroyName",shopListBeans.getBrand().get(position).getName());
                                        intent.putExtra("brandId", shopListBeans.getBrand().get(position).getID());
                                        startActivity(intent);
                                    }
                                });
                                rc_grid_education.setNestedScrollingEnabled(false);
                                tv_shop_newprod.setVisibility(View.VISIBLE);
                                rc_grid_education.setVisibility(View.VISIBLE);
                            } else {
                                tv_shop_newprod.setVisibility(View.GONE);
                                rc_grid_education.setVisibility(View.GONE);
                            }

                            if (shopListBeans.getCategory() != null) {
                                ArrayList<CategoryLevelOneBean> brandBeans=new ArrayList<CategoryLevelOneBean>();
                                brandBeans.addAll(shopListBeans.getCategory());
                                mAdapter = new ShopCatetoryAndBrandAdapter(context,brandBeans);
                                rc_shop_hotsell.setAdapter(mAdapter);//recyclerview设置适配器
                                mAdapter.setOnItemClickListener(new ShopCatetoryAndBrandAdapter.OnItemClickLitener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                                        intent.putExtra("ShopID",ShopID);
                                        intent.putExtra("brandName",shopListBeans.getCategory().get(position).getName());
                                        intent.putExtra("categroyId", shopListBeans.getCategory().get(position).getID());
                                        startActivity(intent);
                                    }
                                });
                                rc_shop_hotsell.setNestedScrollingEnabled(false);
                                tv_shop_hotsell.setVisibility(View.VISIBLE);
                                rc_shop_hotsell.setVisibility(View.VISIBLE);
                            } else {
                                tv_shop_hotsell.setVisibility(View.GONE);
                                rc_shop_hotsell.setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(activity,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context, "数据加载超时");
                    }
                });
    }

    private void initView() {
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("店铺分类");
        tv_shop_newprod=(TextView)findViewById(R.id.tv_shop_newprod);
        tv_shop_hotsell=(TextView)findViewById(R.id.tv_shop_hotsell);
        rc_shop_hotsell = (RecyclerView) findViewById(R.id.rc_shop_hotsell );
        rc_shop_hotsell.setLayoutManager(new GridLayoutManager(context,2));

        rc_grid_education = (RecyclerView) findViewById(R.id.rc_grid_education );
        rc_grid_education.setLayoutManager(new GridLayoutManager(context,2));
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
