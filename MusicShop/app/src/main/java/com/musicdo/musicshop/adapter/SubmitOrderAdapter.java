package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.bean.SubmitOrder;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 描述:
 * 作者：haiming on 2017/8/17 14:28
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SubmitOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    ArrayList<SubmitOrder> submitOrders=new ArrayList<>();
    private LayoutInflater inflater;
    View view;
    String shopName;
    private boolean showtype=false;
    StringBuffer paramStrBuff;
    DecreaseCound decreaseCound;
    IncreaseCound increaseCound;
    //适配器初始化

    public SubmitOrderAdapter(Context context,ArrayList<SubmitOrder> datas,String ShopName){
        mContext=context;
        this.submitOrders=datas;
        this.shopName=ShopName;
        inflater = LayoutInflater.from(mContext);
    }

    public interface DecreaseCound
    {
        /**
         * 减少操作
         * @param OrderNumber 订单号
         */
        public void doDecreaseCound(String OrderNumber, int groupPosition,String UserName);
    }
    public interface IncreaseCound
    {
        /**
         * 增加操作
         * @param OrderNumber 订单号
         */
        public void doIncreaseCound(String OrderNumber, int groupPosition,String UserName);
    }

    public void setDecreaseCound(DecreaseCound decreaseCound){
        this.decreaseCound = decreaseCound;
    }
    public void setIncreaseCound(IncreaseCound increaseCound){
        this.increaseCound = increaseCound;
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
        view = inflater.inflate(R.layout.item_submitorder_buynow_child, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        paramStrBuff=new StringBuffer("");
        ((MyViewHolder)holder).tv_choose_parameter.setText(submitOrders.get(position).getPropertys());
        if (submitOrders.get(position).getPropertys()==null){
            ((MyViewHolder)holder).tv_number.setVisibility(View.GONE);
            ((MyViewHolder)holder).ll_reduce_add.setVisibility(View.VISIBLE);
        }else{
            if (submitOrders.get(position).getPropertys().equals("")){
                ((MyViewHolder)holder).tv_number.setVisibility(View.GONE);
                ((MyViewHolder)holder).ll_reduce_add.setVisibility(View.VISIBLE);
            }else{
                ((MyViewHolder)holder).tv_number.setVisibility(View.VISIBLE);
                ((MyViewHolder)holder).ll_reduce_add.setVisibility(View.GONE);
            }
        }
        ((MyViewHolder)holder).tv_number.setText("X"+String.valueOf(submitOrders.get(position).getCount()));
        ((MyViewHolder)holder).tv_intro.setText(submitOrders.get(position).getName());
        ((MyViewHolder)holder).tv_shop_name.setText(shopName);
        ((MyViewHolder)holder).tv_num.setText(String.valueOf(submitOrders.get(position).getCount()));
        ((MyViewHolder)holder).tv_price.setText(mContext.getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(submitOrders.get(position).getMemberPrice()));

        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+submitOrders.get(position).getImgUrl())
                .resize(200,200)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(((MyViewHolder)holder).iv_adapter_list_pic);//加载网络图片
        ((MyViewHolder)holder).itemView.setTag(position);

        ((MyViewHolder)holder).ll_pay_distribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ((MyViewHolder)holder).rl_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ((MyViewHolder)holder).tv_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseCound.doDecreaseCound(null, submitOrders.get(position).getCount(),null);// 删除订单
            }
        });
        ((MyViewHolder)holder).tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCound.doIncreaseCound(null, submitOrders.get(position).getCount(),null);// 删除订单
            }
        });
        ((MyViewHolder)holder).iv_adapter_list_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                                        Toast.makeText(context, "点击了"+position, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                    intent.putExtra("prod_id",submitOrders.get(position).getID());
                    mContext.startActivity(intent);
            }
        });
    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_choose_parameter,tv_number,tv_intro,tv_price,tv_shop_name,tv_reduce,tv_add,tv_num;
        ImageView iv_adapter_list_pic;
        RelativeLayout ll_pay_distribution,rl_invoice;
        LinearLayout ll_reduce_add;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            ll_pay_distribution = (RelativeLayout) itemView.findViewById(R.id.ll_pay_distribution);
            rl_invoice = (RelativeLayout) itemView.findViewById(R.id.rl_invoice);
            iv_adapter_list_pic = (ImageView) itemView.findViewById(R.id.iv_adapter_list_pic);
            tv_shop_name = (TextView) itemView.findViewById(R.id.tv_shop_name);
            tv_choose_parameter = (TextView) itemView.findViewById(R.id.tv_choose_parameter);
            tv_reduce = (TextView) itemView.findViewById(R.id.tv_reduce);
            tv_add = (TextView) itemView.findViewById(R.id.tv_add);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            ll_reduce_add = (LinearLayout) itemView.findViewById(R.id.ll_reduce_add);
            tv_intro = (TextView) itemView.findViewById(R.id.tv_intro);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
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
        return submitOrders.size();//获取数据的个数;
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
    public void addMoreItem(ArrayList<SubmitOrder> newdatas){
        submitOrders.addAll(newdatas);
        notifyDataSetChanged();
    }
}
