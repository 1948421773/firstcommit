package com.musicdo.musicshop.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.ImageShowAdapter;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Yuedu on 2017/11/30.
 */

public class ImageShowActivity extends BaseActivity implements View.OnClickListener{
    ArrayList<String> imgUrls;
    private ViewPager viewpager;
    private TextView login_forgot;
    private ArrayList<View> pageview;
    private ImageShowAdapter adapter;
    private Context context;
    private RelativeLayout rl_imageshow;
    private int mTouchSlop;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageshow);
        context=this;
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
        MyApplication.getInstance().addActivity(this);
        imgUrls=getIntent().getStringArrayListExtra("imgUrls");
        index=getIntent().getIntExtra("index",0);
        rl_imageshow = (RelativeLayout) findViewById(R.id.rl_imageshow);
        login_forgot = (TextView) findViewById(R.id.login_forgot);
        if (imgUrls.get(0).contains(".mp4")){
            imgUrls.remove(0);
            index=index-1;
        }
        login_forgot.setText((index+1)+"/"+imgUrls.size());
        viewpager = (ViewPager)findViewById(R.id.vp_guide);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        if (getIntent().getStringExtra("PersonalFragment")!=null) {
            if (getIntent().getStringExtra("PersonalFragment").equals("PersonalFragment")){
                login_forgot.setVisibility(View.GONE);
                rl_imageshow.setDrawingCacheBackgroundColor(getResources().getColor(R.color.text_black));
            }
        }
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                login_forgot.setText((position+1)+"/"+imgUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setOnTouchListener(new View.OnTouchListener() {
            int touchFlag = 0;
            float x = 0, y = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchFlag = 0;
                        x = event.getX();
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float xDiff = Math.abs(event.getX() - x);
                        float yDiff = Math.abs(event.getY() - y);
                        if (xDiff < mTouchSlop && xDiff >= yDiff)
                            touchFlag = 0;
                        else
                            touchFlag = -1;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (touchFlag == 0) {
                            int currentItem = viewpager.getCurrentItem();
                           finish();
                        }
                        break;
                }
                return false;
            }
        });

        // viewpager 查找布局文件用LayoutInflater.inflate
        pageview =new ArrayList<View>();
        for (int i = 0; i <imgUrls.size() ; i++) {
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_imageshow,null);
                SimpleDraweeView im_showing=(SimpleDraweeView)view1.findViewById(R.id.im_showing);
                RelativeLayout rl_imgshow=(RelativeLayout)view1.findViewById(R.id.rl_imgshow);
//            im_showing.getLayoutParams().width=AppConstants.ScreenWidth;
//            im_showing.getLayoutParams().height=AppConstants.ScreenWidth/2;
            /*Picasso.with(context)
                    .load(Uri.parse(imgUrls.get(i)))
                    .resize(AppConstants.ScreenWidth/2,AppConstants.ScreenWidth/2)
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.img_start_loading)
                    .error(R.mipmap.img_load_error)
                    .into(im_showing);//加载网络图片*/
                im_showing.setImageURI(imgUrls.get(i));
                RelativeLayout.LayoutParams linearParamsPic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                linearParamsPic.width = AppConstants.ScreenWidth/2;
                linearParamsPic.height =AppConstants.ScreenWidth/2;
                im_showing.setLayoutParams(linearParamsPic);
//            TextView login_forgot=(TextView)view1.findViewById(R.id.login_forgot);
//            login_forgot.setText(1+"/"+imgUrls.size()+1);
                RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                linearParams.width = AppConstants.ScreenWidth;
                linearParams.height =AppConstants.ScreenWidth/2;
                rl_imgshow.setLayoutParams(linearParams);

                pageview.add(view1);
        }

        //  viewpPager数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //是从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }

        };
        //绑定适配器
        viewpager.setAdapter(mPagerAdapter);
        viewpager.setCurrentItem(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
