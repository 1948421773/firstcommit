package com.musicdo.musicshop.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gxz.PagerSlidingTabStrip;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.BitmapCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AddressManagerActivity;
import com.musicdo.musicshop.activity.GoodsInfoSpecActivity;
import com.musicdo.musicshop.activity.GoodsInfoTrueSpecActivity;
import com.musicdo.musicshop.activity.ImageShowActivity;
import com.musicdo.musicshop.activity.LoginActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.ShopIndexBaseActivity;
import com.musicdo.musicshop.adapter.AssessmentAdapter;
import com.musicdo.musicshop.adapter.CommentImgAdapter;
import com.musicdo.musicshop.adapter.NetworkImageHolderView;
import com.musicdo.musicshop.bean.AddressBean;
import com.musicdo.musicshop.bean.CommentBean;
import com.musicdo.musicshop.bean.CommentImg;
import com.musicdo.musicshop.bean.CommentImgList;
import com.musicdo.musicshop.bean.ProCommentBean;
import com.musicdo.musicshop.bean.ProductDetailBean;
import com.musicdo.musicshop.bean.RecommendGoodsBean;
import com.musicdo.musicshop.bean.ShopDetailBean;
import com.musicdo.musicshop.bean.SpecBean;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.bean.SpecitemDataBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.Banner;
import com.musicdo.musicshop.view.SlideDetailsLayout;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 商品详情-商品
 * item页ViewPager里的商品Fragment
 */
public class GoodsInfoFragment extends Fragment implements View.OnClickListener, SlideDetailsLayout.OnSlideDetailsListener,Banner.OnClickMyTextView{
    private static String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    private PagerSlidingTabStrip psts_tabs;
    private SlideDetailsLayout sv_switch;
    private ScrollView sv_goods_info;
    private FloatingActionButton fab_up_slide;
    public ConvenientBanner vp_item_goods_img;
    private LinearLayout ll_goods_detail, ll_goods_config, ll_detail_comment,mLinearLayout;
    private TextView tv_goods_detail, tv_goods_config, tv_prodetail_prod_type;
    private View v_tab_cursor;
    private List<String> commentimglists;//数据
    public FrameLayout fl_content;
    public LinearLayout ll_current_goods, ll_activity, ll_comment, ll_recommend, ll_pull_up;
    public TextView tv_goods_title, tv_new_price, tv_old_price, tv_current_goods,
            tv_comment_count, tv_good_comment, tv_shop_name,
            tv_shop_allprod, tv_shop_newprod, tv_shop_collect,
            tv_prodes, tv_service, tv_logistics, tv_productdetail_currentcity,
            tv_saleCount, tv_into_shop, tv_share;
    View rootView;
    ArrayList<String> imgUrls;
    private View inflate;
    /**
     * 当前商品详情数据页的索引分别是图文详情、规格参数
     */
    private int nowIndex;
    private float fromX;
    private ImageView iv_develop_icon;
    public GoodsConfigFragment goodsConfigFragment;
    public GoodsInfoWebFragment goodsInfoWebFragment;
    private Fragment nowFragment;
    private List<TextView> tabTextList;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    public ProductDetailActivity activity;
    private int prodDetailAddress = 2;//跳转地址管理列表
    private LayoutInflater inflater;
    private LinearLayout ll_specnum, ll_prodetail_parameter;
    private FrameLayout fl_goods_image;
    private FrameLayout fl_goods_image1;
    private RecyclerView rv_product_RecyclerView_list,rv_comment_photos;
    private AssessmentAdapter assessmentAdapter;
    ArrayList<CommentBean> data = new ArrayList<>();
    ProductDetailBean ProductDetailBeans = new ProductDetailBean();
    private SpecBean specBeans = new SpecBean();
    ShopDetailBean shopDetailBeans = new ShopDetailBean();
    CommentBean commentBeans = new CommentBean();
    int productId;
    private Dialog dialog;
    private int SpecOK = 1;
    private int SpecAddCart = 2;//选择规格，加入购物车
    private int SpecSubmitOrder = 3;//选择规格，立即购买
    private TextView iv_isscore, tv_all_comment;
    private String paramString = "";
    private FragmentManager manger;
    private FragmentTransaction ft;
    private OnButtonClick onButtonClick;//2、定义接口成员变量
    private ArrayList<SpecItemBean> specCaceBeans = new ArrayList<>();
    private String Propertyscace = "";
    AddressBean addressBean = new AddressBean();
    private String provider;//位置提供器
    private LocationManager locationManager;//位置服务
    private Location location;
    private ShareAction mShareAction;
    private UMShareListener mShareListener;
    NetworkImageHolderView networkImageHolderView;
    NetworkImageHolderView networkImageHolderView1;
    CBViewHolderCreator cBViewHolderCreator;
    CBViewHolderCreator cBViewHolderCreator1;
    int imgheight, imgwidth;
    String ReturnDataPre=null;
    private Banner banner;
    private Banner banner1;
    private List<String> list=new ArrayList<>();

    // 2.1 定义用来与外部activity交互，获取到宿主activity
    private FragmentInteraction listterner;
    private FragmentaddcartInteraction listaddcartterner;

    protected Context mContext;
    @Override
    public void myTextViewClick(int index) {
        Intent intent=new Intent(activity,ImageShowActivity.class);
        intent.putStringArrayListExtra("imgUrls",imgUrls);
        intent.putExtra("index",index);
        startActivity(intent);
    }

