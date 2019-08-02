package com.musicdo.musicshop.activity;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.musicdo.musicshop.constant.AppConstants;

/**
 * Activity 基类 所有界面都集成此类
 * Created by Yuedu on 2017/9/12.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
            Bugtags.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
            Bugtags.onPause(this);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //注：回调 3
            Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }
}
