package com.musicdo.musicshop.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.musicdo.musicshop.fragments.GroupDevelopment;
import com.musicdo.musicshop.fragments.PopularityTend;
import com.musicdo.musicshop.fragments.SearchFragment;

import java.util.ArrayList;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/8.
 * 版 本 ：
 * 备 注 ：
 */

public class NearByFragmentController {
    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;

    private static NearByFragmentController controller;

    public static NearByFragmentController getInstance(Fragment parentFragment, int containerId) {
        if (controller == null) {
            controller = new NearByFragmentController(parentFragment, containerId);
        }
        return controller;
    }

    private NearByFragmentController(Fragment fragment, int containerId) {
        this.containerId = containerId;
        //fragment嵌套fragment，调用getChildFragmentManager
        fm = fragment.getChildFragmentManager();

        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new GroupDevelopment());
        fragments.add(new SearchFragment());
        fragments.add(new PopularityTend());

        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : fragments) {
            ft.add(containerId, fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments() {
        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : fragments) {
            if(fragment != null) {
                ft.hide(fragment);
            }
        }
//        ft.commit();
        ft.commitAllowingStateLoss();
    }
    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
}
