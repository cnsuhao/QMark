package com.star.talk.startalk.http;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;

import com.star.talk.startalk.App;
import com.wei.c.L;
import com.wei.c.utils.Manifest;

public class ClientMultipartFormPost {

	public static void uploadFile(final String url, final Header[] headers, final List<NameValuePair> params, final Map<String, File> files, final Callback callback) {
		new AsyncTask<Void, Void, Result> () {

			@Override
			protected Result doInBackground(Void... args) {
				try {
					HttpPost httppost = new HttpPost(url);
					httppost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
					if (headers != null && headers.length > 0) {
						for (Header header : headers) {
							httppost.addHeader(header);
						}
					}
					httppost.setEntity(makeMultipartEntity(params, files));

					L.i(ClientMultipartFormPost.class, "executing request " + httppost.getRequestLine());

					HttpResponse response = getHttpClient().execute(httppost);
					StatusLine statusLine = response.getStatusLine();
					L.i(ClientMultipartFormPost.class, "response: " + statusLine);
					int statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {
						HttpEntity entity = response.getEntity();
						String content = null;
						if (entity != null) {
							L.i(ClientMultipartFormPost.class, "response content length: " + entity.getContentLength());
							if (entity.getContentLength() > 0) {
								content = EntityUtils.toString(entity);
							}
						}
						return new Result(content);
					} else {
						return new Result(statusCode, null);
					}
				} catch (Exception e) {
					L.e(ClientMultipartFormPost.class, e);
					return new Result(0, e);
				}
			}

			@Override
			protected void onPostExecute(Result result) {
				if (result.success) {
					callback.onSuccess(result.s);
				} else {
					callback.onFailure(result.code, result.e);
				}
			};
		}.execute();
	}

	public static HttpEntity makeMultipartEntity(List<NameValuePair> params, final Map<String, File> files) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);	//如果有SocketTimeoutException等情况，可修改这个枚举
		//builder.setCharset(Charset.forName("UTF-8"));	//不要用这个，会导致服务端接收不到参数
		if (params != null && params.size() > 0) {
			for (NameValuePair p : params) {
				builder.addTextBody(p.getName(), p.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
			}
		}
		if (files != null && files.size() > 0) {
			Set<Entry<String, File>> entries = files.entrySet();
			for (Entry<String, File> entry : entries) {
				builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
			}
		}
		return builder.build();
	}

	private static HttpClient mClient;
	private static HttpClient getHttpClient() {
		if(mClient == null) {
			//if(Build.VERSION.SDK_INT >= 9);	//将不走本类的Case，基于HttpURLConnection
			if(Build.VERSION.SDK_INT >= 8) {
				mClient = AndroidHttpClient.newInstance(getUserAgent());
			}else {
				mClient = new DefaultHttpClient();
			}
		}
		return mClient;
	}

	protected static String getUserAgent() {
		return App.get().getPackageName() + "/" + Manifest.getVersionName(App.get());
	}

	public static boolean getContentIsGzip(final HttpEntity entity) throws ParseException {
		boolean gzip = false;
		if (entity.getContentEncoding() != null) {
			HeaderElement values[] = entity.getContentEncoding().getElements();
			for (HeaderElement e : values) {
				gzip = e.getName().equals("gzip");
				if(gzip) break;
			}
		}
		return gzip;
	}

	public static interface Callback {
		void onSuccess(String s);
		void onFailure(int code, Exception e);
	}

	private static class Result {
		public final boolean success;
		public String s;
		public int code;
		public Exception e;

		public Result(String s) {
			this.success = true;
			this.s = s;
		}

		public Result(int code, Exception e) {
			this.success = false;
			this.code = code;
			this.e = e;
		}
	}
}
