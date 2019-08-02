package com.musicdo.musicshop.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.GroupDevelopmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/8.
 * 版 本 ：
 * 备 注 ：
 */

public class GroupDevelopment extends BaseFragment {
    RecyclerView rv_group_develop_list;
    GroupDevelopmentAdapter developmentAdapter;
    List<Integer> data = new ArrayList<>();
    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_develop_list, null);
        rv_group_develop_list = (RecyclerView) view.findViewById(R.id.rv_group_develop_list );
        developmentAdapter= new GroupDevelopmentAdapter(mContext ,getListViewData());
        developmentAdapter.setOnItemClickListener(new GroupDevelopmentAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
//                Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
            }
        });
        rv_group_develop_list.setLayoutManager(new LinearLayoutManager(mContext));
        rv_group_develop_list.setAdapter(developmentAdapter);
        return view;
    }

    private List<Integer> getListViewData() {

        for(int i = 0; i <10; i ++) {
            data.add(i);
        }

        return data;
    }
}
