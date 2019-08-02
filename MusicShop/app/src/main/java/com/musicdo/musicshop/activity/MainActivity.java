package com.musicdo.musicshop.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bugtags.library.Bugtags;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.bean.VersionInfoBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.fragments.BaseFragment;
import com.musicdo.musicshop.fragments.GroupFragment;
import com.musicdo.musicshop.fragments.HomeFragment;
import com.musicdo.musicshop.fragments.PersonalFragment;
import com.musicdo.musicshop.fragments.SearchFragment;
import com.musicdo.musicshop.fragments.ShoppingCartFragment;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.util.UpdateStatus;
import com.musicdo.musicshop.util.UpdateVersionUtil;
import com.musicdo.musicshop.view.CustomDialog;
import com.musicdo.musicshop.view.JdRefreshHeader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,HomeFragment.onClickChangePageListner {
    private final String LoginKey="LoginKey";
    private final String LoginFile="LoginFile";
    private static final int OPENCAMERA=11;
    private Context context;
    final String  TAG="MainActivity";
    FrameLayout frameLayout;
    RadioGroup rgMain;
    RadioButton rb_cart,rb_user;
    JdRefreshHeader mHeaderView;
    //装fragment的实例集合
    private ArrayList<BaseFragment> fragments;
    private ArrayList<BaseFragment> saceFragments=new ArrayList<BaseFragment>(){{add(new HomeFragment()); add(new SearchFragment());add(new GroupFragment());add(new ShoppingCartFragment());add(new PersonalFragment());}};

    private int position = 0;
    //缓存Fragment或上次显示的Fragment
    private BaseFragment tempFragment;
    ImageView Qrcode,iv;
    private double xDistance,yDistance  ;
    private float xStart ,yStart,xEnd ,yEnd;
    private float mLastX;
    RelativeLayout rl_loopswitch;
    //记录用户首次点击返回键的时间
    private long firstTime=0;
    private ArrayList<SpecItemBean> specItemBeans=new ArrayList<>();

    private String Guid="";
    private int ProductID=0;
    private String Propertys="";
    private String PropertysText="";
    private double price=0.0;
    private int Count=0;
    String loginData="";
    FragmentManager fManager;
    BaseFragment HomeFragment,SearchFragment,GroupFragment,ShoppingCartFragment,PersonalFragment;
    boolean isSavedInstanceState=false;//// 判断是否“内存重启”

    SensorManager mSensorManager;
    JZVideoPlayer.JZAutoFullscreenListener mSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context=this;
        if (savedInstanceState != null) {
            // “内存重启”时调用,避免Fragment界面重叠
            isSavedInstanceState=true;
            rgMain=(RadioGroup) findViewById(R.id.rg_main);
            List<android.support.v4.app.Fragment> fragmentList = getSupportFragmentManager().getFragments();
//            ToastUtil.showLong(context,""+fragmentList.size());
            ArrayList<BaseFragment> caceFragments=saceFragments;
            for (int i = 0; i < fragmentList.size(); i++) {
//                    ToastUtil.showShort(context, fragmentList.get(i).toString());
//                    fragments.add((BaseFragment) fragmentList.get(i));
//                ToastUtil.showShort(context, "--"+i+"有"+fragmentList.get(i).toString());
                for (int j = 0; j <saceFragments.size() ; j++) {
                    if (saceFragments.get(j).getClass()==fragmentList.get(i).getClass()){
                        caceFragments.set(j,(BaseFragment) fragmentList.get(i));
//                        ToastUtil.showShort(context, "第"+j+"添加"+fragmentList.get(i).toString());
                        continue;
                    }
                }
            }
//            ToastUtil.showShort(context, "长度"+caceFragments.size());
            for (int i = 0; i <caceFragments.size() ; i++) {
//                ToastUtil.showShort(context, ""+caceFragments.get(i));
                /*if (caceFragments.get(0)==null){
                    caceFragments.add(new HomeFragment());
                }
                if (caceFragments.get(1)==null){
                    caceFragments.add(new SearchFragment());
                }
                if (caceFragments.get(2)==null){
                    caceFragments.add(new GroupFragment());
                }
                if (caceFragments.get(3)==null){
                    caceFragments.add(new ShoppingCartFragment());
                }
                if (caceFragments.get(4)==null){
                    caceFragments.add(new PersonalFragment());
                }*/
            }
            fragments = new ArrayList<>();
            fragments.addAll(caceFragments);
            /*if (fragments==null){
                fragments = new ArrayList<>();
                for (int i = 0; i<5; i++) {
                    if (fragmentList.size()==1){
                        if (fragmentList.get(0)!=null){
                            if (fragmentList.get(0) instanceof HomeFragment) {
                                HomeFragment = (HomeFragment) fragmentList.get(0);
                                fragments.add(HomeFragment);
//                                ToastUtil.showShort(context,"HomeFragment");
                            }else{
                                fragments.add(new SearchFragment());
                                fragments.add(new GroupFragment());
                                fragments.add(new ShoppingCartFragment());
                                fragments.add(new PersonalFragment());
                                break;
                            }
                        }
                    }

                    if (fragmentList.size()==2){

                        if (fragmentList.get(0)!=null){
                            if (fragmentList.get(0) instanceof HomeFragment) {
                                HomeFragment = (HomeFragment) fragmentList.get(0);
                                fragments.add(HomeFragment);
                            }else{
//                                fragments.add(new HomeFragment());
                            }
                        }
                        if (fragmentList.get(1)!=null) {
                             if (fragmentList.get(1) instanceof SearchFragment){
                                 SearchFragment = (SearchFragment) fragmentList.get(1);
                                fragments.add(SearchFragment);
                                fragments.add(new GroupFragment());
                                fragments.add(new ShoppingCartFragment());
                                fragments.add(new PersonalFragment());
                                 position = 1;
                            }else if (fragmentList.get(1) instanceof GroupFragment){
                                 GroupFragment = (GroupFragment) fragmentList.get(1);
                                 fragments.add(new SearchFragment());
                                 fragments.add(GroupFragment);
                                 fragments.add(new ShoppingCartFragment());
                                 fragments.add(new PersonalFragment());
                                 position = 2;
                             }else if (fragmentList.get(1) instanceof ShoppingCartFragment){
                                 ShoppingCartFragment = (ShoppingCartFragment) fragmentList.get(1);
                                 fragments.add(new SearchFragment());
                                 fragments.add(new GroupFragment());
                                 fragments.add(ShoppingCartFragment);
                                 fragments.add(new PersonalFragment());
                                 position = 3;
                             }else if (fragmentList.get(1) instanceof PersonalFragment){
                                 PersonalFragment = (PersonalFragment) fragmentList.get(1);
                                 fragments.add(new SearchFragment());
                                 fragments.add(new GroupFragment());
                                 fragments.add(new ShoppingCartFragment());
                                 fragments.add(PersonalFragment);
                                 position = 4;
                             }
                        }
                    }

                    if (fragmentList.size()==3){
                        if (fragmentList.get(0)!=null){
                            if (fragmentList.get(0) instanceof HomeFragment) {
                                HomeFragment = (HomeFragment) fragmentList.get(0);
                                fragments.add(HomeFragment);
                            }else{
//                                fragments.add(new HomeFragment());
                            }
                        }
                        if (fragmentList.get(1)!=null) {
                            if (fragmentList.get(1) instanceof SearchFragment){
                                SearchFragment = (SearchFragment) fragmentList.get(1);
                                fragments.add(SearchFragment);
                            }else {
                                fragments.add(new SearchFragment());
                            }
                        }
                        if (fragmentList.get(2)!=null) {
                            if (fragmentList.get(2) instanceof GroupFragment){
                                GroupFragment = (GroupFragment) fragmentList.get(2);
                                fragments.add(GroupFragment);
                                fragments.add(new ShoppingCartFragment());
                                fragments.add(new PersonalFragment());
                                position = 2;
                            }else if (fragmentList.get(1) instanceof ShoppingCartFragment){
                                ShoppingCartFragment = (ShoppingCartFragment) fragmentList.get(1);
                                fragments.add(new SearchFragment());
                                fragments.add(new GroupFragment());
                                fragments.add(ShoppingCartFragment);
                                fragments.add(new PersonalFragment());
                                position = 3;
                            }else if (fragmentList.get(1) instanceof PersonalFragment){
                                PersonalFragment = (PersonalFragment) fragmentList.get(1);
                                fragments.add(new SearchFragment());
                                fragments.add(new GroupFragment());
                                fragments.add(new ShoppingCartFragment());
                                fragments.add(PersonalFragment);
                                position = 4;
                            }

                        }
                    }
                    if (fragmentList.size()==4){
                        if (fragmentList.get(0)!=null){
                            if (fragmentList.get(0) instanceof HomeFragment) {
                                HomeFragment = (HomeFragment) fragmentList.get(0);
                                fragments.add(HomeFragment);
                            }else{
                                fragments.add(new HomeFragment());
                            }
                        }
                        if (fragmentList.get(1)!=null) {
                            if (fragmentList.get(1) instanceof SearchFragment) {
                                SearchFragment = (SearchFragment) fragmentList.get(1);
                                fragments.add(SearchFragment);
                                position = 1;
                            } else {
                                fragments.add(new SearchFragment());
                            }
                        }
                        if (fragmentList.get(2)!=null) {
                            if (fragmentList.get(2) instanceof GroupFragment) {
                                GroupFragment = (GroupFragment) fragmentList.get(2);
                                fragments.add(GroupFragment);
                                position = 2;
                            } else {
                                fragments.add(new GroupFragment());
                            }
                        }
                        if (fragmentList.get(3)!=null) {
                            if (fragmentList.get(3) instanceof ShoppingCartFragment) {
                                ShoppingCartFragment = (ShoppingCartFragment) fragmentList.get(3);
                                fragments.add(ShoppingCartFragment);
                                fragments.add(new PersonalFragment());
                                position = 3;
                            }else if (fragmentList.get(3) instanceof PersonalFragment){
                                PersonalFragment = (PersonalFragment) fragmentList.get(3);
                                fragments.add(new ShoppingCartFragment());
                                fragments.add(PersonalFragment);
                                position = 4;
                            }
                        }
                    }

                    if (fragmentList.size()==5){
                        if (fragmentList.get(0)!=null){
                            if (fragmentList.get(0) instanceof HomeFragment) {
                                HomeFragment = (HomeFragment) fragmentList.get(0);
                                fragments.add(HomeFragment);
                            }else{
                                fragments.add(new HomeFragment());
                            }
                        }
                        if (fragmentList.get(1)!=null) {
                            if (fragmentList.get(1) instanceof SearchFragment) {
                                SearchFragment = (SearchFragment) fragmentList.get(1);
                                fragments.add(SearchFragment);
                            } else {
                                fragments.add(new SearchFragment());
                            }
                        }
                        if (fragmentList.get(2)!=null) {
                            if (fragmentList.get(2) instanceof GroupFragment) {
                                GroupFragment = (GroupFragment) fragmentList.get(2);
                                fragments.add(GroupFragment);
                            } else {
                                fragments.add(new GroupFragment());
                            }
                        }
                        if (fragmentList.get(3)!=null) {
                            if (fragmentList.get(3) instanceof ShoppingCartFragment) {
                                ShoppingCartFragment = (ShoppingCartFragment) fragmentList.get(3);
                                fragments.add(ShoppingCartFragment);
                            } else {
                                fragments.add(new ShoppingCartFragment());
                            }
                        }
                        if (fragmentList.get(4)!=null) {
                            if (fragmentList.get(4) instanceof PersonalFragment) {
                                PersonalFragment = (PersonalFragment) fragmentList.get(4);
                                fragments.add(PersonalFragment);
                                position=5;
                            } else {
                                fragments.add(new PersonalFragment());
                            }
                        }
                    }

                }
                *//*switch (position){
                    case 0:
                        rgMain.check(R.id.rb_home);
                        break;
                    case 1:
                        rgMain.check(R.id.rb_type);
                        break;
                    case 2:
                        rgMain.check(R.id.rb_community);
                        break;
                    case 3:
                        rgMain.check(R.id.rb_cart);
                        break;
                    case 4:
                        rgMain.check(R.id.rb_user);
                        break;
                }*//*
                *//*BaseFragment baseFragment = getFragment(position);
//                switchFragment(tempFragment,baseFragment);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i <fragments.size() ; i++) {
                    if (position!=i) {
                        if (!fragments.get(i).isAdded()) {
                            transaction.add(R.id.frameLayout, fragments.get(i));
                            transaction.hide(fragments.get(i));
                            ToastUtil.showShort(context,"恢复添加"+fragments.get(position).toString());
                        } else {
                            transaction.hide(fragments.get(i));
                        }
                    }else{
                        ToastUtil.showShort(context,"恢复显示"+fragments.get(position).toString());
                        if (!fragments.get(i).isAdded()) {
                            transaction.replace(R.id.frameLayout, fragments.get(i)).commit();
//                            transaction.hide(fragments.get(i));
                        } else {
//                            transaction.hide(fragments.get(i));
                        }
                    }
                }

//                transaction.show(fragments.get(position)).commit();*//*

        }*/


        }
        if (HomeFragment==null){
            HomeFragment=new HomeFragment();
        }
        if (SearchFragment==null) {
            SearchFragment = new SearchFragment();
        }
        if (GroupFragment==null) {
            GroupFragment = new GroupFragment();
        }
        if (ShoppingCartFragment==null) {
            ShoppingCartFragment = new ShoppingCartFragment();
        }
        if (PersonalFragment==null) {
             PersonalFragment=new PersonalFragment();
            }

        loginData= SpUtils.getString(context, LoginKey, LoginFile);
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
        Authorization();//针对android 6.0 api-23及其以上的权限管理
        init();
        initFragment();
        //设置RadioGroup的监听
        initListener();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //初始化Fragment
                if (!isSavedInstanceState){
//                    ToastUtil.showShort(context,"正常显示"+isSavedInstanceState);
                    switchFragment(null,fragments.get(position));
                }else{
//                    ToastUtil.showShort(context,"恢复显示"+isSavedInstanceState);
                    isSavedInstanceState=false;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    for (int i = 0; i <fragments.size() ; i++) {
                        if (position!=i) {
                            if (!fragments.get(i).isAdded()) {
                                transaction.add(R.id.frameLayout, fragments.get(i));
                                transaction.hide(fragments.get(i));
//                                ToastUtil.showShort(context,"恢复添加"+fragments.get(i).toString());
                            } else {
                                transaction.hide(fragments.get(i));
                            }
                        }else{
//                            ToastUtil.showShort(context,"恢复显示"+fragments.get(i).toString());
                            if (!fragments.get(i).isAdded()) {
//                                transaction.replace(R.id.frameLayout, fragments.get(i)).commit();
//                            transaction.hide(fragments.get(i));
                            } else {
//                            transaction.hide(fragments.get(i));
                            }
                        }
                    }
                    tempFragment=fragments.get(position);
                    transaction.show(fragments.get(position)).commit();
                }
            }
        }, 500);
        //获取网络版本号
        OkHttpUtils.post(AppConstants.GetAppversion)
                .tag(this)
