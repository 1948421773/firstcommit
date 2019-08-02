package com.musicdo.musicshop.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.musicdo.musicshop.bean.VersionInfoBean;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * 名 称 ：SharedPreference、数据处理和MD5加密操作类
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/6/14.
 * 版 本 ：
 * 备 注 ：
 */

public class SpUtils {
    private final static String HEX_NUMS_STR = "0123456789ABCDEF";
    private final static Integer SALT_LENGTH = 12;
    String openid;
    Tencent mTencent;
    BaseUiListener baseUiListener=new BaseUiListener();
    //避免短时间内重复点击
    private static long lastClickTime;
    private final static int SPACE_TIME = 1000;

    public static String getString(Context context, String strKey,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        String result = setPreferences.getString(strKey, "");
        return result;
    }

    public static String getString(Context context, String strKey,
                                   String strDefault,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        String result = setPreferences.getString(strKey, strDefault);
        return result;
    }

    public static void putString(Context context, String strKey, String strData,String spFileName) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putString(strKey, strData);
        editor.commit();
    }

    public static Boolean getBoolean(Context context, String strKey,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, false);
        return result;
    }

    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, strDefault);
        return result;
    }


    public static void putBoolean(Context context, String strKey,
                                  Boolean strData,String spFileName) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.commit();
    }

    public static int getInt(Context context, String strKey,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        int result = setPreferences.getInt(strKey, -1);
        return result;
    }

    public static int getInt(Context context, String strKey, int strDefault,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        int result = setPreferences.getInt(strKey, strDefault);
        return result;
    }

    public static void putInt(Context context, String strKey, int strData,String spFileName) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putInt(strKey, strData);
        editor.commit();
    }

    public static long getLong(Context context, String strKey,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        long result = setPreferences.getLong(strKey, -1);
        return result;
    }

    public static long getLong(Context context, String strKey, long strDefault,String spFileName) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        long result = setPreferences.getLong(strKey, strDefault);
        return result;
    }

    public static void putLong(Context context, String strKey, long strData,String spFileName) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putLong(strKey, strData);
        editor.commit();
    }
    public static void remove(Context context,String spFileName) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        activityPreferences.edit().clear().commit();
    }

    /**
     * 解析省市区json
     * @param context
     * @param fileName
     * @return
     */
    public String getJson(Context context,String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * double转String,保留小数点后两位
     * @param num
     * @return
     */
    public static String doubleToString(double num){
        //使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.00").format(num);
    }

    /**
     * 将16进制字符串转换成数组
     *
     * @return byte[]
     * @author jacob
     * */
    public static byte[] hexStringToByte(String hex) {
        /* len为什么是hex.length() / 2 ?
         * 首先，hex是一个字符串，里面的内容是像16进制那样的char数组
         * 用2个16进制数字可以表示1个byte，所以要求得这些char[]可以转化成什么样的byte[]，首先可以确定的就是长度为这个char[]的一半
         */
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR
                    .indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * 将数组转换成16进制字符串
     *
     * @return String
     * @author jacob
     *
     * */
    public static String byteToHexString(byte[] salt){
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < salt.length; i++) {
            String hex = Integer.toHexString(salt[i] & 0xFF);
            if(hex.length() == 1){
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 密码验证
     * @param passwd 用户输入密码
     * @param dbPasswd 数据库保存的密码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static boolean validPasswd(String passwd, String dbPasswd)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] pwIndb =  hexStringToByte(dbPasswd);
        //定义salt
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(pwIndb, 0, salt, 0, SALT_LENGTH);
        //创建消息摘要对象
        MessageDigest md = MessageDigest.getInstance("MD5");
        //将盐数据传入消息摘要对象
        md.update(salt);
        md.update(passwd.getBytes("UTF-8"));
        byte[] digest = md.digest();
        //声明一个对象接收数据库中的口令消息摘要
        byte[] digestIndb = new byte[pwIndb.length - SALT_LENGTH];
        //获得数据库中口令的摘要
        System.arraycopy(pwIndb, SALT_LENGTH, digestIndb, 0,digestIndb.length);
        //比较根据输入口令生成的消息摘要和数据库中的口令摘要是否相同
        if(Arrays.equals(digest, digestIndb)){
            //口令匹配相同
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获得md5之后的16进制字符
     * @param passwd 用户输入密码字符
     * @return String md5加密后密码字符
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getEncryptedPwd(String passwd)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //拿到一个随机数组，作为盐
        byte[] pwd = null;
        SecureRandom sc = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        sc.nextBytes(salt);

        //声明摘要对象，并生成
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(salt);
        md.update(passwd.getBytes("UTF-8"));
        byte[] digest = md.digest();

        pwd = new byte[salt.length + digest.length];
        System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);
        System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);
        return byteToHexString(pwd);
    }
    /**
     * MD5编码
     * 原始字符串
     * @return 经过MD5加密之后的结果
     */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 隐藏电话号码 用（*）星号代替
     * @param pNumber
     * @return
     */
    public static String setPhoneNumberStart (String pNumber){
        if(!TextUtils.isEmpty(pNumber) && pNumber.length() > 6 ){
            StringBuilder sb  =new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
        }
        return pNumber;
    }

    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void deletePhotoAtPathAndName(String path, String fileName) {
        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
                if (files[i].getName().equals(fileName)) {
                    files[i].delete();
                }
            }
        }
    }

    /*public static String QQAccountAssociation(Activity activity) {
        mTencent = Tencent.createInstance(AppConstants.QQ_APP_ID, activity);
        if (!mTencent.isSessionValid()){
            mTencent.login(activity, "all", baseUiListener);
        }
        return openid;
    }*/

    private class BaseUiListener implements IUiListener {


        @Override
        public void onComplete(Object response) {
//            ToastUtil.showLong(activity,response.toString());
            if (null == response) {
//                ToastUtil.showLong(activity.this,"登录失败");
                return;
            }
//            ToastUtil.showLong(activity, "登录成功");
//            startActivity(MainActivity.newIntent(mContext));//进入教师界面
            // 有奖分享处理
//            handlePrizeShare();
            String openid = null;
            try {
                JSONObject jsonResponse = (JSONObject) response;
                openid=(String) jsonResponse.get("openid");
//                ToastUtil.showLong(context,"BaseUiListener"+jsonResponse);
                if ((int) jsonResponse.get("ret") == 0) {

                    openid = jsonResponse.getString("openid");
                    String accessToken = jsonResponse.getString("access_token");
                    String expires = jsonResponse.getString("expires_in");
                    mTencent.setOpenId(openid);
                    mTencent.setAccessToken(accessToken, expires);
                    /*userInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                    userInfo.getUserInfo(userInfoListener);*/
                }

//                CheckBangDing(openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            doComplete((JSONObject)response);
        }
        protected void doComplete(JSONObject values) {

        }
        @Override
        public void onError(UiError e) {
//            ToastUtil.showLong(AccountAssociationActivity.this, "onError: " +e.errorDetail);
        }

        @Override
        public void onCancel() {
//            ToastUtil.showLong(AccountAssociationActivity.this, "onCancel:");

        }

    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    /**\
     * 计算软件缓存大小
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 照片路径转换 上传路径异常：content://media/external/images/media/539163
     转成：/storage/emulated/0/DCIM/Camera/IMG_20160807_133403.jpg路径；
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 版本更新
     * @param context
     * @return
     */
    public static void UpdateVersionCode(final Context context) {
        UpdateVersionUtil.localCheckedVersion(context, new UpdateVersionUtil.UpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, VersionInfoBean versionInfo) {
                //判断回调过来的版本检测状态
                switch (updateStatus) {
                    case UpdateStatus.YES:
                        //弹出更新提示
                        UpdateVersionUtil.showDialog(context, versionInfo);
                        break;
                    case UpdateStatus.NO:
                        //没有新版本
                        ToastUtil.showShort(context, "已经是最新版本了!");
                        break;
                    case UpdateStatus.NOWIFI:
                        //当前是非wifi网络
                        ToastUtil.showShort(context, "只有在wifi下更新！");
//                          DialogUtils.showDialog(MainActivity.this, "温馨提示","当前非wifi网络,下载会消耗手机流量!", "确定", "取消",new DialogOnClickListenner() {
//                              @Override
//                              public void btnConfirmClick(Dialog dialog) {
//                                  dialog.dismiss();
//                                  //点击确定之后弹出更新对话框
//                                  UpdateVersionUtil.showDialog(SystemActivity.this,versionInfo);
//                              }
//
//                              @Override
//                              public void btnCancelClick(Dialog dialog) {
//                                  dialog.dismiss();
//                              }
//                          });
                        break;
                    case UpdateStatus.ERROR:
                        //检测失败
//                        ToastUtil.showShort(context, "检测失败，请稍后重试！");
                        break;
                    case UpdateStatus.TIMEOUT:
                        //链接超时
                        ToastUtil.showShort(context, "链接超时，请检查网络设置!");
                        break;
                }
            }
        });

    }

    /**
     * 根据
     * 1. "汉字区间"
     * 2. "数字区间"
     * 3. "小写字母区间"
     * 4. "大写字母区间"
     * 过滤掉非法字符的方法
     * @param oldString
     * @return
     */
    public static String filterCharToNormal(String oldString) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = oldString.length();
        for (int i = 0; i < length; i++) {//遍历传入的String的所有字符
            char codePoint = oldString.charAt(i);
            if (//如果当前字符为常规字符,则将该字符拼入StringBuilder
                    ((codePoint >= 0x4e00) && (codePoint <= 0x9fa5)) ||//表示汉字区间
                            ((codePoint >= 0x30) && (codePoint <= 0x39)) ||//表示数字区间
                            ((codePoint >= 0x41) && (codePoint <= 0x5a)) ||//表示大写字母区间
                            ((codePoint >= 0x61) && (codePoint <= 0x7a))) {//小写字母区间
                stringBuilder.append(codePoint);
            } else {//如果当前字符为非常规字符,则忽略掉该字符
                System.out.println("包含特殊字符");
            }
        }
        return stringBuilder.toString();
    }

    public static String filterCharToarticle(String oldString) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = oldString.length();
        for (int i = 0; i < length; i++) {//遍历传入的String的所有字符
            char codePoint = oldString.charAt(i);//如果当前字符为常规字符,则将该字符拼入StringBuilder
            if (((codePoint >= 0x4e00) && (codePoint <= 0x9fa5)) ||//表示汉字区间
                            ((codePoint >= 0x30) && (codePoint <= 0x39)) ||//表示数字区间 :;"?!,.，。
                            ((codePoint >= 0x41) && (codePoint <= 0x5a)) ||//表示大写字母区间
                            ((codePoint == ',') || (codePoint == '.')) ||((codePoint == '，') || (codePoint == '。')) ||
                            ((codePoint == '?') || (codePoint == '？')) ||((codePoint == '!') || (codePoint == '！')) ||
                            ((codePoint == ';') || (codePoint == '：')) ||((codePoint == '“') || (codePoint == '”')) ||
                            ((codePoint == '‘') || (codePoint == '’')) ||((codePoint == ':') || (codePoint == '"')) ||
                            ((codePoint >= 0x61) && (codePoint <= 0x7a))) {//小写字母区间
                stringBuilder.append(codePoint);
            } else {//如果当前字符为非常规字符,则忽略掉该字符
                System.out.println("包含特殊字符");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 价格保留两位小数
     */
    public static String PriceTwoDecimal(String price){
        StringBuilder stringBuilder = new StringBuilder();
        String[] MemberPrices=price.split("\\.");
        if (MemberPrices.length>1){//价格截取显示
            if (MemberPrices[0].equals("0")){
                stringBuilder.append("¥0.");
            }else{
                stringBuilder.append("¥"+MemberPrices[0]);
            }
            if (!MemberPrices[1].equals("0")){
                if (MemberPrices[1].length()==1){
                    stringBuilder.append("."+MemberPrices[1]+"0");
                }else{
                    stringBuilder.append(""+MemberPrices[1]);
                }
            }else{
                stringBuilder.append(".00");
            }
        }else{
            stringBuilder.append("¥"+MemberPrices[0]+".00");
        }
        return stringBuilder.toString();
    }
}
