package com.inter.trade.ui.fragment.telephone;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inter.protocol.ProtocolData;
import com.inter.protocol.ProtocolRsp;
import com.inter.protocol.util.ProtocolUtil;
import com.inter.trade.AsyncLoadWork.AsyncLoadWorkListener;
import com.inter.trade.R;
import com.inter.trade.SwipKeyTask;
import com.inter.trade.data.CardData;
import com.inter.trade.data.ResponseData;
import com.inter.trade.error.ErrorUtil;
import com.inter.trade.net.HttpUtil;
import com.inter.trade.ui.BankListActivity;
import com.inter.trade.ui.DaikuanActivity;
import com.inter.trade.ui.PayApp;
import com.inter.trade.ui.QMoneyPayActivity;
import com.inter.trade.ui.PayApp.SwipListener;
import com.inter.trade.ui.TelephonePayActivity;
import com.inter.trade.ui.creditcard.CommonEasyCreditcardPayActivity;
import com.inter.trade.ui.creditcard.MyBankCardActivity;
import com.inter.trade.ui.creditcard.PayWaysHandler;
import com.inter.trade.ui.creditcard.SmsCodeDialog;
import com.inter.trade.ui.creditcard.SmsCodeDialog.SmsCodeSubmitListener;
import com.inter.trade.ui.creditcard.data.DefaultBankCardData;
import com.inter.trade.ui.creditcard.data.SmsCode;
import com.inter.trade.ui.creditcard.task.GetDefaultTask;
import com.inter.trade.ui.creditcard.task.SmsCodeTask;
import com.inter.trade.ui.creditcard.util.CreditCard;
import com.inter.trade.ui.fragment.BaseFragment;
import com.inter.trade.ui.fragment.returndaikuan.util.DaikuanData;
import com.inter.trade.ui.fragment.telephone.util.MobilePayTask;
import com.inter.trade.ui.fragment.telephone.util.MoblieRechangeData;
import com.inter.trade.ui.fragment.telephone.util.MoblieRechangeNoParser;
import com.inter.trade.ui.fragment.waterelectricgas.data.ResponseStateListener;
import com.inter.trade.util.BankCardUtil;
import com.inter.trade.util.Base64Pay;
import com.inter.trade.util.LoginUtil;
import com.inter.trade.util.NumberFormatUtil;
import com.inter.trade.util.PromptUtil;
import com.inter.trade.util.PromptUtil.NegativeListener;
import com.inter.trade.util.PromptUtil.PositiveListener;
import com.inter.trade.util.TaskUtil;
import com.inter.trade.util.UserInfoCheck;
import com.unionpay.uppayplugin.demo.UnionpayUtil;

