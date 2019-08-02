package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.QrcodeActivity;
import com.musicdo.musicshop.constant.AppConstants;
import com.squareup.picasso.Picasso;

/**
 * 图片轮播适配器
 */
public class NetworkImageHolderView implements Holder<String> {
    private View rootview;
    private SimpleDraweeView imageView;
    private LinearLayout ll_head_img;
    int with,height;
    public NetworkImageHolderView(int screenWidth, int screenHeight) {
        this.with=screenWidth;
        this.height=screenHeight;
    }

    @Override
    public View createView(Context context) {
        rootview = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.goods_item_head_img, null);
        imageView = (SimpleDraweeView) rootview.findViewById(R.id.sdv_item_head_img);
        ll_head_img = (LinearLayout) rootview.findViewById(R.id.ll_head_img);
        return rootview;
    }

    @Override
    public void UpdateUI(final Context context, final int position, String data) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearParams.width =AppConstants.ScreenWidth;
        linearParams.height =AppConstants.ScreenHeight/2;
        ll_head_img.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        /*Picasso.with(context)
                .load(Uri.parse(data))
                .resize(with,height)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(imageView);//加载网络图片*/
        imageView.setImageURI(data);
        if (with==200&&height==200){
            LinearLayout.LayoutParams linearParamsPic = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            linearParamsPic.width = AppConstants.ScreenWidth *21/ 50;
            linearParamsPic.height =AppConstants.ScreenWidth *21/ 50;
            imageView.setLayoutParams(linearParamsPic);
        }

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0:{
                        Intent intent=new Intent(context, QrcodeActivity.class);
                        context.startActivity(intent);
                    }
                    break;
                }
            }
        });*/

    }
}
