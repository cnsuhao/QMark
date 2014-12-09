package com.star.talk.startalk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.data.FavoriteBean;
import com.star.talk.startalk.utils.MagicBoardUtils;
import com.star.talk.startalk.widget.MagicBoardView;
import com.wei.c.L;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;

public class FavoriteGridAdapter extends AbsAdapter<FavoriteBean> {
	private boolean mDeleteMode = false;

	public FavoriteGridAdapter(Context context) {
		super(context);
	}

	public void setDeleteMode(boolean deleteMode) {
		mDeleteMode = deleteMode;
		notifyDataSetChanged();
	}

	public void setMoreLoading(boolean loading) {
		int count = getCount();
		if (count > 0) {
			FavoriteBean data = getItem(count - 1);
			if (data.hasMore) {
				data.loading = loading;
				notifyDataSetChanged();
			}
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position).hasMore) {
			return 1;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (getItemViewType(position) == 1) {
			view = MoreGridViewHolder.getAndBindView(position, convertView, parent, getInflater(), MoreGridViewHolder.class, getItem(position));
		} else {
			view = FavoriteGridViewHolder.getAndBindView(position, convertView, parent, getInflater(), FavoriteGridViewHolder.class, getItem(position));
			FavoriteGridViewHolder holder = FavoriteGridViewHolder.get(view);
			holder.setDeleteMode(mDeleteMode);
		}
		return view;
	}

	@ViewLayoutId(R.layout.i_m_favorite)
	public static class FavoriteGridViewHolder extends ViewHolder<FavoriteBean, Void> {
		@ViewId(R.id.i_m_favorite_magic_board)
		private MagicBoardView mMagicBoard;
		@ViewId(R.id.i_m_favorite_translucence_panel)
		private ViewGroup mPanelTranslucence;
		@ViewId(R.id.i_m_favorite_checkbox_gou)
		private CheckBox mCBGou;

		public FavoriteGridViewHolder(View view) {
			super(view);
		}

		public void setDeleteMode(boolean deleteMode) {
			if (deleteMode) {
				mPanelTranslucence.setVisibility(View.VISIBLE);
				updateChecked();
			} else {
				mPanelTranslucence.setVisibility(View.GONE);
			}
		}

		public void setChecked(boolean checked) {
			getData().selected = checked;
			updateChecked();
		}

		private void updateChecked() {
			if (getData().selected) {
				mPanelTranslucence.setBackgroundColor(getView().getResources().getColor(R.color.bg_white_alpha_7f));
				mCBGou.setChecked(true);
			} else {
				mPanelTranslucence.setBackgroundColor(getView().getResources().getColor(R.color.bg_black_alpha_7f));
				mCBGou.setChecked(false);
			}
		}

		@Override
		protected void init(Void... args) {}

		@Override
		public void bind(int position, FavoriteBean data) {
			MagicBoardUtils.display(getView().getContext(), mMagicBoard, data.magicBoard);
		}
	}

	@ViewLayoutId(R.layout.i_m_favorite_more)
	public static class MoreGridViewHolder extends ViewHolder<FavoriteBean, Void> {
		//@ViewId(R.id.i_m_favorite_more_img)
		//private ImageView mMore;
		@ViewId(R.id.i_m_favorite_more_progress)
		private ProgressBar mProgress;
		@ViewId(R.id.i_m_favorite_more_img_shou)
		private ImageView mImgShou;
		@ViewId(R.id.i_m_favorite_more_text)
		private TextView mText;

		public MoreGridViewHolder(View view) {
			super(view);
		}

		@Override
		protected void init(Void... args) {}

		@Override
		protected void bind(int position, FavoriteBean data) {
			updateLoading();
		}

		public void setMoreLoading(boolean loading) {
			getData().loading = loading;
			updateLoading();
		}

		private void updateLoading() {
			/*if (getData().loading) {
				mMore.setImageResource(R.drawable.anim_list_i_pull_2_refresh_header);
				AnimationDrawable anim = (AnimationDrawable)mMore.getDrawable();
				anim.start();
			} else {
				mMore.setImageResource(R.drawable.btn_m_contribute_add_img);
			}*/
			if (getData().loading) {
				mProgress.setVisibility(View.VISIBLE);
				mImgShou.setVisibility(View.GONE);
				mText.setVisibility(View.GONE);
			} else {
				mProgress.setVisibility(View.GONE);
				mImgShou.setVisibility(View.VISIBLE);
				mText.setVisibility(View.VISIBLE);
			}
			L.d(FavoriteGridAdapter.class, "updateLoading---------getData().loading:" + getData().loading);
		}
	}
}
