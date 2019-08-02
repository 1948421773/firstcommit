package com.musicdo.musicshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.adapter.AddressAdapter;
import com.musicdo.musicshop.adapter.GridAdapter;
import com.musicdo.musicshop.bean.AddressBean;
import com.musicdo.musicshop.bean.AreasBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:
 * 作者：haiming on 2017/8/15 15:57
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AddressManagerActivity extends BaseActivity implements View.OnClickListener {
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private LinearLayout ll_back;
    private TextView tv_default_address,tv_title;
    private Context context;
    private RecyclerView rc_addresss_list;
    ArrayList<AddressBean> AddressBeans=new ArrayList<>();
    AddressAdapter addressAdapter;
    private ArrayList<AreasBean> options1Items = new ArrayList<>();
    private Thread thread;
    private String PersinalCenterActivity="";
    private boolean isLoaded = false;//判断是否解析json
    LoadingDialog dialog;
    String loginData="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressmanager);
        MyApplication.getInstance().addActivity(this);
        context=this;
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        initView();
        PersinalCenterActivity=getIntent().getStringExtra("PersinalCenterActivity");
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

        if (AddressBeans==null) {
            if (AddressBeans.size()==0){
                mHandler.sendEmptyMessage(MSG_LOAD_DATA);//获取json省市区
            }else{
                UserAddressList();
            }
        }else{
            UserAddressList();
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread==null){//如果已创建就不再重新创建子线程了

//                        Toast.makeText(context,"Begin Parse Data",Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS://本地JSON解析完成，可以显示
                    if(isLoaded) {
                        UserAddressList();
                    }else{

                    }
                    break;

            }
        }
    };

    private void UserAddressList() {
        OkHttpUtils.post(AppConstants.Query_UserAddressList)
                .tag(this)
                .params("UserID",AppConstants.USERID)
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
                        if (!Flag){
//                            ToastUtil.showShort(context,Message);
                            AddressBeans.clear();
                            if (addressAdapter!=null) {
                                addressAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if (AddressBeans!=null){
                                if (AddressBeans.size()!=0){
                                    AddressBeans.clear();
                                    ArrayList<AddressBean> AddressBeanCases=new ArrayList<>();
                                    AddressBeanCases= gson.fromJson(jsonData, new TypeToken<ArrayList<AddressBean>>() {}.getType());
                                    AddressBeans.addAll(AddressBeanCases);
                                    addressAdapter.notifyDataSetChanged();
                                }else {
                                    AddressBeans= gson.fromJson(jsonData, new TypeToken<ArrayList<AddressBean>>() {}.getType());
                                    //显示乐器馆
                                    if(addressAdapter==null&&AddressBeans!=null){
                                        addressAdapter = new AddressAdapter(context,AddressBeans,options1Items);
                                        rc_addresss_list.setAdapter(addressAdapter);//recyclerview设置适配器
                                        addressAdapter.setOnItemClickListener(new AddressAdapter.OnSearchItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
//                                                Toast.makeText(context, "点击了"+position, Toast.LENGTH_SHORT).show();
                                                if (PersinalCenterActivity!=null){
                                                    if (PersinalCenterActivity.equals("GoodsInfoFragment")) {
                                                        Intent intent = new Intent();
                                                        intent.putExtra("address",AddressBeans.get(position));
                                                        setResult(2, intent);
                                                        finish();
                                                    }
                                                }else{
                                                    Intent intent = new Intent(context, SubmitOrderActivity.class);
                                                    intent.putExtra("address", AddressBeans.get(position));
                                                    intent.putExtra("addressId", AddressBeans.get(position).getID());
                                                    startActivity(intent);

                                                }

                                            }
                                        });
                                        rc_addresss_list.setNestedScrollingEnabled(false);
                                    }
                                }
                            }

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

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new SpUtils().getJson(this,"areas.json");//获取assets目录下的json文件数据

        ArrayList<AreasBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getDistrict().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getDistrict().get(c).getDistrictName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getDistrict().get(c).getCounty() == null
                        ||jsonBean.get(i).getDistrict().get(c).getCounty().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getDistrict().get(c).getCounty().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getDistrict().get(c).getCounty().get(d).getCountyName();

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
//            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
//            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
        isLoaded = true;

    }

    public ArrayList<AreasBean> parseData(String result) {//Gson 解析
        ArrayList<AreasBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                AreasBean entity = gson.fromJson(data.optJSONObject(i).toString(), AreasBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    private void initView() {

        ll_back=(LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_default_address=(TextView)findViewById(R.id.tv_default_address);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("地址管理");
        tv_default_address.setOnClickListener(this);

        rc_addresss_list = (RecyclerView) findViewById(R.id.rc_addresss_list);
        GridLayoutManager layoutManage = new GridLayoutManager(context, 1);
        rc_addresss_list.setLayoutManager(layoutManage);

    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_back:
                    if (PersinalCenterActivity!=null) {
                    if (PersinalCenterActivity.equals("GoodsInfoFragment")) {
                        Intent intent = new Intent();
                        intent.putExtra("address", "");
                        setResult(2, intent);
                        finish();
                    }else{
                        finish();
                    }
                    }else{
                        finish();
                    }
                    break;
                case R.id.tv_default_address: {
                    Intent intent = new Intent(context, AddingAddressActivity.class);
                    startActivity(intent);
                }
                    break;
            }
    }

    @Override
    public void onDestroy() {//activity结束dialog消失
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if (PersinalCenterActivity!=null) {
            if (PersinalCenterActivity.equals("GoodsInfoFragment")) {//
                Intent intent = new Intent();
                intent.putExtra("address", "");
                setResult(2, intent);
                finish();
            }else{
                finish();
            }
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
