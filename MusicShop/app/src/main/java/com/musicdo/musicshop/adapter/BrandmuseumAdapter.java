package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.BrandRecommendBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/4.
 * 版 本 ：
 * 备 注 ：
 */

public class BrandmuseumAdapter extends RecyclerView.Adapter<BrandmuseumAdapter.MyViewHolder> implements View.OnClickListener {
    private ArrayList<BrandRecommendBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener = null;

    public BrandmuseumAdapter(Context context, ArrayList<BrandRecommendBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return 10;//品牌馆只显示八个
//        return mDatas.size();
    }


    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.view_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+mDatas.get(position).getLogo())
//                .resize(AppConstants.ScreenWidth/ 5,((AppConstants.ScreenWidth / 5)*3)/4)
                .resize(200,(200*3)/4)//只有一个设备做不了适配，有问题可以改为AppConstants.ScreenWidth/5的分数值
                .centerInside()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(holder.list_item_iv);
        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        linearParams.width= AppConstants.ScreenWidth/6;
        linearParams.height=AppConstants.ScreenHeight/10;
        holder.list_item_iv.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        holder.list_item_tv.setVisibility(View.GONE);
        holder.itemView.setTag(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView list_item_iv;
        TextView list_item_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            list_item_iv = (ImageView) itemView.findViewById(R.id.list_item_iv);
            list_item_tv = (TextView) itemView.findViewById(R.id.list_item_tv);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
