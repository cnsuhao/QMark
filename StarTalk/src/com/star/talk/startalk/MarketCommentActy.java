package com.star.talk.startalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wei.c.utils.SPref;

public class MarketCommentActy extends AbsSiteActy {
	private static final String KEY_MARKET_COMMENTED		= "market_commented";
	private static final int REQUEST_CODE	= 0x8001;	//只能使用低16位，否则报错java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode

	public static void startMe(Activity context) {
		context.startActivityForResult(new Intent(context, MarketCommentActy.class), REQUEST_CODE);
	}

	public static boolean isResultBelongsToMeAndOK(int requestCode, int resultCode, Intent data) {
		return requestCode == REQUEST_CODE && resultCode == RESULT_OK;
	}

	public static boolean isMarketCommented(Context context) {
		return SPref.getSPref(context, MarketCommentActy.class).getBoolean(KEY_MARKET_COMMENTED, false);
	}

	public static void setMarketCommented(Context context) {
		SPref.edit(context, MarketCommentActy.class).putBoolean(KEY_MARKET_COMMENTED, true).commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleStrId(R.string.title_m_category_v_1_market_comment);
		setWebsiteUrl(Const.APP_SITE_URL);	//TODO 待修改
	}

	@Override
	protected void onWebPageFinished(android.webkit.WebView view, String url) {
		setMarketCommented(MarketCommentActy.this);
	};

	@Override
	public void finish() {
		setResult(isPageFinished() ? RESULT_OK : RESULT_CANCELED);
		super.finish();
	}
}
