package com.musicdo.musicshop.loopswitch;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.musicdo.loopswitch.AutoLoopSwitchBaseAdapter;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.HomeBannerBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class AutoSwitchAdapter extends AutoLoopSwitchBaseAdapter {

  private Context mContext;
  private boolean isstop;
  private ArrayList<HomeBannerBean> mDatas=new ArrayList<>();

  public AutoSwitchAdapter() {
    super();
  }

  public AutoSwitchAdapter(Context mContext, ArrayList<HomeBannerBean> mDatas) {
    this.mContext = mContext;
    this.mDatas = mDatas;
  }

  @Override
  public int getDataCount() {
    return mDatas == null ? 0 : mDatas.size();
  }

  @Override
  public View getView(final int position) {
    ImageView imageView = new ImageView(mContext);
    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
    HomeBannerBean model = (HomeBannerBean) getItem(position);
    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    Picasso.with(mContext)
            .load(AppConstants.PhOTOADDRESS+model.getImgUrl())
            .resize(AppConstants.ScreenWidth,AppConstants.ScreenHeight/4)
            .centerCrop()
            .config(Bitmap.Config.RGB_565)
            .placeholder(R.mipmap.img_start_loading)
            .error(R.mipmap.img_load_error)
            .into(imageView);
    /*imageView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
          case MotionEvent.ACTION_UP:
            Log.i("AutoSwitchAdapter","imageView^^^^^^onTouch^^^^ACTION_UP");
            break;
          case MotionEvent.ACTION_DOWN:
            Log.i("AutoSwitchAdapter","imageView^^^^^^onTouch^^^^ACTION_DOWN");
            break;
          default:break;
        }
        return false;
      }
    });*/
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        Toast.makeText(mContext,"点击了"+position,Toast.LENGTH_SHORT).show();
      }
    });
    return imageView;
  }

  @Override
  public Object getItem(int position) {
    if (position >= 0 && position < getDataCount()) {

      return mDatas.get(position);
    }
    return null;
  }


  @Override
  public View getEmptyView() {
    return null;
  }

  @Override
  public void updateView(View view, int position) {

  }



  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    super.destroyItem(container, position, object);
  }

}