//                .params("PageIndex","")
//                .params("PageSize","")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {

                        JSONObject jsonObject;
                        String jsonData = null;
                        String Message = null;
                        String version = null;
                        boolean Flag = false;
                        try {
                            jsonObject = new JSONObject(s);
                            Message = jsonObject.getString("Message");
                            version = jsonObject.getString("version");
                            Flag = jsonObject.getBoolean("Flag");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (Flag) {
                            AppConstants.version=version;
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                    }
                });
        new Handler().postDelayed(new Runnable(){//更新版本
            @Override
            public void run() {
                SpUtils.UpdateVersionCode(MainActivity.this);
            }
        }, 1000);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new JZVideoPlayer.JZAutoFullscreenListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, "SaveFragment", base);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {

    }

    private void Authorization() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },OPENCAMERA);
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case OPENCAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    /*ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},OPENCAMERA);*/
//                    ToastUtil.showLong(context,"请打开打开全部权限");
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
                    builder.setMessage("需要开启权限后才能使用");
                    builder.setTitle("");
                    builder.setNegativeButton("设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                            }
                            startActivity(localIntent);
                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
            Bugtags.onResume(this);
        if (AppConstants.ScreenHeight==0||AppConstants.ScreenHeight==0){
            AppConstants.ScreenWidth= ScreenUtil.getScreenWidth(context);
            AppConstants.ScreenHeight= ScreenUtil.getScreenHeight(context);
        }
        loginData= SpUtils.getString(context, LoginKey, LoginFile);
        if (!loginData.equals("")){//获取本地信息
            AppConstants.ISLOGIN=true;
            try {
                AppConstants.USERNAME=new JSONObject(loginData).getString("Name");
                AppConstants.USERID=new JSONObject(loginData).getInt("ID");
                AppConstants.PHONE=new JSONObject(loginData).getString("Phone");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
//            Intent intent = new Intent(context, LoginActivity.class);
//            startActivity(intent);
        }
        if (getIntent().getStringExtra("ShoppingCartFragment")!=null) {
            if (getIntent().getStringExtra("ShoppingCartFragment").equals("ShoppingCartFragment")){
            position = 3;
            rgMain.check(R.id.rb_cart);
            switchContent2(tempFragment, getFragment(3));
        }
        }else{
        }
        if (getIntent().getStringExtra("PersonalFragment")!=null) {
            if (getIntent().getStringExtra("PersonalFragment").equals("PersonalFragment")){
                position = 4;
                rgMain.check(R.id.rb_user);
                switchContent2(tempFragment, getFragment(4));
            }
        }
        if (getIntent().getStringExtra("HomeFragment")!=null) {
            if (getIntent().getStringExtra("HomeFragment").equals("HomeFragment")){
                position = 0;
                rgMain.check(R.id.rb_home);
                switchContent2(tempFragment, getFragment(0));
            }
        }
        if (getIntent().getParcelableArrayListExtra("specItemBeans")!=null){//购物车编辑商品参数，返回主页后跳转购物车
            specItemBeans=getIntent().getParcelableArrayListExtra("specItemBeans");
            switchContent2(tempFragment,getFragment(3));
        } else{
        }
        if (getIntent().getIntExtra("ProductID",0)!=0){
            ProductID=getIntent().getIntExtra("getIntExtra",0);
        }
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
        }
        JZVideoPlayer.goOnPlayOnResume();
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
            Bugtags.onPause(this);
        JZVideoPlayer.releaseAllVideos();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //注：回调 3
            Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        ToastUtil.showShort(this,"onRestart购物车");
        switchFragment(null,fragments.get(3));
    }*/

    private void initListener() {
        switch (position){
            case 0:
                rgMain.check(R.id.rb_home);
                break;
            case 1:
                rgMain.check(R.id.rb_type);
                break;
            case 2:
                rgMain.check(R.id.rb_community);
                break;
            case 3:
                if (AppConstants.ISLOGIN){

                    rgMain.check(R.id.rb_cart);
                }
                break;
            case 4:
                if (AppConstants.ISLOGIN) {
                    rgMain.check(R.id.rb_user);
                }
                break;
        }

        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                if (!loginData.equals("")){//获取本地信息
                    AppConstants.ISLOGIN=true;
                    try {
                        AppConstants.USERNAME=new JSONObject(loginData).getString("Name");
                        AppConstants.USERID=new JSONObject(loginData).getInt("ID");
                        AppConstants.PHONE=new JSONObject(loginData).getString("Phone");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    switch (index){
                        case 0:
                            rgMain.check(R.id.rb_home);
                            break;
                        case 1:
                            rgMain.check(R.id.rb_type);
                            break;
                        case 2:
                            rgMain.check(R.id.rb_community);
                            break;
                        case 3:
                            rgMain.check(R.id.rb_cart);
                            break;
                        case 4:
                            rgMain.check(R.id.rb_user);
                            break;
                    }
                }else{
                    if (index==R.id.rb_cart||index==R.id.rb_user){
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                        switch (position){
                            case 0:
                                rgMain.check(R.id.rb_home);
                                break;
                            case 1:
                                rgMain.check(R.id.rb_type);
                                break;
                            case 2:
                                rgMain.check(R.id.rb_community);
                                break;
                            case 3:
                                rgMain.check(R.id.rb_cart);
                                break;
                            case 4:
                                rgMain.check(R.id.rb_user);
                                break;
                        }
                    return;
                    }
                }
                    switch (index){
                        case R.id.rb_home: //首页
                            position = 0;
                            break;
                        case R.id.rb_type: //分类
                            position = 1;
                            break;
                        case R.id.rb_community: //圈子
                            position = 2;
                            break;
                        case R.id.rb_cart: //购物车
                            position = 3;
                            break;
                        case R.id.rb_user: //个人中心
                            position = 4;
                            break;
                        default:
                            position = 0;
                            break;
                    }
                    //根据位置得到相应的Fragment
                    if (position==2)
                    {//屏蔽圈子功能
//                        ToastUtil.showLong(context,"圈子功能暂未开放使用");
                        return;
                    }
                    BaseFragment baseFragment = getFragment(position);
//                ToastUtil.showShort(context,"点击了"+baseFragment.toString());
                    /**
                     * 第一个参数: 上次显示的Fragment
                     * 第二个参数: 当前正要显示的Fragment
                     */
                    switchFragment(tempFragment,baseFragment);



            }
        });
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
//                        ToastUtil.showShort(context,"隐藏"+fragment.toString());
                        transaction.hide(fragment);
                    }
                    //添加Fragment
