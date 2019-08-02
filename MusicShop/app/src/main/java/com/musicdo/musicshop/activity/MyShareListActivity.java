package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
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
import com.musicdo.musicshop.adapter.MyShareListAdapter;
import com.musicdo.musicshop.bean.SharelistBean;
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
 * Created by Yuedu on 2019/2/27.
 */

public class MyShareListActivity extends BaseActivity implements View.OnClickListener{
    public Context context;
    String loginData="";
    LoadingDialog dialog;
    private LinearLayout ll_back;
    private TextView tv_title,tv_empty,tv_right_one_bt,tv_right_tow_bt;
    private RecyclerView rc_addresss_list;
    ArrayList<SharelistBean> sharelistBeans=new ArrayList<>();
    MyShareListAdapter myShareListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelist);
        MyApplication.getInstance().addActivity(this);
        context=this;
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

        if (AppConstants.USERID!=0){
            sharelistBeans.clear();
            initData();
        }
    }

    private void initData() {
        OkHttpUtils.post(AppConstants.GetShareList)
                .tag(this)
                .params("UserID",AppConstants.USERID)
                .params("username",AppConstants.USERNAME)
                .params("pagesize",100)
                .params("pageindex",1)
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
                            tv_empty.setVisibility(View.VISIBLE);
                            rc_addresss_list.setVisibility(View.GONE);
                            return;
                        }else{
                            tv_empty.setVisibility(View.GONE);
                            rc_addresss_list.setVisibility(View.VISIBLE);
                            sharelistBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<SharelistBean>>() {}.getType());
                            //显示乐器馆
                            if(sharelistBeans!=null){
                                myShareListAdapter = new MyShareListAdapter(context,sharelistBeans,false);
                                rc_addresss_list.setAdapter(myShareListAdapter);//recyclerview设置适配器
                                myShareListAdapter.setOnItemClickListener(new MyShareListAdapter.OnSearchItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent=new Intent(context,ProductDetailActivity.class);
                                        intent.putExtra("prod_id",sharelistBeans.get(position).getShareProID());
                                        startActivity(intent);
                                    }
                                });

                                rc_addresss_list.setNestedScrollingEnabled(false);
                            }else{
                                tv_empty.setVisibility(View.VISIBLE);
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

    private void initView() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("我的分享");
        tv_right_one_bt=(TextView)findViewById(R.id.tv_right_one_bt);
        tv_right_one_bt.setBackgroundResource(R.mipmap.search_history_clear);
        tv_right_one_bt.setOnClickListener(this);
        tv_right_one_bt.setVisibility(View.GONE);
        tv_right_tow_bt=(TextView)findViewById(R.id.tv_right_tow_bt);
        tv_right_tow_bt.setBackgroundResource(R.mipmap.prodetail_setting);
        tv_right_tow_bt.setOnClickListener(this);
        tv_right_tow_bt.setVisibility(View.GONE);

        tv_empty=(TextView)findViewById(R.id.tv_empty);
        tv_empty.setText("我的分享");
        rc_addresss_list = (RecyclerView) findViewById(R.id.rc_addresss_list);
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rc_addresss_list.setLayoutManager(layoutManage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }

    }
}
