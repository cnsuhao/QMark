package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.utils.Manifest;

@ViewLayoutId(R.layout.m_about_us)
public class AboutUsActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, AboutUsActy.class));
	}

	@ViewId(R.id.m_about_us_title_left_btn_back)
	private ImageButton mBtnBack;
	@ViewId(R.id.m_about_us_text_version)
	private TextView mTextVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTextVersion.setText(getString(R.string.m_about_us_version, Manifest.getVersionName(this)));
	}
}
