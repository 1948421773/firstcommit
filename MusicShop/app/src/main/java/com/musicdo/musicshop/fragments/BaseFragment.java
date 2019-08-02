package com.musicdo.musicshop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 名 称 ：
 *
 * 基类Fragment
 * 首页：HomeFragment
 * 分类：TypeFragment
 * 发现：CommunityFragment
 * 购物车：ShoppingCartFragment
 *  用户中心：UserFragment
 *  等等都要继承该类
 * 创建者：  张海明
 * 创建时间：2017/6/19.
 * 版 本 ：
 * 备 注 ：
 */

public abstract  class BaseFragment extends Fragment {
    protected Context mContext;
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    /*BaseFragment HomeFragment=new HomeFragment();
    BaseFragment SearchFragment=new SearchFragment();
    BaseFragment GroupFragment=new GroupFragment();
    BaseFragment ShoppingCartFragment=new ShoppingCartFragment();
    BaseFragment PersonalFragment=new PersonalFragment();*/
    /**
     * 当该类被系统创建的时候回调
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        if (childFragment == null) {
//            HomeFragment = (HomeFragment)childFragment;
//        }else if (SearchFragment == null && childFragment instanceof SearchFragment) {
//            SearchFragment = (SearchFragment)childFragment;
//        }else if (GroupFragment == null && childFragment instanceof GroupFragment) {
//            GroupFragment = (GroupFragment)childFragment;
//        }else if (ShoppingCartFragment == null && childFragment instanceof ShoppingCartFragment) {
//            ShoppingCartFragment = (ShoppingCartFragment)childFragment;
//        }else if (PersonalFragment == null && childFragment instanceof PersonalFragment) {
//            PersonalFragment = (PersonalFragment)childFragment;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    //抽象类，由孩子实现，实现不同的效果
    public abstract View initView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 当子类需要联网请求数据的时候，可以重写该方法，该方法中联网请求
     */
    public void initData() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        super.onSaveInstanceState(outState);
    }
}
