package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.star.talk.startalk.thirdapi.QQ;
import com.star.talk.startalk.utils.FontUtils;
import com.star.talk.startalk.widget.MagicBoardView;
import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;

@ViewLayoutId(R.layout.m_feedback)
public class FeedbackActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, FeedbackActy.class));
	}

	@ViewId(R.id.m_feedback_title_left_btn_back)
	private ImageButton mBtnBack;
	@ViewId(R.id.m_feedback_magic_view)
	private MagicBoardView mMagicView;
	@ViewId(R.id.m_feedback_btn_join_qq_group)
	private Button mBtnJoinQQGroup;
	@ViewId(R.id.m_feedback_btn_to_contribute)
	private Button mBtnToContribute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBtnJoinQQGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QQ.joinQQGroup(FeedbackActy.this, Const.QQ_GROUP_KEY_332460930);
			}
		});
		mBtnToContribute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContributeActy.startMeFromFeedback(FeedbackActy.this);
			}
		});

		/* 在不同dpi的手机上bitmap会被缩放，倒是偏移。已经更改为放到drawable-nodpi下了
		 * Options opt = new Options();
		 * opt.inScaled = false;
		 * mMagicView.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.img_m_feedback_magic, opt)));
		 */
		mMagicView.setTextFont(FontUtils.getTypefaceWithCode(FeedbackActy.this, 1));
	}
}
