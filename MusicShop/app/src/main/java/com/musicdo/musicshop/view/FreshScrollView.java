package com.musicdo.musicshop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/6/29.
 * 版 本 ：
 * 备 注 ：
 */

public class FreshScrollView extends ScrollView implements AbsListView.OnScrollListener{
    private int downX;
    private int downY;
    private int mTouchSlop;
    private Context context;
    public FreshScrollView(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.context=context;
    }

    public FreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.context=context;
    }

    public FreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.context=context;
    }

    public FreshScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.context=context;

    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i("FreshScrollView","dispatchTouchEvent-----ACTION_UP");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i("FreshScrollView","dispatchTouchEvent-----ACTION_DOWN");
                break;
            default:break;
        }
        return super.dispatchTouchEvent(ev);//JdReFreshLayout//false时中执行onTouchEvent
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i("FreshScrollView","onTouchEvent-----ACTION_UP");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i("FreshScrollView","onTouchEvent-----ACTION_DOWN");
                break;
            default:break;
        }
        return false;//ture则自己消费掉，false，则交给父类的onTouchEvent处理.
    }*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getRawY();
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
