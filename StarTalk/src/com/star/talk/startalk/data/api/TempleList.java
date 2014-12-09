package com.star.talk.startalk.data.api;

import com.google.gson.reflect.TypeToken;
import com.star.talk.startalk.data.api.beans.MagicBoardBody;
import com.tisumoon.data.abs.AbsData;

/*
{
	"result": "tempPub",
	"info": {
		"temps": [
			{
				"mid": 100019,
				"body": {
					"text": "今天是中秋节啊",
					"x": 20,
					"y": 45,
					"w": 212,
					"h": 23656,
					"font_id": 1,
					"font_color": "#5f491e",
					"bg": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/8be79509aaf0af486c94b240028aff78.jpg",
					"water_mark_align": "br"
				},
				"forwarding_count": 0,
				"classify": 6,
				"start_time": 1265059500,
				"end_time": 0,
				"collect": 0
			},
			{
				"mid": 100017,
				"body": {
					"text": "端午节快乐",
					"x": 200,
					"y": 15,
					"w": 300,
					"h": 200,
					"font_id": 1,
					"font_color": "#5f491e",
					"bg": "http://app-class-img.oss-cn-qingdao.aliyuncs.com/91d1870e3e65e7ea570198c19940503c.jpg",
					"water_mark_align": "br"
				},
				"forwarding_count": 5,
				"classify": 6,
				"start_time": 1412112300,
				"end_time": 1413265800,
				"collect": 0
			}
		],
		"total_number": 2
	}
}
 */
public class TempleList extends AbsData<TempleList> {
	public Info info;

	public static class Info {
		public Temp[] temps;
		public int total_number;
	}

	public static class Temp {
		public long mid;
		public MagicBoardBody body;
		public int forwarding_count;
		public int classify;
		public long start_time;
		public long end_time;
		public int collect;
	}

	@Override
	protected TypeToken<TempleList> getTypeToken() {
		return new TypeToken<TempleList>(){};
	}

	@Override
	protected String[] typeValues() {
		return new String[] {TYPE};
	}

	public static final String TYPE		= "tempPub";
}
