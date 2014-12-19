package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.phone.Device;

@ViewLayoutId(R.layout.m_welcome_snow)
public class WelcomeSnowActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, WelcomeSnowActy.class));
	}

	@ViewId(R.id.m_welcome_snow_img_tree_1)
	private ImageView mTree1;
	@ViewId(R.id.m_welcome_snow_img_tree_2)
	private ImageView mTree2;

	@ViewId(R.id.m_welcome_snow_img_0)
	private ImageView mImgSnow0;
	@ViewId(R.id.m_welcome_snow_img_1)
	private ImageView mImgSnow1;
	@ViewId(R.id.m_welcome_snow_img_2)
	private ImageView mImgSnow2;
	@ViewId(R.id.m_welcome_snow_img_3)
	private ImageView mImgSnow3;
	@ViewId(R.id.m_welcome_snow_img_4)
	private ImageView mImgSnow4;
	@ViewId(R.id.m_welcome_snow_img_5)
	private ImageView mImgSnow5;
	@ViewId(R.id.m_welcome_snow_img_6)
	private ImageView mImgSnow6;
	@ViewId(R.id.m_welcome_snow_img_7)
	private ImageView mImgSnow7;
	@ViewId(R.id.m_welcome_snow_img_8)
	private ImageView mImgSnow8;
	@ViewId(R.id.m_welcome_snow_img_9)
	private ImageView mImgSnow9;
	@ViewId(R.id.m_welcome_snow_img_10)
	private ImageView mImgSnow10;
	@ViewId(R.id.m_welcome_snow_img_11)
	private ImageView mImgSnow11;
	@ViewId(R.id.m_welcome_snow_img_12)
	private ImageView mImgSnow12;
	@ViewId(R.id.m_welcome_snow_img_13)
	private ImageView mImgSnow13;
	@ViewId(R.id.m_welcome_snow_img_14)
	private ImageView mImgSnow14;
	@ViewId(R.id.m_welcome_snow_img_15)
	private ImageView mImgSnow15;
	@ViewId(R.id.m_welcome_snow_img_16)
	private ImageView mImgSnow16;
	@ViewId(R.id.m_welcome_snow_img_17)
	private ImageView mImgSnow17;
	@ViewId(R.id.m_welcome_snow_img_18)
	private ImageView mImgSnow18;
	@ViewId(R.id.m_welcome_snow_img_19)
	private ImageView mImgSnow19;
	@ViewId(R.id.m_welcome_snow_img_20)
	private ImageView mImgSnow20;
	@ViewId(R.id.m_welcome_snow_img_21)
	private ImageView mImgSnow21;
	@ViewId(R.id.m_welcome_snow_img_22)
	private ImageView mImgSnow22;
	@ViewId(R.id.m_welcome_snow_img_23)
	private ImageView mImgSnow23;
	@ViewId(R.id.m_welcome_snow_img_24)
	private ImageView mImgSnow24;
	@ViewId(R.id.m_welcome_snow_img_25)
	private ImageView mImgSnow25;
	@ViewId(R.id.m_welcome_snow_img_26)
	private ImageView mImgSnow26;
	@ViewId(R.id.m_welcome_snow_img_27)
	private ImageView mImgSnow27;
	@ViewId(R.id.m_welcome_snow_img_28)
	private ImageView mImgSnow28;
	@ViewId(R.id.m_welcome_snow_img_29)
	private ImageView mImgSnow29;

	@ViewId(R.id.m_welcome_snow_btn_skip)
	private ImageButton mBtnSkip;

	private int mScreenWidth;
	private int mScreenHeight;
	private boolean mPaused = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mScreenWidth = Device.getInstance(this).width;
		mScreenHeight = Device.getInstance(this).height;

		mBtnSkip.setOnClickListener(mOnSkipClick);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPaused = false;

		startSkipInterval();

		((AnimationDrawable) mTree1.getDrawable()).start();
		((AnimationDrawable) mTree2.getDrawable()).start();

		buildAnimation(mImgSnow0);
		buildAnimation(mImgSnow1);
		buildAnimation(mImgSnow2);
		buildAnimation(mImgSnow3);
		buildAnimation(mImgSnow4);
		buildAnimation(mImgSnow5);
		buildAnimation(mImgSnow6);
		buildAnimation(mImgSnow7);
		buildAnimation(mImgSnow8);
		buildAnimation(mImgSnow9);
		buildAnimation(mImgSnow10);
		buildAnimation(mImgSnow11);
		buildAnimation(mImgSnow12);
		buildAnimation(mImgSnow13);
		buildAnimation(mImgSnow14);
		buildAnimation(mImgSnow15);
		buildAnimation(mImgSnow16);
		buildAnimation(mImgSnow17);
		buildAnimation(mImgSnow18);
		buildAnimation(mImgSnow19);
		buildAnimation(mImgSnow20);
		buildAnimation(mImgSnow21);
		buildAnimation(mImgSnow22);
		buildAnimation(mImgSnow23);
		buildAnimation(mImgSnow24);
		buildAnimation(mImgSnow25);
		buildAnimation(mImgSnow26);
		buildAnimation(mImgSnow27);
		buildAnimation(mImgSnow28);
		buildAnimation(mImgSnow29);
	}

	@Override
	protected void onPause() {
		mPaused = true;

		abortAnimation(mImgSnow0);
		abortAnimation(mImgSnow1);
		abortAnimation(mImgSnow2);
		abortAnimation(mImgSnow3);
		abortAnimation(mImgSnow4);
		abortAnimation(mImgSnow5);
		abortAnimation(mImgSnow6);
		abortAnimation(mImgSnow7);
		abortAnimation(mImgSnow8);
		abortAnimation(mImgSnow9);
		abortAnimation(mImgSnow10);
		abortAnimation(mImgSnow11);
		abortAnimation(mImgSnow12);
		abortAnimation(mImgSnow13);
		abortAnimation(mImgSnow14);
		abortAnimation(mImgSnow15);
		abortAnimation(mImgSnow16);
		abortAnimation(mImgSnow17);
		abortAnimation(mImgSnow18);
		abortAnimation(mImgSnow19);
		abortAnimation(mImgSnow20);
		abortAnimation(mImgSnow21);
		abortAnimation(mImgSnow22);
		abortAnimation(mImgSnow23);
		abortAnimation(mImgSnow24);
		abortAnimation(mImgSnow25);
		abortAnimation(mImgSnow26);
		abortAnimation(mImgSnow27);
		abortAnimation(mImgSnow28);
		abortAnimation(mImgSnow29);

		((AnimationDrawable) mTree1.getDrawable()).stop();
		((AnimationDrawable) mTree2.getDrawable()).stop();
		super.onPause();
	}

	private void buildAnimation(View view) {
		if (mPaused) return;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
		lp.leftMargin = (int) (Math.random() * mScreenWidth);
		lp.topMargin = (int) (Math.random() * mScreenHeight / 2);
		view.setLayoutParams(lp);

		Animation anim = createAnimation(lp.topMargin, new MyAnimationListener(view));
		view.setVisibility(View.VISIBLE);
		view.startAnimation(anim);
	}

	private void abortAnimation(View view) {
		view.clearAnimation();
		view.setVisibility(View.INVISIBLE);
	}

	private Animation createAnimation(int top, AnimationListener l) {
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(startAlpha());
		set.addAnimation(startScale());
		int duration = (int) (Math.random() * 10000 + 3000);
		set.addAnimation(randomRotate(duration));
		set.addAnimation(randomScale());
		set.addAnimation(endScale(duration));
		//添加的顺序很重要，TranslateAnimation必须在ScaleAnimation的后面，否则会出现奇怪的现象
		set.addAnimation(randomTranslate(top, duration, l));
		set.addAnimation(randomTranslateX());
		set.addAnimation(endAlpha(duration));
		return set;
	}

	private AlphaAnimation startAlpha() {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setInterpolator(this, android.R.anim.accelerate_interpolator);
		alpha.setStartOffset(0);
		alpha.setDuration(1500);
		return alpha;
	}

	private ScaleAnimation startScale() {
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setInterpolator(this, android.R.anim.accelerate_interpolator);
		scale.setStartOffset(0);
		scale.setDuration(1500);
		return scale;
	}

	private TranslateAnimation randomTranslate(int startTopInParent, int duration, AnimationListener l) {
		float percent = 1 - startTopInParent * 1.0f / mScreenHeight;
		TranslateAnimation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, (float) ((Math.random() > 0.5f ? 1 : -1) * Math.random()) * 5,
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, (float) (Math.random() * percent + 0.1f));
		trans.setInterpolator(this, android.R.anim.linear_interpolator);
		trans.setStartOffset(0);
		trans.setDuration(duration);
		trans.setAnimationListener(l);
		return trans;
	}

	private TranslateAnimation randomTranslateX() {
		TranslateAnimation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, (float) ((Math.random() > 0.5f ? 1 : -1) * Math.random()),
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
		trans.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator);
		trans.setRepeatCount(Animation.INFINITE);
		trans.setRepeatMode(Animation.REVERSE);
		trans.setStartOffset(0);
		trans.setDuration((int) (Math.random() * 2000 + 1000));
		return trans;
	}

	private RotateAnimation randomRotate(int duration) {
		RotateAnimation rotate = new RotateAnimation(0, (float) ((Math.random() > 0.5f ? 1 : -1) * 360 / (Math.random() * 20000 + 1000) * duration),
				Animation.RELATIVE_TO_SELF, (float) (Math.random() * 0.5f), Animation.RELATIVE_TO_SELF, (float) (Math.random() * 0.5f));
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setRepeatMode(Animation.RESTART);
		rotate.setStartOffset(0);
		rotate.setDuration(duration);
		return rotate;
	}

	private ScaleAnimation randomScale() {
		float scaleTo = (float) (0.3f + Math.random() * 0.5f);
		ScaleAnimation scale = new ScaleAnimation(1, scaleTo, 1, scaleTo, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setRepeatCount(Animation.INFINITE);
		scale.setRepeatMode(Animation.REVERSE);
		scale.setStartOffset(1000);
		scale.setDuration((int) (Math.random() * 3000) + 1000);
		return scale;
	}

	private AlphaAnimation endAlpha(int startTime) {
		AlphaAnimation alpha = new AlphaAnimation(1, 0);
		alpha.setInterpolator(this, android.R.anim.decelerate_interpolator);
		alpha.setStartOffset(startTime - 300);
		alpha.setDuration(300);
		return alpha;
	}

	private ScaleAnimation endScale(int startTime) {
		ScaleAnimation scale = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setInterpolator(this, android.R.anim.decelerate_interpolator);
		scale.setStartOffset(startTime - 300);
		scale.setDuration(300);
		return scale;
	}

	private class MyAnimationListener implements AnimationListener {
		private View mView;

		public MyAnimationListener(View view) {
			mView = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {}

		@Override
		public void onAnimationEnd(Animation animation) {
			abortAnimation(mView);
			buildAnimation(mView);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}
	}

	private final OnClickListener mOnSkipClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onSkipRun();
		}
	};

	private void startSkipInterval() {
		App.get().getMainHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				onSkipRun();
			}
		}, App.get().isFirstLaunch() ? 30000 : 6000);
	}

	private void onSkipRun() {
		if (GuideActy.isShown(WelcomeSnowActy.this)) {
			if (App.isLogined()) {
				MainTabsActy_v_2.startMe(WelcomeSnowActy.this);
			} else {
				WelcomeActy.startMe(WelcomeSnowActy.this);
			}
		} else {
			GuideActy.startMe(WelcomeSnowActy.this);
		}
		WelcomeSnowActy.this.finish();
	}
}
