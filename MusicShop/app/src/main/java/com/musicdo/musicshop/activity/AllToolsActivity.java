package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.PersonalAdapter;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuedu on 2018/1/23.
 * haiming
 * 全部必备工具
 */

public class AllToolsActivity extends BaseActivity implements View.OnClickListener{
    public TextView tv_title;
    private Context context;
    private LinearLayout ll_back;
    RecyclerView function_recyclerview;
    private GridLayoutManager function_gridLayoutManager;
    PersonalAdapter personalAdapter;
    private List<Drawable> functionDatas;
    private List<String> functionDataTitle;
    int[] function_ItemIcon={R.mipmap.personal_favorites_all,R.mipmap.personal_attention_all,R.mipmap.personal_footprints_all,R.mipmap.personal_share_all,R.mipmap.personal_group_all,
            R.mipmap.personal_comment_all,R.mipmap.personal_address_all,R.mipmap.personal_service_all,R.mipmap.personal_pointsm_all,R.mipmap.personal_gold_all,R.mipmap.personal_signin_all};
    String[] function_ItemTitle={"收藏夹","店铺关注","我的足迹","我的分享","我的圈子","我的评论","我的地址","联系客服","积分商城","我的金币","我的签到"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_function);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("必备工具");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        makeFunctionData();
        function_recyclerview = (RecyclerView) findViewById(R.id.rv_home_function_list );
        personalAdapter= new PersonalAdapter(context , functionDatas,functionDataTitle);
        personalAdapter.setOnItemClickListener(new PersonalAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                switch (position){
                    case 0:{//收藏夹
                        if (AppConstants.USERID==0){
                            return;
                        }
                        Intent intent=new Intent(context,ProductCollectListActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 1:{//店铺关注
                        if (AppConstants.USERID==0){
                            return;
                        }
                        Intent intent=new Intent(context,ShopCollectListActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 2:{//足迹
                        if (AppConstants.USERID==0){
                            return;
                        }
                        Intent intent=new Intent(context,ProductLookActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 3:{//分享

                    }
                    break;
                    case 4:{//圈子

                    }
                    break;
                    case 5:{//我的评论
                        if (AppConstants.USERID==0){
                            return;
                        }
                        Intent intent=new Intent(context,MyCommentActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 6:{//地址
                        Intent intent=new Intent(context,AddressManagerActivity.class);
                        intent.putExtra("PersinalCenterActivity","PersonalFragment");
                        startActivity(intent);
                    }
                    break;
                    case 7:{//联系客服
                        ToastUtil.showShort(context,"功能正在开发中");
                    }
                    break;
                    case 8:{//积分商城
                        Intent intent=new Intent(context,PointsMallActivity.class);
                        startActivity(intent);

                    }
                    break;
                    case 9:{//我的金币
//                        ToastUtil.showShort(context,"功能暂未开放");
                        Intent intent=new Intent(context,MyGoldCoinActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 10:{//我的签到
                        ToastUtil.showShort(context,"功能暂未开放");
                        Intent intent=new Intent(context,SignInDayActivity.class);
                        startActivity(intent);
                    }
                    break;
                }
            }
        });
        function_gridLayoutManager=new GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false);//设置为一个3列的纵向网格布局
        function_recyclerview.setLayoutManager(function_gridLayoutManager);
        function_recyclerview.setAdapter(personalAdapter);
    }

    private void makeFunctionData() {
        functionDatas=new ArrayList<>();
        for ( int i=0; i < function_ItemIcon.length; i++) {
            functionDatas.add(getResources().getDrawable(function_ItemIcon[i]));
        }
        functionDataTitle=new ArrayList<>();
        for ( int i=0; i < function_ItemTitle.length; i++) {
            functionDataTitle.add(function_ItemTitle[i]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back: {
                finish();
            }
        }
    }
}
