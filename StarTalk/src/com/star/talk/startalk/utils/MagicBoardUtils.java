package com.star.talk.startalk.utils;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Color;

import com.star.talk.startalk.data.MagicBoardBean;
import com.star.talk.startalk.widget.MagicBoardView;
import com.tisumoon.utils.ImageFactory;

public class MagicBoardUtils {
	public static void display(Context context, MagicBoardView magicBoard, MagicBoardBean data) {
		magicBoard.setOnTextSizeChangeListener(data.getOnTextSizeChangeListener());
		getBitmapLoader(context).display(magicBoard, data.imgUrl);
		magicBoard.setTextCoordsBaseOnImage(data.left, data.top, data.width, data.height);
		magicBoard.setTextFont(FontUtils.getTypefaceWithCode(context, data.textFontCode));
		magicBoard.setTextColor(Color.parseColor(data.textColor));
		if (data.isTextSizeSaved()) magicBoard.setTextSize(data.getTextSizeInPx());
		magicBoard.setText(data.textDefault);
	}

	public static FinalBitmap getBitmapLoader(Context context) {
		return ImageFactory.getFbDiskCache(context, 1000, 1000);
	}
}
