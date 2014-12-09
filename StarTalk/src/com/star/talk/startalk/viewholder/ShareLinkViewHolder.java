package com.star.talk.startalk.viewholder;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.star.talk.startalk.App;
import com.star.talk.startalk.CategoryActy_v_1;
import com.star.talk.startalk.Const;
import com.star.talk.startalk.R;
import com.star.talk.startalk.data.share.SinaWeiboError;
import com.star.talk.startalk.thirdapi.ShareSdk;
import com.star.talk.startalk.thirdapi.ShareSdk.QQShare;
import com.star.talk.startalk.thirdapi.ShareSdk.QZoneShare;
import com.star.talk.startalk.thirdapi.ShareSdk.Share;
import com.star.talk.startalk.thirdapi.ShareSdk.SinaWeiboShare;
import com.star.talk.startalk.thirdapi.ShareSdk.WechatMomentsShare;
import com.star.talk.startalk.thirdapi.ShareSdk.WechatShare;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.file.FileUtils;
import com.wei.c.framework.ViewHolder;
import com.wei.c.utils.SPref;

@ViewLayoutId(R.layout.m_category_v_1_share_link)
public class ShareLinkViewHolder extends ViewHolder<Void, OnClickListener> {
	private static final String KEY_LINK_SHARED		= "link_shared";

	@ViewId(R.id.m_category_v_1_share_link_window_bg)
	private ViewGroup mWindowBg;
	@ViewId(R.id.m_category_v_1_share_link_content)
	private ViewGroup mContentView;
	@ViewId(R.id.m_category_v_1_share_link_btn_wechat)
	private Button mBtnWechat;
	@ViewId(R.id.m_category_v_1_share_link_btn_wechat_moments)
	private Button mBtnWechatMoments;
	@ViewId(R.id.m_category_v_1_share_link_btn_qq)
	private Button mBtnQQ;
	@ViewId(R.id.m_category_v_1_share_link_btn_qzone)
	private Button mBtnQzone;
	@ViewId(R.id.m_category_v_1_share_link_btn_sina_weibo)
	private Button mBtnSinaWeibo;
	@ViewId(R.id.m_category_v_1_share_link_btn_complete)
	private Button mBtnComplete;

	private Context mContext;
	private OnClickListener mOnCompleteClick;
	private String mShareText;
	private String mAdvertImgPath;
	private String mCurrPlatform;
	private boolean mShareComplete = false, mPaused = false;
	private boolean mOkWechat = false, mOkWechatMoments = false, mOkQQ = false, mOkQzone = false, mOkSinaWeibo = false;

	public ShareLinkViewHolder(View view) {
		super(view);
		mContext = view.getContext();
		mShareText = getView().getResources().getString(R.string.m_category_v_1_share_link_share_text);
	}

	public static ShareLinkViewHolder ensureLinkShared(Activity context, ViewGroup parent, OnClickListener onCompleteClick) {
		return ensureLinkShared(context, parent, onCompleteClick, true);
	}

	public static ShareLinkViewHolder ensureLinkShared(Activity context, ViewGroup parent, OnClickListener onCompleteClick, boolean justOnece) {
		if (justOnece && isLinkShared(context)) return null;
		View view = ShareLinkViewHolder.makeView(ShareLinkViewHolder.class, context.getLayoutInflater(), parent);
		ShareLinkViewHolder vHolder = ShareLinkViewHolder.bindView(0, view, ShareLinkViewHolder.class, null, onCompleteClick);
		parent.addView(view);
		vHolder.startAnimIn();
		return vHolder;
	}

	public void destroy() {
		startAnimOut();
	}

