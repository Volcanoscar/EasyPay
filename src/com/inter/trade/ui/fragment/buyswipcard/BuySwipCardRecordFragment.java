package com.inter.trade.ui.fragment.buyswipcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.inter.protocol.ProtocolData;
import com.inter.protocol.ProtocolRsp;
import com.inter.protocol.util.ProtocolUtil;
import com.inter.trade.R;
import com.inter.trade.adapter.BuySwipCardRecordAdapter;
import com.inter.trade.data.CommonData;
import com.inter.trade.data.ResponseData;
import com.inter.trade.error.ErrorUtil;
import com.inter.trade.log.Logger;
import com.inter.trade.net.HttpUtil;
import com.inter.trade.ui.DetialActivity;
import com.inter.trade.ui.MyListView;
import com.inter.trade.ui.MyListView.OnPullDownListener;
import com.inter.trade.ui.MyListView.OnRefreshListener;
import com.inter.trade.ui.fragment.BaseFragment;
import com.inter.trade.ui.fragment.buyswipcard.util.BuySwipCardRecordData;
import com.inter.trade.ui.fragment.buyswipcard.util.BuySwipCardRecordParser;
import com.inter.trade.ui.fragment.coupon.BuySuccessFragment;
import com.inter.trade.ui.fragment.transfer.RecordDetialFragment;
import com.inter.trade.util.Constants;
import com.inter.trade.util.LoginUtil;
import com.inter.trade.util.PromptUtil;

/**
 * 购买刷卡器记录Fragment
 * @author zhichao.huang
 *
 */
public class BuySwipCardRecordFragment extends BaseFragment implements OnClickListener,OnItemClickListener {
	private Button cridet_back_btn;
	private ArrayList<BuySwipCardRecordData> mList = new ArrayList<BuySwipCardRecordData>();
	private MyListView mListView; 
	private RecordTask mRecordTask;
	
	private int mStartIndex=0;//起始索引
	private int mTotalCount=0;//订单总条数
	private int mLoadedCount=0;//当前加载的总条数
	private boolean isMore=false;
	
	BuySwipCardRecordAdapter mAdapter;
	public static final String TYPE_STRING="TYPE_STRING";
//	private String mType;
	public static BuySwipCardRecordFragment instance(String type){
		BuySwipCardRecordFragment fragment = new BuySwipCardRecordFragment();
		Bundle argsBundle = new Bundle();
		argsBundle.putString(TYPE_STRING, type);
		fragment.setArguments(argsBundle);
		return fragment;
		
	}
	
	
	public BuySwipCardRecordFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("历史记录");
		mRecordTask = new RecordTask();
		mRecordTask.execute("");
//		mType = getArguments().getString(TYPE_STRING)==null?TransferType.USUAL_TRANSER:getArguments().getString(TYPE_STRING);
		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(mList.size() == (arg2-1)){
			return;
		}
		BuySwipCardRecordData data = mList.get(arg2-1);
		RecordDetialFragment.mMaps.clear();
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("订单编号", data.orderno);
		RecordDetialFragment.mMaps.add(item);
		
		HashMap<String, String> item1 = new HashMap<String, String>();
		item1.put("产品名称",
				data.orderprodurename
				);
		RecordDetialFragment.mMaps.add(item1);
		
		
		HashMap<String, String> item2 = new HashMap<String, String>();
		item2.put("购买单价", data.orderprice+"元");
		RecordDetialFragment.mMaps.add(item2);
		
		HashMap<String, String> shoukuanbank = new HashMap<String, String>();
		shoukuanbank.put("购买数量",
				data.ordernum+"个"
				);
		RecordDetialFragment.mMaps.add(shoukuanbank);
		
		HashMap<String, String> item11 = new HashMap<String, String>();
		item11.put("运费金额", data.yunmoney);
		RecordDetialFragment.mMaps.add(item11);
		
		HashMap<String, String> fukuanbank = new HashMap<String, String>();
		fukuanbank.put("订单总额",
				data.ordermoney+"元"
				);
		RecordDetialFragment.mMaps.add(fukuanbank);
		
//		HashMap<String, String> item3 = new HashMap<String, String>();
//		item3.put("付款借记卡", CommonActivity.mTransferData.getValue(DaikuanData.fucardno));
//		mActivity.mCommonData.add(item3);
		
		HashMap<String, String> item5 = new HashMap<String, String>();
		item5.put("收货人", data.ordershman);
		RecordDetialFragment.mMaps.add(item5);
		
