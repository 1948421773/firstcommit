package com.musicdo.musicshop.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.SearchProdAdapter;
import com.musicdo.musicshop.bean.SearchProdBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.GoodsCommentFragment;
import com.musicdo.musicshop.fragments.ShopIndexBaseFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomPopWindow;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.MyRecyclerView;
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

public class ShopCatetoryDetailActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private RelativeLayout rl_shopcategroylist,rl_menu;
    private EditText et_search;
    private int ShopID,categroyId,brandId;
    private String categroyName, brandName;
    private Context context;
    private String ShopIndexBaseActivity;
    private String Keyword="";
    private ImageView im_setting;
    CustomPopWindow mCustomPopWindow;
    String loginData="";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcategroydetail);
        MyApplication.getInstance().addActivity(this);
        context = this;
        ShopID = getIntent().getIntExtra("ShopID", 0);
        categroyName = getIntent().getStringExtra("categroyName");
        brandName = getIntent().getStringExtra("brandName");
        categroyId = getIntent().getIntExtra("categroyId",0);
        brandId = getIntent().getIntExtra("brandId",0);
        Keyword=getIntent().getStringExtra("keyword");
        if (Keyword==null){
            Keyword="";
        }
        ShopIndexBaseActivity = getIntent().getStringExtra("ShopIndexBaseActivity");
        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    private void initView() {
        im_setting = (ImageView) findViewById(R.id.im_setting);
        View contentView = LayoutInflater.from(this).inflate(R.layout.menu_pop,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setAnimationStyle(R.style.mypopwindow_anim_style)
                .create();

        rl_shopcategroylist = (RelativeLayout) findViewById(R.id.rl_shopcategroylist);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        ll_back.setOnClickListener(this);
        rl_shopcategroylist.setOnClickListener(this);
        rl_menu.setOnClickListener(this);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setOnClickListener(this);
        if (categroyName != null) {
            et_search.setText(categroyName);
        } else if (brandName!=null){
            et_search.setText(brandName);
        }else{
            et_search.setText(Keyword);
        }
        // 实例化碎片对象
        ShopIndexBaseFragment fragment = new ShopIndexBaseFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Bundle data = new Bundle();
        data.putInt("ShopID", ShopID);
        data.putInt("categroyId", categroyId);
        data.putString("Keyword", et_search.getText().toString().trim());
        data.putInt("brandId", brandId);
        fragment.setArguments(data);//通过Bundle向Activity中传递值
        // 把碎片添加到碎片中
        transaction.add(R.id.ll_shopcategroydetail, fragment);
        transaction.commit();



    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     */
    private void handleLogic(View contentView){
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
                        intent.putExtra("HomeFragment", "HomeFragment");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_menu:
                mCustomPopWindow.showAsDropDown(im_setting,0,0);
                break;
            case R.id.et_search:{
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("ShopIndexBaseActivity","ShopIndexBaseActivity");
                intent.putExtra("ShopID",ShopID);
                startActivity(intent);
            }
                break;
            case R.id.rl_shopcategroylist:
                Intent intent = new Intent(context, ShopCatetoryAndBrandActivity.class);
                intent.putExtra("ShopID", ShopID);
                startActivity(intent);
                break;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ShopCatetoryDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

}
