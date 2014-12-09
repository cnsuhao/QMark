package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "login",
	"type": "new",
	"fix": "no",
	"token": "o0mFnkSd3dO9TgxGk3nZNnr1j70n0Jd8nLlIGvO/XB+J9tb3d/pX1QlJnMrneAq+B59PbhbF1/wjcyVVPAbgXA=="
}
 */
public class Login extends AbsData<Login> {
	public String type;
	public String fix;
	public String token;

	@Override
	protected TypeToken<Login> getTypeToken() {
		return new TypeToken<Login>(){};
	}

	@Override
	protected String[] typeValues() {
		return new String[] {TYPE};
	}

	public static final String TYPE		= "login";
}
