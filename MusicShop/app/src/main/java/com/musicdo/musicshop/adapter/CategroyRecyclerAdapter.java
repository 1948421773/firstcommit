package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.CategoryLevelOneBean;
import com.musicdo.musicshop.bean.CategoryLevelOneBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/7.
 * 版 本 ：
 * 备 注 ：
 */

public class CategroyRecyclerAdapter extends RecyclerView.Adapter<CategroyRecyclerAdapter.MyViewHolder> implements View.OnClickListener{
    private Context mContext;
    private ArrayList<CategoryLevelOneBean> mDatas;//数据
    private LayoutInflater inflater;
    private CategroyRecyclerAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private  int listPosition=0;
    //适配器初始化
    public CategroyRecyclerAdapter(Context context,ArrayList<CategoryLevelOneBean> mDatas) {
        mContext=context;
        this.mDatas=mDatas;
        inflater = LayoutInflater.from(mContext);
    }

    //点击事件回调
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setPosition(int Position) {
        listPosition=Position;
    }


    //自定义监听事件
    public  interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();//获取数据的个数
    }

    @Override
    public CategroyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //根据item类别加载不同ViewHolder
        View view = inflater.inflate(R.layout.category_item_left_layout,parent, false);
        CategroyRecyclerAdapter.MyViewHolder holder= new CategroyRecyclerAdapter.MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final CategroyRecyclerAdapter.MyViewHolder holder, int position) {
        holder.tv.setText( mDatas.get(position).getName());
        if (listPosition==position){
            holder.iv_line.setVisibility(View.VISIBLE);
            holder.ll_categort_left.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }else {
            holder.iv_line.setVisibility(View.INVISIBLE);
            holder.ll_categort_left.setBackgroundColor(mContext.getResources().getColor(R.color.home_top_reflash_bg));
        }
        holder.itemView.setTag(position);
//        if(mOnItemClickListener != null){
//            //为ItemView设置监听器
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = holder.getLayoutPosition(); // 1
//                    mOnItemClickListener.onItemClick(holder.itemView,position); // 2
//                }
//            });
//        }
//        holder.itemView.setOnClickListener(this);
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_line;
        TextView tv;
        LinearLayout ll_categort_left;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            iv_line = (ImageView) itemView.findViewById(R.id.iv_line);
            ll_categort_left = (LinearLayout) itemView.findViewById(R.id.ll_categort_left);
        }

    }

    public void setOnItemClickListener(CategroyRecyclerAdapter.OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
