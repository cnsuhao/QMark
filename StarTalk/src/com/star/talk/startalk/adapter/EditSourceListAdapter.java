package com.star.talk.startalk.adapter;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.data.EditSourceListBean;
import com.tisumoon.utils.ImageFactory;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;

public class EditSourceListAdapter extends AbsAdapter<EditSourceListBean> {

	public EditSourceListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return MyViewHolder.getAndBindView(position, convertView, parent, getInflater(), MyViewHolder.class, getItem(position));
	}

	@ViewLayoutId(R.layout.i_f_m_tabs_edit_source)
	public static class MyViewHolder extends ViewHolder<EditSourceListBean, Void> {
		@ViewId(R.id.i_f_m_tabs_edit_source_img_bg)
		private ViewGroup mImgBg;
		@ViewId(R.id.i_f_m_tabs_edit_source_img_category)
		private ImageView mImgCatgy;
		@ViewId(R.id.i_f_m_tabs_edit_source_text_left)
		private TextView mTextLeft;
		@ViewId(R.id.i_f_m_tabs_edit_source_text_sign_size)
		private TextView mTextSignSize;

		private FinalBitmap mFB;

		public MyViewHolder(View view) {
			super(view);
		}

		@Override
		protected void init(Void... args) {
			mFB = ImageFactory.newFbDiskCache(getView().getContext(), -1, -1, 0, 0);
		}

		@Override
		public void bind(int position, EditSourceListBean data) {
			mFB.display(mImgBg, data.imgBgUrl);
			mFB.display(mImgCatgy, data.ImgCatgyUrl);
			mTextLeft.setText(data.textLeft);
			Resources resources = getView().getResources();
			SpannableStringBuilder spans = new SpannableStringBuilder(resources.getString(R.string.i_m_category_text_sign_count, data.textSignSize));
			spans.setSpan(new ForegroundColorSpan(resources.getColor(R.color.text_title_orange)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spans.setSpan(new ForegroundColorSpan(resources.getColor(R.color.text_title_orange)), spans.length() - 3, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mTextSignSize.setText(spans);
		}
	}
}
