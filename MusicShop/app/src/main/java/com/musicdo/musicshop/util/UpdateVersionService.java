package com.musicdo.musicshop.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yuedu on 2017/12/30.
 */

public class UpdateVersionService extends Service {
    private NotificationManager nm;
    private Notification notification;
    //标题标识
    private int titleId = 0;
    //安装文件
    private File updateFile;
    private long initTotal = 0;//文件的总长度

    @Override
    public void onCreate() {
        super.onCreate();
        updateFile = new File(AppConstants.SAVEFILE+"/musicdo.apk");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = "开始下载";
        Intent intent = ApkUtils.getInstallIntent(updateFile);
        PendingIntent pendingIntent = PendingIntent.getActivity(UpdateVersionService.this, 0, intent, 0);
        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击通知栏之后 消失
        notification.contentIntent  = pendingIntent;//启动指定意图
        notification.when = System.currentTimeMillis();
        notification.contentView = new RemoteViews(getPackageName(), R.layout.notifycation);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//		VersionInfo versionInfo = (VersionInfo) intent.getSerializableExtra("versionInfo");
//		String url = versionInfo.getDownloadUrl();
        Bundle bundle = intent.getExtras();
        String url = bundle.getString("downloadUrl");
        PreferenceUtils.setString(UpdateVersionService.this, "apkDownloadurl", url);
        nm.notify(titleId, notification);
        downLoadFile(url);
        return Service.START_STICKY;
    }

    public void downLoadFile(String url){
        OkHttpUtils.get(url)
                .tag(this)
                .execute(new FileCallback(AppConstants.SAVEFILE,"musicdo.apk") {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // 更改文字
                        notification.contentView.setTextViewText(R.id.msg, "下载完成!点击安装");
//                notification.contentView.setViewVisibility(R.id.btnStartStop, View.GONE);
//                notification.contentView.setViewVisibility(R.id.btnCancel,View.GONE);
                        // 发送消息
                        nm.notify(0, notification);
//                        stopSelf();
                        //收起通知栏
                        UpdateVersionUtil.collapsingNotification(UpdateVersionService.this);
                        //自动安装新版本
                        Intent installIntent = ApkUtils.getInstallIntent(updateFile);
                        startActivity(installIntent);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //网络连接错误
                        if(response.code() == 0 ){
                            // 更改文字
                            notification.contentView.setTextViewText(R.id.msg, "网络异常！请检查网络设置！");
                        }else{//文件已经下载完毕
                            // 更改文字
                            notification.contentView.setTextViewText(R.id.msg, "乐都城");
                            // 更改文字
                            notification.contentView.setTextViewText(R.id.bartext, "检测到新版本已经下载完成，点击即安装!");
                            // 隐藏进度条
                            notification.contentView.setViewVisibility(R.id.progressBar1, View.GONE);
                            Intent intent = ApkUtils.getInstallIntent(updateFile);
                            PendingIntent pendingIntent = PendingIntent.getActivity(UpdateVersionService.this, 0, intent, 0);
                            notification.flags = Notification.FLAG_AUTO_CANCEL;//点击通知栏之后 消失
                            notification.contentIntent  = pendingIntent;//启动指定意图
                        }
                        // 发送消息
                        nm.notify(0, notification);
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        if(initTotal == 0){//说明第一次开始下载
                            initTotal = totalSize;
                        }

                        if(initTotal != totalSize){//说明下载过程中暂停过，文件的总长度出现问题  就把初始的文件的长度赋值给他重新计算已经下载的比例
                            totalSize = initTotal;
                        }

                        long l = currentSize*100/totalSize;
                        notification.contentView.setTextViewText(R.id.msg, "正在下载：乐都城");
                        // 更改文字
                        notification.contentView.setTextViewText(R.id.bartext, l+ "%");
                        // 更改进度条
                        notification.contentView.setProgressBar(R.id.progressBar1, 100,(int)l, false);
                        // 发送消息
                        nm.notify(0, notification);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        notification.contentView.setTextViewText(R.id.msg, "开始下载：乐都城");
                        nm.notify(titleId, notification);
                    }



    });

}


    @Override
    public void onDestroy() {
        //下载完成时，清楚该通知，自动安装
        nm.cancel(titleId);
        System.out.println("UpdateVersionService----onDestroy");
//		try {
//			GdmsaecApplication.db.deleteAll(VersionInfo.class);
//		} catch (DbException e) {
//			e.printStackTrace();
//		}
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
