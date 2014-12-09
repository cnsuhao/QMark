package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "tempClass",
	"info": {
		"classes": [
			{
				"id": 8,
				"class_name": "钟小哇,你是我滴小呀小太阳",
				"class_author": "画师:@-撸撸喵-",
				"img": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/78e112ada427153ded50e6299ebac482.png",
				"lock_share": 1,
				"type": "template",
				"count": 0,
				"forwarding_count": 0
			}...
		],
		"total_number": 2;
		"page_no": 1;
		"has_next_page": 0
	}
}
 */
public class MainCategory extends AbsData<MainCategory> {
	public Info info;

	public static class Info {
		public Clases[] classes;
		public int total_number;
		public int page_no;
		public int has_next_page;
	}

	public static class Clases {
		public long id;
		public String class_name;
		public String class_author;
		public String type;
		public String img;
		public int lock_share;
		public int count;
		public int forwarding_count;
	}

	@Override
	protected TypeToken<MainCategory> getTypeToken() {
		return new TypeToken<MainCategory>(){};
	}

	@Override
	protected String[] typeValues() {
		return new String[] {TYPE};
	}

	public static final String TYPE		= "tempClass";
}
