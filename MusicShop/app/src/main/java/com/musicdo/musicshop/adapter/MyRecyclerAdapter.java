package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/6/30.
 * 版 本 ：
 * 备 注 ：
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> implements View.OnClickListener{

    private int[] mDatas;
    private List<String> mDatasTitle;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener = null;
    public MyRecyclerAdapter(Context context, int[] datas,List<String> mDatasTitle){
        this. mContext=context;
        this. mDatas=datas;
        this. mDatasTitle=mDatasTitle;
        inflater=LayoutInflater. from(mContext);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    @Override
    public int getItemCount() {
        return mDatas.length;
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Picasso.with(mContext)
                .load(mDatas[position])
                .resize(AppConstants.ScreenWidth/10,AppConstants.ScreenWidth/10)
//                .onlyScaleDown()
                .into(holder.list_item_iv);
        holder.list_item_tv.setText(mDatasTitle.get(position));
        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        linearParams.width= AppConstants.ScreenWidth/6;
        linearParams.height=AppConstants.ScreenWidth/6;
        holder.rl_function_icon.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

         LinearLayout.LayoutParams functionlinearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//        linearParams.width= AppConstants.ScreenWidth/6;
//        linearParams.height= AppConstants.ScreenWidth/6;
        holder.ll_brand_item.setLayoutParams(functionlinearParams); //使设置好的布局参数应用到控件
        holder.itemView.setTag(position);
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_list_item,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView list_item_iv;
        TextView list_item_tv;
        LinearLayout ll_brand_item;
        LinearLayout rl_function_icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_brand_item=(LinearLayout) itemView.findViewById(R.id. ll_brand_item);
            rl_function_icon=(LinearLayout) itemView.findViewById(R.id. rl_function_icon);
            list_item_iv=(ImageView) itemView.findViewById(R.id. list_item_iv);
            list_item_tv=(TextView) itemView.findViewById(R.id. list_item_tv);
        }

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
