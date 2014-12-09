package com.star.talk.startalk.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.data.MainCategoryListBean;
import com.star.talk.startalk.utils.MagicBoardUtils;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;

public class MainCategoryListAdapter extends AbsAdapter<MainCategoryListBean> {

	public MainCategoryListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return CategoryViewHolder.getAndBindView(position, convertView, parent, getInflater(), CategoryViewHolder.class, getItem(position));
	}

	@ViewLayoutId(R.layout.i_m_category_v_1)
	public static class CategoryViewHolder extends ViewHolder<MainCategoryListBean, Void> {
		@ViewId(R.id.i_m_category_v_1_img)
		private ViewGroup mImg;
		@ViewId(R.id.i_m_category_v_1_lock_panel)
		private ViewGroup mLockPanel;
		@ViewId(R.id.i_m_category_v_1_text_lock)
		private TextView mTextLock;
		@ViewId(R.id.i_m_category_v_1_text_title)
		private TextView mTextTitle;
		@ViewId(R.id.i_m_category_v_1_text_author)
		private TextView mTextAuthor;
		@ViewId(R.id.i_m_category_v_1_text_sign_count)
		private TextView mTextSignCount;

		public CategoryViewHolder(View view) {
			super(view);
		}

		@Override
		protected void init(Void... args) {}

		@Override
		public void bind(int position, MainCategoryListBean data) {
			MagicBoardUtils.getBitmapLoader(getView().getContext()).display(mImg, data.imgUrl);
			switch (data.lockShareType) {
			case 1:
				mLockPanel.setVisibility(View.VISIBLE);
				mTextLock.setText(R.string.i_m_category_v_1_text_lock_invite_friend);
				break;
			case 2:
				mLockPanel.setVisibility(View.VISIBLE);
				mTextLock.setText(R.string.i_m_category_v_1_text_lock_comment_app);
				break;
			default:
				mLockPanel.setVisibility(View.GONE);
				break;
			}
			mTextTitle.setText(data.title);
			mTextAuthor.setText(data.author);
			Resources resources = getView().getResources();
			SpannableStringBuilder spans = new SpannableStringBuilder(resources.getString(R.string.i_m_category_text_sign_count, data.signCount));
			spans.setSpan(new ForegroundColorSpan(resources.getColor(R.color.text_i_m_category_v_1_text_sign_count_num)), 2, spans.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mTextSignCount.setText(spans);
		}
	}
}
