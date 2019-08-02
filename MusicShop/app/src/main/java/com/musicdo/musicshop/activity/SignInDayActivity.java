package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.util.DateUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CommonCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 我的签到
 * Created by Yuedu on 2018/1/31.
 */

public class SignInDayActivity extends BaseActivity implements View.OnClickListener {
    public TextView tv_title,tv_signin,tv_signin_bg;
    private Context context;
    private LinearLayout ll_back,ll_signin_bg;
    private CommonCalendarView mycalendarView;
    public OnItemClick onItemClick ;
    boolean isSignIn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinday);
        context=this;
        MyApplication.getInstance().addActivity(this);
        initView();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClick{
        void onItemClick(String date);
    }

    private void initView() {
        tv_signin = (TextView) findViewById(R.id.tv_signin);
        tv_signin_bg = (TextView) findViewById(R.id.tv_signin_bg);
        tv_signin.setOnClickListener(this);
        tv_signin_bg.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("签到");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        ll_signin_bg = (LinearLayout) findViewById(R.id.ll_signin_bg);
        ll_signin_bg.setOnClickListener(this);
        /*calendarView=(CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                ToastUtil.showShort(context,year+"年"+month+"月"+dayOfMonth+"日"+"签到成功");
            }
        });*/
        mycalendarView = (CommonCalendarView) findViewById(R.id.mycalendarView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//
        // HH:mm:ss //获取当前时间
         Date date = new Date(System.currentTimeMillis());
//        mycalendarView.setMinDate(DateUtils.stringtoDate("2018-01-01","yyyy-MM-dd"));
        mycalendarView.setMaxDate(DateUtils.stringtoDate(simpleDateFormat.format(date),"yyyy-MM-dd"));
        mycalendarView.init(null);
        String [] datas=simpleDateFormat.format(date).split("-");
        mycalendarView.getDate(datas[2]);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_signin:{
                /*Intent intent=new Intent(context,PointsRecordActivity.class);
                startActivity(intent);*/
                if (!isSignIn){
                    isSignIn=true;
                    tv_signin.setBackground(getResources().getDrawable(R.mipmap.hassignin));
                    mycalendarView.UpdateList();
                    ll_signin_bg.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ll_signin_bg.setVisibility(View.GONE);
                        }
                    },3000);
                }
            }
            break;
        }
    }
}