    // 1 定义了所有activity必须实现的接口方法
    public interface FragmentInteraction {
        void process(String paramString, String Propertys, String paramNumber, ArrayList<SpecItemBean> paramlist);
    }

    public interface FragmentaddcartInteraction {
        void addcartprocess(String paramString, String Propertys, String paramNumber, ArrayList<SpecItemBean> paramlist);
    }

    public interface OnButtonClick {
        public void onAllComment(String comment);
    }

    public void setOnButtonClick(OnButtonClick onClick) {
        this.onButtonClick = onClick;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ProductDetailActivity) context;
        if (activity instanceof FragmentInteraction) {
            listterner = (FragmentInteraction) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
        if (activity instanceof FragmentaddcartInteraction) {
            listaddcartterner = (FragmentaddcartInteraction) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    // 开始自动翻页
    @Override
    public void onResume() {//第二次进入商品详情，如果商品详情有数据更新则调用此方法更新Fragment，第二次回来执行onStart，onResume
        super.onResume();

        //开始自动翻页
        if (AppConstants.ScreenHeight == 0 || AppConstants.ScreenHeight == 0) {
            AppConstants.ScreenWidth = ScreenUtil.getScreenWidth(activity);
            AppConstants.ScreenHeight = ScreenUtil.getScreenHeight(activity);
        }
        if (imgUrls != null) {
            if (imgUrls.size() != 1) {
//                vp_item_goods_img.startTurning(4000);
            }
        }

//        jzVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"));
//        if (AppConstants.ISLOGIN){
//            if (addressBean.getAddress()==null){
//                getDefaultAddress();
//            }
//        }


    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
//        if (vp_item_goods_img!=null) {
//            vp_item_goods_img.stopTurning();
//        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ProductDetailBean", ProductDetailBeans);
        outState.putParcelable("specBeans", specBeans);
        outState.putInt("productId", productId);
        outState.putStringArrayList("imgUrls", imgUrls);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        manger = getFragmentManager();
//        if (ProductDetailBeans.getName()==null){
//            ProductDetailBeans=getArguments().getParcelable("DetailBean");
//        }
        specBeans = getArguments().getParcelable("spec");
//        UMShareAPI.get(activity).getPlatformInfo(this, mShareAction, mShareListener);

        mShareListener = new CustomShareListener(activity);
        mShareAction = new ShareAction(activity).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
//                .addButton("umeng_sharebutton_copy", "umeng_sharebutton_copy", "umeng_socialize_copy", "umeng_socialize_copy")
//                .addButton("umeng_sharebutton_copyurl", "umeng_sharebutton_copyurl", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (snsPlatform.mShowWord.equals("umeng_sharebutton_copy")) {
                            Toast.makeText(activity, "复制文本按钮", Toast.LENGTH_LONG).show();
                        } else if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
                            Toast.makeText(activity, "复制链接按钮", Toast.LENGTH_LONG).show();

                        } else if (share_media == SHARE_MEDIA.SMS) {
                            new ShareAction(activity).withText(ProductDetailBeans.getName() + "请点击链接" + "http://www.musicdo.cn")
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        } else {
                            UMWeb web = new UMWeb(AppConstants.ShareProductUrl + ProductDetailBeans.getID()+"&uid="+AppConstants.USERID);
                            web.setTitle(ProductDetailBeans.getShopName());
                            web.setDescription(ProductDetailBeans.getName());
                            web.setThumb(new UMImage(activity, imgUrls.get(0)));
                            new ShareAction(activity).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        if (productId == 0) {
            productId = getArguments().getInt("ID", 0);
        }
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_goods_info, null);
                GetData();
        }

        return rootView;
    }

    /**
     * 视频图片轮播
     */
    private void startBanner() {
        banner = (Banner) rootView.findViewById(R.id.banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MimeTypeMap.getFileExtensionFromUrl(imgUrls.get(0)).equals("mp4"))
                {
                Intent intent=new Intent(activity,ImageShowActivity.class);
                intent.putStringArrayListExtra("imgUrls",imgUrls);
                intent.putExtra("index",0);
                startActivity(intent);
            }
            }

        });
//        banner.setwh(AppConstants.ScreenWidth,AppConstants.ScreenWidth);
        banner.setDataList(imgUrls,activity);
        banner.setImgDelyed(5000);
        banner.startBanner();
//        banner.startAutoPlay();

        banner1 = (Banner) rootView.findViewById(R.id.banner1);
        banner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MimeTypeMap.getFileExtensionFromUrl(imgUrls.get(0)).equals("mp4"))
                {
                    Intent intent=new Intent(activity,ImageShowActivity.class);
                    intent.putStringArrayListExtra("imgUrls",imgUrls);
                    intent.putExtra("index",0);
                    startActivity(intent);
                }
            }

        });
