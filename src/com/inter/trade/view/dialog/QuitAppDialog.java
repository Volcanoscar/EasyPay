/*
 * @Title:  SmsCodeDialog.java
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * @Description:  TODO<请描述此文件是做什么的>
 * @author:  MeiYi
 * @data:  2014年7月23日 下午1:36:17
 * @version:  V1.0
 */
package com.inter.trade.view.dialog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.inter.trade.R;
import com.inter.trade.view.styleddialog.SimpleDialogFragment;

/**
 * 忘记手势密码dialog
 * 
 * @author ChenGuangChi
 * @data: 2014年7月23日 下午1:36:17
 * @version: V1.0
 */
public class QuitAppDialog extends SimpleDialogFragment {
	private final static String TAG = "esaypay";
	private QuitAppListener listener;

	
	public void show(FragmentActivity activity, QuitAppListener listener) {
		this.listener = listener;
		show(activity.getSupportFragmentManager(), TAG);
	}

	@Override
	protected Builder build(Builder builder) {
		builder.setMessage("确定退出手机通付宝?");
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					dismiss();
					if(listener!=null){
						listener.onPositive();
					}
			}
		});
		return builder;
	}

	public interface QuitAppListener {
		void onPositive();
	}

}
