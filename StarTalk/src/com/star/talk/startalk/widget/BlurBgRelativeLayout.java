package com.star.talk.startalk.widget;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.star.talk.startalk.R;
import com.wei.c.L;
import com.wei.c.utils.BitmapUtils;

public class BlurBgRelativeLayout extends RelativeLayout {

	private WeakReference<BitmapDrawable> mBluredBackgroundRef;
	private WeakReference<Bitmap> mSrcBitmapRef;
	private float mBlurRadius = 10;

	public BlurBgRelativeLayout(Context context) {
		super(context);
		init();
	}

	public BlurBgRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BlurBgRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurBgRelativeLayout, defStyle, 0);
		setBlurRadius(a.getFloat(R.styleable.BlurBgRelativeLayout_blurRadius, mBlurRadius));
		a.recycle();
		init();
	}

	private void init() {}

	public void setBlurRadius(float blurRadius) {
		if (mBlurRadius != blurRadius) {
			mBlurRadius = blurRadius;
			mBluredBackgroundRef = null;
			mSrcBitmapRef = null;
		}
	}

	private void blurBackground() {
		Drawable background = getBackground();
		if (background != null) {
			super.setBackgroundDrawable(blur(background));
		}
	}

	@Override
	public void draw(Canvas canvas) {
		blurBackground();
		super.draw(canvas);
	}

	/*dispatchDraw的目的就是不画背景
	@Override
	protected void dispatchDraw(Canvas canvas) {
		blurBackground();
		super.dispatchDraw(canvas);
	}*/

	//本方法的调用在本类的实例变量初始化之前，所以造成mBlurRadius为0，而BlurMaskFilter的第一个参数必须大于0，导致异常
	private Drawable blur(Drawable background) {
		TransitionDrawable transitionDrawable = null;
		Drawable drawable1 = background;
		if (background != null && background instanceof TransitionDrawable) {
			transitionDrawable = (TransitionDrawable)background;
			drawable1 = transitionDrawable.getDrawable(1);
		}
		if (drawable1 != null && drawable1 instanceof BitmapDrawable) {
			Drawable backgroundCached = mBluredBackgroundRef != null ? mBluredBackgroundRef.get() : null;
			//setBackground()会调用setBackgroundDrawable(), 而setBackground()已经调用了本方法
			if (mBlurRadius > 0 && backgroundCached != drawable1) {
				Bitmap srcBitmap = ((BitmapDrawable)drawable1).getBitmap();
				Bitmap srcBitmapCached = mSrcBitmapRef != null ? mSrcBitmapRef.get() : null;
				//srcBitmapCached虽然在这里被丢弃，但是可能放在MemoryCache中
				//同时由于这里是同时对mSrcBitmapRef和mBluredBackgroundRef进行更新的，所以只要srcBitmapCached没有变，
				//那么backgroundCached就是对应的blur后的结果
				if (srcBitmapCached != null && srcBitmapCached == srcBitmap && backgroundCached != null) {
					drawable1 = backgroundCached;
				} else if (srcBitmap != null) {	//虽然是从BitmapDrawable里面取出来的，但是还是可能为null，FinalBitmap会有这种情况
					mSrcBitmapRef = new WeakReference<Bitmap>(srcBitmap);
					Bitmap destBitmap = sBmpSrcTopBlur.get(srcBitmap);
					if (destBitmap == null) {
						destBitmap = BitmapUtils.blur(getContext(), srcBitmap, mBlurRadius);
						L.e(this, "blur--------create" + "[" + destBitmap.getWidth() + ", " + destBitmap.getHeight() + "]");
						sBmpSrcTopBlur.put(srcBitmap, destBitmap);
					}
					drawable1 = new BitmapDrawable(getResources(), destBitmap);
					mBluredBackgroundRef = new WeakReference<BitmapDrawable>((BitmapDrawable)drawable1);
					destroyDrawingCache();
				}
				if (srcBitmap != null) {
					if (transitionDrawable != null) {
						int id1 = transitionDrawable.getId(1);
						if (id1 <= 0) {
							int id0 = transitionDrawable.getId(1);
							id1 = id0 <= 0 ? 0x7fff0000 : id0 + 1;
							transitionDrawable.setId(1, id1);
						}
						transitionDrawable.setDrawableByLayerId(id1, drawable1);
						background = transitionDrawable;
					} else {
						background = drawable1;
					}
				}
			}
		} else {
			mBluredBackgroundRef = null;
		}
		return background;
	}

	private static final WeakHashMap<Bitmap, Bitmap> sBmpSrcTopBlur = new WeakHashMap<Bitmap, Bitmap>();

	//本方法的调用在本类的实例变量初始化之前，所以造成mBlurRadius为0，而BlurMaskFilter的第一个参数必须大于0，导致异常
	//以下方案还是不起作用
	//((BitmapDrawable)background).getPaint().setMaskFilter(new BlurMaskFilter(mBlurRadius, Blur.NORMAL));
}
