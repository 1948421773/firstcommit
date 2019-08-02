package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ShopIndexBaseActivity;
import com.musicdo.musicshop.bean.ShopCollectBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Yuedu on 2017/11/23.
 */

public class ShopCollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    OnSearchItemLongClickListener mOnItemLongClickListener;
    private ArrayList<ShopCollectBean> datas = new ArrayList<>();
    private LayoutInflater inflater;
    View view;
    private boolean showtype=false;
    StringBuffer paramStrBuff;
    //适配器初始化

    public ShopCollectAdapter(Context context,ArrayList<ShopCollectBean> datas){
        mContext=context;
        this.datas=datas;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public boolean onLongClick(View v) {
        if(mOnItemLongClickListener != null){
            mOnItemLongClickListener.onItemLongClickListener(v,(int)v.getTag());
        }
        return true;
    }

    //自定义监听事件
    public static interface OnSearchItemClickListener{
        void onItemClick(View view, int position);
    }
    //自定义长按监听事件
    public static interface OnSearchItemLongClickListener{
        void  onItemLongClickListener(View view, int position);
    }

    public void changeShowType(boolean type){
        showtype=type;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        view = inflater.inflate(R.layout.item_shopcollect, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        paramStrBuff=new StringBuffer("");
        ((MyViewHolder)holder).tv_shopcollect_name.setText(datas.get(position).getShopName());
        ((MyViewHolder)holder).tv_shopcollect_num.setText(datas.get(position).getConcern()+"个人关注");
//        ((MyViewHolder)holder).iv_shopcollect_icon.setImageURI(Uri.parse(AppConstants.PhOTOADDRESS + datas.get(position).getSrcLogo()));
        if (!datas.get(position).getSrcLogo().equals("")){
            ImageView iv_shopcollect_icon=((MyViewHolder)holder).iv_shopcollect_icon;
            Picasso.with(mContext)
                    .load(AppConstants.PhOTOADDRESS+datas.get(position).getSrcLogo())
                    .resize(100,75)
//                    .onlyScaleDown()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.img_start_loading)
                    .error(R.mipmap.img_load_error)
                    .into(iv_shopcollect_icon);
        }else{
            ((MyViewHolder)holder).iv_shopcollect_icon.setImageResource(R.mipmap.ic_launcher);
        }

        /*((MyViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopIndexBaseActivity.class);
                intent.putExtra("ShopID",datas.get(position).getShopID());
                mContext.startActivity(intent);
            }
        });*/
        ((MyViewHolder)holder).itemView.setTag(position);

    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_shopcollect_name,tv_shopcollect_num;
        ImageView iv_shopcollect_icon;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_shopcollect_num = (TextView) itemView.findViewById(R.id.tv_shopcollect_num);
            tv_shopcollect_name = (TextView) itemView.findViewById(R.id.tv_shopcollect_name);
            iv_shopcollect_icon = (ImageView) itemView.findViewById(R.id.iv_shopcollect_icon);
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
    public void setOnItemClickListener(OnSearchItemClickListener listener){
        mOnItemClickListener=listener;
    }

    public void setOnItemLongClickListener(OnSearchItemLongClickListener listener){
        mOnItemLongClickListener=listener;
    }

}
