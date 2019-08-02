package com.musicdo.musicshop.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.VersionInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.bean.VersionInfoBean;
import com.musicdo.musicshop.constant.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/12/30.
 * 版本更新的工具类
 */

public class UpdateVersionUtil {
    /**
     * 接口回调
     * @author wenjie
     *
     */
    public interface UpdateListener{
        void onUpdateReturned(int updateStatus,VersionInfoBean versionInfo);
    }

    public UpdateListener updateListener;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    /**
     * 网络测试 检测版本
     * @param context 上下文
     */
    public static void checkVersion(final Context context, final UpdateListener updateListener){
        OkHttpUtils.get(AppConstants.GetHomeThird)
                .tag(context)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        try {
                            JSONObject jsonObject = JsonUtil.stringToJson(s);
                            JSONArray array = jsonObject.getJSONArray("data");
                            VersionInfoBean mVersionInfo = JsonUtil.jsonToBean(array.getJSONObject(0).toString(), VersionInfoBean.class);
                            int clientVersionCode = ApkUtils.getVersionCode(context);
                            int serverVersionCode = mVersionInfo.getVersionCode();
                            //有新版本
                            if(clientVersionCode < serverVersionCode){
                                int i = NetworkUtil.checkedNetWorkType(context);
                                if(i == NetworkUtil.NOWIFI){
                                    updateListener.onUpdateReturned(UpdateStatus.NOWIFI,mVersionInfo);
                                }else if(i == NetworkUtil.WIFI){
                                    updateListener.onUpdateReturned(UpdateStatus.YES,mVersionInfo);
                                }
                            }else{
                                //无新本
                                updateListener.onUpdateReturned(UpdateStatus.NO,null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            updateListener.onUpdateReturned(UpdateStatus.ERROR,null);
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        swipeRefreshLayout.setRefreshing(true);
//                            Log.i(TAG, "onBefore: 开始加载");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        updateListener.onUpdateReturned(UpdateStatus.TIMEOUT,null);
                    }
                });
    }


    /**
     * 本地测试
     */
    public static void localCheckedVersion(final Context context, final UpdateListener updateListener){
        try {
//			JSONObject jsonObject = JsonUtil.stringToJson(resultData);
//			JSONArray array = jsonObject.getJSONArray("data");
//			VersionInfo mVersionInfo = JsonUtil.jsonToBean(array.getJSONObject(0).toString(), VersionInfo.class);
            VersionInfoBean mVersionInfo = new VersionInfoBean();
            mVersionInfo.setDownloadUrl(AppConstants.GetApp);
            mVersionInfo.setVersionDesc("是否更新");
            mVersionInfo.setVersionCode(Integer.valueOf(AppConstants.version));
            mVersionInfo.setId("1");
            int clientVersionCode = ApkUtils.getVersionCode(context);
            int serverVersionCode = mVersionInfo.getVersionCode();
            //有新版本
            if(clientVersionCode < serverVersionCode){
                int i = NetworkUtil.checkedNetWorkType(context);
                if(i == NetworkUtil.NOWIFI){
                    updateListener.onUpdateReturned(UpdateStatus.YES,mVersionInfo);
                }else if(i == NetworkUtil.WIFI){
                    updateListener.onUpdateReturned(UpdateStatus.YES,mVersionInfo);
                }
            }else{
                //无新本
//                updateListener.onUpdateReturned(UpdateStatus.YES,mVersionInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateListener.onUpdateReturned(UpdateStatus.ERROR,null);
        }
    }


    /**
     * 弹出新版本提示
     * @param context 上下文
     * @param versionInfo 更新内容
     */
    public static void showDialog(final Context context,final VersionInfoBean versionInfo){
        final Dialog dialog = new AlertDialog.Builder(context).create();
        final File file = new File(AppConstants.SAVEFILE+"/musicdo.apk");
        dialog.setCancelable(true);// 可以用“返回键”取消
        dialog.setCanceledOnTouchOutside(false);//
        dialog.show();
        View view = LayoutInflater.from(context).inflate(R.layout.version_update_dialog, null);
        dialog.setContentView(view);

        final Button btnOk = (Button) view.findViewById(R.id.btn_update_id_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_update_id_cancel);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_update_content);
        TextView tvUpdateTile = (TextView) view.findViewById(R.id.tv_update_title);
        final TextView tvUpdateMsgSize = (TextView) view.findViewById(R.id.tv_update_msg_size);

        tvContent.setText(versionInfo.getVersionDesc());
        tvUpdateTile.setText("最新版本："+versionInfo.getVersionName());

        if(file.exists() && file.getName().equals("musicdo.apk")){
            tvUpdateMsgSize.setText("新版本已经下载，是否安装？");
        }else{
            tvUpdateMsgSize.setText("新版本大小："+versionInfo.getVersionSize());
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(v.getId() == R.id.btn_update_id_ok){
                    //新版本已经下载
//                    if(file.exists() && file.getName().equals(AppConstants.SAVEFILE+"/musicdo.apk")){
//                        Intent intent = ApkUtils.getInstallIntent(file);
//                        context.startActivity(intent);
//                    }else{
                        //没有下载，则开启服务下载新版本
                        ToastUtil.showLong(context,"正在下载新版本,下载完成将自动安装");
                        Intent intent = new Intent(context,UpdateVersionService.class);
                        intent.putExtra("downloadUrl", versionInfo.getDownloadUrl());
                        context.startService(intent);
//                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 收起通知栏
     * @param context
     */
    public static void collapsingNotification(Context context) {
          Object service = context.getSystemService("statusbar");
          if (null == service)
             return;
          try {
                 Class<?> clazz = Class.forName("android.app.StatusBarManager");
                 int sdkVersion = android.os.Build.VERSION.SDK_INT;
                 Method collapse;
                 if (sdkVersion <= 16) {
                    collapse = clazz.getMethod("collapse");
                     } else {
                    collapse = clazz.getMethod("collapsePanels");
                     }
                 collapse.setAccessible(true);
                 collapse.invoke(service);
              } catch (Exception e) {
                 e.printStackTrace();
              }
           }
}
