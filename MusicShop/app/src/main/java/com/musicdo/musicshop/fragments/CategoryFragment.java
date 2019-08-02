package com.musicdo.musicshop.fragments;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.adapter.CategroyRecyclerAdapter;
import com.musicdo.musicshop.adapter.StickyGridAdapter;
import com.musicdo.musicshop.bean.CategoryLevelOneBean;
import com.musicdo.musicshop.bean.GridItem;
import com.musicdo.musicshop.bean.MusicalBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

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

public class CategoryFragment extends BaseFragment {
    public final String TAG="CategoryFragment";
    public View view;
    RecyclerView function_recyclerview;
    CategroyRecyclerAdapter function_recycleAdapter;
    StickyGridHeadersGridView category_right_gdvv;
    private List<GridItem> nonHeaderIdList = new ArrayList<GridItem>();
    List<String> griddata = new ArrayList<>();
    private List<GridItem> mGirdList=new ArrayList();
    private ArrayList<CategoryLevelOneBean> categoryLevelOneBeans=new ArrayList();
    String[] toolsList = {"品牌馆", "潮流女装", "品牌男装", "内衣配饰", "家用电器", "手机数码", "电脑办公", "个护化妆", "母婴频道", "食物生鲜", "酒水饮料", "家居家纺", "整车车品", "鞋靴箱包", "运动户外", "图书", "玩具乐器", "钟表", "居家生活", "珠宝饰品", "音像制品", "家具建材", "计生情趣", "营养保健", "奢侈礼品", "生活服务", "旅游出行"};

    @Override
    public View initView() {
        if (view==null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.main_category_layout, null);
            category_right_gdvv = (StickyGridHeadersGridView) view.findViewById(R.id.category_right_gdvv);
            category_right_gdvv.setAreHeadersSticky(false);
            function_recyclerview = (RecyclerView) view.findViewById(R.id.category_left_lstv);
            GridLayoutManager grid_education_layoutManage = new GridLayoutManager(mContext,1);
            function_recyclerview.setLayoutManager(grid_education_layoutManage);
            categoryLevelOneBeans.clear();
            for (int i = 0; i < 5; i++) {
                CategoryLevelOneBean BrandMuseum = new CategoryLevelOneBean();
                BrandMuseum.setName("品牌馆");
                categoryLevelOneBeans.add(BrandMuseum);
            }
            function_recycleAdapter = new CategroyRecyclerAdapter(mContext, categoryLevelOneBeans);
            function_recyclerview.setAdapter(function_recycleAdapter);
            function_recycleAdapter.setOnItemClickListener(new CategroyRecyclerAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
//                Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                function_recycleAdapter.setPosition(position);
                function_recycleAdapter.notifyDataSetChanged();
            }
        });
//            getGridData();
//            GetLevelOne();
            category_right_gdvv.setAdapter(new StickyGridAdapter(mContext, mGirdList,null));
        }
        return view;
    }

    /**
     * 分类第一级
     */
    private void GetLevelOne() {
            OkHttpUtils.get(AppConstants.GetProductCategory_Level1)
                    .tag(this)
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
                            ToastUtil.showShort(mContext,message);
                            ArrayList<CategoryLevelOneBean> categoryLevelOne=new ArrayList<CategoryLevelOneBean>();
                            categoryLevelOne= gson.fromJson(jsonData, new TypeToken<ArrayList<CategoryLevelOneBean>>() {}.getType());
                            categoryLevelOneBeans.addAll(categoryLevelOne);
                            //显示乐器馆
                            if (function_recycleAdapter!=null){
                                function_recycleAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            Log.i(TAG, "onBefore: 开始加载");
                        }
                    });
    }

    /*private void  getGridData() {
        for (int i = 0; i <20; i++) {
            GridItem item1;
           switch(i%3){
               case 0:
                   item1 =new GridItem(Long.valueOf(i),String.valueOf(i),0);
                   mGirdList.add(item1);
                   break;
               case 1:
                   item1 =new GridItem(Long.valueOf(i),String.valueOf(i),1);
                   mGirdList.add(item1);
                   break;
               case 2:
                   item1 =new GridItem(Long.valueOf(i),String.valueOf(i),2);
                   mGirdList.add(item1);
                   break;
           }
        }
    }*/

}
