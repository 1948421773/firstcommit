package com.musicdo.musicshop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.EvaluateBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuedu on 2017/10/11.
 */

public class AdditionalCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,EvaluateImageAdapter.DeleteEvaluateImags{
    private Context mContext;
    View rootView;
    OnSearchItemClickListener mOnItemClickListener;
    private ArrayList<EvaluateBean> datas;//数据
    private LayoutInflater inflater;
    View view;
    boolean isChange=false;
    private boolean showtype=false;
    StringBuffer paramStrBuff;
    OrderAddingImags addingImags;
    OrderUpdateImags updateImags;
    OrderEvaluate evaluate;
    OrderEvaluateNum evaluateNum;
    OrderRatingLevel ratingLevel;
    OrderPro_DesLevel pro_DesLevel;
    OrderLogistics_service logistics_service;
    OrderPro_Service pro_Service;
    EvaluateImageAdapter imageAdapter;
    private boolean ischange=true;
    int indexFocus=0;

    @Override
    public void DeleteImgs(final String path, final String fileName) {
        AlertDialog alert;
        alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle("操作提示");
        alert.setMessage("是否删除图片？");
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SpUtils.deletePhotoAtPathAndName(path,fileName);
                for (EvaluateBean data:datas) {
                    if (data.getImgpath() != null) {
                        for (int i = 0; i < data.getImgpath().size(); i++) {
                            if (data.getImgpath().get(i).equals(path + fileName)) {
                                data.getImgpath().remove(i);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
                updateImags.UpdateImags(datas);
            }
        });
        alert.show();

    }
    //适配器初始化

    public interface OrderAddingImags{
        public void AddingImages(Drawable drawable, int product, int position);
    }
    public interface OrderEvaluateNum{
        public void EvaluateNum(String number,int position);
    }
    public interface OrderUpdateImags{
        public void UpdateImags(ArrayList<EvaluateBean> datas);
    }
    public interface OrderEvaluate{
        public void Evaluate(String content,int position);
    }
    public interface OrderRatingLevel{
        public void ratingLevel(String content,int position);
    }
    public interface OrderPro_DesLevel{
        public void Pro_DesLevel(String content,int position);
    }
    public interface OrderLogistics_service{
        public void logistics_service(String content,int position);
    }
    public interface OrderPro_Service{
        public void pro_Service(String content,int position);
    }

    public void setEvaluateNum(OrderEvaluateNum evaluateNum){
        this.evaluateNum=evaluateNum;
    }
    public void setEvaluate(OrderEvaluate evaluate){
        this.evaluate=evaluate;
    }
    public void setRatingLevel(OrderRatingLevel ratingLevel){
        this.ratingLevel=ratingLevel;
    }
    public void setPro_DesLevel(OrderPro_DesLevel pro_DesLevel){
        this.pro_DesLevel=pro_DesLevel;
    }
    public void setLogistics_service(OrderLogistics_service logistics_service){
        this.logistics_service=logistics_service;
    }
    public void setPro_Service(OrderPro_Service pro_Service){
        this.pro_Service=pro_Service;
    }
    public void setAddingImage(OrderAddingImags orderAddingImags){
        this.addingImags=orderAddingImags;
    }
    public void setUpdateImage(OrderUpdateImags updateImags){
        this.updateImags=updateImags;
    }
    public AdditionalCommentsAdapter(Context context, ArrayList<EvaluateBean> datas){
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
        view = inflater.inflate(R.layout.item_additionalcomments, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    public void upData(int position) {
        ischange=true;
        indexFocus=position;
//        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        /*ImageView im_evaluate_img,im_adding;
        RatingBar room_ratingbar;
        TextView tv_evaluate,tv_evaluate_tips;
        RecyclerView rv_evaluate_photos;
        ((MyViewHolder)holder).tv_name.setText(datas.get(position).get);*/
        if (position==datas.size()-1){
            ((MyViewHolder)holder).ll_payfor_applicants.setVisibility(View.GONE);
        }else{
            ((MyViewHolder)holder).ll_payfor_applicants.setVisibility(View.GONE);
        }
        /*if (position==indexFocus){
            ((MyViewHolder)holder).tv_evaluate.setFocusableInTouchMode(true);
        }*/
        List<String> imgs=datas.get(position).getImgpath();
        if (imgs!=null){
            imageAdapter = new EvaluateImageAdapter(mContext,imgs);
            ((MyViewHolder)holder).rv_evaluate_photos.setLayoutManager(new GridLayoutManager(mContext,3));
            ((MyViewHolder)holder).rv_evaluate_photos.setAdapter(imageAdapter);
//            specialUpdate(((MyViewHolder)holder).tv_evaluate_tips,textNumber+"/120");
            imageAdapter.DoDeleteEvaluateImags(this);
            ((MyViewHolder)holder).rv_evaluate_photos.setNestedScrollingEnabled(false);
        }
        ((MyViewHolder)holder).tv_evaluate.addTextChangedListener(new MyTextWatcher(((MyViewHolder)holder),position));

        ischange =true;
        if (datas.get(position).getEvaluateStringLength()==null) {
            ((MyViewHolder)holder).tv_evaluate.setText("");
            ((MyViewHolder) holder).tv_evaluate_tips.setText("0/120");
        }else{
            ((MyViewHolder)holder).tv_evaluate.setText(datas.get(position).getCommentContent());
            ((MyViewHolder)holder).tv_evaluate.setSelection(datas.get(position).getCommentContent().length());
            ((MyViewHolder) holder).tv_evaluate_tips.setText(datas.get(position).getEvaluateStringLength()+"/120");
        }
        ischange =false;
        ((MyViewHolder)holder).itemView.setTag(position);
        /*EditText editText = ((MyViewHolder)holder).tv_evaluate;
        if (editText.getTag() instanceof MyTextWatcher) {
            editText.removeTextChangedListener((MyTextWatcher) editText.getTag());
        }*/
        //单个item中的商品描述
        ((MyViewHolder)holder).rb_evaluate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingLevel.ratingLevel(String.valueOf(rating),position);
//                ToastUtil.showLong(mContext,""+rating+fromUser);
            }
        });
//        rb_evaluate_shop_describe,rb_logistics_service,rb_service_attitude
        //所有商品描述
        ((MyViewHolder)holder).rb_evaluate_shop_describe.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                pro_DesLevel.Pro_DesLevel(String.valueOf(rating),position);
//                ToastUtil.showLong(mContext,""+rating+fromUser);
            }
        });
        ((MyViewHolder)holder).rb_logistics_service.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                logistics_service.logistics_service(String.valueOf(rating),position);
