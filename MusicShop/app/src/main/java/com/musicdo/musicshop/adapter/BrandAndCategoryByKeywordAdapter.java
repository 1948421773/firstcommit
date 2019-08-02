package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.BrandAndCategoryByKeywordItemBean;

import java.util.ArrayList;

/**
 * Created by Yuedu on 2017/12/5.
 */

public class BrandAndCategoryByKeywordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private OnItemClickLitener mOnItemClickListener = null;
    private ArrayList<BrandAndCategoryByKeywordItemBean> categoryLevelOneBeans=new ArrayList();
    private  int listPosition=0;
    private Context content;
    CheckChangeItem checkChangeItem;
    String type;
    boolean isShow=true;
    public interface CheckChangeItem{
       public void doCheckChangeItem(String type,int index,boolean isCheck);
    }

    public void setCheckChangeItem(CheckChangeItem checkChangeItem){
        this.checkChangeItem=checkChangeItem;
    }

    public void setOnItemClickListener(OnItemClickLitener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setShowNumber(boolean isShow) {
        this.isShow = isShow;
    }

    public BrandAndCategoryByKeywordAdapter(Context content, ArrayList<BrandAndCategoryByKeywordItemBean> categoryLevelOneBeans,String type) {
        this.categoryLevelOneBeans = categoryLevelOneBeans;
        this.content=content;
        this.type=type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_brandandcategorykeyword, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position<6){
            ((ViewHolder)holder).ll_categort_left.setVisibility(View.VISIBLE);
        }else{
            if (isShow){
                ((ViewHolder)holder).ll_categort_left.setVisibility(View.GONE);
            }else{
                ((ViewHolder)holder).ll_categort_left.setVisibility(View.VISIBLE);
            }

        }
        ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setText(categoryLevelOneBeans.get(position).getName());
        if (categoryLevelOneBeans.get(position).ischeck()){
            Drawable drawable= content.getResources().getDrawable(R.mipmap.saixuan_check);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setCompoundDrawables(drawable,null,null,null);
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setChecked(true);
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setText(categoryLevelOneBeans.get(position).getName());
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setTextColor(content.getResources().getColor(R.color.saixuan_click));
            checkChangeItem.doCheckChangeItem(type,position,true);
        }else {
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setCompoundDrawables(null,null,null,null);
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setChecked(false);
            ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setTextColor(content.getResources().getColor(R.color.umeng_text_color));
            checkChangeItem.doCheckChangeItem(type,position,false);
        }
        /*holder.cb_saixuan_categroy_chekbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Drawable drawable= content.getResources().getDrawable(R.mipmap.saixuan_check);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.cb_saixuan_categroy_chekbox.setCompoundDrawables(drawable,null,null,null);
                }else {
                    holder.cb_saixuan_categroy_chekbox.setCompoundDrawables(null,null,null,null);
                }

            }
        });*/
        /*((ViewHolder)holder).cb_saixuan_categroy_chekbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkChangeItem.doCheckChangeItem(type,position,true);
                *//*if (categoryLevelOneBeans.get(position).ischeck()){
                    Drawable drawable= content.getResources().getDrawable(R.mipmap.saixuan_check);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.cb_saixuan_categroy_chekbox.setCompoundDrawables(drawable,null,null,null);
                }else {
                    holder.cb_saixuan_categroy_chekbox.setCompoundDrawables(null,null,null,null);
                }*//*
            }
        });*/
        ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setOnClickListener(this);
        ((ViewHolder)holder).cb_saixuan_categroy_chekbox.setTag(holder.getLayoutPosition());
        /*if (listPosition==position){
            holder.iv_line.setVisibility(View.VISIBLE);
            holder.ll_categort_left.setBackgroundColor(content.getResources().getColor(R.color.white));
        }else {
            holder.iv_line.setVisibility(View.INVISIBLE);
            holder.ll_categort_left.setBackgroundColor(content.getResources().getColor(R.color.home_top_reflash_bg));
        }*/
        if (listPosition==0){

        }else{

        }
    }

    @Override
    public int getItemCount() {
        return categoryLevelOneBeans == null ? 0 : categoryLevelOneBeans.size();
    }

    public void setPosition(int Position) {
        listPosition=Position;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_saixuan_categroy_chekbox:
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
                break;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_saixuan_categroy_chekbox;
        LinearLayout ll_categort_left;

        public ViewHolder(View view) {
            super(view);
            cb_saixuan_categroy_chekbox = (CheckBox) view.findViewById(R.id.cb_saixuan_categroy_chekbox);
            ll_categort_left = (LinearLayout) view.findViewById(R.id.ll_categort_left);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
