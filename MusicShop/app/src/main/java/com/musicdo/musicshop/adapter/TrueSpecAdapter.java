package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.TruespecBean;

import java.util.ArrayList;

/**
 * 描述:
 * 作者：haiming on 2017/8/5 11:25
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class TrueSpecAdapter extends RecyclerView.Adapter<TrueSpecAdapter.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<TruespecBean> datas;//数据
    private LayoutInflater inflater;
    private int showtype=0;
    private CheckInterface checkInterface;
    //适配器初始化

    public TrueSpecAdapter(Context context,ArrayList<TruespecBean> datas){
        mContext=context;
        this.datas=datas;
        inflater = LayoutInflater.from(mContext);
    }

    public interface CheckInterface
    {
        public void checkChild(int groudPosition,int childPosition);
    }

    public void setCheckInterface(CheckInterface checkInterface)
    {
        this.checkInterface = checkInterface;
    }
    //自定义监听事件
    public static interface OnSearchItemClickListener{
        void onItemClick(View view, int position);
    }

    public void changeShowType(int type){
        showtype=type;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view= inflater.inflate(R.layout.item_truespec, parent, false);//这个布局就是一个imageview用来显示图片
        return new TrueSpecAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_truespec_name.setText(datas.get(position).getTitle());
        holder.tv_truespec_value.setText(datas.get(position).getName());
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_truespec_name;
        TextView tv_truespec_value;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_truespec_name = (TextView) itemView.findViewById(R.id.tv_truespec_name);
            tv_truespec_value = (TextView) itemView.findViewById(R.id.tv_truespec_value);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();//获取数据的个数;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_spec_color_chekbox:
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
                checkInterface.checkChild(0,(int) v.getTag());// 暴露组选接口,颜色组下标为0
                break;
        }
    }

    public void setOnItemClickListener(OnSearchItemClickListener listener){
        mOnItemClickListener=listener;
    }
    public void addMoreItem(ArrayList<TruespecBean> newdatas){
        datas.addAll(newdatas);
        notifyDataSetChanged();
    }
}