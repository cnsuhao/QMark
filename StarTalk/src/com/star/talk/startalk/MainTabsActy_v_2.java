package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;

import com.star.talk.startalk.fragment.v2.mtabs.MTabsLeftFrgmt;
import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;

@ViewLayoutId(R.layout.m_m_tabs_v_2)
public class MainTabsActy_v_2 extends AbsBaseActivity implements OnClickListener {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, MainTabsActy_v_2.class));
	}

	private static final String KEY_TAB_INDEX		= "key_tab_index";
	private int mTabIndex = -1;

	//@ViewId(R.id.m_m_tabs_v_2_content_panel)
	//private FrameLayout mContentPanel;
	@ViewId(R.id.m_m_tabs_v_2_btn_tabbar_left)
	private RadioButton mBtnLeft;
	@ViewId(R.id.m_m_tabs_v_2_btn_tabbar_center)
	private RadioButton mBtnCenter;
	@ViewId(R.id.m_m_tabs_v_2_btn_tabbar_right)
	private RadioButton mBtnRight;

	private long mLeftTabPressedTime = 0;
	private long mBackPressedTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtnLeft.setOnClickListener(this);
		mBtnCenter.setOnClickListener(this);
		mBtnRight.setOnClickListener(this);

		onClick(mBtnLeft);
	}

	@Override
	public void onClick(View v) {
		Fragment fragment = null;
		int id = 0;
		switch (v.getId()) {
		case R.id.m_m_tabs_v_2_btn_tabbar_left:
			id = R.id.m_m_tabs_v_2_btn_tabbar_left;
			if (mTabIndex == 0) {
				if (System.currentTimeMillis() - mLeftTabPressedTime <= 1000) {
					MTabsLeftFrgmt frgmt = (MTabsLeftFrgmt) getSupportFragmentManager().findFragmentByTag(getResources().getResourceEntryName(id));
					frgmt.scroll2TopAndRefresh();
					mLeftTabPressedTime = 0;	//清零，防止连续多次点击的触发
				} else {
					mLeftTabPressedTime = System.currentTimeMillis();
				}
				return;
			} else {
				fragment = new MTabsLeftFrgmt();
				mTabIndex = 0;
			}
			mLeftTabPressedTime = System.currentTimeMillis();
			break;
		case R.id.m_m_tabs_v_2_btn_tabbar_center:
			id = R.id.m_m_tabs_v_2_btn_tabbar_center;
			//fragment = new SignInBoardFragment();
			//mTabIndex = 1;
			//TODO 待定
			return;
		case R.id.m_m_tabs_v_2_btn_tabbar_right:
			id = R.id.m_m_tabs_v_2_btn_tabbar_right;
			if (mTabIndex == 2) return;
			//fragment = new MineFragment();
			mTabIndex = 2;
			break;
		}
		updateCheckState();
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().
			replace(R.id.m_m_tabs_v_2_content_panel, fragment, getResources().getResourceEntryName(id)).commit();
		}
	}

	private void updateCheckState() {
		switch (mTabIndex) {
		case 0:
			mBtnLeft.setChecked(true);
			mBtnRight.setChecked(false);
			break;
		case 2:
			mBtnLeft.setChecked(false);
			mBtnRight.setChecked(true);
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_TAB_INDEX, mTabIndex);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mBackPressedTime > 2500) {
			Toast.makeText(this, R.string.m_category_v_1_back_click_1th_more_to_exit, Toast.LENGTH_SHORT).show();
			mBackPressedTime = System.currentTimeMillis();
			return;
		}
		super.onBackPressed();
	}
}
