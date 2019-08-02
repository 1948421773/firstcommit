package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.SearchProdBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.view.RecyclerImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.mylibrary.util.PtrLocalDisplay.dp2px;

/**
 * 描述:
 * 作者：haiming on 2017/7/28 11:37
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SearchProdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<SearchProdBean> datas;//数据
    private LayoutInflater inflater;
    private static final int TYPE_ITEM_LIST = 0;
    private static final int TYPE_FOOTER_GRID = 1;
    private boolean showtype=false;
    private int chooseView=0;
    Object tag;
    //适配器初始化

    public SearchProdAdapter(Context context, ArrayList<SearchProdBean> datas, boolean showtype, Object tag){
        mContext=context;
        this.datas=datas;
        this.showtype=showtype;
        this.tag=tag;
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
        if (viewType == TYPE_ITEM_LIST) {
            chooseView=0;
            View view = inflater.inflate(R.layout.searchprod_list_item, parent, false);//这个布局就是一个imageview用来显示图片
            SearchProdAdapter.MyViewHolder holder = new SearchProdAdapter.MyViewHolder(view);
            view.setOnClickListener(this);
            return holder;
        }else{
            chooseView=1;
            View view = inflater.inflate(R.layout.searchprod_grid_item, parent, false);
            SearchProdAdapter.MyViewHolderFooterView holderFooterView = new SearchProdAdapter.MyViewHolderFooterView(view);
            view.setOnClickListener(this);
            return holderFooterView;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RequestOptions options = new RequestOptions();
        options.fitCenter();
        if(holder instanceof MyViewHolder) {
//            ((MyViewHolder)holder).iv_popularity_icon.setImageURI(Uri.parse(String.valueOf(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail())));

//            Picasso.with(mContext)
//                    .load(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail())
////                .resize(ScreenUtil.getScreenWidth(mContext)*11/20,ScreenUtil.getScreenHeight(mContext)*5/16)
//                    .resize(AppConstants.ScreenWidth *12/ 25,AppConstants.ScreenWidth *12/ 25)
////                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
////                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
//                    .config(Bitmap.Config.ALPHA_8)
//                    .placeholder(R.mipmap.img_start_loading)
////                    .tag("list")
//                    .centerInside()
//                    .priority(Picasso.Priority.LOW)
//                    .error(R.mipmap.img_load_error)
//                    .into(((MyViewHolder)holder).iv_popularity_icon);

//            RelativeLayout.LayoutParams linearParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//            linearParams.width=ScreenUtil.getScreenWidth(mContext)*11/25;
//            linearParams.height=ScreenUtil.getScreenHeight(mContext)*5/20;
//            ((MyViewHolder)holder).iv_popularity_icon.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
            ((MyViewHolder)holder).tv_searchprod_item_title.setText(datas.get(position).getName());
            ((MyViewHolder)holder).tv_popularity_price.setText(mContext.getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(datas.get(position).getMemberPrice()));
            ((MyViewHolder)holder).tv_popularity_salenumber.setText("月销"+String.valueOf(datas.get(position).getSaleCount()));

//            ((MyViewHolder) holder).iv_popularity_icon.setImageURI(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            ((MyViewHolder) holder).setImg(((MyViewHolder) holder).iv_popularity_icon,AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            RelativeLayout.LayoutParams linearParamsPic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            linearParamsPic.width = AppConstants.ScreenWidth * 21 / 50;
            linearParamsPic.height = AppConstants.ScreenWidth * 21 / 50;
            ((MyViewHolder) holder).iv_popularity_icon.setLayoutParams(linearParamsPic);

//            Glide.with(mContext).load(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail()).apply(options).into(((MyViewHolder) holder).iv_item_icon);

            RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            linearParams.width = AppConstants.ScreenWidth *12/ 25;
            linearParams.height =AppConstants.ScreenWidth *12/ 25;
            ((MyViewHolder)holder).rl_search_item_img.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
            ((MyViewHolder)holder).itemView.setTag(position);
        }else{
            ((MyViewHolderFooterView)holder).tv_popularity_price.setText(mContext.getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(datas.get(position).getMemberPrice()));
//            ((MyViewHolderFooterView)holder).iv_popularity_icon.setImageURI(AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            ((MyViewHolderFooterView) holder).setImg(((MyViewHolderFooterView) holder).iv_popularity_icon,AppConstants.PhOTOADDRESS + datas.get(position).getSrcDetail());
            ((MyViewHolderFooterView)holder).tv_searchprod_item_title.setText(datas.get(position).getName());
            ((MyViewHolderFooterView)holder).tv_popularity_salenumber.setText("月销"+String.valueOf(datas.get(position).getSaleCount()));
            ((MyViewHolderFooterView)holder).itemView.setTag(position);

        }
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
        SimpleDraweeView  iv_popularity_icon;
        TextView tv_searchprod_item_title;
        TextView tv_popularity_salenumber;
        TextView tv_popularity_price;
        RelativeLayout rl_search_item;
        RelativeLayout rl_search_item_img;
//        ImageView iv_item_icon;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            rl_search_item = (RelativeLayout) itemView.findViewById(R.id.rl_search_item);
            rl_search_item_img = (RelativeLayout) itemView.findViewById(R.id.rl_search_item_img);
            iv_popularity_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_popularity_icon);
            tv_searchprod_item_title = (TextView) itemView.findViewById(R.id.tv_searchprod_item_title);
            tv_popularity_salenumber = (TextView) itemView.findViewById(R.id.tv_popularity_salenumber);
            tv_popularity_price = (TextView) itemView.findViewById(R.id.tv_popularity_price);
//            iv_item_icon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
        }


        public void setImg(SimpleDraweeView  mImg, String url) {
            Uri uri = Uri.parse(url);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(400, 400))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(mImg.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>())
                    .setImageRequest(request).build();
            mImg.setController(controller);

        }
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolderFooterView extends RecyclerView.ViewHolder{
        SimpleDraweeView  iv_popularity_icon;
        TextView tv_searchprod_item_title;
        TextView tv_popularity_salenumber;
        TextView tv_popularity_price;

        public MyViewHolderFooterView(View itemView)
        {
            super(itemView);
            iv_popularity_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_popularity_icon);
            tv_searchprod_item_title = (TextView) itemView.findViewById(R.id.tv_searchprod_item_title);
            tv_popularity_salenumber = (TextView) itemView.findViewById(R.id.tv_popularity_salenumber);
            tv_popularity_price = (TextView) itemView.findViewById(R.id.tv_popularity_price);

        }
        public void setImg(SimpleDraweeView  mImg, String url) {
            Uri uri = Uri.parse(url);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(400, 400))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(mImg.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>())
                    .setImageRequest(request).build();
            mImg.setController(controller);

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
    public void addMoreItem(ArrayList<SearchProdBean> newdatas){
        datas.addAll(newdatas);
        notifyDataSetChanged();
    }



}
