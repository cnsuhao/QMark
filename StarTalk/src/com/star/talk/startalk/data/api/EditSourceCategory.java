package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "tempClass",
	"info": {
		"classes": [
			{
				"id": 10,
				"name": "北京北京",
				"bgurl": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/97099d1aa7b21fa5d1b1c5eb17bf7c14.jpg",
				"fgurl": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/97099d1aa7b21fa5d1b1c5eb17bf7c14.jpg",
				"count": 0
			},
			{
				"id": 6,
				"name": "节日相关",
				"bgurl": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/1146e75d458470cc2259e6102d987b7c.jpg",
				"fgurl": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/97099d1aa7b21fa5d1b1c5eb17bf7c14.jpg",
				"count": 155
			},
			...
		],
		"total_number": 5
	}
}
 */
public class EditSourceCategory extends AbsData<EditSourceCategory> {
	public Info info;

	public static class Info {
		public Clases[] classes;
		public int total_number;
	}

	public static class Clases {
		public long id;
		public String name;
		public String bgurl;
		public String fgurl;
		public int count;
	}

	@Override
	protected TypeToken<EditSourceCategory> getTypeToken() {
		return new TypeToken<EditSourceCategory>(){};
	}

	@Override
	protected String[] typeValues() {
		return new String[] {TYPE};
	}

	public static final String TYPE		= "tempClass";
}