//                    ToastUtil.showShort(context,"添加"+nextFragment.toString());
                        transaction.add(R.id.frameLayout,nextFragment).commit();
                }else {
                    //隐藏当前Fragment
//                    ToastUtil.showShort(context,"隐藏"+fragment.toString());
                    if (fragment != null){
                        transaction.hide(fragment);
                    }
//                    ToastUtil.showShort(context,"显示"+nextFragment.toString());
                    transaction.show(nextFragment).commit();
                }

            }
            if (nextFragment.equals(HomeFragment.class)){
                Log.i(TAG, "switchFragment: 点了该空控件");
                    Fragment exFragment = (Fragment)getFragmentManager().findFragmentById(R.id.frameLayout);
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
    public void switchContent2(BaseFragment fragment,BaseFragment nextFragment){
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frameLayout,to);
        //transaction.addToBackStack(null);
//        transaction.commit();
//        rgMain.check(R.id.rb_cart);

        if (tempFragment != nextFragment){
            tempFragment = nextFragment;
            if (nextFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加成功
                if (!nextFragment.isAdded()){
                    //隐藏当前的Fragment
                    if (fragment != null){
                        transaction.remove(fragment);
                    }
                    //添加Fragment
                    transaction.replace(R.id.frameLayout,nextFragment).commit();
                }else {
                    //隐藏当前Fragment
                    if (fragment != null){
                        transaction.remove(fragment);
                    }
                    transaction.show(nextFragment).commit();
                }

            }
        }
    }
    /**
     * 添加的时候按照顺序
     */
    private void initFragment(){
        if (fragments==null){
        fragments = new ArrayList<>();
                fragments.add(HomeFragment);
                fragments.add(SearchFragment);
                fragments.add(GroupFragment);
                fragments.add(ShoppingCartFragment);
                fragments.add(PersonalFragment);
        }
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

    private void init() {
        frameLayout=(FrameLayout) findViewById(R.id.frameLayout);
        rgMain=(RadioGroup) findViewById(R.id.rg_main);
        rgMain.check(R.id.rb_home);
        rb_cart=(RadioButton) findViewById(R.id.rb_cart);
        rb_user=(RadioButton) findViewById(R.id.rb_user);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//扫一扫结果跳转
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Intent intent=new Intent(context,QrcodeActivity.class);
            intent.putExtra("url",scanResult);
            startActivity(intent);
        }
    }

    public String[] getGuid(){
            String GuidCace = Guid;
            String PropertysCase = Propertys;
            String PropertysTextCace = PropertysText;
            double priceCace = price;
            int ProductIDCace = ProductID;
            int CountCace = Count;
            Guid = "";
            Propertys = "";
            PropertysText = "";
            price = 0.0;
            ProductID = 0;
            Count = 0;
            return new String[]{GuidCace, PropertysCase, PropertysTextCace, String.valueOf(priceCace), String.valueOf(ProductIDCace), String.valueOf(CountCace)};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }}

    @Override
    public void setOnClickChangePageListner(int index) {
        position=1;
        rgMain.check(R.id.rb_type);
        switchContent2(tempFragment, getFragment(1));
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"onTouchEvent++++++ACTION_DOWN");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG,"onTouchEvent++++++ACTION_DOWN");

                break;

        }
        return super.onTouchEvent(event);
    }*/
    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "dispatchTouchEvent: ACTION_UP");
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "dispatchTouchEvent: ACTION_DOWN");
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "dispatchTouchEvent: ACTION_MOVE");
        }
        return  super.dispatchTouchEvent(ev);
//        return  false;

    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            JZVideoPlayerStandard jz=new JZVideoPlayerStandard(context);
            boolean aaa=jz.backPress();
            if (aaa) {
                JZVideoPlayer.backPress();
                Log.i("TAG",""+"关闭全屏");
                return true;
            }

            if (System.currentTimeMillis()-firstTime>1000){
                Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            }else{
                JZVideoPlayerStandard.releaseAllVideos();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
