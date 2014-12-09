package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.star.talk.startalk.viewholder.ShareLinkViewHolder;
import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;

@ViewLayoutId(R.layout.m_settings)
public class SettingsActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, SettingsActy.class));
	}

	@ViewId(R.id.m_settings_content)
	private ViewGroup mContentView;
	@ViewId(R.id.m_settings_title_left_btn_back)
	private ImageButton mBtnBack;

	@ViewId(R.id.m_settings_item_comment)
	private ViewGroup mItemComment;
	@ViewId(R.id.m_settings_item_invite_friends)
	private ViewGroup mItemInviteFrds;
	@ViewId(R.id.m_settings_item_feedback)
	private ViewGroup mItemFeedback;
	@ViewId(R.id.m_settings_item_contribute)
	private ViewGroup mItemContribute;
	@ViewId(R.id.m_settings_item_site)
	private ViewGroup mItemSite;
	@ViewId(R.id.m_settings_item_agreement)
	private ViewGroup mItemAgreement;
	@ViewId(R.id.m_settings_item_about_us)
	private ViewGroup mItemAboutUs;

	@ViewId(R.id.m_settings_btn_exit)
	private Button mBtnExit;

	private ShareLinkViewHolder mShareLinkViewHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mItemComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				App.openAppMarketsAtMyDetails(SettingsActy.this, 0);
			}
		});
		mItemInviteFrds.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mShareLinkViewHolder = ShareLinkViewHolder.ensureLinkShared(SettingsActy.this, (ViewGroup)mContentView.getParent(), mOnCompleteClick, false);
			}
		});
		mItemFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FeedbackActy.startMe(SettingsActy.this);
			}
		});
		mItemContribute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContributeActy.startMe(SettingsActy.this);
			}
		});
		mItemSite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OfficialSiteActy.startMe(SettingsActy.this);
			}
		});
		mItemAgreement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AgreementActy.startMe(SettingsActy.this);
			}
		});
		mItemAboutUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutUsActy.startMe(SettingsActy.this);
			}
		});
		mBtnExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				App.exitForRelogin();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mShareLinkViewHolder != null) mShareLinkViewHolder.onResume();
	}

	@Override
	protected void onPause() {
		if (mShareLinkViewHolder != null) mShareLinkViewHolder.onPause();
		super.onPause();
	}

	private final OnClickListener mOnCompleteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mShareLinkViewHolder = null;
		}
	};

	@Override
	public void onBackPressed() {
		if (mShareLinkViewHolder != null) {
			mShareLinkViewHolder.destroy();
			mShareLinkViewHolder = null;
			return;
		}
		super.onBackPressed();
	}
}