//        banner.setwh(AppConstants.ScreenWidth,AppConstants.ScreenWidth);
        banner1.setDataList(imgUrls,activity);
        banner1.setImgDelyed(5000);
        banner1.startBanner();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {// Restore the fragment's state here
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            imgUrls = savedInstanceState.getStringArrayList("savedInstanceState");
            productId = savedInstanceState.getInt("productId", 0);
            ProductDetailBeans = savedInstanceState.getParcelable("ProductDetailBeans");
            specBeans = savedInstanceState.getParcelable("specBeans");
//            ToastUtil.showShort(activity,"productId="+productId);
        }
    }

    private  class CustomShareListener implements UMShareListener {

        private WeakReference<ProductDetailActivity> mActivity;

        private CustomShareListener(ProductDetailActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST
                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
//                    Toast.makeText(mActivity.get(), platform + " 分享成功", Toast.LENGTH_SHORT).show();
                    //添加分享插入记录
                    getAddShare();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                ToastUtil.showShort(mActivity.get(), " 分享失败啦");
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            Toast.makeText(mActivity.get(), " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCity() {
        locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);//获得位置服务
        provider = judgeProvider(locationManager);

        if (provider != null) {//有位置提供器的情况
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                getLocation(location);//得到当前经纬度并开启线程去反向地理编码
            } else {
//                ToastUtil.showShort(getActivity(),"获取当前位置失败");
            }
        } else {//不存在位置提供器的情况

        }
    }

    private void getDefaultAddress() {
        OkHttpUtils.post(AppConstants.GetUserAddress_Default)
                .tag(this)
                .params("UserID", AppConstants.USERID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData = null;
                        String Message = null;
                        boolean Flag = false;
                        Gson gson = new Gson();
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!Flag) {
//                            ToastUtil.showShort(context,Message);
                        } else {
                            addressBean = null;
                            addressBean = gson.fromJson(jsonData, AddressBean.class);
                            tv_productdetail_currentcity.setText(addressBean.getAddress());
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(activity, "数据加载超时");
                    }
                });
    }
    /**
     * 添加 https://www.musicdo.cn:444/member/AddShare?username=&userid=&productid=*/
    public void getAddShare() {
//        Toast.makeText(getContext(), AppConstants.USERNAME+"--"+AppConstants.USERID+"--"+ productId, Toast.LENGTH_SHORT).show();
        OkHttpUtils.post(AppConstants.AddShare)
                .tag(this)
                .params("username", AppConstants.USERNAME)
                .params("userid", AppConstants.USERID)
                .params("productid", productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        String jsonData = null;
                        String Message = null;
                        boolean Flag = false;
                        Gson gson = new Gson();
                        try {
                            jsonObject = new JSONObject(s);
                            Message=jsonObject.getString("Message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), Message , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(activity, "数据加载超时");
                    }
                });
    }

    private void getLocation(Location location) {
        /**
         * 得到当前经纬度并开启线程去反向地理编码
         */
        String latitude = location.getLatitude() + "";
        String longitude = location.getLongitude() + "";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=" + AppConstants.baiduMap_AK + "&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";
        OkHttpUtils.get(url)
                .tag(this)
                .params("ID", productId)//固定地方便调试，其他ID没数据"26013"
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        try {
                            s = s.replace("renderReverse&&renderReverse", "");
                            s = s.replace("(", "");
                            s = s.replace(")", "");
                            JSONObject jsonObject = new JSONObject(s);
                            JSONObject address = jsonObject.getJSONObject("result");
                            String city = address.getString("formatted_address");
                            String district = address.getString("sematic_description");
                            tv_productdetail_currentcity.setText(city + "附近");
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        ToastUtil.showShort(activity, "数据加载超时");
                    }
                });
    }

    /**
     * 判断是否有可用的内容提供器
     *
     * @return 不存在返回null
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
//            ToastUtil.showShort(getActivity(),"没有可用的位置提供器");
        }
        return null;
    }

    private void GetData() {
        if (productId == 0) {
            return;
        }
        OkHttpUtils.get(AppConstants.GetProductDetail)
                .tag(this)
                .params("ID", productId)//固定地方便调试，其他ID没数据"26013"
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            ReturnDataPre=jsonObject.getString("ReturnDataPre");
                            commentObject = new JSONObject(jsonData);
                            comment = commentObject.getString("Data");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //评论Data不是array
                        ProductDetailBeans = gson.fromJson(jsonData, ProductDetailBean.class);
                        initView(rootView);
                        initListener();
                        initData();
                        SetDate();
                        LinearLayout ll_hpl = (LinearLayout) rootView.findViewById(R.id.ll_hpl);
                        if (comment == null) {
                            rv_comment_photos.setVisibility(View.GONE);
                            ll_detail_comment.setVisibility(View.GONE);
                            ll_hpl.setVisibility(View.GONE);
                            if (tv_all_comment == null) {
                                tv_all_comment = (TextView) rootView.findViewById(R.id.tv_all_comment);
                            }
                            tv_all_comment.setVisibility(View.GONE);
                        } else {
                            if (comment.equals("")) {
                                ll_detail_comment.setVisibility(View.GONE);
                                ll_hpl.setVisibility(View.GONE);
                                tv_all_comment.setVisibility(View.GONE);
                                rv_comment_photos.setVisibility(View.GONE);
                            } else {//显示评论
                                //设置评论图片图片
                                commentBeans= gson.fromJson(comment, CommentBean.class);
                                if (commentBeans!=null){
                                    if (commentBeans.getImgList()!=null){
                                        for (CommentImgList Img:commentBeans.getImgList()){
                                            list.add(Img.getImgUrl());
                                        }
                                        CommentImgAdapter imageAdapter = new CommentImgAdapter(mContext,list);
                                        rv_comment_photos.setLayoutManager(new GridLayoutManager(mContext,3));
                                        rv_comment_photos.setAdapter(imageAdapter);
                                        imageAdapter.setOnItemClickListener(new CommentImgAdapter.OnSearchItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                onButtonClick.onAllComment("全部评价");
                                            }
                                        });
//            specialUpdate(((MyViewHolder)holder).tv_evaluate_tips,textNumber+"/120");
//            imageAdapter.DoDeleteEvaluateImags(mContext);
                                        rv_comment_photos.setNestedScrollingEnabled(false);
                                        rv_comment_photos.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    rv_comment_photos.setVisibility(View.GONE);
                                }


                                ll_hpl.setVisibility(View.VISIBLE);
                                tv_all_comment.setVisibility(View.VISIBLE);
                                try {
                                    JSONObject commentObj = new JSONObject(comment);

                                    String commentIco = commentObj.getString("Ico");
                                    SimpleDraweeView iv_comment_shop_icon = (SimpleDraweeView) rootView.findViewById(R.id.iv_comment_shop_icon);
                                    TextView tv_comment_shopname = (TextView) rootView.findViewById(R.id.tv_comment_shopname);
                                    TextView tv_comment_context = (TextView) rootView.findViewById(R.id.tv_comment_context);
                                    RatingBar rb_evaluate_shop_describe = (RatingBar) rootView.findViewById(R.id.rb_evaluate_shop_describe);
                                    if (commentObj.getInt("ID") == 0) {
                                        ll_detail_comment.setVisibility(View.GONE);
                                        ll_hpl.setVisibility(View.GONE);
                                        tv_all_comment.setVisibility(View.GONE);
                                    } else {
                                        ll_detail_comment.setVisibility(View.VISIBLE);
                                        ll_hpl.setVisibility(View.VISIBLE);
                                        tv_all_comment.setVisibility(View.VISIBLE);
                                    }
                                    if (!commentIco.equals("")) {
                                        iv_comment_shop_icon.setImageURI(commentIco);
                                    } else {
                                        iv_comment_shop_icon.setActualImageResource(R.mipmap.ic_launcher);
                                    }
                                    tv_comment_shopname.setText(commentObj.getString("UserName"));
                                    tv_comment_context.setText(commentObj.getString("Content"));
                                    rb_evaluate_shop_describe.setRating(Float.valueOf(commentObj.getString("StarNum")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
//            data.add(commentBeans);
//                        ToastUtil.showShort(activity,"getShopID="+ProductDetailBeans.getShopID());
                        getShopData();

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(activity,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(activity, "数据加载超时");
                    }
                });
    }

    private void getShopData() {
        OkHttpUtils.get(AppConstants.GetShop_ProductCount)
                .tag(this)
                .params("ID", ProductDetailBeans.getShopID())//固定地方便调试，其他ID没数据
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        JSONObject commentObject;
                        Gson gson = new Gson();
                        String jsonData = null;
                        String comment = null;
                        String Message = null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            commentObject = new JSONObject(jsonData);
//                            comment =  commentObject.getString("Data");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        shopDetailBeans = gson.fromJson(jsonData, ShopDetailBean.class);

                        SetShopDate();
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
//                        ToastUtil.showShort(activity,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(activity, "数据加载超时");
                    }
                });
    }

    private void SetDate() {
        /*Picasso.with(activity)
                .load(AppConstants.PhOTOADDRESS+ProductDetailBeans.getShopIco())
                .resize(100,75)
                .onlyScaleDown()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_start_loading)
                .error(R.mipmap.img_load_error)
                .into();*/
