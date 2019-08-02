package com.musicdo.musicshop.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;


/**
 * 详情
 * 图文详情webview的Fragment
 */
public class GoodsDetailWebFragment extends Fragment {
    public WebView wv_detail;

    private WebSettings webSettings;
    private LayoutInflater inflater;
    private int priductId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        priductId=getArguments().getInt("prod_id",0);
        View rootView = inflater.inflate(R.layout.fragment_item_detail_web, null);
        initWebView(rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("priductId",priductId);
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null){
            priductId=savedInstanceState.getInt("priductId",0);
        }

    }
    public void initWebView(View rootView) {
//        String url = "http://m.okhqb.com/item/description/1000334264.html?fromApp=true";
        String url = AppConstants.ShowProductContent+"?ProductID="+priductId;
        wv_detail = (WebView) rootView.findViewById(R.id.wv_detail);
        wv_detail.setFocusable(false);
        wv_detail.loadUrl(url);
        webSettings = wv_detail.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setBlockNetworkImage(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setUseWideViewPort (true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        wv_detail.setWebViewClient(new GoodsDetailWebViewClient());
        wv_detail.setBackgroundColor(Color.TRANSPARENT);
        wv_detail.setVisibility(View.GONE);
    }

    private class GoodsDetailWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            wv_detail.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
