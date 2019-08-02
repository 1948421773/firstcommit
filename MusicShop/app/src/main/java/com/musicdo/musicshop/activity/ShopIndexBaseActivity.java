package com.musicdo.musicshop.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.BitmapCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.ItemEducationAdapter;
import com.musicdo.musicshop.adapter.ProductOfShopIndexAdapter;
import com.musicdo.musicshop.adapter.SearchProdAdapter;
import com.musicdo.musicshop.bean.SearchProdBean;
import com.musicdo.musicshop.bean.ShopIndexBase;
import com.musicdo.musicshop.bean.ShopListBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.ShopIndexBaseFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomGridLayoutManager;
import com.musicdo.musicshop.view.CustomLinearLayoutManager;
import com.musicdo.musicshop.view.CustomPopWindow;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.MyLinearLayout;
import com.musicdo.musicshop.view.MyNestedScrollChild;
import com.musicdo.musicshop.view.MyNestedScrollView;
import com.musicdo.musicshop.view.MyRecyclerView;
import com.musicdo.musicshop.view.NestedRecyclerView;
import com.musicdo.musicshop.view.PersonalScrollView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 店铺页
 * Created by Yuedu on 2017/11/14.
 */

public class ShopIndexBaseActivity extends BaseActivity implements View.OnClickListener {
    private int ShopID;
    private NestedScrollView NestedScrollView;
    private ImageView iv_develop_icon,iv_price_state,im_setting;
    private Context context;
    private TextView tv_persinal_name,tv_shop_recommend,tv_shop_hotsell,tv_shop_newprod,tv_empty,tv_shop_attention;
    private LinearLayout ll_shop_banner,viewGroup,ll_back,rl_allprod_memu;
//    LinearLayout ll_shopcategroydetail;
    private RelativeLayout rl_shopcategroylist,rl_shop_homepage_list,rl_menu;
    private FrameLayout rl_shop_homepage;
    private List<Bitmap> bitmaps=new ArrayList<>();
    ShopIndexBase ShopIndexBases=new ShopIndexBase();
    ShopListBean shopListBeans=new ShopListBean();
    private RecyclerView rc_grid_education,rc_shop_hotsell,rc_shop_recommend;
    ProductOfShopIndexAdapter productOfShopIndexAdapter,hotsellShopIndexAdapter;
    RadioGroup rg_shop_tabs,rg_searchtab;
    int pageIndex=1;
    RecyclerView rc_searchprod_overall;
    LoadingDialog dialog;
    ArrayList<SearchProdBean> searchProdBeans=new ArrayList<>();
    SearchProdAdapter searchprodAdapter;
    private boolean showtype=false;//recyclelistview显示布局切换
    GridLayoutManager grid_tend_layoutManage;
//    CustomLinearLayoutManager grid_tend_layoutManage;
//    CustomGridLayoutManager grid_tend_layoutManage;
    private boolean price_state=false;
    private int sortall=0;
    private int OrderType=0;
    private EditText et_search;
    private CheckBox cb_shoplist_chekbox;
    private int lastVisibleItem;
    RadioButton rb_shop_price;
    CustomPopWindow mCustomPopWindow;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopindexbase);
        MyApplication.getInstance().addActivity(this);
        context=this;
        ShopID=getIntent().getIntExtra("ShopID",0);
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        intView();
        initData();
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

    private void initData() {
            if (ShopID==0) {
                ToastUtil.showShort(context,"数据加载超时");
                return;
            }
            OkHttpUtils.get(AppConstants.GetShopIndexBase)
                    .tag(this)
                    .params("ShopID",ShopID)//固定地方便调试，其他ID没数据"26013"
                    .params("UserID",AppConstants.USERID)
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
                                commentObject = new JSONObject(jsonData);
                                comment =  commentObject.getString("Data");
                                Message = jsonObject.getString("Message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //评论Data不是array
                            ShopIndexBases= gson.fromJson(jsonData, ShopIndexBase.class);
                            if (!ShopIndexBases.getBasic().getShopIco().equals("")){
                                Picasso.with(context)
                                        .load(AppConstants.PhOTOADDRESS+ShopIndexBases.getBasic().getShopIco())
                                        .resize(AppConstants.ScreenWidth/7,((AppConstants.ScreenWidth/7)*138)/329)
//                                        .onlyScaleDown()
                                        .config(Bitmap.Config.RGB_565)
                                        .placeholder(R.mipmap.img_start_loading)
                                        .error(R.mipmap.img_load_error)
                                        .into(iv_develop_icon);
                            }else{
                                iv_develop_icon.setImageResource(R.mipmap.ic_launcher);
                            }
                            tv_persinal_name.setText(ShopIndexBases.getBasic().getShopName());
                            final List<String> imgs=new ArrayList<>();
                            if(ShopIndexBases.getImgUrlArray().length()!=0){//分割店铺首页介绍图
                                String urlArray=ShopIndexBases.getImgUrlArray();
                                for(String o : urlArray.split(",")) {
                                    String inicode=string2Unicode(o);//字符串转unicode
                                    inicode=inicode.replaceAll("\\\\u1f","");//去掉json中的\u001f
                                        inicode=unicode2String(inicode);//unicode转字符串
                                        imgs.add(inicode);

                                }
                            }
                            for (int i = 0; i < imgs.size(); i++) {
                                        ImageView imageView = new ImageView(context);
                                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                                        imageView.setPadding(0,10,0,10);
                                        Picasso.with(context)
                                                .load(AppConstants.PhOTOADDRESS+imgs.get(i))
                                                .resize(AppConstants.ScreenWidth,AppConstants.ScreenHeight/5)
//                                                .onlyScaleDown()
                                                .config(Bitmap.Config.RGB_565)
                                                .placeholder(R.mipmap.img_start_loading)
                                                .error(R.mipmap.img_load_error)
                                                .into(imageView);
                                        viewGroup.addView(imageView);

                            }
                            if(ShopIndexBases.getBasic().getIsConcern()==1){//店铺关注
                                tv_shop_attention.setText("已关注");
                                tv_shop_attention.setTextColor(getResources().getColor(R.color.colorAccent));
                                tv_shop_attention.setBackground(getResources().getDrawable(R.drawable.shop_attention));
                            }else{
                                tv_shop_attention.setText("关注");
                                tv_shop_attention.setTextColor(getResources().getColor(R.color.white));
                                tv_shop_attention.setBackground(getResources().getDrawable(R.drawable.login_login_click_now));
                            }
                        }
                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
//                        ToastUtil.showShort(activity,"开始加载数据");
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ToastUtil.showShort(context,"数据加载超时");
                        }
                    });

        OkHttpUtils.get(AppConstants.GetProductOfShopIndex)
                .tag(this)
                .params("ShopID",ShopID)//固定地方便调试，其他ID没数据"26013"
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
                            commentObject = new JSONObject(jsonData);
                            comment =  commentObject.getString("Data");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        shopListBeans= gson.fromJson(jsonData, ShopListBean.class);
                        if (shopListBeans!=null){
                           if(shopListBeans.getList1()!=null){
                               productOfShopIndexAdapter = new ProductOfShopIndexAdapter(context,shopListBeans.getList1());
                               rc_grid_education.setAdapter(productOfShopIndexAdapter);//recyclerview设置适配器
                               productOfShopIndexAdapter.setOnItemClickListener(new ProductOfShopIndexAdapter.OnSearchItemClickListener(){
                                   @Override
                                   public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                       Intent intent=new Intent(context,ProductDetailActivity.class);
                                       intent.putExtra("prod_id",shopListBeans.getList1().get(position).getProductID());
                                       startActivity(intent);
                                   }
                               });
                               rc_grid_education.setNestedScrollingEnabled(false);
                               tv_shop_newprod.setVisibility(View.VISIBLE);
                               rc_grid_education.setVisibility(View.VISIBLE);
                           }else{
                               tv_shop_newprod.setVisibility(View.GONE);
                               rc_grid_education.setVisibility(View.GONE);
                           }

                            if(shopListBeans.getList2()!=null){
                                productOfShopIndexAdapter = new ProductOfShopIndexAdapter(context,shopListBeans.getList2());
                                rc_shop_hotsell.setAdapter(productOfShopIndexAdapter);//recyclerview设置适配器
                                productOfShopIndexAdapter.setOnItemClickListener(new ProductOfShopIndexAdapter.OnSearchItemClickListener(){
                                    @Override
                                    public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(context,ProductDetailActivity.class);
                                        intent.putExtra("prod_id",shopListBeans.getList2().get(position).getProductID());
                                        startActivity(intent);
                                    }
                                });
                                rc_shop_hotsell.setNestedScrollingEnabled(false);
                                tv_shop_hotsell.setVisibility(View.VISIBLE);
                                rc_shop_hotsell.setVisibility(View.VISIBLE);
                            }else{
                                tv_shop_hotsell.setVisibility(View.GONE);
                                rc_shop_hotsell.setVisibility(View.GONE);
                            }

                            if(shopListBeans.getList3()!=null){
                                productOfShopIndexAdapter = new ProductOfShopIndexAdapter(context,shopListBeans.getList3());
                                rc_shop_recommend.setAdapter(productOfShopIndexAdapter);//recyclerview设置适配器
                                productOfShopIndexAdapter.setOnItemClickListener(new ProductOfShopIndexAdapter.OnSearchItemClickListener(){
                                    @Override
                                    public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(context,ProductDetailActivity.class);
                                        intent.putExtra("prod_id",shopListBeans.getList3().get(position).getProductID());
                                        startActivity(intent);
                                    }
                                });
                                rc_shop_recommend.setNestedScrollingEnabled(false);
                                rc_shop_recommend.setVisibility(View.VISIBLE);
                                tv_shop_recommend.setVisibility(View.VISIBLE);
                            }else{
                                rc_shop_recommend.setVisibility(View.GONE);
                                tv_shop_recommend.setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(activity,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");
                    }
                });
    }


    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    private void intView() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.menu_pop,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setAnimationStyle(R.style.mypopwindow_anim_style)
                .create();

        rb_shop_price=(RadioButton) findViewById(R.id.rb_shop_price);
        rb_shop_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShopID==0){
                    return;
                }
                searchProdBeans.clear();
                searchprodAdapter.notifyDataSetChanged();
                if (price_state){
                    iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_down));
                    price_state=false;
                    OrderType=3;
                }else{
                    iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_up));
                    price_state=true;
                    OrderType=4;
                }
                pageIndex=1;
                doSth(OrderType);
            }
        });

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setOnClickListener(this);
        //全部商品列表
        rc_searchprod_overall = (RecyclerView) findViewById(R.id.rc_searchprod_overall);
        NestedScrollView=(NestedScrollView)findViewById(R.id.NestedScrollView);
        grid_tend_layoutManage=new GridLayoutManager(context, 2);
        NestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    //底部加载
                    if (searchProdBeans!=null){
                        if(searchProdBeans.size()!=0){
                                Picasso.with(context).resumeTag("list");
                                pageIndex++;
                                doSth(OrderType);
                        }
                    }
                }
            }
        });
        rc_searchprod_overall.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (searchProdBeans!=null){
                    if(searchProdBeans.size()!=0){
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchprodAdapter.getItemCount()) {
                            Picasso.with(context).resumeTag("list");
                            pageIndex++;
                            doSth(OrderType);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = grid_tend_layoutManage.findLastVisibleItemPosition();
                Picasso.with(context).pauseTag("list");
            }
        });

        tv_shop_attention=(TextView) findViewById(R.id.tv_shop_attention);
        tv_shop_attention.setOnClickListener(this);
