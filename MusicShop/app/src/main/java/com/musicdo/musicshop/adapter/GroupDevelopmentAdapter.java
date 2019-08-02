package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicdo.musicshop.R;

import java.util.List;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/8.
 * 版 本 ：
 * 备 注 ：
 */

public class GroupDevelopmentAdapter extends RecyclerView.Adapter<GroupDevelopmentAdapter.MyViewHolder> implements View.OnClickListener{
private List<Integer> mDatas;
private Context mContext;
private LayoutInflater inflater;
private OnItemClickListener mOnItemClickListener = null;

public GroupDevelopmentAdapter(Context context, List<Integer> datas){
        this. mContext=context;
        this. mDatas=datas;
        inflater=LayoutInflater. from(mContext);
        }

@Override
public void onClick(View v) {
        if (mOnItemClickListener != null) {
        //注意这里使用getTag方法获取position
        mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
        }

public  interface OnItemClickListener {
    void onItemClick(View view , int position);
}

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (mDatas.get(position)!=-1) {
            holder.tv_develop_time.setText("12分钟前");
            holder.itemView.setTag(position);
        }
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.group_develop_list_item,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView develop_icon,iv_develop_img1,iv_develop_img2,iv_develop_img3;
    TextView tv_develop_time,tv_develop_title,tv_develop_context;
    public MyViewHolder(View itemView) {
        super(itemView);
        tv_develop_time=(TextView) itemView.findViewById(R.id.tv_develop_time);
        tv_develop_title=(TextView) itemView.findViewById(R.id.tv_develop_title);
        tv_develop_context=(TextView) itemView.findViewById(R.id.tv_develop_context);
        develop_icon=(ImageView) itemView.findViewById(R.id.iv_develop_icon);
        iv_develop_img1=(ImageView) itemView.findViewById(R.id.iv_develop_img1);
        iv_develop_img2=(ImageView) itemView.findViewById(R.id.iv_develop_img2);
        iv_develop_img3=(ImageView) itemView.findViewById(R.id.iv_develop_img3);
    }

}
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
