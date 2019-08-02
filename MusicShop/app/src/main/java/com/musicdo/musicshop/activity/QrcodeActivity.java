package com.musicdo.musicshop.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.view.X5WebView;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.utils.TbsLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yuedu on 2017/11/28.
 */

public class QrcodeActivity extends BaseActivity implements View.OnClickListener {
    ProgressBar bar;
//    WebView webView;
    TextView tv_right_one_bt;
    LinearLayout ll_back;
    String url="";
    String loginData="";
    Context context;
    X5WebView mWebView;
    FrameLayout mViewParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        MyApplication.getInstance().addActivity(this);
        context=this;
        url=getIntent().getStringExtra("url");
//        url="http://www.musicdo.cn/product.aspx?id=27673";
//        initView();
        init();
    }

    private void init() {
        bar = (ProgressBar)findViewById(R.id.myProgressBar);
        tv_right_one_bt = (TextView) findViewById(R.id.tv_right_one_bt);
        tv_right_one_bt.setText("刷新");
        tv_right_one_bt.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_right_one_bt.setOnClickListener(this);
        ll_back = (LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        mViewParent=(FrameLayout)findViewById(R.id.webView1);
        mWebView = new X5WebView(this, null);
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));


//        initProgressBar();
        mWebView.loadUrl(url);
//        mWebView.loadUrl("file://" + course_url);
        mWebView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, String s) {
                mWebView.loadUrl(s);
                return true;
            }


            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView webView, String s) {
                super.onPageFinished(webView, s);
//                if (mWebView == null) {
//                    mWebView.loadUrl("file://" + course_url);
//                    return;
//                }


            }

            @Override
            public void onReceivedLoginRequest(com.tencent.smtt.sdk.WebView webView, String s, String s1, String s2) {
                super.onReceivedLoginRequest(webView, s, s1, s2);
            }

            @Override
            public void onDetectedBlankScreen(String s, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onDetectedBlankScreen(s, newProgress);
            }
        });
        mWebView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
            @Override
            public boolean onJsConfirm(com.tencent.smtt.sdk.WebView webView, String s, String s1, com.tencent.smtt.export.external.interfaces.JsResult jsResult) {
                return super.onJsConfirm(webView, s, s1, jsResult);
            }
            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;
            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view,
                                         IX5WebChromeClient.CustomViewCallback customViewCallback) {
//                mWebView.setVisibility(View.GONE);
//                video_fullView.setVisibility(View.VISIBLE);
//                video_fullView.addView(view);
//                xCustomView = view;
                callback = customViewCallback;
//                video_fullView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
            }


            @Override
            public void onHideCustomView() {
//                if (xCustomView == null) {
//                    return;
//                }
//                xCustomView.setVisibility(View.GONE);
//                video_fullView.removeView(xCustomView);
//                xCustomView = null;
//                video_fullView.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
//                video_fullView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
            }


            @Override
            public boolean onJsAlert(com.tencent.smtt.sdk.WebView webView, String s, String s1, com.tencent.smtt.export.external.interfaces.JsResult jsResult) {
                return super.onJsAlert(webView, s, s1, jsResult);
            }
        });
        com.tencent.smtt.sdk.WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(com.tencent.smtt.sdk.WebSettings.PluginState.ON_DEMAND);
        long time = System.currentTimeMillis();
//        mWebView.loadUrl("file://" + course_url);
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

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

    /*private void initView() {
        ll_back = (LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        bar = (ProgressBar)findViewById(R.id.myProgressBar);
        webView = (WebView)findViewById(R.id.myWebView);
        tv_right_one_bt = (TextView) findViewById(R.id.tv_right_one_bt);
        tv_right_one_bt.setText("刷新");
        tv_right_one_bt.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_right_one_bt.setOnClickListener(this);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        WebSettings seting=webView.getSettings();
        seting.setJavaScriptEnabled(true);//设置webview支持javascript脚本
        seting.setUseWideViewPort(true);
        seting.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                webView.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);
    }*/

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_right_one_bt:
                mWebView.reload();
                break;
            case R.id.ll_back:
                if(mWebView.canGoBack()){
                    mWebView.goBack();
                }else{
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
            if(mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }else{
                finish();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        if(mWebView != null ) {
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
