package com.musicdo.musicshop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.adapter.GoodsConfigAdapter;
import com.musicdo.musicshop.bean.GoodsConfigBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 图文详情里的规格参数的Fragment
 */
public class GoodsConfigFragment extends Fragment {
    public ProductDetailActivity activity;
    public ListView lv_config;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ProductDetailActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_config, null);
        lv_config = (ListView) view.findViewById(R.id.lv_config);
        lv_config.setFocusable(false);

        List<GoodsConfigBean> data = new ArrayList<>();
        data.add(new GoodsConfigBean("MB-AV自编9527", "编号"));
        data.add(new GoodsConfigBean("宝石蓝，翡翠绿，土豪金，玫瑰红好", "颜色"));
        data.add(new GoodsConfigBean("31寸，32寸，33寸，42寸，43寸，44寸", "尺寸"));
        data.add(new GoodsConfigBean("28888-80000000", "价格"));
        lv_config.setAdapter(new GoodsConfigAdapter(activity, data));
        return view;
    }
}
