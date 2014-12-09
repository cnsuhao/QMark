package com.star.talk.startalk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.star.talk.startalk.R;

public class AspectRatioFrameLayout extends FrameLayout {
	private float mAspectRatio;

	public AspectRatioFrameLayout(Context context) {
		super(context);
	}

	public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioFrameLayout, defStyle, 0);
		mAspectRatio = a.getFloat(R.styleable.AspectRatioFrameLayout_aspectRatioFl, 0);
		a.recycle();
	}

	public void setAspectRatio(float aspectRatio) {
		mAspectRatio = aspectRatio;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mAspectRatio > 0) {
			int size = MeasureSpec.getSize(widthMeasureSpec);
			int mode = MeasureSpec.getMode(widthMeasureSpec);
			super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int)(size / mAspectRatio), mode));
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		/* 对于ViewGroup和某些View如EditText不建议这么做，因为EditText里的排版控件需要在测量的时候确定大小，
		 * 而在EditText.layout()的时候并不能重新确定其大小，这可能是一个bug
		int size = getMeasuredWidth();
		setMeasuredDimension(size, size);
		 */
	}
}
