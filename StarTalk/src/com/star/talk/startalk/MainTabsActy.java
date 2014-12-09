package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.star.talk.startalk.fragment.maintabs.EditSourceFragment;
import com.star.talk.startalk.fragment.maintabs.MineFragment;
import com.star.talk.startalk.fragment.maintabs.SignInBoardFragment;
import com.tisumoon.AbsBaseActivity;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.utils.SPref;

@ViewLayoutId(R.layout.m_tabs)
public class MainTabsActy extends AbsBaseActivity implements OnCheckedChangeListener {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, MainTabsActy.class));
	}

	private static final String KEY_TAB_INDEX		= "key_tab_index";
	private int mTabIndex = 1;

	@ViewId(R.id.m_tabs_content_panel)
	private FrameLayout mContentPanel;
	@ViewId(R.id.m_tabs_btn_edit)
	private RadioButton mBtnEdit;
	@ViewId(R.id.m_tabs_btn_sign_in_board)
	private RadioButton mBtnSignInBoard;
	@ViewId(R.id.m_tabs_btn_mine)
	private RadioButton mBtnMine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtnEdit.setOnCheckedChangeListener(this);
		mBtnSignInBoard.setOnCheckedChangeListener(this);
		mBtnMine.setOnCheckedChangeListener(this);

		RadioButton checkedBtn = mBtnEdit;
		mTabIndex = SPref.getSPref(MainTabsActy.this, MainTabsActy.class).getInt(KEY_TAB_INDEX, 0);

		switch (mTabIndex) {
		case 0:
			checkedBtn = mBtnEdit;
			break;
		case 1:
			checkedBtn = mBtnSignInBoard;
			break;
		case 2:
			checkedBtn = mBtnMine;
			break;
		default:
			break;
		}
		checkedBtn.setChecked(true);
		//onCheckedChanged(checkedBtn, true);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			Fragment fragment;
			switch (buttonView.getId()) {
			case R.id.m_tabs_btn_edit:
				fragment = new EditSourceFragment();
				mTabIndex = 0;
				break;
			case R.id.m_tabs_btn_sign_in_board:
				fragment = new SignInBoardFragment();
				mTabIndex = 1;
				break;
			case R.id.m_tabs_btn_mine:
				fragment = new MineFragment();
				mTabIndex = 2;
				break;
			default:
				fragment = null;	//逻辑上不会出现
				break;
			}
			SPref.edit(MainTabsActy.this, MainTabsActy.class).putInt(KEY_TAB_INDEX, mTabIndex).commit();
			getSupportFragmentManager().beginTransaction().replace(R.id.m_tabs_content_panel, fragment).commit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_TAB_INDEX, mTabIndex);
	}
}