//        ToastUtil.showShort(activity,"getShopIco="+ProductDetailBeans.getShopIco());
        if (!(ProductDetailBeans == null)) {
            if (!ProductDetailBeans.getShopIco().equals("")) {
                Picasso.with(activity)
                        .load(AppConstants.PhOTOADDRESS + ProductDetailBeans.getShopIco())
                        .resize(100, 45)
//                        .onlyScaleDown()
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.mipmap.img_start_loading)
                        .error(R.mipmap.img_load_error)
                        .into(iv_develop_icon);
            } else {
                iv_develop_icon.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            iv_develop_icon.setImageResource(R.mipmap.ic_launcher);
        }
        tv_saleCount.setText("销售" + ProductDetailBeans.getSaleCount() + "笔");
        tv_goods_title.setText(ProductDetailBeans.getName());
        tv_shop_name.setText(ProductDetailBeans.getShopName());
        tv_new_price.setText(SpUtils.doubleToString(ProductDetailBeans.getMemberPrice()));
        tv_old_price.setText(getResources().getString(R.string.pricesymbol) + SpUtils.doubleToString(ProductDetailBeans.getMarketPrice()));
        tv_comment_count.setText("(" + String.valueOf(ProductDetailBeans.getCommentCount()) + ")");
        setLoopView();//轮播图
        //评论
