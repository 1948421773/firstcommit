package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ClassifyProuductActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.SearchProuductActivity;
import com.musicdo.musicshop.bean.GridItem;
import com.musicdo.musicshop.bean.HomeBrandBean;
import com.musicdo.musicshop.bean.HotBrandBean;
import com.musicdo.musicshop.bean.RecommendBrandBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.ToastUtil;
import com.squareup.picasso.Picasso;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;


/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/7.
 * 版 本 ：
 * 备 注 ：
 */

public class StickyGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    private List<GridItem> list;
    private String categroyname;
    private LayoutInflater mInflater;

    private Context mContext;

    public StickyGridAdapter(Context context, List<GridItem> list,String categroyname) {

        this.mContext=context;
        this.categroyname=categroyname;
        this.list= list;

        mInflater= LayoutInflater.from(context);

    }

    @Override

    public int getCount() {

        return list.size();

    }

    @Override

    public Object getItem(int position) {

        return list.get(position);

    }

    @Override

    public long getItemId(int position) {

        return position;

    }

    @Override

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if(convertView ==null) {

            mViewHolder =new ViewHolder();

            convertView =mInflater.inflate(R.layout.category_item_right_itemview,parent, false);

            mViewHolder.tv_griditem= (TextView) convertView.findViewById(R.id.list_item_tv);
            mViewHolder.list_item_iv= (ImageView) convertView.findViewById(R.id.list_item_iv);
            mViewHolder.rl_list_item= (RelativeLayout) convertView.findViewById(R.id.rl_list_item);

            convertView.setTag(mViewHolder);

        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
//        mViewHolder.list_item_iv.setImageURI(AppConstants.PhOTOADDRESS+list.get(position).getPath());
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearParams.width = AppConstants.ScreenWidth / 6;
        linearParams.height = ((AppConstants.ScreenWidth / 6)*3)/4+20;
//        linearParams.width = 200;
//        linearParams.height = 200;
        mViewHolder.rl_list_item.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        mViewHolder.tv_griditem.setText(list.get(position).getTime());
        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+list.get(position).getPath())
                .resize(AppConstants.ScreenWidth / 6,((AppConstants.ScreenWidth / 6)*3)/4)
//                .resize(400,400)
//                .centerCrop()
//                .onlyScaleDown()//如果图片规格大于6000*2000,将只会被resize
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(mViewHolder.list_item_iv);
//        mViewHolder.list_item_iv.setImageURI(AppConstants.PhOTOADDRESS+list.get(position).getPath());
        convertView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//                Toast.makeText(mContext,String.valueOf(list.get(position).getHeaderId()),Toast.LENGTH_SHORT).show();
                if (list.get(position).getHeaderId()!=-1){//-1即为产品分类，其他为品牌分类
                    Intent intent = new Intent(mContext, ClassifyProuductActivity.class);//跳转搜索带上分类ID categoryID
//                    intent.putExtra("categoryID",list.get(position).getProdid());
                    String brandIDString=String.valueOf(list.get(position).getProdid());
                    intent.putExtra("keyword",list.get(position).getTime());
                      intent.putExtra("brandIDString", brandIDString);
                    mContext.startActivity(intent);
//                    GetProductDetail();
                }else{
//                    GetBrandDetail();
                    Intent intent=new Intent(mContext, ClassifyProuductActivity.class);
//                    intent.putExtra("keyword",list.get(position).getTime());
                    intent.putExtra("categoryID",list.get(position).getProdid());
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    /**
     * 单个商品进入的页面信息
     * @param
     */
    private void GetProductDetail() {
        OkHttpUtils.get(AppConstants.GetBrandDetail)
                .tag(this)
//                .params("ID",ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i("StickyGridAdapter", "onSuccess: 加载成功");
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        String HotBrands=null;
                        String RecommendBrands=null;
                        String HomeBrands=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            HotBrands = new JSONObject(jsonData).getString("HotBrand");//
                            RecommendBrands = new JSONObject(jsonData).getString("RecommendBrand");//
                            HomeBrands = new JSONObject(jsonData).getString("HomeBrand");//
                            message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(mContext,message);
                        //进入单个商品详情
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }
                });
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getHeaderId();
    }

    @Override
    public View getHeaderView(final int position,View convertView,ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if(convertView ==null) {
            mHeaderHolder =new HeaderViewHolder();
            convertView =mInflater.inflate(R.layout.category_item_right_itemhead,parent, false);
            mHeaderHolder.mTextView= (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(mHeaderHolder);
        }else{
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        String headertitle=null;
        switch (list.get(position).getHeaderId()){
            case 0:
                headertitle="推荐品牌";
                mHeaderHolder.mTextView.setVisibility(View.VISIBLE);
                break;
            case 1:
                headertitle="国内品牌";
                mHeaderHolder.mTextView.setVisibility(View.VISIBLE);
                break;
            case 2:
                headertitle="国际品牌";
                mHeaderHolder.mTextView.setVisibility(View.VISIBLE);
                break;
            default:
                headertitle=categroyname;
                mHeaderHolder.mTextView.setVisibility(View.VISIBLE);
                break;
        }
        mHeaderHolder.mTextView.setText(headertitle);
        mHeaderHolder.mTextView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                Toast.makeText(mContext,list.get(position).getPath()+"",Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        public TextView tv_griditem;
        public ImageView list_item_iv;
        public RelativeLayout rl_list_item;
//        public SimpleDraweeView list_item_iv;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }
    /**
     * 13.单个品牌信息
     * @param
     */
    private void GetBrandDetail() {
        OkHttpUtils.get(AppConstants.GetBrandDetail)
                .tag(this)
//                .params("ID",ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i("StickyGridAdapter", "onSuccess: 加载成功");
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        String HotBrands=null;
                        String RecommendBrands=null;
                        String HomeBrands=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            HotBrands = new JSONObject(jsonData).getString("HotBrand");//
                            RecommendBrands = new JSONObject(jsonData).getString("RecommendBrand");//
                            HomeBrands = new JSONObject(jsonData).getString("HomeBrand");//
                            message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(mContext,message);
                        //单个品牌json
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }
                });
    }
}
