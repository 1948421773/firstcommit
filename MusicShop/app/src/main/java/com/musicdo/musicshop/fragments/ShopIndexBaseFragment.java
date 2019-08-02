package com.musicdo.musicshop.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.musicdo.musicshop.activity.ShopCatetoryDetailActivity;
import com.musicdo.musicshop.adapter.SearchProdAdapter;
import com.musicdo.musicshop.bean.SearchProdBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.MyRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**店铺分类--跳转商品列表Fragment
 * Created by Yuedu on 2017/11/17.
 */

public class ShopIndexBaseFragment extends Fragment implements View.OnClickListener {
    View rootView;
    RadioGroup rg_searchtab;
    RadioButton rb_shop_price;
    private CheckBox cb_shoplist_chekbox;
    private ShopCatetoryDetailActivity activity;
    private int OrderType=0;
    private int ShopID,categroyId,brandId;
    private ImageView iv_price_state;
    private boolean price_state=false;
    int pageIndex=1;
    ArrayList<SearchProdBean> searchProdBeans=new ArrayList<>();
    private boolean showtype=false;//recyclelistview显示布局切换
    SearchProdAdapter searchprodAdapter;
    GridLayoutManager grid_tend_layoutManage;
    private RecyclerView rc_searchprod_overall;
    private TextView tv_empty;
    LoadingDialog dialog;
    private Context mContext;
    private String Keyword="";
    private int lastVisibleItem;
    private boolean isending=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        ShopID=getArguments().getInt("ShopID");
        categroyId=getArguments().getInt("categroyId");
        brandId=getArguments().getInt("brandId");
        Keyword=getArguments().getString("Keyword");
        if (Keyword==null){
            Keyword="";
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shopindex, null);
        initView();
        if (searchProdBeans==null){
            doSth(OrderType);
        }else{
            if (searchProdBeans.size()==0){
                doSth(OrderType);
            }
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ShopCatetoryDetailActivity) context;
    }
    public View initView() {
        dialog = new LoadingDialog(mContext,R.style.LoadingDialog);
        iv_price_state=(ImageView) rootView.findViewById(R.id.iv_price_state);
        rb_shop_price=(RadioButton) rootView.findViewById(R.id.rb_shop_price);
        iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
        tv_empty=(TextView) rootView.findViewById(R.id.tv_empty);
        tv_empty.setText("该店铺没有商品");
        rc_searchprod_overall = (RecyclerView) rootView.findViewById(R.id.rc_searchprod_overall);
//        rc_searchprod_overall.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE)
//                {
//                    Picasso.with(mContext).resumeTag("list");
//                }
//                else
//                {
//                    Picasso.with(mContext).pauseTag("list");
//                }
//            }
//        });
        grid_tend_layoutManage=new GridLayoutManager(activity, 2);
        rc_searchprod_overall.setLayoutManager( grid_tend_layoutManage);


        rc_searchprod_overall.setLayoutManager(grid_tend_layoutManage);

        rc_searchprod_overall.setNestedScrollingEnabled(false);

        rc_searchprod_overall.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (searchProdBeans!=null){
                    if(searchProdBeans.size()!=0){
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchprodAdapter.getItemCount()) {
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
            }
        });

            rg_searchtab=(RadioGroup)rootView.findViewById(R.id.rg_searchtab);
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
                            isending=false;
                            pageIndex=1;
                            doSth(OrderType);
                            break;
                        case R.id.rb_shop_newprod: //新品
                            iv_price_state.setBackground(getResources().getDrawable(R.mipmap.searchprod_price_enable));
                            price_state=false;
                            OrderType=2;
                            isending=false;
                            pageIndex=1;
                            doSth(OrderType);
                            break;
                        case R.id.rb_shop_price: //价格
                            isending=false;
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
        cb_shoplist_chekbox = (CheckBox) rootView.findViewById(R.id.cb_shoplist_chekbox);
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
//                Picasso.with(mContext).pauseTag("list");
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
        return rootView;
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
//                Picasso.with(mContext).resumeTag("list");
            }
        },200);
    }
    private void doSth(int OrderType) {
//        if (sort!=0){
//            sortall=sort;
//        }
        if (isending){
            return;
        }
        String keyOrcategory="";
        String keyOrcategoryString="";
//        if (categoryID!=0){
//            keyOrcategory=String.valueOf(categoryID);
//            keyOrcategoryString="categoryID";
//        }else {
//            keyOrcategory=key;
//            keyOrcategoryString="keyword";
//        }

        OkHttpUtils.post(AppConstants.GetShopProductSearch)
                .tag(this)
                .params("ShopID",ShopID)
                .params("ShopCategoryID",categroyId)
                .params("BrandID",brandId)
                .params("Keyword",Keyword)
                .params("PageIndex",pageIndex)
                .params("OrderType",OrderType)
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
//                        Picasso.with(mContext).resumeTag("list");
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
                            if (!jsonData.equals("[]")){
                                isending=false;
                                rc_searchprod_overall.setVisibility(View.VISIBLE);
                                tv_empty.setVisibility(View.GONE);
//                            pageIndex++;
                            }else{
                                if (pageIndex==1){
                                    rc_searchprod_overall.setVisibility(View.GONE);
                                    tv_empty.setVisibility(View.VISIBLE);
                                }else{
                                    isending=true;
                                    rc_searchprod_overall.setVisibility(View.VISIBLE);
                                    tv_empty.setVisibility(View.GONE);
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
                        }else{
                            isending=true;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!dialog.isShowing()){
                            dialog.show();

                        }
//                        Picasso.with(mContext).pauseTag("list");
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
//                        Picasso.with(mContext).resumeTag("list");
                    }
                });
    }

    private void setData() {
//        if(searchprodAdapter==null){
        searchprodAdapter = new SearchProdAdapter(mContext,searchProdBeans,showtype,"list");
        rc_searchprod_overall.setAdapter(searchprodAdapter);//recyclerview设置适配器
        searchprodAdapter.setOnItemClickListener(new SearchProdAdapter.OnSearchItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                            Toast.makeText(mContext, "点击了"+position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,ProductDetailActivity.class);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog!=null){
            if (dialog.isIndeterminate()){
                dialog.dismiss();
            }
        }
    }
}
