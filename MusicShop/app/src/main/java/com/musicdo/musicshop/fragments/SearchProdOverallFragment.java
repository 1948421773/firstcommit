package com.musicdo.musicshop.fragments;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.adapter.SearchProdAdapter;
import com.musicdo.musicshop.bean.Meizi;
import com.musicdo.musicshop.bean.SearchProdBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:
 * 作者：haiming on 2017/7/28 11:20
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SearchProdOverallFragment extends BaseFragment {
    private static final String TAG = "SearchProdOverallFragment";
    private View view;
    private RecyclerView rc_searchprod_overall;
    SearchProdAdapter searchprodAdapter;
    ArrayList<SearchProdBean> searchProdBeans=new ArrayList<>();
    private int lastVisibleItem;
    GridLayoutManager grid_tend_layoutManage;
    private int pageIndex=1;
    boolean type=false;
    @Override
    public View initView() {
        if (view==null) {
            type=getArguments().getBoolean("type");
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_searchprod_overall, null);
            rc_searchprod_overall = (RecyclerView) view.findViewById(R.id.rc_searchprod_overall);
            if (type){
                grid_tend_layoutManage= new GridLayoutManager(mContext, 2);
            }else{
                grid_tend_layoutManage= new GridLayoutManager(mContext, 1);
            }
            rc_searchprod_overall.setLayoutManager(grid_tend_layoutManage);
            rc_searchprod_overall.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchprodAdapter.getItemCount()) {
                        doSth();
                    }
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    lastVisibleItem = grid_tend_layoutManage.findLastVisibleItemPosition();
                }
            });
        }
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        doSth();
    }

    private void doSth() {
        OkHttpUtils.get(AppConstants.SearchProduct)
                .tag(this)
                .params("pageIndex",pageIndex)
                .params("keyword","")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 搜索成功");

                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!jsonData.equals("[]")){
                            pageIndex++;
                        }
                        ToastUtil.showShort(mContext,Message);

                        if(searchProdBeans==null||searchProdBeans.size()==0){
                            searchProdBeans= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
                        }else{
                            List<SearchProdBean>  more= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
                            searchProdBeans.addAll(more);
                        }
                        if (pageIndex==2){
                            setData();
                        }else{
                            searchprodAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Log.i(TAG, "onBefore: 开始搜索");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.i(TAG, "onError: 搜索失败");
                    }
                });
    }


    private void setData() {
        if(searchprodAdapter==null){
//            searchprodAdapter = new SearchProdAdapter(mContext,searchProdBeans);
            rc_searchprod_overall.setAdapter(searchprodAdapter);//recyclerview设置适配器
            searchprodAdapter.setOnItemClickListener(new SearchProdAdapter.OnSearchItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                    startActivity(intent);
                }
            });
            rc_searchprod_overall.setNestedScrollingEnabled(false);
        }else{
            //让适配器刷新数据
            searchprodAdapter.notifyDataSetChanged();//
        }
    }
}
