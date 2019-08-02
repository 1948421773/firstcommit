package com.musicdo.musicshop.view;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 *
 * Created by Yuedu on 2017/11/22.
 */

public class MyNestedScrollParent extends LinearLayout implements NestedScrollingParent {
    private String Tag = "MyNestedScrollParent";
    private View topView ;
    private View centerView;
    private View contentView;
    private MyNestedScrollChild nsc ;
    private NestedScrollingParentHelper mParentHelper;
    private int imgHeight;
    private int tvHeight;
    private int mLastTouchY;
    public boolean topShow;

    public MyNestedScrollParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyNestedScrollParent(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topView = getChildAt(0);
        centerView =  getChildAt(1);
        contentView = getChildAt(2);
        topView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(imgHeight<=0){
                    imgHeight =  topView.getMeasuredHeight();
                    Log.i(Tag,"imgHeight:"+imgHeight+",tvHeight:"+tvHeight);
                }
            }
        });
        centerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(tvHeight<=0){
                    tvHeight =  centerView.getMeasuredHeight();
                    Log.i(Tag,"imgHeight:"+imgHeight+",tvHeight:"+tvHeight);
                }
            }
        });



    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), topView.getMeasuredHeight() + centerView.getMeasuredHeight() + contentView.getMeasuredHeight());

    }
    public int  getTopViewHeight(){
        Log.i(Tag,"getTopViewHeight--"+topView.getMeasuredHeight());
        return topView.getMeasuredHeight();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.i(Tag,"onStartNestedScroll--"+"child:"+child+",target:"+target+",nestedScrollAxes:"+nestedScrollAxes);
        return true;
    }
    private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);

    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        Log.i(Tag,"onNestedScrollAccepted"+"child:"+child+",target:"+target+",nestedScrollAxes:"+nestedScrollAxes);
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.i(Tag,"onStopNestedScroll--target:"+target);
        mParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(Tag,"onNestedScroll--"+"target:"+target+",dxConsumed"+dxConsumed+",dyConsumed:"+dyConsumed
                +",dxUnconsumed:"+dxUnconsumed+",dyUnconsumed:"+dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if(showImg(dy)||hideImg(dy)){//如果父亲自己要滑动，则拦截
            consumed[1]=dy;
            scrollBy(0,dy);
            Log.i("onNestedPreScroll","Parent滑动："+dy);
        }
        Log.i(Tag,"onNestedPreScroll--getScrollY():"+getScrollY()+",dx:"+dx+",dy:"+dy+",consumed:"+consumed);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(Tag,"onNestedFling--target:"+target);
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.i(Tag,"onNestedPreFling--target:"+target);
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.i(Tag,"getNestedScrollAxes");
        return 0;
    }



    @Override
    public void scrollTo(int x, int y) {
        if(y<0){
            y=0;
        }
        if(y>imgHeight){
            y=imgHeight;
        }

        super.scrollTo(x, y);
    }

    /**
     下拉的时候是否要向下滑动显示图片
     */
    public boolean showImg(int dy){
        if(dy<0){

//            if(getScrollY()>0&&nsc.getScrollY()==0){//如果parent外框，还可以往上滑动
            if(getScrollY()>0){

                return true;
            }
        }


        return false;
    }



    /**
     * 上拉的时候，是否要向上滑动，隐藏图片
     * @return
     */
    public boolean hideImg(int dy){
        if(dy>0){
            if(getScrollY()<imgHeight){//如果parent外框，还可以往下滑动
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            return true;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        return super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i("aaa","getY():getRawY:"+event.getRawY());

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastTouchY = (int) (event.getRawY() + 0.5f);

                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) (event.getRawY() + 0.5f);
                int dy = mLastTouchY - y;
                mLastTouchY = y;
                if(showImg(dy)||hideImg(dy)){//如果父亲自己要滑动
                    scrollBy(0,dy);
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }


        return super.dispatchTouchEvent(event);
    }

}