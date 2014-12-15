package com.star.talk.startalk;

import java.io.File;
import java.util.List;

import net.tsz.afinal.FinalHttp;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.star.talk.startalk.thirdapi.ShareSdk;
import com.tisumoon.exception.TokenNotValidException;
import com.umeng.message.PushAgent;
import com.wei.c.Debug;
import com.wei.c.L;
import com.wei.c.exception.FileCreateFailureException;
import com.wei.c.exception.SdCardNotMountedException;
import com.wei.c.exception.SdCardNotValidException;
import com.wei.c.file.FStoreLoc;
import com.wei.c.file.FStoreLoc.DirLevel;
import com.wei.c.framework.AbsApp;
import com.wei.c.phone.Device;
import com.wei.c.phone.Network;
import com.wei.c.phone.Storage;

public class App extends AbsApp {
	private boolean mExitForRestart = false;

	@Override
	public void onCreate() {
		//TODO
		Debug.DEBUG = false;
		if (Debug.DEBUG) PushAgent.getInstance(this).setDebugMode(true);

		super.onCreate();
		ShareSdk.startWork(this);
		FStoreLoc.BIGFILE.setBaseDirName(this, Const.APP_DIR_NAME);
		FStoreLoc.BIGFILE.switchTo(this, Storage.CARD_EXT);
	}

	@Override
	protected boolean onExit() {
		L.i(this, "程序正常退出------App.onExit()");
		ShareSdk.stopWork();
		super.onExit();
		if (mExitForRestart) {
			WelcomeActy.startMe(this);
			mExitForRestart = false;
		}
		return false;
	}

	/**是否必须重新展示当前版本的引导页**/
	public static boolean mustReShowGuide() {
		return true;
	}

	public static void exitForRelogin() {
		App app = App.get();
		UID.updateToken(app, null);
		app.mExitForRestart = true;
		app.exit();
	}

	public static void openGooglePlay() {
		Intent i = new Intent("com.google.android.finsky.VIEW_MY_DOWNLOADS");
		i.setComponent(new ComponentName("com.android.vending","com.android.vending.AssetBrowserActivity"));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		get().startActivity(i);
	}

