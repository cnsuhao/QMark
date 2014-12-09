package com.star.talk.startalk.widget;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.star.talk.startalk.R;

public class MagicBoardView extends FrameLayout {
	private boolean mTextSizeScaleComplete = true;
	private boolean mSuspendPostRetry = false;
	//private boolean mCanPostRetry = false;
	private boolean mAttachedToWindow = false;
	private boolean mScreenOn = true;
	private boolean mRecreateLayout = false;
	private boolean mReincrease = false;
	private boolean mIncrease = false;
	private boolean mDecrease = false;

	private float mAspectRatio;

	private OnTextSizeChangeListener mOnTextSizeChangeListener;

	/**字号，单位为px**/
	private int mTextSize = -1;
	private int mTextSizePrev = -1;
	private int mMinTextSize = -1;
	private int mMaxTextSize = -1;
	private int mMinTextSizeScaled = -1;
	private int mMaxTextSizeScaled = -1;
	private int mUnitInPx;

	private boolean mEditMode = false;
	private TextCoords mCoords;
	private TextView mTextView;

	public MagicBoardView(Context context) {
		super(context);
		init(context, 0, null, 0, null);
	}

	public MagicBoardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MagicBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MagicBoardView, defStyle, 0);

		mEditMode = a.getBoolean(R.styleable.MagicBoardView_magicEditMode, false);
		mAspectRatio = a.getFloat(R.styleable.MagicBoardView_magicAspectRatio, 0);
		int drawableId = a.getResourceId(R.styleable.MagicBoardView_magicSrc, 0);
		int textColor = a.getColor(R.styleable.MagicBoardView_magicTextColor, 0);
		String text = a.getString(R.styleable.MagicBoardView_magicText);
		String typefacePath = a.getString(R.styleable.MagicBoardView_magicTextTypeface);
		mMinTextSize = a.getDimensionPixelSize(R.styleable.MagicBoardView_magicMinTextSize, -1);	//不能直接写10，还需要转换，见init()
		mMaxTextSize = a.getDimensionPixelSize(R.styleable.MagicBoardView_magicMaxTextSize, -1);
		mCoords = new TextCoords(
				a.getInteger(R.styleable.MagicBoardView_magicBaseOnSrc_TextLeft, -1),
				a.getInteger(R.styleable.MagicBoardView_magicBaseOnSrc_TextTop, -1),
				a.getInteger(R.styleable.MagicBoardView_magicBaseOnSrc_TextWidth, -1),
				a.getInteger(R.styleable.MagicBoardView_magicBaseOnSrc_TextHeight, -1));
		a.recycle();
		init(context, drawableId, text, textColor, typefacePath);
	}

	private void init(Context context, int drawableId, String text, int textColor, String typefacePath) {
		reset();
		if (drawableId > 0) setMagicSrc(drawableId);

		mTextView = mEditMode ? new EditText(context) : new TextView(context);
		//不要用LayoutParams.WRAP_CONTENT，否则每次设置文本都会引发requestLayout()
		addView(mTextView, new LayoutParams(0, 0));
		mTextView.addTextChangedListener(mTextWatcher);
		mTextView.setBackgroundResource(0);
		mTextView.setPadding(0, 0, 0, 0);	//paddings会导致文本的遮盖，对于EditText, 即使将背景置为null, 也还会有padding
		mTextView.setTextColor(textColor);
		mTextView.setGravity(Gravity.CENTER);
		if (typefacePath != null) setTextFont(typefacePath.startsWith("asset:"), typefacePath);

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		//DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		//mUnitInPx = (int)(displayMetrics.density * 2 + 0.5f);
		//if (!mEditMode) mUnitInPx *= 2;
		if (mMinTextSize <= 0) mMinTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, displayMetrics);
		if (mMaxTextSize <= 0) mMaxTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, displayMetrics);
		if (mMinTextSize > mMaxTextSize) throw new IllegalArgumentException("字号范围设置错误:[" + mMinTextSize + ", " + mMaxTextSize + "]");
		scaleTextSize(1);
		mTextSize = mMinTextSizeScaled;
		setTextSize(mTextSize);
		setText(text);
	}

	public void setText(CharSequence text) {
		mTextView.setText(text);
		resetAndPostRetry();
	}

	public void setTextColor(int color) {
		mTextView.setTextColor(color);
	}

	public void setTextSize(int sizeInPx) {
		setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInPx);
	}

	public void setTextSize(int unit, float size) {
		mTextView.setTextSize(unit, size);
		int textSize = (int)mTextView.getTextSize();
		if (textSize != mTextSize) {
			mTextSize = textSize;
			resetAndPostRetry();
		}
	}

	/**@deprecated 本方法用在ListView里面，性能损耗太大。建议使用{@link #setTextFont(Typeface)}, 其中的参数Typeface应该使用单例缓存机制，不要重复创建。**/
	public void setTextFont(boolean fromAsset, String ttfFilePath) {
		if (!fromAsset && !new File(ttfFilePath).exists()) throw new IllegalArgumentException("字体文件不存在");
		setTextFont(fromAsset ? Typeface.createFromAsset(getContext().getAssets(), ttfFilePath) : Typeface.createFromFile(ttfFilePath));
	}

	public void setTextFont(Typeface typeface) {
		if (mTextView.getTypeface() != typeface) {
			mTextView.setTypeface(typeface);
			resetAndPostRetry();
		}
	}

	public void setTextCoordsBaseOnImage(int left, int top, int width, int height) {
		if (mCoords == null || (mCoords.mTextLeft != left || mCoords.mTextTop != top ||
				mCoords.mTextWidth != width || mCoords.mTextHeight != height)) {
			mCoords = new TextCoords(left, top, width, height);
			//在字号范围重写缩放前，不应该测量字号，否则有可能会由于之前的最小字号过大导致减小字数
			mTextSizeScaleComplete = false;
			//mCanPostRetry = false;
			//虽然坐标没变，但是背景图片可能变了，尺寸变了，而本坐标是基于图片尺寸进行缩放的，不过变图片的时候也会引起重写测量、布局，引起重写文本框的重写布局
			requestLayout();
			invalidate();
		}
	}

	public void setMagicSrc(int drawableId) {
		setBackgroundResource(drawableId);
	}

	public TextView getTextView() {
		return mTextView;
	}

	public CharSequence getText() {
		return mTextView.getText();
	}

	/**@deprecated 本View的padding都将被置为0**/
	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(0, 0, 0, 0);
	}

	/**@deprecated 本View的padding都将被置为0**/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void setPaddingRelative(int start, int top, int end, int bottom) {
		super.setPaddingRelative(0, 0, 0, 0);
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
		//就是layoutChildren()，而只有一个子mTextView, 下面直接进行layoutTextView()
		//super.onLayout(changed, left, top, right, bottom);
		//L.w(MagicBoardView.class, "onLayout---mTextSize:" + mTextSize + ", " + mTextView.getText());
		layoutTextView();
	}

	private void layoutTextView() {
		if (scaleTextCoords()) {
			//如果是显示的，就不要隐藏了，会造成在输入文字的过程中的闪动，而且失去输入焦点
			if (mTextView.getVisibility() == GONE) mTextView.setVisibility(INVISIBLE);

			int textLeft = mCoords.mTextLeftScaled;
			int textTop = mCoords.mTextTopScaled;
			int textRight = Math.min(textLeft + mCoords.mTextWidthScaled, getWidth());
			int textBottom = Math.min(textTop + mCoords.mTextHeightScaled, getHeight());

			measureTextView(textRight - textLeft, textBottom - textTop);
			mTextView.layout(textLeft, textTop, textRight, textBottom);
			/*if (mTextView instanceof MyTextView) {
				((MyTextView)mTextView).startLayout(textLeft, textTop, textRight, textBottom);
			} else {
				((MyEditText)mTextView).startLayout(textLeft, textTop, textRight, textBottom);
			}*/
			ensurePostRetry();
		} else {
			mTextView.setVisibility(GONE);
		}
	}

	private void measureTextView(int width, int height) {
		//如果不进行测量，则mTextView.getLayout()的布局范围将与mTextView不一致，会是本MagicBoardView.onMeasure()的尺寸，仅仅对mTextView.layout()都不行
		if (mTextView.getWidth() != width || mTextView.getHeight() != height) {
			ViewGroup.LayoutParams lp = mTextView.getLayoutParams();
			lp.width = width;
			lp.height = height;
			//不要mTextView.setLayoutParams(lp);否则会引发requestLayout()
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
			mTextView.forceLayout();
			mTextView.measure(widthMeasureSpec, heightMeasureSpec);

			mSuspendPostRetry = true;
			//放在全部布局完毕之后
			//ensurePostRetry();
		}
	}

	private void measureTextViewForCreateLayout() {
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(mTextView.getWidth(), MeasureSpec.EXACTLY);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(mTextView.getWidth(), MeasureSpec.EXACTLY);
		mTextView.forceLayout();
		mTextView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	private boolean scaleTextCoords() {
		if (getWidth() <= 0 || getHeight() <= 0) return false;
		Drawable drawable = getBackground();
		if (drawable != null && drawable instanceof TransitionDrawable) {
			drawable = ((TransitionDrawable)drawable).getDrawable(1);
		}
		if (drawable == null || !(drawable instanceof BitmapDrawable)) return false;
		Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
		if (bmp == null) return false;
		float heightScale = getHeight() * 1.0f / bmp.getHeight();
		mCoords.setScale(getWidth() * 1.0f / bmp.getWidth(), heightScale);
		scaleTextSize(heightScale);
		return true;
	}

	/**只缩放最大最小字号，不缩放现有字号mTextSize，现有字号自定计算适应**/
	private void scaleTextSize(float heightScale) {
		mMinTextSizeScaled = (int)(mMinTextSize * heightScale + 0.5f);
		mMaxTextSizeScaled = (int)(mMaxTextSize * heightScale + 0.5f);
		mTextSizeScaleComplete = true;
		//放在全部布局完毕之后
		//ensurePostRetry();
	}

	private void ifNeedInvisible() {
		//!mEditMode && 去掉，防止出现闪动，检查文本是否为空，空的就不隐藏，防止无法编辑
		if (mTextView.getVisibility() == VISIBLE && !TextUtils.isEmpty(mTextView.getText())) mTextView.setVisibility(INVISIBLE);
	}

	private void resetAndPostRetry() {
		reset();
		ifNeedInvisible();
		postRetryAdjustTextSizeOrLengthInDefinedBounds();
	}

	private void ensurePostRetry() {
		if (mSuspendPostRetry || mTextView.getVisibility() != VISIBLE) postRetryAdjustTextSizeOrLengthInDefinedBounds();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onScreenStateChanged(int screenState) {
		super.onScreenStateChanged(screenState);
		mScreenOn = screenState == SCREEN_STATE_ON;
		if (mScreenOn) ensurePostRetry();
	};

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mAttachedToWindow = true;
		ensurePostRetry();
	}

	@Override
	protected void onDetachedFromWindow() {
		mAttachedToWindow = false;
		super.onDetachedFromWindow();
	}

	/*@deprecated 视图范围是否在屏幕上，需要计算当前View在屏幕上的绝对位置，再计算每个父级的绝对位置，并且计算当前View与各个父级有没有交集，比较耗时，丢弃**/
	/*private boolean isViewBoundsInScreen(View view) {
		return new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()).intersects(getLeft(), getTop(), getRight(), getBottom());
	}*/

	/*自己或父View是否不可见，有一个不可见即返回true。在ListView里面，会有个看不见的item一直在更新position为0的数据**/
	/*private boolean isViewOrParentInvisible() {
		//那个看不见的item0，显示状态还是为VISIBLE
		if (getVisibility() != VISIBLE) return true;
		ViewParent parent = getParent();
		while (parent != null && parent instanceof ViewGroup) {
			if (((ViewGroup)parent).getVisibility() != VISIBLE) return true;
			parent = parent.getParent();
		}
		return false;
	}*/

	private boolean isStateReadyForAdjustText() {
		return mScreenOn && mAttachedToWindow && mTextSizeScaleComplete;	// && isViewOrParentInvisible();
	}

	private final TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override
		public void afterTextChanged(Editable s) {
			postRetryAdjustTextSizeOrLengthInDefinedBounds();
		}
	};

	private void adjustTextSizeOrLengthInDefinedBounds() {
		CharSequence text = mTextView.getText();
		if (TextUtils.isEmpty(text)) {
			adjustComplete(false);
			return;
		}
		if (mTextSize < mMinTextSizeScaled) {
			mTextSize = mMinTextSizeScaled;
			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
			postRetryAdjustTextSizeOrLengthInDefinedBounds();
			return;
		}
		if (mTextSize > mMaxTextSizeScaled) {
			mTextSize = mMaxTextSizeScaled;
			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
			postRetryAdjustTextSizeOrLengthInDefinedBounds();
			return;
		}

		//自动换行，因此不用测试宽度，只需要知道高度有没有出界
		int textHeight = getTextLayoutHeight();
		int textHeightDefined = getTextLayoutHeightDefined();

		//layout可能为空
		if (textHeight < 0 || textHeightDefined < 0) {
			//adjustOk();	//无法完成
			mSuspendPostRetry = true;
			if (isStateReadyForAdjustText()) {
				if (mRecreateLayout) {
					mRecreateLayout = false;
				} else {
					measureTextViewForCreateLayout();
					postRetryAdjustTextSizeOrLengthInDefinedBounds();
					mRecreateLayout = true;
				}
			}
			return;
		}

		if (textHeight >= textHeightDefined) {
			if (mTextSize <= mMinTextSizeScaled) {
				if (textHeight == textHeightDefined) {
					//如果已经是最小号字体了，且刚好占满，那就这样吧
					adjustComplete(true);
					return;
				} else {	//逐个减少字数
					int selectionEnd = mTextView.getSelectionEnd();
					if (selectionEnd <= 0) selectionEnd = text.length();
					if (text instanceof Editable) {
						((Editable)text).delete(selectionEnd-1, selectionEnd);
					} else {
						mTextView.setText(new StringBuilder(text.subSequence(0, selectionEnd-1)).append(text.subSequence(selectionEnd, text.length())));
					}
					postRetryAdjustTextSizeOrLengthInDefinedBounds();
				}
			} else {	//逐个减小字号
				if (mIncrease) {
					if (mTextSize == mTextSizePrev || mTextSize > mTextSizePrev && mTextSize - mTextSizePrev < 2) {	//增大的幅度小于2，则停止
						if (mTextSize != mTextSizePrev) {
							int currSize = mTextSizePrev;	//增大的幅度小于2，但是现在发现大了，那么减回去
							mTextSizePrev = mTextSize;
							mTextSize = currSize;
							if (mTextSize < mMinTextSizeScaled) mTextSize = mMinTextSizeScaled;
							mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
						}
						adjustComplete(true);
						return;
					} else {
						int currSize;
						if (mTextSizePrev > mTextSize) {	//上次是减小，那么这次减小的幅度减半，否则如果不减半，则连续两次减小就回到了上次增加前的值了
							int delta = mTextSizePrev - mTextSize;
							if (delta >= 2) delta /= 2;
							currSize = mTextSize - delta;
						} else {	//上次是增大，那么减小的幅度减半
							currSize = mTextSize - (mTextSize - mTextSizePrev) / 2;
						}
						mTextSizePrev = mTextSize;
						mTextSize = currSize;
						if (mTextSize < mMinTextSizeScaled) mTextSize = mMinTextSizeScaled;
						mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
						postRetryAdjustTextSizeOrLengthInDefinedBounds();
					}
				} else {
					mTextSizePrev = mTextSize;
					mUnitInPx = mDecrease ? mUnitInPx * 2 : 1;	//从1开始指数型增加，避免当设置了正确的字号之后还会大幅度调整字号，造成闪动或耗时
					mTextSize -= mUnitInPx;
					if (mTextSize < mMinTextSizeScaled) mTextSize = mMinTextSizeScaled;
					mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
					postRetryAdjustTextSizeOrLengthInDefinedBounds();
				}
				if (!mDecrease) mDecrease = true;
			}
		} else {	//逐个增大字号
			/* 无论是减小的要增大，还是开始就要增大，但凡走到这里，说明不够大。那么在这里由于下面的条件而结束是合理的。
			 * 有几种情况：
			 * 下面赋值了mTextSize = mMaxTextSizeScaled，如果字号还不够大，才会走到这，那么就这么大吧；
			 * 也可能字号大了，走到减小的位置，但由于下面的拦截使得不会超过mMaxTextSizeScaled，那么在减小的位置不用管；
			 * 还有一种情况：一开始字号特大，直接进行减小操作，而最大字号还是不够大，不过由于减小操作最后
			 * 还是会进行至少一次增大操作，会走到这里被拦截，so...完美
			 */
			if (mTextSize >= mMaxTextSizeScaled) {
				mTextSize = mMaxTextSizeScaled;
				mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
				adjustComplete(true);
				return;
			} else {
				if (mDecrease) {	//已经减小过
					if (mTextSizePrev == mTextSize || mTextSizePrev > mTextSize && mTextSizePrev - mTextSize < 2) {	//减小的幅度小于2，则停止
						adjustComplete(true);
						return;
					} else {
						int currSize;
						if (mTextSize > mTextSizePrev) {	//上次是增大，那么这次增大的幅度减半，否则如果不减半，则连续两次增大就回到了上次减小前的值了
							int delta = mTextSize - mTextSizePrev;
							if (delta >= 2) delta /= 2;
							currSize = mTextSize + delta;
						} else {	//上次是减小，那么增大的幅度减半
							currSize = mTextSize + (mTextSizePrev - mTextSize) / 2;
						}
						mTextSizePrev = mTextSize;
						mTextSize = currSize;
						if (mTextSize > mMaxTextSizeScaled) mTextSize = mMaxTextSizeScaled;
						mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
						postRetryAdjustTextSizeOrLengthInDefinedBounds();
					}
				} else {
					//字号过大，textHeight也会为0
					if (textHeight == 0) {
						if (mReincrease) {
							mTextSize = mMinTextSizeScaled + (int)((mMaxTextSizeScaled - mMinTextSizeScaled) / 3.0f);
							mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
							adjustComplete(true);
							return;
						} else {
							mTextSize = mMinTextSizeScaled;
							mIncrease = false;
							mReincrease = true;
						}
					}
					mTextSizePrev = mTextSize;
					mUnitInPx = mIncrease ? mUnitInPx * 2 : 1;	//从1开始指数型增加，避免当设置了正确的字号之后还会大幅度调整字号，造成闪动或耗时
					mTextSize += mUnitInPx;
					if (mTextSize > mMaxTextSizeScaled) mTextSize = mMaxTextSizeScaled;
					mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
					postRetryAdjustTextSizeOrLengthInDefinedBounds();
				}
				if (!mIncrease) mIncrease = true;
			}
		}
	}

	private void adjustComplete(boolean ok) {
		reset();
		if (mTextView.getVisibility() != View.VISIBLE) mTextView.setVisibility(VISIBLE);
		if (ok && mOnTextSizeChangeListener != null) mOnTextSizeChangeListener.onResizeComplete(mTextSize);
	}

	private void reset() {
		mTextSizePrev = -1;
		mReincrease = false;
		mRecreateLayout = false;
		mIncrease = false;
		mDecrease = false;
	}

	private void postRetryAdjustTextSizeOrLengthInDefinedBounds() {
		if (isStateReadyForAdjustText()) {
			removeCallbacks(mRetryRun);
			//通常情况下，如果没有Attached到Window，即使post成功了，也不会立即执行，也不会在Attached到Window之后自动执行，而是在当前页面触发某事件之后才会执行
			//而在ListView中，即使之后Attached到Window后重新post，也不会执行，因此只好监听draw()
			post(mRetryRun);
			mSuspendPostRetry = false;
		} else {
			mSuspendPostRetry = true;
		}
	}

	private final Runnable mRetryRun = new Runnable() {

		@Override
		public void run() {
			adjustTextSizeOrLengthInDefinedBounds();
		}
	};

	private int getTextLayoutHeight() {
		Layout layout = mTextView.getLayout();
		return layout == null ? -1 : layout.getHeight();
	}

	private int getTextLayoutHeightDefined() {
		Layout layout = mTextView.getLayout();
		//如果是居中对齐(android:gravity="center")的话，getTotalPaddingTop()会包含文字layout区域以上的所有部分，getTotalPaddingBottom()同理，
		//而getCompoundPaddingTop()和getCompoundPaddingBottom()不会，getTotalPaddingLeft()，getTotalPaddingLeft()也不会。
		//return testEditText.getHeight() - testEditText.getTotalPaddingTop() - testEditText.getTotalPaddingBottom();
		return layout == null ? -1 : mTextView.getHeight() - mTextView.getCompoundPaddingTop() - mTextView.getCompoundPaddingBottom();
	}

	/**文本的坐标**/
	public static class TextCoords {
		private final int mTextLeft, mTextTop, mTextWidth, mTextHeight;
		private int mTextLeftScaled, mTextTopScaled, mTextWidthScaled, mTextHeightScaled;

		public TextCoords(int left, int top, int width, int height) {
			mTextLeft = left;
			mTextTop = top;
			mTextWidth = width;
			mTextHeight = height;
			//设置默认值
			setScale(1, 1);
		}

		private void setScale(float widthScale, float heightScale) {
			mTextLeftScaled = (int)(mTextLeft * widthScale);
			mTextTopScaled = (int)(mTextTop * heightScale);
			mTextWidthScaled = (int)(mTextWidth * widthScale);
			mTextHeightScaled = (int)(mTextHeight * heightScale);
		}
	}

	public void setOnTextSizeChangeListener(OnTextSizeChangeListener l) {
		mOnTextSizeChangeListener = l;
	}

	public interface OnTextSizeChangeListener {
		void onResizeComplete(int sizeInPx);
	}

	/*private static class MyTextView extends TextView {

		public MyTextView(Context context) {
			super(context);
		}

		//public void startMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//	super.measure(widthMeasureSpec, heightMeasureSpec);
		//}

		//测量与Layout对象（getLayout()）的创建有关，因此这里不进行优化
		//@Override
		//protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//	setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
		//}

		public void startLayout(int left, int top, int right, int bottom) {
			super.layout(left, top, right, bottom);
		}

		@Override
		public void layout(int l, int t, int r, int b) {
			// nothing...
		}
	}

	private static class MyEditText extends EditText {

		public MyEditText(Context context) {
			super(context);
		}

		//public void startMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//	super.measure(widthMeasureSpec, heightMeasureSpec);
		//}

		//测量与Layout对象（getLayout()）的创建有关，因此这里不进行优化
		//@Override
		//protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//	setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
		//}

		public void startLayout(int left, int top, int right, int bottom) {
			super.layout(left, top, right, bottom);
		}

		@Override
		public void layout(int l, int t, int r, int b) {
			// nothing...
		}
	}*/
}
