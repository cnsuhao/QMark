package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class OfficialSiteActy extends AbsSiteActy {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, OfficialSiteActy.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleStrId(R.string.title_m_official_site);
		setWebsiteUrl(Const.APP_SITE_URL);
	}
}
