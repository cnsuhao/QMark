package com.star.talk.startalk.viewholder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.star.talk.startalk.R;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;

@ViewLayoutId(R.layout.m_favorite_delete_panel)
public class FavoriteDeleteViewHolder extends ViewHolder<Void, OnClickListener> {
	@ViewId(R.id.m_favorite_delete_panel_window_bg)
	private ViewGroup mWindowBg;
	@ViewId(R.id.m_favorite_delete_panel_content)
	private ViewGroup mContentView;
	@ViewId(R.id.m_favorite_delete_panel_btn_delete)
	private Button mBtnDelete;
	@ViewId(R.id.m_favorite_delete_panel_btn_cancel)
	private Button mBtnCancel;

	private Context mContext;
	private OnClickListener mOnDeleteClickCallback;
	private boolean mDelete;

	public FavoriteDeleteViewHolder(View view) {
		super(view);
		mContext = view.getContext();
	}

	public static FavoriteDeleteViewHolder showDeleteDialog(Activity context, ViewGroup parent, OnClickListener onDeleteClickCallback) {
		View view = FavoriteDeleteViewHolder.makeView(FavoriteDeleteViewHolder.class, context.getLayoutInflater(), parent);
		FavoriteDeleteViewHolder vHolder = FavoriteDeleteViewHolder.bindView(0, view, FavoriteDeleteViewHolder.class, null, onDeleteClickCallback);
		parent.addView(view);
		vHolder.startAnimIn();
		return vHolder;
	}

	public void destroy() {
		startAnimOut();
	}

	private void startAnimIn() {
		mWindowBg.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_bg_in));
		mContentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_in));
	}

	private void startAnimOut() {
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_out);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				((ViewGroup)getView().getParent()).removeView(getView());
				if(mDelete) mOnDeleteClickCallback.onClick(mBtnDelete);
			}
		});
		mWindowBg.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_category_v_1_share_link_panel_bg_out));
		mContentView.startAnimation(anim);
	}

	@Override
	protected void init(OnClickListener... args) {
		mOnDeleteClickCallback = args[0];
		mBtnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDelete = true;
				destroy();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDelete = false;
				destroy();
			}
		});
		mWindowBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDelete = false;
				destroy();
			}
		});
	}

	@Override
	public void bind(int position, Void data) {}
}