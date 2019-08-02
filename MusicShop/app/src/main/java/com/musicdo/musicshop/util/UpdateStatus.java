package com.musicdo.musicshop.util;

/**
 * Created by Yuedu on 2017/12/30.
 * @author wenjie
 *	检测版本的状态类
 */

public class UpdateStatus {
    /**
     * 没有新版本
     */
    public final static int NO = 1;

    /**
     * 有新版本
     */
    public final static int YES = 2;

    /**
     * 链接超时
     */
    public final static int TIMEOUT = 3;

    /**
     * 没有wifi
     */
    public final static int NOWIFI = 4;

    /**
     * 数据解析出错
     */
    public final static int ERROR = -1;

}
