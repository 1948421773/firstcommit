package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.BrandAndCategoryByKeywordAdapter;
import com.musicdo.musicshop.adapter.SearchProdAdapter;
import com.musicdo.musicshop.bean.BrandAndCategoryByKeywordBean;
import com.musicdo.musicshop.bean.BrandAndCategoryByKeywordItemBean;
import com.musicdo.musicshop.bean.SearchProdBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.BaseFragment;
import com.musicdo.musicshop.fragments.GroupDevelopment;
import com.musicdo.musicshop.fragments.OrderAllFragment;
import com.musicdo.musicshop.fragments.PopularityTend;
import com.musicdo.musicshop.fragments.SearchProdOverallFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomPopWindow;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/12/7.
 */

public class ClassifyProuductActivity extends AppCompatActivity implements View.OnClickListener,BrandAndCategoryByKeywordAdapter.CheckChangeItem{
    final String  TAG="ClassifyProuductActivity";
    FrameLayout frameLayout;
    RadioGroup rg_searchtab;
    private RadioButton rb_searchprod_price,rb_searchprod_sales;//顶部选择框
    private ArrayList<BaseFragment> fragments;
    private ImageView iv_price_state,iv_sales_state,im_setting,iv_line;
    private boolean price_state=false;
    private boolean sales_state=false;
    private Context context;
    private int position = 0;
    private BaseFragment tempFragment;
    private CheckBox cb_searchprod_chekbox,cb_kind_choose,cb_brand_choose;
    private boolean showtype=false;//recyclelistview显示布局切换
    private TextView tv_empty,tv_searchprod_screen;
    private RecyclerView rc_searchprod_overall,rc_grid_education,rc_shop_hotsell;
    SearchProdAdapter searchprodAdapter;
    ArrayList<SearchProdBean> searchProdBeans=new ArrayList<>();
    BrandAndCategoryByKeywordBean brandAndCategoryByKeywordBeans=new BrandAndCategoryByKeywordBean();
    ArrayList<BrandAndCategoryByKeywordItemBean> categortBeans=new ArrayList<BrandAndCategoryByKeywordItemBean>();
    ArrayList<BrandAndCategoryByKeywordItemBean> brandBeans=new ArrayList<BrandAndCategoryByKeywordItemBean>();
    private int lastVisibleItem;
    GridLayoutManager grid_tend_layoutManage;
    private int pageIndex=1;
    private int sortall=0;
    private String key="";
    private int categoryID=0;
    private String categoryIDString="";
    private String brandIDString="";
    private ImageView iv_qrcode;
    private EditText et_search,ed_low_price,ed_height_price;
    LoadingDialog dialog;
    private boolean isending=false;
    private RelativeLayout rl_menu;
    //    PopupWindow pop;
    CustomPopWindow mCustomPopWindow;
    private DrawerLayout mDrawerLayout = null;
    int type;
    String content;
    BrandAndCategoryByKeywordAdapter categoryAdapter,brandAdapter;
    TextView tv_shop_newprod,tv_shop_hotsell,tv_reset,tv_submit;
    int chooseItem=0;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchprouduct);
        MyApplication.getInstance().addActivity(this);
        context=this;
        key=getIntent().getStringExtra("keyword");
        if (key==null){
            key="";
        }
        brandIDString=getIntent().getStringExtra("brandIDString");
        categoryID=getIntent().getIntExtra("categoryID",0);
        init();
//        initFragment();
        initListener();
