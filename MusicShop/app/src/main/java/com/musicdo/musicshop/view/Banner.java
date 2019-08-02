package com.musicdo.musicshop.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ImageShowActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.adapter.BannerViewAdapter;
import com.musicdo.musicshop.fragments.GoodsInfoFragment;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

import static com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder.FRAME_OPTION;

/**
 * Created by Yuedu on 2018/9/6.
 */

public class Banner extends RelativeLayout
{
    private ViewPager viewPager;
    private final int UPTATE_VIEWPAGER = 100;
    //图片默认时间间隔
    private int imgDelyed = 2000;
    //每个位置默认时间间隔，因为有视频的原因
    private int delyedTime = 2000;
    //默认显示位置
    private int autoCurrIndex = 0;
    //是否自动播放
    private boolean isAutoPlay = false;
    private Time time;
    Context context;
    private List<BannerModel> bannerModels;
    private List<String> list;
    private List<View> views;
    private BannerViewAdapter mAdapter;
    int index=0;
    int width=0;
    int height=0;
    LinearLayout mLinearLayout;
    private static OnClickMyTextView onClickMyTextView;

    public Banner(Context context)
    {
        super(context);
        init();
    }

    public Banner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Banner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    //接口回调
    public interface OnClickMyTextView{
        public void myTextViewClick(int id);
    }
    public static void setOnClickMyTextView(OnClickMyTextView onClickMy) {
        onClickMyTextView = onClickMy;
    }

    private void init(){
        time = new Time();
//        viewPager = new ViewPager(getContext());
//        LinearLayout.LayoutParams vp_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        viewPager.setLayoutParams(vp_param);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view=inflater.inflate(R.layout.item_goodsinfofragment_viewpager,null);
        viewPager=(ViewPager)view.findViewById(R.id.main_viewpager) ;
        mLinearLayout = (LinearLayout) view.findViewById(R.id.main_linear);
        this.addView(view);
    }