//        tv_empty=(TextView) findViewById(R.id.tv_empty);
//        tv_empty.setText("该店铺没有商品");
        iv_price_state = (ImageView) findViewById(R.id.iv_price_state);
        im_setting = (ImageView) findViewById(R.id.im_setting);
        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
        rg_searchtab=(RadioGroup)findViewById(R.id.rg_searchtab);
        rg_searchtab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (ShopID==0){
                    return;
                }
                switch (i){
                    case R.id.rb_shop_hotsell: //销量
                        price_state=false;
                        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
                        OrderType=1;
                        pageIndex=1;
                        doSth(OrderType);
                        break;
                    case R.id.rb_shop_newprod: //新品
                        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
                        price_state=false;
                        OrderType=2;
                        pageIndex=1;
                        doSth(OrderType);
                        break;
                    case R.id.rb_shop_price: //价格

                        break;
                }
                //根据位置得到相应的Fragment
//                BaseFragment baseFragment = getFragment(position);
                /**
                 * 第一个参数: 上次显示的Fragment
                 * 第二个参数: 当前正要显示的Fragment
                 */
//                switchFragment(tempFragment,baseFragment);
            }
        });

        cb_shoplist_chekbox = (CheckBox) findViewById(R.id.cb_shoplist_chekbox);
        cb_shoplist_chekbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (searchProdBeans==null){
                    return;
                }else{
                    if (searchProdBeans.size()==0){
                        return;
                    }
                }
                rc_searchprod_overall.setVisibility(View.GONE);
                showtype=isChecked;
                int count = isChecked ?  1: 2;
                if (searchprodAdapter!=null)
                    searchprodAdapter.changeShowType(showtype);
                changeShowItemCount(count);
