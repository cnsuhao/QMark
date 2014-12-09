package com.star.talk.startalk.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.star.talk.startalk.data.api.TempleList;
import com.tisumoon.data.abs.AbsData;

public class EditListBean extends AbsData<EditListBean> {
	@Expose
	public long id;
	@Expose
	public long topicId;
	@Expose
	public boolean favorite;
	@Expose
	public MagicBoardBean magicBoard;
	public boolean selected = false;

	public EditListBean() {}

	public EditListBean(TempleList.Temp temp) {
		id = temp.mid;
		topicId = temp.classify;
		favorite = temp.collect == 1;
		magicBoard = new MagicBoardBean(temp.body);
	}

	@Override
	public int hashCode() {
		return (int)id;
	}

	@Override
	public boolean equals(Object o) {
		EditListBean fb = ((EditListBean)o);
		return fb.id == id && fb.topicId == topicId;
	}

	@Override
	public EditListBean fromJson(String json) {
		return fromJsonWithExposeAnnoFields(json, getTypeToken());
	}

	@Override
	public String toJson() {
		return toJsonWithExposeAnnoFields(this);
	}

	@Override
	protected String[] typeValues() {
		return null;
	}

	@Override
	protected TypeToken<EditListBean> getTypeToken() {
		return new TypeToken<EditListBean>(){};
	}
}
