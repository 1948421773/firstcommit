package com.musicdo.musicshop.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.OrderAllFragment;
import com.musicdo.musicshop.fragments.OrderUnDeliveryFragment;
import com.musicdo.musicshop.fragments.OrderUnEvaluationFragment;
import com.musicdo.musicshop.fragments.OrderUnReceiptFragment;
import com.musicdo.musicshop.fragments.OrderUnpaidFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单 四个Tab (全部订单,待付款,待发货,待收货,待评价)
 * Created by A on 2017/7/17.
 */

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_title;
    private LinearLayout ll_back;
    private int TabIndex=0;
    private int orderNumber=0;
    private String StatusID="";
    private String orderName="";
    private int PageIndex=1;
    private int pageSize=5;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderlist);
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
        ll_back=(LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("我的订单");
        TabIndex=getIntent().getIntExtra("TabIndex",0);
        orderNumber=getIntent().getIntExtra("orderNumber",0);
        StatusID=getIntent().getStringExtra("StatusID");
        orderName=getIntent().getStringExtra("orderName");
        String[] buttom_title={"全部订单","待付款","待发货","待收货","待评价"};
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
        adapter.insertNewFragment(new OrderAllFragment());
        adapter.insertNewFragment(new OrderUnpaidFragment());
        adapter.insertNewFragment(new OrderUnDeliveryFragment());
        adapter.insertNewFragment(new OrderUnReceiptFragment());
        adapter.insertNewFragment(new OrderUnEvaluationFragment());
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
//        overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
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
