package com.star.talk.startalk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.star.talk.startalk.App.ApiType;
import com.star.talk.startalk.adapter.ContributeAddImageAdapter;
import com.star.talk.startalk.http.ClientMultipartFormPost;
import com.tisumoon.AbsBaseListViewActivity;
import com.tisumoon.exception.TokenNotValidException;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.file.FileUtils;
import com.wei.c.utils.PhotoUtils;
import com.wei.c.utils.PhotoUtils.CropArgs;
import com.wei.c.utils.PhotoUtils.Session;

@ViewLayoutId(R.layout.m_contribute)
public class ContributeActy extends AbsBaseListViewActivity<GridView, Uri, ContributeAddImageAdapter> {
	private static final String EXTRA_FROM_FEEDBACK		= ContributeActy.class.getName() + ".FROM_FEEDBACK";
	private static final String EXTRA_SESSION			= ContributeActy.class.getName() + ".SESSION";
	private static final String EXTRA_LIST_DATA			= ContributeActy.class.getName() + ".LIST_DATA";

	private static final int REQUEST_CODE_PICK_PHOTO	= 100;
	private static final int REQUEST_CODE_CROP			= 101;

	public static void startMe(Context context) {
		startMe(context, new Intent(context, ContributeActy.class));
	}

	public static void startMeFromFeedback(Context context) {
		Intent i = new Intent(context, ContributeActy.class);
		i.putExtra(EXTRA_FROM_FEEDBACK, true);
		startMe(context, i);
	}

	@ViewId(R.id.m_contribute_title_left_btn_back)
	private ImageButton mBtnBack;
	@ViewId(R.id.m_contribute_title_text)
	private TextView mTextTitle;
	@ViewId(R.id.m_contribute_edit_title)
	private EditText mEditTitle;
	@ViewId(R.id.m_contribute_edit_content)
	private EditText mEditContent;
	@ViewId(R.id.m_contribute_btn_add_img)
	private ImageButton mBtnAddImg;
	@ViewId(R.id.m_contribute_btn_commit)
	private Button mBtnCommit;

	private boolean mFromFeedback = false;
	private PhotoUtils.Session session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFromFeedback = getIntent().getBooleanExtra(EXTRA_FROM_FEEDBACK, false);
		if (savedInstanceState != null) restoreInstanceStateInner(savedInstanceState);

