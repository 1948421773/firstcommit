package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;

import java.util.List;

/**
 * Created by Yuedu on 2017/11/24.
 */

public class ProductCollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    OnSearchItemLongClickListener mOnItemLongClickListener;
    private List<ProductCollectBean> itemGridList;
    private LayoutInflater inflater;
    private static final int TYPE_ITEM_LIST = 0;
    private static final int TYPE_FOOTER_GRID = 1;
    private boolean showtype=false;
    //适配器初始化

    public ProductCollectAdapter(Context context, List<ProductCollectBean> itemGridList, boolean showtype){
        mContext=context;
        this.itemGridList=itemGridList;
        this.showtype=showtype;
        inflater = LayoutInflater.from(mContext);
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
//        if (viewType == TYPE_ITEM_LIST) {
        View view = inflater.inflate(R.layout.item_productlookadapter, parent, false);//这个布局就是一个imageview用来显示图片
        ProductCollectAdapter.MyViewHolder holder = new ProductCollectAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
//        }
        /*else{
            ProductCollectAdapter.MyViewHolderFooterView holderFooterView = new ProductCollectAdapter.MyViewHolderFooterView(LayoutInflater.from(
                    mContext).inflate(R.layout.searchprod_grid_item, parent,
                    false));
            return holderFooterView;
        }*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if(holder instanceof MyViewHolder) {
        ((MyViewHolder)holder).tv_similar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //足迹列表找相似产品
            }
        });
        ((MyViewHolder)holder).iv_popularity_icon.setImageURI(AppConstants.PhOTOADDRESS + itemGridList.get(position).getSrcDetail());
        ((MyViewHolder)holder).tv_searchprod_item_title.setText(itemGridList.get(position).getName());
        ((MyViewHolder)holder).tv_popularity_price.setText(mContext.getResources().getString(R.string.pricesymbol)+String.valueOf(itemGridList.get(position).getMemberPrice()));
        ((MyViewHolder)holder).itemView.setTag(position);


//        }
    }


    @Override
    public int getItemViewType(int position) {
        if (showtype) {
            return TYPE_FOOTER_GRID;
        } else {
            return TYPE_ITEM_LIST;
        }
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView iv_popularity_icon;
        TextView tv_searchprod_item_title;
        TextView tv_popularity_price;
        TextView tv_similar;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            iv_popularity_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_popularity_icon);
            tv_searchprod_item_title = (TextView) itemView.findViewById(R.id.tv_searchprod_item_title);
            tv_popularity_price = (TextView) itemView.findViewById(R.id.tv_popularity_price);
            tv_similar = (TextView) itemView.findViewById(R.id.tv_similar);
        }
    }

    //自定义ViewHolder，用于加载图片
    /*class MyViewHolderFooterView extends RecyclerView.ViewHolder{
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
    }*/

    @Override
    public int getItemCount() {
//        return 2;//获取数据的个数;
        return itemGridList.size();//获取数据的个数;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(mOnItemLongClickListener != null){
            mOnItemLongClickListener.onItemLongClickListener(v,(int)v.getTag());
        }
        return true;
    }

    public void setOnItemClickListener(OnSearchItemClickListener listener){
        mOnItemClickListener=listener;
    }

    public void setOnItemLongClickListener(OnSearchItemLongClickListener listener){
        mOnItemLongClickListener=listener;
    }
}
