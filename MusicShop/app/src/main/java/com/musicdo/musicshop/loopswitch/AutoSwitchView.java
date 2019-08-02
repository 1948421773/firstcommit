package com.musicdo.musicshop.loopswitch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.musicdo.loopswitch.AutoLoopSwitchBaseAdapter;
import com.musicdo.loopswitch.AutoLoopSwitchBaseView;
import com.musicdo.musicshop.bean.HomeBannerBean;


/**
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class AutoSwitchView extends AutoLoopSwitchBaseView {
  private Context context;
  private double xDistance,yDistance  ;
  private float xStart ,yStart,xEnd ,yEnd;

  public AutoSwitchView(Context context) {
    super(context);
    this.context=context;
  }

  public AutoSwitchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context=context;
  }

  public AutoSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context=context;
  }

  public AutoSwitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    this.context=context;
  }


  @Override
  protected void onSwitch(int index, Object o) {
    HomeBannerBean model = (HomeBannerBean) o;
    if (model != null) {
      System.out.println("index="+index);
    }
  }

  @Override
  protected View getFailtView() {
    return null;
  }

  @Override
  protected long getDurtion() {
    return 3000;
  }

  @Override
  public void setAdapter(AutoLoopSwitchBaseAdapter adapter) {
    super.setAdapter(adapter);
    mHandler.sendEmptyMessage(LoopHandler.MSG_REGAIN);
  }

  /*public void setStop(AutoLoopSwitchBaseView.LoopHandler loopHandler){
    loopHandler.handleMessage(AutoLoopSwitchBaseView);
  }*/

  /*@Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()){
      case MotionEvent.ACTION_UP:
        Log.i("AutoSwitchView","onTouchEvent-----ACTION_UP");
        break;
      case MotionEvent.ACTION_DOWN:
        Log.i("AutoSwitchView","onTouchEvent-----ACTION_DOWN");
        break;
      default:break;
    }
    return false;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()){
      case MotionEvent.ACTION_DOWN:
        Log.i("AutoSwitchView","dispatchTouchEvent-----ACTION_DOWN");
//        xDistance = yDistance = 0f;
//        xStart = ev.getX();Log.i("X开始位置",String.valueOf(xStart));
//        yStart = ev.getY();Log.i("Y开始位置",String.valueOf(yStart));
        break;
      case MotionEvent.ACTION_MOVE:
        Log.i("AutoSwitchView","dispatchTouchEvent-----ACTION_MOVE");
//        xEnd = ev.getX();Log.i("X移动位置",String.valueOf(xEnd));
//        yEnd = ev.getY();Log.i("Y移动位置",String.valueOf(yEnd));
        break;
      case MotionEvent.ACTION_UP:
        Log.i("AutoSwitchView","dispatchTouchEvent-----ACTION_UP");
        break;
    }
//    xDistance = Math.abs(xEnd-xStart);
//    yDistance = Math.abs(yEnd-yStart);
//    if(xDistance<yDistance) {
//      return true;  //拦截事件向下分发
//    }
//    return true;
//    if (ev.getAction()==MotionEvent.ACTION_DOWN){

//    }
//    Log.i("AutoSwitchView","dispatchTouchEvent-----触摸到了轮播图");
//      return super.dispatchTouchEvent(ev);//触摸到该控件就往下执行

//    return false;//传递上一级的JDrefreshLayout的onTouchEvent
    return super.dispatchTouchEvent(ev);//继续往下执行
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    switch (ev.getAction()){
      case MotionEvent.ACTION_DOWN:
        Log.i("AutoSwitchView","onInterceptTouchEvent-----ACTION_UP");
//        xDistance = yDistance = 0f;
//        xStart = ev.getX();
//        yStart = ev.getY();
        break;
//      case MotionEvent.ACTION_MOVE:
//        Log.i("AutoSwitchView","onInterceptTouchEvent-----ACTION_MOVE");
//        xEnd = ev.getX();
//        yEnd = ev.getY();
//        break;
      case MotionEvent.ACTION_UP:
        Log.i("AutoSwitchView","onInterceptTouchEvent-----ACTION_UP");
        break;
    }
//    xDistance = Math.abs(xEnd-xStart);
//    yDistance = Math.abs(yEnd-yStart);
//    if(xDistance>yDistance)
//      return true;  //拦截事件向下分发
//    return false;
//    return super.dispatchTouchEvent(ev);
    return true;
  }*/

}