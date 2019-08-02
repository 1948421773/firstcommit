package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.Meizi;

import java.util.List;

/**
 * Created by A on 2017/7/18.
 */

public class OrderUnpaidAdapter extends RecyclerView.Adapter<OrderUnpaidAdapter.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    private List<Meizi> datas;//数据
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    //适配器初始化
    public OrderUnpaidAdapter(Context context,List<Meizi> datas) {
        mContext=context;
        this.datas=datas;
        inflater = LayoutInflater.from(mContext);
    }

    //点击事件回调
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    //自定义监听事件
    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public int getItemCount()
    {
        return datas.size();//获取数据的个数
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //根据item类别加载不同ViewHolder
        View view = inflater.inflate(R.layout.order_unpaid_item, parent,false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        /*Picasso.with(mContext)
                .load(datas.get(position).getUrl())
                .resize(200,200)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(holder.homeitem_icon);//加载网络图片*/

//        holder.homeitem_icon.setImageResource(R.mipmap.ic_launcher);
        holder.sd_purchase_img.setImageURI(datas.get(position).getUrl());
        holder.itemView.setTag(position);
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView sd_purchase_img;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            sd_purchase_img = (SimpleDraweeView) itemView.findViewById(R.id.sd_purchase_img);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
