package com.star.talk.startalk.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.data.SignInBoardFriendsListBean;
import com.star.talk.startalk.widget.MagicBoardView;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;
import com.wei.c.widget.CircleImageView;

public class SignInBoardFriendsListAdapter extends AbsAdapter<SignInBoardFriendsListBean> {
	private OnClickListener mOnMagicBoardClick, mOnCommentClick, mOnAiXingClick, mOnResendClick;

	public SignInBoardFriendsListAdapter(Context context, List<SignInBoardFriendsListBean> data,
			OnClickListener onMagicBoardClick, OnClickListener onCommentClick,
			OnClickListener onAiXingClick, OnClickListener onResendClick) {
		super(context, data);
		mOnMagicBoardClick = onMagicBoardClick;
		mOnCommentClick = onCommentClick;
		mOnAiXingClick = onAiXingClick;
		mOnResendClick = onResendClick;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return MyViewHolder.getAndBindView(position, convertView, parent, getInflater(), MyViewHolder.class, getItem(position),
				mOnMagicBoardClick, mOnCommentClick, mOnAiXingClick, mOnResendClick);
	}

	@ViewLayoutId(R.layout.i_f_f_m_tabs_sign_in_board_friends_list)
	public static class MyViewHolder extends ViewHolder<SignInBoardFriendsListBean, OnClickListener> {
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_img_face)
		private CircleImageView mImgFace;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_magic_board)
		private MagicBoardView mMagicBoard;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_nickname)
		private TextView mTextNickName;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_starsign)
		private TextView mTextStarSign;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_timebefore)
		private TextView mTextTimeBefore;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_location)
		private TextView mTextLocation;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_comment)
		private TextView mTextComment;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_aixing)
		private TextView mTextAiXing;
		@ViewId(R.id.i_f_f_m_tabs_sign_in_board_friends_list_text_resend)
		private TextView mTextResend;

		public MyViewHolder(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mMagicBoard.setOnClickListener(args[0]);
			mTextComment.setOnClickListener(args[1]);
			mTextAiXing.setOnClickListener(args[2]);
			mTextResend.setOnClickListener(args[3]);
		}

		@Override
		public void bind(int position, SignInBoardFriendsListBean data) {
			// TODO Auto-generated method stub

		}
	}
}
