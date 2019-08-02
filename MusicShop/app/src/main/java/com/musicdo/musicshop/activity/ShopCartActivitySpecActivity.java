package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.SpecColorTagGridAdapter;
import com.musicdo.musicshop.adapter.SpecMateriaGridAdapter;
import com.musicdo.musicshop.adapter.SpecSizeGridAdapter;
import com.musicdo.musicshop.bean.SpecBean;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.bean.SpecitemDataBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/11/3.
 */

public class ShopCartActivitySpecActivity extends BaseActivity implements View.OnClickListener,SpecColorTagGridAdapter.CheckInterface,SpecSizeGridAdapter.CheckInterface,SpecMateriaGridAdapter.CheckInterface {
    private int ProductID;
    private Context context;
    private SpecBean specBeans=new SpecBean();
    private ArrayList<SpecItemBean> specItemBeans=new ArrayList<>();
    private ArrayList<SpecItemBean> specCaceBeans=new ArrayList<>();
    private ArrayList<SpecitemDataBean> specitemCaceBeans=new ArrayList<>();
    private ArrayList<SpecitemDataBean> specitemDataBeans_color=new ArrayList<>();
    private ArrayList<SpecitemDataBean> specitemDataBeans_size=new ArrayList<>();
    private ArrayList<SpecitemDataBean> specitemDataBeans_material =new ArrayList<>();

    List<String> colorTags;
    private RecyclerView rc_spce_color,rc_spce_size,rc_spce_material;
    private SpecColorTagGridAdapter colorAdapter;
    private SpecSizeGridAdapter sizeAdapter;
    private SpecMateriaGridAdapter materialAdapter;
    private TextView choose,iv_increase,iv_decrease,tv_count,color_label,size_label,material_label,tv_addcart,tv_buynow,tv_cart_change_complete;
    private ImageView close;
    StringBuffer   testStrBuff;
    int currentCount =1;
    RoundedImageView imageView1;
    private int SpecOK=1;//选择规格
    private int SpecAddCart=2;//选择规格，加入购物车
    private int SpecSubmitOrder=3;//选择规格，立即购买

    private double price=0.0;
    private TextView tv_price,tv_stock;
    private String shoppingCart;
    private String Propertys;
    List<Integer> Propertyslist=new ArrayList<>();
    List<Map<String,Integer>> PropertysCace=new ArrayList<>();
    private String Guid="";
    private String Propertyscace="";
    private String PropertysTextcace="";
    private LinearLayout ll_cart_spec,ll_detaid_spec;
    private int Count=0;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_goosinfospec);
        MyApplication.getInstance().addActivity(this);
//        getWindow().getAttributes().windowAnimations = R.style.ActionSheetDialogAnimation;
//        overridePendingTransition(R.anim.goodsinfospec_dialog_in, R.anim.goodsinfospec_dialog_out);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        context=this;

        ProductID=getIntent().getIntExtra("ProductID",0);
        price=getIntent().getDoubleExtra("price",0.0);
        specBeans=getIntent().getParcelableExtra("spec");
        specCaceBeans=getIntent().getParcelableArrayListExtra("specItemBeans");

        if (getIntent().getStringExtra("Propertys")!=null){
            Propertys=getIntent().getStringExtra("Propertys");
            if(Propertys.length()!=0){
                for(String o : Propertys.split(",")) {
                    Propertyslist.add(Integer.valueOf(o));
                }
            }
        }
        if (getIntent().getStringExtra("shoppingCart")!=null){//获取购物车传递数据,可以像判断是否传递shoppingCart，如果传递则说明购物车传数据，然后才获取GUID等数据
            shoppingCart=getIntent().getStringExtra("shoppingCart");
        }
        if (getIntent().getStringExtra("ProductID")!=null){//获取购物车传递数据
            ProductID=Integer.valueOf(getIntent().getStringExtra("ProductID"));
        }
        if (getIntent().getStringExtra("GUID")!=null){//获取购物车传递数据
            Guid=getIntent().getStringExtra("GUID");
        }
        if (getIntent().getIntExtra("Count",0)!=0){//获取购物车传递数据
            Count=getIntent().getIntExtra("Count",0);
            currentCount=Count;

        }
        if (getIntent().getStringExtra("PropertysText")!=null){//获取购物车传递数据
            PropertysTextcace=getIntent().getStringExtra("PropertysText");
        }if (getIntent().getStringExtra("PropertysText")!=null){//获取购物车传递数据
            PropertysTextcace=getIntent().getStringExtra("PropertysText");
        }
        if (getIntent().getStringExtra("Propertys")!=null){//获取购物车传递数据
            Propertyscace=getIntent().getStringExtra("Propertys");
        }

        initView();

