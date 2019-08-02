package com.musicdo.musicshop.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.GridItem;
import com.musicdo.musicshop.bean.HomeBrandBean;
import com.musicdo.musicshop.bean.HotBrandBean;
import com.musicdo.musicshop.bean.RecommendBrandBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.HotBrandFragment;
import com.musicdo.musicshop.fragments.OrderAllFragment;
import com.musicdo.musicshop.fragments.OrderUnDeliveryFragment;
import com.musicdo.musicshop.fragments.OrderUnpaidFragment;
import com.musicdo.musicshop.fragments.RecommendBrandFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 首页品牌跳转列表
 * Created by Yuedu on 2017/10/17.
 */

public class BrandRecommendtivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_title;
    private LinearLayout ll_back;
    private int TabIndex=0;
    private int orderNumber=0;
    private String StatusID="";
    private String orderName="";
    private int PageIndex=1;
    private int pageSize=5;
    private ArrayList<HotBrandBean> HotBrandBeans=new ArrayList();//品牌馆列表
    private ArrayList<RecommendBrandBean> RecommendBrandBeans=new ArrayList();//品牌推荐列表
    private ArrayList<HomeBrandBean> HomeBrandBeans=new ArrayList();//品牌推荐列表
    LoadingDialog dialog;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brandrecommend);
        MyApplication.getInstance().addActivity(this);
        context=this;
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("品牌");
        getCategoryHotBrand();//获取左边列表
    }

    private void getCategoryHotBrand() {
        OkHttpUtils.get(AppConstants.GetBrandClassify)
                .tag(this)
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
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        String HotBrands=null;
                        String RecommendBrands=null;
                        String HomeBrands=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            HotBrands = new JSONObject(jsonData).getString("HotBrand");//
                            RecommendBrands = new JSONObject(jsonData).getString("RecommendBrand");//
                            HomeBrands = new JSONObject(jsonData).getString("HomeBrand");//
                            message = jsonObject.getString("Message");
                            Log.i("BrandRecommendtivity", "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HotBrandBeans.clear();
                        HotBrandBeans= gson.fromJson(HotBrands, new TypeToken<ArrayList<HotBrandBean>>() {}.getType());
                        RecommendBrandBeans.clear();
                        RecommendBrandBeans= gson.fromJson(RecommendBrands, new TypeToken<ArrayList<RecommendBrandBean>>() {}.getType());
                        HomeBrandBeans.clear();
                        HomeBrandBeans= gson.fromJson(HomeBrands, new TypeToken<ArrayList<HomeBrandBean>>() {}.getType());
                        initView();
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
                    }
                });
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

        //注：回调 1
            Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
            Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //注：回调 3
            Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    private void initView() {

        TabIndex=getIntent().getIntExtra("TabIndex",0);
        orderNumber=getIntent().getIntExtra("orderNumber",0);
        StatusID=getIntent().getStringExtra("StatusID");
        orderName=getIntent().getStringExtra("orderName");
        String[] buttom_title={"推荐品牌","国内品牌","国际品牌"};
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_purchaselist);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_purchaselist);
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < buttom_title.length; i++) {
            tabLayout.getTabAt(i).setText(buttom_title[i]);//点击图标不改变放弃此方法，改为以下方法

        }
        tabLayout.getTabAt(TabIndex).select();
//        Typeface tf=Typeface.createFromAsset(getResources().getAssets(), "fonts/FZZZHUNHJW.TTF");//根据路径得到Typeface
//        tv_title.setTypeface(tf);//设置字体


    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        HotBrandFragment OF=new HotBrandFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("HotBrandBeans",HotBrandBeans);
        OF.setArguments(bundle);
        adapter.insertNewFragment(OF);

        HotBrandFragment rbf=new HotBrandFragment();
//        RecommendBrandFragment rbf=new RecommendBrandFragment();
        Bundle rbfbundle = new Bundle();
        rbfbundle.putParcelableArrayList("RecommendBrandBeans",RecommendBrandBeans);
        rbf.setArguments(rbfbundle);
        adapter.insertNewFragment(rbf);

        HotBrandFragment oud=new HotBrandFragment();
        Bundle oudbundle = new Bundle();
        oudbundle.putParcelableArrayList("HomeBrandBeans",HomeBrandBeans);
        oud.setArguments(oudbundle);
        adapter.insertNewFragment(oud);

        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void insertNewFragment(Fragment fragment) {
            mFragmentList.add(fragment);
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
        finish();
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