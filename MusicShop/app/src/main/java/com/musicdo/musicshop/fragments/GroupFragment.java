package com.musicdo.musicshop.fragments;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.controller.NearByFragmentController;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/8.
 * 版 本 ：
 * 备 注 ：
 */

public class GroupFragment extends BaseFragment {
    private RadioGroup rg_tab;//顶部选择框
    private RadioButton rb_equipment;//顶部选择框
    private NearByFragmentController controller;
    private ImageView iv_price_state;
    private boolean price_state=false;

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_group_layout, null);
        controller = NearByFragmentController.getInstance(this, R.id.id_fragment);
        controller.showFragment(0);
        iv_price_state = (ImageView) view.findViewById(R.id.iv_price_state);
        rg_tab = (RadioGroup) view.findViewById(R.id.rg_tab);
        rb_equipment = (RadioButton) view.findViewById(R.id.rb_equipment);
        iv_price_state.setBackground(mContext.getResources().getDrawable(R.mipmap.searchprod_price_enable));
        rb_equipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (price_state){
                    iv_price_state.setBackground(mContext.getResources().getDrawable(R.mipmap.searchprod_price_down));
                    price_state=false;
                }else{
                    iv_price_state.setBackground(mContext.getResources().getDrawable(R.mipmap.searchprod_price_up));
                    price_state=true;
                }
            }
        });
        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_near:
                        controller.showFragment(0);
                        iv_price_state.setBackground(mContext.getResources().getDrawable(R.mipmap.searchprod_price_enable));
                        break;
                    case R.id.rb_equipment:
                        controller.showFragment(1);
                        break;
                    case R.id.rb_sort:
                        controller.showFragment(2);
                        iv_price_state.setBackground(mContext.getResources().getDrawable(R.mipmap.searchprod_price_enable));
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }
}
