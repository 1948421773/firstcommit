package com.musicdo.musicshop.activity;

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
import com.musicdo.musicshop.fragments.BuyGoodsPiano;
import com.musicdo.musicshop.fragments.GroupDevelopment;
import com.musicdo.musicshop.fragments.PopularityTend;
import com.musicdo.musicshop.fragments.PurchasePiano;
import com.musicdo.musicshop.fragments.SearchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A on 2017/7/17.
 */

public class BuyGoodsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView purchaselist_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buygoods);
        MyApplication.getInstance().addActivity(this);
        purchaselist_title=(TextView)findViewById(R.id.purchaselist_title);


        String[] buttom_title={"钢琴","吉他","弦乐器","管乐器","键盘乐器","打击乐器"};
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_purchaselist);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_purchaselist);
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < buttom_title.length; i++) {
            tabLayout.getTabAt(i).setText(buttom_title[i]);//点击图标不改变放弃此方法，改为以下方法

        }
        tabLayout.getTabAt(0).select();
        Typeface tf=Typeface.createFromAsset(getResources().getAssets(), "fonts/FZZZHUNHJW.TTF");//根据路径得到Typeface
        purchaselist_title.setTypeface(tf);//设置字体
    }

    @Override
    protected void onResume() {
        super.onResume();

            Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
            Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
            Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    private void setupViewPager(ViewPager viewPager) {
        BuyGoodsActivity.ViewPagerAdapter adapter = new BuyGoodsActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.insertNewFragment(new BuyGoodsPiano());
        adapter.insertNewFragment(new GroupDevelopment());
        adapter.insertNewFragment(new SearchFragment());
        adapter.insertNewFragment(new GroupDevelopment());
        adapter.insertNewFragment(new PopularityTend());
        adapter.insertNewFragment(new SearchFragment());
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

    }

}
