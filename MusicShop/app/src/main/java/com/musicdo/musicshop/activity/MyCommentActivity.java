package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.musicdo.musicshop.adapter.MyCommentAdapter;
import com.musicdo.musicshop.bean.MyCommentBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 我的评论
 * Created by Yuedu on 2017/11/29.
 */

public class MyCommentActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private TextView tv_title,tv_empty,tv_right_one_bt,tv_right_tow_bt;
    private Context context;
    LoadingDialog dialog;
    ArrayList<MyCommentBean> myCommentBeans=new ArrayList<>();
    MyCommentAdapter myCommentAdapter;
    private RecyclerView rc_addresss_list;
    String loginData="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycomment);
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
            myCommentBeans.clear();
            initData();
        }
    }

    private void initView() {
        /*View contentView = LayoutInflater.from(this).inflate(R.layout.menu_pop,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setAnimationStyle(R.style.mypopwindow_anim_style)
                .create();*/

        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("我的评价");
        tv_empty=(TextView)findViewById(R.id.tv_empty);
        tv_empty.setText("暂无评价");

        /*tv_right_one_bt=(TextView)findViewById(R.id.tv_right_one_bt);
        tv_right_one_bt.setBackgroundResource(R.mipmap.search_history_clear);
        tv_right_one_bt.setOnClickListener(this);*/
        /*tv_right_tow_bt=(TextView)findViewById(R.id.tv_right_tow_bt);
        tv_right_tow_bt.setBackgroundResource(R.mipmap.prodetail_setting);
        tv_right_tow_bt.setOnClickListener(this);*/

        rc_addresss_list = (RecyclerView) findViewById(R.id.rc_addresss_list);
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rc_addresss_list.setLayoutManager(layoutManage);

    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param
     */
    /*private void handleLogic(View contentView){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomPopWindow!=null){
                    mCustomPopWindow.dissmiss();
                }
                String showContent = "";
                switch (v.getId()){
                    case R.id.menu1:
                        break;
                    case R.id.menu2:{
                        Intent intent=new Intent(context,MainActivity.class);
                        intent.putExtra("ShoppingCartFragment", "");
                        intent.putExtra("specItemBeans", "");
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu3:
//                        showContent = "点击 Item菜单3";
                        break;
                    case R.id.menu4:{
                        Intent intent=new Intent(context,MainActivity.class);
                        intent.putExtra("PersonalFragment", "PersonalFragment");
                        intent.putExtra("specItemBeans", "");
                        startActivity(intent);
                    }
                    break;

                }
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
    }*/

    private void initData() {
        OkHttpUtils.post(AppConstants.GetMyComments)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)
                .params("UserName",AppConstants.USERNAME)
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
                            myCommentBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<MyCommentBean>>() {}.getType());
                            //显示乐器馆
                            if(myCommentBeans!=null){
                                myCommentAdapter = new MyCommentAdapter(context,myCommentBeans,false);
                                rc_addresss_list.setAdapter(myCommentAdapter);//recyclerview设置适配器
                                myCommentAdapter.setOnItemClickListener(new MyCommentAdapter.OnSearchItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        /*Intent intent=new Intent(context,ProductDetailActivity.class);
                                        intent.putExtra("prod_id",myCommentBeans.get(position).getProductID());
                                        startActivity(intent);*/
                                    }
                                });
                                /*productCollectAdapter.setOnItemLongClickListener(new ProductCollectAdapter.OnSearchItemLongClickListener() {
                                    @Override
                                    public void onItemLongClickListener(View view, final int index) {

                                        CustomDialog.Builder builderAll= new CustomDialog.Builder(context);
                                        builderAll.setMessage("确定从收藏夹中删除此宝贝?");
                                        builderAll.setTitle("");
                                        builderAll.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                DeleteCollectProduct(String.valueOf(productCollectBeans.get(index).getID()));
                                            }
                                        });
                                        builderAll.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builderAll.create().show();
                                    }
                                });*/
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
    /*private void DeleteCollectProduct(String id) {
        OkHttpUtils.post(AppConstants.DeleteCollectProduct)
                .tag(this)
                .params("UserID",AppConstants.USERID)
                .params("IDs",id)
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
                        if (Flag){
                            productCollectBeans.clear();
                            initData();
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
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_right_one_bt:

                break;
            case R.id.tv_right_tow_bt:
//                mCustomPopWindow.showAsDropDown(tv_right_tow_bt,0,-12);
                break;
        }
    }
}
