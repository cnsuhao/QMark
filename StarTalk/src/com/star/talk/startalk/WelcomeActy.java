package com.star.talk.startalk;

import java.util.HashMap;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.tencent.qq.QQ;

import com.star.talk.startalk.App.ApiType;
import com.star.talk.startalk.data.api.Login;
import com.star.talk.startalk.thirdapi.ShareSdk;
import com.star.talk.startalk.utils.FontUtils;
import com.tisumoon.AbsBaseActivity;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.wei.c.Debug;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.phone.Device;
import com.wei.c.widget.DotLoadingView;

@ViewLayoutId(R.layout.m_welcome)
public class WelcomeActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, WelcomeActy.class));
	}

	@ViewId(R.id.m_welcome_img_logo)
	private ImageView mImgLogo;
	@ViewId(R.id.m_welcome_img_market_yingyongbao)
	private ImageView mImgMarketYingYongBao;
	@ViewId(R.id.m_welcome_img_market_baidu)
	private ImageView mImgMarketBaiDu;
	@ViewId(R.id.m_welcome_text_market_baidu)
	private TextView mTextMarketBaiDu;
	@ViewId(R.id.m_welcome_text_test)
	private TextView mTextTest;
	@ViewId(R.id.m_welcome_login_btn_bg)
	private ViewGroup mLoginPanel;
	@ViewId(R.id.m_welcome_login_text_qq)
	private TextView mLoginText;
	@ViewId(R.id.m_welcome_login_loding)
	private DotLoadingView mLoginLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//启动友盟推送服务
		PushAgent pushAgent = PushAgent.getInstance(this);
		if (checkLoginedOrGoToMain(false)) {
			pushAgent.enable();
		} else {
			//由于本页面处理时间较短，所以如果要处理相关回调的话，应该在主Activity中进行。
			pushAgent.enable(new IUmengRegisterCallback() {
				@Override
				public void onRegistered(final String registrationId) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							L.i(WelcomeActy.class, registrationId);
							//L.i(WelcomeActy.class, UmengRegistrar.getRegistrationId(WelcomeActy.this));
						}
					});
				}
			});
		}

		mLoginPanel.setVisibility(View.GONE);
		mImgMarketYingYongBao.setVisibility(View.GONE);
		mImgMarketBaiDu.setVisibility(View.GONE);
		mTextMarketBaiDu.setVisibility(View.GONE);
		mTextTest.setVisibility(Debug.DEBUG ? View.VISIBLE : View.GONE);
		if (Debug.DEBUG) mTextTest.setTypeface(FontUtils.getTypefaceWithCode(this, 1));

		Animation anim = AnimationUtils.loadAnimation(this, R.anim.m_welcome_logo);
		anim.setAnimationListener(mLogoAnimListener);
		mImgLogo.startAnimation(anim);
	}

	private void startLoginBtnAnim() {
		updateLoginBtnState(true);
		mLoginPanel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.m_welcome_btn_login));
	}

	private boolean checkLoginedOrGoToMain(boolean gotoMain) {
		boolean logined = App.isLogined();
		if (logined && gotoMain) {
			//MainTabsActy.startMe(WelcomeActy.this);
			CategoryActy_v_1.startMe(WelcomeActy.this);
			WelcomeActy.this.finish();
		}
		return logined;
	}

	private void updateLoginBtnState(boolean readyForClick) {
		if (readyForClick) {
			mLoginPanel.setOnClickListener(mOnClickListener);
			mLoginText.setText(R.string.m_welcome_login_qq);
			mLoginLoading.setVisibility(View.GONE);
		} else {
			mLoginPanel.setOnClickListener(null);
			mLoginText.setText(R.string.m_welcome_login_qq_loading);
			mLoginLoading.setVisibility(View.VISIBLE);
		}
		mLoginPanel.setVisibility(View.VISIBLE);
	}

	private final AnimationListener mLogoAnimListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationEnd(Animation animation) {
			if (App.isMarketInfoShown()) {
				String appChannel = App.getAppChannel();
				if (appChannel.equalsIgnoreCase("BaiDu")) {
					mImgMarketBaiDu.setVisibility(View.VISIBLE);
					mTextMarketBaiDu.setVisibility(View.VISIBLE);
				} else if (appChannel.equalsIgnoreCase("YingYongBao")) {
					mImgMarketYingYongBao.setVisibility(View.VISIBLE);
				}
			}

			if (GuideActy.isShown(WelcomeActy.this)) {
				if (checkLoginedOrGoToMain(false)) {
					getApp().getMainHandler().postDelayed(new Runnable() {
						@Override
						public void run() {
							checkLoginedOrGoToMain(true);
						}
					}, 1000);
				} else {
					startLoginBtnAnim();
				}
			} else {
				GuideActy.startMe(WelcomeActy.this);
				WelcomeActy.this.finish();
			}
		}
	};

	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!checkLoginedOrGoToMain(true)) {
				ShareSdk.authorize(WelcomeActy.this, QQ.NAME, mPlatformActionListener);
				updateLoginBtnState(false);
			}
		}
	};

	private final PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
		@Override
		public void onComplete(Platform platform, int action, HashMap<String, Object> map) {
			/*
			L.d(WelcomeActy.class, "onComplete---platform:" + platform.getName());
			parseAction(action);
			if (map != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					L.i(WelcomeActy.class, "Map--key:" + entry.getKey() + ", value:" + entry.getValue());
				}
			}*/
			switch (action) {
			case Platform.ACTION_AUTHORIZING:	//认证登录
				if (platform.getName().equals(QQ.NAME)) {
					PlatformDb db = ShareSdk.getAuthorizeData(WelcomeActy.this, platform.getName());
					loginWithQQ(db.getUserId());
					return;
				}
				break;
			case Platform.ACTION_FOLLOWING_USER:
				break;
			case Platform.ACTION_GETTING_FRIEND_LIST:
				break;
			case Platform.ACTION_SENDING_DIRECT_MESSAGE:
				break;
			case Platform.ACTION_SHARE:
				break;
			case Platform.ACTION_TIMELINE:
				break;
			case Platform.ACTION_USER_INFOR:
				break;
			}
			updateLoginBtnState(true);
		}

		@Override
		public void onError(Platform platform, int action, Throwable e) {
			L.e(WelcomeActy.class, "onComplete---platform:" + platform.getName(), e);
			//parseAction(action);
			updateLoginBtnState(true);
		}

		@Override
		public void onCancel(Platform platform, int action) {
			L.w(WelcomeActy.class, "onComplete---platform:" + platform.getName());
			//parseAction(action);
			updateLoginBtnState(true);
		}

		/*private void parseAction(int action) {
			switch (action) {
			case Platform.ACTION_AUTHORIZING:
				L.i(WelcomeActy.class, "ACTION_AUTHORIZING");
				break;
			case Platform.ACTION_FOLLOWING_USER:
				L.i(WelcomeActy.class, "ACTION_FOLLOWING_USER");
				break;
			case Platform.ACTION_GETTING_FRIEND_LIST:
				L.i(WelcomeActy.class, "ACTION_GETTING_FRIEND_LIST");
				break;
			case Platform.ACTION_SENDING_DIRECT_MESSAGE:
				L.i(WelcomeActy.class, "ACTION_SENDING_DIRECT_MESSAGE");
				break;
			case Platform.ACTION_SHARE:
				L.i(WelcomeActy.class, "ACTION_SHARE");
				break;
			case Platform.ACTION_TIMELINE:
				L.i(WelcomeActy.class, "ACTION_TIMELINE");
				break;
			case Platform.ACTION_USER_INFOR:
				L.i(WelcomeActy.class, "ACTION_USER_INFOR");
				break;
			}
		}*/
	};

	private void loginWithQQ(String qqstr) {
		try {
			AjaxParams params = new AjaxParams();
			params.put("qqstr", qqstr);
			params.put("clienttime", String.valueOf(System.currentTimeMillis()));
			params.put("deviceid", Device.getUniqueId(WelcomeActy.this));
			params.put("channel", App.getAppChannel());
			params.put("openid", UID.encrypt(qqstr));

			App.getFHttp().get(Const.Url.login, App.getHeadersWithNoUidToken(), params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					if (!App.checkApiJsonError(WelcomeActy.class, json)) {
						Login login = new Login().fromJson(json);
						UID.updateToken(WelcomeActy.this, login.token);
					}
					addPushAlias();
					checkLoginedOrGoToMain(true);
					updateLoginBtnState(true);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(WelcomeActy.class, ApiType.LOGIN, t, errorNo, strMsg);
					updateLoginBtnState(true);
				}
			});
		} catch (Exception e) {
			L.e(WelcomeActy.class, e);
		}
	}

	private void addPushAlias() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					PlatformDb db = ShareSdk.getAuthorizeData(WelcomeActy.this, QQ.NAME);
					String userName = db.getUserName();
					if (TextUtils.isEmpty(userName)) userName = db.getUserId();
					PushAgent.getInstance(WelcomeActy.this).addAlias(userName, ALIAS_TYPE.QQ);
					L.d(WelcomeActy.class, "addAlias(" + userName + ", ALIAS_TYPE.QQ)------");
				} catch (JSONException e) {
					L.e(WelcomeActy.class, e);
				}
				return null;
			}
		}.execute();
	}
}
