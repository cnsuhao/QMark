package com.star.talk.startalk;

import com.wei.c.Debug;

public class Const {
	public static final String APP_DIR_NAME						= "QMark";
	public static final String APP_SITE_URL						= "http://www.yuchats.com/";
	public static final String AGREEMENT_URL					= "http://www.yuchats.com/agreement.html";
	/*
	 * 群号：Android编程&amp;移动互联(215621863) 的 key 为： 9gqhSj4gnhFHOpJ2N89zKLRokMgLXD7c
	 * 群号：Mark猫和它的小老婆们(332460930) 的 key 为： 71IyaV94MEt1XvAqS9B1_Xz3HZXcp-V0
	 */
	public static final String QQ_GROUP_KEY_332460930			= "71IyaV94MEt1XvAqS9B1_Xz3HZXcp-V0";

	public static class Url {
		public static String login								= "http://mengmakejiekou.yuchats.com/1/Account/login";
		private static String mainCategory						= "http://mengmakejiekou.yuchats.com/1/template/classes";
		private static String editTempList						= "http://mengmakejiekou.yuchats.com/1/template/status";
		public static String createShare						= "http://mengmakejiekou.yuchats.com/1/statuses/create";
		public static String createShareSimple					= "http://mengmakejiekou.yuchats.com/1/statuses/create_single";
		public static String feedback							= "http://mengmakejiekou.yuchats.com/1/proposal/send";
		public static String contribute							= "http://mengmakejiekou.yuchats.com/1/contribute/send";

		public static String recentUse							= "http://mengmakejiekou.yuchats.com/1/collect/common_list";
		public static String favorite							= "http://mengmakejiekou.yuchats.com/1/collect/conllect_list";

		public static String addFavorite						= "http://mengmakejiekou.yuchats.com/1/collect/add_conllect";
		public static String deleteFavorite						= "http://mengmakejiekou.yuchats.com/1/collect/del_conllects";

		private static String mainCategoryTest					= "http://mengmakejiekou.yuchats.com/1/template/classes_test";
		private static String editTempListTest					= "http://mengmakejiekou.yuchats.com/1/template/status_test";

		public static String getMainCategory() {
			return Debug.DEBUG ? mainCategoryTest : mainCategory;
		}

		public static String getEditTempList() {
			return Debug.DEBUG ? editTempListTest : editTempList;
		}

		//public static String mineHistory						= "http://starapi.yuchats.com/1/statuses/userTimeline";
	}

	public static class Broadcast {
		//YO通知被点击
		public static String YO_CLICK_NOTIFY					= "com.tisumoon.intent.action.YO_CLICK_NOTIFY";
		//Ya的Mail通知被点击
		public static String YO_MAIL_CLICK_NOTIFY				= "com.tisumoon.intent.action.YO_MAIL_CLICK_NOTIFY";
	}

	public static class EventName {
		public static String SCROLL_2_TOP_AND_REFRESH_frgmt_favorite	= "SCROLL_2_TOP_AND_REFRESH_frgmt_favorite";
		public static String SCROLL_2_TOP_AND_REFRESH_frgmt_recommend	= "SCROLL_2_TOP_AND_REFRESH_frgmt_recommend";
	}

	public static class ThirdAccount {
		public static String sSharedPrefName					= "third.account";
		public static String sKey_qq_login_json					= "qq.login.json";
		public static String sKey_qq_user_info_json				= "qq.user.info.json";

		//QQ_key QQ第三方登录
		//public static String QQ_APP_ID_XIUXIU					= "1101766923";

		public static String QQ_SCOPE							= "get_user_info, get_simple_userinfo";	//"get_user_info";	//"get_info(腾讯微博的), get_user_info, get_simple_userinfo";

		//public static String BAIDU_MAP_API_KEY				= "9oWkxzjnEGfOmOL1s0f91ULd";

		//WeChat_key QQ第三方登录
		public static String WECHAT_APP_ID						= "wxdf420f6e4e95fcbd";
	}
}