		HashMap<String, String> item6 = new HashMap<String, String>();
		item6.put("收货电话", data.ordershphone);
		RecordDetialFragment.mMaps.add(item6);
		
		HashMap<String, String> item4 = new HashMap<String, String>();
		item4.put("详细收货地址", data.ordershaddress);
		RecordDetialFragment.mMaps.add(item4);
		
		HashMap<String, String> item7 = new HashMap<String, String>();
		item7.put("支付状态", data.orderpaystatus);
		RecordDetialFragment.mMaps.add(item7);
		
		HashMap<String, String> item8 = new HashMap<String, String>();
		item8.put("订单状态", data.orderstate);
		RecordDetialFragment.mMaps.add(item8);
		
		HashMap<String, String> item9 = new HashMap<String, String>();
		item9.put("物流订单号", data.wlno);
		RecordDetialFragment.mMaps.add(item9);
		
		HashMap<String, String> item10 = new HashMap<String, String>();
		item10.put("物流公司id", data.kdcomanyid);
		RecordDetialFragment.mMaps.add(item10);
		
		
//		HashMap<String, String> item12 = new HashMap<String, String>();
//		item12.put("产品总额", data.ordermoney);
//		RecordDetialFragment.mMaps.add(item12);
		
//		HashMap<String, String> item5 = new HashMap<String, String>();
//		item5.put("手续费", data.feemoney);
//		RecordDetialFragment.mMaps.add(item5);
//		
//		HashMap<String, String> item6 = new HashMap<String, String>();
//		item6.put("刷卡金额", data.allmoney);
//		RecordDetialFragment.mMaps.add(item6);
		
		
		Intent intent = new Intent();
		intent.putExtra("type", "record");
		intent.setClass(getActivity(), DetialActivity.class);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LoginUtil.detection(getActivity());
		View view = inflater.inflate(R.layout.buy_swipcard_record_list, container, false);//R.layout.mm_list
		 mListView = (MyListView) view.findViewById(R.id.mm_listview);
		 mListView.setOnItemClickListener(this);
		 
		 mListView.setOnItemClickListener(this);
		 mListView.setonRefreshListener(onRefreshListener);
			