//        GetData();
        if (specBeans!=null||specCaceBeans!=null){

            SetDate();
        }else{
            color_label.setVisibility(View.GONE);
            size_label.setVisibility(View.GONE);
            material_label.setVisibility(View.GONE);
            rc_spce_color.setVisibility(View.GONE);
            rc_spce_size.setVisibility(View.GONE);
            rc_spce_material.setVisibility(View.GONE);
            choose.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
        if (AppConstants.USERID==0){//当内存不足，activity被销毁了，信息没有保存恢复不了，只能重新回到首页
            loginData= SpUtils.getString(context, "LoginKey", "LoginFile");
            if (!loginData.equals("")){//获取本地信息
                AppConstants.ISLOGIN=true;
                try {
                    AppConstants.USERNAME=new JSONObject(loginData).getString("Name");
                    AppConstants.USERID=new JSONObject(loginData).getInt("ID");
                    AppConstants.PHONE=new JSONObject(loginData).getString("Phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initView() {
        tv_stock = (TextView) findViewById(R.id.tv_stock);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_price.setText(String.valueOf(getResources().getString(R.string.pricesymbol)+ SpUtils.doubleToString(price)));
        rc_spce_color = (RecyclerView) findViewById(R.id.rc_spce_color);
        rc_spce_size = (RecyclerView) findViewById(R.id.rc_spce_size);
        rc_spce_size = (RecyclerView) findViewById(R.id.rc_spce_size);
        rc_spce_material = (RecyclerView) findViewById(R.id.rc_spce_material);
        //小图标
        imageView1=(RoundedImageView)findViewById(R.id.imageView1);
        Picasso.with(context)
                .load(R.mipmap.img_start_loading)
                .resize(ScreenUtil.getScreenWidth(context)/4,ScreenUtil.getScreenWidth(context)/4)
                .config(Bitmap.Config.RGB_565)
                .into(imageView1);//加载网络图片
        choose=(TextView)findViewById(R.id.choose);
        close=(ImageView)findViewById(R.id.close);
        //数量
        iv_increase = (TextView) findViewById(R.id.tv_add);
        iv_decrease = (TextView) findViewById(R.id.tv_reduce);
        tv_count = (TextView) findViewById(R.id.tv_num);
        if (Count!=0){
            tv_count.setText(String.valueOf(Count));
        }
        color_label = (TextView) findViewById(R.id.color_label);
        size_label = (TextView) findViewById(R.id.size_label);
        material_label = (TextView) findViewById(R.id.material_label);
        tv_addcart = (TextView) findViewById(R.id.tv_addcart);
        ll_cart_spec = (LinearLayout) findViewById(R.id.ll_cart_spec);
        ll_cart_spec.setOnClickListener(this);
        ll_detaid_spec = (LinearLayout) findViewById(R.id.ll_detaid_spec);
        tv_addcart = (TextView) findViewById(R.id.tv_addcart);
        tv_addcart.setOnClickListener(this);
        tv_buynow = (TextView) findViewById(R.id.tv_buynow);
        tv_buynow.setOnClickListener(this);
        tv_cart_change_complete = (TextView) findViewById(R.id.tv_cart_change_complete);
        tv_cart_change_complete.setOnClickListener(this);
        iv_increase.setOnClickListener(this);
        iv_decrease.setOnClickListener(this);
        close.setOnClickListener(this);
        if (shoppingCart!=null) {
            if (shoppingCart.length() != 0) {
                ll_cart_spec.setVisibility(View.VISIBLE);
                ll_detaid_spec.setVisibility(View.GONE);
            } else {
                ll_cart_spec.setVisibility(View.GONE);
                ll_detaid_spec.setVisibility(View.VISIBLE);
            }
        }else {
            ll_cart_spec.setVisibility(View.GONE);
            ll_detaid_spec.setVisibility(View.VISIBLE);
        }
    }

    private void GetData() {
        if (ProductID==0) {
            ToastUtil.showShort(context,"数据加载超时");
            return;
        }
        OkHttpUtils.get(AppConstants.GetSpec)
                .tag(this)
                .params("ProductID",ProductID)//ProductID固定地方便调试，其他ID没数据
//                .params("ID",productId)
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
                        ToastUtil.showShort(context,Message);
                        //评论Data不是array
                        specBeans= gson.fromJson(s, SpecBean.class);
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
                        color_label.setVisibility(View.GONE);
                        size_label.setVisibility(View.GONE);
                        material_label.setVisibility(View.GONE);
                        rc_spce_color.setVisibility(View.GONE);
                        rc_spce_size.setVisibility(View.GONE);
                        rc_spce_material.setVisibility(View.GONE);
                    }
                });
    }


    private void SetDate(){
        if (specBeans!=null){
            if (specBeans.getData()!=null){
                if (specBeans.getData().size()!=0){
                    specItemBeans.addAll(specBeans.getData());//获取json中的ReturnData并保存列表
                }else{
                    color_label.setVisibility(View.GONE);
                    size_label.setVisibility(View.GONE);
                    material_label.setVisibility(View.GONE);
                    choose.setVisibility(View.GONE);
                    return;
                }

            }else{
                color_label.setVisibility(View.GONE);
                size_label.setVisibility(View.GONE);
                material_label.setVisibility(View.GONE);
                choose.setVisibility(View.GONE);
                return;
            }

            if (specItemBeans.size()==3){
                rc_spce_color.setVisibility(View.VISIBLE);
                color_label.setVisibility(View.VISIBLE);
                rc_spce_size.setVisibility(View.VISIBLE);
                size_label.setVisibility(View.VISIBLE);
                rc_spce_material.setVisibility(View.VISIBLE);
                material_label.setVisibility(View.VISIBLE);
            }else if (specItemBeans.size()==2){
                rc_spce_color.setVisibility(View.VISIBLE);
                color_label.setVisibility(View.VISIBLE);
                rc_spce_size.setVisibility(View.VISIBLE);
                size_label.setVisibility(View.VISIBLE);
                rc_spce_material.setVisibility(View.GONE);
                material_label.setVisibility(View.GONE);
            }else if (specItemBeans.size()==1){
                rc_spce_color.setVisibility(View.VISIBLE);
                color_label.setVisibility(View.VISIBLE);
                rc_spce_size.setVisibility(View.GONE);
                size_label.setVisibility(View.GONE);
                rc_spce_material.setVisibility(View.GONE);
                material_label.setVisibility(View.GONE);
            }
            if (Propertyslist!=null){
                if (Propertyslist.size()!=0){//显示默认选中
                    for (Integer Propertyslists:Propertyslist){
                        for (int i = 0; i <specItemBeans.size() ; i++) {
                            for (int j = 0; j <specItemBeans.get(i).getData().size() ; j++) {
                                if (Propertyslists==specItemBeans.get(i).getData().get(j).getSpecValueID()){
                                    specItemBeans.get(i).getData().get(j).setIscheck(true);
                                }
                            }
                        }

                    }
                }else{
                    for (int i = 0; i <specItemBeans.size() ; i++) {
                        for (int j = 0; j <specItemBeans.get(i).getData().size() ; j++) {
                            if (j==0){
                                specItemBeans.get(i).getData().get(j).setIscheck(true);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < specItemBeans.size(); i++) {

                switch (i){
                    case 0: {
                        color_label.setText(specItemBeans.get(i).getTitle());
                        specitemDataBeans_color.addAll(specItemBeans.get(i).getData());
                        specitemCaceBeans.add(specitemDataBeans_color.get(0));//默认选中尺寸
                        int listsNumber=1;
                        if (specitemDataBeans_color!=null){
                            if (specitemDataBeans_color.get(0).getSpecValue().length()>20){
                                listsNumber=1;
                            }else if (specitemDataBeans_color.get(0).getSpecValue().length()>12){
                                listsNumber=2;
                            }else if (specitemDataBeans_color.get(0).getSpecValue().length()>8){
                                listsNumber=3;
                            }else if (specitemDataBeans_color.get(0).getSpecValue().length()>5){
                                listsNumber=4;
                            }else{
                                listsNumber=specitemDataBeans_color.size();
                            }
                        }
                        GridLayoutManager layoutManage = new GridLayoutManager(context,listsNumber,GridLayoutManager.VERTICAL,false);

                        rc_spce_color.setLayoutManager(layoutManage);
                        if(colorAdapter==null&&specitemDataBeans_color!=null){
                            colorAdapter = new SpecColorTagGridAdapter(context,specitemDataBeans_color);
                            rc_spce_color.setAdapter(colorAdapter);//recyclerview设置适配器
                            colorAdapter.setCheckInterface(this);
                            colorAdapter.setOnItemClickListener(new SpecColorTagGridAdapter.OnSearchItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                            ToastUtil.showShort(context,specitemDataBeans_color.get(position).getSpecValue());
//                                colorAdapter.changeShowType(position);
                                    for (int j = 0; j <specitemDataBeans_color.size() ; j++) {
                                        if (position==j){
                                            specitemDataBeans_color.get(j).setIscheck(true);
                                        }else{
                                            specitemDataBeans_color.get(j).setIscheck(false);
                                        }
                                    }
                                    colorAdapter.notifyDataSetChanged();
                                }
                            });
                            rc_spce_color.setNestedScrollingEnabled(false);
                        }
                    }
                    break;
                    case 1: {
                        size_label.setText(specItemBeans.get(i).getTitle());
                        specitemDataBeans_size.addAll(specItemBeans.get(i).getData());
                        specitemCaceBeans.add(specitemDataBeans_size.get(0));//默认选中尺寸
                        int listsNumber=1;
                        if (specitemDataBeans_size!=null){
                            if (specitemDataBeans_size.get(0).getSpecValue().length()>20){
                                listsNumber=1;
                            }else if (specitemDataBeans_size.get(0).getSpecValue().length()>12){
                                listsNumber=2;
                            }else if (specitemDataBeans_size.get(0).getSpecValue().length()>8){
                                listsNumber=3;
                            }else if (specitemDataBeans_size.get(0).getSpecValue().length()>5){
                                listsNumber=4;
                            }else{
                                listsNumber=specitemDataBeans_size.size();
                            }
                        }
                        GridLayoutManager layoutManage =new GridLayoutManager(context,listsNumber,GridLayoutManager.VERTICAL,false);
                        rc_spce_size.setLayoutManager(layoutManage);
                        if(sizeAdapter==null&&specitemDataBeans_size!=null) {
                            sizeAdapter = new SpecSizeGridAdapter(context, specitemDataBeans_size);
                            rc_spce_size.setAdapter(sizeAdapter);//recyclerview设置适配器
                            sizeAdapter.setCheckInterface(this);
                            sizeAdapter.setOnItemClickListener(new SpecSizeGridAdapter.OnSearchItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                                ToastUtil.showShort(context, specitemDataBeans_size.get(position).getSpecValue());
                                    for (int j = 0; j <specitemDataBeans_size.size() ; j++) {
                                        if (position==j){
                                            specitemDataBeans_size.get(j).setIscheck(true);
                                        }else{
                                            specitemDataBeans_size.get(j).setIscheck(false);
                                        }
                                    }
                                    sizeAdapter.notifyDataSetChanged();
                                }
                            });
                            rc_spce_size.setNestedScrollingEnabled(false);
                        }
                    }
                    break;
                    case 2: {
                        material_label.setText(specItemBeans.get(i).getTitle());
                        specitemDataBeans_material.addAll(specItemBeans.get(i).getData());
                        specitemCaceBeans.add(specitemDataBeans_material.get(0));//默认选中材质
                        int listsNumber=1;
                        if (specitemDataBeans_material!=null){
                            if (specitemDataBeans_material.get(0).getSpecValue().length()>20){
                                listsNumber=1;
                            }else if (specitemDataBeans_material.get(0).getSpecValue().length()>12){
                                listsNumber=2;
                            }else if (specitemDataBeans_material.get(0).getSpecValue().length()>8){
                                listsNumber=3;
                            }else if (specitemDataBeans_material.get(0).getSpecValue().length()>5){
                                listsNumber=4;
                            }else{
                                listsNumber=specitemDataBeans_material.size();
                            }
                        }
                        GridLayoutManager layoutManage =new GridLayoutManager(context,listsNumber,GridLayoutManager.VERTICAL,false);
                        rc_spce_material.setLayoutManager(layoutManage);
                        if(materialAdapter==null&&specitemDataBeans_material!=null) {
                            materialAdapter = new SpecMateriaGridAdapter(context, specitemDataBeans_material);
                            rc_spce_material.setAdapter(materialAdapter);//recyclerview设置适配器
                            materialAdapter.setCheckInterface(this);
                            materialAdapter.setOnItemClickListener(new SpecMateriaGridAdapter.OnSearchItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                                ToastUtil.showShort(context, specitemDataBeans_material.get(position).getSpecValue());
                                    for (int j = 0; j <specitemDataBeans_material.size() ; j++) {
                                        if (position==j){
                                            specitemDataBeans_material.get(j).setIscheck(true);
                                        }else{
                                            specitemDataBeans_material.get(j).setIscheck(false);
                                        }
                                    }
                                    materialAdapter.notifyDataSetChanged();
                                }
                            });
                            rc_spce_material.setNestedScrollingEnabled(false);
                        }
                    }
                    break;
                }
//            SpecItemBean si=new SpecItemBean();
//            si.setTitle(specItemBeans.get(i).getTitle());
//            List<SpecitemDataBean> specitemDataBeans_Cace =new ArrayList<>();
//            specitemDataBeans_Cace.add(specItemBeans.get(i).getData().get(0));
//            si.setData(specitemDataBeans_Cace);
//            specCaceBeans.add(si);
            }
            //显示全部默认项（每种规格的第一项）
            showSelectSpec(specItemBeans);//追加字符串显示已选择的规格
            if (specItemBeans.get(0).getData().get(0).getSrc()!=null){
                showPreView(specItemBeans.get(0).getData().get(0).getSrc());
            }

        }
    }

    private void showSelectSpec(ArrayList<SpecItemBean> caceSpec) {
        testStrBuff=new StringBuffer("已选:");
        StringBuffer caceStrBuff=new StringBuffer("");
        StringBuffer paramStrBuff=new StringBuffer("");
        StringBuffer paramValueIdStrBuff=new StringBuffer("");
        for (int j = 0; j <specItemBeans.size() ; j++){
            for (int i = 0; i <specItemBeans.get(j).getData().size() ; i++) {
                if (specItemBeans.get(j).getData().get(i).ischeck()){
                    testStrBuff.append("“"+specItemBeans.get(j).getData().get(i).getSpecValue()+"”  ");
                    caceStrBuff.append(specItemBeans.get(j).getData().get(i).getSpecValue()+",");
                    paramStrBuff.append(""+specItemBeans.get(j).getData().get(i).getSpecValueID()+",");
                }
            }
        }
        choose.setText(testStrBuff);
        String cacepro=paramStrBuff.toString();
        String caceprotext=caceStrBuff.toString();
        Propertyscace=cacepro.substring(0,cacepro.length()-1);//去掉后面的逗号
        Propertys="";
        Propertys=Propertyscace;
        PropertysTextcace=caceprotext.substring(0,caceprotext.length()-1);//去掉后面的逗号
        choose.setVisibility(View.VISIBLE);
        //获取销量和选择的价格
        GetProductTrueSpec(Propertyscace);
    }

    private void GetProductTrueSpec(String paramString) {
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
                        JSONArray jsonDataObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        int Store=0;
                        double Price=0.00;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            jsonDataObject = new JSONArray(jsonData);
                            Message = jsonObject.getString("Message");
                            Store=new JSONObject(jsonDataObject.get(0).toString()).getInt("Store");
                            Price=new JSONObject(jsonDataObject.get(0).toString()).getDouble("Price");
                            tv_price.setText(String.valueOf(getResources().getString(R.string.pricesymbol)+SpUtils.doubleToString(Price)));
                            tv_stock.setText("库存"+String.valueOf(Store));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        ToastUtil.showShort(context,s);
                        //评论Data不是array
//                        truespecBeans= gson.fromJson(jsonData, new TypeToken<TruespecBean>(){}.getType());
//                        SetDate();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                if (currentCount>200) {
                    ToastUtil.showShort(context, "数量超出范围~");
                } else {
                    currentCount++;
                    tv_count.setText(currentCount + "");
                }
                break;
            case R.id.tv_reduce:
                if (currentCount == 1) {
                    ToastUtil.showShort(context, "数量超出范围~");
                } else {
                    currentCount--;
                    tv_count.setText(currentCount + "");
                }
                break;
            case R.id.close:
                close();
                break;
            case R.id.tv_addcart:{
                ArrayList<SpecItemBean> CaceBeans=new ArrayList<>();
                for (int j = 0; j <specItemBeans.size() ; j++){
                    SpecItemBean si=new SpecItemBean();
                    si.setTitle(specItemBeans.get(j).getTitle());
                    List<SpecitemDataBean> specitemDataBeans_Cace =new ArrayList<>();
                    for (int i = 0; i <specItemBeans.get(j).getData().size() ; i++) {
                        if (specItemBeans.get(j).getData().get(i).ischeck()){
                            specitemDataBeans_Cace.add(specItemBeans.get(j).getData().get(i));

                        }
                    }
                    si.setData(specitemDataBeans_Cace);
                    CaceBeans.add(si);
                }

                Intent intent = new Intent();
                Bundle bd = new Bundle();
                bd.putString("chooseSpec", choose.getText().toString().trim());
                bd.putString("paramString", PropertysTextcace);
                bd.putString("paramAddCart", "paramAddCart");
                bd.putString("paramNumber", tv_count.getText().toString().trim());
                intent.putExtras(bd);
//        intent.putExtra("paramlist",specCaceBeans);
                intent.putParcelableArrayListExtra("paramlist", CaceBeans);
                setResult(SpecAddCart, intent);
                finish();
            }
            break;
            case R.id.tv_buynow: {
                ArrayList<SpecItemBean> CaceBeans=new ArrayList<>();
                for (int j = 0; j <specItemBeans.size() ; j++){
                    SpecItemBean si=new SpecItemBean();
                    si.setTitle(specItemBeans.get(j).getTitle());
                    List<SpecitemDataBean> specitemDataBeans_Cace =new ArrayList<>();
                    for (int i = 0; i <specItemBeans.get(j).getData().size() ; i++) {
                        if (specItemBeans.get(j).getData().get(i).ischeck()){
                            specitemDataBeans_Cace.add(specItemBeans.get(j).getData().get(i));

                        }
                    }
                    si.setData(specitemDataBeans_Cace);
                    CaceBeans.add(si);
                }

                Intent intent = new Intent();
                Bundle bd = new Bundle();
                bd.putString("chooseSpec", choose.getText().toString().trim());
                bd.putString("paramString", PropertysTextcace);
                bd.putString("paramSubmitOrder", "paramSubmitOrder");
                bd.putString("paramNumber", tv_count.getText().toString().trim());
                intent.putExtras(bd);
//        intent.putExtra("paramlist",specCaceBeans);
                intent.putParcelableArrayListExtra("paramlist", CaceBeans);
                setResult(SpecSubmitOrder, intent);
                finish();
            }
            break;
            case R.id.tv_cart_change_complete: {
                String total=tv_count.getText().toString().trim();
                Intent intent = new Intent(context, ShoppingCartActivity.class);
                intent.putExtra("changeShoppingCart", "changeShoppingCart");
                intent.putExtra("GUID",Guid);
                intent.putExtra("Propertys",Propertys);
                intent.putExtra("PropertysText",PropertysTextcace);
                intent.putExtra("price",price);
                intent.putExtra("Count",currentCount);
                intent.putParcelableArrayListExtra("specItemBeans",specItemBeans);
                startActivity(intent);
                finish();
            }
            break;
        }
    }
    public void close(){
        Intent intent = new Intent();
        Bundle bd=new Bundle();
        bd.putString("chooseSpec",choose.getText().toString().trim());
        bd.putString("paramString",PropertysTextcace);
        bd.putString("GUID",Guid);
        bd.putString("Propertys",Propertyscace);
        bd.putString("PropertysText",PropertysTextcace);
        bd.putString("paramNumber",tv_count.getText().toString().trim());
        intent.putExtras(bd);
//        intent.putExtra("paramlist",specCaceBeans);
        intent.putParcelableArrayListExtra("paramlist",specItemBeans);
        if (shoppingCart!=null){
            intent.putExtra("shoppingCart",shoppingCart);
        }
        setResult(SpecOK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void checkChild(int groudPostion,int childPosition) {
        switch (groudPostion){
            case 0://主规格才更新图片，次要规格不获取图片
                if (specItemBeans.get(groudPostion).getData().get(childPosition).getSrc()!=null){
                    showPreView(specItemBeans.get(groudPostion).getData().get(childPosition).getSrc());
                }
                //adapter传数据activity，activity并传递给制定的Fragment
                for (int j = 0; j <specItemBeans.get(groudPostion).getData().size() ; j++) {
                    if (childPosition==j){
                        specItemBeans.get(groudPostion).getData().get(j).setIscheck(true);
                    }else{
                        specItemBeans.get(groudPostion).getData().get(j).setIscheck(false);
                    }
                }

                colorAdapter.notifyDataSetChanged();
                break;
            case 1:
//                for (int j = 0; j <specitemDataBeans_size.size() ; j++) {
//                    if (childPosition==j){
//                        specitemDataBeans_size.get(j).setIscheck(true);
//                    }else{
//                        specitemDataBeans_size.get(j).setIscheck(false);
//                    }
//                }
                for (int j = 0; j <specItemBeans.get(groudPostion).getData().size() ; j++) {
                    if (childPosition==j){
                        specItemBeans.get(groudPostion).getData().get(j).setIscheck(true);
                    }else{
                        specItemBeans.get(groudPostion).getData().get(j).setIscheck(false);
                    }
                }
                sizeAdapter.notifyDataSetChanged();
                break;
            case 2:
                showPreView(specItemBeans.get(groudPostion).getData().get(childPosition).getSrc());
//                for (int j = 0; j <specitemDataBeans_material.size() ; j++) {
//                    if (childPosition==j){
//                        specitemDataBeans_material.get(j).setIscheck(true);
//                    }else{
//                        specitemDataBeans_material.get(j).setIscheck(false);
//                    }
//                }
                for (int j = 0; j <specItemBeans.get(groudPostion).getData().size() ; j++) {
                    if (childPosition==j){
                        specItemBeans.get(groudPostion).getData().get(j).setIscheck(true);
                    }else{
                        specItemBeans.get(groudPostion).getData().get(j).setIscheck(false);
                    }
                }
                materialAdapter.notifyDataSetChanged();
                break;
        }
//        List<SpecitemDataBean> specitemDataBeans_Cace =new ArrayList<>();
//        specitemDataBeans_Cace.add(specItemBeans.get(groudPostion).getData().get(childPosition));
//        specCaceBeans.get(groudPostion).setData(specitemDataBeans_Cace);
        showSelectSpec(specItemBeans);
    }

    private void showPreView(String src) {
//        imageView1.setImageURI(Uri.parse(AppConstants.PhOTOADDRESS+src));
        Picasso.with(context)
                .load(AppConstants.PhOTOADDRESS+src)
                .resize(ScreenUtil.getScreenWidth(context)/4,ScreenUtil.getScreenHeight(context)/7)
                .config(Bitmap.Config.RGB_565)
//                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into(imageView1);//加载网络图片
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            close();
            return true;
        }
        return super.onTouchEvent(event);
    }
}