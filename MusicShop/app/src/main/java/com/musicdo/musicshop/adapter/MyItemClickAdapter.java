package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.CategoryLevelOneBean;

import java.util.ArrayList;

/**
 * 描述:
 * 作者：haiming on 2017/7/24 14:39
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class MyItemClickAdapter extends RecyclerView.Adapter<MyItemClickAdapter.ViewHolder> implements View.OnClickListener {
    private OnItemClickLitener mOnItemClickListener = null;
    private ArrayList<CategoryLevelOneBean> categoryLevelOneBeans=new ArrayList();
    private  int listPosition=0;
    private Context content;

    public void setOnItemClickListener(OnItemClickLitener listener) {
        this.mOnItemClickListener = listener;
    }

    public MyItemClickAdapter(Context content, ArrayList<CategoryLevelOneBean> categoryLevelOneBeans) {
        this.categoryLevelOneBeans = categoryLevelOneBeans;
        this.content=content;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item_left_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(categoryLevelOneBeans.get(position).getName());
        holder.tv.setTag(holder.getLayoutPosition());
        holder.tv.setOnClickListener(this);
        if (listPosition==position){
            holder.iv_line.setVisibility(View.VISIBLE);
            holder.ll_categort_left.setBackgroundColor(content.getResources().getColor(R.color.white));
        }else {
            holder.iv_line.setVisibility(View.INVISIBLE);
            holder.ll_categort_left.setBackgroundColor(content.getResources().getColor(R.color.home_top_reflash_bg));
        }
        if (listPosition==0){

        }else{

        }
    }

    @Override
    public int getItemCount() {
        return categoryLevelOneBeans == null ? 0 : categoryLevelOneBeans.size();
    }

    public void setPosition(int Position) {
        listPosition=Position;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv:
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
                break;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_line;
        TextView tv;
        LinearLayout ll_categort_left;

        public ViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv);
            iv_line = (ImageView) view.findViewById(R.id.iv_line);
            ll_categort_left = (LinearLayout) view.findViewById(R.id.ll_categort_left);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}