package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.CategoryFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.MySearchViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/7/25 15:19
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener,TextView.OnEditorActionListener{
    private final String TAG="SearchActivity";
    private final String SearchHistoryKey="searchkey";
    private final String SearchHistoryFile="searchhistory";
    private Context context;
    private Button bt_username_clear;
    private ImageView iv_search_history_clear,iv_back;
    private EditText et_search;
    private TextView tv_hot_blsl,tv_hot_lsgq,tv_hot_dqqh,tv_hot_gq,popularity_title,tv_hot_xtq;
    private RelativeLayout rl_search_history;
    private List<String> mHistoryKeywords = new ArrayList<String>();
    MySearchViewGroup myViewGroup;
    String ShopIndexBaseActivity;
    int ShopID;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        MyApplication.getInstance().addActivity(this);
        context=this;

        tv_hot_blsl=(TextView) findViewById(R.id.tv_hot_blsl);
        tv_hot_lsgq=(TextView) findViewById(R.id.tv_hot_lsgq);
        tv_hot_dqqh=(TextView) findViewById(R.id.tv_hot_dqqh);
        tv_hot_gq=(TextView) findViewById(R.id.tv_hot_gq);
        tv_hot_xtq=(TextView) findViewById(R.id.tv_hot_xtq);
        popularity_title=(TextView) findViewById(R.id.popularity_title);
        iv_back=(ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_hot_blsl.setOnClickListener(this);
        tv_hot_lsgq.setOnClickListener(this);
        tv_hot_dqqh.setOnClickListener(this);
        tv_hot_gq.setOnClickListener(this);
        tv_hot_xtq.setOnClickListener(this);
        popularity_title.setOnClickListener(this);
        rl_search_history=(RelativeLayout) findViewById(R.id.rl_search_history);
        iv_search_history_clear=(ImageView) findViewById(R.id.iv_search_history_clear);
        et_search=(EditText)findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(this);
        bt_username_clear=(Button)findViewById(R.id.bt_username_clear);
        bt_username_clear.setOnClickListener(this);
        iv_search_history_clear.setOnClickListener(this);

        et_search.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 0) {
                    bt_username_clear.setVisibility(View.VISIBLE);
                }else{
                    bt_username_clear.setVisibility(View.GONE);
                }
            }
        });
        initHistory();//搜索历史
    }

    /**
     *
     * 重写此方法，加上setIntent(intent);否则在onResume里面得不到intent
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0|| AppConstants.ScreenHeight==0){
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

        ShopIndexBaseActivity = getIntent().getStringExtra("ShopIndexBaseActivity");
        ShopID = getIntent().getIntExtra("ShopID",0);
    }

    private void initHistory() {
        myViewGroup= (MySearchViewGroup) this.findViewById(R.id.myviewgroup);

        String history = SpUtils.getString(context,SearchHistoryKey,SearchHistoryFile);
        if (!TextUtils.isEmpty(history)){
            List<String> list = new ArrayList<String>();
            for(Object o : history.split(",")) {
                list.add((String)o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            rl_search_history.setVisibility(View.VISIBLE);
        } else {
            rl_search_history.setVisibility(View.GONE);
        }
        for (int i = 0; i < mHistoryKeywords.size(); i++) {
            if (i<20){
                View view= LayoutInflater.from(context).inflate(R.layout.item_search_history,null);
                final TextView button = (TextView)view.findViewById(R.id.tv_search_history_item);
                button.setBackground(getResources().getDrawable(R.drawable.search_history_tv_bg));
                final String keywordString=mHistoryKeywords.get(i);
                button.setText(keywordString);
                button.setTextSize(12);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转搜索界面
                        et_search.setText(keywordString);
                        if (ShopIndexBaseActivity==null) {
                            Intent intent = new Intent(context, SearchProuductActivity.class);
                            intent.putExtra("keyword", keywordString);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                            intent.putExtra("ShopID",ShopID);
                            intent.putExtra("keyword", keywordString);
                            startActivity(intent);
                        }

                    }
                });
                myViewGroup.addView(view);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_username_clear:
                et_search.setText("");
                break;
            case R.id.popularity_title: {
                String historyString=et_search.getText().toString().trim();
                String oldText = SpUtils.getString(context,SearchHistoryKey,SearchHistoryFile);
                if (!historyString.equals("")){
                    if (!oldText.contains(historyString)) {
                        if (et_search.getText().toString().trim().length()>15){
                            historyString=historyString.substring(0,15);
                        }
                        SpUtils.putString(context,SearchHistoryKey,historyString+"," + oldText,SearchHistoryFile);
                        mHistoryKeywords.clear();
                        myViewGroup.removeAllViews();
                        initHistory();
                    }
                }
                if (ShopIndexBaseActivity==null) {
                    Intent intent = new Intent(context, SearchProuductActivity.class);
                    intent.putExtra("keyword", et_search.getText().toString().trim());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                    intent.putExtra("ShopID",ShopID);
                    intent.putExtra("keyword", et_search.getText().toString().trim());
                    startActivity(intent);
                }
            }
                break;
            case R.id.tv_hot_gq: {
                et_search.setText("钢琴");
                if (ShopIndexBaseActivity==null) {
                    Intent intent = new Intent(context, SearchProuductActivity.class);
                    intent.putExtra("keyword", "钢琴");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                    intent.putExtra("ShopID",ShopID);
                    intent.putExtra("keyword", "钢琴");
                    startActivity(intent);
                }

            }
                break;
            case R.id.tv_hot_xtq:{
                et_search.setText("小提琴");
                if (ShopIndexBaseActivity==null) {
                    Intent intent = new Intent(context, SearchProuductActivity.class);
                    intent.putExtra("keyword", "小提琴");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                    intent.putExtra("ShopID",ShopID);
                    intent.putExtra("keyword", "小提琴");
                    startActivity(intent);
                }
            }
                break;
            case R.id.tv_hot_lsgq:{
                et_search.setText("立式钢琴");
                if (ShopIndexBaseActivity==null) {
                    Intent intent = new Intent(context, SearchProuductActivity.class);
                    intent.putExtra("keyword", "立式钢琴");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                    intent.putExtra("ShopID",ShopID);
                    intent.putExtra("keyword", "立式钢琴");
                    startActivity(intent);
                }
            }
                break;
            case R.id.tv_hot_blsl: {
                et_search.setText("博兰斯勒");
                if (ShopIndexBaseActivity==null) {
                    Intent intent = new Intent(context, SearchProuductActivity.class);
                    intent.putExtra("keyword", "博兰斯勒");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                    intent.putExtra("ShopID",ShopID);
                    intent.putExtra("keyword", "博兰斯勒");
                    startActivity(intent);
                }
            }
                break;
            case R.id.tv_hot_dqqh: {
                et_search.setText("大提琴");
                if (ShopIndexBaseActivity==null) {
                    Intent intent = new Intent(context, SearchProuductActivity.class);
                    intent.putExtra("keyword", "大提琴");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                    intent.putExtra("ShopID",ShopID);
                    intent.putExtra("keyword", "大提琴");
                    startActivity(intent);
                }
            }
                break;
            case R.id.iv_search_history_clear:
                SpUtils.remove(context,SearchHistoryFile);
                mHistoryKeywords.clear();
                initHistory();
                break;
            case R.id.iv_back:
                finish();
                break;
            default: {
//                Intent intent = new Intent(context, SearchProuductActivity.class);
////                intent.putExtra("keyword",keywordString);
//                startActivity(intent);
            }
                break;

        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.et_search:
                String historyString=et_search.getText().toString().trim();
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String oldText = SpUtils.getString(context,SearchHistoryKey,SearchHistoryFile);
                    if (!historyString.equals("")){
                        if (!oldText.contains(historyString)) {
                            if (et_search.getText().toString().trim().length()>15){
                                historyString=historyString.substring(0,15);
                            }
                            SpUtils.putString(context,SearchHistoryKey,historyString+"," + oldText,SearchHistoryFile);
                            mHistoryKeywords.clear();
                            myViewGroup.removeAllViews();
                            initHistory();

                        }
                    }
                    if (ShopIndexBaseActivity==null) {
                        Intent intent = new Intent(context, SearchProuductActivity.class);
                        intent.putExtra("keyword", et_search.getText().toString().trim());
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(context, ShopCatetoryDetailActivity.class);
                        intent.putExtra("ShopID",ShopID);
                        intent.putExtra("keyword", et_search.getText().toString().trim());
                        startActivity(intent);
                    }

                    et_search.setText("");
                }
                break;
            default:
                break;
        }
        return false;
    }
}
