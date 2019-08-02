package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Yuedu on 2017/9/13.
 */

public class MyGridViewAdapter extends BaseAdapter {
    private Context mContext;

    /**
     * 每个分组下的每个子项的 GridView 数据集合
     */
    private List<MusicalBean> itemGridList;

    public MyGridViewAdapter(Context mContext, List<MusicalBean> itemGridList) {
        this.mContext = mContext;
        this.itemGridList = itemGridList;
    }

    @Override
    public int getCount() {
        return itemGridList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemGridList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_homesecond_gridview, null);
            viewHolder=new ViewHolder();
            viewHolder.rl_homesecond_item = (RelativeLayout) convertView.findViewById(R.id.rl_homesecond_item);
//            viewHolder.rl_image = (RelativeLayout) convertView.findViewById(R.id.rl_image);
            viewHolder.homeitem_icon = (ImageView) convertView.findViewById(R.id.homeitem_icon);
            viewHolder.tv_home_grid_item_title = (TextView) convertView.findViewById(R.id.tv_home_grid_item_title);
            viewHolder.tv_home_grid_item_price = (TextView) convertView.findViewById(R.id.tv_home_grid_item_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_home_grid_item_title.setText(itemGridList.get(position).getName());
        viewHolder.tv_home_grid_item_price.setText(mContext.getResources().getString(R.string.pricesymbol)+itemGridList.get(position).getMemberPrice());
//        viewHolder.homeitem_icon.setImageURI(AppConstants.PhOTOADDRESS+itemGridList.get(position).getSrcDetail());
        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+itemGridList.get(position).getSrcDetail())
//                .resize(ScreenUtil.getScreenWidth(mContext)*11/20,ScreenUtil.getScreenHeight(mContext)*5/16)
                .resize(200,200)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(viewHolder.homeitem_icon);
//        2560×1536像素 546ppi
//        if (ScreenUtil.getScreenHeight(mContext)>2500) {
            /*RelativeLayout.LayoutParams relaticeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            relaticeParams.width = ScreenUtil.getScreenWidth(mContext) / 2;
            relaticeParams.height = ScreenUtil.getScreenHeight(mContext) * 5 / 20;
        viewHolder.rl_image.setLayoutParams(relaticeParams); //使设置好的布局参数应用到控件*/
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            linearParams.width = AppConstants.ScreenWidth / 2;
            linearParams.height = AppConstants.ScreenHeight * 8 / 20;
        viewHolder.rl_homesecond_item.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
//        }else if (ScreenUtil.getScreenHeight(mContext)>1200){
//
//        }
//        2048 x 1536 分辨率，326 ppi
        return convertView;
    }
   class ViewHolder{
       RelativeLayout rl_homesecond_item ;
       RelativeLayout rl_image ;
       ImageView homeitem_icon;
       TextView tv_home_grid_item_title ;
       TextView tv_home_grid_item_price ;
    }
}
