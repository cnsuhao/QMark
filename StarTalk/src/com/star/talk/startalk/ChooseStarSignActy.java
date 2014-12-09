package com.star.talk.startalk;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tisumoon.AbsBaseActivity;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;

@ViewLayoutId(R.layout.m_choose_star_sign)
public class ChooseStarSignActy extends AbsBaseActivity implements OnClickListener {
	@ViewId(R.id.m_choose_star_sign_btn_baiyang)
	private TextView mBaiYang;
	@ViewId(R.id.m_choose_star_sign_btn_jinniu)
	private TextView mJinNiu;
	@ViewId(R.id.m_choose_star_sign_btn_shuangzi)
	private TextView mShuangZi;
	@ViewId(R.id.m_choose_star_sign_btn_juxie)
	private TextView mJuXie;
	@ViewId(R.id.m_choose_star_sign_btn_shizi)
	private TextView mShiZi;
	@ViewId(R.id.m_choose_star_sign_btn_chunv)
	private TextView mChuNv;
	@ViewId(R.id.m_choose_star_sign_btn_tianping)
	private TextView mTianPing;
	@ViewId(R.id.m_choose_star_sign_btn_tianxie)
	private TextView mTianXie;
	@ViewId(R.id.m_choose_star_sign_btn_sheshou)
	private TextView mSheShou;
	@ViewId(R.id.m_choose_star_sign_btn_mojie)
	private TextView mMoJie;
	@ViewId(R.id.m_choose_star_sign_btn_shuiping)
	private TextView mShuiPing;
	@ViewId(R.id.m_choose_star_sign_btn_shuangyu)
	private TextView mShuangYu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBaiYang.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_baiyang)));
		mJinNiu.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_jinniu)));
		mShuangZi.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_shuangzi)));
		mJuXie.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_juxie)));
		mShiZi.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_shizi)));
		mChuNv.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_chunv)));
		mTianPing.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_tianping)));
		mTianXie.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_tianxie)));
		mSheShou.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_sheshou)));
		mMoJie.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_mojie)));
		mShuiPing.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_shuiping)));
		mShuangYu.setText(Html.fromHtml(getResources().getString(R.string.btn_m_choose_star_sign_shuangyu)));

		mBaiYang.setOnClickListener(this);
		mJinNiu.setOnClickListener(this);
		mShuangZi.setOnClickListener(this);
		mJuXie.setOnClickListener(this);
		mShiZi.setOnClickListener(this);
		mChuNv.setOnClickListener(this);
		mTianPing.setOnClickListener(this);
		mTianXie.setOnClickListener(this);
		mSheShou.setOnClickListener(this);
		mMoJie.setOnClickListener(this);
		mShuiPing.setOnClickListener(this);
		mShuangYu.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.m_choose_star_sign_btn_baiyang:

			break;
		case R.id.m_choose_star_sign_btn_jinniu:

			break;
		case R.id.m_choose_star_sign_btn_shuangzi:

			break;
		case R.id.m_choose_star_sign_btn_juxie:

			break;
		case R.id.m_choose_star_sign_btn_shizi:

			break;
		case R.id.m_choose_star_sign_btn_chunv:

			break;
		case R.id.m_choose_star_sign_btn_tianping:

			break;
		case R.id.m_choose_star_sign_btn_tianxie:

			break;
		case R.id.m_choose_star_sign_btn_sheshou:

			break;
		case R.id.m_choose_star_sign_btn_mojie:

			break;
		case R.id.m_choose_star_sign_btn_shuiping:

			break;
		case R.id.m_choose_star_sign_btn_shuangyu:

			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		L.i(this, "onDestroy()--------------");
	}

	@Override
	protected void onDestroyToExit() {
		L.i(this, "onDestroyToExit()");
	}
}
