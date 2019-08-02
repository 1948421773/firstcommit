package com.musicdo.musicshop.activity;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.AreasBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ScreenUtil;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.musicdo.musicshop.view.LoadingDialog;
import com.musicdo.musicshop.view.OptionsPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:添加收货地址
 * 作者：haiming on 2017/8/15 10:48
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AddingAddressActivity extends BaseActivity implements View.OnClickListener,TextView.OnEditorActionListener {
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private static final int MSG_CHANGE_ADDRESS = 0x0004;
    private Thread thread;
    LoadingDialog dialog;
    private TextView tv_right_one_bt,tv_default_address,tv_address_areas,tv_title;
    private EditText ed_address_name,ed_address_phone,ed_address_detail_address;
    int isDefault=0;
    private Context context;
    private boolean isLoaded = false;//判断是否解析json
    private ArrayList<AreasBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String addressString="";
    String ProvinceID="";
    String CityID="";
    String CountyID="";
    OptionsPickerView pvOptions;
    private LinearLayout ll_back;
    private CheckBox cb_address_default;
    String loginData="";
    String ProvinceName="";
    String CityName="";
    String CountyName="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addingaddress);
        MyApplication.getInstance().addActivity(this);
        context=this;
        initView();
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
//                    Toast.makeText(JsonDataActivity.this,"Parse Succeed",Toast.LENGTH_SHORT).show();
                    if(isLoaded) {
                        ShowPickerView();
                    }
                    break;

                case MSG_LOAD_FAILED://本地JSON解析失败
//                    Toast.makeText(JsonDataActivity.this,"Parse Failed",Toast.LENGTH_SHORT).show();
                    break;
                case MSG_CHANGE_ADDRESS://本地JSON解析失败
                    tv_address_areas.setText(addressString);
                    break;

            }
        }
    };

    private void initView() {
        dialog = new LoadingDialog(context,R.style.LoadingDialog);
        ll_back=(LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_right_one_bt=(TextView)findViewById(R.id.tv_right_one_bt);
        tv_right_one_bt.setOnClickListener(this);
        tv_right_one_bt.setText("保存");
        cb_address_default=(CheckBox) findViewById(R.id.cb_address_default);
        cb_address_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked){
                    isDefault=1;
//                    tv_default_address.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    tv_default_address.setTextColor(getResources().getColor(R.color.white));
                }else{
                    isDefault=0;
//                    tv_default_address.setBackgroundColor(getResources().getColor(R.color.white));
//                    tv_default_address.setTextColor(getResources().getColor(R.color.text_black));
                }
            }
        });
        tv_default_address=(TextView)findViewById(R.id.tv_default_address);
        tv_default_address.setOnClickListener(this);
        ed_address_name=(EditText)findViewById(R.id.ed_address_name);
        ed_address_name.setOnEditorActionListener(this);
        ed_address_detail_address=(EditText)findViewById(R.id.ed_address_detail_address);
        ed_address_detail_address.setOnEditorActionListener(this);
        ed_address_phone=(EditText)findViewById(R.id.ed_address_phone);
        ed_address_phone.setOnEditorActionListener(this);
        tv_address_areas=(TextView)findViewById(R.id.tv_address_areas);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("添加地址");
        tv_address_areas.setOnClickListener(this);
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
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
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

    private void ShowPickerView() {// 弹出选择器

        pvOptions= new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                addressString = options1Items.get(options1).getProvinceName()+
                        options2Items.get(options1).get(options2)+
                        options3Items.get(options1).get(options2).get(options3);
//                Toast.makeText(context,tx,Toast.LENGTH_SHORT).show();
                ProvinceID=options1Items.get(options1).getProvinceId();
                ProvinceName=options1Items.get(options1).getProvinceName();
                CityID=options1Items.get(options1).getDistrict().get(options2).getDistrictId();
                CityName=options1Items.get(options1).getDistrict().get(options2).getDistrictName();
                CountyID=options1Items.get(options1).getDistrict().get(options2).getCounty().get(options3).getCountyId();
                CountyName=options1Items.get(options1).getDistrict().get(options2).getCounty().get(options3).getCountyName();
                mHandler.sendEmptyMessage(MSG_CHANGE_ADDRESS);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.RED)
                .setTextColorCenter(Color.RED) //设置选中项文字颜色
                .setContentTextSize(20)
                .setSubmitColor(Color.RED)
                .setCancelColor(Color.RED)
                .setBgColor(context.getResources().getColor(R.color.white))
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
        InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
}

    private void AddUserAddress(String name,String PhoneNumber,String Address,int IsDefault,String ProvinceID,String CityID,String CountyID) {
        OkHttpUtils.post(AppConstants.AddUserAddress)
                .tag(this)
                .params("UserID",AppConstants.USERID)
                .params("Name",name)
                .params("PhoneNumber",PhoneNumber)
                .params("Address",Address)
                .params("IsDefault",IsDefault)
                .params("ProvinceID",ProvinceID)
                .params("ProvinceName",ProvinceName)
                .params("CityID",CityID)
                .params("CityName",CityName)
                .params("CountyID",CountyID)
                .params("CountyName",CountyName)
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
                            ToastUtil.showShort(context,Message);

                        }else{
                            finish();
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
    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_right_one_bt:
                    InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String address_name=SpUtils.filterCharToNormal(ed_address_name.getText().toString().trim());
                    ed_address_name.setText(address_name);
                    if (address_name!=""){
                        ed_address_name.setSelection(ed_address_name.getText().toString().trim().length());
                    }
                    if (address_name.trim().length()>15){
                        address_name=address_name.trim().substring(0,15);
                    }
                    String address_detail_address=SpUtils.filterCharToNormal(ed_address_detail_address.getText().toString().trim());
                    ed_address_detail_address.setText(address_detail_address);
                    if (address_detail_address!=""){
                        ed_address_detail_address.setSelection(ed_address_detail_address.getText().toString().trim().length());
                    }
                    if (address_detail_address.trim().length()>36){
                        address_detail_address=address_detail_address.trim().substring(0,36);
                    }
                    if(address_name.length()!=0&&ed_address_phone.getText().toString().trim().length()!=0&&tv_address_areas.getText().toString().trim().length()!=0&&address_detail_address.trim().length()!=0){
                        if (SpUtils.isMobileNO(ed_address_phone.getText().toString().trim())){
                            AddUserAddress(address_name,ed_address_phone.getText().toString().trim(),address_detail_address,isDefault,ProvinceID,CityID,CountyID);
                        }else{
                            ToastUtil.showShort(context,"手机号码不正确");
                        }
                    }else if(address_name.length()==0){
                        ToastUtil.showShort(context,"收货人不能为空");
                        return;
                    }
                    else if(ed_address_phone.getText().toString().trim().length()==0){
                        ToastUtil.showShort(context,"手机号码不正确");
                        return;
                    }else if(tv_address_areas.getText().toString().trim().length()==0){
                        ToastUtil.showShort(context,"请选择地区");
                        return;

                    }else if(address_detail_address.trim().length()==0){
                        ToastUtil.showShort(context,"请输入详细地址");
                        return;

                    }
                    break;
                case R.id.tv_default_address:
                    /*if (isDefault==0){
                        isDefault=1;
                        tv_default_address.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        tv_default_address.setTextColor(getResources().getColor(R.color.white));
                    }else{
                        isDefault=0;
                        tv_default_address.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_default_address.setTextColor(getResources().getColor(R.color.text_black));
                    }*/
                    break;
                case R.id.tv_address_areas:
                    if(isLoaded) {
                        ShowPickerView();
                    }else{
                        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
                    }
                    break;
                case R.id.ll_back:
                    finish();
                    break;
            }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(v.getId()){
            case R.id.ed_address_name:
                if (ed_address_name.getText().toString().trim().length()>15){
                    ed_address_name.setText(ed_address_name.getText().toString().trim().substring(0,15));
                }
                break;
            case R.id.ed_address_phone:
                if (ed_address_phone.getText().toString().trim().length()>11){
                    ed_address_phone.setText(ed_address_phone.getText().toString().trim().substring(0,11));
                }
                break;
            case R.id.ed_address_detail_address:
                if (ed_address_detail_address.getText().toString().trim().length()>36){
                    ed_address_detail_address.setText(ed_address_detail_address.getText().toString().trim().substring(0,36));
                }
                break;
        }
        return false;
    }
    public static void showInputKeyboard(Context context , View view){

//        if (null != view){

            InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);

//        }

    }

    public static void hideInputKeyboard(Context context , View view){

//        if (null != view){

            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

//        }

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
