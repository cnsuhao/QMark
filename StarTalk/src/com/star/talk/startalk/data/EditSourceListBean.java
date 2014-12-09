package com.star.talk.startalk.data;

import com.star.talk.startalk.data.api.EditSourceCategory;

public class EditSourceListBean {
	public EditSourceListBean() {}

	public EditSourceListBean(EditSourceCategory.Clases clazs) {
		id = clazs.id;

		imgBgUrl = clazs.bgurl;
		ImgCatgyUrl = clazs.fgurl;

		textLeft = clazs.name;
		textSignSize = clazs.count;
	}

	public long id;

	public String imgBgUrl;
	public String ImgCatgyUrl;

	public String textLeft;
	public int textSignSize;
}
