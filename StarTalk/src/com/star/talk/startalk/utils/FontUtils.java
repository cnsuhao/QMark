package com.star.talk.startalk.utils;

import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {
	/* Typeface.createFromAsset(mgr, path)，如果字体只包含英文，而输入的全是中文，那么几乎没有性能影响；
	 * 但是字体包含中文的，而输入的也是中文，则影响很大
	 */
	/**
	 * 不能把String作为Key，会立即回收
	 */
	public final static WeakHashMap<Typeface, String> sTypefaceCache = new WeakHashMap<Typeface, String>();

	public static Typeface getTypefaceWithCode(Context context, int fontCode) {
		String path = codeToAssetsPath(fontCode);
		Set<Entry<Typeface, String>> entries = sTypefaceCache.entrySet();
		for (Entry<Typeface, String> entry : entries) {
			if (entry.getValue().equals(path)) {
				return entry.getKey();
			}
		}
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);
		sTypefaceCache.put(typeface, path);
		return typeface;
	}

	public static String codeToAssetsPath(int fontCode) {
		String path = null;
		switch (fontCode) {
		case 1:
			path = "font/hua-kang-wa-wa-ti-w5.ttc";
			break;
		case 2:
			path = "font/hua-kang-shao-nv-wen-zi-jian-w5.ttc";
			break;
		default:
			break;
		}
		return path;
	}
}
