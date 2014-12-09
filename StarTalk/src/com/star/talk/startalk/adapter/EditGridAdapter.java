package com.star.talk.startalk.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.data.EditListBean;
import com.star.talk.startalk.utils.MagicBoardUtils;
import com.star.talk.startalk.widget.MagicBoardView;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;

public class EditGridAdapter extends AbsAdapter<EditListBean> {
	private OnClickListener mOnFavoriteClick;

	public EditGridAdapter(Context context, OnClickListener onFavoriteClick) {
		super(context);
		mOnFavoriteClick = onFavoriteClick;
	}

	public void setSelected(int position) {
		List<EditListBean> data = getData();
		EditListBean item;
		for (int i = 0; i < data.size(); i++) {
			item = data.get(i);
			item.selected = i == position;
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return EditGridViewHolder.getAndBindView(position, convertView, parent, getInflater(), EditGridViewHolder.class, getItem(position), mOnFavoriteClick);
	}

	@ViewLayoutId(R.layout.i_m_edit)
	public static class EditGridViewHolder extends ViewHolder<EditListBean, OnClickListener> {
		@ViewId(R.id.i_m_edit_magic_board)
		private MagicBoardView mMagicBoard;
		@ViewId(R.id.i_m_edit_translucence_panel)
		private ViewGroup mPanelTranslucence;
		@ViewId(R.id.i_m_edit_img_favorite_l)
		private ImageView mImgFavL;
		@ViewId(R.id.i_m_edit_img_favorite_b)
		private ImageView mImgFavB;
		@ViewId(R.id.i_m_edit_cb_favorite)
		private CheckBox mCBFav;

		private Animation mAnimYes, mAnimNo;
		private boolean mAnimYesStarted = false, mAnimNoStarted = false;

		public EditGridViewHolder(View view) {
			super(view);
		}

		@Override
		protected void init(OnClickListener... args) {
			mCBFav.setOnClickListener(args[0]);
		}

		@Override
		public void bind(int position, EditListBean data) {
			mCBFav.setTag(this);
			MagicBoardUtils.display(getView().getContext(), mMagicBoard, data.magicBoard);
			
			mPanelTranslucence.setVisibility(data.selected ? View.VISIBLE : View.GONE);
			updateFavorite(false);
		}

		public void setFavorite(boolean favorite) {
			getData().favorite = favorite;
			updateFavorite(true);
		}

		private void updateFavorite(boolean anim) {
			if (getData().selected) {
				mImgFavL.setVisibility(View.GONE);
				mCBFav.setChecked(getData().favorite);
				if (getData().favorite) {
					if (mAnimYesStarted) return;	//动画还没停止，那么继续
					if (anim) {
						//mCBFav.setVisibility(View.GONE);
						mImgFavB.setVisibility(View.VISIBLE);
						stopAnimation();
						mImgFavB.startAnimation(getAnimationYes());
						mAnimYesStarted = true;
					} else {
						//mCBFav.setVisibility(View.VISIBLE);
						mImgFavB.setVisibility(View.GONE);
					}
				} else {
					if (mAnimNoStarted) return;
					if (anim) {
						mImgFavB.setVisibility(View.VISIBLE);
						stopAnimation();
						mImgFavB.startAnimation(getAnimationNo());
						mAnimNoStarted = true;
					} else {
						mImgFavB.setVisibility(View.GONE);
					}
				}
			} else {
				mImgFavL.setVisibility(getData().favorite ? View.VISIBLE : View.GONE);
			}
		}

		private Animation getAnimationYes() {
			if (mAnimYes == null) {
				mAnimYes = AnimationUtils.loadAnimation(getView().getContext(), R.anim.i_m_edit_favorite_yes);
				mAnimYes.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						mAnimYesStarted = false;
						updateFavorite(false);
					}
				});
			} else {
				mAnimYes.reset();
			}
			return mAnimYes;
		}

		private Animation getAnimationNo() {
			if (mAnimNo == null) {
				mAnimNo = AnimationUtils.loadAnimation(getView().getContext(), R.anim.i_m_edit_favorite_no);
				mAnimNo.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						mAnimNoStarted = false;
						updateFavorite(false);
					}
				});
			} else {
				mAnimNo.reset();
			}
			return mAnimNo;
		}

		private void stopAnimation() {
			if (mAnimYes != null) mAnimYes.cancel();	//如果动画没有结束，则会引发onAnimationEnd的执行，因此OK
			if (mAnimNo != null) mAnimNo.cancel();
			mImgFavB.clearAnimation();
		}
	}
}