//        switchFragment(null,fragments.get(0));
        doSth(sortall);
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

        //注：回调 1
        Bugtags.onResume(this);
        if(AppConstants.ScreenWidth==0||AppConstants.ScreenWidth==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    private void init() {

        //筛选
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //        popupwindow
        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        rl_menu.setOnClickListener(this);
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        /*View contentView = mLayoutInflater.inflate(R.layout.menu_pop, null);// R.layout.pop为 PopupWindow 的布局文件
        pop= new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_menu_bg));                            // 指定 PopupWindow 的背景
        pop.setFocusable(true);                   // 设定 PopupWindow 取的焦点，创建出来的 PopupWindow 默认无焦点*/

        View contentView = LayoutInflater.from(this).inflate(R.layout.menu_pop,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setAnimationStyle(R.style.mypopwindow_anim_style)
                .create();
        //加载数据前初始化刷新控件
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        cb_searchprod_chekbox = (CheckBox) findViewById(R.id.cb_searchprod_chekbox);
        cb_kind_choose = (CheckBox) findViewById(R.id.cb_kind_choose);
        cb_brand_choose = (CheckBox) findViewById(R.id.cb_brand_choose);
        cb_brand_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    brandAdapter.setShowNumber(false);
                }else{
                    brandAdapter.setShowNumber(true);
                }
                brandAdapter.notifyDataSetChanged();
            }
        });
        cb_kind_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    categoryAdapter.setShowNumber(false);
                }else{
                    categoryAdapter.setShowNumber(true);
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });
        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        tv_searchprod_screen = (TextView) findViewById(R.id.tv_searchprod_screen);
        tv_searchprod_screen.setVisibility(View.GONE);
        tv_shop_newprod = (TextView) findViewById(R.id.tv_shop_newprod);
        tv_shop_hotsell = (TextView) findViewById(R.id.tv_shop_hotsell);
        tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(this);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(this);

        tv_searchprod_screen.setOnClickListener(this);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        tv_empty.setText("没有相关的产品");
        et_search = (EditText) findViewById(R.id.et_search);
        ed_low_price = (EditText) findViewById(R.id.ed_low_price);
        ed_height_price = (EditText) findViewById(R.id.ed_height_price);
        et_search.setText(key);
        et_search.setOnClickListener(this);
        iv_qrcode.setOnClickListener(this);
        cb_searchprod_chekbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (searchProdBeans==null){
                    return;
                }else{
                    if (searchProdBeans.size()==0){
                        return;
                    }
                }
