package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.TrackingLogisticsBean;

import java.util.ArrayList;

/**
 * 描述:
 * 作者：haiming on 2017/8/31 11:36
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class OrderTrackingLogisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OrderTrackingLogisticsAdapter.OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<TrackingLogisticsBean> datas;//数据
    private LayoutInflater inflater;
    //适配器初始化

    public OrderTrackingLogisticsAdapter(Context context,ArrayList<TrackingLogisticsBean> datas){
        mContext=context;
        this.datas=datas;
        inflater = LayoutInflater.from(mContext);
    }

    //自定义监听事件
    public static interface OnSearchItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
            View view = inflater.inflate(R.layout.item_tracking_logistics, parent, false);//这个布局就是一个imageview用来显示图片
            OrderTrackingLogisticsAdapter.MyViewHolder holder = new OrderTrackingLogisticsAdapter.MyViewHolder(view);
            view.setOnClickListener(this);
            return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if(holder instanceof OrderTrackingLogisticsAdapter.MyViewHolder) {
        if (0==position){
            ((MyViewHolder)holder).point.setBackgroundResource(R.mipmap.tracking_logistics_item1);
            ((MyViewHolder)holder).tv_info_message.setTextColor(mContext.getResources().getColor(R.color.order_tracking_logistics));
            ((MyViewHolder)holder).tv_info_time.setTextColor(mContext.getResources().getColor(R.color.order_tracking_logistics));
            ((MyViewHolder)holder).line_up.setVisibility(View.INVISIBLE);
        }else{
            ((MyViewHolder)holder).point.setBackgroundResource(R.mipmap.tracking_logistics_item22);
            ((MyViewHolder)holder).tv_info_message.setTextColor(mContext.getResources().getColor(R.color.prodetail_freight));
            ((MyViewHolder)holder).tv_info_time.setTextColor(mContext.getResources().getColor(R.color.prodetail_freight));
            ((MyViewHolder)holder).line_up.setVisibility(View.VISIBLE);
            if (datas.size()-1==position){
                ((MyViewHolder)holder).line_down.setVisibility(View.INVISIBLE);
            }else{
                ((MyViewHolder)holder).line_down.setVisibility(View.VISIBLE);
            }
        }

            ((MyViewHolder)holder).tv_info_message.setText(datas.get(position).getInfoMessage());
            ((MyViewHolder)holder).tv_info_time.setText(datas.get(position).getInfoTime());
            ((MyViewHolder)holder).itemView.setTag(position);
        /*}else{
            ((MyViewHolderFooterView)holder).iv_popularity_icon.setImageURI(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            ((MyViewHolderFooterView)holder).tv_searchprod_item_title.setText(datas.get(position).getName());
            ((MyViewHolderFooterView)holder).tv_popularity_salenumber.setText("月销"+String.valueOf(datas.get(position).getSaleCount()));
            ((MyViewHolderFooterView)holder).itemView.setTag(position);
        }*/
    }


   /* @Override
    public int getItemViewType(int position) {
        if (showtype) {
            return TYPE_FOOTER_GRID;
        } else {
            return TYPE_ITEM_LIST;
        }
    }*/

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView point;
        TextView tv_info_message;
        TextView tv_info_time;
        View line_up,line_down;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            point = (ImageView) itemView.findViewById(R.id.point);
            tv_info_time = (TextView) itemView.findViewById(R.id.tv_info_time);
            tv_info_message = (TextView) itemView.findViewById(R.id.tv_info_message);
            line_up = (View) itemView.findViewById(R.id.line_up);
            line_down = (View) itemView.findViewById(R.id.line_down);
        }
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolderFooterView extends RecyclerView.ViewHolder{
        SimpleDraweeView iv_popularity_icon;
        TextView tv_searchprod_item_title;
        TextView tv_popularity_salenumber;
        public MyViewHolderFooterView(View itemView)
        {
            super(itemView);
            iv_popularity_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_popularity_icon);
            tv_searchprod_item_title = (TextView) itemView.findViewById(R.id.tv_searchprod_item_title);
            tv_popularity_salenumber = (TextView) itemView.findViewById(R.id.tv_popularity_salenumber);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();//获取数据的个数;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
    public void setOnItemClickListener(OrderTrackingLogisticsAdapter.OnSearchItemClickListener listener){
        mOnItemClickListener=listener;
    }
    public void addMoreItem(ArrayList<TrackingLogisticsBean> newdatas){
        datas.addAll(newdatas);
        notifyDataSetChanged();
    }
}
