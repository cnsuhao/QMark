package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;
import com.wei.c.phone.Device;
import com.wei.c.utils.Manifest;
import com.wei.c.utils.SPref;

@ViewLayoutId(R.layout.m_guide)
public class GuideActy extends AbsBaseActivity {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, GuideActy.class));
	}

	@ViewId(R.id.m_guide_viewpager)
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*if (isShown(this)) {
			startWelcomeActy();
			return;
		}*/

		mViewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return 5;
			}

			@Override
			public boolean isViewFromObject(View view, Object obj) {
				return view == ((ViewHolder<?, ?>)obj).getView();
			}

			@Override
			public int getItemPosition(Object obj) {
				int position = super.getItemPosition(obj);
				if (obj instanceof ViewHolder4) {
					position = 4;
				} else if (obj instanceof ViewHolder3) {
					position = 3;
				} else if (obj instanceof ViewHolder2) {
					position = 2;
				} else if (obj instanceof ViewHolder1) {
					position = 1;
				} else if (obj instanceof ViewHolder0) {
					position = 0;
				}
				return position;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ViewHolder<Void, OnClickListener> vHolder = null;
				switch (position) {
				case 0:
					vHolder = ViewHolder.bindView(0, ViewHolder.makeView(ViewHolder0.class, getLayoutInflater(), container), ViewHolder0.class, null, mOnNextClick);
					break;
				case 1:
					vHolder = ViewHolder.bindView(0, ViewHolder.makeView(ViewHolder1.class, getLayoutInflater(), container), ViewHolder1.class, null, mOnNextClick);
					break;
				case 2:
					vHolder = ViewHolder.bindView(0, ViewHolder.makeView(ViewHolder2.class, getLayoutInflater(), container), ViewHolder2.class, null, mOnNextClick);
					break;
				case 3:
					vHolder = ViewHolder.bindView(0, ViewHolder.makeView(ViewHolder3.class, getLayoutInflater(), container), ViewHolder3.class, null, mOnNextClick);
					break;
				case 4:
					vHolder = ViewHolder.bindView(0, ViewHolder.makeView(ViewHolder4.class, getLayoutInflater(), container), ViewHolder4.class, null, mOnCompleteClick);
					break;
				}
				container.addView(vHolder.getView());
				return vHolder;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object obj) {
				container.removeView(((ViewHolder<?, ?>)obj).getView());
			}
		});
	}

	private final OnClickListener mOnNextClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(((Integer)v.getTag()) + 1);
		}
	};

	private final OnClickListener mOnCompleteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startWelcomeActy();
		}
	};

	private void startWelcomeActy() {
		if (!isShown(this)) setShown(this);
		WelcomeActy.startMe(GuideActy.this);
		GuideActy.this.finish();
	}

	public static void setShown(Context context) {
		SPref.edit(context, GuideActy.class).clear().putBoolean("shown", true).putBoolean(getShownVersionKey(context), true).commit();
	}

	public static boolean isShown(Context context) {
		SharedPreferences spref = SPref.getSPref(context, GuideActy.class);
		return spref.getBoolean("shown", false) && (App.mustReShowGuide() ? spref.getBoolean(getShownVersionKey(context), false) : true);
	}

	private static String getShownVersionKey(Context context) {
		return "shown_v_" + Manifest.getVersionName(context);
	}

	@ViewLayoutId(R.layout.i_m_guide_next)
	private static class ViewHolder0 extends ViewHolder<Void, OnClickListener> {
		@ViewId(R.id.i_m_guide_bg)
		protected View mBg;
		@ViewId(R.id.i_m_guide_btn_next)
		protected ImageButton mBtnNext;

		protected static final int WIDTH		= 720;
		protected static final int HEIGHT		= 1280;
		protected static final int WIDTH_BTN	= 224;
		protected static final int HEIGHT_BTN	= 88;
		protected static final int RIGHT_BTN	= 16;
		protected static final int BOTTOM_BTN	= 29;

		public ViewHolder0(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mBg.setBackgroundResource(R.drawable.img_i_m_guide_0);
			mBtnNext.setTag(0);
			mBtnNext.setOnClickListener(args[0]);
			initBtnNextPosition();
		}

		@Override
		public void bind(int position, Void data) {}

		protected void initBtnNextPosition() {
			int screenWidth = Device.getInstance(getView().getContext()).width;
			int screenHeight = Device.getInstance(getView().getContext()).height;
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBtnNext.getLayoutParams();
			float widthScale = screenWidth * 1.0f / WIDTH;
			float heightScale = screenHeight * 1.0f / HEIGHT;
			lp.width = (int) (widthScale * WIDTH_BTN);
			lp.height = (int) (heightScale * HEIGHT_BTN);
			lp.rightMargin = (int) (widthScale * RIGHT_BTN);
			lp.bottomMargin = (int) (heightScale * BOTTOM_BTN);
			mBtnNext.setLayoutParams(lp);
		}
	}

	private static class ViewHolder1 extends ViewHolder0 {
		public ViewHolder1(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mBg.setBackgroundResource(R.drawable.img_i_m_guide_1);
			mBtnNext.setTag(1);
			mBtnNext.setOnClickListener(args[0]);
			initBtnNextPosition();
		}
	}

	private static class ViewHolder2 extends ViewHolder0 {
		public ViewHolder2(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mBg.setBackgroundResource(R.drawable.img_i_m_guide_2);
			mBtnNext.setTag(2);
			mBtnNext.setOnClickListener(args[0]);
			initBtnNextPosition();
		}
	}

	private static class ViewHolder3 extends ViewHolder0 {
		public ViewHolder3(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mBg.setBackgroundResource(R.drawable.img_i_m_guide_3);
			mBtnNext.setTag(3);
			mBtnNext.setOnClickListener(args[0]);
			initBtnNextPosition();
		}
	}

	@ViewLayoutId(R.layout.i_m_guide_complete)
	private static class ViewHolder4 extends ViewHolder<Void, OnClickListener> {
		@ViewId(R.id.i_m_guide_bg)
		protected View mBg;
		@ViewId(R.id.i_m_guide_btn_complete)
		protected ImageButton mBtnComplete;

		protected static final int WIDTH		= 720;
		protected static final int HEIGHT		= 1280;
		protected static final int WIDTH_BTN	= 428;
		protected static final int HEIGHT_BTN	= 120;
		protected static final int RIGHT_BTN	= 3;
		protected static final int BOTTOM_BTN	= 35;

		public ViewHolder4(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mBg.setBackgroundResource(R.drawable.img_i_m_guide_4);
			mBtnComplete.setOnClickListener(args[0]);
			initBtnNextPosition();
		}

		@Override
		public void bind(int position, Void data) {}

		protected void initBtnNextPosition() {
			int screenWidth = Device.getInstance(getView().getContext()).width;
			int screenHeight = Device.getInstance(getView().getContext()).height;
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBtnComplete.getLayoutParams();
			float widthScale = screenWidth * 1.0f / WIDTH;
			float heightScale = screenHeight * 1.0f / HEIGHT;
			lp.width = (int) (widthScale * WIDTH_BTN);
			lp.height = (int) (heightScale * HEIGHT_BTN);
			lp.rightMargin = (int) (widthScale * RIGHT_BTN);
			lp.bottomMargin = (int) (heightScale * BOTTOM_BTN);
			mBtnComplete.setLayoutParams(lp);
		}
	}
}