    public void setwh(int bannerwidth,int bannerheight){
        this.width=bannerwidth;
        this.height=bannerheight;
    }
    public void setDataList(List<String> dataList,Context context){
        this.context=context;
        list=dataList;
        if (dataList == null){
            dataList = new ArrayList<>();
        }
        //用于显示的数组
        if (views == null)
        {
            views = new ArrayList<>();
        }else {
            views.clear();
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RequestOptions options = new RequestOptions();
        options.fitCenter();
        //数据大于一条，才可以循环
        if (dataList.size() > 1)
        {
            autoCurrIndex = 0;
            //循环数组，将首位各加一条数据
            View view;
            for (int i = 0; i < dataList.size(); i++)
            {
                final int index=i;
                String url = dataList.get(i);


                if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4"))
                {
//                    MVideoView videoView = new MVideoView(getContext());
//                    videoView.setLayoutParams(lp);
//                    videoView.setVideoURI(Uri.parse(url));
//                    videoView.pause();
                    View root = LayoutInflater.from(context).inflate(R.layout.layout, null);
                    final JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) root.findViewById(R.id.videoplayer);
                    jzVideoPlayerStandard.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                    loadVideoScreenshot(context,url,jzVideoPlayerStandard.thumbImageView,1000);
                    views.add(root);
                } else
                {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(getContext()).load(url).apply(options).into(imageView);
//                    onClickMyTextView.myTextViewClick(i);
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getContext(),ImageShowActivity.class);
                            intent.putStringArrayListExtra("imgUrls", (ArrayList<String>) list);
                            intent.putExtra("index",index);
                            getContext().startActivity(intent);
                        }
                    });
                    views.add(imageView);
                }
                //创建底部指示器(小圆点)
                view = new View(getContext());
                view.setBackgroundResource(R.drawable.loopswitch_page);
                view.setEnabled(false);
                //设置宽高
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
                //设置间隔
                if (dataList.get(0) != dataList.get(i)) {
                    layoutParams.leftMargin = 20;
                }
                //添加到LinearLayout
                mLinearLayout.addView(view, layoutParams);
            }
            mLinearLayout.getChildAt(0).setEnabled(true);
        }else if (dataList.size() == 1){
            autoCurrIndex = 0;
            String url = dataList.get(0);
            if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4"))
            {
//                MVideoView videoView = new MVideoView(getContext());
//                videoView.setLayoutParams(lp);
//                videoView.setVideoURI(Uri.parse(url));
//                videoView.pause();
//                //监听视频播放完的代码
//                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                    @Override
//                    public void onCompletion(MediaPlayer mPlayer) {
//                        mPlayer.start();
//                        mPlayer.setLooping(true);
//
//                    }
//                });
//                views.add(videoView);
                View root = LayoutInflater.from(context).inflate(R.layout.layout, null);
                JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) root.findViewById(R.id.videoplayer);
                jzVideoPlayerStandard.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                jzVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"));
                loadVideoScreenshot(context,url,jzVideoPlayerStandard.thumbImageView,1000);
                views.add(root);
            } else
            {
//                View inflate = inflate(getContext(), R.layout.reg_view, this);
//                inflate.findViewById(R.id.text);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(getContext()).load(url).apply(options).into(imageView);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(),ImageShowActivity.class);
                        intent.putStringArrayListExtra("imgUrls", (ArrayList<String>) list);
                        intent.putExtra("index",index);
                        getContext().startActivity(intent);
                    }
                });
                views.add(imageView);
            }
        }
    }


    public void setImgDelyed(int imgDelyed){
        this.imgDelyed = imgDelyed;
    }

    public void startBanner()
    {
        if (views!=null){
            if (views.size()!=0){
                mAdapter = new BannerViewAdapter(views);
                viewPager.setAdapter(mAdapter);
                viewPager.setOffscreenPageLimit(1);
                viewPager.setCurrentItem(autoCurrIndex);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                {

                    int CurrentPosition=0;
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                    {
                        Log.d("TAG","第"+position+"个"+positionOffset+"----"+positionOffsetPixels);
//                        if(position == 1){
//                            index = list.size()-1;
//                        }else if(position == list.size() - 1){
//                            index = 0;
//                        }
//                        index = position-1;
//                        int selectindex=0;
//                        if (positionOffset!=0){
//
//                        }
//                        System.out.println("停----"+position+"--"+index);
//                        //滑动视频暂停
//                        if (MimeTypeMap.getFileExtensionFromUrl(list.get(index)).equals("mp4")){
//                            MVideoView videoView = (MVideoView) views.get(position);
//                            videoView.pause();
//
//                        }
                    }

                    @Override
                    public void onPageSelected(int position)
                    {
                        Log.d("TAG","当前位置:"+position);
//                        //当前位置
                        autoCurrIndex = position;
//                        getDelayedTime(position);

                        for (int i = 0; i < views.size(); i++) {
                            //选中的页面改变小圆点为选中状态，反之为未选中
                            if (position == i) {
                                mLinearLayout.getChildAt(position).setEnabled(true);
                            } else {
                                mLinearLayout.getChildAt(i).setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state)
                    {
                        Log.d("TAG","移动状态:"+state);
                        //移除自动计时
                        mHandler.removeCallbacks(runnable);
                        //停止滑动时，重新自动倒计时
                        if (state == 0 && isAutoPlay && views.size() > 1){
                            View view1 = views.get(autoCurrIndex);
                            if (view1 instanceof VideoView){
                                final VideoView videoView = (VideoView) view1;
                                int current = videoView.getCurrentPosition();
                                int duration = videoView.getDuration();
                                delyedTime = duration - current;
                                //某些时候，某些视频，获取的时间无效，就延时10秒，重新获取
                                if (delyedTime <= 0){
                                    time.getDelyedTime(videoView,runnable);
                                    mHandler.postDelayed(time,imgDelyed);
                                }else {
                                    mHandler.postDelayed(runnable,delyedTime);
                                }
                            }else {
                                delyedTime = imgDelyed;
                                mHandler.postDelayed(runnable,delyedTime);
                            }
                        }
                        //ViewPager跳转
                        int pageIndex = autoCurrIndex;
                        if(autoCurrIndex == 0){
                            pageIndex = views.size()-2;
                        }else if(autoCurrIndex == views.size() - 1){
                            pageIndex = 1;
                        }
                        if (pageIndex != autoCurrIndex) {
                            //无滑动动画，直接跳转
//                            viewPager.setCurrentItem(pageIndex, false);
                        }
                        if (state==ViewPager.SCROLL_STATE_IDLE){//滑动结束
                            if(autoCurrIndex<list.size()){
                                if (MimeTypeMap.getFileExtensionFromUrl(list.get(autoCurrIndex)).equals("mp4")){
                                    isAutoPlay = false;//视频播放中不自动轮播
                                    System.out.println("播放"+autoCurrIndex+isAutoPlay);
//                                    JZVideoPlayer.goOnPlayOnResume();

                                }
                            }
//
                        }else if (state==ViewPager.SCROLL_STATE_DRAGGING){//正在滑动
                            if(autoCurrIndex<list.size()){
                                if (MimeTypeMap.getFileExtensionFromUrl(list.get(autoCurrIndex)).equals("mp4")){
                                    System.out.println("暂停"+autoCurrIndex+isAutoPlay);
                                  JZVideoPlayer.clearSavedProgress(context, null);
                                    JZVideoPlayer.goOnPlayOnPause();
                                }
                            }
                        }

                    }
                });
            }
        }
    }
    //开启自动循环
    public void startAutoPlay(){
        if (!MimeTypeMap.getFileExtensionFromUrl(list.get(autoCurrIndex)).equals("mp4")){
            isAutoPlay = true;
            if (views.size() > 1){
                getDelayedTime(autoCurrIndex);
                if (delyedTime <= 0){
                    mHandler.postDelayed(time,imgDelyed);
                }else {
                    mHandler.postDelayed(runnable,delyedTime);
                }
            }
        }else{
            isAutoPlay = false;
        }

    }

    /**
     * 发消息，进行循环
     */
    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            mHandler.sendEmptyMessage(UPTATE_VIEWPAGER);
        }
    };

    /**
     * 这个类，恩，获取视频长度，以及已经播放的时间
     */
    private class Time implements Runnable{

        private VideoView videoView;
        private Runnable runnable;

        public void getDelyedTime(VideoView videoView,Runnable runnable){
            this.videoView = videoView;
            this.runnable = runnable;
        }
        @Override
        public void run()
        {
            int current = videoView.getCurrentPosition();
            int duration = videoView.getDuration();
            int delyedTime = duration - current;
            mHandler.postDelayed(runnable,delyedTime);
        }
    }

    //接受消息实现轮播
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    Log.d("TAG","翻页");
                    viewPager.setCurrentItem(autoCurrIndex+1);
                    break;
            }
        }
    };

    private class BannerModel{
        public String url;
        public int playTime;
        public int type = 0;
    }

    /**
     * 获取delyedTime
     * @param position 当前位置
     */
    private void getDelayedTime(int position){
        View view1 = views.get(position);
        if (view1 instanceof VideoView){
            VideoView videoView = (VideoView) view1;
            videoView.start();
            videoView.seekTo(0);
            delyedTime = videoView.getDuration();
            time.getDelyedTime(videoView,runnable);
        }else {
            delyedTime = imgDelyed;
        }
    }

    public void dataChange(List<String> list){
        if (list != null && list.size()>0)
        {
            //改变资源时要重新开启循环，否则会把视频的时长赋给图片，或者相反
            //因为delyedTime也要改变，所以要重新获取delyedTime
            mHandler.removeCallbacks(runnable);
            setDataList(list,context);
            mAdapter.setDataList(views);
            mAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(autoCurrIndex,false);
            //开启循环
            if (isAutoPlay && views.size() > 1){
                getDelayedTime(autoCurrIndex);
                if (delyedTime <= 0){
                    mHandler.postDelayed(time,imgDelyed);
                }else {
                    mHandler.postDelayed(runnable,delyedTime);
                }
            }
        }
    }

    public void destroy(){
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        time = null;
        runnable = null;
        views.clear();
        views = null;
        viewPager = null;
        mAdapter = null;
    }

    public static void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }

            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }
}