package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.star.talk.startalk.data.api.beans.LocationBody;
import com.star.talk.startalk.data.api.beans.MagicBoardBody;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "userTimeline",
	"info": {
		"statuses": [
			{
				"mid": 100008,
				"create_at": 1411698055,
				"body": {
					"bg": "http://app-img-path.oss-cn-qingdao.aliyuncs.com/100109_3462eb93a42832ce226000f4698bf5ed.jpeg",
					"text": "早睡早起",
					"x": 100,
					"y": 50,
					"w":20,
					"h": 10,
					"font_id": 1,
					"font_color": "#ff0000"
				},
				"create_uid": 100109,
				"comments_count": 0,
				"forwarding_count": 0,
				"likes_count": 0,
				"geo": {
					"update": 1411698055,
					"longitude": 100.265412,
					"latitude": 30.125412,
					"city": 1,
					"province": 12,
					"city_name": "深圳",
					"province_name": "广州",
					"address": "不知道啊大大什么街道的88 号",
					"pinyin": "buzhid",
					"more": null
				}
			},
			...
		],
		"previous_cursor": 1411698055,
		"next_cursor": 1411697898,
		"total_number": 7
	}
}
 */
public class UserSignInHistory extends AbsData<UserSignInHistory> {
	public Info info;

	public static class Info {
		public Status[] statuses;
		public long previous_cursor;
		public long next_cursor;
		public int total_number;
	}

	public static class Status {
		/**数据id, 不是用户id**/
		public long mid;
		public long create_uid;
		public long create_at;
		public MagicBoardBody body;
		public int comments_count;
		/**转发数量**/
		public int forwarding_count;
		public int likes_count;
		public LocationBody geo;
	}

	@Override
	protected TypeToken<UserSignInHistory> getTypeToken() {
		return new TypeToken<UserSignInHistory>(){};
	}

	@Override
	protected String[] typeValues() {
		return new String[] {TYPE};
	}

	public static final String TYPE		= "userTimeline";
}
