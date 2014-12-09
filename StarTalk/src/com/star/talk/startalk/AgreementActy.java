package com.star.talk.startalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AgreementActy extends AbsSiteActy {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, AgreementActy.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleStrId(R.string.title_m_agreement);
		setWebsiteUrl(Const.AGREEMENT_URL);
	}
}
