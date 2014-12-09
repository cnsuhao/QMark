package com.star.talk.startalk;

import android.content.Context;
import android.content.pm.PackageManager;

import com.tisumoon.exception.TokenNotValidException;
import com.wei.c.L;
import com.wei.c.framework.UserHelper;
import com.wei.c.phone.Device;
import com.wei.c.utils.RsaUtils;

public class UID {
	private static final String TAG			= UID.class.getSimpleName();

	public static final long MIN_VALUE		= 1000000000;
	public static final long MAX_VALUE		= Long.MAX_VALUE;

	private static long mId	= 0;
	private static TokenInfo mTokenInfo;

	public static final boolean isValid(long uid) {
		return uid >= MIN_VALUE && uid <= MAX_VALUE;
	}

	public static final boolean isUsing(Context context, long uid) {
		try {
			return uid == getMyID(context);
		} catch (TokenNotValidException e) {}
		return false;
	}

	public static synchronized long getMyID(Context context) throws TokenNotValidException {
		if (!isValid(mId)) mId = getTokenInfo(context).id;
		if (!isValid(mId)) throw new RuntimeException("从token取出来的id不正确");
		L.e(TAG, "mId:" + mId);
		return mId;
	}

	public static synchronized void updateToken(Context context, String token) {
		checkPermission(context);
		mId = 0;
		mTokenInfo = null;
		UserHelper.saveToken(context, token);
	}

	public static synchronized String nextToken(Context context) throws TokenNotValidException {
		String[] tokens = getTokenInfo(context).makeNext(Device.getUniqueId(context));
		UserHelper.saveToken(context, tokens[1]);
		L.d(TAG, "token:" + tokens[0]);
		L.d(TAG, "token1:" + tokens[1]);
		return tokens[0];
	}

	public static synchronized Token$ID nextToken$ID(Context context) throws TokenNotValidException {
		return new Token$ID(getMyID(context), nextToken(context));
	}

	public static String encrypt(String s) throws Exception {
		return RsaUtils.encrypt(true, RSA_PUBLIC, s, CHARSET);
	}

	private static TokenInfo getTokenInfo(Context context) throws TokenNotValidException {
		checkPermission(context);
		return mTokenInfo != null ? mTokenInfo : TokenInfo.fromToken(UserHelper.getToken(context));
	}

	private static void checkPermission(Context context) {
		if(!isPermissionAllowed(context)) throw new SecurityException("没有存取权限");
	}

	private static boolean isPermissionAllowed(Context context) {
		//TODO 必须apk签名一样才能通过，但不能覆盖反编译，应该找一种获取签名的方法，并在C代码中实现
		return context.checkCallingOrSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED;
	}

	public static class Token$ID {
		public final long ID;
		public final String token;

		private Token$ID(long id, String token) {
			this.ID = id;
			this.token = token;
		}
	}

	//安全起见，将改为native
	private static class TokenInfo {
		public final long id;
		public final long loginTime;
		private long increment;
		public final String leftPart;

		private TokenInfo(long id, long loginTime, long increment, String leftPart) {
			this.id = id;
			this.loginTime = loginTime;
			this.increment = increment;
			this.leftPart = leftPart;
		}

		/**服务端的token，只有前半部分，即：uid+&+time**/
		private static TokenInfo fromToken(String token) throws TokenNotValidException {
			if (token == null || token.length() == 0) throw new TokenNotValidException("token为空值，需要先登录");
			long no = 0, uid, timeServer;
			String uid$time, decrypt;
			try {
				int index = token.indexOf('$');
				if (index > 0) {
					if(index < token.length()-1) no = Long.valueOf(token.substring(index + 1));
					uid$time = token.substring(0, index);
				} else {
					uid$time = token;
				}
				decrypt = RsaUtils.decrypt(true, RSA_PUBLIC, uid$time, CHARSET);
				index = decrypt.indexOf("&");
				uid = Long.valueOf(decrypt.substring(0, index));
				timeServer = Long.valueOf(decrypt.substring(index + 1));
				if (no <= 0) no = timeServer;
				return new TokenInfo(uid, timeServer, no, uid$time);
			} catch (Exception e) {
				throw new TokenNotValidException("token无效：" + token, e);
			}
		}

		private String[] makeNext(String deviceId) throws TokenNotValidException {
			try {
				increment++;
				return new String[] {
						RsaUtils.encrypt(true, RSA_PUBLIC, id + "&" + loginTime, CHARSET) + RsaUtils.encrypt(true, RSA_PUBLIC, deviceId + "&" + increment, CHARSET),
						leftPart + "$" + increment};
			} catch (Exception e) {
				throw new TokenNotValidException();
			}
		}
	}

	private static final String CHARSET		= "UTF-8";
	private static final String PERMISSION	= "com.star.talk.startalk.UID";
	private static final String RSA_PUBLIC	= "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKSeIHYSarRl+nZ4iQFZIaCLfB+EjwWvpz5sKUVIfRlTgRItQyWEm6PRos/yT+LWlbnoCMcWUTdvhyxf9d+4WWUCAwEAAQ==";
}
