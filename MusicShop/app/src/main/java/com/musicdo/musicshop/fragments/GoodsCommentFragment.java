package com.musicdo.musicshop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.adapter.AssessmentAdapter;
import com.musicdo.musicshop.bean.CommentBean;
import com.musicdo.musicshop.bean.ProCommentBean;
import com.musicdo.musicshop.bean.ProductDetailBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * item页ViewPager里的评价Fragment
 */
public class GoodsCommentFragment extends Fragment {
    public TextView tv_comment_count, tv_good_comment;
    public ProductDetailActivity activity;
    public LinearLayout ll_empty_comment;
    public RecyclerView rv_product_RecyclerView_list;
    private AssessmentAdapter assessmentAdapter;
    int productId;
    int ShopID;
    int Type=0;
    int PageIndex=1;
    int PageSize=50;

    int comment_all,comment_additional,comment_praise,comment_general,comment_bad,comment_hasimg;
    ProductDetailBean ProductDetailBeans=new ProductDetailBean();
    ArrayList<ProCommentBean> data = new ArrayList<>();

//    ArrayList<ProCommentBean> data = new ArrayList<>();
    CommentBean commentBeans=new CommentBean();
    private RadioButton rb_prodetail_comment_all,rb_prodetail_comment_additional,rb_prodetail_comment_praise,
            rb_prodetail_comment_general,rb_prodetail_comment_bad,rb_prodetail_comment_hasimg;
    private RadioGroup rg_prodetail_comment;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ProductDetailActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ToastUtil.showShort(activity,"初始化GoodsCommentFragment");
        View rootView = inflater.inflate(R.layout.fragment_goods_comment, null);
        productId=getArguments().getInt("ID",0);
        ShopID=getArguments().getInt("ShopID",0);
        commentBeans=getArguments().getParcelable("comment");
        initView(rootView);
        GetData();
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("productId",productId);
        outState.putInt("ShopID",ShopID);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null){
            productId=savedInstanceState.getInt("productId",0);
            ShopID=savedInstanceState.getInt("ShopID",0);
        }

    }

    private void GetData() {
        if (productId==0) {
            ToastUtil.showShort(activity,"数据加载超时");
            return;
        }
        OkHttpUtils.get(AppConstants.GetProductCommentList)
                .tag(this)
                .params("ProductID",productId)//固定地方便调试，其他ID没数据"26013"
                .params("ShopID",ShopID)
                .params("Type",Type)
                .params("PageIndex",PageIndex)
                .params("PageSize",PageSize)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String comment=null;
                        String Message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
//                            commentObject = new JSONObject(jsonData);
//                            comment =  commentObject.getString("Data");
//                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //评论Data不是array
//                        ProductDetailBeans= gson.fromJson(jsonData, ProductDetailBean.class);
                        data= gson.fromJson(jsonData, new TypeToken<List<ProCommentBean>>() {}.getType());
//                        commentBeans= gson.fromJson(comment, CommentBean.class);
//                        data.add(commentBeans);
                        if(data==null) {
                            ll_empty_comment.setVisibility(View.GONE);
                        }else{
                        if(data.size()==0){
                            ll_empty_comment.setVisibility(View.GONE);
                        }else{
                            ll_empty_comment.setVisibility(View.VISIBLE);
                        }
                            for (ProCommentBean bean:data){
                                if (bean.getCommentData()!=null){
                                    switch (bean.getCommentData().getCommentLevel()){
                                        case 0://comment_all,comment_additional,comment_praise,comment_general,comment_bad,comment_hasimg;
//                                            comment_additional++;
                                            break;
                                        case 1:
                                            comment_praise++;
                                            break;
                                        case 2:
                                            comment_general++;
                                            break;
                                        case 3:
                                            comment_bad++;
                                            break;
                                        case 4:
                                            break;
                                    }
                                    if (bean.getCommentData().getIsAdditional()!=0){
                                        comment_additional++;
                                    }
                                    if (bean.getCommentImg()!=null){
                                        if (bean.getCommentImg().size()!=0){
                                            comment_hasimg++;
                                        }
                                    }
                                }
                            }
                        startCommentData(data.size(),comment_additional,comment_praise,comment_general,comment_bad,comment_hasimg);
                        setData(data);
                        }
                        /*if (!jsonData.equals("[]")){
                            pageIndex++;
                        }
                        ToastUtil.showShort(context,Message);

//                        if(searchProdBeans==null||searchProdBeans.size()==0){
//                            searchProdBeans= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
//                        }else{
//                            List<SearchProdBean>  more= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
//                            searchProdBeans.addAll(more);
//                        }
                        if (pageIndex==2){
                            setData();
                        }else{
                            searchprodAdapter.notifyDataSetChanged();
                        }*/
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(activity,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(activity,"数据加载超时");
                    }
                });
    }


    private void setData(ArrayList<ProCommentBean> beans) {
        //评论
        assessmentAdapter= new AssessmentAdapter(activity,beans);
        assessmentAdapter.setOnItemClickListener(new AssessmentAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
//                Toast.makeText(activity, "点击了"+position, Toast.LENGTH_SHORT).show();
            }
        });

        rv_product_RecyclerView_list.setLayoutManager(new LinearLayoutManager(activity));
        rv_product_RecyclerView_list.setAdapter(assessmentAdapter);
        rv_product_RecyclerView_list.setNestedScrollingEnabled(false);
    }

    private void initView(View rootView) {
        rg_prodetail_comment = (RadioGroup) rootView.findViewById(R.id.rg_prodetail_comment);
        rb_prodetail_comment_all = (RadioButton) rootView.findViewById(R.id.rb_prodetail_comment_all);
        rb_prodetail_comment_additional = (RadioButton) rootView.findViewById(R.id.rb_prodetail_comment_additional);
        rb_prodetail_comment_praise = (RadioButton) rootView.findViewById(R.id.rb_prodetail_comment_praise);
        rb_prodetail_comment_general = (RadioButton) rootView.findViewById(R.id.rb_prodetail_comment_general);
        rb_prodetail_comment_bad = (RadioButton) rootView.findViewById(R.id.rb_prodetail_comment_bad);
        rb_prodetail_comment_hasimg = (RadioButton) rootView.findViewById(R.id.rb_prodetail_comment_hasimg);
        startCommentData(comment_all,comment_additional,comment_praise,comment_general,comment_bad,comment_hasimg);
        tv_comment_count = (TextView) rootView.findViewById(R.id.tv_comment_count);
        tv_good_comment = (TextView) rootView.findViewById(R.id.tv_good_comment);
        ll_empty_comment = (LinearLayout) rootView.findViewById(R.id.ll_empty_comment);
        rv_product_RecyclerView_list = (RecyclerView) rootView.findViewById(R.id.rv_product_RecyclerView_list);
        rg_prodetail_comment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_prodetail_comment_all:{
                        Type=0;
                        rg_prodetail_comment.check(R.id.rb_prodetail_comment_all);
                        break;
                    }
                    case R.id.rb_prodetail_comment_additional:{
                        Type=1;
                        rg_prodetail_comment.check(R.id.rb_prodetail_comment_additional);
                        break;
                    }
                    case R.id.rb_prodetail_comment_praise:{
                        Type=2;
                        rg_prodetail_comment.check(R.id.rb_prodetail_comment_praise);
                        break;
                    }
                    case R.id.rb_prodetail_comment_general:{
                        Type=3;
                        rg_prodetail_comment.check(R.id.rb_prodetail_comment_general);
                        break;
                    }
                    case R.id.rb_prodetail_comment_bad:{
                        Type=4;
                        rg_prodetail_comment.check(R.id.rb_prodetail_comment_bad);
                        break;
                    }
                    case R.id.rb_prodetail_comment_hasimg:{
                        Type=5;
                        rg_prodetail_comment.check(R.id.rb_prodetail_comment_hasimg);
                        break;
                    }
                }
                int PageIndex=1;
                ArrayList<ProCommentBean> dataCace = new ArrayList<>();
                if (data!=null) {//此处是对三个时段的筛选问题，所有单独处理getIsAdditional为追评，getCommentLevel评论级别，getHasImg为是否有图
                    if(Type==0){
                        dataCace.addAll(data);
                    }else if (Type==1){
                        for (ProCommentBean bean : data) {
                                if (bean.getCommentData() != null) {
                                    if (bean.getCommentData().getIsAdditional() == 1) {
                                        dataCace.add(bean);
                                    }
                                }
                        }
                    }else if (Type==2){
                        for (ProCommentBean bean : data) {
                            if (bean.getCommentData() != null) {
                                if (bean.getCommentData().getCommentLevel() == 1) {
                                    dataCace.add(bean);
                                }
                            }
                        }
                    }else if (Type==3){
                        for (ProCommentBean bean : data) {
                            if (bean.getCommentData() != null) {
                                if (bean.getCommentData().getCommentLevel() == 2) {
                                    dataCace.add(bean);
                                }
                            }
                        }
                    }else if (Type==4){
                        for (ProCommentBean bean : data) {
                            if (bean.getCommentData() != null) {
                                if (bean.getCommentData().getCommentLevel() == 3) {
                                    dataCace.add(bean);
                                }
                            }
                        }
                    }else if (Type==5){
                        for (ProCommentBean bean : data) {
                            if (bean.getCommentData() != null) {
                                if (bean.getCommentData().getHasImg()!=0) {
                                    dataCace.add(bean);
                                }
                            }
                        }
                    }
                    assessmentAdapter=null;
                    setData(dataCace);
                }
            }
        });
    }

    private void startCommentData(int comment_all,int comment_additional,int comment_praise,int comment_general,int comment_bad,int comment_hasimg) {

        rg_prodetail_comment.check(R.id.rb_prodetail_comment_all);
        if (rb_prodetail_comment_all!=null)
        rb_prodetail_comment_all.setText("全部"+"\n"+comment_all);
        if (rb_prodetail_comment_additional!=null)
        rb_prodetail_comment_additional.setText("追加"+"\n"+comment_additional);
        if (rb_prodetail_comment_praise!=null)
        rb_prodetail_comment_praise.setText("好评"+"\n"+comment_praise);
        if (rb_prodetail_comment_general!=null)
            rb_prodetail_comment_general.setText("中评"+"\n"+comment_general);
        if (rb_prodetail_comment_bad!=null)
            rb_prodetail_comment_bad.setText("差评"+"\n"+comment_bad);
        if (rb_prodetail_comment_hasimg!=null)
            rb_prodetail_comment_hasimg.setText("带图"+"\n"+comment_hasimg);
    }


}
