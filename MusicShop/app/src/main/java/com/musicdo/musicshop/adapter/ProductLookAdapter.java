package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.bean.Product_LookBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.view.CustomDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Yuedu on 2017/11/24.
 */

public class ProductLookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    OnSearchItemLongClickListener mOnItemLongClickListener;
    private ArrayList<Product_LookBean> datas = new ArrayList<>();
    private LayoutInflater inflater;
    View view;
    private boolean showtype=false;
    StringBuffer paramStrBuff;
    //适配器初始化
    private DeleteInterface deleteInterface;

    public ProductLookAdapter(Context context, ArrayList<Product_LookBean> datas){
        mContext=context;
        this.datas=datas;
        inflater = LayoutInflater.from(mContext);
    }

    public interface DeleteInterface
    {
        public void delete(int groupPosition, int childPosition);

    }

    public void setDeteInterface(DeleteInterface deleteInterface)
    {
        this.deleteInterface = deleteInterface;
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
        view = inflater.inflate(R.layout.item_productlook, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        paramStrBuff=new StringBuffer("");
        ((MyViewHolder)holder).tv_look_data.setText(datas.get(position).getDate());
//        ((MyViewHolder)holder).rc_look_datalist.setText(datas.get(position).getDataList()+"个人关注");
//        ((MyViewHolder)holder).iv_shopcollect_icon.setImageURI(Uri.parse(AppConstants.PhOTOADDRESS + datas.get(position).getSrcLogo()));
        if (datas.get(position).getDataList()!=null){
            ProductLookItemAdapter RecylistAdapter= new ProductLookItemAdapter(mContext, datas.get(position).getDataList(),false);
            ((MyViewHolder)holder).rc_look_datalist.setLayoutManager( new GridLayoutManager(mContext, 1));
            ((MyViewHolder)holder).rc_look_datalist.setAdapter(RecylistAdapter);
//        childHolder.rc_searchprod_overall.setNestedScrollingEnabled(false);
//        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//        linearParams.width= ScreenUtil.getScreenWidth(mContext);
//        linearParams.height=ScreenUtil.getScreenHeight(mContext);
//        gridView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
            RecylistAdapter.setOnItemClickListener(new ProductLookItemAdapter.OnSearchItemClickListener() {
                @Override
                public void onItemClick(View view, int index) {
                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                    int id=datas.get(position).getDataList().get(index).getProductID();
                    intent.putExtra("prod_id",id);
                    mContext.startActivity(intent);
                }
            });

            RecylistAdapter.setOnItemLongClickListener(new ProductLookItemAdapter.OnSearchItemLongClickListener() {
                @Override
                public void onItemLongClickListener(View view, final int index) {
                    if (deleteInterface!=null){
                    deleteInterface.delete(position, index);// 暴露组选接口
                    }
                }
            });
        }else{
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
        TextView tv_look_data;
        RecyclerView rc_look_datalist;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_look_data = (TextView) itemView.findViewById(R.id.tv_look_data);
            rc_look_datalist = (RecyclerView) itemView.findViewById(R.id.rc_look_datalist);
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

