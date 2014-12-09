package com.star.talk.startalk.thirdapi;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.wei.c.L;
import com.wei.c.file.FsSize;
import com.wei.c.framework.AbsActivity;
import com.wei.c.framework.AbsApp;

/**
 * 对{@link ShareSDK}和{@link SMSSDK}的再次封装，所有的回调{@link PlatformActionListener}和{@link EventHandler}都会在UI线程中回调。
 * 
 * @author Wei.Chou
 */
public class ShareSdk {
	/**程序启动的时候调用，以便开始进行统计相关的工作**/
	public static void startWork(Context context) {
		ensureInit(context);
	}

	/**
	 * 参见：
	 * <a href="http://wiki.mob.com/Android_短信SDK集成文档">短信SDK集成文档</a><br/>
	 * <a href="http://wiki.mob.com/Android_短信SDK无GUI接口调用">短信SDK无GUI接口调用</a>
	 * 
	 * @param context
	 * @param appKey
	 * @param appSecret
	 */
	public static void startSMS(Context context, String appKey, String appSecret) {
		ensureSmsInit(context, appKey, appSecret);
	}

	public static void stopSMS() {
		try {
			SMSSDK.unregisterAllEventHandler();
		} catch(Exception e) {
			L.w(ShareSdk.class, e);
		}
	}

	/**终止工作，以便正确作启动次数等相关统计。
	 * 只应该在{@link AbsApp#onExit()}或{@link AbsActivity#onDestroyToExit()}时被调用。**/
	public static void stopWork() {
		try {
			//api文档表示该方法已过时
			//ShareSDK.stopSDK(context);
			ShareSDK.stopSDK();
		} catch(Exception e) {	//文档没有明确说明多次调用或没有initSDK()会出现的情况
			L.w(ShareSdk.class, e);
		}
		stopSMS();
	}