@SuppressLint("ValidFragment")
public class TelephonePayConfirmFragment extends BaseFragment implements
		OnClickListener, SwipListener, ResponseStateListener,
		SmsCodeSubmitListener, TextWatcher,OnFocusChangeListener,
		AsyncLoadWorkListener{
	private Button cridet_back_btn;
	private ImageView swip_card;
	private TextView swip_prompt;
	private LinearLayout bank_layout;
	private TextView bank_name;
	private EditText open_name_edit;
	private EditText open_phone_edit;
	private TextView acount;

	private String mBkntno;
	private String mMessage = "";
	private String mResult = "";
	private static double count = 0;
	private static String mCouponId;
	private LinearLayout coupon_layout;

	private BuyTask mBuyTask;
	private DaikuanActivity mActivity;

	private Bundle bundle;

	/*** 处理支付方式的类 */
	private PayWaysHandler viewHandler;

	private GetDefaultTask getDefaultTask;
	
	/***用来保存默认的银行卡*/
	private DefaultBankCardData creditCard;

	/** 银行卡号 */
	private String bankno;

	/** 銀行卡號 */
	private String cardNumber;

	/** 记录信用卡是否来自选择银行卡 */
	private boolean isChooseBank = false;
	
	/**易宝交易请求*/
	private MobilePayTask payTask;
	
	/***
	 * 记录是否从引导页进入到手机充值
	 */
	private boolean isLauchGuide;

	public static TelephonePayConfirmFragment create(double value,
			String couponId) {
		count = value;
		mCouponId = couponId;
		return new TelephonePayConfirmFragment();
	}

	public TelephonePayConfirmFragment() {
	}

	public TelephonePayConfirmFragment(Bundle b) {
		this.bundle = b;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// initReader();
		PayApp.mSwipListener = this;
		if (getActivity() instanceof DaikuanActivity) {
			mActivity = (DaikuanActivity) getActivity();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LoginUtil.detection(getActivity());
		View view = inflater.inflate(R.layout.telephone_swip_layout, container,
				false);
		initView(view);

		setTitle("付款");
		setBackVisible();
		// openReader();

		if (savedInstanceState != null) {
			creditCard = (DefaultBankCardData) savedInstanceState
					.getSerializable("data");
		}
		if (creditCard == null) {
			getDefaultTask = new GetDefaultTask(getActivity(), this);
			getDefaultTask.execute("", "1");
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("data", creditCard);
	}

	private void initSwipPic(boolean flag) {
		viewHandler.setCardImageVisibility(flag);
	}

	private void initView(View view) {
		cridet_back_btn = (Button) view.findViewById(R.id.cridet_back_btn);
		swip_card = (ImageView) view.findViewById(R.id.swip_card);
		swip_prompt = (TextView) view.findViewById(R.id.swip_prompt);
		bank_layout = (LinearLayout) view.findViewById(R.id.bank_layout);
		bank_name = (TextView) view.findViewById(R.id.bank_name);
		acount = (TextView) view.findViewById(R.id.acount);
		open_phone_edit = (EditText) view.findViewById(R.id.open_phone_edit);
		open_name_edit = (EditText) view.findViewById(R.id.open_name_edit);
		coupon_layout = (LinearLayout) view.findViewById(R.id.coupon_layout);
		viewHandler = new PayWaysHandler(getActivity(), this, this, this);
		viewHandler.initView(null, view);
		viewHandler.setDefaultPay(2);

		
		if (bundle != null) {
			((TextView) view.findViewById(R.id.mobile_number)).setText(bundle
					.getString(MoblieRechangeData.MRD_RECHAMOBILE));
			((TextView) view.findViewById(R.id.mobile_rechamobileprov))
					.setText(bundle
							.getString(MoblieRechangeData.MRD_RECHAMOBILEPROV));
			((TextView) view.findViewById(R.id.mobile_rechamoney))
					.setText(NumberFormatUtil.format2(bundle
							.getString(MoblieRechangeData.MRD_RECHAMONEY)) + "元");
			((TextView) view.findViewById(R.id.mobile_rechapaymoney))
					.setText(NumberFormatUtil.format2(bundle
							.getString(MoblieRechangeData.MRD_RECHAPAYMONEY))
							+ "元");
		}

		coupon_layout.setVisibility(View.GONE);

		cridet_back_btn.setOnClickListener(this);
		// bank_layout.setOnClickListener(this);
		isLauchGuide=bundle==null?false:bundle.getBoolean(MoblieRechangeData.ISLAUCHGUIDE);
	}

	/*** 判断刷卡时是否为信用卡 */
	private boolean isCreditCardk = false;
	/*** 判断是否为刷卡得到的卡号 */
	private boolean isSwipCard = false;

	@Override
	public void recieveCard(CardData data) {
		// card_edit.setText(data.pan);
		isSwipCard = true;
		isChooseBank = false;
		bankno=data.pan;
		if ("2".equals(data.trackInfo)) {// 信用卡
			viewHandler.setDefaultPay(2);
			isCreditCardk = true;
		} else if ("23".equals(data.trackInfo)) {// 储蓄卡
			viewHandler.setDefaultPay(2);
			isCreditCardk = false;
		}
		switch (viewHandler.getCheckpay()) {
		case 2:// 信用卡支付
			viewHandler.setCredit(data.pan);
			isChooseBank = false;
			break;
		case 3:// 储蓄卡支付
			viewHandler.setDeposit(data.pan);
			break;

		default:
			break;
		}
	}

	@Override
	public void checkedCard(boolean flag) {
		// TODO Auto-generated method stub
		boolean s = PayApp.openReaderNow();
		log("status : " + s);
		viewHandler.setCardImageVisibility(flag);
	}

	@Override
	public void progress(int status, String message) {
		// TODO Auto-generated method stub
		switch (status) {
		case SWIPING_START:
			PromptUtil.showToast(getActivity(), message);
			break;
		case SWIPING_FAIL:
			PromptUtil.showToast(getActivity(), message);
			break;
		case SWIPING_SUCCESS:
			PromptUtil.showToast(getActivity(), message);
			mKeyTask = new SwipKeyTask(getActivity(),
					   PayApp.mKeyCode, 
					   PayApp.mKeyData, 
					   viewHandler.getCredit(), 
					   "mobilerecharge", this);
			mKeyTask.execute("");

			break;
		default:
			break;
		}

	}

	long time = 0L;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cridet_back_btn:
			long currentTime = System.currentTimeMillis();
			if (currentTime - time < 1000||TaskUtil.isTaskExcuting(payTask)||TaskUtil.isTaskExcuting(mBuyTask)) {//
				PromptUtil.showToast(getActivity(), getResources().getString(R.string.warn_repate_commit));
				return;
			}
			time = System.currentTimeMillis();
			defaultDeal();
			break;
		case R.id.bank_layout:
			showBankList();
			break;
		case R.id.btn_choose_one:// 信用卡 选择我的银行卡
			Intent in1 = new Intent(getActivity(), MyBankCardActivity.class);
			in1.putExtra("type", 1);
			startActivityForResult(in1, 77);
			break;
		case R.id.btn_choose_two:// 储蓄卡 选择我的银行卡
			Intent in2 = new Intent(getActivity(), MyBankCardActivity.class);
			in2.putExtra("type", 2);
			startActivityForResult(in2, 78);
			break;
		default:
			break;
		}
	}

	private void defaultDeal() {
		//刷卡后没有获取到信息
		if (checkInput()) {
			switch (viewHandler.getCheckpay()) {// 1 默认 2 信用卡 3储蓄卡
			case 1:// 选择默认,信用卡直接支付
				directCreditPay(creditCard);
				break;
			case 2://银行卡支付
				if (isChooseBank) {// 选择银行
					if ("creditcard".equals(bank.getBkcardcardtype())) {// 信用卡直接支付
						directCreditPay(bank);
					} else if ("bankcard".equals(bank.getBkcardcardtype())) {// 银联支付
						gotoDeposit();
					}else{
						is16CreditCard();
					}
				} else {
					if(isSwipCard){//刷卡后获取到银行卡信息
						if(bankcard!=null && "creditcard".equals(bankcard.getBkcardcardtype())){//保存了这张卡而且是信用卡
							directCreditPay(bankcard);
						}else if(bankcard!=null && "bankcard".equals(bankcard.getBkcardcardtype())){//储蓄卡直接前往银联
								gotoDeposit();
						}else{
							is16CreditCard();
						}
					}else{
						is16CreditCard();
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	/***根据16位卡号判断是否为信用卡**/
	private void is16CreditCard() {
		String bankno = viewHandler.getCredit();
		if (BankCardUtil.isCreditCard(bankno)) {// 信用卡

			PromptUtil.showTwoButtonDialog("温馨提示",
					"您当前使用卡号为信用卡,是否选择信用卡支付通道?",
					new PositiveListener() {

						@Override
						public void onPositive() {// 信用卡填写页面
							gotoCreditpay();
						}
					}, new NegativeListener() {

						@Override
						public void onNegative() {// 银联支付
							gotoDeposit();
						}
					}, getActivity());
		} else {
			gotoDeposit();
		}
	}

	/**
	 * 直接使用易宝通道支付
	 * 
	 * @throw
	 * @return void
	 */
	private void directCreditPay(DefaultBankCardData bank) {
		if (bank != null) {
			checkInput();
			TelephonePayActivity.mCommonData.clear();
			
			HashMap<String, String> item3 = new HashMap<String, String>();
			item3.put("付款卡号:", TelephonePayActivity.moblieRechangeData
					.getValue(MoblieRechangeData.MRD_RECHABKCARDNO));
			TelephonePayActivity.mCommonData.add(item3);
			
			HashMap<String, String> item37 = new HashMap<String, String>();
			item37.put("付款银行:", bank.getBkcardbank());
			QMoneyPayActivity.mCommonData.add(item37);
			
			HashMap<String, String> item38 = new HashMap<String, String>();
			item38.put("付款人:", bank.getBkcardbankman());
			QMoneyPayActivity.mCommonData.add(item38);
			
			HashMap<String, String> item5 = new HashMap<String, String>();
			item5.put("充值号码:", TelephonePayActivity.moblieRechangeData
					.getValue(MoblieRechangeData.MRD_RECHAMOBILE));
			TelephonePayActivity.mCommonData.add(item5);

//			HashMap<String, String> item8 = new HashMap<String, String>();
//			item8.put("号码归属:", TelephonePayActivity.moblieRechangeData
//					.getValue(MoblieRechangeData.MRD_RECHAMOBILEPROV));
//			TelephonePayActivity.mCommonData.add(item8);

			HashMap<String, String> item6 = new HashMap<String, String>();
			item6.put("充值面额:", NumberFormatUtil
					.format2(TelephonePayActivity.moblieRechangeData
							.getValue(MoblieRechangeData.MRD_RECHAMONEY))+"元");
			TelephonePayActivity.mCommonData.add(item6);

			HashMap<String, String> item7 = new HashMap<String, String>();
			item7.put("支付金额:", NumberFormatUtil
					.format2(TelephonePayActivity.moblieRechangeData
							.getValue(MoblieRechangeData.MRD_RECHAPAYMONEY))+"元");
			TelephonePayActivity.mCommonData.add(item7);



			payTask =new MobilePayTask(getActivity(), this);
			payTask.execute(
					bundle.getString(MoblieRechangeData.MRD_RECHAMONEY),
					bundle.getString(MoblieRechangeData.MRD_RECHAPAYMONEY),
					bundle.getString(MoblieRechangeData.MRD_RECHAMOBILE),
					bank.getBkcardno(), bank.getBkcardbankid(),
					bank.getBkcardidcard(), bank.getBkcardbankphone(),
					bank.getBkcardbankman(), bank.getBkcardyxyear(),
					bank.getBkcardyxmonth(), bank.getBkcardcvv(),
					bundle.getString(MoblieRechangeData.MRD_RECHAMOBILEPROV),
					TelephonePayActivity.moblieRechangeData.sunMap.get(MoblieRechangeData.MRD_PAYCARDID),
					bank.getBkcardbank());
		}
	}

	/**
	 * 前往银联支付
	 * 
	 * @throw
	 * @return void
	 */
	private void gotoDeposit() {
		mBuyTask = new BuyTask();
		mBuyTask.execute("");
	}

	/**
	 * 前往信用卡支付
	 * 
	 * @throw
	 * @return void
	 */
	private void gotoCreditpay() {
		Intent intent = new Intent(getActivity(),
				CommonEasyCreditcardPayActivity.class);
		intent.putExtra(CreditCard.PAY_KEY, CreditCard.MOBILE);
		intent.putExtra(MoblieRechangeData.MRD_RECHAMOBILE,
				bundle.getString(MoblieRechangeData.MRD_RECHAMOBILE));
		intent.putExtra(MoblieRechangeData.MRD_RECHAMOBILEPROV,
				bundle.getString(MoblieRechangeData.MRD_RECHAMOBILEPROV));
		intent.putExtra(MoblieRechangeData.MRD_RECHAMONEY,
				bundle.getString(MoblieRechangeData.MRD_RECHAMONEY));
		intent.putExtra(CreditCard.PAYMONEY,
				bundle.getString(MoblieRechangeData.MRD_RECHAPAYMONEY));
		intent.putExtra(CreditCard.CARDNO,
				TelephonePayActivity.moblieRechangeData.sunMap
						.get(MoblieRechangeData.MRD_RECHABKCARDNO));
		intent.putExtra(MoblieRechangeData.ISLAUCHGUIDE, isLauchGuide);
		startActivityForResult(intent,0);
	}

	private void showBankList() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), BankListActivity.class);
		startActivityForResult(intent, 1);
	}

	private boolean checkInput() {

		if (!viewHandler.isSelected()) {
			PromptUtil.showToast(getActivity(), "请选择一种支付方式");
			return false;
		}
		cardNumber = "";
		switch (viewHandler.getCheckpay()) {
		case 1:
			cardNumber = creditCard.getBkcardno();
			break;
		case 2:
			cardNumber = viewHandler.getCredit();
			if (null == cardNumber || "".equals(cardNumber)) {
				PromptUtil.showToast(getActivity(), "请刷卡或输入卡号");
				return false;
			}
			break;
		case 3:
			cardNumber = viewHandler.getDeposit();
			if (null == cardNumber || "".equals(cardNumber)) {
				PromptUtil.showToast(getActivity(), "请刷卡或输入卡号");
				return false;
			}
			break;

		default:
			break;
		}

		if (!UserInfoCheck.checkBankCard(cardNumber)) {
			PromptUtil.showToast(getActivity(), "卡号格式不正确");
			return false;
		}
		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_RECHABKCARDNO, cardNumber);

		if (bundle == null) {
			PromptUtil.showToast(getActivity(), "操作异常");
			return false;
		}

		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_RECHAMONEY,
				bundle.getString(MoblieRechangeData.MRD_RECHAMONEY));
		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_RECHAPAYMONEY,
				bundle.getString(MoblieRechangeData.MRD_RECHAPAYMONEY));
		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_RECHAMOBILE,
				bundle.getString(MoblieRechangeData.MRD_RECHAMOBILE));
		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_RECHAMOBILEPROV,
				bundle.getString(MoblieRechangeData.MRD_RECHAMOBILEPROV));
		// 支付类型id ，默认为1
		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_RECHAPAYTYPEID, 1 + "");

		Log.i("mobile",
				bundle.getString(MoblieRechangeData.MRD_RECHAMONEY)
						+ "-"
						+ bundle.getString(MoblieRechangeData.MRD_RECHAPAYMONEY)
						+ "-"
						+ bundle.getString(MoblieRechangeData.MRD_RECHAMOBILE)
						+ "-"
						+ bundle.getString(MoblieRechangeData.MRD_RECHAMOBILEPROV));

		// isSwipIn = true;
		if (PayApp.isSwipIn && PayApp.mKeyData.mType == 1) {
			PromptUtil.showToast(getActivity(), PayApp.mKeyData.message);
			return false;
		}

		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.MRD_PAYCARDID,
				PayApp.mKeyCode != null ? PayApp.mKeyCode : "");
		TelephonePayActivity.moblieRechangeData.sunMap.put(
				MoblieRechangeData.merReserved,
				Base64Pay.encode(PayApp.merReserved.toString().getBytes()));
		return true;
	}

	/*** 选择银行返回的数据 */
	private DefaultBankCardData bank;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 78) {// 选择银行
			bank = (DefaultBankCardData) data.getSerializableExtra("bankcard");
			// TODO 填充数据
			bankno = bank.getBkcardno();
			switch (viewHandler.getCheckpay()) {// 1 默认支付 2 信用卡支付 3储蓄卡支付
			case 2:
				viewHandler.setCredit(bankno);
				isChooseBank = true;
				break;
			case 3:
				viewHandler.setDeposit(bankno);
				break;
			default:
				break;
			}

		} else {// 银联支付结果
			payResult(data);
			if (data == null) {
				return;
			}
			String bankid = data.getStringExtra(BankListActivity.BANK_ID);
			String bankname = data.getStringExtra(BankListActivity.BANK_NAME);
			if (null != bankname && !"".equals(bankname)) {
				// isSelectedBank = true;
				DaikuanActivity.mDaikuanData.sunMap.put(DaikuanData.fucardbank,
						bankname);
			}
			bank_name.setText(bankname);
		}

	}

	private void payResult(Intent data) {
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		if (data == null) {
			return;
		}
		String msg = "";
		String str = data.getExtras().getString("pay_result");
		if (null == str) {
			return;
		}
		if (str.equalsIgnoreCase("success")) {
			msg = "支付成功！";
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败！";
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "用户取消了支付";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("支付结果通知");
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		// builder.setCustomTitle();
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// endCallStateService();
		// mobileReader.close();
		// closeReader();
		PayApp.mSwipListener = null;
		if (mBuyTask != null) {
			mBuyTask.cancel(true);
		}
		// cancelTimer();
	}

	@Override
	public void onPause() {
		super.onPause();
		log("onpause endCallStateService");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// mobileReader.open();
		// openReaderNow();
		setTitle("付款");
		initSwipPic(PayApp.isSwipIn);
		// startCallStateService();
	}

	@Override
	public void onStop() {
		super.onStop();
		log("onStop endCallStateService");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		log("onDetach endCallStateService");
	}

	private void showChuxuka() {

		UnionpayUtil.startUnionPlungin(mBkntno, getActivity());
	}

	class BuyTask extends AsyncTask<String, Integer, Boolean> {
		ProtocolRsp mRsp;

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				List<ProtocolData> mDatas = ProtocolUtil.getRequestDatas(
						"ApiMobileRechargeInfoV2", "RequestTransNumber",
						TelephonePayActivity.moblieRechangeData);
				MoblieRechangeNoParser authorRegParser = new MoblieRechangeNoParser();
				mRsp = HttpUtil.doRequest(authorRegParser, mDatas);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mRsp = null;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			PromptUtil.dissmiss();
			if (mRsp == null) {
				PromptUtil.showToast(getActivity(),
						getActivity().getString(R.string.net_error));
			} else {
				try {
					List<ProtocolData> mDatas = mRsp.mActions;
					parserResoponse(mDatas);
					if (!ErrorUtil.create().errorDeal(LoginUtil.mLoginStatus,
							getActivity())) {
						return;
					}
					if (LoginUtil.mLoginStatus.result
							.equals(ProtocolUtil.SUCCESS)) {
						TelephonePayActivity.mCommonData.clear();
						TelephonePayActivity.mBankNo = mBkntno;

						HashMap<String, String> item3 = new HashMap<String, String>();
						item3.put(
								"付款账号:",
								TelephonePayActivity.moblieRechangeData
										.getValue(MoblieRechangeData.MRD_RECHABKCARDNO));
						TelephonePayActivity.mCommonData.add(item3);
						
						HashMap<String, String> item5 = new HashMap<String, String>();
						item5.put(
								"充值号码:",
								TelephonePayActivity.moblieRechangeData
										.getValue(MoblieRechangeData.MRD_RECHAMOBILE));
						TelephonePayActivity.mCommonData.add(item5);

//						HashMap<String, String> item8 = new HashMap<String, String>();
//						item8.put(
//								"号码归属:",
//								TelephonePayActivity.moblieRechangeData
//										.getValue(MoblieRechangeData.MRD_RECHAMOBILEPROV));
//						TelephonePayActivity.mCommonData.add(item8);

						HashMap<String, String> item6 = new HashMap<String, String>();
						item6.put(
								"充值面额:",
								NumberFormatUtil
										.format2(TelephonePayActivity.moblieRechangeData
												.getValue(MoblieRechangeData.MRD_RECHAMONEY))+"元");
						TelephonePayActivity.mCommonData.add(item6);

						HashMap<String, String> item7 = new HashMap<String, String>();
						item7.put(
								"支付金额:",
								NumberFormatUtil
										.format2(TelephonePayActivity.moblieRechangeData
												.getValue(MoblieRechangeData.MRD_RECHAPAYMONEY))+"元");
						TelephonePayActivity.mCommonData.add(item7);

//
//						HashMap<String, String> item = new HashMap<String, String>();
//						item.put("交易请求号:", mBkntno);
//						TelephonePayActivity.mCommonData.add(item);
						showChuxuka();
					} else {
						PromptUtil.showToast(getActivity(),
								LoginUtil.mLoginStatus.message);
					}
				} catch (Exception e) {
					PromptUtil.showToast(getActivity(),
							getString(R.string.req_error));
					e.printStackTrace();
				}

			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PromptUtil.showDialog(getActivity(), getActivity().getResources()
					.getString(R.string.loading));
		}
	}

	private void parserResoponse(List<ProtocolData> params) {
		ResponseData response = new ResponseData();
		LoginUtil.mLoginStatus.mResponseData = response;
		for (ProtocolData data : params) {
			if (data.mKey.equals(ProtocolUtil.msgheader)) {
				ProtocolUtil.parserResponse(response, data);
			} else if (data.mKey.equals(ProtocolUtil.msgbody)) {

				List<ProtocolData> result1 = data.find("/result");
				if (result1 != null) {
					LoginUtil.mLoginStatus.result = result1.get(0).mValue;
				}

				List<ProtocolData> message = data.find("/message");
				if (message != null) {
					LoginUtil.mLoginStatus.message = message.get(0).mValue;
				}

				List<ProtocolData> bkntno = data.find("/bkntno");
				if (bkntno != null) {
					mBkntno = bkntno.get(0).mValue;
				}
			}
		}
	}

	private String orderId;

	@Override
	public void onSuccess(Object obj, Class cla) {

		if (cla.equals(SmsCode.class)) {
			SmsCode sms = (SmsCode) obj;
			if (sms != null) {
				orderId = sms.getOrderId();
//				HashMap<String, String> item = new HashMap<String, String>();
//				item.put("交易请求号:", orderId);
//				TelephonePayActivity.mCommonData.add(item);
				
				if (sms.isNeed()) {// 需要验证码
					SmsCodeDialog dialog = new SmsCodeDialog();
					dialog.show(getActivity(), this);
				} else {// 不需要验证码,表示支付成功
					PromptUtil.showToast(getActivity(), sms.getMessage());
					gotoPaySuccess();
				}
			}
		} else {
			creditCard = (DefaultBankCardData) obj;
			viewHandler.setDefaultBank(creditCard);
			viewHandler.setDefaultVisibility(View.VISIBLE);
			viewHandler.setDefaultPay(1);
			viewHandler.setBankTip();
		}
	}

	@Override
	public void onPositive(String code, SmsCodeDialog dialog) {
		String funName = "ApiMobileRechargeInfoV2";
		String func = "PayWithVerifyCode";
		new SmsCodeTask(getActivity(), new ResponseStateListener() {

			@Override
			public void onSuccess(Object obj, Class cla) {
				PromptUtil.showToast(getActivity(), (String) obj);
				// dialog.dismiss();
				gotoPaySuccess();
			}

		}).execute(orderId, code, funName, func);
	}

	private void gotoPaySuccess() {
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.func_container, TelephonePaySuccessFragment
				.createFragment(TelephonePayActivity.mCommonData,isLauchGuide));
		transaction.commit();
	}

	@Override
	public void onFocusChange(View arg0, boolean isFocus) {
//		if (isFocus) {
//			isSwipCard = false;
//			isChooseBank = false;
//		}
	}
	
	private DefaultBankCardData bankcard;
	

	@Override
	public void onSuccess(Object protocolDataList, Bundle bundle) {
		bankcard = (DefaultBankCardData) protocolDataList;
	}

	@Override
	public void onFailure(String error) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(!(s+"").equals(bankno)){
			isSwipCard = false;
			isChooseBank = false;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
}
