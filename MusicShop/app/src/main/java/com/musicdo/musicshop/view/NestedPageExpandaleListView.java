package com.musicdo.musicshop.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.AllOrderExpandableListViewAdapter;

/**
 * 分页ExpandableListView
 * Created by Yuedu on 2017/11/9.
 */

public class NestedPageExpandaleListView extends ExpandableListView implements AbsListView.OnScrollListener {
    public Boolean canLoad = false; // 是否能够加载数据
    public OnPageLoadListener listener;// 分页侦听事件
    public int currentPageIndex = 0; // 记录页索引
    public int pageSize = 0; // 每页显示项目数
    public LinearLayout footerLayout;// 页脚
    public String loadMessage = "正在加载...."; // 进度条提示消息

    public NestedPageExpandaleListView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                View.MeasureSpec.AT_MOST);

        //将重新计算的高度传递回去
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     * 设置第页显示项目数
     *
     * @param pageSize
     *            第页显示项目数
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        this.setOnScrollListener(this);
        this.BuildProgressBar();
        // 必须在setAdapter()方法之前构建进度条并添加到页脚
        super.setAdapter(adapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.canLoad && (scrollState == OnScrollListener.SCROLL_STATE_IDLE)) {
            // 加载数据
            if (listener != null) {
                this.currentPageIndex++;
                listener.onPageChanging(this.pageSize, currentPageIndex);
//  System.out.println("----####------this.pageSize-------"+this.pageSize);
//  System.out.println("----####------this.currentPageIndex-------"+currentPageIndex);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.canLoad = true;
        if (this.listener == null) {
            return;
        }
        if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
            this.canLoad = this.listener.canLoadData();
        }
    }

    /**
     * 创建页脚显示进度条,必须在setAdapter()方法之前调用.
     *
     * @return
     */
    private void BuildProgressBar() {
        if (this.getFooterViewsCount() != 0) {
            return;
        }
        footerLayout = new LinearLayout(this.getContext());
        footerLayout.setGravity(Gravity.CENTER);
        footerLayout.setPadding(0, 0, 0, 0);
        footerLayout.setOrientation(LinearLayout.HORIZONTAL);
        ProgressBar bar = new ProgressBar(this.getContext() ,null, R.style.mProgress_circle);
        footerLayout.addView(bar);
        footerLayout.setBackgroundResource(R.color.white);
        TextView txt = new TextView(this.getContext());
        txt.setText(this.loadMessage);
        /*Drawable drawable= getResources().getDrawable(R.drawable.progressbar_circle_1);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        txt.setCompoundDrawables(drawable,null,null,null);*/
        footerLayout.addView(txt);
        footerLayout.setVisibility(View.GONE);
        this.addFooterView(footerLayout);
    }

    /**
     * 进度条显示的消息文本
     *
     * @param msg
     */
    public void setLoadMessage(String msg) {
        this.loadMessage = msg;
    }

    /**
     * 是否显示进度条
     *
     * @param isVisible
     *            true:显示,false:不显示
     */
    public void setProggressBarVisible(Boolean isVisible) {
        if (this.footerLayout == null) {
            return;
        }
        int visibility = View.VISIBLE;
        if (!isVisible) {
            visibility = View.GONE;
        }
        // 定位到最后一行,必须设置，要不然进度条看不到
        this.setSelection(this.getAdapter().getCount());
        this.footerLayout.setVisibility(visibility);
        // 设置页脚中组件的显示状态
        for (int i = 0; i < this.footerLayout.getChildCount(); i++) {
            View v = this.footerLayout.getChildAt(i);
            v.setVisibility(visibility);
        }
    }

    public interface OnPageLoadListener {
        /**
         * 触发分页事件
         *
         * @param pageSize
         * @param pageIndex
         */
        public void onPageChanging(int pageSize, int pageIndex);
        /**
         * 是否能够加载数据 此方法返回结果为true时触发OnPageChanging事件，否则不做任何处理
         *
         * @return
         */
        public boolean canLoadData();
    }

    /**
     * 设置页加载侦听事件
     *
     * @param listener
     */
    public void setOnPageLoadListener(OnPageLoadListener listener) {
        this.listener = listener;
    }

}
