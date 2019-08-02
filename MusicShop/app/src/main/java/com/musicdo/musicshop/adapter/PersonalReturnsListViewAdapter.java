package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AllOrderPayForActivity;
import com.musicdo.musicshop.activity.GoodsInfoSpecActivity;
import com.musicdo.musicshop.activity.OrderDtailActivity;
import com.musicdo.musicshop.activity.PaymenyActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.RefundDetailActivity;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.CartItemBean;
import com.musicdo.musicshop.bean.OrderDetailBean;
import com.musicdo.musicshop.bean.PersonalReturnsBean;
import com.musicdo.musicshop.bean.SpecBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/10/16.
 */

public class PersonalReturnsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<PersonalReturnsBean> datas;//数据
    private LayoutInflater inflater;
    private static final int TYPE_ITEM_LIST = 0;
    private static final int TYPE_FOOTER_GRID = 1;
    private boolean showtype=false;
    //适配器初始化

    public PersonalReturnsListViewAdapter(Context context,ArrayList<PersonalReturnsBean> datas,boolean showtype){
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
            View view = inflater.inflate(R.layout.item_personalreturns, parent, false);//这个布局就是一个imageview用来显示图片
            MyViewHolder holder = new MyViewHolder(view);
            view.setOnClickListener(this);
            return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+datas.get(position).getProductImgUrl())
                .resize(200,200)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(((MyViewHolder)holder).sd_purchase_img);//加载网络图片
        ((MyViewHolder)holder).tv_shop_name.setText(datas.get(position).getShopName());
        ((MyViewHolder)holder).tv_intro.setText(datas.get(position).getProductName());
        ((MyViewHolder)holder).tv_choose_parameter.setText(datas.get(position).getPropertysText());
        ((MyViewHolder)holder).tv_number.setText("退款金额:"+mContext.getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(datas.get(position).getPrice()));
        switch (datas.get(position).getIsDealwith()){
            case 0://0：默认状态,这个商品订单的状态专门为退款而设定的，如果没有退款的话，不用管它，保留默认值0
                break;
            case 1:
                ((MyViewHolder)holder).tv_all_order_state.setText("退款申请中");
                ((MyViewHolder)holder).tv_all_order_state.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                break;
            case 2:
                ((MyViewHolder)holder).tv_all_order_state.setText("被拒退款");
                ((MyViewHolder)holder).tv_all_order_state.setTextColor(mContext.getResources().getColor(R.color.text_black));
                break;
            case 3:
                ((MyViewHolder)holder).tv_all_order_state.setText("退款完成");
                ((MyViewHolder)holder).tv_all_order_state.setTextColor(mContext.getResources().getColor(R.color.text_black));
                break;
        }

        ((MyViewHolder)holder).tv_remind_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, RefundDetailActivity.class);
                intent.putExtra("Number",String.valueOf(datas.get(position).getID()));//订单编号
                intent.putExtra("IsDealwith",String.valueOf(datas.get(position).getIsDealwith()));//订单编号
                mContext.startActivity(intent);
            }
        });
        ((MyViewHolder)holder).rl_purchaselist_item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, RefundDetailActivity.class);
                intent.putExtra("Number",String.valueOf(datas.get(position).getID()));//订单编号
                intent.putExtra("IsDealwith",String.valueOf(datas.get(position).getIsDealwith()));//订单编号
                mContext.startActivity(intent);
            }
        });
        /*if(holder instanceof MyViewHolder) {
            ((MyViewHolder)holder).iv_popularity_icon.setImageURI(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            RelativeLayout.LayoutParams linearParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            linearParams.width= ScreenUtil.getScreenWidth(mContext)*11/25;
            linearParams.height=ScreenUtil.getScreenHeight(mContext)*5/20;
            ((MyViewHolder)holder).iv_popularity_icon.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
            ((MyViewHolder)holder).tv_searchprod_item_title.setText(datas.get(position).getName());
            ((MyViewHolder)holder).tv_popularity_price.setText("￥"+SpUtils.doubleToString(datas.get(position).getMemberPrice()));
            ((MyViewHolder)holder).tv_popularity_salenumber.setText("月销"+String.valueOf(datas.get(position).getSaleCount()));

            RelativeLayout.LayoutParams relaticeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            relaticeParams.width = ScreenUtil.getScreenWidth(mContext) *12/ 25;
            relaticeParams.height = ScreenUtil.getScreenHeight(mContext) * 8 / 20;
            ((MyViewHolder)holder).rl_search_item.setLayoutParams(relaticeParams); //使设置好的布局参数应用到控件
            ((MyViewHolder)holder).itemView.setTag(position);
        }else{
            ((MyViewHolderFooterView)holder).iv_popularity_icon.setImageURI(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            ((MyViewHolderFooterView)holder).tv_searchprod_item_title.setText(datas.get(position).getName());
            ((MyViewHolderFooterView)holder).tv_popularity_salenumber.setText("月销"+String.valueOf(datas.get(position).getSaleCount()));
            ((MyViewHolderFooterView)holder).itemView.setTag(position);
        }*/
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
        ImageView sd_purchase_img;
        TextView tv_shop_name;
        TextView tv_intro;
        TextView tv_choose_parameter;
        TextView tv_number;
        TextView tv_all_order_state;
        TextView tv_remind_delivery;
        RelativeLayout rl_purchaselist_item_title;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            rl_purchaselist_item_title = (RelativeLayout) itemView.findViewById(R.id.rl_purchaselist_item_title);
            sd_purchase_img = (ImageView) itemView.findViewById(R.id.sd_purchase_img);
            tv_shop_name = (TextView) itemView.findViewById(R.id.tv_shop_name);
            tv_intro = (TextView) itemView.findViewById(R.id.tv_intro);
            tv_choose_parameter = (TextView) itemView.findViewById(R.id.tv_choose_parameter);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_all_order_state = (TextView) itemView.findViewById(R.id.tv_all_order_state);
            tv_remind_delivery = (TextView) itemView.findViewById(R.id.tv_remind_delivery);
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
    public void addMoreItem(ArrayList<PersonalReturnsBean> newdatas){
        datas.addAll(newdatas);
        notifyDataSetChanged();
    }
}