package com.musicdo.musicshop.fragments;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.SearchProuductActivity;
import com.musicdo.musicshop.adapter.RecommendBrandAdapter;
import com.musicdo.musicshop.bean.HomeBrandBean;
import com.musicdo.musicshop.bean.HotBrandBean;
import com.musicdo.musicshop.bean.RecommendBrandBean;

import java.util.ArrayList;

/**
 * Created by Yuedu on 2017/10/17.
 */

public class RecommendBrandFragment extends BaseFragment implements View.OnClickListener {
    View rootView;
    private RecyclerView rc_searchprod_overall;
    GridLayoutManager grid_tend_layoutManage;
    ArrayList<HotBrandBean> HotBrandBeans=new ArrayList();//品牌馆列表
    ArrayList<RecommendBrandBean> RecommendBrandBeans=new ArrayList();//品牌推荐列表
    ArrayList<HomeBrandBean> HomeBrandBeans=new ArrayList();//品牌推荐列表
    RecommendBrandAdapter recommendBrandAdapter;
    @Override
    public View initView() {
        if (rootView==null) {
            HotBrandBeans=getArguments().getParcelableArrayList("HotBrandBeans");
            RecommendBrandBeans=getArguments().getParcelableArrayList("RecommendBrandBeans");
            HomeBrandBeans=getArguments().getParcelableArrayList("HomeBrandBeans");
            rootView = LayoutInflater.from(mContext).inflate(R.layout.fm_hotbrand, null);
            rc_searchprod_overall = (RecyclerView) rootView.findViewById(R.id.rc_searchprod_overall);
            grid_tend_layoutManage= new GridLayoutManager(mContext, 3);
            rc_searchprod_overall.setLayoutManager(grid_tend_layoutManage);
        }
        return rootView;
    }

    @Override
    public void initData() {
        super.initData();
        if (RecommendBrandBeans!=null){
            if (RecommendBrandBeans.size()!=0){
                recommendBrandAdapter = new RecommendBrandAdapter(mContext,RecommendBrandBeans,false);
                rc_searchprod_overall.setAdapter(recommendBrandAdapter);//recyclerview设置适配器
                recommendBrandAdapter.setOnItemClickListener(new RecommendBrandAdapter.OnSearchItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent=new Intent(mContext, SearchProuductActivity.class);
                        intent.putExtra("keyword",RecommendBrandBeans.get(position).getName());
                        startActivity(intent);
                    }
                });
            }}
        rc_searchprod_overall.setNestedScrollingEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:

                break;
        }
    }
}
