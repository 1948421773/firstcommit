package com.musicdo.musicshop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/7.
 */

public class EvaluateImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private List<String> datas;//数据
    private LayoutInflater inflater;
    View view;
    private boolean showtype=false;
    StringBuffer paramStrBuff;
    DeleteEvaluateImags deleteEvaluateImags;
    //适配器初始化

    public interface DeleteEvaluateImags{
        public void DeleteImgs(String path, String fileName);
    }

    public void DoDeleteEvaluateImags(DeleteEvaluateImags deleteEvaluateImags){
        this.deleteEvaluateImags=deleteEvaluateImags;
    }
    public EvaluateImageAdapter(Context context, List<String> datas){
        mContext=context;
        this.datas=datas;
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
        view = inflater.inflate(R.layout.item_evaluateimage, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        /*ImageView im_evaluate_img,im_adding;
        RatingBar room_ratingbar;
        TextView tv_evaluate,tv_evaluate_tips;
        RecyclerView rv_evaluate_photos;
        ((MyViewHolder)holder).tv_name.setText(datas.get(position).get);*/
        ((MyViewHolder)holder).im_evaluate_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String completePath=datas.get(position);
                String name=completePath.substring(completePath.lastIndexOf("/")+1,completePath.length());
                String path=completePath.substring(0,completePath.lastIndexOf("/")+1);
                deleteEvaluateImags.DeleteImgs(path,name);
            }
        });
        Picasso.with(mContext)
                .load(new File(datas.get(position)))
                .resize(200,200)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
//                .transform(new CircleTransform())设置圆角图片
                .into(((MyViewHolder)holder).im_evaluate);//加载网络图片
        ((MyViewHolder)holder).itemView.setTag(position);
    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView im_evaluate,im_evaluate_delete;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            im_evaluate = (ImageView) itemView.findViewById(R.id.im_evaluate);
            im_evaluate_delete = (ImageView) itemView.findViewById(R.id.im_evaluate_delete);
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