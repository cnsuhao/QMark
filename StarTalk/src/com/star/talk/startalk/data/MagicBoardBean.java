package com.star.talk.startalk.data;

import com.google.gson.annotations.Expose;
import com.star.talk.startalk.data.api.beans.MagicBoardBody;
import com.star.talk.startalk.widget.MagicBoardView.OnTextSizeChangeListener;

public class MagicBoardBean {
	public MagicBoardBean() {}

	public MagicBoardBean(MagicBoardBean self, String text) {
		imgUrl = self.imgUrl;
		textFontCode = self.textFontCode;
		textColor = self.textColor;
		textDefault = text;
		left = self.left;
		top = self.top;
		width = self.width;
		height = self.height;
		waterMarkAlign = self.waterMarkAlign;
	}

	public MagicBoardBean(MagicBoardBody body) {
		imgUrl = body.bg;
		textFontCode = body.font_id;
		textColor = body.font_color;
		textDefault = body.text;
		left = body.x;
		top = body.y;
		width = body.w;
		height = body.h;
		waterMarkAlign = WaterMarkAlign.get(body.water_mark_align);
	}

	@Expose
	public String imgUrl;
	@Expose
	public int textFontCode;
	@Expose
	public String textColor;
	@Expose
	public String textDefault;

	@Expose
	public int left;
	@Expose
	public int top;
	@Expose
	public int width;
	@Expose
	public int height;

	@Expose
	public WaterMarkAlign waterMarkAlign;

	/////////////////////////////////////////////////////////////////////////////

	private int textSizeInPx;
	private boolean textSizeSaved = false;

	public int getTextSizeInPx() {
		return textSizeInPx;
	}

	public boolean isTextSizeSaved() {
		return textSizeSaved;
	}

	public OnTextSizeChangeListener getOnTextSizeChangeListener() {
		return mOnTextSizeChangeListener;
	}

	private final OnTextSizeChangeListener mOnTextSizeChangeListener = new OnTextSizeChangeListener() {
		@Override
		public void onResizeComplete(int sizeInPx) {
			MagicBoardBean.this.textSizeInPx = sizeInPx;
			MagicBoardBean.this.textSizeSaved = true;
		};
	};

	public static enum WaterMarkAlign {
		TOP_LEFT("tl"),
		TOP_RIGHT("tr"),
		BOTTOM_LEFT("bl"),
		BOTTOM_RIGHT("br"),
		CENTER("c");

		public final String param;
		private WaterMarkAlign(String p) {
			param = p;
		}

		private static WaterMarkAlign get(String wma) {
			if (wma == null) {
				return BOTTOM_RIGHT;
			} else if (wma.equals(TOP_LEFT.param)) {
				return TOP_LEFT;
			} else if (wma.equals(TOP_RIGHT.param)) {
				return TOP_RIGHT;
			} else if (wma.equals(BOTTOM_LEFT.param)) {
				return BOTTOM_LEFT;
			} else if (wma.equals(BOTTOM_RIGHT.param)) {
				return BOTTOM_RIGHT;
			} else if (wma.equals(CENTER.param)) {
				return CENTER;
			} else {
				return null;
			}
		}
	}
}
