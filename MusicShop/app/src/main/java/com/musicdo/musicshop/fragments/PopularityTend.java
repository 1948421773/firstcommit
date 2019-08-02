package com.musicdo.musicshop.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.adapter.ItemEducationAdapter;
import com.musicdo.musicshop.adapter.MyRecyclerAdapter;
import com.musicdo.musicshop.adapter.PopularityTendAdapter;
import com.musicdo.musicshop.bean.Meizi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by A on 2017/7/15.
 */

public class PopularityTend extends BaseFragment {
    View rootView;
    private final static String TAG = HomeFragment.class.getSimpleName();
    PopularityTendAdapter tendAdapter;
    int page=3;
    List<Meizi> meizis=new ArrayList<>();
    private TextView textView;
    RecyclerView rc_popu_tend;
    @Override
    public View initView() {
        if (rootView==null) {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.popularity_tend, null);
            rc_popu_tend = (RecyclerView) rootView.findViewById(R.id.rc_popu_tend);
            GridLayoutManager grid_tend_layoutManage = new GridLayoutManager(mContext, 2);
            rc_popu_tend.setLayoutManager(grid_tend_layoutManage);
        /*for (int i = 0; i <20 ; i++) {
            Meizi meizi=new Meizi();
            meizi.setUrl("https://ws1.sinaimg.cn/large/610dc034ly1fhj53yz5aoj21hc0xcn41.jpg");
            meizi.setPage(1);
            meizis.add(meizi);
        }
        tendAdapter = new PopularityTendAdapter(mContext,meizis);
        rc_popu_tend.setAdapter(tendAdapter);//recyclerview设置适配器
        tendAdapter.setOnItemClickListener(new PopularityTendAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,ProductDetailActivity.class);
                startActivity(intent);
            }
        });
        rc_popu_tend.setNestedScrollingEnabled(false);*/
            doSth();
        }
        return rootView;
    }

    /**
     * demo
     */
    private void doSth() {
        OkHttpUtils.get("http://gank.io/api/data/福利/10/"+page)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        Log.i(TAG, "onSuccess: 获取成功");

                        page++;
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("results");
                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(meizis==null||meizis.size()==0){
                            meizis= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                        }else{
                            List<Meizi>  more= gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {}.getType());
                            meizis.addAll(more);
                        }
                        setData();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Log.i(TAG, "onBefore: 开始获取");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.i(TAG, "onError: 获取失败");
                    }
                });
    }

    private void setData() {
        if(tendAdapter==null){
            tendAdapter = new PopularityTendAdapter(mContext,meizis);
            rc_popu_tend.setAdapter(tendAdapter);//recyclerview设置适配器
            tendAdapter.setOnItemClickListener(new PopularityTendAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,ProductDetailActivity.class);
                    startActivity(intent);
                }
            });
            rc_popu_tend.setNestedScrollingEnabled(false);
        }else{
            //让适配器刷新数据
            tendAdapter.notifyDataSetChanged();//
        }

    }

    @Override
    public void initData() {
        super.initData();

    }
}