	////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 授权
	 * @param context
	 * @param platformName e.g: {@link SinaWeibo#NAME}
	 * @param paListener 监听，会在UI线程中回调<br/>
	 * 文档说：这个操作回调并不实际带回什么数据，只是通过回调告知外部成功或者失败
	 * （<a href="http://wiki.mob.com/Android_授权/取消授权">Android_授权</a>），<br/>
	 * 文档又说：PlatformActionListener是ShareSDK统一的操作回调。它有三个方法：onComplete、onError和onCancel。分别表示：成功、失败和取消。
	 * 一般来说，“取消”的事件很少出现，但是授权的时候会有；错误的时候，可以通过对其Throwable参数t执行“t.printStackTrace()”可以得到错误的堆栈。
	 * 这个堆栈十分重要，如果您向ShareSDK的客服提交bug，请带上这个堆栈，以便他们查询异常。
	 * 成功的时候，会将操作的结果通过其HashMap<String, Object>参数res进行返回，返回时的res已经解析，可以根据不同平台的api文档，从res中得到返回体的数据。
	 * 注意这三个回调中的action参数可参见{@link Platform#ACTION_AUTHORIZING}等。
	 * <pre>
	 * new PlatformActionListener() {
	 * 	public void onError(Platform platform, int action, Throwable e) {}
	 * 	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {}
	 * 	public void onCancel(Platform platform, int action) {}
	 * }
	 * </pre>
	 */
	public static void authorize(Context context, String platformName, PlatformActionListener paListener) {
		ensureInit(context);
		Platform platform = ShareSDK.getPlatform(context, platformName);
		platform.setPlatformActionListener(getUiThreadActionListener(paListener));
		/* true表示不使用SSO方式授权。注意与assets/ShareSDK.xml配置中的ShareByAppClient相区别，两者试用对象不同。
		 * ShareByAppClient标识是否使用客户端分享，默认是false。只有新浪微博设置为false才能使用一键静默分享，
		 * 其他平台反正都要弹框，那不如弹客户端的框美观些。
		 */
		platform.SSOSetting(false);
		platform.authorize();
		//自定义授权，可能需要更多的权限和调用更多的接口
		//platform.authorize(String[]);
	}

	public static void unauthorize(Context context, String platformName) {
		ensureInit(context);
		Platform platform = ShareSDK.getPlatform(context, platformName);
		if (platform.isValid()) platform.removeAccount();
	}

	/**
	 * 获取用户基本信息。注意授权只是获取access_token和openID等信息。
	 * 
	 * @param context
	 * @param platformName
	 * @param userId 若为null，表示获取授权账户自己的资料；否则为该Id对应的微博用户资料。
	 * @param paListener
	 */
	public static void getUserInfo(Context context, String platformName, String userId, PlatformActionListener paListener) {
		ensureInit(context);
		Platform platform = ShareSDK.getPlatform(context, platformName);
		platform.setPlatformActionListener(getUiThreadActionListener(paListener));
		/* 获取用户的资料，如果account为null，则表示获取授权账户自己的资料。 其结果将通过操作回调paListener返回给外部代码，
		 * 在oncomplete中的hashmap返回数据，然后开发者再自己解析数据，通过打印hashmap的数据看看有哪些数据是你想要的。
		 * weibo.showUser(“3189087725”);	//获取账号为“3189087725”的资料
		 */
		platform.showUser(userId);
	}

	public static Platform getPlatform(Context context, String platformName) {
		ensureInit(context);
		return ShareSDK.getPlatform(context, platformName);
	}

	/**
	 * 第三方分享回调，目前只支持
	 * @param context
	 * @param platformClass 如内部类{@link QQShare}
	 * @param paListener 参见{@link #authorize(Context, String, PlatformActionListener)}中对其解释。
	 * <font color=red>注意在分享的时候，其{@link PlatformActionListener#onComplete(Platform, int, HashMap) onComplete(Platform, int, HashMap)}
	 * 并不是在第三方平台回调的时候触发，而只是表示成功启动了第三方平台分享。</font>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <S extends Share> S share(Context context, Class<S> platformClass, PlatformActionListener paListener) {
		ensureInit(context);
		if (platformClass == SinaWeiboShare.class) {
			Platform platform = ShareSDK.getPlatform(context, SinaWeibo.NAME);
			platform.setPlatformActionListener(getUiThreadActionListener(paListener));
			return (S)new SinaWeiboShare(platform);
		} else if (platformClass == QQShare.class) {
			Platform platform = ShareSDK.getPlatform(context, QQ.NAME);
			platform.setPlatformActionListener(getUiThreadActionListener(paListener));
			return (S)new QQShare(platform);
		} else if (platformClass == QZoneShare.class) {
			Platform platform = ShareSDK.getPlatform(context, QZone.NAME);
			platform.setPlatformActionListener(getUiThreadActionListener(paListener));
			return (S)new QZoneShare(platform);
		} else if (platformClass == WechatShare.class) {
			Platform platform = ShareSDK.getPlatform(context, Wechat.NAME);
			platform.setPlatformActionListener(getUiThreadActionListener(paListener));
			return (S)new WechatShare(platform);
		} else if (platformClass == WechatMomentsShare.class) {
			Platform platform = ShareSDK.getPlatform(context, WechatMoments.NAME);
			platform.setPlatformActionListener(getUiThreadActionListener(paListener));
			return (S)new WechatMomentsShare(platform);
		}
		return null;
	}

	/**
	 * 判断某平台是否已经授权（在ShareSDK中，判断此平台是否授权的方法是isValid，而取消授权的方法是removeAccount）。
	 * @param context
	 * @param platformName e.g: {@link QZone#NAME}
	 * @return
	 */
	public static boolean isAuthorized(Context context, String platformName) {
		ensureInit(context);
		Platform platform = ShareSDK.getPlatform(context, platformName);
		return platform.isValid();
	}

	/**获取授权基本信息。如果是获取用户资料参见{@link #getUserInfo()}**/
	public static PlatformDb getAuthorizeData(Context context, String platformName) {
		ensureInit(context);
		PlatformDb db = ShareSDK.getPlatform(context, platformName).getDb();
		/*db.getUserId();
		db.getUserName();
		db.getUserIcon();
		db.getUserGender();
		db.getToken();
		db.getTokenSecret();
		db.getPlatformNname();
		db.getPlatformVersion();
		db.getExpiresTime();
		db.getExpiresIn();*/
		return db;
	}

	/**
	 * ShareSDK同时还允许开发者使用“exportData”和“importData”两个方法，
	 * 批量导出和导入PlatformDb中的数据。开放这两个方法的目的是：
	 * 部分应用具备多用户系统，如果同一设备上不同时期要登录多个账户，那么他们需要备份上一个用户的资料。
	 * ShareSDK并不设置多用户系统，但是用户可以通过登录不同用户的时候，批量导出旧用户的资料，
	 * 然后再登录新用户，直到新用户重新登录的似乎，重新导入其数据的方式，实现其多用户系统功能。
	 * 见<a href="http://wiki.mob.com/Android_授权以及授权页面自定义#平台数据库的操作">ShareSDK平台数据库的操作</a>
	 * 
	 * @param context
	 * @param platformName e.g: {@link QZone#NAME}
	 * @return 导出的字符串格式（json或xml等，不确定，也不需要知道）的平台授权数据
	 */
	public static String exportData(Context context, String platformName) {
		ensureInit(context);
		return ShareSDK.getPlatform(context, platformName).getDb().exportData();
	}

	public static void importData(Context context, String platformName, String data) {
		ensureInit(context);
		ShareSDK.getPlatform(context, platformName).getDb().importData(data);
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	private static void ensureInit(Context context) {
		checkIsParamValid(context);
		ShareSDK.initSDK(context.getApplicationContext());
	}

	private static void ensureSmsInit(Context context, String appKey, String appSecret) {
		checkIsParamValid(context);
		SMSSDK.initSDK(context.getApplicationContext(), appKey, appSecret);
	}

	private static void checkIsParamValid(Context context) {
		if (context == null) throw new NullPointerException("参数不能为空");
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 获取短信目前支持的国家列表，在监听中返回。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_GET_SUPPORTED_COUNTRIES}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数类型为
	 * ArrayList(HashMap(String, Object)).
	 * 参见：
	 * <a href="http://wiki.mob.com/Android_短信SDK集成文档">短信SDK集成文档</a><br/>
	 * <a href="http://wiki.mob.com/Android_短信SDK无GUI接口调用">短信SDK无GUI接口调用</a>
	 * 
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void getSupportedCountries(EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.getSupportedCountries();
	}

	/**获取国家组-列表，安照字母排序，如 key=a, values=["阿富汗","阿根廷"...]*/
	public static HashMap<Character, ArrayList<Country>> getGroupedCountryList() {
		HashMap<Character, ArrayList<String[]>> list = SMSSDK.getGroupedCountryList();
		HashMap<Character, ArrayList<Country>> result = new HashMap<Character, ArrayList<Country>>();
		Set<Entry<Character, ArrayList<String[]>>> set = list.entrySet();
		for (Entry<Character, ArrayList<String[]>> entry : set) {
			ArrayList<Country> countries = new ArrayList<Country>();
			for (String[] country : entry.getValue()) {
				countries.add(new Country(country));
			}
			result.put(entry.getKey(), countries);
		}
		return result;
	}

	/**根据国家id获取国家名称区号等信息*/
	public static Country getCountry(String coutryId) {
		return new Country(SMSSDK.getCountry(coutryId));
	}

	/**
	 * 请求获取短信验证码，在监听中返回。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_GET_VERIFICATION_CODE}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数为null.
	 * 
	 * @param country
	 * @param phone
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void getVerificationCode(String country, String phone, EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.getVerificationCode(country, phone);
	}

	/**
	 * 获取手机内部的通信录列表，在监听中返回。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_GET_CONTACTS}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数类型为
	 * ArrayList(HashMap(String,Object)).
	 * 
	 * @param withAvatar 是否带头像
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void getContacts(boolean withAvatar, EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.getContacts(withAvatar);
	}

	/**
	 * 获取应用内新增加的好友数，在监听中返回。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_GET_NEW_FRIENDS_COUNT}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数类型为Integer.
	 * 
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void getNewFriendsCount(EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.getNewFriendsCount();
	}

	/**
	 * 获取应用内的好友列表，在监听中返回。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_GET_FRIENDS_IN_APP}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数类型为
	 * ArrayList(HashMap(String,Object)).
	 * 
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void getFriendsInApp(EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.getFriendsInApp();
	}

	/**
	 * 提交短信验证码，在监听中返回。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_SUBMIT_VERIFICATION_CODE}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数类型为
	 * HashMap(String,Object), 表示校验的手机和国家代码.
	 * 
	 * @param country
	 * @param phone
	 * @param code
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void submitVerificationCode(String country, String phone, String code, EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.submitVerificationCode(country, phone, code);
	}

	/**
	 * 提交应用内的用户资料。
	 * 若{@link EventHandler#afterEvent(int, int, Object)}第一个参数为
	 * {@link SMSSDK#EVENT_SUBMIT_USER_INFO}，第二个参数为
	 * {@link SMSSDK#RESULT_COMPLETE}即为本方法调用的返回，此时第三个参数为null.
	 * 
	 * @param uid
	 * @param nickname
	 * @param avatar
	 * @param country
	 * @param phone
	 * @param eventHandler 会在UI线程中调用
	 */
	public static void submitUserInfo(String uid, String nickname, String avatar, String country, String phone, EventHandler eventHandler) {
		SMSSDK.registerEventHandler(getUiThreadEventHandler(eventHandler));
		SMSSDK.submitUserInfo(uid, nickname, avatar, country, phone);
	}

	public static class Country {
		public final String id;
		public final String name;
		public final String areaNo;

		/**
		 * @param params [国名，区号，ID]
		 */
		private Country(String[] params) {
			name = params[0];
			areaNo = params[1];
			id = params[2];
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	/**不仅是为了节约内存，而且是为了防止同一个传进来的paListener（在{@link #getUiThreadActionListener(PlatformActionListener)}中）被回调多次**/
	private static WeakHashMap<PlatformActionListener, UiPlatformActionListener> sPaListener = new WeakHashMap<PlatformActionListener, UiPlatformActionListener>();
	private static UiPlatformActionListener getUiThreadActionListener(final PlatformActionListener paListener) {
		UiPlatformActionListener result = sPaListener.get(paListener);
		if (result == null) {
			result = new UiPlatformActionListener(paListener);
			sPaListener.put(paListener, result);
		}
		return result;
	}

	private static class UiPlatformActionListener implements PlatformActionListener {
		private WeakReference<PlatformActionListener> mPaListrnerRef;
		private UiPlatformActionListener(PlatformActionListener paListener) {
			mPaListrnerRef = new WeakReference<PlatformActionListener>(paListener);
		}

		@Override
		public void onError(final Platform platform, final int action, final Throwable e) {
			L.e(ShareSdk.class, "platform:" + platform.getName() + ", action:" + parseAction(action), e);
			final PlatformActionListener paListener = mPaListrnerRef.get();
			if (paListener != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						paListener.onError(platform, action, e);
					}
				});
			}
		}

		@Override
		public void onComplete(final Platform platform, final int action, final HashMap<String, Object> res) {
			final PlatformActionListener paListener = mPaListrnerRef.get();
			if (paListener != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						paListener.onComplete(platform, action, res);
					}
				});
			}
		}

