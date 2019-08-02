package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.HomeSecondBean;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.bean.PersonalRecommend;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A on 2017/7/10.
 */

public class ItemEducationAdapter extends RecyclerView.Adapter<ItemEducationAdapter.MyViewHolder> implements View.OnClickListener {
private Context mContext;
    ArrayList<PersonalRecommend> homeSecondBeans=new ArrayList<>();
    private LayoutInflater inflater;
    private OnSearchItemClickListener mOnItemClickListener = null;
    //适配器初始化
    public ItemEducationAdapter(Context context,ArrayList<PersonalRecommend> datas) {
        mContext=context;
        this.homeSecondBeans=datas;
        inflater = LayoutInflater.from(mContext);
    }

    //自定义监听事件
    public static interface OnSearchItemClickListener{
        void onItemClick(View view, int position);
    }

    //点击事件回调
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }


    @Override
    public int getItemCount()
    {
        return homeSecondBeans.size();//获取数据的个数
    }

    @Override
    public ItemEducationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //根据item类别加载不同ViewHolder
        View view = inflater.inflate(R.layout.grid_education_item, parent,false);//这个布局就是一个imageview用来显示图片
        ItemEducationAdapter.MyViewHolder holder = new ItemEducationAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemEducationAdapter.MyViewHolder holder, int position) {
        ((MyViewHolder)holder).tv_home_grid_item_title.setText(homeSecondBeans.get(position).getName());
        String MemberPrice=SpUtils.PriceTwoDecimal(String.valueOf(homeSecondBeans.get(position).getMemberPrice()));
        if (MemberPrice!=null&&!MemberPrice.equals("")){
            String[] MemberPrices=MemberPrice.split("\\.");
            ((MyViewHolder)holder).tv_home_grid_item_price.setText(MemberPrices[0]);
            ((MyViewHolder)holder).tv_decimal.setText("."+MemberPrices[1]);
        }
//        Picasso.with(mContext)
//                .load(AppConstants.PhOTOADDRESS+homeSecondBeans.get(position).getSrcDetail())
////                .resize(ScreenUtil.getScreenWidth(mContext)*11/20,ScreenUtil.getScreenHeight(mContext)*5/16)
//                .resize(AppConstants.ScreenWidth *21/ 50,AppConstants.ScreenWidth *21/ 50)
////                .onlyScaleDown()//调用scaleDown(true)只有当原图比目标更大时才去请求resize()。
////                .resize(ScreenUtil.getScreenWidth(mContext)*11/25,ScreenUtil.getScreenHeight(mContext)*8/20)
//                .config(Bitmap.Config.RGB_565)
//                .placeholder(R.mipmap.img_start_loading)
//                .error(R.mipmap.img_load_error)
//                .into(((MyViewHolder)holder).homeitem_icon);
        ((MyViewHolder) holder).homeitem_icon.setImageURI(Uri.parse(AppConstants.PhOTOADDRESS + homeSecondBeans.get(position).getSrcDetail()));
        RelativeLayout.LayoutParams linearParamsPic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        linearParamsPic.width = AppConstants.ScreenWidth * 21 / 50;
        linearParamsPic.height = AppConstants.ScreenWidth * 21 / 50;
        ((MyViewHolder) holder).homeitem_icon.setLayoutParams(linearParamsPic);
//        2560×1536像素 546ppi
//        if (ScreenUtil.getScreenHeight(mContext)>2500) {
        RelativeLayout.LayoutParams relaticeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relaticeParams.width = AppConstants.ScreenWidth *12/ 25;
        relaticeParams.height = AppConstants.ScreenWidth *12/ 25;
        ((MyViewHolder)holder).rl_homesecond_item.setLayoutParams(relaticeParams); //使设置好的布局参数应用到控件
        ((MyViewHolder)holder).itemView.setTag(position);
    }

//自定义ViewHolder，用于加载图片
class MyViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_homesecond_item ;
    RelativeLayout rl_image ;
    ImageView homeitem_icon;
    //        SimpleDraweeView homeitem_icon;
    TextView tv_home_grid_item_title ;
    TextView tv_home_grid_item_price,tv_decimal ;
    public MyViewHolder(View itemView)
    {
        super(itemView);
        rl_homesecond_item = (RelativeLayout) itemView.findViewById(R.id.rl_homesecond_item);
//            rl_image = (RelativeLayout) itemView.findViewById(R.id.rl_image);
        homeitem_icon = (ImageView) itemView.findViewById(R.id.homeitem_icon);
        tv_home_grid_item_title = (TextView) itemView.findViewById(R.id.tv_home_grid_item_title);
        tv_home_grid_item_price = (TextView) itemView.findViewById(R.id.tv_home_grid_item_price);
        tv_decimal = (TextView) itemView.findViewById(R.id.tv_decimal);
    }
}

    public void setOnItemClickListener(ItemEducationAdapter.OnSearchItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}