//        assessmentAdapter = new AssessmentAdapter(activity, data);
//        assessmentAdapter.setOnItemClickListener(new AssessmentAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
////                Toast.makeText(activity, "点击了"+position, Toast.LENGTH_SHORT).show();
//            }
//        });
//        rv_product_RecyclerView_list.setLayoutManager(new LinearLayoutManager(activity));
//        rv_product_RecyclerView_list.setAdapter(assessmentAdapter);
//        rv_product_RecyclerView_list.setNestedScrollingEnabled(false);
        //判断是否显示产品sku（规格颜色尺寸）
        if (ProductDetailBeans.getSpecNum() > 0) {
            ll_specnum.setVisibility(View.VISIBLE);
            ll_prodetail_parameter.setVisibility(View.VISIBLE);
        } else {
            ll_specnum.setVisibility(View.GONE);
            ll_prodetail_parameter.setVisibility(View.GONE);
        }
        //积分
        tv_current_goods.setText("购买可得" + ProductDetailBeans.getScore() + "积分");
        if (ProductDetailBeans.getID() != 0) {
            setDetailData();
        }
    }

    private void SetShopDate() {
        tv_shop_collect.setText(String.valueOf(shopDetailBeans.getShopCollectCount()));
        tv_shop_newprod.setText(String.valueOf(shopDetailBeans.getProductNewCount()));
        tv_shop_allprod.setText(String.valueOf(shopDetailBeans.getProductCount()));
        tv_logistics.setText(String.valueOf(shopDetailBeans.getLogistics()));
        tv_service.setText(String.valueOf(shopDetailBeans.getService()));
        tv_prodes.setText(String.valueOf(shopDetailBeans.getProDes()));

    }

    private void initListener() {
        fab_up_slide.setOnClickListener(this);
        ll_current_goods.setOnClickListener(this);
        ll_activity.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_pull_up.setOnClickListener(this);
        ll_goods_detail.setOnClickListener(this);
        ll_goods_config.setOnClickListener(this);
        sv_switch.setOnSlideDetailsListener(this);
    }

    private void initView(View rootView) {
//        JZMediaManager.instance().mediaPlayer.start();
//        ToastUtil.showShort(activity,"初始化");
        fl_goods_image = (FrameLayout) rootView.findViewById(R.id.fl_goods_image);
//        fl_goods_image.setBackground(getResources().getDrawable(R.drawable.));
        fl_goods_image.getLayoutParams().width = AppConstants.ScreenWidth;
        fl_goods_image.getLayoutParams().height = AppConstants.ScreenWidth;//控制商品详情中轮播图占屏幕尺寸

         fl_goods_image1 = (FrameLayout) rootView.findViewById(R.id.fl_goods_image1);
//        fl_goods_image.setBackground(getResources().getDrawable(R.drawable.));
        fl_goods_image1.getLayoutParams().width = AppConstants.ScreenWidth;
        fl_goods_image1.getLayoutParams().height = AppConstants.ScreenWidth;//控制商品详情中轮播图占屏幕尺寸

        psts_tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.psts_tabs);
        fab_up_slide = (FloatingActionButton) rootView.findViewById(R.id.fab_up_slide);
        sv_switch = (SlideDetailsLayout) rootView.findViewById(R.id.sv_switch);
        sv_goods_info = (ScrollView) rootView.findViewById(R.id.sv_goods_info);
        v_tab_cursor = rootView.findViewById(R.id.v_tab_cursor);
//        vp_item_goods_img = (ConvenientBanner) rootView.findViewById(R.id.vp_item_goods_img);
//        vp_item_goods_img.getLayoutParams().width=AppConstants.ScreenWidth;
//        vp_item_goods_img.getLayoutParams().height=AppConstants.ScreenWidth;

        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);
        ll_current_goods = (LinearLayout) rootView.findViewById(R.id.ll_current_goods);
        ll_activity = (LinearLayout) rootView.findViewById(R.id.ll_activity);
        ll_comment = (LinearLayout) rootView.findViewById(R.id.ll_comment);
        ll_recommend = (LinearLayout) rootView.findViewById(R.id.ll_recommend);
        ll_pull_up = (LinearLayout) rootView.findViewById(R.id.ll_pull_up);
        ll_goods_detail = (LinearLayout) rootView.findViewById(R.id.ll_goods_detail);
        ll_goods_config = (LinearLayout) rootView.findViewById(R.id.ll_goods_config);
        tv_goods_detail = (TextView) rootView.findViewById(R.id.tv_goods_detail);
        tv_goods_config = (TextView) rootView.findViewById(R.id.tv_goods_config);
        tv_prodetail_prod_type = (TextView) rootView.findViewById(R.id.tv_prodetail_prod_type);
        ll_detail_comment = (LinearLayout) rootView.findViewById(R.id.ll_detail_comment);
        ll_detail_comment.setOnClickListener(this);
        tv_goods_title = (TextView) rootView.findViewById(R.id.tv_goods_title);
        tv_shop_name = (TextView) rootView.findViewById(R.id.tv_shop_name);
        tv_new_price = (TextView) rootView.findViewById(R.id.tv_new_price);
        tv_old_price = (TextView) rootView.findViewById(R.id.tv_old_price);
        tv_current_goods = (TextView) rootView.findViewById(R.id.tv_current_goods);
        tv_comment_count = (TextView) rootView.findViewById(R.id.tv_comment_count);
        tv_good_comment = (TextView) rootView.findViewById(R.id.tv_good_comment);
        tv_shop_allprod = (TextView) rootView.findViewById(R.id.tv_shop_allprod);
        tv_shop_newprod = (TextView) rootView.findViewById(R.id.tv_shop_newprod);
        tv_shop_collect = (TextView) rootView.findViewById(R.id.tv_shop_collect);
        tv_prodes = (TextView) rootView.findViewById(R.id.tv_prodes);
        tv_service = (TextView) rootView.findViewById(R.id.tv_service);
        tv_logistics = (TextView) rootView.findViewById(R.id.tv_logistics);
        tv_saleCount = (TextView) rootView.findViewById(R.id.tv_saleCount);
        tv_into_shop = (TextView) rootView.findViewById(R.id.tv_into_shop);
        tv_into_shop.setOnClickListener(this);
        tv_share = (TextView) rootView.findViewById(R.id.tv_share);
        tv_share.setOnClickListener(this);
        iv_develop_icon = (ImageView) rootView.findViewById(R.id.iv_develop_icon);
        tv_productdetail_currentcity = (TextView) rootView.findViewById(R.id.tv_productdetail_currentcity);
        tv_productdetail_currentcity.setOnClickListener(this);
        iv_isscore = (TextView) rootView.findViewById(R.id.iv_isscore);
        iv_isscore.setOnClickListener(this);
        //全部评论
        tv_all_comment = (TextView) rootView.findViewById(R.id.tv_all_comment);
        tv_all_comment.setOnClickListener(this);
        rv_product_RecyclerView_list = (RecyclerView) rootView.findViewById(R.id.rv_product_RecyclerView_list);
        rv_comment_photos = (RecyclerView) rootView.findViewById(R.id.rv_comment_photos);
        //规格
        ll_specnum = (LinearLayout) rootView.findViewById(R.id.ll_specnum);
        ll_specnum.setOnClickListener(this);
        //参数
        ll_prodetail_parameter = (LinearLayout) rootView.findViewById(R.id.ll_prodetail_parameter);
        ll_prodetail_parameter.setOnClickListener(this);


        setRecommendGoods();

        //设置文字中间一条横线
        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        fab_up_slide.hide();

        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//        vp_item_goods_img.setPageIndicator(new int[]{R.mipmap.tracking_logistics_item2, R.mipmap.loopswitch_page_current});
