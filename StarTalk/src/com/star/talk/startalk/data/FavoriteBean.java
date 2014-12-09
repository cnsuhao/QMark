package com.star.talk.startalk.data;

import com.star.talk.startalk.data.api.TempleList;

public class FavoriteBean extends EditListBean {
	public final boolean hasMore;
	public boolean loading = false;

	public FavoriteBean() {
		this.hasMore = false;
	}

	public FavoriteBean(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public FavoriteBean(EditListBean bean, String text) {
		this();
		id = bean.id;
		topicId = bean.topicId;
		magicBoard = new MagicBoardBean(bean.magicBoard, text);
		selected = false;
	}

	public FavoriteBean(TempleList.Temp temp) {
		super(temp);
		this.hasMore = false;
	}
}
