package com.musicdo.musicshop.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.TrueSpecAdapter;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.bean.TruespecBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:商品详情-商品参数显示
 * 作者：haiming on 2017/8/5 10:25
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class GoodsInfoTrueSpecActivity extends BaseActivity implements View.OnClickListener{
    private RecyclerView rc_truespec;
    private Context context;
    private int ProductID;
    private ArrayList<TruespecBean> truespecBeans=new ArrayList<>();
    private TrueSpecAdapter truespecAdapter;
    private String paramString;
    private TextView tv_complete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodsinfotruespec);
        MyApplication.getInstance().addActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        context=this;
        ProductID=getIntent().getIntExtra("ProductID",0);
        paramString=getIntent().getStringExtra("Param");
        initView();
        GetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
    }
    private void initView() {
        rc_truespec = (RecyclerView) findViewById(R.id.rc_truespec);
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        tv_complete.setOnClickListener(this);
    }

    private void GetData() {
        if (ProductID==0) {
            ToastUtil.showShort(context,"请选择商品规格");
            return;
        }
        OkHttpUtils.get(AppConstants.GetProductTrueSpec)
                .tag(this)
                .params("ProductID",ProductID)//ProductID固定地方便调试，其他ID没数据
                .params("Param",paramString)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(context,s);
                        //评论Data不是array
//                        truespecBeans= gson.fromJson(jsonData, new TypeToken<TruespecBean>(){}.getType());
                        SetDate();
                        /*ProductDetailBeans= gson.fromJson(jsonData, new TypeToken<List<ProductDetailBean>>() {}.getType());
                        commentBeans= gson.fromJson(comment, CommentBean.class);
                        data.add(commentBeans);
                        getShopData();
                        if (!jsonData.equals("[]")){
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
//                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");
                    }
                });
    }

    private void SetDate() {
        for (int i = 0; i <8; i++) {
            TruespecBean tsp=new TruespecBean();
            tsp.setTitle("颜色尺码");
            tsp.setName("原木色，黑色，白色，原谅色，钢琴黑");
            truespecBeans.add(tsp);
        }
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rc_truespec.setLayoutManager(layoutManage);
        if(truespecAdapter==null&&truespecBeans!=null){
            truespecAdapter = new TrueSpecAdapter(context,truespecBeans);
            rc_truespec.setAdapter(truespecAdapter);//recyclerview设置适配器
            rc_truespec.setNestedScrollingEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_complete:
                finish();
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
