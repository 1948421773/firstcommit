package com.musicdo.musicshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.GuideViewPagerAdapter;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 名 称 ：欢迎页
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/6/14.
 * 版 本 ：
 * 备 注 ：
 */

public class WelcomeGuideActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager vp;
    String filename="app";
    private GuideViewPagerAdapter adapter;
    private List<View> views;
    private Button bt_jumpover;

    // 引导页图片资源
    private static final int[] pics = { R.drawable.startpage1,
            R.drawable.startpage2, R.drawable.startpage4 };

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        MyApplication.getInstance().addActivity(this);

        views = new ArrayList<View>();
        bt_jumpover = (Button) findViewById(R.id.bt_jumpover);
        bt_jumpover.setOnClickListener(this);
        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
//            View view = LayoutInflater.from(this).inflate(pics[i], null);
            ImageView view=new ImageView(getApplicationContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setImageResource(pics[i]);
            if (i == pics.length - 1) {
                view.setTag("enter");
                view.setOnClickListener(this);
            }

            views.add(view);
        }

        vp = (ViewPager) findViewById(R.id.vp_guide);
        // 初始化adapter
        adapter = new GuideViewPagerAdapter(views);
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(new PageChangeListener());

//        initDots();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SpUtils.putBoolean(WelcomeGuideActivity.this, AppConstants.FIRST_OPEN, true,filename);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态

    }

    /**
     * 设置当前view
     *
     * @param position
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_jumpover){
            enterMainActivity();
        }else if (v.getTag().equals("enter")) {
            enterMainActivity();
        }
    }


    private void enterMainActivity() {
        SpUtils.putBoolean(WelcomeGuideActivity.this, AppConstants.FIRST_OPEN, true,filename);
        Intent intent = new Intent(WelcomeGuideActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int position) {
            // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。

        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            // arg0 :当前页面，及你点击滑动的页面
            // arg1:当前页面偏移的百分比
            // arg2:当前页面偏移的像素位置

        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
//            setCurDot(position);
        }

    }
}