//                switchContent2(fragments.get(0));

                /*SearchProdOverallFragment anotherRightFragment=new SearchProdOverallFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("type",showtype);
                anotherRightFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.id_fragment,anotherRightFragment);
//              fragmentTransaction.add(R.id.fragment_right,anotherRightFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                ToastUtil.showShort(context,""+isChecked);*/

            }
        });

        rl_shop_homepage_list=(RelativeLayout) findViewById(R.id.rl_shop_homepage_list);
        rl_menu=(RelativeLayout) findViewById(R.id.rl_menu);
        rl_menu.setOnClickListener(this);
        rl_shopcategroylist=(RelativeLayout) findViewById(R.id.rl_shopcategroylist);
        rl_shopcategroylist.setOnClickListener(this);
        rl_shop_homepage=(FrameLayout) findViewById(R.id.rl_shop_homepage);
        viewGroup=(LinearLayout) findViewById(R.id.viewGroup);
        rl_allprod_memu=(LinearLayout) findViewById(R.id.rl_allprod_memu);
//        ll_shopcategroydetail=(LinearLayout) findViewById(R.id.ll_shopcategroydetail);
        ll_back=(LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        ll_shop_banner=(LinearLayout) findViewById(R.id.ll_shop_banner);
        CollapsingToolbarLayout.LayoutParams lparams=new CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT,CollapsingToolbarLayout.LayoutParams.MATCH_PARENT);
        lparams.width=AppConstants.ScreenWidth;
        lparams.height=AppConstants.ScreenHeight/4;
        ll_shop_banner.setLayoutParams(lparams);
        tv_persinal_name=(TextView) findViewById(R.id.tv_persinal_name);
        tv_shop_recommend=(TextView) findViewById(R.id.tv_shop_recommend);
        tv_shop_hotsell=(TextView) findViewById(R.id.tv_shop_hotsell);
        tv_shop_newprod=(TextView) findViewById(R.id.tv_shop_newprod);
        iv_develop_icon=(ImageView)findViewById(R.id.iv_develop_icon);

        rc_grid_education = (RecyclerView) findViewById(R.id.rc_grid_education );
        rc_grid_education.setLayoutManager(new GridLayoutManager(context,2));

        rc_shop_hotsell = (RecyclerView) findViewById(R.id.rc_shop_hotsell );
        rc_shop_hotsell.setLayoutManager(new GridLayoutManager(context,2));

        rc_shop_recommend = (RecyclerView) findViewById(R.id.rc_shop_recommend );
        rc_shop_recommend.setLayoutManager(new GridLayoutManager(context,2));

        rg_shop_tabs=(RadioGroup)findViewById(R.id.rg_shop_tabs);

        rg_shop_tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (ShopID==0){
                        return;
                    }
                switch (i){
                    case R.id.rb_shop_allprod: //店铺首页
                        rl_shop_homepage.setVisibility(View.GONE);
//                        ll_shopcategroydetail.setVisibility(View.VISIBLE);
                        rl_allprod_memu.setVisibility(View.VISIBLE);
                        viewGroup.setVisibility(View.GONE);
                        rl_shop_homepage_list.setVisibility(View.GONE);
//                        ShopIndexBaseFragment fragment = new ShopIndexBaseFragment();
//                        FragmentManager manager = getFragmentManager();
//                        FragmentTransaction transaction=manager.beginTransaction();
//                        Bundle data = new Bundle();
//                        data.putInt("ShopID", ShopID);
//                        fragment.setArguments(data);//通过Bundle向Activity中传递值
//                        // 把碎片添加到碎片中
//                        transaction.replace(R.id.ll_shopcategroydetail, fragment);
//                        transaction.commit();
                        doSth(OrderType);

                        break;
                    case R.id.rb_shop_homepage: //全部商品
                        rl_shop_homepage.setVisibility(View.VISIBLE);
//                        ll_shopcategroydetail.setVisibility(View.GONE);
                        rl_allprod_memu.setVisibility(View.GONE);
                        viewGroup.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rl_shop_homepage_list.setVisibility(View.VISIBLE);
                            }
                        },100);
                        break;
                }
            }
        });
    }


    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     */
    private void handleLogic(View contentView){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomPopWindow!=null){
                    mCustomPopWindow.dissmiss();
                }
                String showContent = "";
                switch (v.getId()){
                    case R.id.menu1:
                        break;
                    case R.id.menu2:{
                        Intent intent=new Intent(context,MainActivity.class);
                        intent.putExtra("ShoppingCartFragment", "");
                        intent.putExtra("specItemBeans", "");
                        intent.putExtra("HomeFragment", "HomeFragment");
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu3:
//                        showContent = "点击 Item菜单3";
                        break;
                    case R.id.menu4:{
                        Intent intent=new Intent(context,MainActivity.class);
                        intent.putExtra("PersonalFragment", "PersonalFragment");
                        intent.putExtra("specItemBeans", "");
                        startActivity(intent);
                    }
                    break;

                }
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
    }

    private void doSth(int OrderType) {
//        if (sort!=0){
//            sortall=sort;
//        }
        String keyOrcategory="";
        String keyOrcategoryString="";
//        if (categoryID!=0){
//            keyOrcategory=String.valueOf(categoryID);
//            keyOrcategoryString="categoryID";
//        }else {
//            keyOrcategory=key;
//            keyOrcategoryString="keyword";
//        }

        OkHttpUtils.get(AppConstants.GetShopProductSearch)
                .tag(this)
                .params("ShopID",ShopID)
                .params("pageIndex",pageIndex)
                .params("PageSize",10)
                .params("OrderType",OrderType)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                    Picasso.with(context).resumeTag("list");

                                }
                            }, 1000);
                        }
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
                        if (!jsonData.equals("[]")){
                            rc_searchprod_overall.setVisibility(View.VISIBLE);
//                            tv_empty.setVisibility(View.GONE);
//                            pageIndex++;
                        }else{
                            if (pageIndex==1){
                                rc_searchprod_overall.setVisibility(View.GONE);
//                                tv_empty.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        if (searchProdBeans!=null){
                            if (pageIndex<2){//第一页时初始化adapter
                                searchProdBeans= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
                                setData();
                            }else{//第二页之后加入列表后刷新adapter
                                List<SearchProdBean>  more= gson.fromJson(jsonData, new TypeToken<List<SearchProdBean>>() {}.getType());
                                searchProdBeans.addAll(more);
                                searchprodAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!dialog.isShowing()){
                            dialog.show();
                            Picasso.with(context).pauseTag("list");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                    Picasso.with(context).resumeTag("list");

                                }
                            }, 1000);
                        }
                    }
                });
    }

    private void setData() {
//        if(searchprodAdapter==null){
        searchprodAdapter = new SearchProdAdapter(context,searchProdBeans,showtype,"list");
        rc_searchprod_overall.setAdapter(searchprodAdapter);//recyclerview设置适配器
//        grid_tend_layoutManage.setSmoothScrollbarEnabled(true);
//        grid_tend_layoutManage.setAutoMeasureEnabled(true);
        rc_searchprod_overall.setLayoutManager(grid_tend_layoutManage);
        rc_searchprod_overall.setNestedScrollingEnabled(false);
        rc_searchprod_overall.setHasFixedSize(true);
        searchprodAdapter.setOnItemClickListener(new SearchProdAdapter.OnSearchItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,ProductDetailActivity.class);
                intent.putExtra("prod_id",searchProdBeans.get(position).getID());
                startActivity(intent);
            }
        });
