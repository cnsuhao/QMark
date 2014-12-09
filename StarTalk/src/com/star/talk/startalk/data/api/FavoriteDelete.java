package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "delCollect",
	"info": {
		"mid": [
			"100819"
		]
	}
}
*/
public class FavoriteDelete extends AbsData<FavoriteDelete> {
	public Info info;

	public static class Info {
		public long[] mid;
	}

	@Override
	protected String[] typeValues() {
		return new String[] {"delCollect"};
	}

	@Override
	protected TypeToken<FavoriteDelete> getTypeToken() {
		return new TypeToken<FavoriteDelete>(){};
	}
}