		mTextTitle.setText(mFromFeedback ? R.string.title_m_contribute_4_feedback : R.string.title_m_contribute);

		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBtnAddImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getAdapter().getCount() < 5) {
					try {
						session = PhotoUtils.openSysGallery2ChoosePhoto(ContributeActy.this, REQUEST_CODE_PICK_PHOTO,
								new CropArgs(REQUEST_CODE_CROP,
										new File(App.getImagesCacheDirPrivate(), "croptemp-" + System.currentTimeMillis() + ".png").getPath(),
										Bitmap.CompressFormat.PNG, false,
										true, 0, 0, 0, 0, 640, 640));
						L.e(ContributeActy.class, session.toString());
					} catch (Exception e) {
						L.e(ContributeActy.class, e);
					}
				} else {
					Toast.makeText(ContributeActy.this, R.string.m_contribute_max_image_length_5, Toast.LENGTH_LONG).show();
				}
			}
		});
		mBtnCommit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable title = mEditTitle.getText();
				Editable text = mEditContent.getText();
				if (TextUtils.isEmpty(title)) {
					Toast.makeText(ContributeActy.this, R.string.m_contribute_commit_title_empty, Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(text) || text.length() < 15) {
					Toast.makeText(ContributeActy.this, R.string.m_contribute_commit_text_min_size_15, Toast.LENGTH_LONG).show();
					return;
				}
				List<File> images = null;
				if (getAdapter().getData().size() > 0) {
					images = new ArrayList<File>();
					for (Uri uri : getAdapter().getData()) {
						L.i(ContributeActy.class, "uri: " + uri);
						try {
							//有try...catch, 就给直接.getPath(), 不用考虑文件是否存在
							String path = FileUtils.bringUriFileToDir(ContributeActy.this, uri, App.getImagesCacheDirPrivate().getPath(), null, "png").getPath();
							L.i(ContributeActy.class, "path: " + path);
							images.add(new File(path));
						} catch (Exception e) {
							L.e(ContributeActy.class, e);
						}
					}
				}
				doCommit(title.toString(), text.toString(), images);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		L.d(ContributeActy.class, "onSaveInstanceState---------");
		outState.putBoolean(EXTRA_FROM_FEEDBACK, mFromFeedback);
		List<Uri> data = getAdapter().getData();
		if (data.size() > 0) {
			ArrayList<String> value = new ArrayList<String>();
			for (Uri uri : data) {
				value.add(uri.toString());
			}
			outState.putStringArrayList(EXTRA_LIST_DATA, value);
		}
		if (session != null) outState.putString(EXTRA_SESSION, session.toJson());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreInstanceStateInner(savedInstanceState);
	}

	private void restoreInstanceStateInner(Bundle savedInstanceState) {
		L.d(ContributeActy.class, "restoreInstanceStateInner---------");
		mFromFeedback = savedInstanceState.getBoolean(EXTRA_FROM_FEEDBACK, false);
		ArrayList<String> value = savedInstanceState.getStringArrayList(EXTRA_LIST_DATA);
		if (value != null && value.size() > 0) {
			List<Uri> data = new ArrayList<Uri>();
			for (String s : value) {
				data.add(Uri.parse(s));
			}
			getAdapter().setDataSource(data);
		}
		String json = savedInstanceState.getString(EXTRA_SESSION);
		if (json != null) session = Session.fromJsonWithAllFields(json, Session.class);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = PhotoUtils.onActivityResult(this, session, requestCode, resultCode, data);
		if (uri != null) {
			getAdapter().getData().add(uri);
			getAdapter().notifyDataSetChanged();
			session = null;
		}
	}

	@Override
	protected int listViewId() {
		return R.id.m_contribute_grid_view;
	}

	@Override
	protected ContributeAddImageAdapter newAdapter() {
		return new ContributeAddImageAdapter(this);
	}

	private void doCommit(String title, String text, List<File> fileList) {
		Toast.makeText(ContributeActy.this, R.string.m_contribute_commit_start, Toast.LENGTH_LONG).show();
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("text", text));
			Map<String, File> files = new HashMap<String, File>();
			if (fileList != null && fileList.size() > 0) {
				params.add(new BasicNameValuePair("hasfile", "1"));
				int count = 0;
				for (File f : fileList) {
					files.put(count == 0 ? "img" : "img" + count, f);
					count++;
				}
			} else {
				params.add(new BasicNameValuePair("hasfile", "0"));
			}
			ClientMultipartFormPost.makeMultipartEntity(params, files);
			App.getFHttp().post(mFromFeedback ? Const.Url.feedback : Const.Url.contribute,
					App.getHeadersWithUidToken(),
					ClientMultipartFormPost.makeMultipartEntity(params, files), null, new AjaxCallBack<String>() {
				@Override
				public void onLoading(long count, long current) {
					if (count > 0 && current > 0 && current >= count) {
						Toast.makeText(ContributeActy.this, getString(R.string.m_contribute_commit_progress, (int)(current * 100 / count)), Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onSuccess(String json) {
					if (!App.checkApiJsonError(ContributeActy.class, json)) {
						Toast.makeText(ContributeActy.this, R.string.m_contribute_commit_success, Toast.LENGTH_LONG).show();
					} else if (!App.checkIsJson(json)) {
						Toast.makeText(ContributeActy.this, json, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(ContributeActy.class, ApiType.COMMIT, t, errorNo, strMsg);
				}
			});
		} catch (TokenNotValidException e) {
			L.e(ContributeActy.class, e);
		}
	}
}
