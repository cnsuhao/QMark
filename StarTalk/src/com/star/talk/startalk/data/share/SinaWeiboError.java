package com.star.talk.startalk.data.share;

import org.json.JSONObject;

import com.wei.c.data.abs.AbsJson;

/*
从
{
	"error":
		"{
			\"error\":\"update weibo too fast!\",
			\"error_code\":20016,
			\"request\":\"\/2\/statuses\/upload.json\"
		}",
	"status":400
}
中提取出
{
	"error":"update weibo too fast!",
	"error_code":20016,
	"request":"/2/statuses/upload.json"
}
 */
public class SinaWeiboError extends AbsJson<SinaWeiboError> {
	public ShareError shareError;

	public int error_code;
	public String error;
	public String request;

	@Override
	public SinaWeiboError fromJson(String json) {
		shareError = new ShareError().fromJson(json);
		SinaWeiboError sinaWeiboError = fromJsonWithAllFields(shareError.error, SinaWeiboError.class);
		sinaWeiboError.shareError = shareError;
		return sinaWeiboError;
	}

	@Override
	public String toJson() {
		return toJsonWithAllFields(this);
	};

	@Override
	public boolean isBelongToMe(JSONObject json) {
		return true;
	}
}
