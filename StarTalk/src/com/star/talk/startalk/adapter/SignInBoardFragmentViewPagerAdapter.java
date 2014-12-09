package com.star.talk.startalk.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.star.talk.startalk.fragment.signinboard.FriendsFragment;
import com.star.talk.startalk.fragment.signinboard.NearByFragment;
import com.star.talk.startalk.fragment.signinboard.RecommendFragment;

public class SignInBoardFragmentViewPagerAdapter extends FragmentStatePagerAdapter {

	public SignInBoardFragmentViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "   推荐   ";	//由于无法给文本设置paddings, 这里加空格
		case 1:
			return "   好友   ";
		case 2:
			return "   附近   ";
		default:
			return null;	//逻辑上不存在这种情况
		}
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		switch (position) {
		case 0:
			fragment = new RecommendFragment();
			break;
		case 1:
			fragment = new FriendsFragment();
			break;
		case 2:
			fragment = new NearByFragment();
			break;
		default:
			fragment = null;	//逻辑上不存在这种情况
			break;
		}
		return fragment;
	}

	/*@Override
	public boolean isViewFromObject(View view, Object object) {
		boolean result = super.isViewFromObject(view, object);
		L.d(this, "isViewFromObject--return:" + result);
		L.w(this, "isViewFromObject--view:" + view);
		L.e(this, "isViewFromObject--object:" + object);
		return result;//view == ((AbsFragment)object).getCreatedView();
	}*/
}
