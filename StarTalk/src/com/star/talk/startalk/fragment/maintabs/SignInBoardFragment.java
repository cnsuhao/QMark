package com.star.talk.startalk.fragment.maintabs;

import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.adapter.SignInBoardFragmentViewPagerAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsFragment;
import com.wei.c.utils.SPref;

@ViewLayoutId(R.layout.f_m_tabs_sign_in_board)
public class SignInBoardFragment extends AbsFragment {
	private static final String KEY_CURRENT_ITEM	= "key_current_item";

	@ViewId(R.id.f_m_tabs_sign_in_board_viewpager)
	private ViewPager mViewPager;
	@ViewId(R.id.f_m_tabs_sign_in_board_pagertab)
	private PagerTabStrip mPagerTabStrip;
	@ViewId(R.id.f_m_tabs_sign_in_board_title_right_btn_msg)
	private ImageButton mBtnMsg;
	@ViewId(R.id.f_m_tabs_sign_in_board_text_msg_count)
	private TextView mTextMsg;

	private int mCurrentItem = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		//PagerTabStrip继承自PagerTitleStrip，只有下面有限的设置
		//mPagerTabStrip.setPadding(20, 0, 20, 0);
		//mPagerTabStrip.setBackgroundColor(0xffff0000);
		//mPagerTabStrip.setDrawFullUnderline(true);	//是否画底下那根线
		mPagerTabStrip.setNonPrimaryAlpha(.6f);	//未被选中的标签的文字透明度
		mPagerTabStrip.setTabIndicatorColorResource(R.color.text_title_orange);
		mPagerTabStrip.setTextColor(getResources().getColor(R.color.text_title_orange));
		mPagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_title_14));
		mPagerTabStrip.setTextSpacing(100);

		mViewPager.setAdapter(new SignInBoardFragmentViewPagerAdapter(getChildFragmentManager()));
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			/**页面跳转完之后，最后调用**/
			@Override
			public void onPageSelected(int position) {
				mCurrentItem = mViewPager.getCurrentItem();
				SPref.edit(getActivity(), SignInBoardFragment.class).putInt(KEY_CURRENT_ITEM, mCurrentItem).commit();
			}
		});

		mCurrentItem = SPref.getSPref(getActivity(), SignInBoardFragment.class).getInt(KEY_CURRENT_ITEM, 0);
		mViewPager.setCurrentItem(mCurrentItem);

		return getCreatedView();
	}
}
