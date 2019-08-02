package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AddingAddressActivity;
import com.musicdo.musicshop.activity.EditAddressActivity;
import com.musicdo.musicshop.bean.AddressBean;
import com.musicdo.musicshop.bean.AreasBean;
import com.musicdo.musicshop.constant.AppConstants;

import java.util.ArrayList;

/**
 * 描述:
 * 作者：haiming on 2017/8/15 16:36
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<AreasBean> options1Items = new ArrayList<>();
    private ArrayList<AddressBean> datas;//数据
    private LayoutInflater inflater;
    View view;
    private boolean showtype=false;
    StringBuffer paramStrBuff;
    //适配器初始化

    public AddressAdapter(Context context,ArrayList<AddressBean> datas,ArrayList<AreasBean> AreasBeans){
        mContext=context;
        this.datas=datas;
        this.options1Items=AreasBeans;
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
                view = inflater.inflate(R.layout.item_address_manager, parent, false);//这个布局就是一个imageview用来显示图片
                MyViewHolder holder = new MyViewHolder(view);
                view.setOnClickListener(this);
            return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        paramStrBuff=new StringBuffer("");
        for (AreasBean o1:options1Items){
            if (o1.getProvinceId().equals(datas.get(position).getProvinceID())){
                paramStrBuff.append(o1.getProvinceName());
                for (int i = 0; i <o1.getDistrict().size() ; i++) {
                    if (o1.getDistrict().get(i).getDistrictId().equals(datas.get(position).getCityID())){
                        paramStrBuff.append(o1.getDistrict().get(i).getDistrictName());
                        for (int j = 0; j < o1.getDistrict().get(i).getCounty().size(); j++) {
                            if (o1.getDistrict().get(i).getCounty().get(j).getCountyId().equals(datas.get(position).getCountyID())){
                                paramStrBuff.append(o1.getDistrict().get(i).getCounty().get(j).getCountyName());
                                break;
                            }
                        }
                    }

                }
            }

        }
            ((MyViewHolder)holder).tv_name.setText(datas.get(position).getName());
            ((MyViewHolder)holder).tv_phone.setText(datas.get(position).getMoblie());
        if (datas.get(position).getIsDefault()=="1"){
            ((MyViewHolder)holder).tv_default_text.setVisibility(View.VISIBLE);
        }else{
            ((MyViewHolder)holder).tv_default_text.setVisibility(View.GONE);
        }
        if (paramStrBuff.toString()!=null){
            if (paramStrBuff.toString().length()!=0){
                ((MyViewHolder)holder).tv_address.setText(paramStrBuff.toString()+datas.get(position).getAddress());

            }else{
                ((MyViewHolder)holder).tv_address.setText(datas.get(position).getProvinceName()+datas.get(position).getCityName()+datas.get(position).getCountyName()+datas.get(position).getAddress());
            }
        }else{
            ((MyViewHolder)holder).tv_address.setText(datas.get(position).getAddress());
        }
            ((MyViewHolder)holder).im_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditAddressActivity.class);
                AddressBean ab=new AddressBean();
                ab=datas.get(position);
                intent.putExtra("address",ab);
                mContext.startActivity(intent);
            }
        });
            ((MyViewHolder)holder).itemView.setTag(position);

    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name,tv_phone,tv_default_text,tv_address;
        ImageView im_edit_address;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_default_text = (TextView) itemView.findViewById(R.id.tv_default_text);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            im_edit_address = (ImageView) itemView.findViewById(R.id.im_edit_address);
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
    public void addMoreItem(ArrayList<AddressBean> newdatas){
        datas.addAll(newdatas);
        notifyDataSetChanged();
    }
}
