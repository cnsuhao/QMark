package com.star.talk.startalk.data.share;

import org.json.JSONObject;

import com.wei.c.data.abs.AbsJson;

/*
{
	"error":
		"{
			\"error\":\"update weibo too fast!\",
			\"error_code\":20016,
			\"request\":\"\/2\/statuses\/upload.json\"
		}",
	"status":400
}
 */
public class ShareError extends AbsJson<ShareError> {
	public int status;
	public String error;

	@Override
	public ShareError fromJson(String json) {
		return fromJsonWithAllFields(json, ShareError.class);
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
