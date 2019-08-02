package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.HotBrandBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.musicdo.musicshop.R.id.list_item_iv;

/**
 *
 * Created by Yuedu on 2017/10/17.
 */

public class HotBrandBeanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<HotBrandBean> datas;//数据
    private LayoutInflater inflater;
    private static final int TYPE_ITEM_LIST = 0;
    private static final int TYPE_FOOTER_GRID = 1;
    private boolean showtype=false;
    //适配器初始化

    public HotBrandBeanAdapter(Context context,ArrayList<HotBrandBean> datas,boolean showtype){
        mContext=context;
        this.datas=datas;
        this.showtype=showtype;
        inflater = LayoutInflater.from(mContext);
    }

    //自定义监听事件
    public static interface OnSearchItemClickListener{
        void onItemClick(View view, int position);
    }

    public void changeShowType(boolean type){
        showtype=type;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = inflater.inflate(R.layout.category_item_right_itemview, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
            return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((MyViewHolder)holder).tv_griditem.setText(datas.get(position).getName());
        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+datas.get(position).getLogo())
                .resize(AppConstants.ScreenWidth / 5,((AppConstants.ScreenWidth / 5)*3)/4)
                .centerCrop()
//                .onlyScaleDown()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(((MyViewHolder)holder).list_item_iv);
        ((MyViewHolder)holder).itemView.setTag(position);
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
        public TextView tv_griditem;
        public ImageView list_item_iv;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_griditem= (TextView) itemView.findViewById(R.id.list_item_tv);
            list_item_iv= (ImageView) itemView.findViewById(R.id.list_item_iv);
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


}