//        }else{
//            //让适配器刷新数据
//            searchprodAdapter = new SearchProdAdapter(context,searchProdBeans,showtype);
//            searchprodAdapter.notifyDataSetChanged();//
//        }
    }

    /**
     * 更改每行显示数目
     */
    private void changeShowItemCount(int count) {
        final int i = 3 - count;
        grid_tend_layoutManage.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return i;
            }
        });
        if (searchprodAdapter!=null)
            searchprodAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rc_searchprod_overall.setVisibility(View.VISIBLE);
            }
        },200);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_back:

                finish();
                break;
            case R.id.rl_menu:
                mCustomPopWindow.showAsDropDown(im_setting,0,0);
                break;
            case R.id.tv_shop_attention://店铺关注
                if (AppConstants.USERID==0){
                    ToastUtil.showShort(context,"请先登录");
                    return;
                }
                if (tv_shop_attention.getText().toString().trim().endsWith("已关注")){
                    DeleteCollectShop();
                }else{
                    CollectShop();
                }


                break;
            case R.id.et_search:{
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("ShopIndexBaseActivity","ShopIndexBaseActivity");
                intent.putExtra("ShopID",ShopID);
                startActivity(intent);
            }
            break;
            case R.id.rl_shopcategroylist:{
                Intent intent=new Intent(context,ShopCatetoryAndBrandActivity.class);
                intent.putExtra("ShopID",ShopID);
                startActivity(intent);
            }
                break;
        }
    }

    private void DeleteCollectShop() {
        OkHttpUtils.post(AppConstants.DeleteCollectShop)
                .tag(this)
                .params("ShopID",ShopID)//固定地方便调试，其他ID没数据"26013"
                .params("UserID",AppConstants.USERID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if(dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();

                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String comment=null;
                        String Message=null;
                        boolean Flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Flag=jsonObject.getBoolean("Flag");
                            commentObject = new JSONObject(jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag){
                            tv_shop_attention.setText("关注");
                            tv_shop_attention.setTextColor(getResources().getColor(R.color.white));
                            tv_shop_attention.setBackground(getResources().getDrawable(R.drawable.login_login_click_now));

                        }
                        //评论Data不是array
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
                            ToastUtil.showShort(context,"数据加载超时");
                        }
                    }
                });
    }

    private void CollectShop() {
        OkHttpUtils.post(AppConstants.CollectShop)
                .tag(this)
                .params("ShopID",ShopID)//固定地方便调试，其他ID没数据"26013"
                .params("UserID",AppConstants.USERID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        if(dialog.isShowing()) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();

                                }
                            }, 1000);
                        }
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String comment=null;
                        String Message=null;
                        boolean Flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Flag=jsonObject.getBoolean("Flag");
                            commentObject = new JSONObject(jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag){
                                tv_shop_attention.setText("已关注");
                                tv_shop_attention.setTextColor(getResources().getColor(R.color.colorAccent));
                                tv_shop_attention.setBackground(getResources().getDrawable(R.drawable.shop_attention));

                        }
                        //评论Data不是array
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
                        ToastUtil.showShort(context,"数据加载超时");
                    }
                }
    });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null){
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
}
