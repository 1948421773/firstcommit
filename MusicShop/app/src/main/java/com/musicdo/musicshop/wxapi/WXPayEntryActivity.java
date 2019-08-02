package com.musicdo.musicshop.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.activity.MyOrderListActivity;
import com.musicdo.musicshop.activity.OrderActivity;
import com.musicdo.musicshop.activity.PaymentSuccessActivity;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	private Context context;
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		context=this;
		api = WXAPIFactory.createWXAPI(this, AppConstants.APP_ID);
//		setContentView(R.layout.pay_result);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			/*AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.app_tip);
			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			builder.show();*/
			if (resp.errCode==0){
//				ToastUtil.showShort(context,"支付成功");
				Intent intent = new Intent(context, PaymentSuccessActivity.class);
				startActivity(intent);
				MyApplication.getInstance().finishPayActivity();
				finish();
			}else if (resp.errCode==-2){
				ToastUtil.showShort(context,"已取消支付");
				/*
				Intent intent = new Intent(context, MyOrderListActivity.class);
				intent.putExtra("TabIndex", 1);
				startActivity(intent);*/
				finish();
			}else{
				ToastUtil.showShort(context,"支付失败");
				/*
				Intent intent = new Intent(context, MyOrderListActivity.class);
				intent.putExtra("TabIndex", 1);
				startActivity(intent);*/
				finish();
			}
		}
	}
}