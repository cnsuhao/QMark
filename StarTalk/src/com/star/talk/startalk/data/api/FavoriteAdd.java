package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "addCollect",
	"info": {
		"mid": "100823"
	}
}
 */
public class FavoriteAdd extends AbsData<FavoriteAdd> {
	public Info info;

	public static class Info {
		public long mid;
	}

	@Override
	protected String[] typeValues() {
		return new String[]{"addCollect"};
	}

	@Override
	protected TypeToken<FavoriteAdd> getTypeToken() {
		return new TypeToken<FavoriteAdd>(){};
	}
}
