package com.star.talk.startalk.data;

import com.star.talk.startalk.data.api.UserSignInHistory;

public class MineListBean {
	public User user;
	public History history;

	public static class User {
		public long ID;
		public String nicename;
		public String starSign;
		public int newFriendsCount;

		public String headUrl;
		public String bgUrl;
	}

	public static class History {
		public History() {}

		public History(UserSignInHistory.Status status) {
			id = status.mid;
			magicBoard = new MagicBoardBean(status.body);
		}
		public long id;
		public long time;
		public String location;
		public int commentCount;
		public int favoriteCount;

		public MagicBoardBean magicBoard;
	}
}
