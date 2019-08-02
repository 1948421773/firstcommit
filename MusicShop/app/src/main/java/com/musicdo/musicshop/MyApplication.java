package com.musicdo.musicshop;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.lzy.okhttputils.OkHttpUtils;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.CrashMailHandler;
import com.musicdo.musicshop.util.MyFileNameGenerator;
import com.musicdo.musicshop.util.ToastUtil;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;
import com.v5kf.client.lib.Logger;
import com.v5kf.client.lib.V5ClientAgent;
import com.v5kf.client.lib.V5ClientConfig;
import com.v5kf.client.lib.callback.V5InitCallback;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2016/12/6.
 */

public class MyApplication extends MultiDexApplication {
    private static Stack<Activity> activityStack;
    private static Stack<Activity> payactivityStack;
    private Context context;
    private static MyApplication instance;
    private HttpProxyCacheServer proxy;

    public synchronized static MyApplication getInstance(){
        if(null==instance){
            instance=new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //在这里初始化Bugtags
        Config.DEBUG=true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        context = getApplicationContext();
        if (AppConstants.STARTBUGTAGS) {
            Bugtags.start("f710d5f27b8c7fe2bbe374b97decb7c2", this, Bugtags.BTGInvocationEventBubble);
        }



        initFresco();
        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        try {
            OkHttpUtils.getInstance()//
                    //是否打开调试
                    .debug("OkHttpUtils")
                    //默认信任全部证书 https
    //                    .setCertificates(getAssets().open("xxx.crt"))//crt要装换为bks
                            .setCertificates(getAssets().open("7f171e743f0f3c55.bks"),"musicshop")//password是musicshop
                    .setConnectTimeout(5000)
                    .setWriteTimeOut(5000)
                    .setReadTimeOut(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 异常捕获并发送邮件crashHandler,调试关闭
//        CrashMailHandler crashHandler = CrashMailHandler.getInstance();
//        crashHandler.init(getApplicationContext());

        initTBS();

        if (isMainProcess()) { // 判断为主进程，在主进程中初始化，多进程同时初始化可能导致不可预料的后果
            Logger.w("MyApplication", "onCreate isMainProcess V5ClientAgent.init");
            V5ClientConfig.FILE_PROVIDER = "com.musicdo.musicshop.fileprovider"; // 设置fileprovider的authorities
            V5ClientAgent.init(this, "162363", "27a3b0801bc94",  new V5InitCallback() {

                @Override
                public void onSuccess(String response) {
                    // TODO Auto-generated method stub
                    Logger.i("MyApplication", "V5ClientAgent.init(): " + response);
                }

                @Override
                public void onFailure(String response) {
                    // TODO Auto-generated method stub
                    Logger.e("MyApplication", "V5ClientAgent.init(): " + response);
                }
            });
        }
    }

    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加所有activity到mList列表中*/
    public void addActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * 添加支付activity到mList列表中*/
    public void addPayActivity(Activity activity){
        if(payactivityStack==null){
            payactivityStack=new Stack<Activity>();
        }
        payactivityStack.add(activity);
    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
    /**
     * 结束支付Activity
     */
    public void finishPayActivity(){
        for (int i = 0, size = payactivityStack.size(); i < size; i++){
            if (null != payactivityStack.get(i)){
                payactivityStack.get(i).finish();
            }
        }
        payactivityStack.clear();
    }
    /**
     * 退出应用程序
     */
    public void exitApp(){
        try {
            finishAllActivity();
            ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {}
    }

    {
        PlatformConfig.setWeixin("wx414bc9bba7078c6f","ZK17AyLiK972sgdpRhQyYcSPf81BfMtB");
        PlatformConfig.setQQZone("1106352211","805izi1CmoGNknJW");
        PlatformConfig.setSinaWeibo("","","");
    }

    /**
     * 初始化TBS浏览服务X5内核
     */
    private void initTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {}
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        exitApp();
    }

    /**
     * TRIM_MEMORY_COMPLETE：内存不足，并且该进程在后台进程列表最后一个，马上就要被清理
     TRIM_MEMORY_MODERATE：内存不足，并且该进程在后台进程列表的中部。
     TRIM_MEMORY_BACKGROUND：内存不足，并且该进程是后台进程。
     TRIM_MEMORY_UI_HIDDEN：内存不足，并且该进程的UI已经不可见了
     TRIM_MEMORY_COMPLETE这个监听的时候有时候监听不到，建议监听TRIM_MEMORY_MODERATE，在这个里面处理退出程序操作。
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        HttpProxyCacheServer proxy = new HttpProxyCacheServer.Builder(this)
                .fileNameGenerator(new MyFileNameGenerator())
                .build();
        return proxy;
    }

    private void initFresco() {

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext())
                .setDownsampleEnabled(true)// 对图片进行自动缩放
                .setResizeAndRotateEnabledForNetwork(true)   // 对网络图片进行resize处理，减少内存消耗
                .setBitmapsConfig(Bitmap.Config.ARGB_4444)    //图片设置RGB_565，减小内存开销  fresco默认情况下是RGB_8888
                .build();

        Fresco.initialize(this, getConfigureCaches(this));

    }

    private ImagePipelineConfig getConfigureCaches(Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                2* ByteConstants.MB,// 内存缓存中总图片的最大大小,以字节为单位。
                1,// 内存缓存中图片的最大数量。
                100* ByteConstants.MB,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                200,// 内存缓存中准备清除的总图片的最大数量。
                1* ByteConstants.MB);// 内存缓存中单个图片的最大大小。

        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(context);
        builder.setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams);
        builder.setDownsampleEnabled(true);// 对图片进行自动缩放
        builder.setResizeAndRotateEnabledForNetwork(true);   // 对网络图片进行resize处理，减少内存消耗
        builder.setBitmapsConfig(Bitmap.Config.ARGB_4444);   //图片设置RGB_565，减小内存开销  fresco默认情况下是RGB_8888
        return builder.build();
    }
}