//                        Picasso.with(context).pauseTag("list");
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

        iv_price_state = (ImageView) findViewById(R.id.iv_price_state);
        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
        im_setting = (ImageView) findViewById(R.id.im_setting);
        iv_line = (ImageView) findViewById(R.id.iv_line);
        iv_line.setVisibility(View.GONE);
        iv_sales_state = (ImageView) findViewById(R.id.iv_sales_state);
        iv_sales_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
        rb_searchprod_price=(RadioButton) findViewById(R.id.rb_searchprod_price);
        rb_searchprod_sales=(RadioButton) findViewById(R.id.rb_searchprod_sales);
        rb_searchprod_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchProdBeans==null){
                    return;
                }else{
                    if (searchProdBeans.size()==0){
                        return;
                    }
                }
                searchProdBeans.clear();
                searchprodAdapter.notifyDataSetChanged();
                if (price_state){
                    iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_down));
                    price_state=false;
                    sortall=2;
                }else{
                    iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_up));
                    price_state=true;
                    sortall=1;
                }
                pageIndex=1;
                isending=false;
                doSth(sortall);
            }
        });
        rb_searchprod_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchProdBeans==null){
                    return;
                }else{
                    if (searchProdBeans.size()==0){
                        return;
                    }
                }
                searchProdBeans.clear();
                searchprodAdapter.notifyDataSetChanged();
                if (sales_state){
                    iv_sales_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_down));
                    sales_state=false;
                    sortall=4;
                }else{
                    iv_sales_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_up));
                    sales_state=true;
                    sortall=3;
                }
                pageIndex=1;
                isending=false;
                doSth(sortall);
            }
        });

        frameLayout=(FrameLayout) findViewById(R.id.id_fragment);
        rg_searchtab=(RadioGroup) findViewById(R.id.rg_searchtab);

        rc_searchprod_overall = (RecyclerView) findViewById(R.id.rc_searchprod_overall);
        rc_shop_hotsell = (RecyclerView) findViewById(R.id.rc_shop_hotsell);
        rc_shop_hotsell.setLayoutManager(new GridLayoutManager(context,3));

        rc_grid_education = (RecyclerView) findViewById(R.id.rc_grid_education);
        rc_grid_education.setLayoutManager(new GridLayoutManager(context,3));
        /*rc_searchprod_overall.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    Picasso.with(context).resumeTag("list");
                }
                else
                {
                    Picasso.with(context).pauseTag("list");
                }
            }
        });*/
        /*if (showtype){
            grid_tend_layoutManage= new GridLayoutManager(context, 2);
        }else{
            grid_tend_layoutManage= new GridLayoutManager(context, 1);
        }*/
        grid_tend_layoutManage= new GridLayoutManager(context, 2);
        rc_searchprod_overall.setLayoutManager(grid_tend_layoutManage);
        rc_searchprod_overall.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (searchProdBeans!=null){
                    if(searchProdBeans.size()!=0){
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchprodAdapter.getItemCount()) {
                            pageIndex++;
                            doSth(sortall);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = grid_tend_layoutManage.findLastVisibleItemPosition();
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
//                Picasso.with(context).resumeTag("list");
            }
        },200);
    }

    /**
     * 添加的时候按照顺序
     */
    private void initFragment(){
        fragments = new ArrayList<>();
        SearchProdOverallFragment myFragment = new SearchProdOverallFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("type",showtype);
        myFragment.setArguments(bundle);
        fragments.add(myFragment);
        fragments.add(new OrderAllFragment());
        fragments.add(new GroupDevelopment());
        fragments.add(new PopularityTend());
    }

    private void initListener() {
        rg_searchtab.check(R.id.rb_searchprod_overall);
        rg_searchtab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (searchProdBeans==null){
                    return;
                }else{
                    if (searchProdBeans.size()==0){
                        return;
                    }
                }
                switch (i){
                    case R.id.rb_searchprod_overall: //综合
                        position = 0;
                        price_state=false;
                        sales_state=false;
                        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
                        iv_sales_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
                        sortall=0;
                        pageIndex=1;
                        isending=false;
                        doSth(sortall);
                        break;
                    case R.id.rb_searchprod_sales: //销量
                        position = 1;
                        price_state=false;
                        isending=false;
                        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
//                        if (sales_state){
//                            sortall=3;
//                            pageIndex=1;
//                        }else{
//                            sortall=4;
//                            pageIndex=1;
//                        }
//                        doSth(sortall);
                        break;
                    case R.id.rb_searchprod_price: //价格
                        sales_state=false;
                        position = 2;
                        isending=false;
                        iv_sales_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
//                        if (price_state){
//                            sortall=1;
//                        }else{
//                            sortall=2;
//                        }
//                        doSth(sortall);
                        break;

                    default:
                        isending=false;
                        position = 0;
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
    }

    /**
     * 根据位置得到对应的 Fragment
     * @param position
     * @return
     */
    private BaseFragment getFragment(int position){
        if(fragments != null && fragments.size()>0){
            BaseFragment baseFragment = fragments.get(position);
            return baseFragment;
        }
        return null;
    }

    /**
     * 切换Fragment
     * @param fragment
     * @param nextFragment
     */
    private void switchFragment(BaseFragment fragment,BaseFragment nextFragment){
        if (tempFragment != nextFragment){
            tempFragment = nextFragment;
            if (nextFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加成功
                if (!nextFragment.isAdded()){
                    //隐藏当前的Fragment
                    if (fragment != null){
                        transaction.hide(fragment);
                    }
                    //添加Fragment
                    transaction.add(R.id.id_fragment,nextFragment).commit();
                }else {
                    //隐藏当前Fragment
                    if (fragment != null){
                        transaction.hide(fragment);
                    }
                    transaction.show(nextFragment).commit();
                }

            }
            if (nextFragment.equals(SearchProdOverallFragment.class)){
                android.app.Fragment exFragment = (android.app.Fragment)getFragmentManager().findFragmentById(R.id.id_fragment);
                     /*rl_loopswitch=(RelativeLayout)exFragment.getView().findViewById(R.id.rl_loopswitch);
                    rl_loopswitch.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();

                if(action == MotionEvent.ACTION_DOWN) {
                    // 记录点击到ViewPager时候，手指的X坐标
                    mLastX = event.getX();
                }
                if(action == MotionEvent.ACTION_MOVE) {
                    // 超过阈值
                    if(Math.abs(event.getX() - mLastX) > 1.0f) {
                        rl_loopswitch.setEnabled(false);
//                        sv_home_layout.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if(action == MotionEvent.ACTION_UP) {
                    // 用户抬起手指，恢复父布局状态
//                    sv_home_layout.requestDisallowInterceptTouchEvent(false);
                    rl_loopswitch.setEnabled(true);
                }
                            Log.i(TAG, "switchFragment: 点了该空控件");
                return false;
                        }
                    });*/
            }
        }
    }

    /**
     * 修改显示的内容 但会重新加载 *
     */
    public void switchContent2(Fragment to){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.id_fragment,to);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    private void doSth(int sort) {
        if (isending){
            return;
        }
        if (sort!=0){
            sortall=sort;
        }
        /*String keyOrcategory="";
        String keyOrcategoryString="";*/
        if (categoryID!=0){
           /* keyOrcategory=String.valueOf(categoryID);
            keyOrcategoryString="categoryID";*/
            type=2;
            content=String.valueOf(categoryID);
            categoryIDString=String.valueOf(categoryID);
        }else{
            type=1;
            content=String.valueOf(key);
            /*keyOrcategory=key;
            keyOrcategoryString="keyword";*/
            categoryIDString="";
        }

        String lowprice=ed_low_price.getText().toString().trim();
        String heightprice=ed_height_price.getText().toString().trim();
        OkHttpUtils.get(AppConstants.SearchProduct)
                .tag(this)
                .params("price_min",lowprice)
                .params("price_max",heightprice)
//                .params("categoryID",categoryID)
                .params("pageIndex",pageIndex)
//                .params(keyOrcategoryString,keyOrcategory)
                .params("categoryID",categoryIDString)
                .params("brandID",brandIDString)
                .params("keyword",key)
                .params("sort",sortall)
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
//                        Picasso.with(context).resumeTag("list");
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
//                            Log.i(TAG, "onSuccess: "+jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!jsonData.equals("[]")){
                            rc_searchprod_overall.setVisibility(View.VISIBLE);
                            tv_empty.setVisibility(View.GONE);
//                            pageIndex++;
                        }else{
                            if (pageIndex==1){
                                rc_searchprod_overall.setVisibility(View.GONE);
                                tv_empty.setVisibility(View.VISIBLE);
                            }else{
                                isending=true;
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
                        }
//                        Picasso.with(context).pauseTag("list");
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
//                        Picasso.with(context).resumeTag("list");
                    }
                });

        if (brandAndCategoryByKeywordBeans!=null){
            if (brandAndCategoryByKeywordBeans.getCategory()==null&&brandAndCategoryByKeywordBeans.getBrand()==null){
                OkHttpUtils.get(AppConstants.GetBrandAndCategoryByKeyword)
                        .tag(this)
                        .params("type",type)
                        .params("content",content)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, okhttp3.Call call, Response response) {

//                        Picasso.with(context).resumeTag("list");
                                JSONObject jsonObject;
                                Gson gson = new Gson();
                                String jsonData = null;
                                String Message = null;
                                try {
                                    jsonObject = new JSONObject(s);
                                    jsonData = jsonObject.getString("ReturnData");
                                    Message = jsonObject.getString("Message");
//                                    Log.i(TAG, "onSuccess: "+jsonData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                brandAndCategoryByKeywordBeans = gson.fromJson(jsonData, BrandAndCategoryByKeywordBean.class);
                                if (View.VISIBLE==tv_searchprod_screen.getVisibility()){
                                if (brandAndCategoryByKeywordBeans != null) {
                                    if (brandAndCategoryByKeywordBeans.getBrand() != null) {
                                        brandBeans.clear();
                                        brandBeans.addAll(brandAndCategoryByKeywordBeans.getBrand());
                                        brandAdapter = new BrandAndCategoryByKeywordAdapter(context, brandBeans, "Brand");
                                        brandAdapter.setCheckChangeItem(ClassifyProuductActivity.this);
                                        rc_grid_education.setAdapter(brandAdapter);//recyclerview设置适配器
                                        brandAdapter.setOnItemClickListener(new BrandAndCategoryByKeywordAdapter.OnItemClickLitener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                for (int i = 0; i < brandBeans.size(); i++) {
                                                    if (i == position) {
                                                        if (brandBeans.get(i).ischeck()) {
                                                            brandBeans.get(i).setIscheck(false);
                                                        } else {
                                                            brandBeans.get(i).setIscheck(true);
                                                            chooseItem = position;
                                                        }
                                                    } else {
                                                        brandBeans.get(i).setIscheck(false);
                                                    }
                                                }
                                                brandAdapter.notifyDataSetChanged();
                                            }
                                        });
                                        rc_grid_education.setNestedScrollingEnabled(false);
                                        tv_shop_newprod.setVisibility(View.VISIBLE);
                                        rc_grid_education.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_shop_newprod.setVisibility(View.GONE);
                                        rc_grid_education.setVisibility(View.GONE);
                                    }

                                    if (brandAndCategoryByKeywordBeans.getCategory() != null) {
                                        categortBeans.clear();
                                        categortBeans.addAll(brandAndCategoryByKeywordBeans.getCategory());
                                        categoryAdapter = new BrandAndCategoryByKeywordAdapter(context, categortBeans, "Category");
                                        categoryAdapter.setCheckChangeItem(ClassifyProuductActivity.this);
                                        rc_shop_hotsell.setAdapter(categoryAdapter);//recyclerview设置适配器
                                        categoryAdapter.setOnItemClickListener(new BrandAndCategoryByKeywordAdapter.OnItemClickLitener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                for (int i = 0; i < categortBeans.size(); i++) {
                                                    if (i == position) {
                                                        if (categortBeans.get(i).ischeck()) {
                                                            categortBeans.get(i).setIscheck(false);
                                                        } else {
                                                            categortBeans.get(i).setIscheck(true);
                                                            chooseItem = position;
                                                        }
                                                    } else {
                                                        categortBeans.get(i).setIscheck(false);
                                                    }
                                                }
                                                categoryAdapter.notifyDataSetChanged();
                                            }
                                        });
                                        rc_shop_hotsell.setNestedScrollingEnabled(false);
                                        tv_shop_hotsell.setVisibility(View.VISIBLE);
                                        rc_shop_hotsell.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_shop_hotsell.setVisibility(View.GONE);
                                        rc_shop_hotsell.setVisibility(View.GONE);
                                    }
                                }
                            }
                        /*if (!jsonData.equals("[]")){
                            rc_searchprod_overall.setVisibility(View.VISIBLE);
                            tv_empty.setVisibility(View.GONE);
//                            pageIndex++;
                        }else{
                            if (pageIndex==1){
                                rc_searchprod_overall.setVisibility(View.GONE);
                                tv_empty.setVisibility(View.VISIBLE);
                            }else{
                                isending=true;
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
                        }*/
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.showShort(context,"加载超时");
                            }
                        });
            }else{
                return;
            }
        }

    }

    private void setData() {
//        if(searchprodAdapter==null){
        searchprodAdapter = new SearchProdAdapter(context,searchProdBeans,showtype,"list");
        rc_searchprod_overall.setAdapter(searchprodAdapter);//recyclerview设置适配器
        searchprodAdapter.setOnItemClickListener(new SearchProdAdapter.OnSearchItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,ProductDetailActivity.class);
                intent.putExtra("prod_id",searchProdBeans.get(position).getID());
                startActivity(intent);
            }
        });
        rc_searchprod_overall.setNestedScrollingEnabled(false);