	public static void openAppMarketsAtMyDetails(Activity activity, int requestCode) {
		String uri = "market://details?id=" + get().getPackageName();
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(uri));
		//如果没有安装任何应用市场，则startActivity()会报错
		//PackageManager.MATCH_DEFAULT_ONLY用于非Launcher但是可以接受data的Intent.CATEGORY_DEFAULT
		List<ResolveInfo> infos = get().getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
		if (infos != null && infos.size() > 0) {
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	//加这句的话，App还没启动，就会收到onActivityResult()
			try {
				activity.startActivityForResult(i, requestCode);
			} catch (Exception e) {
				L.e(App.class, e);
			}
		}
	}

	public static void openAppMarkets() {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory("android.intent.category.APP_MARKET");	//Intent.CATEGORY_APP_MARKET
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		get().startActivity(i);
	}

	public static boolean isLogined() {
		try {
			UID.nextToken(get());
			return true;
		} catch (TokenNotValidException e) {}
		return false;
	}

	private static String getNextUidToken() throws TokenNotValidException {
		try {
			return UID.nextToken(get());
		} catch (TokenNotValidException e) {
			L.e(get(), e);
			exitForRelogin();
			throw e;
		}
	}

	public static Header[] getHeadersWithUidToken() throws TokenNotValidException {
		return new Header[]{new BasicHeader("token", App.getNextUidToken()),
				new BasicHeader("client", getDeviceVerAndBrand())};
	}

	public static Header[] getHeadersWithNoUidToken() throws TokenNotValidException {
		return new Header[]{new BasicHeader("client", getDeviceVerAndBrand())};
	}

	private static String getDeviceVerAndBrand() {
		Device device = Device.getInstance(get());
		String verAndBrand = device.sysVersion + ", " + device.brand;
		L.i(App.class, verAndBrand);
		return verAndBrand;
	}

	public static File getImagesCacheDir4ThirdApi() throws SdCardNotMountedException, SdCardNotValidException, FileCreateFailureException {
		return FStoreLoc.BIGFILE.getImagesCacheDir(get(), DirLevel.CUSTOM);
	}

	public static File getImagesCacheDirPrivate() throws SdCardNotMountedException, SdCardNotValidException, FileCreateFailureException {
		return FStoreLoc.BIGFILE.getImagesCacheDir(get(), DirLevel.PRIVATE);
	}

	public static String getPicturesSaveDir() {
		return FStoreLoc.BIGFILE.getCustomDirCreatableSdCard(get()).path + File.separator + Environment.DIRECTORY_DCIM + File.separator + Const.APP_DIR_NAME;
	}

	/**检查并返回是否有网络连接，若无，则弹出toast提示**/
	public static boolean checkNetStateAndFeedbackUser() {
		if (!Network.isNetConnected(get())) {
			Toast.makeText(get(), R.string.qmark_common_no_net, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/**打log,弹出toast提示，并返回是否有网络连接**/
	public static boolean handleApiWorkFailureAndFeedbackUser(Class<?> logClazz, ApiType apiType, Throwable t, int errorNo, String strMsg) {
		logApiError(logClazz, t, errorNo, strMsg);
		if (checkNetStateAndFeedbackUser()) {
			int showTextId;
			switch (apiType) {
			case LOGIN:
				showTextId = R.string.m_welcome_login_failure;
				break;
			case COMMIT:
				showTextId = R.string.qmark_common_commit_failure;
				break;
			case FAV_ADD:
				showTextId = R.string.m_favarite_add_failure;
				break;
			case FAV_DELETE:
				showTextId = R.string.m_favarite_delete_failure;
				break;
			default:
				showTextId = R.string.qmark_common_load_failure;
				break;
			}
			Toast.makeText(get(), showTextId, Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}

	public static boolean checkIsJson(String s) {
		try {
			new JSONObject(s);
			return true;
		} catch (JSONException e) {}
		try {
			new JSONArray(s);
			return true;
		} catch (JSONException e) {}
		return false;
	}

	/**检测Api返回的json是否属于错误，是则处理错误。
	 * @return 是否属于错误信息**/
	public static boolean checkApiJsonError(Class<?> logClazz, String json) {
		try {
			com.tisumoon.data.api.Error error = new com.tisumoon.data.api.Error();
			if (error.isBelongToMe(new JSONObject(json))) {
				error = error.fromJson(json);
				if (error.code >= 900000) {	//关于用户身份验证的
					exitForRelogin();
				} else {
					logApiError(logClazz, null, error.code, error.error);
				}
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			L.e(logClazz, json, e);
			return true;
		}
	}

	private static void logApiError(Class<?> logClazz, Throwable t, int errorNo, String strMsg) {
		L.e(logClazz, "errorNo:" + errorNo + ", strMsg:" + strMsg, t);
	}

	public enum ApiType {
		LOGIN, COMMIT, FAV_ADD, FAV_DELETE, DEF
	}

	/**
	 * 返回是否应该加载数据
	 * 
	 * @param empty 页面是否为空
	 * @param showLoading 如果为空，且有网络的情况下是否显示正在加载，若为false则显示点击重新加载
	 * @param 用来显示这些图片的ImageView
	 **/
	public static boolean showLoadingOrNoNet(boolean empty, boolean showLoading, boolean feedbackIfNoNet, ImageView imgState) {
		if (empty) {
			if (feedbackIfNoNet ? checkNetStateAndFeedbackUser() : Network.isNetConnected(get())) {
				if (showLoading) {
					imgState.setVisibility(View.VISIBLE);
					imgState.setImageResource(R.drawable.anim_list_i_pull_2_refresh_header);
					AnimationDrawable anim = (AnimationDrawable)imgState.getDrawable();
					anim.start();
					return true;
				} else {
					imgState.setVisibility(View.VISIBLE);
					imgState.setImageResource(R.drawable.img_common_click_reload);
				}
			} else {
				imgState.setVisibility(View.VISIBLE);
				imgState.setImageResource(R.drawable.img_common_no_net);
			}
		} else {
			imgState.setImageResource(0);
			imgState.setVisibility(View.GONE);
		}
		return false;
	}

	private static FinalHttp sFHttp;
	public static FinalHttp getFHttp() {
		if (sFHttp == null) sFHttp = new FinalHttp();
		return sFHttp;
	}
}
