package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "fankui",	//tougao
	"info": {
		"uid": 100001,
		"update": 1416211093
	}
}
 */

/**投稿页面**/
public class Contribute extends AbsData<Contribute> {

	public boolean isFanKui() {
		return result.equals(TYPE_FANKUI);
	}

	public boolean isTouGao() {
		return result.equals(TYPE_TOUGAO);
	}

	@Override
	protected String[] typeValues() {
		return new String[]{TYPE_FANKUI, TYPE_TOUGAO};
	}

	@Override
	protected TypeToken<Contribute> getTypeToken() {
		return new TypeToken<Contribute>(){};
	}

	public static final String TYPE_FANKUI		= "fankui";
	public static final String TYPE_TOUGAO		= "tougao";
}