//        }else{
//            //让适配器刷新数据
//            searchprodAdapter = new SearchProdAdapter(context,searchProdBeans,showtype);
//            searchprodAdapter.notifyDataSetChanged();//
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (mCustomPopWindow!=null){
            mCustomPopWindow.dissmiss();
        }
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_qrcode:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    mDrawerLayout.closeDrawers();
                }else{
                    finish();
                }
                break;
            case R.id.tv_searchprod_screen:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    mDrawerLayout.closeDrawers();
                }else{
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            case R.id.et_search:
                finish();
                break;
            case R.id.rl_menu:
                mCustomPopWindow.showAsDropDown(im_setting,0,0);
                break;
            case R.id.tv_reset://重置
                if (categoryID==0) {
                    categoryIDString = "";
                }
                brandIDString="";
                ed_low_price.setText("");
                ed_height_price.setText("");
                if (brandAdapter!=null){
                    brandAdapter.setShowNumber(true);
                    for (int i = 0; i < brandBeans.size(); i++) {
                        brandBeans.get(i).setIscheck(false);
                    }
                    brandAdapter.notifyDataSetChanged();
                }
                if (categoryAdapter!=null) {
                    categoryAdapter.setShowNumber(true);
                    for (int i = 0; i < categortBeans.size(); i++) {
                        categortBeans.get(i).setIscheck(false);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.tv_submit://提交
                pageIndex=1;
                isending=false;
                searchProdBeans.clear();
                searchprodAdapter.notifyDataSetChanged();
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    mDrawerLayout.closeDrawers();
                }
                doSth(sortall);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                mDrawerLayout.closeDrawers();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void doCheckChangeItem(String type,int index, boolean isCheck) {

        if (type.equals("Brand")){
            boolean brand_check=false;
            for (int i = 0; i < brandBeans.size(); i++) {
                if (brandBeans.get(i).ischeck()){
                    brand_check=true;
                    break;
                }

            }
            if (brand_check){
                brandIDString=String.valueOf(brandBeans.get(chooseItem).getID());
                cb_brand_choose.setText(brandBeans.get(chooseItem).getName());
                cb_brand_choose.setTextColor(getResources().getColor(R.color.saixuan_click));
            }else{
                brandIDString="";
                cb_brand_choose.setText("全部");
                cb_brand_choose.setTextColor(getResources().getColor(R.color.umeng_text_color));
            }
        }else{
            boolean categroy_check=false;
            for (int i = 0; i < categortBeans.size(); i++) {
                if (categortBeans.get(i).ischeck()){
                    categroy_check=true;
                    break;
                }
            }
            if (categroy_check) {
                categoryIDString=String.valueOf(categortBeans.get(chooseItem).getID());
                cb_kind_choose.setText(categortBeans.get(chooseItem).getName());
                cb_kind_choose.setTextColor(getResources().getColor(R.color.saixuan_click));
            }else{
                categoryIDString="";
                cb_kind_choose.setText("全部");
                cb_kind_choose.setTextColor(getResources().getColor(R.color.umeng_text_color));
            }
        }
    }
}