		@Override
		public void onCancel(final Platform platform, final int action) {
			L.w(ShareSdk.class, "platform:" + platform.getName() + ", action:" + parseAction(action));
			final PlatformActionListener paListener = mPaListrnerRef.get();
			if (paListener != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						paListener.onCancel(platform, action);
					}
				});
			}
		}
	}

	private static WeakHashMap<EventHandler, UiEventHandler> sEventHandler = new WeakHashMap<EventHandler, UiEventHandler>();
	private static EventHandler getUiThreadEventHandler(final EventHandler eventHandler) {
		UiEventHandler result = sEventHandler.get(eventHandler);
		if (result == null) {
			result = new UiEventHandler(eventHandler);
			sEventHandler.put(eventHandler, result);
		}
		return result;
	}

	private static class UiEventHandler extends EventHandler {
		private WeakReference<EventHandler> mEventHandlerRef;
		private UiEventHandler(EventHandler eventHandler) {
			mEventHandlerRef = new WeakReference<EventHandler>(eventHandler);
		}

		@Override
		public void beforeEvent(final int event, final Object data) {
			//super.beforeEvent(event, data);
			final EventHandler eventHandler = mEventHandlerRef.get();
			if (eventHandler != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						eventHandler.beforeEvent(event, data);
					}
				});
			}
		}

		@Override
		public void afterEvent(final int event, final int result, final Object data) {
			//super.afterEvent(event, result, data);
			if (result == SMSSDK.RESULT_ERROR) L.e(ShareSdk.class, "event:" + parseEvent(event) + ", result:RESULT_ERROR", (Throwable)data);
			final EventHandler eventHandler = mEventHandlerRef.get();
			if (eventHandler != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						eventHandler.afterEvent(event, result, data);
					}
				});
			}
		}

		@Override
		public void onRegister() {
			//super.onRegister();
			final EventHandler eventHandler = mEventHandlerRef.get();
			if (eventHandler != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						eventHandler.onRegister();
					}
				});
			}
		}

		@Override
		public void onUnregister() {
			final EventHandler eventHandler = mEventHandlerRef.get();
			if (eventHandler != null) {
				AbsApp.get().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						eventHandler.onUnregister();
					}
				});
			}
			//super.onUnregister();
		}
	}

	private static String parseAction(int action) {
		switch (action) {
		case Platform.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";
		case Platform.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case Platform.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case Platform.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case Platform.ACTION_SHARE:
			return "ACTION_SHARE";
		case Platform.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case Platform.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		default:
			return null;
		}
	}

	private static String parseEvent(int event) {
		switch (event) {
		case SMSSDK.EVENT_GET_CONTACTS:
			return "EVENT_GET_CONTACTS";
		case SMSSDK.EVENT_GET_FRIENDS_IN_APP:
			return "EVENT_GET_FRIENDS_IN_APP";
		case SMSSDK.EVENT_GET_NEW_FRIENDS_COUNT:
			return "EVENT_GET_NEW_FRIENDS_COUNT";
		case SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES:
			return "EVENT_GET_SUPPORTED_COUNTRIES";
		case SMSSDK.EVENT_GET_VERIFICATION_CODE:
			return "EVENT_GET_VERIFICATION_CODE";
		case SMSSDK.EVENT_SUBMIT_USER_INFO:
			return "EVENT_SUBMIT_USER_INFO";
		case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
			return "EVENT_SUBMIT_VERIFICATION_CODE";
		default:
			return null;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	/**详见文档：<a href="http://wiki.mob.com/Android_不同平台分享内容的详细说明">不同平台分享内容的详细说明</a>*/
	public static abstract class Share {
		private Platform mPlatform;
		protected Share(Platform platform) {
			mPlatform = platform;
		}

		protected void share(ShareParams params) {
			mPlatform.share(params);
		}

		/**允许的最大标题字符数**/
		public abstract int maxTitleLength();
		/**允许的最大文本字符数**/
		public abstract int maxTextLength();
		/**允许的最大地址字符数**/
		public abstract int maxUrlLength();
		/**允许的最大图像尺寸，单位：byte**/
		public abstract int maxImageSize();

		protected void checkTitle(String title) {
			if (maxTitleLength() > 0 && (title == null || title.length() > maxTitleLength())) throw new IllegalArgumentException("标题不能为空或超过" + maxTitleLength() + "个字符");
		}

		protected void checkText(String text) {
			if (maxTextLength() > 0 && (text == null || text.length() > maxTextLength())) throw new IllegalArgumentException("文本不能为空或超过" + maxTextLength() + "个字符");
		}

		protected void checkUrl(String url) {
			if (url != null && maxUrlLength() > 0) {
				if (url.length() > maxUrlLength()) throw new IllegalArgumentException("地址长度不能超过" + maxUrlLength() + "个字符");

			}
		}

		protected void checkImage(String imgPath) {
			if (imgPath != null && maxImageSize() > 0) {
				long len = new File(imgPath).length();
				if (len <= 0 || len > maxImageSize()) throw new IllegalArgumentException("图像大小不能超过" + new FsSize(maxImageSize()).toString());
			}
		}

		/**所有平台允许的最大标题字符数**/
		public static int getMaxTitleLength() {
			int sinaWeiboLen = new SinaWeiboShare(null).maxTitleLength();
			int qqLen = new QQShare(null).maxTitleLength();
			int qzoneLen = new QZoneShare(null).maxTitleLength();
			int wechatLen = new WechatShare(null).maxTitleLength();
			int wechatMomentsLen = new WechatMomentsShare(null).maxTitleLength();

			return Math.min(Math.min(Math.min(Math.min(sinaWeiboLen, qqLen), qzoneLen), wechatLen), wechatMomentsLen);
		}

		/**所有平台允许的最大文本字符数**/
		public static int getMaxTextLength() {
			int sinaWeiboLen = new SinaWeiboShare(null).maxTextLength();
			int qqLen = new QQShare(null).maxTextLength();
			int qzoneLen = new QZoneShare(null).maxTextLength();
			int wechatLen = new WechatShare(null).maxTextLength();
			int wechatMomentsLen = new WechatMomentsShare(null).maxTextLength();

			return Math.min(Math.min(Math.min(Math.min(sinaWeiboLen, qqLen), qzoneLen), wechatLen), wechatMomentsLen);
		}

		/**所有平台允许的最大地址字符数**/
		public static int getMaxUrlLength() {
			int sinaWeiboLen = new SinaWeiboShare(null).maxUrlLength();
			int qqLen = new QQShare(null).maxUrlLength();
			int qzoneLen = new QZoneShare(null).maxUrlLength();
			int wechatLen = new WechatShare(null).maxUrlLength();
			int wechatMomentsLen = new WechatMomentsShare(null).maxUrlLength();

			return Math.min(Math.min(Math.min(Math.min(sinaWeiboLen, qqLen), qzoneLen), wechatLen), wechatMomentsLen);
		}

		/**所有平台允许的最大图像尺寸，单位：byte**/
		public static int getMaxImageSize() {
			int sinaWeiboLen = new SinaWeiboShare(null).maxImageSize();
			int qqLen = new QQShare(null).maxImageSize();
			int qzoneLen = new QZoneShare(null).maxImageSize();
			int wechatLen = new WechatShare(null).maxImageSize();
			int wechatMomentsLen = new WechatMomentsShare(null).maxImageSize();

			return Math.min(Math.min(Math.min(Math.min(sinaWeiboLen, qqLen), qzoneLen), wechatLen), wechatMomentsLen);
		}

		public static String getTitleNotOverMax(Share share, String text) {
			if (text != null && share.maxTitleLength() >= 0 && text.length() > share.maxTitleLength()) {
				text = text.substring(0, share.maxTitleLength());
			}
			return text;
		}

		public static String getTextNotOverMax(Share share, String text, String url) {
			if (text == null && url == null) return null;
			if (url != null) {
				if (share.maxTextLength() >= 0 && url.length() + 1 > share.maxTextLength()) {
					url = null;
				}
			}
			String result = text == null ? "" : text + (url == null ? "" : " " + url);
			if (share.maxTextLength() >= 0 && result.length() > share.maxTextLength()) {	//说明text不为null
				int end = text.length() - (result.length() - share.maxTextLength());
				if (end < 0) {
					url = null;
					end = share.maxTextLength();
				}
				result = end < text.length() ? text.substring(0, end) : text + (url == null ? "" : " " + url);
			}
			return result;
		}
	}

	/**
	 * <pre>
	 * 新浪微博支持分享文字、本地图片、网络图片和经纬度信息
	 * 新浪微博使用客户端分享不会正确回调
	 * 参数说明
	 * text：不能超过140个汉字
	 * image：图片最大5M，仅支持JPEG、GIF、PNG格式
	 * latitude：有效范围:-90.0到+90.0，+表示北纬
	 * longitude：有效范围：-180.0到+180.0，+表示东经
	 * 分享文本	text	latitude(可选)	longitude(可选)
	 * 分享图文	text	imagePath/imageUrl	latitude(可选)	longitude(可选)
	 * <font color=red>如果imagePath和imageUrl同时存在，imageUrl将被忽略。跟其他平台相反。</font>
	 * </pre>
	 */
	public static class SinaWeiboShare extends Share {
		private SinaWeiboShare(Platform platform) {
			super(platform);
		}

		@Override
		public int maxTitleLength() {
			return -1;
		}

		@Override
		public int maxTextLength() {
			return 140;
		}

		@Override
		public int maxUrlLength() {
			return -1;
		}

		@Override
		public int maxImageSize() {
			return 1024*1024*5;
		}

		/**
		 * 本地图片的图文位置分享
		 * @param text 不能超过140个汉字。
		 * @param imgPath 可选。本地图片路径（非私有目录），图片最大5M，仅支持JPEG、GIF、PNG格式。
		 * @param latitude 可选。有效范围:-90.0到+90.0，+表示北纬。
		 * @param longitude 可选。有效范围：-180.0到+180.0，+表示东经。
		 */
		public void shareTextImagePath(String text, String imgPath, float latitude, float longitude) {
			checkText(text);
			checkImage(imgPath);
			cn.sharesdk.sina.weibo.SinaWeibo.ShareParams params = new cn.sharesdk.sina.weibo.SinaWeibo.ShareParams();
			params.text = text;
			params.imagePath = imgPath;
			if (latitude != 0.0f) params.latitude = latitude;
			if (longitude != 0.0f) params.longitude = longitude;
			share(params);
		}

		/**
		 * 网络图片的图文位置分享
		 * @param text 不能超过140个汉字。
		 * @param imgPath 可选。网络图片地址，图片最大5M，仅支持JPEG、GIF、PNG格式。
		 * @param latitude 可选。有效范围:-90.0到+90.0，+表示北纬。
		 * @param longitude 可选。有效范围：-180.0到+180.0，+表示东经。
		 */
		public void shareTextImageUrl(String text, String imgUrl, float latitude, float longitude) {
			checkText(text);
			cn.sharesdk.sina.weibo.SinaWeibo.ShareParams params = new cn.sharesdk.sina.weibo.SinaWeibo.ShareParams();
			params.text = text;
			params.imageUrl = imgUrl;
			params.latitude = latitude;
			params.longitude = longitude;
			share(params);
		}
	}

	/**
	 * <pre>
	 * QQ分享支持图文、音乐分享和QQ授权后分享到腾讯微博
	 * 参数说明
	 * title：最多30个字符
	 * text：最多40个字符
	 * 分享图文	title	titleUrl	text	imagePath/imageUrl
	 * 分享音乐	title	titleUrl	text	imagePath/imageUrl	musicUrl
	 * 分享到微博	x		x			text	imagePath/imageUrl	isShareTencentWeibo
	 * QQ分享图文和音乐，在PC版本的QQ上可能只看到一条连接，因为PC版本的QQ只会对其白名单的连接作截图，如果不在此名单中，则只是显示连接而已。
	 * 如果只分享图片在PC端看不到图片的，只会显示null，在手机端会显示图片和null字段。
	 * </pre>
	 */
	public static class QQShare extends Share {
		private QQShare(Platform platform) {
			super(platform);
		}

		@Override
		public int maxTitleLength() {
			return 30;
		}

		@Override
		public int maxTextLength() {
			return 40;
		}

		@Override
		public int maxUrlLength() {
			return -1;
		}

		@Override
		public int maxImageSize() {
			return -1;
		}

		/**
		 * 本地图片的图文分享
		 * @param title
		 * @param titleUrl 标题超链接
		 * @param text
		 * @param imgPath
		 */
		public void shareTextImagePath(String title, String titleUrl, String text, String imgPath) {
			checkTitle(title);
			checkText(text);
			cn.sharesdk.tencent.qq.QQ.ShareParams params = new cn.sharesdk.tencent.qq.QQ.ShareParams();
			params.title = title;
			params.titleUrl = titleUrl;
			params.text = text;
			params.imagePath = imgPath;
			share(params);
		}

		/**
		 * 网络图片的图文分享
		 * @param title
		 * @param titleUrl 标题超链接
		 * @param text
		 * @param imgUrl
		 */
		public void shareTextImageUrl(String title, String titleUrl, String text, String imgUrl) {
			checkTitle(title);
			checkText(text);
			cn.sharesdk.tencent.qq.QQ.ShareParams params = new cn.sharesdk.tencent.qq.QQ.ShareParams();
			params.title = title;
			params.titleUrl = titleUrl;
			params.text = text;
			params.imageUrl = imgUrl;
			share(params);
		}
	}

	/**
	 * <pre>
	 * QQ空间支持分享文字、图文和分享到腾讯微博
	 * 参数说明
	 * title：最多200个字符
	 * text：最多600个字符
	 * 分享文本		title	titleUrl	text	x					site	siteUrl
	 * 分享图文		title	titleUrl	text	imagePath/imageUrl	site	siteUrl
	 * 分享到腾讯微博	x		x			text	imagePath/imageUrl	isShareTencentWeibo
	 * QQ空间分享时一定要携带title、titleUrl、site、siteUrl，QQ空间本身不支持分享本地图片，因此如果想分享本地图片，
	 * 图片会先上传到ShareSDK的文件服务器，得到连接以后才分享此链接。由于本地图片更耗流量，因此imageUrl优先级高于imagePath。
	 * QQ空间授权分享到腾讯微博时，如果不传图片则为分享文本。
	 * </pre>
	 */
	public static class QZoneShare extends Share {
		private QZoneShare(Platform platform) {
			super(platform);
		}

		@Override
		public int maxTitleLength() {
			return 200;
		}

		@Override
		public int maxTextLength() {
			return 600;
		}

		@Override
		public int maxUrlLength() {
			return -1;
		}

		@Override
		public int maxImageSize() {
			return -1;
		}

		/**
		 * 本地图片的图文分享
		 * @param title		标题（必填，不可为null，可以为""，会显示"来自xxx(site参数)的分享"，可为多个空格则会不显示标题）
		 * @param titleUrl	标题超链接（必填）
		 * @param text		分享内容（必填[ShareSDK没说必填]，不可为null，不可为""，不可为null，可为多个空格则会不显示）
		 * @param imgPath
		 * @param site		网站名称（选填[ShareSDK说必填]，若为null或""则会用app名称作替换，多个空格则会显示空格如"来自   (空格)的分享"）
		 * @param siteUrl	网站超链接（选填[ShareSDK说必填]，其实没用，分享出去的所有超链接都是titleUrl）
		 */
		public void shareTextImagePath(String title, String titleUrl, String text, String imgPath, String site, String siteUrl) {
			checkTitle(title);
			checkText(text);
			cn.sharesdk.tencent.qzone.QZone.ShareParams params = new cn.sharesdk.tencent.qzone.QZone.ShareParams();
			params.title = title;
			params.titleUrl = titleUrl;
			params.text = text;
			params.imagePath = imgPath;
			params.site = site;
			params.siteUrl = siteUrl;
			params.comment = text;
			share(params);
		}

		/**
		 * 网络图片的图文分享
		 * @param title		标题（必填，不可为null，可以为""，会显示"来自xxx(site参数)的分享"，可为多个空格则会不显示标题）
		 * @param titleUrl	标题超链接（必填）
		 * @param text		分享内容（必填[ShareSDK没说必填]，不可为null，不可为""，不可为null，可为多个空格则会不显示）
		 * @param imgUrl
		 * @param site		网站名称（选填[ShareSDK说必填]，若为null或""则会用app名称作替换，多个空格则会显示空格如"来自   (空格)的分享"）
		 * @param siteUrl	网站超链接（选填[ShareSDK说必填]，其实没用，分享出去的所有超链接都是titleUrl）
		 */
		public void shareTextImageUrl(String title, String titleUrl, String text, String imgUrl, String site, String siteUrl) {
			checkTitle(title);
			checkText(text);
			cn.sharesdk.tencent.qzone.QZone.ShareParams params = new cn.sharesdk.tencent.qzone.QZone.ShareParams();
			params.title = title;
			params.titleUrl = titleUrl;
			params.text = text;
			params.imageUrl = imgUrl;
			params.site = site;
			params.siteUrl = siteUrl;
			params.comment = text;
			share(params);
		}
	}

	/**
	 * <pre>
	 * 绕过审核只对微信好友、微信朋友圈有效
	 * 微信分享如果是绕过审核(配置信息BypassApproval属性设置为true为绕过审核),微信朋友圈可以分享单张图片或者图片与文字一起分享，
	 * 微信好友可以进行文字或者单张图片或者文件进行分享,分享回调不会正确回调。
	 * 不绕过审核，微信三个平台中，好友的功能最完整，朋友圈不能分享表情、文件和应用，收藏不能分享表情和应用，表格下以好友为例子：
	 * 参数说明
	 * title：512Bytes以内
	 * text：1KB以内
	 * imageData：10M以内
	 * imagePath：10M以内(传递的imagePath路径不能超过10KB)
	 * imageUrl：10KB以内
	 * musicUrl：10KB以内
	 * url：10KB以内
	 * 分享文本	shareType(SHARE_TEXT)		title	text
	 * 分享图片	shareType(SHARE_IMAGE)		title	text(设置了不会显示,可选参数)	imagePath/imageUrl/imageData
	 * 分享音乐	shareType(SHARE_MUSIC)		title	text						imagePath/imageUrl/imageData	musicUrl	url（消息点击后打开的页面）
	 * 分享视频	shareType(SHARE_VIDEO)		title	text						imagePath/imageUrl/imageData	url（视频网页地址）
	 * 分享网页	shareType(SHARE_WEBPAGE)	title	text						imagePath/imageUrl/imageData	url
	 * 分享应用	shareType(SHARE_APPS)		title	text						imagePath/imageUrl/imageData	filePath（apk文件）	extInfo（应用信息脚本）
	 * 分享文件	shareType(SHARE_FILE)		title	text						imagePath/imageUrl/imageData	filePath
	 * 分享表情	shareType(SHARE_EMOJI)		title	text						imagePath/imageUrl/imageData
	 * 微信并无实际的分享网络图片和分享bitmap的功能，如果设置了网络图片，此图片会先下载到本地，之后再当作本地图片分享，因此延迟较大。
	 * bitmap则好一些，但是由于bitmap并不知道图片的格式，因此都会被当作png编码，再提交微信客户端。
	 * 此外，SHARE_EMOJI支持gif文件，但是如果使用imageData，则默认只是提交一个png图片，因为bitmap是静态图片。
	 * </pre>
	 */
	public static class WechatShare extends Share {
		private WechatShare(Platform platform) {
			super(platform);
		}

		/**512Bytes以内，由于是客户端分享，应该使用unicode编码，因此应该是256个字符*/
		@Override
		public int maxTitleLength() {
			return 256;
		}

		/**1KB以内，由于是客户端分享，应该使用unicode编码，因此应该是512个字符*/
		@Override
		public int maxTextLength() {
			return 512;
		}

		@Override
		public int maxUrlLength() {
			return 5120;
		}

		@Override
		public int maxImageSize() {
			return 1024*1024*10;
		}

		/**并不能带标题和文本，只能在分享链接的时候或分享给好友的时候**/
		public void shareImage(String imgPath) {
			checkImage(imgPath);
			cn.sharesdk.wechat.friends.Wechat.ShareParams params = new cn.sharesdk.wechat.friends.Wechat.ShareParams();
			params.shareType = Wechat.SHARE_IMAGE;
			params.imagePath = imgPath;
			share(params);
		}

		public void shareWebPageWithText(String title, String text, String imgPath, String pageUrl) {
			checkTitle(title);
			checkText(text);
			checkUrl(pageUrl);
			cn.sharesdk.wechat.friends.Wechat.ShareParams params = new cn.sharesdk.wechat.friends.Wechat.ShareParams();
			params.shareType = Wechat.SHARE_WEBPAGE;
			params.title = title;
			params.text = text;
			params.imagePath = imgPath;
			params.url = pageUrl;
			share(params);
		}
	}

	/**不能分享表情、文件和应用，其他同{@link WechatShare}**/
	public static class WechatMomentsShare extends WechatShare {
		private WechatMomentsShare(Platform platform) {
			super(platform);
		}

		@Override
		public void shareImage(String imgPath) {
			checkImage(imgPath);
			cn.sharesdk.wechat.moments.WechatMoments.ShareParams params = new cn.sharesdk.wechat.moments.WechatMoments.ShareParams();
			params.shareType = WechatMoments.SHARE_IMAGE;
			params.imagePath = imgPath;
			share(params);
		}

		/**标题字段将显示到内容中，而text字段只会出现在分享的对话框的图片右边，而最终不会显示到朋友圈，为保险起见，两者可以设置为一样**/
		@Override
		public void shareWebPageWithText(String title, String text, String imgPath, String pageUrl) {
			checkTitle(title);
			checkUrl(pageUrl);
			cn.sharesdk.wechat.moments.WechatMoments.ShareParams params = new cn.sharesdk.wechat.moments.WechatMoments.ShareParams();
			params.shareType = WechatMoments.SHARE_WEBPAGE;
			params.title = title;
			params.text = text;
			params.imagePath = imgPath;
			params.url = pageUrl;
			share(params);
		}
	}
}
