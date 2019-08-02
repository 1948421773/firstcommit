package com.musicdo.musicshop.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.GroupDevelopment;
import com.musicdo.musicshop.fragments.PopularityTend;
import com.musicdo.musicshop.fragments.SearchFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A on 2017/7/15.
 */

public class PopularityActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView popularity_title;
    TabLayout tabLayout;
    ViewPager viewPager;
    TendFragmentAdapter adapter;
    Context context;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popularity);
        MyApplication.getInstance().addActivity(this);
        context=this;
        popularity_title=(TextView)findViewById(R.id.popularity_title);


        int[] icons = {R.drawable.popularity_tend,R.drawable.popularity_selling,R.drawable.popularity_value};
        String[] buttom_title={"趋势榜","热卖榜","超值榜"};
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager= (ViewPager) findViewById(R.id.vp_popularity_content);
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]).setText(buttom_title[i]);//点击图标不改变放弃此方法，改为以下方法
        }
        tabLayout.getTabAt(0).select();
        Typeface tf=Typeface.createFromAsset(PopularityActivity.this.getResources().getAssets(), "fonts/FZZZHUNHJW.TTF");//根据路径得到Typeface
        popularity_title.setTypeface(tf);//设置字体
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

    private void setupViewPager(ViewPager viewPager) {
        adapter= new TendFragmentAdapter(getSupportFragmentManager());
        adapter.insertNewFragment(new PopularityTend());
        adapter.insertNewFragment(new GroupDevelopment());
        adapter.insertNewFragment(new SearchFragment());
        viewPager.setAdapter(adapter);
    }
    class TendFragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public TendFragmentAdapter(FragmentManager manager) {
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

    }
}
