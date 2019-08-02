package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;

import java.util.List;

/**
 * Created by Yuedu on 2017/9/13.
 */

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.MyViewHolder> implements View.OnClickListener{

    private List<Drawable> mDatas;
    private List<String> mDatasTitle;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener = null;
    public PersonalAdapter(Context context, List<Drawable> datas,List<String> mDatasTitle){
        this. mContext=context;
        this. mDatas=datas;
        this. mDatasTitle=mDatasTitle;
        inflater=LayoutInflater. from(mContext);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.list_item_iv.setBackground( mDatas.get(position));
        holder.list_item_tv.setText(mDatasTitle.get(position));
        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        linearParams.width= AppConstants.ScreenWidth/4;
        linearParams.height= AppConstants.ScreenHeight/10;
//        holder.list_item_iv.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        holder.ll_personal_item.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        holder.itemView.setTag(position);
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_personal_tools,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView list_item_iv;
        TextView list_item_tv;
        LinearLayout ll_personal_item;
        public MyViewHolder(View itemView) {
            super(itemView);
            list_item_iv=(ImageView) itemView.findViewById(R.id. list_item_iv);
            list_item_tv=(TextView) itemView.findViewById(R.id. list_item_tv);
            ll_personal_item=(LinearLayout) itemView.findViewById(R.id. ll_personal_item);
        }

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