//                ToastUtil.showLong(mContext,""+rating+fromUser);
            }
        });
        ((MyViewHolder)holder).rb_service_attitude.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                pro_Service.pro_Service(String.valueOf(rating),position);
//                ToastUtil.showLong(mContext,""+rating+fromUser);
            }
        });
//        if (datas.get(position).getCommentLevel()!=null){
//            ((MyViewHolder)holder).rb_evaluate.setRating(Float.valueOf(datas.get(position).getCommentLevel()));
//        }
        if (datas.get(position).getPro_Des()!=null){
            ((MyViewHolder)holder).rb_evaluate_shop_describe.setRating(Float.valueOf(datas.get(position).getPro_Des()));
        }
        if (datas.get(position).getPro_Logistics()!=null){
            ((MyViewHolder)holder).rb_logistics_service.setRating(Float.valueOf(datas.get(position).getPro_Logistics()));
        }
        if (datas.get(position).getPro_Service()!=null){
            ((MyViewHolder)holder).rb_service_attitude.setRating(Float.valueOf(datas.get(position).getPro_Service()));
        }
        ((MyViewHolder)holder).radioGroup.setOnCheckedChangeListener(null);
        if (datas.get(position)!=null){
            if (datas.get(position).getCommentLevel()!=null){
                //rb_dissatisfied,rb_commonly,rb_satisfied
                switch (Integer.valueOf(datas.get(position).getCommentLevel())){
                    case 1:
                        ((MyViewHolder)holder).radioGroup.check(R.id.rb_satisfied);
                        break;
                    case 2:
                        ((MyViewHolder)holder).radioGroup.check(R.id.rb_commonly);
                        break;
                    case 3:
                        ((MyViewHolder)holder).radioGroup.check(R.id.rb_dissatisfied);
                        break;
                    default:
                        ((MyViewHolder)holder).radioGroup.clearCheck();
                        break;
                }
            }else{
                ((MyViewHolder)holder).radioGroup.clearCheck();
            }
        }

        Picasso.with(mContext)
                .load(AppConstants.PhOTOADDRESS+datas.get(position).getSrc())
                .resize(200,200)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(((MyViewHolder)holder).im_evaluate_img);//加载网络图片
        ((MyViewHolder)holder).tv_intro.setText(datas.get(position).getName());
        ((MyViewHolder)holder).tv_contents.setText(datas.get(position).getContents());//追平内容
        ((MyViewHolder)holder).tv_contents_state.setText(datas.get(position).getContents_state());//追平状态
        ((MyViewHolder)holder).tv_contents_time.setText(datas.get(position).getContents_time());//追平时间
        if (imgs!=null){
            if (imgs.size()<5){
                ((MyViewHolder)holder).im_adding.setVisibility(View.VISIBLE);
            }else{
                ((MyViewHolder)holder).im_adding.setVisibility(View.GONE);
            }
        }

        ((MyViewHolder)holder).im_adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingImags.AddingImages(null,1,position);
            }
        });
        ((MyViewHolder)holder).radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.rb_dissatisfied:
                        evaluate.Evaluate("3",position);
//
                        break;
                    case R.id.rb_commonly:
                        evaluate.Evaluate("2",position);
//
                        break;
                    case R.id.rb_satisfied:
                        evaluate.Evaluate("1",position);
