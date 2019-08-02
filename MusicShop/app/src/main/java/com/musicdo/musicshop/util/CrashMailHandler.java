package com.musicdo.musicshop.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.activity.MainActivity;
import com.musicdo.musicshop.constant.AppConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Created by Yuedu on 2017/11/27.
 */

public class CrashMailHandler implements Thread.UncaughtExceptionHandler {
    private final static String TAG = "UncaughtExceptionHandler";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashMailHandler mInstance;
    private Context mContext;
    /** 使用Properties来保存设备的信息和错误堆栈信息*/
    private Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    /** 在Release状态下关闭以提示程序性能
     * */
    public static final boolean DEBUG = false;
    /** 错误报告文件的扩展名 */
    private static final String CRASH_REPORTER_EXTENSION = ".cr";
    private CrashMailHandler() {

    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashMailHandler getInstance() {
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//                .penaltyLog().penaltyDeath().build());
        if (mInstance == null)
            mInstance = new CrashMailHandler();
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        AppConstants.BRAND=android.os.Build.BRAND;
        AppConstants.MODEL=android.os.Build.MODEL;
        if (!handleException(throwable) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            // Sleep一会后结束程序
            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
            try {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                        PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    mDeviceCrashInfo.put(VERSION_NAME,
                            pi.versionName == null ? "not set" : pi.versionName);
                    mDeviceCrashInfo.put(VERSION_CODE, ""+pi.versionCode);
                }
                EmailUtil.sendEmail(null, AppConstants.BRAND+"--"+AppConstants.MODEL+"普罗爵士 "+"VersionName="+pi.versionName+" "+"VersionCode="+pi.versionCode,throwable.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
//                Log.e(TAG, "Error : ", e);
            }
            MyApplication.getInstance().exitApp();
        }
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        new Thread() {
            @Override
            public void run() {
                try {
                    PackageManager pm = mContext.getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                            PackageManager.GET_ACTIVITIES);
                    if (pi != null) {
                        mDeviceCrashInfo.put(VERSION_NAME,
                                pi.versionName == null ? "not set" : pi.versionName);
                        mDeviceCrashInfo.put(VERSION_CODE, ""+pi.versionCode);
                    }
                    EmailUtil.sendEmail(null,"普罗爵士 "+"VersionName="+pi.versionName+" "+"VersionCode="+pi.versionCode,msg.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                /*Toast toast = Toast.makeText(mContext, "程序出错，即将退出:\r\n" + msg,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
//              MsgPrompt.showMsg(mContext, "程序出错啦", msg+"\n点确认退出");
                Looper.loop();*/

            }
        }.start();

        //创建消息
//        collectCrashDeviceInfo(mContext);
        //保存错误报告文件
//        saveCrashInfoToFile(ex);
        //发送锗误报告到服务器，暂时没有用到，以后可以加上 //
//        sendCrashReportsToServer(mContext);
        return true;
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     * @param ctx
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            for (String fileName : sortedFiles) {
                File cr = new File(ctx.getFilesDir(), fileName);
                postReport(cr);
                cr.delete();// 删除已发送的报告
            }
        }
    }
    private void postReport(File file) {
        // TODO 发送错误报告到服务器
//        1.简单发送没有附件和抄送
        /*Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:1948421773@qq.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
        data.putExtra(Intent.EXTRA_TEXT, "这是内容");
        mContext.startActivity(data);*/

//        2.多个附件 可抄送 发送邮件
       /* Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        String[] tos = { "way.ping.li@gmail.com" };
        String[] ccs = { "way.ping.li@gmail.com" };
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_CC, ccs);
        intent.putExtra(Intent.EXTRA_TEXT, "body");
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        imageUris.add(Uri.parse("file:///mnt/sdcard/a.jpg"));
        imageUris.add(Uri.parse("file:///mnt/sdcard/b.jpg"));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.setType("image*//*");
        intent.setType("message/rfc882");
        Intent.createChooser(intent, "Choose Email Client");
        mContext.startActivity(intent);*/
    }

    /**
     * 获取错误报告文件名
     * @param ctx
     * @return
     */
    private String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME,
                        pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, ""+pi.versionCode);
            }
        }catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "Error while collect package info", e);
        }
        //使用反射来收集设备信息.在Build类中包含各种设备信息,
        //例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        //具体信息请参考后面的截图
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), ""+field.get(null));
                if (DEBUG) {
//                    Log.d(TAG, field.getName() + " : " + field.get(null));
                }
            } catch (Exception e) {
//                Log.e(TAG, "Error while collect crash info", e);
            }}}
    /**
     * 保存错误信息到文件中
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put("EXEPTION", ex.getLocalizedMessage());
        mDeviceCrashInfo.put(STACK_TRACE, result);
        try {

            //long timestamp = System.currentTimeMillis();
            Time t = new Time("GMT+8");
            t.setToNow(); // 取得系统时间
            int date = t.year * 10000 + t.month * 100 + t.monthDay;
            int time = t.hour * 10000 + t.minute * 100 + t.second;
            String fileName = "crash-" + date + "-" + time + CRASH_REPORTER_EXTENSION;
            FileOutputStream trace = mContext.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            mDeviceCrashInfo.store(trace, "");
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
//            Log.e(TAG, "an error occured while writing report file...", e);
        }
        return null;
    }
}