//        vp_item_goods_img.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
//        vp_item_goods_img.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                if (activity.isPause())
//                    return;
//                Intent intent=new Intent(activity,ImageShowActivity.class);
//                intent.putStringArrayListExtra("imgUrls",imgUrls);
//                intent.putExtra("index",position);
//                startActivity(intent);
//            }
//        });
//        vp_item_goods_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (activity.isPause()){
//                    return;
//                }
//                Intent intent=new Intent(activity,ImageShowActivity.class);
//                intent.putStringArrayListExtra("imgUrls",imgUrls);
//                startActivity(intent);
//            }
//        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.width = ScreenUtil.getScreenWidth(activity) / 2;
//        v_tab_cursor.setLayoutParams(params);


    }

    private void initData() {
        fragmentList = new ArrayList<>();
        tabTextList = new ArrayList<>();
        tabTextList.add(tv_goods_detail);
        tabTextList.add(tv_goods_config);
    }

    /**
     * 加载完商品详情执行
     */
    public void setDetailData() {
        goodsConfigFragment = new GoodsConfigFragment();
        goodsInfoWebFragment = new GoodsInfoWebFragment();
        Bundle bd = new Bundle();
        bd.putString("ProductID", String.valueOf(ProductDetailBeans.getID()));
        goodsInfoWebFragment.setArguments(bd);
        fragmentList.add(goodsConfigFragment);
        fragmentList.add(goodsInfoWebFragment);

        nowFragment = goodsInfoWebFragment;
        fragmentManager = getChildFragmentManager();
        //默认显示商品详情tab
        fragmentManager.beginTransaction().replace(R.id.fl_content, nowFragment).commitAllowingStateLoss();
    }

    /**
     * 设置推荐商品
     */
    public void setRecommendGoods() {
        List<RecommendGoodsBean> data = new ArrayList<>();
        data.add(new RecommendGoodsBean("Letv/乐视 LETV体感-超级枪王 乐视TV超级电视产品玩具 体感游戏枪 电玩道具 黑色",
                "http://img4.hqbcdn.com/product/79/f3/79f3ef1b0b2283def1f01e12f21606d4.jpg", new BigDecimal(599), "799"));
        data.add(new RecommendGoodsBean("IPEGA/艾派格 幽灵之子 无线蓝牙游戏枪 游戏体感枪 苹果安卓智能游戏手柄 标配",
                "http://img2.hqbcdn.com/product/00/76/0076cedb0a7d728ec1c8ec149cff0d16.jpg", new BigDecimal(299), "399"));
        data.add(new RecommendGoodsBean("Letv/乐视 LETV体感-超级枪王 乐视TV超级电视产品玩具 体感游戏枪 电玩道具 黑色",
                "http://img4.hqbcdn.com/product/79/f3/79f3ef1b0b2283def1f01e12f21606d4.jpg", new BigDecimal(599), "799"));
        data.add(new RecommendGoodsBean("IPEGA/艾派格 幽灵之子 无线蓝牙游戏枪 游戏体感枪 苹果安卓智能游戏手柄 标配",
                "http://img2.hqbcdn.com/product/00/76/0076cedb0a7d728ec1c8ec149cff0d16.jpg", new BigDecimal(299), "399"));
        List<List<RecommendGoodsBean>> handledData = handleRecommendGoods(data);
        //设置如果只有一组数据时不能滑动

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {//选择规格之后回调
            case 1: {
                if (resultCode == SpecOK) {
                    paramString = data.getStringExtra("paramString");
                    Propertyscace = data.getStringExtra("Propertys");
                    String count = data.getStringExtra("paramNumber");
                    specCaceBeans = data.getParcelableArrayListExtra("paramlist");
                    //回传数据数据到商品详情主activity
                    activity.setParamList(specCaceBeans, count);//传递数据给主Activity
                    if (specCaceBeans.size() != 0) {
                        tv_prodetail_prod_type.setText(paramString + " ," + count + "件");
                    } else {
                        tv_prodetail_prod_type.setText(count + "件");
                    }
                } else if (resultCode == SpecAddCart) {
                    listaddcartterner.addcartprocess(data.getStringExtra("paramString"), data.getStringExtra("Propertys"), data.getStringExtra("paramNumber"), data.<SpecItemBean>getParcelableArrayListExtra("paramlist")); // 3.1 执行回调
                } else if (resultCode == SpecSubmitOrder) {
                    listterner.process(data.getStringExtra("paramString"), data.getStringExtra("Propertys"), data.getStringExtra("paramNumber"), data.<SpecItemBean>getParcelableArrayListExtra("paramlist")); // 3.1 执行回调
                }
                break;
            }
            case 2: {
                if (data.getParcelableExtra("address") != null) {
                    addressBean = data.getParcelableExtra("address");
                }
                if (addressBean != null) {
                    if (addressBean.getAddress() != null) {
                        tv_productdetail_currentcity.setText(addressBean.getAddress());
                    }
                }
                break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (activity.isPause()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_pull_up:
                //上拉查看图文详情
                sv_switch.smoothOpen(true);
                break;

            case R.id.fab_up_slide:
                //点击滑动到顶部
                sv_goods_info.smoothScrollTo(0, 0);
                sv_switch.smoothClose(true);
                break;

            case R.id.ll_goods_detail:
                //商品详情tab
                nowIndex = 0;
                scrollCursor();
                switchFragment(nowFragment, goodsInfoWebFragment);
                nowFragment = goodsInfoWebFragment;
                break;

            case R.id.ll_goods_config:
                //规格参数tab
                nowIndex = 1;
                scrollCursor();
                switchFragment(nowFragment, goodsConfigFragment);
                nowFragment = goodsConfigFragment;
                break;
            case R.id.ll_specnum: {
                //规格参数
//                1.activity形式弹框
                Intent intent = new Intent(activity, GoodsInfoSpecActivity.class);
                intent.putExtra("ProductID", productId);
                intent.putExtra("Propertys", Propertyscace);
                intent.putExtra("price", ProductDetailBeans.getMemberPrice());
                intent.putExtra("spec", specBeans);
//                startActivity(intent);
                startActivityForResult(intent, SpecOK);
//                2.dialog形式弹框
                /*dialog = new Dialog(activity,R.style.ActionSheetDialogStyle);
                //填充对话框的布局
                inflate = LayoutInflater.from(activity).inflate(R.layout.activity_goosinfospec, null);
                //初始化控件
//                choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
//                takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
//                choosePhoto.setOnClickListener(this);
//                takePhoto.setOnClickListener(this);
                //将布局设置给Dialog
                dialog.setContentView(inflate);
                //获取当前Activity所在的窗体
                Window dialogWindow = dialog.getWindow();
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity( Gravity.BOTTOM);
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = ScreenUtil.getScreenWidth(activity);//设置Dialog距离底部的距离
                //将属性设置给窗体
                dialogWindow.setAttributes(lp);
                dialog.show();//显示对话框*/
            }
            break;
            case R.id.ll_prodetail_parameter: {
                /*if (TextUtils.isEmpty(paramString)){
                    ToastUtil.showShort(activity,"请求超时");
                    return;
                }
                Intent intent = new Intent(activity, GoodsInfoTrueSpecActivity.class);
                intent.putExtra("ProductID", productId);
                intent.putExtra("Param", paramString);
//                startActivity(intent);
                startActivity(intent);*/
            }
            break;
            case R.id.ll_detail_comment: {
                onButtonClick.onAllComment("全部评价");
            }
            break;
            case R.id.tv_all_comment: {
                onButtonClick.onAllComment("全部评价");
            }
            break;
            case R.id.iv_isscore:
                if (ProductDetailBeans.getIsScore() == 0) {
                    ToastUtil.showShort(activity, "积分不可用");
                } else {

                }
                break;
            case R.id.tv_productdetail_currentcity: {
                if (!AppConstants.ISLOGIN) {
                    return;
                }
                Intent intent = new Intent(activity, AddressManagerActivity.class);
                intent.putExtra("PersinalCenterActivity", "GoodsInfoFragment");
                startActivityForResult(intent, prodDetailAddress);
            }
            break;
            case R.id.tv_into_shop: {
                if (ProductDetailBeans.getShopID() == 0) {
                    return;
                }
                Intent intent = new Intent(activity, ShopIndexBaseActivity.class);
                intent.putExtra("ShopID", ProductDetailBeans.getShopID());
                startActivity(intent);
            }
            break;
            case R.id.tv_share: {//分享商品
                if (!AppConstants.ISLOGIN) {//没有登录提示登录
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ShareBoardConfig config = new ShareBoardConfig();
                    config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
                    mShareAction.open(config);
                }
            }
            break;
            default:
                break;
        }
    }

    /**
     * 给商品轮播图设置图片路径
     */
    public void setLoopView() {
        imgUrls = new ArrayList<String>();

        if (ProductDetailBeans != null) {
            if (ProductDetailBeans.getName() != null) {
                if (ReturnDataPre != null) {
                    if (!ReturnDataPre.equals("")) {
                        imgUrls.add("http://www.musicdo.cn"+ReturnDataPre);
                    }
                }
                if (!TextUtils.isEmpty(ProductDetailBeans.getImgUrl())) {
                    for (Object o : ProductDetailBeans.getImgUrl().split(",")) {
                        imgUrls.add(AppConstants.PhOTOADDRESS + (String) o);
                        if (imgUrls.size() == 5) {
                            break;
                        }
                    }
                    if (imgUrls.get(0) != null && !imgUrls.get(0).equals("")) {
                        Bitmap WidthHeight = decodeThumbBitmapForFile(imgUrls.get(0), AppConstants.ScreenWidth, AppConstants.ScreenWidth);//获取图片宽高之后对比
                        Bitmap aaa = WidthHeight;
//                        int Ratio=WidthHeight[0]/WidthHeight[1];
//                        if (WidthHeight[0]>AppConstants.ScreenWidth) {
//                            imgwidth=AppConstants.ScreenWidth;
//                            imgheight=imgwidth/Ratio;
//                        } else if (WidthHeight[1]>AppConstants.ScreenHeight) {
//                            imgheight=AppConstants.ScreenHeight;
//                            imgwidth=imgheight*Ratio;
//                        }else{
//                            imgheight=WidthHeight[1];
//                            imgwidth=WidthHeight[0];
//                        }
                    }
                }
            }
        }

        //初始化商品图片轮播
//        ToastUtil.showShort(activity,"size="+imgUrls.size());
        cBViewHolderCreator = new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                networkImageHolderView = new NetworkImageHolderView(200, 200);//修改原图片宽高
                return networkImageHolderView;
            }
        };

        cBViewHolderCreator1 = new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                networkImageHolderView1 = new NetworkImageHolderView(200, 200);//修改原图片宽高
                return networkImageHolderView1;
            }
        };
