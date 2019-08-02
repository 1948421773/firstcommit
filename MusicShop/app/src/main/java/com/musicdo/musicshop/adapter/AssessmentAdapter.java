package com.musicdo.musicshop.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ImageShowActivity;
import com.musicdo.musicshop.bean.CommentBean;
import com.musicdo.musicshop.bean.CommentImg;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.ProCommentBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by A on 2017/7/11.
 */

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<ProCommentBean> datas;//数据
    private LayoutInflater inflater;
    private AssessmentAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;
    //适配器初始化
    public AssessmentAdapter(Context context, ArrayList<ProCommentBean> commentBeans) {
        mContext=context;
        this.datas=commentBeans;
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
    public AssessmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //根据item类别加载不同ViewHolder
        View view = inflater.inflate(R.layout.grid_assessment_layout, parent,false);//这个布局就是一个imageview用来显示图片
        AssessmentAdapter.MyViewHolder holder = new AssessmentAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(AssessmentAdapter.MyViewHolder holder, int position) {
        if (datas==null){
            return;
        }
        if (datas.get(position).getCommentData()==null){
            return;
        }else{
            holder.tv_comment_shopname.setText(datas.get(position).getCommentData().getUserName());
            if (datas.get(position).getCommentData().getPropertysText()!=null){
                if (!datas.get(position).getCommentData().getPropertysText().equals("")){
                    holder.tv_comment_propertysText.setText("【"+datas.get(position).getCommentData().getPropertysText()+"】");
                }
            }
            if (datas.get(position).getCommentData().getContents()!=null) {
                holder.tv_comment_context.setText(datas.get(position).getCommentData().getContents());
            }
            if (datas.get(position).getCommentData().getDate()!=null) {
                holder.tv_comment_time.setText(datas.get(position).getCommentData().getDate());
            }
        }
        if (datas.get(position).getCommentImg()==null){
            holder.ll_comment_imgs.setVisibility(View.GONE);
        }else{
            if (datas.get(position).getCommentImg().size()==0){
                holder.ll_comment_imgs.setVisibility(View.GONE);
            }else{
                holder.ll_comment_imgs.setVisibility(View.VISIBLE);
            }
        }
        if(datas.get(position).getCommentData().getUserIco()!=null){
            /*Picasso.with(mContext)
                    .load(datas.get(position).getCommentData().getUserIco())
                    .resize(200,200)
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.img_start_loading)
                    .error(R.mipmap.img_load_error)
                    .into(holder.iv_comment_shop_icon);//加载网络图片*/

            if (!datas.get(position).getCommentData().getUserIco().equals("")){

                holder.iv_comment_shop_icon.setImageURI(datas.get(position).getCommentData().getUserIco());
            }else{
                holder.iv_comment_shop_icon.setActualImageResource(R.mipmap.ic_launcher);
            }

        }else{
            holder.iv_comment_shop_icon.setActualImageResource(R.mipmap.ic_launcher);
        }
        if(datas.get(position).getCommentImg().size()!=0){
            final List<String> imgs=new ArrayList();//评论图片展示
            for (CommentImg Img:datas.get(position).getCommentImg()){
                imgs.add(Img.getImgUrl());
            }
            CommentImgAdapter imageAdapter = new CommentImgAdapter(mContext,imgs);
            ((MyViewHolder)holder).rv_comment_photos.setLayoutManager(new GridLayoutManager(mContext,3));
            ((MyViewHolder)holder).rv_comment_photos.setAdapter(imageAdapter);
            imageAdapter.setOnItemClickListener(new CommentImgAdapter.OnSearchItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ArrayList<String> imgUrls=(ArrayList<String>)imgs;
                    Intent intent=new Intent(mContext,ImageShowActivity.class);
                    intent.putStringArrayListExtra("imgUrls",imgUrls);
                    intent.putExtra("index",position);
                    mContext.startActivity(intent);
                }
            });
//            specialUpdate(((MyViewHolder)holder).tv_evaluate_tips,textNumber+"/120");
//            imageAdapter.DoDeleteEvaluateImags(mContext);
            ((MyViewHolder)holder).rv_comment_photos.setNestedScrollingEnabled(false);
        }else{

        }

//        holder.tv_assessment_title.setText(datas.get(position).getUserName());
        holder.itemView.setTag(position);
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView iv_comment_shop_icon;
        TextView tv_comment_shopname,tv_comment_propertysText,tv_comment_context,tv_comment_time;
        LinearLayout ll_comment_imgs;
        RecyclerView rv_comment_photos;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            iv_comment_shop_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_comment_shop_icon);
            rv_comment_photos = (RecyclerView) itemView.findViewById(R.id.rv_comment_photos);
            tv_comment_shopname = (TextView) itemView.findViewById(R.id.tv_comment_shopname);
            tv_comment_propertysText = (TextView) itemView.findViewById(R.id.tv_comment_propertysText);
            tv_comment_context = (TextView) itemView.findViewById(R.id.tv_comment_context);
            tv_comment_time = (TextView) itemView.findViewById(R.id.tv_comment_time);
            ll_comment_imgs = (LinearLayout) itemView.findViewById(R.id.ll_comment_imgs);
        }
    }

    public void setOnItemClickListener(AssessmentAdapter.OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
