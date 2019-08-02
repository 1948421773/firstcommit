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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.SearchProuductActivity;
import com.musicdo.musicshop.bean.HomeSecondBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/1.
 * 版 本 ：picasso.resumeTag(context);
 * 备 注 ：
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> implements View.OnClickListener {
   private Context mContext;
    private ArrayList<HomeSecondBean> datas;//数据
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //适配器初始化
    public GridAdapter(Context context,ArrayList<HomeSecondBean> datas) {
        mContext=context;
        this.datas=datas;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //根据item类别加载不同ViewHolder
        View view = inflater.inflate(R.layout.grid_meizi_item, parent,false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        switch (position){
            case 0:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.DimGray));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.DimGray));
                break;
            case 1:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.Turquoise));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.Turquoise));
                break;
            case 2:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.LimeGreen));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.LimeGreen));
                break;
            case 3:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.RosyBrown));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.RosyBrown));
                break;
            case 4:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.LightCoral));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.LightCoral));
                break;
            case 5:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.MediumPurple));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.MediumPurple));
                break;
            case 6:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.CornflowerBlue));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.CornflowerBlue));
                break;
            case 7:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.HotPink));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.HotPink));
                break;
            case 8:
                holder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.HotPink));
                holder.Group_title.setTextColor(mContext.getResources().getColor(R.color.HotPink));
                break;
        }
        if (datas.get(position).get_List1()!=null){//添加主页每个分类中的广告图
            for (int i = 0; i <datas.get(position).get_List1().size() ; i++) {
                View rootview = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.goods_item_head_img, null);
                SimpleDraweeView imageView=(SimpleDraweeView) rootview.findViewById(R.id.sdv_item_head_img);
                Picasso.with(mContext)
                        .load(AppConstants.PhOTOADDRESS+datas.get(position).get_List1().get(i).Image)
                        .resize(AppConstants.ScreenWidth,(AppConstants.ScreenWidth*Integer.valueOf(datas.get(position).get_List1().get(i).getGao()))/Integer.valueOf(datas.get(position).get_List1().get(i).getKaun()))
//                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
//                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.mipmap.img_start_loading)
                        .error(R.mipmap.img_load_error)
                        .into(imageView);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(0,4,0,0);
                holder.ll_AdInfo1.addView(rootview,lp1);
            }

            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            // btn1 位于父 View 的顶部，在父 View 中水平居中

        }

        holder.Group_title.setText(datas.get(position).getName());
        if(datas.get(position).get_List().size()==0){
            holder.rl_brand_gruop.setVisibility(View.GONE);
        }else{
            holder.rl_brand_gruop.setVisibility(View.VISIBLE);
        }
        holder.Group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SearchProuductActivity.class);
                intent.putExtra("categoryID",datas.get(position).getCategoryID());
                mContext.startActivity(intent);
            }
        });
        HomeSecondItemRecylistAdapter RecylistAdapter= new HomeSecondItemRecylistAdapter(mContext, datas.get(position).get_List(),false);
        holder.rc_searchprod_overall.setLayoutManager( new GridLayoutManager(mContext, 2));
        holder.rc_searchprod_overall.setAdapter(RecylistAdapter);
//        childHolder.rc_searchprod_overall.setNestedScrollingEnabled(false);
//        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//        linearParams.width= ScreenUtil.getScreenWidth(mContext);
//        linearParams.height=ScreenUtil.getScreenHeight(mContext);
//        gridView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        RecylistAdapter.setOnItemClickListener(new HomeSecondItemRecylistAdapter.OnSearchItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
//                Toast.makeText(mContext, "点击了第" + (groupPosition + 1) + "组，第" +
//                        (position + 1) + "项", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,ProductDetailActivity.class);
                int id=datas.get(position).get_List().get(index).getID();
                intent.putExtra("prod_id",id);
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setTag(position);
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView Group_line;
        ImageView im_AdInfo1;
        TextView Group_title;
        TextView Group_more;
        RecyclerView rc_searchprod_overall;
        RelativeLayout rl_brand_gruop;
        LinearLayout ll_AdInfo1;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Group_line = (ImageView) itemView.findViewById(R.id.im_title_line);
            im_AdInfo1 = (ImageView) itemView.findViewById(R.id.im_AdInfo1);
            Group_title = (TextView) itemView.findViewById(R.id.tv_title);
            Group_more = (TextView) itemView.findViewById(R.id.tv_homesecond_more);
            rc_searchprod_overall = (RecyclerView) itemView.findViewById(R.id.rc_searchprod_overall);
            rl_brand_gruop = (RelativeLayout) itemView.findViewById(R.id.rl_brand_gruop);
            ll_AdInfo1 = (LinearLayout) itemView.findViewById(R.id.ll_AdInfo1);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}