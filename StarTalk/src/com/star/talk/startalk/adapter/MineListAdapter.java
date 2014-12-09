package com.star.talk.startalk.adapter;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.data.MineListBean;
import com.star.talk.startalk.utils.FontUtils;
import com.star.talk.startalk.utils.MagicBoardUtils;
import com.star.talk.startalk.widget.BlurBgRelativeLayout;
import com.star.talk.startalk.widget.MagicBoardView;
import com.tisumoon.utils.ImageFactory;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;
import com.wei.c.utils.TimeUtils;
import com.wei.c.widget.CircleImageView;

public class MineListAdapter extends AbsAdapter<MineListBean> {
	private OnClickListener mOnImgHeadClick, mOnImgBlurClick, mOnFriendsClick,
	mOnMoreClick, mOnCloseClick, mOnResendClick, mOnDeleteClick;

	public MineListAdapter(Context context, OnClickListener onImgHeadClick, OnClickListener onImgBlurClick, OnClickListener onFriendsClick,
			OnClickListener onMoreClick, OnClickListener onCloseClick, OnClickListener onResendClick, OnClickListener onDeleteClick) {
		super(context);
		mOnImgHeadClick = onImgHeadClick;
		mOnImgBlurClick = onImgBlurClick;
		mOnFriendsClick = onFriendsClick;
		mOnMoreClick = onMoreClick;
		mOnCloseClick = onCloseClick;
		mOnResendClick = onResendClick;
		mOnDeleteClick = onDeleteClick;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? 0 : 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItemViewType(position) == 0 ?
				ViewHolderUser.getAndBindView(position, convertView, parent, getInflater(), ViewHolderUser.class, getItem(position),
						mOnImgHeadClick, mOnImgBlurClick, mOnFriendsClick) :
							ViewHolderHistory.getAndBindView(position, convertView, parent, getInflater(), ViewHolderHistory.class, getItem(position),
									mOnMoreClick, mOnCloseClick, mOnResendClick, mOnDeleteClick);
	}

	@ViewLayoutId(R.layout.i_f_m_tabs_mine_list_type_user)
	private static class ViewHolderUser extends ViewHolder<MineListBean, OnClickListener> {
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_img_head)
		private CircleImageView mImgHead;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_text_nickname)
		private TextView mTextNickname;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_text_starsign)
		private TextView mTextStarSign;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_text_id)
		private TextView mTextID;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_text_new_friends_count)
		private TextView mTextNewFriendsCount;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_img_blur_bg)
		private BlurBgRelativeLayout mImgBlurBg;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_user_my_friends_panel)
		private ViewGroup mFriendsPanel;

		private static FinalBitmap mFb;
		private FinalBitmap getFb() {
			if (mFb == null) mFb = ImageFactory.newFbDiskCache(getView().getContext(), -1, -1, 0, 0);
			return mFb;
		}

		public ViewHolderUser(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mImgHead.setOnClickListener(args[0]);
			mImgBlurBg.setOnClickListener(args[1]);
			mFriendsPanel.setOnClickListener(args[2]);
		}

		@Override
		public void bind(int position, MineListBean data) {
			MagicBoardUtils.getBitmapLoader(getView().getContext()).display(mImgHead, data.user.headUrl);
			getFb().display(mImgBlurBg, TextUtils.isEmpty(data.user.bgUrl) ? data.user.headUrl : data.user.bgUrl);

			mTextNickname.setText(data.user.nicename);
			mTextStarSign.setText(data.user.starSign);
			mTextID.setText("ID:" + data.user.ID);
			mTextNewFriendsCount.setText("+" + data.user.newFriendsCount);
		}
	}

	@ViewLayoutId(R.layout.i_f_m_tabs_mine_list_type_my_sign_in_history)
	private static class ViewHolderHistory extends ViewHolder<MineListBean, OnClickListener> {
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_text_time)
		private TextView mTextTime;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_text_location)
		private TextView mTextLocation;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_text_comment)
		private TextView mTextComment;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_text_aixing)
		private TextView mTextAiXing;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_btn_more)
		private ImageButton mBtnMore;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_btn_close)
		private ImageButton mBtnClose;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_btn_resend)
		private ImageButton mBtnResend;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_btn_delete)
		private ImageButton mBtnDelete;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_magic_board)
		private MagicBoardView mMagicBoard;
		@ViewId(R.id.i_f_m_tabs_mine_list_type_my_sign_in_history_more_panel)
		private ViewGroup mMorePanel;

		public ViewHolderHistory(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mBtnMore.setOnClickListener(args[0]);
			mBtnClose.setOnClickListener(args[1]);
			mBtnResend.setOnClickListener(args[2]);
			mBtnDelete.setOnClickListener(args[3]);
		}

		@Override
		public void bind(int position, MineListBean data) {
			mMagicBoard.setOnTextSizeChangeListener(data.history.magicBoard.getOnTextSizeChangeListener());

			MagicBoardUtils.getBitmapLoader(getView().getContext()).display(mMagicBoard, data.history.magicBoard.imgUrl);
			mMagicBoard.setTextCoordsBaseOnImage(data.history.magicBoard.left, data.history.magicBoard.top, data.history.magicBoard.width, data.history.magicBoard.height);
			mMagicBoard.setTextFont(FontUtils.getTypefaceWithCode(getView().getContext(), data.history.magicBoard.textFontCode));
			mMagicBoard.setTextColor(Color.parseColor(data.history.magicBoard.textColor));
			if (data.history.magicBoard.isTextSizeSaved()) mMagicBoard.setTextSize(data.history.magicBoard.getTextSizeInPx());
			mMagicBoard.setText(data.history.magicBoard.textDefault);

			mTextTime.setText(TimeUtils.toYMDHM(data.history.time));
			mTextLocation.setText(data.history.location);
			mTextComment.setText("" + data.history.commentCount);
			mTextAiXing.setText("" + data.history.favoriteCount);
			mMorePanel.setVisibility(View.GONE);
		}
	}
}
