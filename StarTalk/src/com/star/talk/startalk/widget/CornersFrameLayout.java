package com.star.talk.startalk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.star.talk.startalk.R;
import com.wei.c.L;

public class CornersFrameLayout extends FrameLayout {
	//private final PorterDuffXfermode mPorterDstIn = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
	//注意，很多手机不支持Mode.DST_IN
	private final PorterDuffXfermode mPorterSrcIn = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

	private final Paint mPaint = new Paint();
	private final Canvas mCanvas = new Canvas();
	private final RectF mRect = new RectF();
	private Bitmap mBitmap;
	private int mCornersRadius;
	private float mAspectRatio;
	private boolean mDrawing = false;

	public CornersFrameLayout(Context context) {
		super(context);
		init();
	}

	public CornersFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CornersFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CornersFrameLayout, defStyle, 0);
		mCornersRadius = a.getDimensionPixelOffset(R.styleable.CornersFrameLayout_cornersRadius, 0);
		mAspectRatio = a.getFloat(R.styleable.CornersFrameLayout_cornersAspectRatio, 0);
		a.recycle();
		init();
	}

	private void init() {
		//mPaint.setStyle(Paint.Style.STROKE);	//暂不设置
		//设置抗锯齿，三者必须同时设置效果才可以
		mPaint.setAntiAlias(true);	//等同于mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setFilterBitmap(true);
		mPaint.setDither(true);
	}

	public void setCornersRadius(int cornersRadius) {
		mCornersRadius = cornersRadius;
		invalidate();
	}

	public void setAspectRatio(float aspectRatio) {
		mAspectRatio = aspectRatio;
		requestLayout();
		invalidate();
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
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		L.d(this, "onLayout--------------------------changed:" + changed + ", " + mRect);
		super.onLayout(changed, left, top, right, bottom);
		if (changed) recycleBitmap();
		mRect.set(0, 0, getWidth(), getHeight());
	}

	@Override
	public void draw(Canvas canvas) {
		//TODO
		L.d(this, "draw--------------------------" + mRect);
		mDrawing = true;
		drawCorners(canvas, true);
		mDrawing = false;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		L.d(this, "dispatchDraw--------------------------" + mRect);
		drawCorners(canvas, false);
	}

	private void drawCorners(Canvas canvas, boolean fromDraw) {
		if (!fromDraw && mDrawing || !hasCornersRadius()) {
			if (fromDraw) {
				super.draw(canvas);
			} else {
				super.dispatchDraw(canvas);
			}
		} else {
			//注意：如果是局部变量，而且方法结束后没有赋空值，则会出现图像绘制错误（绘制的是其他图像）的奇葩问题
			if (mBitmap == null) mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			mCanvas.setBitmap(mBitmap);
			int saveCount = mCanvas.save();
			mCanvas.setDensity(canvas.getDensity());
			mCanvas.setDrawFilter(canvas.getDrawFilter());
			if (fromDraw) {
				super.draw(mCanvas);
			} else {
				super.dispatchDraw(mCanvas);
			}
			mCanvas.restoreToCount(saveCount);
			try { mCanvas.setBitmap(null); }catch(Exception e) {}

			saveCount = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

			mPaint.setXfermode(null);
			canvas.drawRoundRect(mRect, mCornersRadius, mCornersRadius, mPaint);
			mPaint.setXfermode(mPorterSrcIn);
			canvas.drawBitmap(mBitmap, 0, 0, mPaint);

			canvas.restoreToCount(saveCount);
		}
	}

	private boolean hasCornersRadius() {
		return mCornersRadius > 0;
	}

	private void recycleBitmap() {
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		recycleBitmap();
		super.finalize();
	}
}
