/*
 * Copyright (C) 2014 Wei Chou (weichou2010@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.star.talk.startalk.thirdapi;

import android.content.Context;
import android.widget.Toast;

import com.star.talk.startalk.App;
import com.star.talk.startalk.Const;
import com.star.talk.startalk.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.wei.c.Debug;
import com.wei.c.L;
import com.wei.c.utils.BitmapUtils;

/**
 * @author 周伟 Wei Chou(weichou2010@gmail.com)
 */
public class WeChat {
	public static final int THUMB_SIZE = 300;
	public static final int THUMB_DATA_SIZE = 32 * 1024;	//32kb
	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	private static IWXAPI sWxApi;
	private static int sWxSdkVersion;

	private static IWXAPI getWxApi() {
		if (sWxApi == null) {
			sWxApi = WXAPIFactory.createWXAPI(App.get(), Const.ThirdAccount.WECHAT_APP_ID, !Debug.DEBUG);	//第三个参数表示是否检查签名
			sWxApi.registerApp(Const.ThirdAccount.WECHAT_APP_ID);
		}
		return sWxApi;
	}

	/**
	 * 分享表情
	 * @param context
	 * @param title
	 * @param description
	 * @param thumbPngPath
	 * @param gifPath
	 * @param toCicle 是否分享到朋友圈，注意有些版本的微信不支持分享到朋友圈
	 */
	public static final void shareAsEmoji(Context context, String title, String description, String thumbPngPath, String gifPath, boolean toCicle) {
		L.d(WeChat.class, "shareAsEmoji-----");
		if (!checkOperationSupport(context, toCicle)) return;

		WXEmojiObject emoji = new WXEmojiObject();
		emoji.emojiPath = gifPath;

		WXMediaMessage msg = new WXMediaMessage(emoji);
		msg.title = title;
		msg.description = description;
		msg.thumbData = BitmapUtils.compressToSize(BitmapUtils.readImage(thumbPngPath, THUMB_SIZE, THUMB_SIZE), THUMB_DATA_SIZE, true);	//FileUtils.readBytesFromFile(thumbPngPath, 0, -1);
		L.d(WeChat.class, "shareAsEmoji-----msg.thumbData:" + (msg.thumbData == null ? null : "length:" + msg.thumbData.length));

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("emoji");
		req.message = msg;
		req.scene = toCicle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		getWxApi().sendReq(req);
	}

	/**
	 * @param context
	 * @param toCicle	是否分享到微信朋友圈
	 * @return
	 */
	public static boolean checkOperationSupport(Context context, boolean toCicle) {
		if (toCicle && !isWXAppSupportAPI(context)) {
			L.d(WeChat.class, "wxSdkVersion = " + Integer.toHexString(sWxSdkVersion) + "\ntimeline not supported");
			Toast.makeText(context, R.string.third_api_wechat_not_supported_version, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	public static boolean isWXAppSupportAPI(Context context) {
		sWxSdkVersion = getWxApi().getWXAppSupportAPI();
		return sWxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
	}

	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
