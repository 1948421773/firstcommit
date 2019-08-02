package com.musicdo.musicshop.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Yuedu on 2017/11/22.
 */

public class NestedRecyclerView extends RecyclerView {
    public NestedRecyclerView(Context context) {
        super(context);
    }

    public NestedRecyclerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedRecyclerView(Context context,  AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        setMeasuredDimension(getMeasuredWidth(),getMeasuredHeight()+((MyNestedScrollParent)getParent()).getTopViewHeight());
    }
}