package com.star.talk.startalk.fragment.signinboard;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.adapter.SignInBoardFriendsListAdapter;
import com.star.talk.startalk.data.SignInBoardFriendsListBean;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsFragment;

@ViewLayoutId(R.layout.f_f_m_tabs_sign_in_board_friends)
public class FriendsFragment extends AbsFragment {
	@ViewId(R.id.f_f_m_tabs_sign_in_board_friends_list)
	private ListView mList;
	private SignInBoardFriendsListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//TODO 加载数据
	}

	private void updateData(List<SignInBoardFriendsListBean> data) {
		if (mAdapter == null) {
			mAdapter = new SignInBoardFriendsListAdapter(getActivity(), data,
					mOnMagicBoardClick, mOnCommentClick, mOnAiXingClick, mOnResendClick);
			mList.setAdapter(mAdapter);
		} else {
			mAdapter.setDataSource(data);
		}
	}

	private OnClickListener mOnMagicBoardClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener mOnCommentClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener mOnAiXingClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener mOnResendClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};
}