	private void startAnimIn() {
		mWindowBg.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_bg_in));
		mContentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_in));
	}

	private void startAnimOut() {
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_out);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				((ViewGroup)getView().getParent()).removeView(getView());
				mOnCompleteClick.onClick(mBtnComplete);
			}
		});
		mWindowBg.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_bg_out));
		mContentView.startAnimation(anim);
	}

	public static boolean isLinkShared(Context context) {
		return SPref.getSPref(context, ShareLinkViewHolder.class).getBoolean(KEY_LINK_SHARED, false);
	}

	private void setLinkShared() {
		SPref.edit(getView().getContext(), ShareLinkViewHolder.class).putBoolean(KEY_LINK_SHARED, true).commit();
	}

	@Override
	protected void init(OnClickListener... args) {
		mBtnWechat.setOnClickListener(mOnClickListener);
		mBtnWechatMoments.setOnClickListener(mOnClickListener);
		mBtnQQ.setOnClickListener(mOnClickListener);
		mBtnQzone.setOnClickListener(mOnClickListener);
		mBtnSinaWeibo.setOnClickListener(mOnClickListener);
		mOnCompleteClick = args[0];
		mBtnComplete.setText(R.string.m_category_v_1_share_link_btn_cancel);
		mBtnComplete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShared()) setLinkShared();	//至少有一个分享成功
				destroy();
			}
		});
		mWindowBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				destroy();
			}
		});
	}

	@Override
	public void bind(int position, Void data) {
		resetState();
	}

	private boolean isShared() {
		return mOkWechat || mOkWechatMoments || mOkQQ || mOkQzone || mOkSinaWeibo;
	}

	public void onResume() {
		L.d(CategoryActy_v_1.class, "onResume-------------");
		if (mShareComplete) {
			if (mCurrPlatform.equals(SinaWeibo.NAME)) {
				mOkSinaWeibo = true;
			} else if (mCurrPlatform.equals(QZone.NAME)) {
				mOkQzone = true;
			} else if (mCurrPlatform.equals(WechatMoments.NAME)) {
				mOkWechatMoments = true;
			} else if (mCurrPlatform.equals(Wechat.NAME)) {
				mOkWechat = true;
			} else if (mCurrPlatform.equals(QQ.NAME)) {
				mOkQQ = true;
			}
			if (isShared()) mBtnComplete.setText(R.string.m_category_v_1_share_link_btn_complete);
		}
		resetState();
	}

	private void resetState() {
		mCurrPlatform = null;
		mShareComplete = false;
		mPaused = false;
	}

	public void onPause() {
		mPaused = true;
		L.d(CategoryActy_v_1.class, "onPause++++++++++++++");
	}

	private String getAdvertImagePath() {
		if (mAdvertImgPath == null) {
			try {
				String path = App.getImagesCacheDir4ThirdApi().getPath() + File.separator + "linkshare" + File.separator + "link_share_01.png";
				if (FileUtils.copyStreamToFile(mContext.getAssets().open("linkshare" + File.separator + "link_share_01.png"), path)) {
					mAdvertImgPath = path;
				}
			} catch (Exception e) {
				L.e(ShareLinkViewHolder.class, e);
			}
		}
		return mAdvertImgPath;
	}

	private final OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String imgPath = getAdvertImagePath();
			if (imgPath != null) {
				if (v == mBtnSinaWeibo) {
					SinaWeiboShare sina = ShareSdk.share(mContext, SinaWeiboShare.class, mPlatformActionListener);
					sina.shareTextImagePath(Share.getTextNotOverMax(sina, mShareText, Const.APP_SITE_URL), imgPath, 0.0f, 0.0f);
					mCurrPlatform = SinaWeibo.NAME;
				} else if (v == mBtnQzone) {
					QZoneShare qzone = ShareSdk.share(mContext, QZoneShare.class, mPlatformActionListener);
					//注意有空格和无空格的情况，不可随意修改
					//qzone.shareTextImagePath("", Const.APP_SITE_URL, " ", bitmapPath, null, Const.APP_SITE_URL);
					qzone.shareTextImagePath("", Const.APP_SITE_URL, Share.getTextNotOverMax(qzone, mShareText, null), imgPath, null, Const.APP_SITE_URL);
					mCurrPlatform = QZone.NAME;
					//不会正确回调，因此，只要调用成功即视为分享成功
					//mPlatformActionListener.onComplete(ShareSdk.getPlatform(mContext, QZone.NAME), Platform.ACTION_SHARE, null);
				} else if (v == mBtnWechatMoments) {
					WechatMomentsShare wechatMoments = ShareSdk.share(mContext, WechatMomentsShare.class, mPlatformActionListener);
					wechatMoments.shareWebPageWithText(Share.getTitleNotOverMax(wechatMoments, mShareText), "", imgPath, Const.APP_SITE_URL);
					mCurrPlatform = WechatMoments.NAME;
					//不跳页，且不会回调，但是会收到onPause()，只要调用成功即视为分享成功，那么只要在onResume()时认为成功即可
					mShareComplete = true;
				} else if (v == mBtnWechat) {
					WechatShare wechat = ShareSdk.share(mContext, WechatShare.class, mPlatformActionListener);
					wechat.shareWebPageWithText("", Share.getTextNotOverMax(wechat, mShareText, null), imgPath, Const.APP_SITE_URL);
					mCurrPlatform = Wechat.NAME;
					//不会正确回调，但会跳页，因此，只要调用成功即视为分享成功，同上
					mShareComplete = true;
				} else if (v == mBtnQQ) {
					QQShare qq = ShareSdk.share(mContext, QQShare.class, mPlatformActionListener);
					qq.shareTextImagePath("", Const.APP_SITE_URL, mShareText, imgPath);
					mCurrPlatform = QQ.NAME;
				}
			}
		}
	};

	private final PlatformActionListener mPlatformActionListener = new PlatformActionListener() {

		@Override
		public void onError(Platform platform, int action, Throwable e) {
			L.e(CategoryActy_v_1.class, "[onError]platform:" + platform.getName());

			mShareComplete = false;
			if (action == Platform.ACTION_SHARE) {
				if (platform.getName().equals(SinaWeibo.NAME)) {
					if (e != null) {
						SinaWeiboError error = new SinaWeiboError().fromJson(e.getMessage());
						Toast.makeText(mContext, error.error_code == 20016 ?
								R.string.d_m_edit_share_error_sina_weibo_update_too_fast_code_20016 :
									R.string.d_m_edit_share_error_sina_weibo, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mContext, R.string.d_m_edit_share_error_sina_weibo, Toast.LENGTH_LONG).show();
					}
				} else if (platform.getName().equals(QZone.NAME)) {
					//
				} else if (platform.getName().equals(WechatMoments.NAME)) {
					//
				} else if (platform.getName().equals(Wechat.NAME)) {
					//
				} else if (platform.getName().equals(QQ.NAME)) {
					//
				}
			}
		}

		@Override
		public void onComplete(Platform platform, int action, HashMap<String, Object> map) {
			L.d(CategoryActy_v_1.class, "[onComplete]platform:" + platform.getName());

			mShareComplete = true;
			if (action == Platform.ACTION_SHARE) {
				if (platform.getName().equals(SinaWeibo.NAME)) {
					//Platform.isSSODisable()与是否跳客户端没关系
					L.e(CategoryActy_v_1.class, "complete---------------SinaWeibo");
					/* 启用了客户端分享，但是在打开客户端之前或者没有安装客户端而直接进行了微博分享都会回调本方法，
					 * 而我无法分辨这两种情况，没有相关的方法或返回参数。
					 * 但有一个办法：如果是打开客户端，则会先执行onPause(),再执行本方法。so...
					 */
					if(!mPaused) {
						//不跳页，给来个Toast
						Toast.makeText(mContext, R.string.d_m_edit_share_complete_sina_weibo, Toast.LENGTH_LONG).show();
						mOkSinaWeibo = true;
						resetState();
					}
				} else if (platform.getName().equals(QZone.NAME)) {
					//正常跳页且正确回调，但统一放到onStart()
				} else if (platform.getName().equals(WechatMoments.NAME)) {
					//不跳页，且不会回调，但是会弹窗，因此会收到onPause()
					L.e(CategoryActy_v_1.class, "complete---------------WechatMoments");
				} else if (platform.getName().equals(Wechat.NAME)) {
					//跳页，但不会正确回调，返回即视为发布成功
				} else if (platform.getName().equals(QQ.NAME)) {
					//会跳页，会正确回调，但是在跳页之前回调，这里等待跳页回来调用onStart()
				}
			}
		}

		@Override
		public void onCancel(Platform platform, int action) {
			L.w(CategoryActy_v_1.class, "[onCancel]platform:" + platform.getName());

			if (action == Platform.ACTION_SHARE) {
				if (platform.getName().equals(SinaWeibo.NAME)) {
					//
				} else if (platform.getName().equals(QZone.NAME)) {
					//
				} else if (platform.getName().equals(WechatMoments.NAME)) {
					//
				} else if (platform.getName().equals(Wechat.NAME)) {
					//
				} else if (platform.getName().equals(QQ.NAME)) {
					//
				}
			}
		}
	};
}