//        vp_item_goods_img.setPages(cBViewHolderCreator, imgUrls);
//        if (imgUrls!=null){
//            if (vp_item_goods_img!=null){
//            if (imgUrls.size()==1){
//                vp_item_goods_img.stopTurning();
//                vp_item_goods_img.setManualPageable(false);
//            }else{
//                vp_item_goods_img.setScrollDuration(800);
//            }
//            }
//        }
        startBanner();
    }


    @Override
    public void onStatucChanged(SlideDetailsLayout.Status status) {
        if (status == SlideDetailsLayout.Status.OPEN) {
            //当前为图文详情页
            fab_up_slide.show();
            activity.vp_content.setNoScroll(true);
            activity.tv_title.setVisibility(View.VISIBLE);
            activity.psts_tabs.setVisibility(View.GONE);
        } else {
            //当前为商品详情页
            fab_up_slide.hide();
            activity.vp_content.setNoScroll(false);
            activity.tv_title.setVisibility(View.GONE);
            activity.psts_tabs.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 不加载图片的前提下获得图片的宽高
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
//        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     *
     * @param options
     * @param
     * @param
     */
    public static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewHeight == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    /**
     * 滑动游标
     */
    private void scrollCursor() {
        TranslateAnimation anim = new TranslateAnimation(fromX, nowIndex * v_tab_cursor.getWidth(), 0, 0);
        anim.setFillAfter(true);//设置动画结束时停在动画结束的位置
        anim.setDuration(50);
        //保存动画结束时游标的位置,作为下次滑动的起点
        fromX = nowIndex * v_tab_cursor.getWidth();
        v_tab_cursor.startAnimation(anim);

        //设置Tab切换颜色
        for (int i = 0; i < tabTextList.size(); i++) {
            tabTextList.get(i).setTextColor(i == nowIndex ? getResources().getColor(R.color.text_red) : getResources().getColor(R.color.text_black));
        }
    }

    /**
     * 切换Fragment
     * <p>(hide、show、add)
     *
     * @param fromFragment
     * @param toFragment
     */
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {
        if (nowFragment != toFragment) {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (!toFragment.isAdded()) {    // 先判断是否被add过
                fragmentTransaction.hide(fromFragment).add(R.id.fl_content, toFragment).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到activity中
            } else {
                fragmentTransaction.hide(fromFragment).show(toFragment).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    /**
     * 处理推荐商品数据(每两个分为一组)
     *
     * @param data
     * @return
     */
    public static List<List<RecommendGoodsBean>> handleRecommendGoods(List<RecommendGoodsBean> data) {
        List<List<RecommendGoodsBean>> handleData = new ArrayList<>();
        int length = data.size() / 2;
        if (data.size() % 2 != 0) {
            length = data.size() / 2 + 1;
        }
        for (int i = 0; i < length; i++) {
            List<RecommendGoodsBean> recommendGoods = new ArrayList<>();
            for (int j = 0; j < (i * 2 + j == data.size() ? 1 : 2); j++) {
                recommendGoods.add(data.get(i * 2 + j));
            }
            handleData.add(recommendGoods);
        }
        return handleData;
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        OutputStream out;
        Bitmap bitma = null;
        try {
            out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitma = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
// bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitma;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(String.valueOf(activity),"清除视频播放进度");
        if(imgUrls!=null&&imgUrls.size()!=0){
        if (!MimeTypeMap.getFileExtensionFromUrl(imgUrls.get(0)).equals("mp4")) {
            JZVideoPlayer.clearSavedProgress(activity, imgUrls.get(0));
        }
        }
    }
}
