package com.musicdo.musicshop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;



/**
 *仿京东下拉刷新
 * Created by shenminjie on 2016/12/6.
 */

public class JdRefreshLayout  {

    /**
     * headerView
     *//*
    JdRefreshHeader mHeaderView;
    private double xDistance, yDistance;
    private float xStart, yStart, xEnd, yEnd;
    private int startX;
    private int startY;
    public JdRefreshLayout(Context context) {
        super(context);
        initView();
    }

    public JdRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public JdRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    *//**
     * 初始化view
     *//*
    private void initView() {
        mHeaderView = new JdRefreshHeader(getContext());
        setHeaderView(mHeaderView);
        addPtrUIHandler(mHeaderView);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        *//*switch (ev.getAction())
                 {
                         case MotionEvent.ACTION_DOWN:
                                 startX= (int) ev.getX();
                                 startY= (int) ev.getY();
                                 break;
                         case MotionEvent.ACTION_MOVE:

                                 int dX= (int) (ev.getX()-startX);
                                 int dY= (int) (ev.getY()-startX);
                                 if(Math.abs(dX)>Math.abs(dY)){//左右滑动
                                         return true;
                                     }else {//上下滑动
                                         return false;
                                 }
                             case MotionEvent.ACTION_UP:
                                 break;
                     }
//                 return super.onInterceptTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        } else {
            return true;
    }*//*
        Log.i("JdRefreshLayout","onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i("JdRefreshLayout","onTouchEvent-----ACTION_UP");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i("JdRefreshLayout","onTouchEvent-----ACTION_DOWN");
                break;
            default:break;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("JdRefreshLayout","dispatchTouchEvent-----ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.i("JdRefreshLayout","dispatchTouchEvent-----ACTION_UP");
                break;
            default:break;
        }
//    return false;//
        return super.dispatchTouchEvent(ev);
    }*/
}
