package com.star.talk.startalk.fragment.v2.mtabs;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.star.talk.startalk.FavoriteActy;
import com.star.talk.startalk.R;
import com.star.talk.startalk.adapter.v2.MTabsLeftFrgmtVPagerAdapter;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsFragment;

@ViewLayoutId(R.layout.f_m_m_tabs_left_btn_page)
public class MTabsLeftFrgmt extends AbsFragment {
	@ViewId(R.id.f_m_m_tabs_left_btn_page_title_left_btn_favorite)
	private ImageButton mBtnTitleFav;
	@ViewId(R.id.f_m_m_tabs_left_btn_page_rbtn_recommend)
	private RadioButton mRbtnRecommend;
	@ViewId(R.id.f_m_m_tabs_left_btn_page_rbtn_favorite)
	private RadioButton mRbtnFavorite;
	@ViewId(R.id.f_m_m_tabs_left_btn_page_scrollable_tabstrip)
	private ViewGroup mTabstrip;
	@ViewId(R.id.f_m_m_tabs_left_btn_page_viewpager)
	private ViewPager mViewPager;

	private int mScrollWidth;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mBtnTitleFav.setOnClickListener(mOnBtnTitleFavClick);
		mRbtnRecommend.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRbtnFavorite.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		mTabstrip.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mScrollWidth = (mTabstrip.getWidth() - mTabstrip.getPaddingLeft() - mTabstrip.getPaddingRight()) / 2;
			}
		});
		mViewPager.setAdapter(new MTabsLeftFrgmtVPagerAdapter(getChildFragmentManager()));
		mRbtnRecommend.setChecked(true);
		return getCreatedView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private final OnClickListener mOnBtnTitleFavClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FavoriteActy.startMe(getActivity());
		}
	};

	private final OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) mViewPager.setCurrentItem(buttonView == mRbtnRecommend ? 0 : 1);
		}
	};

	private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

		/**
		 * @see
		 * {@link ViewPager#SCROLL_STATE_IDLE}表示View处于显示状态，没有拖动或滚动动画正在进行<br/>
		 * {@link ViewPager#SCROLL_STATE_DRAGGING}表示用户正在拖动<br/>
		 * {@link ViewPager#SCROLL_STATE_SETTLING}表示拖动被释放，正在进行固定View的操作和滚动动画<br/>
		 */
		@Override
		public void onPageScrollStateChanged(int state) {}

		/**
		 * @param position 表示正在显示的左边的一个页面的位置（滚动的时候会显示两个页面）
		 * @param positionOffset 滚动距离的百分比
		 * @param positionOffsetPixels 滚动距离的像素个数
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			mTabstrip.scrollTo((int) (- mScrollWidth * (position + positionOffset)), 0);
		}

		/**页面跳转完之后，最后调用**/
		@Override
		public void onPageSelected(int position) {
			if (position == 0) {
				mRbtnRecommend.setChecked(true);
			} else {
				mRbtnFavorite.setChecked(true);
			}
		}
	};

	public void scrollToTopAndRefresh() {
		//TODO 这里需要处理多次连续调用的情况
		
		// TODO Auto-generated method stub
		L.i(MTabsLeftFrgmt.class, "scrollToTopAndRefresh");
	}
}
