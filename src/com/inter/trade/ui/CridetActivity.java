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
import android.view.View;

import com.inter.trade.R;
import com.inter.trade.ui.fragment.cridet.CridetCardFragment;
import com.inter.trade.util.Constants;

public class CridetActivity extends FragmentActivity{
	public String mBankNo="";
	public ArrayList<HashMap<String, String>> mCommonData = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.func_layout);
		
//		LoginUtil.detection(this);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add( R.id.func_container, new CridetCardFragment());
		fragmentTransaction.commit();
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, arg2);
		if(resultCode == Constants.ACTIVITY_FINISH
				){
			setResult(Constants.ACTIVITY_FINISH);
			finish();
		}
	}
}