		mListView.setOnPullDownListener(onPullDownListener);
		mListView.setEnableAutoFetchMore(true);
		setBackVisible();
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle("历史记录");
	}

	OnPullDownListener onPullDownListener = new OnPullDownListener() {
		
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onMore() {
			// TODO Auto-generated method stub
			isMore=true;
			loadMore();
			
		}
	};
	private void loadMore(){
		if(mLoadedCount<mTotalCount){
			mStartIndex = mLoadedCount;
			mRecordTask = new RecordTask();
			mRecordTask.execute("");
		}else{
			mListView.setLastText();
//			mListView.setProgressGone();
//			mListView.setIsFetchMoreing(true);
		}
	}
	
	OnRefreshListener onRefreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			isMore=false;
			mStartIndex=0;
			mRecordTask = new RecordTask();
			mRecordTask.execute("");
		}
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mRecordTask != null){
			mRecordTask.cancel(true);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cridet_back_btn:
			showChuxuka();
			break;

		default:
			break;
		}
	}

	private void showChuxuka() {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.func_container, new BuySuccessFragment());
		transaction.commit();
	}

	public class RecordTask extends AsyncTask<String, Integer, Boolean> {
		ProtocolRsp mRsp = null;
		FragmentActivity mActivity;
		private String mResultString;
		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			PromptUtil.showDialog(getActivity(), getActivity().getResources().getString(R.string.loading));
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				CommonData data = new CommonData();
//				data.putValue("authorid", LoginUtil.mLoginStatus.authorid);
//				if(TransferType.USUAL_TRANSER.equals(mType)){
//					data.putValue("paytype",TYPECLASS.tfmg);	
//				}else {
//					data.putValue("paytype",TYPECLASS.suptfmg);	
//				}
				
				data.putValue("msgstart",mStartIndex+"");
				data.putValue("msgdisplay", Constants.RECORD_DISPLAY_COUNT+"");
				List<ProtocolData> mDatas = ProtocolUtil.getRequestDatas("ApiBuyOderInfo", "readOrderlist",
						data);
				BuySwipCardRecordParser recordParser = new BuySwipCardRecordParser();
				mRsp = HttpUtil.doRequest(recordParser, mDatas);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Logger.e(e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			PromptUtil.dissmiss();
			try {
			if (mRsp == null) {
				PromptUtil.showToast(mActivity, mActivity.getResources().getString(R.string.net_error));
			} else {
				
				if(!isMore){
					mList.clear();
				}
				
					List<ProtocolData> mDatas = mRsp.mActions;
					parserResponse(mDatas);
					
					if(!ErrorUtil.create().errorDeal(LoginUtil.mLoginStatus,getActivity())){
						return;
					}
					
					mAdapter = new BuySwipCardRecordAdapter(getActivity(), mList);
					mListView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					
					if(isMore){
						 mListView.setSelection(mLoadedCount);
					 }
					 isMore=false;
					 mListView.setProgressGone();
					 mListView.setIsFetchMoreing(false);

			}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			mListView.onRefreshComplete();
		}
	}

//	private List<ProtocolData> createRequest(CommonData data) {
//		if(TransferType.USUAL_TRANSER.equals(mType)){
//			return ProtocolUtil.getRequestDatas("ApiPayinfo", "readTransferMoneyglist",
//					data);
//		}else{
//			//超级转账历史纪录
//			return ProtocolUtil.getRequestDatas("ApiPayinfo", "readSupTransferMoneyglist",
//					data);
//		}
//		
//	}

	private void parserResponse(List<ProtocolData> mDatas) {
		ResponseData response = new ResponseData();
		LoginUtil.mLoginStatus.mResponseData = response;
		for (ProtocolData data : mDatas) {
			if (data.mKey.equals(ProtocolUtil.msgheader)) {
				ProtocolUtil.parserResponse(response, data);

			} else if (data.mKey.equals(ProtocolUtil.msgbody)) {

				List<ProtocolData> result = data.find("/result");
				if (result != null) {
					LoginUtil.mLoginStatus.result = result.get(0).mValue;
				}
				
				List<ProtocolData> message = data.find("/message");
				if (message != null) {
					LoginUtil.mLoginStatus.message = result.get(0).mValue;
				}
				
				List<ProtocolData> msgallcount = data.find("/msgallcount");
				if(msgallcount != null){
					mTotalCount = Integer.parseInt(msgallcount.get(0).mValue.trim());
				}
				List<ProtocolData> msgdiscount = data.find("/msgdiscount");
				if(msgdiscount != null){
					mLoadedCount  = Integer.parseInt(msgdiscount.get(0).mValue.trim());
				}
				
				
				List<ProtocolData> aupic = data.find("/msgchild");
				if(aupic==null){
					return;
				}
				for(ProtocolData child:aupic){
					BuySwipCardRecordData recordData = new BuySwipCardRecordData();
					if (child.mChildren != null && child.mChildren.size() > 0) {
						Set<String> keys = child.mChildren.keySet();
						for(String key:keys){
							List<ProtocolData> rs = child.mChildren.get(key);
							for(ProtocolData item: rs){
								if(item.mKey.equals("orderno")){
									recordData.orderno  = item.mValue;
									
								}else if(item.mKey.equals("orderprodurename")){
									recordData.orderprodurename  = item.mValue;
									
								}else if(item.mKey.equals("ordernum")){
									
									recordData.ordernum  = item.mValue;
									
								}else if(item.mKey.equals("orderprice")){
									
									recordData.orderprice  = item.mValue;
									
								}else if(item.mKey.equals("ordermoney")){
									
									recordData.ordermoney  = item.mValue;
									
								}else if(item.mKey.equals("ordershaddress")){
									
									recordData.ordershaddress  = item.mValue;
								}else if(item.mKey.equals("ordershman")){
									
									recordData.ordershman  = item.mValue;
								}
								else if(item.mKey.equals("ordershphone")){
									
									recordData.ordershphone  = item.mValue;
								}else if(item.mKey.equals("orderpaystatus")){
									
									recordData.orderpaystatus  = item.mValue;
								}else if(item.mKey.equals("orderstate")){
									
									recordData.orderstate  = item.mValue;
								}else if(item.mKey.equals("wlno")){
									
									recordData.wlno  = item.mValue;
								}else if(item.mKey.equals("kdcomanyid")){
									
									recordData.kdcomanyid  = item.mValue;
								}else if(item.mKey.equals("yunmoney")){
									
									recordData.yunmoney  = item.mValue;
								}else if(item.mKey.equals("promoney")){
									
									recordData.promoney  = item.mValue;
								}
							}
						}
					}
					
					mList.add(recordData);
				}
			}
		}
	}

}
