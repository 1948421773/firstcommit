package com.musicdo.musicshop.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.musicdo.musicshop.R;

/**
 * 描述:
 * 作者：haiming on 2017/8/25 13:34
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class LoadingDialog extends ProgressDialog {
    private ProgressBar Prprogressbar;
    public LoadingDialog(Context context)
    {
        super(context);
    }

    public LoadingDialog(Context context, int theme)
    {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.brogressbar_loading);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        Prprogressbar=(ProgressBar)findViewById(R.id.progressbar);
    }

    @Override
    public void show()
    {
        super.show();
    }
}
