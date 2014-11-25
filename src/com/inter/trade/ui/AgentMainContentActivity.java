package com.inter.trade.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.inter.trade.R;
import com.inter.trade.ui.fragment.agent.AgentMainContentFragment;
import com.inter.trade.ui.fragment.qmoney.util.QMoneyData;
import com.inter.trade.util.Constants;

/**
 * 代理商主界面--登录
 * @author Lihaifeng
 *
 */
public class AgentMainContentActivity extends FragmentActivity{
	public static String mBankNo="";
	public static ArrayList<HashMap<String, String>> mCommonData = new ArrayList<HashMap<String,String>>();
	public static QMoneyData moblieRechangeData = new QMoneyData();
	private boolean isSuccess = false;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.func_layout);
		
//		LoginUtil.detection(this);
		
		Bundle bundle = getIntent().getBundleExtra("AgentMain");
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if(bundle != null){
			transaction.replace(R.id.func_container, new AgentMainContentFragment(bundle));
		}else{
			transaction.replace(R.id.func_container, new AgentMainContentFragment());
		}
		transaction.commit();
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isSuccess){
				setResult(Constants.ACTIVITY_FINISH);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
