package com.musicdo.musicshop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylibrary.PtrClassicFrameLayout;
import com.example.mylibrary.PtrDefaultHandler;
import com.example.mylibrary.PtrFrameLayout;
import com.example.mylibrary.PtrHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.ShopcartActivityAdapter;
import com.musicdo.musicshop.adapter.ShopcartActivityAdapter;
import com.musicdo.musicshop.adapter.ShopcartExpandableListViewAdapter;
import com.musicdo.musicshop.bean.CartBean;
import com.musicdo.musicshop.bean.CartItemBean;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.NestedExpandaleListView;
import com.musicdo.musicshop.view.RefreshCompleteCallBack;
import com.musicdo.musicshop.view.SExpandableListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 商品详情中的购物车
 * Created by Yuedu on 2017/11/2.
 */

public class ShoppingCartActivity extends BaseActivity implements ShopcartExpandableListViewAdapter.CheckInterface,ShopcartExpandableListViewAdapter.ModifyCountInterface, View.OnClickListener,ShopcartExpandableListViewAdapter.DeleteInterface{
    public MainActivity activity;
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private final static String TAG = "ShoppingCartFragment";
    private SExpandableListView exListView;
    private CheckBox cb_check_all;
    private TextView tv_total_price,tv_add_keep,tv_cart_calculate,tv_cart_complete_edit,tv_empty;
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量
    private int PageIndex = 1;//页数
    private int PageSize = 100;//每页个数
    private boolean allEdit=false;//全部编辑
    private int lastVisibleItem;
    //    private MySwipeRefreshLayout swipeRefreshLayout;
    private ShopcartExpandableListViewAdapter selva;
    //    private List<GroupInfo> groups = new ArrayList<GroupInfo>();// 组元素数据列表
//    private Map<String, List<ProductInfo>> children = new HashMap<String, List<ProductInfo>>();// 子元素数据列表
    private ArrayList<CartBean> CartBeans=new ArrayList<>();
    private ArrayList<CartBean> groupCarts=new ArrayList<>();
    private ArrayList<CartItemBean> childCarts=new ArrayList<>();
    private ArrayList<SpecItemBean> specItemBeans=new ArrayList<>();
    private boolean isReflash=false;
    private boolean isInfoSpec=false;
    private int[] infoSpecChangeShopId;//记录编辑状态下某个店铺的iD,刷新数据后重新设置成编辑状态
    LoadingDialog dialog;
    private Context context;
    private LinearLayout ll_back;
    private boolean isPull;
    private boolean isEnd;
    String loginData="";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isEnd) {
                if (exListView!=null) {
                    exListView.refreshComplete();
                    exListView.setNoMore(true);
                }
            } else {
                addLoadMoreData();
                if (isPull) {
                    if (exListView!=null) {
                        exListView.refreshComplete();
                    }
                }
                if (selva!=null)
                    selva.notifyDataSetChanged();
//                expanedAll();
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppingcart);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
        initData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void initView() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        exListView = (SExpandableListView) findViewById(R.id.exListView);
        cb_check_all = (CheckBox) findViewById(R.id.all_chekbox);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_add_keep = (TextView) findViewById(R.id.tv_add_keep);
        tv_cart_calculate = (TextView)findViewById(R.id.tv_cart_calculate);
        tv_empty = (TextView)findViewById(R.id.tv_empty);
        tv_cart_complete_edit = (TextView) findViewById(R.id.tv_cart_complete_edit);
        tv_cart_complete_edit.setOnClickListener(this);
        /*exListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean isChangePage=false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(CartBeans.size()!=0){
                    int aaa=selva.getGroupCount();
                    if (scrollState == RecyclerView.SCROLL_STATE_IDLE && isChangePage) {
                        int bbb=PageIndex;
                        getData();
                        isChangePage=false;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastVisibleItem = firstVisibleItem+visibleItemCount;
                if (lastVisibleItem==totalItemCount&&totalItemCount!=0){//此处bug，不满一屏幕的时候会自动加载下一页数据，即会执行加载更多，可用加载更多插件
                    isChangePage=true;
                }else{
                    isChangePage=false;
                }
            }
        });*/
    }


    @Override
    public void onResume() {
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

        allEdit=false;
//        String[] Cace=activity.getGuid();
        String[] Cace=null;
        String Guid="";
        String Propertys="";
        String PropertysText="";
        double price=0.0;
        int ProductID=0;
        int Count=0;
        if (getIntent().getStringExtra("changeShoppingCart")!=null) {//购物车编辑参数回传宿主MainActivity，购物车Fragment重新显示，执行getGuid()
            if (getIntent().getStringExtra("changeShoppingCart").equals("changeShoppingCart")) {
                if (getIntent().getStringExtra("GUID")!=null){
                    Guid=getIntent().getStringExtra("GUID");
                }
                if (getIntent().getStringExtra("Propertys")!=null){
                    Propertys=getIntent().getStringExtra("Propertys");
                }
                if (getIntent().getStringExtra("PropertysText")!=null){
                    PropertysText=getIntent().getStringExtra("PropertysText");
                }
                if (getIntent().getDoubleExtra("price",0.0)!=0.0){
                    price=getIntent().getDoubleExtra("price",0.0);
                }
                if (getIntent().getIntExtra("Count",0)!=0){
                    Count=getIntent().getIntExtra("Count",0);
                }
            }
            PageIndex=1;
            GetEditCartData(Guid,Propertys,PropertysText,price,ProductID,Count);
        }else{
            if (CartBeans==null||CartBeans.size()==0){
                getData();
            }else{
                if (!isInfoSpec){
                    SignEditItem();
                    CartBeans.clear();
                    groupCarts.clear();
                    childCarts.clear();
                    PageIndex=1;
                    getData();
                }else{
                    isInfoSpec=false;
                    selva.notifyDataSetChanged();
                }
            }
        }


        allEdit=false;
        tv_cart_complete_edit.setText(R.string.shoppingcart_edit);
        tv_add_keep.setVisibility(View.GONE);
        tv_cart_calculate.setText("结算");
    }


    /**
     * 模拟加载更多数据
     */
    private void addLoadMoreData() {
        CartBeans.clear();
//        groupCarts.clear();
        childCarts.clear();
//        selva=null;
        allEdit=false;
        getData();
    }
    public void initData() {

    }

    private void getData() {
        OkHttpUtils.post(AppConstants.GetCart)
                .tag(this)
                .params("UserName",AppConstants.USERNAME)//固定地方便调试，其他ID没数据"26013"
                .params("PageIndex",PageIndex)
                .params("PageSize",PageSize)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        /*if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }*/
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
//                        mPtrFrame.refreshComplete();
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        boolean Flag=false;
                        String comment=null;
                        String Message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            Flag = jsonObject.getBoolean("Flag");
                            Message = jsonObject.getString("Message");
                            jsonData = jsonObject.getString("ReturnData");
//                            jsonData = jsonObject.getString("ReturnDataPre");
                            if (!Flag){
                                isEnd=true;
                                exListView.setNoMore(true);
                                if (PageIndex==1){
                                    groupCarts.clear();
                                    exListView.setVisibility(View.GONE);
                                    tv_empty.setVisibility(View.VISIBLE);
                                    tv_empty.setText("购物车为空");
                                    infoSpecChangeShopId=null;
                                    tv_cart_complete_edit.setText(R.string.shoppingcart_edit);
                                    tv_add_keep.setVisibility(View.GONE);
                                    tv_cart_calculate.setText("结算");
                                    calculate();
                                }
                                return;
                            }else{

                                exListView.setVisibility(View.VISIBLE);
                                tv_empty.setVisibility(View.GONE);
                                if(jsonData!=null) {
                                    if (!jsonData.equals("[]")) {
                                        PageIndex++;
                                        isEnd=false;
                                        exListView.setNoMore(false);
                                    } else {
                                        if (PageIndex==1) {
                                            isEnd = true;
                                            exListView.setNoMore(true);
                                            exListView.setVisibility(View.GONE);
                                            tv_empty.setVisibility(View.VISIBLE);
                                            tv_empty.setText("购物车为空");
                                            calculate();
                                            if (selva != null)
                                                selva.notifyDataSetChanged();
                                        }else{
                                            isEnd=true;
                                            isPull = false;
                                            Message msg = handler.obtainMessage();
                                            handler.sendMessageDelayed(msg, 2000);
                                        }
                                        return;
                                    }
                                }else{
                                    isEnd=true;
                                    exListView.setNoMore(true);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (PageIndex<=2){
                            CartBeans.clear();

                            childCarts.clear();
                            CartBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<CartBean>>(){}.getType());
                            setfootLoadMoreState(CartBeans);
                            ArrayList<CartBean> groupCartCaces=new ArrayList<>();
                            for (int i = 0; i < CartBeans.size(); i++)
                            {
                                CartBean cb=new CartBean();
                                cb.setShopName(CartBeans.get(i).getShopName());
                                cb.setShopID(CartBeans.get(i).getShopID());
                                cb.setProductDetail(CartBeans.get(i).getProductDetail());
                                groupCartCaces.add(cb);
                            }
                            groupCarts.clear();
                            groupCarts.addAll(groupCartCaces);
                            setData();
                        }else{
                            groupCarts.clear();
                            ArrayList<CartBean> groupCartCaces=new ArrayList<>();
                            List<CartBean> more= gson.fromJson(jsonData, new TypeToken<List<CartBean>>() {}.getType());
                            setfootLoadMoreState(more);
                            boolean isexist=false;
                            for (CartBean mores:more){
                                for (CartBean CBeans:CartBeans){
                                    if (CBeans.getShopID()==mores.getShopID()){
                                        ArrayList<CartItemBean> childCaces=new ArrayList<>();
                                        childCaces.addAll(mores.getProductDetail());
                                        childCaces.addAll(CBeans.getProductDetail());
                                        CBeans.setProductDetail(childCaces);
                                        isexist=true;
                                    }
                                }
                            }
                            if (!isexist){
                                CartBeans.addAll(more);
                            }
                            for (int i = 0; i < CartBeans.size(); i++)
                            {
                                CartBean cb=new CartBean();
                                cb.setShopName(CartBeans.get(i).getShopName());
                                cb.setShopID(CartBeans.get(i).getShopID());
                                cb.setProductDetail(CartBeans.get(i).getProductDetail());
                                groupCartCaces.add(cb);
                            }
                            groupCarts.addAll(groupCartCaces);
//                            childCarts.addAll(childCartCaces);
                            setData();
                        }


//                        ProductDetailBeans= gson.fromJson(jsonData, new TypeToken<List<ProductDetailBean>>() {}.getType());
                       /* commentBeans= gson.fromJson(comment, CommentBean.class);
                        data.add(commentBeans);
                        getShopData();*/
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
                        if(!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");
                        exListView.setVisibility(View.GONE);
                        tv_empty.setVisibility(View.VISIBLE);
                        tv_empty.setText("加载超时");
//                        mPtrFrame.refreshComplete();
                        /*if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }*/
                        if(dialog.isShowing()){
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        isEnd=true;
                        exListView.setNoMore(true);
                    }
                });
    }

    /**
     * 当每页加载小于六条则到底了，修改底部状态
     * @param CartBeans
     */
    private void setfootLoadMoreState(List<CartBean> CartBeans) {
        if(CartBeans!=null){
            int i=0;
            for (int j = 0; j <CartBeans.size() ; j++) {
                if (CartBeans.get(j).getProductDetail()!=null){
                    i=i+CartBeans.get(j).getProductDetail().size();
                }
            }
            if (i<AppConstants.ORDER_PAGESIZE){
                exListView.setNoMore(true);
            }
        }
    }

    /**
     * 购物车编辑商品规格
     * @param Guid
     * @param Propertys
     * @param PropertysText
     * @param price
     * @param ProductID
     * @param Count
     */

    private void GetEditCartData(String Guid,String Propertys,String PropertysText,double price,int ProductID,int Count) {
        if (Guid==null) {
            ToastUtil.showShort(context,"数据加载超时");
            return;
        }
        OkHttpUtils.post(AppConstants.EditCart)
                .tag(this)
                .params("GUID",Guid)//ProductID固定地方便调试，其他ID没数据
                .params("UserName",AppConstants.USERNAME)//ProductID固定地方便调试，其他ID没数据
                .params("Count",Count)
                .params("Propertys",Propertys)
                .params("PropertysText",PropertysText)
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
                        SignEditItem();
                        getData();
                       /* ToastUtil.showShort(context,Message);
                        //评论Data不是array
                        specBeans= gson.fromJson(s, SpecBean.class);
                        SetDate();*/
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
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");

                    }
                });
    }

    /**
     * 标记编辑状态
     */
    public void SignEditItem(){
        infoSpecChangeShopId=new int[groupCarts.size()];
        for (int l = 0; l < groupCarts.size(); l++)//记录某个店铺的编辑状态
        {
            if (groupCarts.get(l)!=null){
                if (groupCarts.get(l).isEdited()){
                    infoSpecChangeShopId[l]=groupCarts.get(l).getShopID();
                }
            }
        }
    }

    private void GetDeleteData(String GUIDs) {
        if (GUIDs==null&&GUIDs.length()!=0) {
            ToastUtil.showShort(context,"数据加载超时");
            return;
        }
        OkHttpUtils.post(AppConstants.DeleteCart)
                .tag(this)
                .params("GUIDs",GUIDs)//ProductID固定地方便调试，其他ID没数据
                .params("UserName",AppConstants.USERNAME)//ProductID固定地方便调试，其他ID没数据
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
                        /*PageIndex=1;
                        getData();*/
                        selva.notifyDataSetChanged();
                        calculate();
                       /* ToastUtil.showShort(context,Message);
                        //评论Data不是array
                        specBeans= gson.fromJson(s, SpecBean.class);
                        SetDate();*/
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
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");

                    }
                });
    }

    private void setData() {
        if (selva==null) {
            selva = new ShopcartExpandableListViewAdapter(groupCarts, childCarts, context, specItemBeans);
            selva.setCheckInterface(this);// 关键步骤1,设置复选框接口
            selva.setModifyCountInterface(this);// 关键步骤2,设置数量增减接口
            selva.setDeteInterface(this);

            exListView.setLoadingMoreEnabled(true);
            exListView.setPullRefreshEnabled(true);
            exListView.setAdapter(selva);
            for (int i = 0; i < selva.getGroupCount(); i++) {
                exListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
            }
            exListView.setmLoadingListener(new SExpandableListView.LoadingListener() {
                @Override
                public void onLoadMore() {
                    isPull = false;
                    Message msg = handler.obtainMessage();
                    handler.sendMessageDelayed(msg, 2000);
                }

                @Override
                public void onRefresh() {
                    isPull = true;
                    Message msg = handler.obtainMessage();
                    handler.sendMessageDelayed(msg, 2000);
                }
            });

            cb_check_all.setOnClickListener(this);
            tv_add_keep.setOnClickListener(this);
            tv_cart_calculate.setOnClickListener(this);
        }else{
            for (int i = 0; i < selva.getGroupCount(); i++){
                exListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
            }
//            selva.notifyDataSetChanged();//
        }
        if (infoSpecChangeShopId!=null){//当编辑商品参数时候才设置
            for (int i = 0; i < groupCarts.size(); i++) {
                if (infoSpecChangeShopId[i]==groupCarts.get(i).getShopID()){
                    groupCarts.get(i).setEdited(true);
                    for (int j = 0; j <groupCarts.get(i).getProductDetail().size() ; j++) {
                        for(CartItemBean cib:groupCarts.get(i).getProductDetail()){
                            cib.setEdited(true);
                        }
                    }
                }
            }

            boolean isallchoose=true;//只要有一个店铺不是AllEdit状态就不执行，编辑全部才执行
            for (int i = 0; i < infoSpecChangeShopId.length; i++) {//普通for
                if (infoSpecChangeShopId[i]==0){
                    isallchoose=false;
                }
            }
            if (isallchoose){
                allEdit=false;
                changeAllEditState();
            }

        }
        infoSpecChangeShopId=null;
        calculate();
        selva.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v)
    {
        if (!AppConstants.ISLOGIN){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return;
        }
        switch (v.getId())
        {
            case R.id.all_chekbox:
                doCheckAll();
                break;
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_cart_complete_edit:
                changeAllEditState();
                break;
            case R.id.tv_cart_calculate:{//删除或者结算
                if(allEdit){//删除
                    if (totalCount == 0)
                    {
                        Toast.makeText(context, "请您选择商品", Toast.LENGTH_LONG).show();
                        return;
                    }
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
                    builder.setMessage("提醒");
                    builder.setTitle("确定删除购物车？");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            allEdit=true;//全部编辑状态下删除，刷新是重置状态
                            doDelete();
                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                }else {//结算,提交订单
                    if (totalCount == 0)
                    {
                        Toast.makeText(context, "请您选择商品", Toast.LENGTH_LONG).show();
                        return;
                    }
                    StringBuffer sb=new StringBuffer("");
                    for (int i = 0; i < groupCarts.size(); i++)
                    {
                        for (int j = 0; j < groupCarts.get(i).getProductDetail().size(); j++)
                        {
                            if (groupCarts.get(i).getProductDetail().get(j).isChoosed())
                            {
                                sb.append(groupCarts.get(i).getProductDetail().get(j).getGUID()+",");
                            }
                        }
                    }
                    String paramString=sb.toString();
                    paramString=paramString.substring(0,paramString.length()-1);
                    if (!sb.toString().equals("")){//购物车选择商品后跳转确认订单界面
                        Intent intent=new Intent(context,SubmitOrderActivity.class);
                        intent.putExtra("Param",paramString);
                        startActivity(intent);
                    }

                }
            }
            break;
            case R.id.tv_add_keep://加入收藏夹
                if (totalCount == 0)
                {
                    Toast.makeText(context, "请选择宝贝", Toast.LENGTH_LONG).show();
                    return;
                }
                StringBuffer sb=new StringBuffer("");
                StringBuffer sbproductID=new StringBuffer("");
                for (int i = 0; i < groupCarts.size(); i++)
                {
                    for (int j = 0; j < groupCarts.get(i).getProductDetail().size(); j++)
                    {
                        if (groupCarts.get(i).getProductDetail().get(j).isChoosed())
                        {
                            sb.append(groupCarts.get(i).getProductDetail().get(j).getID()+",");
                            sbproductID.append(groupCarts.get(i).getProductDetail().get(j).getProductID()+",");
                        }
                    }
                }
                MoveToUserCollect(sb.substring(0, sb.length() - 1),sbproductID.substring(0, sbproductID.length() - 1));
                break;
        }
    }


    private void MoveToUserCollect(String IDs,String ProductIDs) {
        OkHttpUtils.post(AppConstants.MoveToUserCollect)
                .tag(this)
                .params("UserID",AppConstants.USERID)
                .params("IDs",IDs)
                .params("ProductIDs",ProductIDs)
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
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        boolean Flag=false;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag){
                            ToastUtil.showShort(context,Message);
                            PageIndex=1;
                            SignEditItem();
                            getData();
                        }
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
                        ToastUtil.showShort(context,"数据加载超时");
                    }
                });
    }

    private void changeAllEditState() {
        if (!allEdit){
            allEdit=true;
            tv_cart_complete_edit.setText(R.string.shoppingcart_complete_edit);
            tv_add_keep.setVisibility(View.VISIBLE);//移入收藏夹控制隐藏和显示
            tv_cart_calculate.setText("删除");
        }else{
            allEdit=false;
            tv_cart_complete_edit.setText(R.string.shoppingcart_all_edit);
            tv_add_keep.setVisibility(View.GONE);
            tv_cart_calculate.setText("结算");
        }
        if (groupCarts!=null){
            if (groupCarts.size()!=0){

                for (int i = 0; i < groupCarts.size(); i++) {
                    groupCarts.get(i).setEdited(allEdit);
                    groupCarts.get(i).setAllEdited(allEdit);
                    for (int j = 0; j <groupCarts.get(i).getProductDetail().size() ; j++) {
                        for(CartItemBean cib:groupCarts.get(i).getProductDetail()){
                            cib.setEdited(allEdit);
                        }
                    }
                }
                selva.notifyDataSetChanged();
                calculate();
            }else{
                calculate();
            }
        }
    }

    /**
     * 删除操作<br>
     * 1.不要边遍历边删除，容易出现数组越界的情况<br>
     * 2.现将要删除的对象放进相应的列表容器中，待遍历完后，以removeAll的方式进行删除
     */
    protected void doDelete(){//莫名其妙的bug，删除之后,在最后一个店铺下面最后一个商品会重复多出
        String allGuid="";
        List<CartBean> toBeDeleteGroups =new ArrayList<>();// 待删除的组元素列表
        for (int i = 0; i < groupCarts.size(); i++)
        {
            CartBean group = groupCarts.get(i);
            if (group.isChoosed())
            {
                toBeDeleteGroups.add(group);
            }
            List<CartItemBean> toBeDeleteProducts = new ArrayList<CartItemBean>();// 待删除的子元素列表
            List<CartItemBean> childProducts =group.getProductDetail();// 待删除的子元素列表

            for (int j = 0; j < childProducts.size(); j++)
            {
                if (childProducts.get(j).isChoosed())
                {
                    toBeDeleteProducts.add(childProducts.get(j));
                    allGuid=allGuid+childProducts.get(j).getGUID()+",";
                }
            }

            childProducts.removeAll(toBeDeleteProducts);

        }
        int[] groupShopid=new int[groupCarts.size()];//记录需要删除没有任何产品的店铺，
        ArrayList<CartBean> deleteGroupShops=new ArrayList<>();
        deleteGroupShops.addAll(groupCarts);
        boolean isAllchose=true;
        for (int l = 0; l < groupCarts.size(); l++)
        {
            if (groupCarts.get(l)!=null){
                if (groupCarts.get(l).getProductDetail().size()==0){
                    groupShopid[l]=groupCarts.get(l).getShopID();
//                    groupCarts.remove(l);
                }
                if (!groupCarts.get(l).isChoosed()){
                    isAllchose=false;
                }
            }
        }
        /*Iterator<CartBean> iter = groupCarts.iterator();
        while (iter.hasNext()) {
            int l = 0;
            if (iter.next().getShopID() == groupShopid[l]) {
                iter.remove();
            }
            l++;
        }*/
        if (isAllchose){
            groupCarts.clear();
        }else{
            for (int i = deleteGroupShops.size() - 1; i >= 0; i--) {
                if(deleteGroupShops.get(i).getProductDetail().size() == 0){
                    deleteGroupShops.remove(i);
                }else if(deleteGroupShops.get(i).getProductDetail()==null){
                    deleteGroupShops.remove(i);
                }
                selva.notifyDataSetChanged();
            }
            groupCarts.clear();
            groupCarts.addAll(deleteGroupShops);
            selva=null;
            selva = new ShopcartExpandableListViewAdapter(groupCarts, childCarts, context, specItemBeans);
            selva.setCheckInterface(this);// 关键步骤1,设置复选框接口
            selva.setModifyCountInterface(this);// 关键步骤2,设置数量增减接口
            selva.setDeteInterface(this);
            exListView.setAdapter(selva);
            for (int i = 0; i < selva.getGroupCount(); i++) {
                exListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
            }
            selva.notifyDataSetChanged();
        }

        /*for(Iterator<CartBean> it = groupCarts.iterator(); it.hasNext();){
            int l = 0;
            if(it.next().getProductDetail().size()==0){
                groupCarts.remove(it);
            }
            l++;
        }*/
        GetDeleteData(allGuid.substring(0,allGuid.length()-1));
        /*for (int i = 0; i < groupCarts.size(); i++)
        {
            if (!groupCarts.get(i).isChoosed()&&groupCarts.get(i).getProductDetail().size()!=0){
                for (int j = 0; j < groupCarts.get(i).getProductDetail().size(); j++) {
                    if (groupCarts.get(i).getProductDetail().get(j).isChoosed()){
                        groupCarts.get(i).getProductDetail().remove(j);
                    }
                }
            }else{
                groupCarts.remove(i);
            }
        }
        selva.notifyDataSetChanged();
//        calculate();*/
    }

    @Override
    public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked)
    {

        CartItemBean product = (CartItemBean) selva.getChild(groupPosition, childPosition);
        int currentCount = product.getCount();
        currentCount++;
        product.setCount(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        GetEditCartData(product.getGUID(),product.getPropertys(),product.getPropertysText(),product.getMarketPrice(),product.getProductID(),product.getCount());
        selva.notifyDataSetChanged();
        calculate();
    }



    @Override
    public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked)
    {

        CartItemBean product = (CartItemBean) selva.getChild(groupPosition, childPosition);
        int currentCount = product.getCount();
        if (currentCount == 1)
            return;
        currentCount--;
        product.setCount(currentCount);
        ((TextView) showCountView).setText(currentCount + "");

        GetEditCartData(product.getGUID(),product.getPropertys(),product.getPropertysText(),product.getMarketPrice(),product.getProductID(),product.getCount());
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {

        for (int j = 0; j < groupCarts.get(groupPosition).getProductDetail().size(); j++){
            if (groupCarts.get(groupPosition).getShopID()==groupCarts.get(groupPosition).getProductDetail().get(j).getShopID()){
                groupCarts.get(groupPosition).setChoosed(isChecked);
//                    childCarts.get(j).setChoosed(isChecked);
                groupCarts.get(groupPosition).getProductDetail().get(j).setChoosed(isChecked);
            }
        }
        if (isAllCheck()) {
            cb_check_all.setChecked(true);
//            tv_calculate.setBackgroundColor(getResources().getColor(R.color.prodetail_buynow_bg));
//            tv_add_keep.setBackgroundColor(getResources().getColor(R.color.prodetail_add_shoppingcart_bg));
        }else{
            cb_check_all.setChecked(false);
//            tv_calculate.setBackgroundColor(getResources().getColor(R.color.prodetail_freight));
//            tv_add_keep.setBackgroundColor(getResources().getColor(R.color.shoppingcart_unaccount));
        }
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void checkChild(int groupPosition, int childPosiTion, boolean isChecked)
    {
        boolean allChildSameState = true;// 判断改组下面的所有子元素是否是同一种状态
        CartBean group = groupCarts.get(groupPosition);
        List<CartItemBean> childs = group.getProductDetail();
        for (int i = 0; i < groupCarts.get(groupPosition).getProductDetail().size(); i++)
        {
            if (groupCarts.get(groupPosition).getProductDetail().get(i).isChoosed() != isChecked){
                allChildSameState = false;
                break;
            }else{

            }
        }
        if (allChildSameState)
        {
            group.setChoosed(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
        } else
        {
            group.setChoosed(false);// 否则，组元素一律设置为未选中状态
        }

        if (isAllCheck()) {
            cb_check_all.setChecked(true);
//            tv_calculate.setBackgroundColor(getResources().getColor(R.color.prodetail_buynow_bg));
//            tv_add_keep.setBackgroundColor(getResources().getColor(R.color.prodetail_add_shoppingcart_bg));
        }else{
            cb_check_all.setChecked(false);
//            tv_calculate.setBackgroundColor(getResources().getColor(R.color.prodetail_freight));
//            tv_add_keep.setBackgroundColor(getResources().getColor(R.color.shoppingcart_unaccount));
        }
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void editGroup(int groupPosition, boolean isEdited) {
        CartBean group = groupCarts.get(groupPosition);
        List<CartItemBean> childs = group.getProductDetail();
        for (int i = 0; i < childs.size(); i++)
        {
            childs.get(i).setEdited(isEdited);
        }

        if (groupCarts.get(groupPosition).isAllEdited()){
            allEdit=true;
            changeAllEditState();
        }
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void editChild(int groupPosition, int childPosition, boolean isChecked) {

    }

    private boolean isAllCheck()
    {

        for (CartBean group : groupCarts)
        {
            if (!group.isChoosed())
                return false;

        }

        return true;
    }

    /** 全选与反选 */
    private void doCheckAll()
    {
        for (int i = 0; i < groupCarts.size(); i++)
        {
            groupCarts.get(i).setChoosed(cb_check_all.isChecked());
            CartBean group = groupCarts.get(i);
            List<CartItemBean> childs = group.getProductDetail();
            for (int j = 0; j < childs.size(); j++)
            {
                childs.get(j).setChoosed(cb_check_all.isChecked());
            }
        }
        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 统计操作<br>
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
     * 3.给底部的textView进行数据填充
     */
    public void calculate()
    {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < groupCarts.size(); i++)
        {
            CartBean group = groupCarts.get(i);
            List<CartItemBean> childs = group.getProductDetail();
            for (int j = 0; j < childs.size(); j++)
            {
                CartItemBean product = childs.get(j);
                if (product.isChoosed())
                {
                    totalCount++;
                    totalPrice += product.getMemberPrice() * product.getCount();
                }
            }
        }
        tv_total_price.setText(getResources().getString(R.string.pricesymbol) + SpUtils.doubleToString(totalPrice));
        if (!allEdit) {
            tv_cart_calculate.setText(getString(R.string.shoppingcart_calculate) + "(" + totalCount + ")");
        }else{
            tv_cart_calculate.setText("删除");
        }
        if (totalCount!=0) {
            tv_cart_calculate.setBackgroundColor(getResources().getColor(R.color.prodetail_buynow_bg));
            tv_add_keep.setBackgroundColor(getResources().getColor(R.color.prodetail_add_shoppingcart_bg));
        }else{
            cb_check_all.setChecked(false);//全选不打勾，全部状态重置清空
            tv_cart_calculate.setBackgroundColor(getResources().getColor(R.color.prodetail_freight));
            tv_add_keep.setBackgroundColor(getResources().getColor(R.color.shoppingcart_unaccount));
        }
    }

    @Override
    public void delete(final int groupPosition, final int childPosition, boolean isChecked) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("提醒");
        builder.setTitle("确定删除这个宝贝？");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String Guid="";

                CartBean group = groupCarts.get(groupPosition);
                List<CartItemBean> childs = group.getProductDetail();
                for (int i = 0; i < childs.size(); i++){
                    if (i==childPosition){
                        Guid=childs.get(i).getGUID();
                        childs.remove(i);
                        break;
                    }
                }
                if (group.getProductDetail().size()==0){
//                        groupCarts.clear();
                    groupCarts.remove(groupPosition);
                    PageIndex=1;

                }

                selva.notifyDataSetChanged();
                GetDeleteData(Guid);

            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
