package com.star.talk.startalk.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.utils.FontUtils;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;
import com.wei.c.widget.AbsPull2RefreshListView;
import com.wei.c.widget.OverturnLoadingView;

public class Pull2RefreshListView extends AbsPull2RefreshListView {
	private View mHeaderView, mFooterView;

	public Pull2RefreshListView(Context context) {
		super(context);
	}

	public Pull2RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Pull2RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View getLoadingViewHeader(LayoutInflater inflater) {
		if (mHeaderView == null) mHeaderView = HeaderViewHolder.makeView(HeaderViewHolder.class, inflater, null);
		return mHeaderView;
	}

	@Override
	protected View getLoadingViewFooter(LayoutInflater inflater) {
		if (mFooterView == null) mFooterView = FooterViewHolder.makeView(FooterViewHolder.class, inflater, null);
		return mFooterView;
	}

	@Override
	protected void onPull2Refresh() {
		HeaderViewHolder vHolder = HeaderViewHolder.bindView(0, mHeaderView, HeaderViewHolder.class, null);
		vHolder.startOverturn();
	}

	@Override
	protected void onEnough2Release() {}

	@Override
	protected void onRefreshing() {}

	@Override
	protected void onHeaderHidden() {
		HeaderViewHolder vHolder = HeaderViewHolder.bindView(0, mHeaderView, HeaderViewHolder.class, null);
		vHolder.stopOverturn();
	}

	@Override
	protected void onFooterShown(boolean hasMore) {
		FooterViewHolder vHolder = FooterViewHolder.bindView(0, mFooterView, FooterViewHolder.class, null);
		vHolder.onShown(hasMore);
	}

	@Override
	protected void onFooterLoading() {}

	@Override
	protected void onFooterHidden() {
		FooterViewHolder vHolder = FooterViewHolder.bindView(0, mFooterView, FooterViewHolder.class, null);
		vHolder.onHide();
	}

	@ViewLayoutId(R.layout.i_header_pull_2_refresh)
	public static class HeaderViewHolder extends ViewHolder<Void, Void> {
		@ViewId(R.id.i_header_pull_2_refresh_anim)
		private ImageView mLoadingView;
		private AnimationDrawable mAnim;

		public HeaderViewHolder(View view) {
			super(view);
		}

		public void startOverturn() {
			mAnim.start();
		}

		public void stopOverturn() {
			mAnim.stop();
		}

		@Override
		protected void init(Void... args) {
			mAnim = (AnimationDrawable)mLoadingView.getDrawable();
		}

		@Override
		public void bind(int position, Void data) {}
	}

	@ViewLayoutId(R.layout.i_footer_pull_2_refresh)
	public static class FooterViewHolder extends ViewHolder<Void, Void> {
		@ViewId(R.id.i_footer_pull_2_refresh_panel_loading)
		private ViewGroup mPanelLoading;
		@ViewId(R.id.i_footer_pull_2_refresh_overturn)
		private OverturnLoadingView mOverturnView;
		@ViewId(R.id.i_footer_pull_2_refresh_no_more)
		private TextView mNoMore;

		public FooterViewHolder(View view) {
			super(view);
		}

		public void onShown(boolean hasMore) {
			if (hasMore) {
				mPanelLoading.setVisibility(View.VISIBLE);
				mNoMore.setVisibility(View.GONE);
				mOverturnView.start();
			} else {
				mPanelLoading.setVisibility(View.GONE);
				mNoMore.setVisibility(View.VISIBLE);
			}
		}

		public void onHide() {
			mOverturnView.stop();
		}

		@Override
		protected void init(Void... args) {
			mNoMore.setTypeface(FontUtils.getTypefaceWithCode(getView().getContext(), 1));
		}

		@Override
		public void bind(int position, Void data) {}
	}
}
