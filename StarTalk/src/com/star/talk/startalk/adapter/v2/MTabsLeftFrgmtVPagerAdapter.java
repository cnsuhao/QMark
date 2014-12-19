package com.star.talk.startalk.adapter.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.star.talk.startalk.fragment.v2.lefttab.FavoriteFrgmt;
import com.star.talk.startalk.fragment.v2.lefttab.RecommendFrgmt;

public class MTabsLeftFrgmtVPagerAdapter extends FragmentStatePagerAdapter {

	public MTabsLeftFrgmtVPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new RecommendFrgmt();
		case 1:
			return new FavoriteFrgmt();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return 2;
	}
}
