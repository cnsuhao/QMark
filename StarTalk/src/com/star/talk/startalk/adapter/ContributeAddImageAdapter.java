package com.star.talk.startalk.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.star.talk.startalk.R;
import com.wei.c.adapter.AbsAdapter;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.ViewHolder;

public class ContributeAddImageAdapter extends AbsAdapter<Uri> {

	public ContributeAddImageAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return ImageViewHolder.getAndBindView(position, convertView, parent, getInflater(), ImageViewHolder.class, getItem(position));
	}

	@ViewLayoutId(R.layout.i_m_contribute_grid)
	private static class ImageViewHolder extends ViewHolder<Uri, Void> {
		@ViewId(R.id.i_m_contribute_grid)
		private ImageView mImage;

		public ImageViewHolder(View view) {
			super(view);
		}

		@Override
		protected void init(Void... args) {}

		@Override
		public void bind(int position, Uri data) {
			mImage.setImageURI(data);
		}
	}
}
