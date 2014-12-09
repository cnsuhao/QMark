package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.star.talk.startalk.data.api.TempleList.Temp;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "commonList",
	￼￼￼￼"info": {
		"common": [
				{
					"mid": 100420,
					"body": {
						"text": "不约",
						"x": 64,
						"y": 110,
						"w": 505,
						"h": 117,
						"font_id": 1,
						"font_color": "#ffffff",
						"bg": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/87bbb8005461a35efcfc8140d83d95af.png"
					},
					"classify": 12,
					"start_time": 1415187900,
					"end_time": 0,
					"forwarding_count": 1
				}, {
					"mid": 100419,
					"body": {
						"text": "hi~约吗?", "x": 73,
						"y": 446,
						"w": 510,
						"h": 145,
						"water_mark_align": "tl",
						"font_id": 1,
						"font_color": "#ffffff",
						"bg": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/f6e2bb14c03409c0b62cc2dc87256949.png"
					},
					"classify": 12,
					"start_time": 1414583700,
					"end_time": 0,
					"forwarding_count": 0
				}
		]
	}
}
 */
public class RecentUse extends AbsData<RecentUse> {
	public Info info;

	public static class Info {
		public Temp[] common;
	}

	@Override
	protected TypeToken<RecentUse> getTypeToken() {
		return new TypeToken<RecentUse>(){};
	}

	@Override
	protected String[] typeValues() {
		return new String[] {TYPE};
	}

	public static final String TYPE		= "commonList";
}