//
                        break;
                }
            }
        });
    }


    class MyTextWatcher implements TextWatcher {
        private CharSequence temp;
        private boolean isEdit = true;
        private int selectionStart ;
        private int selectionEnd ;
        private MyViewHolder holder;
        int  position;

        public  MyTextWatcher(MyViewHolder holder,int position){
            this.holder=holder;
            this.position=position;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!ischange) {
//            holder.tv_evaluate.removeTextChangedListener(this);
                selectionStart = ((MyViewHolder) holder).tv_evaluate.getSelectionStart();
                selectionEnd = ((MyViewHolder) holder).tv_evaluate.getSelectionEnd();
//                Log.i("gongbiao1",""+selectionStart);
                if (temp.length() > 120) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
//                                tv_evaluate.setText(s);
//                                specialUpdate("120");
//                                datas.get(position).setEvaluateStringLength("120");
                } else {
//                                datas.get(position).setEvaluateStringLength(String.valueOf(s.toString().length()));
                }
//                    ((MyViewHolder)holder).tv_evaluate.extendSelection(s.length());
//                            evaluateNum.EvaluateNum(s.toString().trim(),position);
                if (s.toString().trim().equals("")) {
//                    return;
                }
                if (temp.length() > 120) {
//                                    datas.get(position).setEvaluateStringLength("120");
                } else {
//                                    datas.get(position).setEvaluateStringLength(String.valueOf(s.toString().length()));
                }
//                                datas.get(position).setCommentContent(s.toString());
                int indexposition=(Integer)((MyViewHolder)holder).itemView.getTag();
                evaluateNum.EvaluateNum(s.toString(),indexposition );
//                    notifyItemChanged(position);
//                holder.tv_evaluate.addTextChangedListener(this);
//                return;
//                                specialUpdate(s.toString().trim(),position);
//            holder.tv_evaluate.addTextChangedListener(this);
            }

        }
    }

    private void specialUpdate(final String text,final int position) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                /*final TextView tv_evaluate_tips=(TextView)view.findViewById(R.id.tv_evaluate_tips);
                tv_evaluate_tips.setText(Number+"/120");*/
//                evaluateNum.EvaluateNum(text,position);
                notifyItemChanged(position);
            }
        };
        handler.post(r);
        isChange=true;
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView im_evaluate_img;
        RatingBar rb_evaluate,
                rb_evaluate_shop_describe,rb_logistics_service,rb_service_attitude;//商品描述，物流服务，服务态度三个评分
        EditText tv_evaluate;
        TextView tv_evaluate_tips,im_adding,tv_intro,tv_contents,tv_contents_state,tv_contents_time;
        RecyclerView rv_evaluate_photos;
        LinearLayout ll_payfor_applicants;
        RadioGroup radioGroup;
        RadioButton rb_dissatisfied,rb_commonly,rb_satisfied;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            ll_payfor_applicants = (LinearLayout) itemView.findViewById(R.id.ll_payfor_applicants);
            im_evaluate_img = (ImageView) itemView.findViewById(R.id.im_evaluate_img);
            rb_evaluate = (RatingBar) itemView.findViewById(R.id.rb_evaluate);
            rb_evaluate_shop_describe = (RatingBar) itemView.findViewById(R.id.rb_evaluate_shop_describe);
            rb_logistics_service = (RatingBar) itemView.findViewById(R.id.rb_logistics_service);
            rb_service_attitude = (RatingBar) itemView.findViewById(R.id.rb_service_attitude);
            tv_evaluate = (EditText) itemView.findViewById(R.id.tv_evaluate);
            tv_evaluate_tips = (TextView) itemView.findViewById(R.id.tv_evaluate_tips);
            tv_intro = (TextView) itemView.findViewById(R.id.tv_intro);
            tv_contents = (TextView) itemView.findViewById(R.id.tv_contents);
            tv_contents_state = (TextView) itemView.findViewById(R.id.tv_contents_state);
            tv_contents_time = (TextView) itemView.findViewById(R.id.tv_contents_time);
            rv_evaluate_photos = (RecyclerView) itemView.findViewById(R.id.rv_evaluate_photos);
            im_adding = (TextView) itemView.findViewById(R.id.im_adding);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radioGroup);
            rb_dissatisfied = (RadioButton) itemView.findViewById(R.id.rb_dissatisfied);
            rb_commonly = (RadioButton) itemView.findViewById(R.id.rb_commonly);
            rb_satisfied = (RadioButton) itemView.findViewById(R.id.rb_satisfied);
        }
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolderFooterView extends RecyclerView.ViewHolder{
        SimpleDraweeView iv_popularity_icon;
        TextView tv_searchprod_item_title;
        TextView tv_popularity_salenumber;
        public MyViewHolderFooterView(View itemView)
        {
            super(itemView);
            iv_popularity_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_popularity_icon);
            tv_searchprod_item_title = (TextView) itemView.findViewById(R.id.tv_searchprod_item_title);
            tv_popularity_salenumber = (TextView) itemView.findViewById(R.id.tv_popularity_salenumber);
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
    public void addMoreItem(ArrayList<EvaluateBean> newdatas){
        datas.addAll(newdatas);
        notifyDataSetChanged();
    }
}