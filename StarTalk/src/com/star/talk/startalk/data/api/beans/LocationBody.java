package com.star.talk.startalk.data.api.beans;

/*
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
 */
public class LocationBody {
	/**上次更新位置的时间**/
	public long update;
	public float longitude;
	public float latitude;
	public int city;
	public int province;
	public String city_name;
	public String province_name;
	public String address;
	/**拼音**/
	public String pinyin;
	public String more;
}
