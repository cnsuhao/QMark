package com.star.talk.startalk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;

@ViewLayoutId(R.layout.m_abs_site)
public abstract class AbsSiteActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, AbsSiteActy.class));
	}

	@ViewId(R.id.m_abs_site_title_left_btn_back)
	private ImageButton mBtnBack;
	@ViewId(R.id.m_abs_site_title_text)
	private TextView mTitleText;
	@ViewId(R.id.m_abs_site_webview)
	private WebView mWebView;
	private boolean mPageFinished = false;

	public void setTitleStrId(int resId) {
		mTitleText.setText(resId);
	}

	public void setWebsiteUrl(String url) {
		mWebView.loadUrl(url);
	}

	public boolean isPageFinished() {
		return mPageFinished;
	}

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mPageFinished = false;
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mPageFinished = true;
				onWebPageFinished(view, url);
				super.onPageFinished(view, url);
			}
		});
	}

	protected void onWebPageFinished(WebView view, String url) {}

	@Override
	protected void onDestroy() {
		mWebView.destroy();
		super.onDestroy();
	}
}
