package com.musicdo.musicshop.fragments;

import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.CategroyRecyclerAdapter;
import com.musicdo.musicshop.adapter.MyItemClickAdapter;
import com.musicdo.musicshop.adapter.StickyGridAdapter;
import com.musicdo.musicshop.bean.CategoryLevelOneBean;
import com.musicdo.musicshop.bean.GridItem;
import com.musicdo.musicshop.bean.HomeBrandBean;
import com.musicdo.musicshop.bean.HotBrandBean;
import com.musicdo.musicshop.bean.ProductCategoryBean;
import com.musicdo.musicshop.bean.RecommendBrandBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.R.attr.onClick;
import static android.R.attr.switchMinWidth;


/**
 * 商品分类
 * Created by adilsoomro on 8/19/16.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = HomeFragment.class.getSimpleName();

    private TextView textView,tv_right_one_bt,tv_title;
    private View view;
    private RecyclerView function_recyclerview;
    private MyItemClickAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout ll_back;
    private RelativeLayout rl_top;
    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<CategoryLevelOneBean> categoryLevelOneBeans=new ArrayList();//左边列表
    private ArrayList<HotBrandBean> HotBrandBeans=new ArrayList();//品牌馆列表
    private ArrayList<RecommendBrandBean> RecommendBrandBeans=new ArrayList();//品牌推荐列表
    private ArrayList<HomeBrandBean> HomeBrandBeans=new ArrayList();//品牌推荐列表
    private ArrayList<ProductCategoryBean> ProductCategoryBeans=new ArrayList();//二级分类列表
    CategroyRecyclerAdapter function_recycleAdapter;
    private int listPosition=0;
    StickyGridHeadersGridView category_right_gdvv;
    private List<GridItem> mGirdList=new ArrayList();
    private List<GridItem> Listitem=new ArrayList();
    LoadingDialog dialog;
    RelativeLayout rl_empty;
    @Override
    public View initView() {
        view= LayoutInflater.from(mContext).inflate(R.layout.main_category_layout,null);
        dialog = new LoadingDialog(mContext,R.style.LoadingDialog);
        function_recyclerview = (RecyclerView) view.findViewById(R.id.category_left_lstv);
        rl_top = (RelativeLayout) view.findViewById(R.id.rl_top);
        rl_top.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        ll_back = (LinearLayout) view.findViewById(R.id.ll_back);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_right_one_bt = (TextView) view.findViewById(R.id.tv_right_one_bt);
//        tv_right_one_bt.setBackgroundResource(R.mipmap.home_message_button);
        tv_title.setText("分类");
        ll_back.setVisibility(View.GONE);
        //创建默认的线性LayoutManager
        layoutManager = new LinearLayoutManager(mContext);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        function_recyclerview.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        function_recyclerview.setHasFixedSize(true);
        category_right_gdvv = (StickyGridHeadersGridView) view.findViewById(R.id.category_right_gdvv);
        category_right_gdvv.setAreHeadersSticky(false);

        rl_empty=(RelativeLayout)view.findViewById(R.id.rl_empty);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(AppConstants.ScreenWidth==0||AppConstants.ScreenWidth==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(mContext);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(mContext);
        }
    }

    /**
     * 热门品牌
     */
    private void getCategoryHotBrand() {
        OkHttpUtils.get(AppConstants.GetBrandClassify)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
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
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(mContext,message);
                        mGirdList.clear();
                        HotBrandBeans.clear();
                        HotBrandBeans= gson.fromJson(HotBrands, new TypeToken<ArrayList<HotBrandBean>>() {}.getType());
                        RecommendBrandBeans.clear();
                        RecommendBrandBeans= gson.fromJson(RecommendBrands, new TypeToken<ArrayList<RecommendBrandBean>>() {}.getType());
                        HomeBrandBeans.clear();
                        HomeBrandBeans= gson.fromJson(HomeBrands, new TypeToken<ArrayList<HomeBrandBean>>() {}.getType());
                        for (HotBrandBean HotBrandBeanitem:HotBrandBeans){
                            GridItem item1;
                            item1 =new GridItem(HotBrandBeanitem.getID(),HotBrandBeanitem.getLogo(),HotBrandBeanitem.getName(),0);
                            mGirdList.add(item1);
                        }
                        for (RecommendBrandBean RecommendBrandBeanitem:RecommendBrandBeans){
                            GridItem item1;
                            item1 =new GridItem(RecommendBrandBeanitem.getID(),RecommendBrandBeanitem.getLogo(),RecommendBrandBeanitem.getName(),1);
                            mGirdList.add(item1);
                        }
                        for (HomeBrandBean HomeBrandBeanitem:HomeBrandBeans){
                            GridItem item1;
                            item1 =new GridItem(HomeBrandBeanitem.getID(),HomeBrandBeanitem.getLogo(),HomeBrandBeanitem.getName(),2);
                            mGirdList.add(item1);
                        }
                        category_right_gdvv.setAdapter(new StickyGridAdapter(mContext, mGirdList,null));
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                    }
                });
    }

    @Override
    public void initData() {
        super.initData();
        getCategoryLeftDada();//获取左边列表
    }

    private void getCategoryLeftDada() {
        OkHttpUtils.get(AppConstants.GetProductCategory_Level1)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!jsonData.equals("[]")){
                            getCategoryHotBrand();//获取左边列表
                        }
//                        ToastUtil.showShort(mContext,message);
                        CategoryLevelOneBean BrandMuseum = new CategoryLevelOneBean();
                        BrandMuseum.setName("品牌馆");
                        categoryLevelOneBeans.add(BrandMuseum);
                        ArrayList<CategoryLevelOneBean> categoryLevelOne=new ArrayList<CategoryLevelOneBean>();
                        categoryLevelOne= gson.fromJson(jsonData, new TypeToken<ArrayList<CategoryLevelOneBean>>() {}.getType());
                        categoryLevelOneBeans.addAll(categoryLevelOne);

                        mAdapter = new MyItemClickAdapter(mContext,categoryLevelOneBeans);
                        function_recyclerview.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new MyItemClickAdapter.OnItemClickLitener(){
                            @Override
                            public void onItemClick(View view , int position){
//                                Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                mAdapter.setPosition(position);
//                                mAdapter.notifyDataSetChanged();
                                if (position==0){
                                    getCategoryHotBrand();
                                }else {
                                    GetProductCategory(categoryLevelOneBeans.get(position).getID(),categoryLevelOneBeans.get(position).getName());
                                }
                                mAdapter.setPosition(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        rl_empty.setVisibility(View.VISIBLE);
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                    }
                });
    }


    private void GetProductCategory(int index, final String categroyname) {
        OkHttpUtils.get(AppConstants.GetProductCategory)
                .tag(this)
                .params("CategoryID",index)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 加载成功");
                        JSONObject jsonObject;
                        JSONObject bannerObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("Data");//
                            message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(mContext,message);
                        ProductCategoryBeans.clear();
                        ProductCategoryBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<ProductCategoryBean>>() {}.getType());
                        mGirdList.clear();
                        for (ProductCategoryBean ProductCategoryBeanitem:ProductCategoryBeans){
                            GridItem item1;
                            item1 =new GridItem(ProductCategoryBeanitem.getID(),ProductCategoryBeanitem.getSrcDetail(),ProductCategoryBeanitem.getName(),-1);
                            mGirdList.add(item1);
                        }
                        category_right_gdvv.setAdapter(new StickyGridAdapter(mContext, mGirdList,categroyname));
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Log.i(TAG, "onBefore: 开始加载");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    @Override
    public void onDestroy() {//activity结束dialog消失
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
