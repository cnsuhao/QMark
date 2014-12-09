package com.star.talk.startalk.data;

import com.star.talk.startalk.data.api.MainCategory;

public class MainCategoryListBean {
	public long id;
	public String title;
	public String author;
	public String imgUrl;
	public int lockShareType;
	public long signCount;

	public MainCategoryListBean(MainCategory.Clases clazs) {
		id = clazs.id;
		title = clazs.class_name;
		author = clazs.class_author;
		imgUrl = clazs.img;
		lockShareType = clazs.lock_share;
		signCount = clazs.forwarding_count;
	}

	@Override
	public int hashCode() {
		return (int)id;
	}

	@Override
	public boolean equals(Object o) {
		return ((MainCategoryListBean)o).id == id;
	}
}
