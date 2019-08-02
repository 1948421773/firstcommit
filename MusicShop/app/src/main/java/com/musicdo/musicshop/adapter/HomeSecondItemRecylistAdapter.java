package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.view.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuedu on 2017/9/15.
 */

public class HomeSecondItemRecylistAdapter extends RecyclerView.Adapter<HomeSecondItemRecylistAdapter.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private List<MusicalBean> itemGridList;
    private LayoutInflater inflater;
    private static final int TYPE_ITEM_LIST = 0;
    private static final int TYPE_FOOTER_GRID = 1;
    private boolean showtype = false;
    //适配器初始化

    public HomeSecondItemRecylistAdapter(Context context, List<MusicalBean> itemGridList, boolean showtype) {
        mContext = context;
        this.itemGridList = itemGridList;
        this.showtype = showtype;
        inflater = LayoutInflater.from(mContext);
    }

    //自定义监听事件
    public static interface OnSearchItemClickListener {
        void onItemClick(View view, int position);
    }

    public void changeShowType(boolean type) {
        showtype = type;
    }

    @Override
    public HomeSecondItemRecylistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
//        if (viewType == TYPE_ITEM_LIST) {
        View view = inflater.inflate(R.layout.item_homesecond_gridview, parent, false);//这个布局就是一个imageview用来显示图片
        HomeSecondItemRecylistAdapter.MyViewHolder holder = new HomeSecondItemRecylistAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
//        }
        /*else{
            HomeSecondItemRecylistAdapter.MyViewHolderFooterView holderFooterView = new HomeSecondItemRecylistAdapter.MyViewHolderFooterView(LayoutInflater.from(
                    mContext).inflate(R.layout.searchprod_grid_item, parent,
                    false));
            return holderFooterView;
        }*/
    }

    @Override
    public void onBindViewHolder(HomeSecondItemRecylistAdapter.MyViewHolder holder, int position) {
//        if(holder instanceof MyViewHolder) {
        ((MyViewHolder) holder).tv_home_grid_item_title.setText(itemGridList.get(position).getName());
//        ((MyViewHolder) holder).tv_home_grid_item_price.setText(mContext.getResources().getString(R.string.pricesymbol) + itemGridList.get(position).getMemberPrice());
        String MemberPrice=SpUtils.PriceTwoDecimal(itemGridList.get(position).getMemberPrice());
        if (MemberPrice!=null&&!MemberPrice.equals("")){
            String[] MemberPrices=MemberPrice.split("\\.");
            ((MyViewHolder)holder).tv_home_grid_item_price.setText(MemberPrices[0]);
            ((MyViewHolder)holder).tv_price_decimal.setText("."+MemberPrices[1]);
        }

        ((MyViewHolder) holder).homeitem_icon.setImageURI(AppConstants.PhOTOADDRESS + itemGridList.get(position).getSrcDetail());

        //获取图片真正的宽高


        RelativeLayout.LayoutParams linearParamsPic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        linearParamsPic.width = AppConstants.ScreenWidth * 21 / 50;
        linearParamsPic.height = AppConstants.ScreenWidth * 21 / 50;
        ((MyViewHolder) holder).homeitem_icon.setLayoutParams(linearParamsPic);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        linearParams.width = AppConstants.ScreenWidth * 12 / 25;
        linearParams.height = AppConstants.ScreenWidth * 12 / 25;
        ((MyViewHolder) holder).rl_homesecond_item.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        ((MyViewHolder) holder).itemView.setTag(position);
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
    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_homesecond_item;
        RelativeLayout rl_image;
        SimpleDraweeView homeitem_icon;
        //        SimpleDraweeView homeitem_icon;
        TextView tv_home_grid_item_title;
        TextView tv_home_grid_item_price;
        TextView tv_price_decimal;

        public MyViewHolder(View itemView) {
            super(itemView);
            rl_homesecond_item = (RelativeLayout) itemView.findViewById(R.id.rl_homesecond_item);
//            rl_image = (RelativeLayout) itemView.findViewById(R.id.rl_image);
            homeitem_icon = (SimpleDraweeView) itemView.findViewById(R.id.homeitem_icon);
            tv_home_grid_item_title = (TextView) itemView.findViewById(R.id.tv_home_grid_item_title);
            tv_home_grid_item_price = (TextView) itemView.findViewById(R.id.tv_home_grid_item_price);
            tv_price_decimal = (TextView) itemView.findViewById(R.id.tv_price_decimal);
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
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnSearchItemClickListener listener) {
        mOnItemClickListener = listener;
    }

}
