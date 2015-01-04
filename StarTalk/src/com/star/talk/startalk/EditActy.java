package com.star.talk.startalk;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.star.talk.startalk.App.ApiType;
import com.star.talk.startalk.adapter.EditGridAdapter;
import com.star.talk.startalk.adapter.EditGridAdapter.EditGridViewHolder;
import com.star.talk.startalk.data.EditListBean;
import com.star.talk.startalk.data.FavoriteBean;
import com.star.talk.startalk.data.api.FavoriteAdd;
import com.star.talk.startalk.data.api.FavoriteDelete;
import com.star.talk.startalk.data.api.TempleList;
import com.star.talk.startalk.data.share.SinaWeiboError;
import com.star.talk.startalk.thirdapi.ShareSdk;
import com.star.talk.startalk.thirdapi.ShareSdk.QQShare;
import com.star.talk.startalk.thirdapi.ShareSdk.QZoneShare;
import com.star.talk.startalk.thirdapi.ShareSdk.Share;
import com.star.talk.startalk.thirdapi.ShareSdk.SinaWeiboShare;
import com.star.talk.startalk.thirdapi.ShareSdk.WechatMomentsShare;
import com.star.talk.startalk.thirdapi.WeChat;
import com.star.talk.startalk.utils.FontUtils;
import com.star.talk.startalk.utils.MagicBoardUtils;
import com.star.talk.startalk.widget.MagicBoardView;
import com.tisumoon.AbsBaseListViewActivity;
import com.tisumoon.exception.TokenNotValidException;
import com.tisumoon.thirdapi.Baidu;
import com.tisumoon.thirdapi.Baidu.LocData;
import com.tisumoon.thirdapi.Baidu.LocGps;
import com.tisumoon.thirdapi.Baidu.LocNet;
import com.tisumoon.thirdapi.Baidu.LocNetvGsm;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.file.FileUtils;
import com.wei.c.framework.ViewHolder;
import com.wei.c.phone.Network;
import com.wei.c.phone.Network.State;
import com.wei.c.phone.Network.Type;
import com.wei.c.receiver.net.NetConnectionReceiver;
import com.wei.c.receiver.net.NetObserver;
import com.wei.c.receiver.storage.StorageReceiver;
import com.wei.c.receiver.storage.ToastStorageObserver;
import com.wei.c.utils.BitmapUtils;
import com.wei.c.utils.PhotoUtils;
import com.wei.c.utils.SPref;

@ViewLayoutId(R.layout.m_edit)
public class EditActy extends AbsBaseListViewActivity<GridView, EditListBean, EditGridAdapter> {
	private static final String EXTRA_CATEGORY_ID		= EditActy.class.getName() + ".EXTRA_CATEGORY_ID";
	private static final String EXTRA_DEFAULT_DATA		= EditActy.class.getName() + ".EXTRA_DEFAULT_DATA";

	public static void startMe(Context context, long categoryId) {
		Intent intent = new Intent(context, EditActy.class);
		intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
		startMe(context, intent);
	}

	public static void startMe(Context context, long categoryId, EditListBean defaultData) {
		Intent intent = new Intent(context, EditActy.class);
		intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
		intent.putExtra(EXTRA_DEFAULT_DATA, defaultData.toJson());
		startMe(context, intent);
	}

	@ViewId(R.id.m_edit_content)
	private ViewGroup mContentView;
	@ViewId(R.id.m_edit_title_left_btn_back)
	private ImageButton mBtnBack;
	@ViewId(R.id.m_edit_title_right_fabu)
	private TextView mBtnFabu;
	@ViewId(R.id.m_edit_snapshot_panel)
	private ViewGroup mSnapshotPanel;
	@ViewId(R.id.m_edit_magic_board)
	private MagicBoardView mMagicBoard;
	@ViewId(R.id.m_edit_img_water_mark)
	private ImageView mWaterMark;
	@ViewId(R.id.m_edit_divider_line)
	private View mDividerLine;
	@ViewId(R.id.m_edit_img_state)
	private ImageView mImgState;

	private EditListBean mCurrent;
	private long mCategoryId;
	private long mTempleId;
	private Bitmap mFabuBmp;
	private Canvas mCanvas;
	private Paint mPaint;

	private Dialog mDialog;
	private DialogViewHolder mDialogViewHolder;
	private boolean mGuideShowed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		mCategoryId = i.getLongExtra(EXTRA_CATEGORY_ID, 0);
		String json = i.getStringExtra(EXTRA_DEFAULT_DATA);
		if (json != null) mCurrent = new EditListBean().fromJson(json);

		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				L.d(EditActy.class, "onItemClick----");
				EditGridViewHolder viewHolder = (EditGridViewHolder)view.getTag();
				showToEditItem(position, viewHolder.getData());
			}
		});
		mBtnFabu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String text = mMagicBoard.getText().toString();
					showShareDialog(text, createSnapshot());
					FavoriteActy.cacheRecentUse(EditActy.this, new FavoriteBean(mCurrent, text));
					commitShare(text);
				} catch (Exception e) {
					L.e(EditActy.class, e);
				}
			}
		});
		mImgState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ensureViewState(true, true);
			}
		});

		if (mCurrent != null) showToEditItem(-1, mCurrent);


		boolean loaded = loadFromCache();
		//有网的时候还是要刷新
		if (loaded ? Network.isNetConnected(this) : App.checkNetStateAndFeedbackUser()) loadData();
	}

	private final OnClickListener mOnFavoriteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			EditGridViewHolder vHolder = (EditGridViewHolder)v.getTag();
			if (vHolder.getData().favorite) {
				vHolder.setFavorite(false);
				commitFavoriteDelete(vHolder.getData().id);
			} else {
				vHolder.setFavorite(true);
				commitFavoriteAdd(vHolder.getData().id);
			}
		}
	};

	private final ToastStorageObserver mToastStorageObserver = new ToastStorageObserver(this);

	private final NetObserver mNetObserver = new NetObserver() {
		@Override
		public void onChanged(Type type, State state) {
			ensureViewState(true, false);
		}
	};

	private boolean isEmpty() {
		return getAdapter().getCount() == 0;
	}

	private void ensureViewState(boolean loaddata, boolean feedbackIfNoNet) {
		boolean empty = isEmpty();
		//mMagicBoard.setVisibility(empty ? View.GONE : View.VISIBLE);
		mSnapshotPanel.setVisibility(empty ? View.GONE : View.VISIBLE);
		mDividerLine.setVisibility(empty ? View.GONE : View.VISIBLE);
		getListView().setVisibility(empty ? View.GONE : View.VISIBLE);
		mBtnFabu.setEnabled(!empty);

		if (App.showLoadingOrNoNet(empty, loaddata, feedbackIfNoNet, mImgState)) loadData();
	}

	private void showToEditItem(int position, EditListBean data) {
		getAdapter().setSelected(position);

		mCurrent = data;
		mTempleId = mCurrent.id;
		/*
		EditListBean magicData = new EditListBean();
		magicData.id = data.id;
		magicData.magicBoard = new MagicBoardBean();

		magicData.magicBoard.imgUrl = data.magicBoard.imgUrl;
		magicData.magicBoard.textFontCode = data.magicBoard.textFontCode;
		magicData.magicBoard.textColor = data.magicBoard.textColor;
		magicData.magicBoard.textDefault = data.magicBoard.textDefault;
		magicData.magicBoard.left = data.magicBoard.left;
		magicData.magicBoard.top = data.magicBoard.top;
		magicData.magicBoard.width = data.magicBoard.width;
		magicData.magicBoard.height = data.magicBoard.height;
		magicData.magicBoard.waterMarkAlign = data.magicBoard.waterMarkAlign;
		 */

		mMagicBoard.setOnTextSizeChangeListener(mCurrent.magicBoard.getOnTextSizeChangeListener());

		MagicBoardUtils.getBitmapLoader(EditActy.this).display(mMagicBoard, mCurrent.magicBoard.imgUrl);
		mMagicBoard.setTextCoordsBaseOnImage(mCurrent.magicBoard.left, mCurrent.magicBoard.top, mCurrent.magicBoard.width, mCurrent.magicBoard.height);
		mMagicBoard.setTextFont(FontUtils.getTypefaceWithCode(EditActy.this, mCurrent.magicBoard.textFontCode));
		mMagicBoard.setTextColor(Color.parseColor(mCurrent.magicBoard.textColor));
		if (mCurrent.magicBoard.isTextSizeSaved()) mMagicBoard.setTextSize(mCurrent.magicBoard.getTextSizeInPx());
		mMagicBoard.setText(mCurrent.magicBoard.textDefault);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mWaterMark.getLayoutParams();
		switch (mCurrent.magicBoard.waterMarkAlign) {
		case TOP_LEFT:
			lp.gravity = Gravity.TOP | Gravity.LEFT;
			break;
		case TOP_RIGHT:
			lp.gravity = Gravity.TOP | Gravity.RIGHT;
			break;
		case BOTTOM_LEFT:
			lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
			break;
		case BOTTOM_RIGHT:
			lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			break;
		case CENTER:
			lp.gravity = Gravity.CENTER;
			break;
		default:
			break;
		}
		mWaterMark.setLayoutParams(lp);
	}

	@Override
	protected void onResume() {
		super.onResume();
		NetConnectionReceiver.registerObserver(mNetObserver);
		StorageReceiver.registerObserver(mToastStorageObserver);
		if (mDialogViewHolder != null) mDialogViewHolder.onResume();
	}

	@Override
	protected void onPause() {
		if (mDialogViewHolder != null) mDialogViewHolder.onPause();
		StorageReceiver.unregisterObserver(mToastStorageObserver);
		NetConnectionReceiver.unregisterObserver(mNetObserver);
		super.onPause();
	}

	@Override
	protected void onStop() {
		recycleFabuBmp();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		recycleFabuBmp();
		unregisterLocationReceiver();
		Baidu.destroy();
		mPaint = null;
		mCanvas = null;
		if (mDialog != null) mDialog.cancel();
		mDialog = null;
		mDialogViewHolder = null;
		super.onDestroy();
	}

	private void showShareDialog(String editText, String snapShotPath) {
		if (mDialog == null) {
			mDialog = new Dialog(this, R.style.Theme_Wei_C_Dialog_Alert);
			View dialogView = DialogViewHolder.makeView(DialogViewHolder.class, mDialog.getLayoutInflater(), (ViewGroup)mDialog.getWindow().getDecorView());
			ViewGroup.LayoutParams lpView = dialogView.getLayoutParams();
			WindowManager.LayoutParams lpWindow = mDialog.getWindow().getAttributes();
			lpWindow.width = lpView.width;
			lpWindow.height = lpView.height;
			lpWindow.gravity = Gravity.CENTER;
			lpWindow.windowAnimations = android.R.style.Animation_Dialog;
			//mDialog.getWindow().setGravity(gravity);
			//mDialog.getWindow().setWindowAnimations(resId);
			mDialogViewHolder = DialogViewHolder.bindView(0, dialogView, DialogViewHolder.class, new Data(editText, snapShotPath), mOnShareComplete);
			mDialog.setContentView(dialogView);
		} else {
			DialogViewHolder.bindView(mDialogViewHolder, 0, new Data(editText, snapShotPath));
		}
		mDialog.show();
		mDialogViewHolder.startAnimIn();
	}

	private final OnClickListener mOnShareComplete = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mDialog != null) mDialog.cancel();
			//预留最近的3张图片，其他全部删除
			String currSnapshotPath = (String)v.getTag();
			File dir = new File(currSnapshotPath).getParentFile();
			File[] files = dir.listFiles(mFilenameFilter);
			if (files != null && files.length > 3) {
				Arrays.sort(files, mComparator);
				int count = 0;
				for (File f : files) {
					count++;
					L.i(EditActy.class, "count:" + count + ", filename:" + f.getPath());
					if (count > 3) {
						f.delete();
						L.e(EditActy.class, "删除--------");
					}
				}
			}
		}
	};

	private String createSnapshot() throws Exception {
		//让光标消失掉
		mMagicBoard.getTextView().setEnabled(false);
		try {
			mSnapshotPanel.destroyDrawingCache();
			mSnapshotPanel.buildDrawingCache();
			Bitmap snapshot = mSnapshotPanel.getDrawingCache();
			L.d(EditActy.class, "bmp:" + (snapshot == null ? "null" : ("[" + snapshot.getWidth() + ", " + snapshot.getHeight() + "]") + snapshot));
			Canvas canvas = getCanvas();
			Bitmap fabuBmp = getFabuBmp();
			canvas.setBitmap(fabuBmp);
			canvas.drawBitmap(snapshot, new Rect(0, 0, snapshot.getWidth(), snapshot.getHeight()),
					new Rect(0, 0, fabuBmp.getWidth(), fabuBmp.getHeight()), getPaint());
			mSnapshotPanel.destroyDrawingCache();

			/* 路径不要使用App私有目录，否则可能无法分享出去，需测试，目前测试发现4.4+的机型也没问题，
			 * 保险起见，还是改为大文件模式（不能使用系统/data/目录和App私有目录），且手动创建存储目录。
			 */
			String path = App.getImagesCacheDir4ThirdApi().getPath() + File.separator + getSnapshotFilename();
			L.d(EditActy.class, "path:" + path);

			BitmapUtils.saveImage(path, fabuBmp);
			return path;
		} finally {
			mMagicBoard.getTextView().setEnabled(true);
		}
	}

	private String getSnapshotFilename() {
		return "snapshot" + "-" + mCategoryId + "-" + mTempleId + "-" + System.currentTimeMillis() + ".png";
	}

	private static final FilenameFilter mFilenameFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String filename) {
			return filename.startsWith("snapshot");
		}
	};

	private static final Comparator<File> mComparator = new Comparator<File>() {
		@Override
		public int compare(File lhs, File rhs) {
			String lfn = lhs.getName();
			String rfn = rhs.getName();
			lfn = lfn.substring(lfn.lastIndexOf('-') + 1);
			rfn = rfn.substring(rfn.lastIndexOf('-') + 1);
			return - lfn.compareTo(rfn);
		};
	};

	private Paint getPaint() {
		if (mPaint == null) {
			mPaint = new Paint();
			//设置抗锯齿，三者必须同时设置效果才可以
			mPaint.setAntiAlias(true);	//等同于mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			mPaint.setFilterBitmap(true);
			mPaint.setDither(true);
		}
		return mPaint;
	}

	private Canvas getCanvas() {
		if (mCanvas == null) mCanvas = new Canvas();
		return mCanvas;
	}

	private Bitmap getFabuBmp() {
		if (mFabuBmp == null) mFabuBmp = Bitmap.createBitmap(640, 640, Config.ARGB_8888);
		return mFabuBmp;
	}

	private void recycleFabuBmp() {
		if (mFabuBmp != null) {
			mFabuBmp.recycle();
			mFabuBmp = null;
		}
	}

	@Override
	protected int listViewId() {
		return R.id.m_edit_grid_view;
	}

	@Override
	protected EditGridAdapter newAdapter() {
		return new EditGridAdapter(this, mOnFavoriteClick);
	}

	private void loadData() {
		try {
			AjaxParams params = new AjaxParams();
			params.put("pid", String.valueOf(mCategoryId));
			App.getFHttp().get(Const.Url.getEditTempList(),
					App.getHeadersWithUidToken(),
					params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					L.d(EditActy.class, json);
					if (!App.checkApiJsonError(EditActy.class, json)) {
						parseJson(json, true);
					}
					ensureViewState(false, false);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(EditActy.class, ApiType.DEF, t, errorNo, strMsg);
					ensureViewState(false, false);
				}
			});
		} catch (TokenNotValidException e) {
			L.e(EditActy.class, e);
		}
	}

	private void parseJson(String json, boolean save) {
		TempleList source = new TempleList().fromJson(json);
		if (save) saveAsCache(json);
		List<EditListBean> listData = new ArrayList<EditListBean>();
		for (TempleList.Temp temp : source.info.temps) {
			listData.add(new EditListBean(temp));
		}
		updateListData(listData);
		if (listData.size() > 0) {
			if (mCurrent == null) {
				showToEditItem(0, listData.get(0));
			} else {
				int position = -1;
				for (int i = 0; i < listData.size(); i++) {
					if (listData.get(i).id == mCurrent.id) {
						position = i;
						break;
					}
				}
				getAdapter().setSelected(position);
				getListView().smoothScrollToPosition(position);
			}
		}
		if (!mGuideShowed) {
			GuideViewHolder.ensureGuideShown(EditActy.this, (ViewGroup)mContentView.getParent());
			mGuideShowed = true;
		}
	}

	private static final String sCacheKey	= "data_cache_json";
	private boolean loadFromCache() {
		String json = SPref.getFromFile(this, this, sCacheKey + "_" + mCategoryId);
		if (json == null) return false;
		parseJson(json, false);
		ensureViewState(false, false);
		return true;
	}

	private void saveAsCache(String json) {
		SPref.saveAsFile(this, this, sCacheKey + "_" + mCategoryId, json);
	}

	private void commitFavoriteAdd(final long tempId) {
		try {
			AjaxParams params = new AjaxParams();
			params.put("mid", String.valueOf(tempId));
			App.getFHttp().post(Const.Url.addFavorite,
					App.getHeadersWithUidToken(),
					params, null, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					L.d(EditActy.class, json);
					if (!App.checkApiJsonError(EditActy.class, json)) {
						FavoriteAdd fa = new FavoriteAdd().fromJson(json);
						if (fa.info.mid != tempId) {
							failureAddFavorite(tempId);
							L.d(EditActy.class, "commitFavoriteAdd(tempId)---------result.info.mid != tempId");
						}
					} else {
						//已经收藏
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(EditActy.class, ApiType.FAV_ADD, t, errorNo, strMsg);
					failureAddFavorite(tempId);
				}

				private void failureAddFavorite(long tempId) {
					for (EditListBean data : getAdapter().getData()) {
						if (data.id == tempId) {
							data.favorite = false;
							break;
						}
					}
					getAdapter().notifyDataSetChanged();
				}
			});
		} catch (TokenNotValidException e) {
			L.e(EditActy.class, e);
		}
	}

	private void commitFavoriteDelete(final long tempId) {
		try {
			AjaxParams params = new AjaxParams();
			params.put("mid", String.valueOf(tempId));
			App.getFHttp().get(Const.Url.deleteFavorite,
					App.getHeadersWithUidToken(),
					params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					L.d(EditActy.class, json);
					if (!App.checkApiJsonError(EditActy.class, json)) {
						FavoriteDelete fd = new FavoriteDelete().fromJson(json);
						if (fd.info.mid[0] != tempId) {
							failureDeleteFavorite(tempId);
							L.d(EditActy.class, "commitFavoriteDelete(tempId)---------result.info.mid[0] != tempId");
						}
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(EditActy.class, ApiType.FAV_DELETE, t, errorNo, strMsg);
					failureDeleteFavorite(tempId);
				}

				private void failureDeleteFavorite(long tempId) {
					for (EditListBean data : getAdapter().getData()) {
						if (data.id == tempId) {
							data.favorite = true;
							break;
						}
					}
					getAdapter().notifyDataSetChanged();
				}
			});
		} catch (TokenNotValidException e) {
			L.e(EditActy.class, e);
		}
	}

	private void commitShare(String text) {
		mCommitList.add(new CommitBean(mTempleId, text));

		registerLocationReceiver();
		Baidu.requestLocation(this);
	}

	private void registerLocationReceiver() {
		if (mLocationReceiver == null) {
			mLocationReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction().equals(Baidu.ACTION_LOC_SUCCESS)) {
						String result = intent.getStringExtra(Baidu.EXTRA_RESULT);
						//L.d(YaActy.class, "onReceive--------result:" + result);
						try {
							JSONObject json = new JSONObject(result);
							if (new LocData().isBelongToMe(json)) {
								mLocData = new LocData().fromJson(result);
								mLocNet = null;
								L.d(EditActy.class, "onReceive--------LocData");
							} else if (new LocGps().isBelongToMe(json)) {
								mLocData = new LocGps().fromJson(result);
								mLocNet = null;
								L.d(EditActy.class, "onReceive--------LocGps");
							} else if (new LocNet().isBelongToMe(json)) {
								mLocNet = new LocNet().fromJson(result);
								mLocData = null;
								L.d(EditActy.class, "onReceive--------LocNet");
							} else if (new LocNetvGsm().isBelongToMe(json)) {
								mLocNet = new LocNetvGsm().fromJson(result);
								mLocData = null;
								L.d(EditActy.class, "onReceive--------LocNetvGsm");
							} else {
								L.d(EditActy.class, "locData----null");
							}
						} catch (JSONException e) {
							L.e(EditActy.class, e);
						}
					}
					commitShare();
				}
			};
			IntentFilter filter = new IntentFilter();
			filter.addAction(Baidu.ACTION_LOC_SUCCESS);
			filter.addAction(Baidu.ACTION_LOC_UNKNOWN);
			filter.addAction(Baidu.ACTION_LOC_FAILED_CLIENT);
			filter.addAction(Baidu.ACTION_LOC_FAILED_SERVER);
			filter.addAction(Baidu.ACTION_LOC_FAILED_KEY_ERROR);
			filter.addAction(Baidu.ACTION_LOC_FAILED_KEY_AUTHORIZE);
			registerReceiver(mLocationReceiver, filter);
		}
	}

	private void unregisterLocationReceiver() {
		if (mLocationReceiver != null) {
			unregisterReceiver(mLocationReceiver);
			mLocationReceiver = null;
		}
	}

	private final List<CommitBean> mCommitList = new ArrayList<CommitBean>();
	private BroadcastReceiver mLocationReceiver;
	private LocData mLocData;
	private LocNet mLocNet;

	private static class CommitBean {
		public final long templeId;
		public final String text;

		public CommitBean(long templeId, String text) {
			this.templeId = templeId;
			this.text = text;
		}
	}

	private void commitShare() {
		try {
			if (mCommitList.size() == 0 || (mLocNet == null && mLocData == null)) return;
			final CommitBean commitBean = mCommitList.get(0);
			L.d(EditActy.class, "commitShare------templeId:" + commitBean.templeId + ", text:" + commitBean.text);

			AjaxParams params = new AjaxParams();
			params.put("zmid", String.valueOf(commitBean.templeId));
			params.put("text", String.valueOf(commitBean.text));
			//必填：token zmid text longitude latitude
			//选填：city province city_name province_name address pinyin more
			if (mLocNet != null) {
				params.put("longitude", String.valueOf(mLocNet.longitude));
				params.put("latitude", String.valueOf(mLocNet.latitude));

				params.put("city", String.valueOf(mLocNet.cityCode));
				params.put("province", String.valueOf(mLocNet.province));
				params.put("city_name", String.valueOf(mLocNet.city));
				params.put("province_name", String.valueOf(mLocNet.province));
				params.put("address", String.valueOf(mLocNet.addrStr));
				//params.put("pinyin", String.valueOf(mLocNet.pinyin));
				//params.put("more", String.valueOf(mLocNet.more));
			} else if (mLocData != null) {
				params.put("longitude", String.valueOf(mLocData.longitude));
				params.put("latitude", String.valueOf(mLocData.latitude));
			}

			App.getFHttp().get(Const.Url.createShareSimple,
					App.getHeadersWithUidToken(),
					params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					mCommitList.remove(commitBean);
					commitShare();
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(EditActy.class, ApiType.COMMIT, t, errorNo, strMsg);
				}
			});
		} catch (TokenNotValidException e) {
			L.e(EditActy.class, e);
		}
	}

	@ViewLayoutId(R.layout.d_m_edit_share)
	private static class DialogViewHolder extends ViewHolder<Data, OnClickListener> {
		@ViewId(R.id.d_m_edit_share_sina_weibo_btn)
		private TextView mBtnSinaWeibo;
		@ViewId(R.id.d_m_edit_share_qzone_btn)
		private TextView mBtnQzone;
		@ViewId(R.id.d_m_edit_share_wechat_moments_btn)
		private TextView mBtnWechatMoments;
		@ViewId(R.id.d_m_edit_share_wechat_btn)
		private TextView mBtnWechat;
		@ViewId(R.id.d_m_edit_share_qq_btn)
		private TextView mBtnQQ;
		@ViewId(R.id.d_m_edit_share_local_btn)
		private TextView mBtnLocal;

		@ViewId(R.id.d_m_edit_share_sina_weibo_ok)
		private ImageView mOkSinaWeibo;
		@ViewId(R.id.d_m_edit_share_qzone_ok)
		private ImageView mOkQzone;
		@ViewId(R.id.d_m_edit_share_wechat_moments_ok)
		private ImageView mOkWechatMoments;
		@ViewId(R.id.d_m_edit_share_wechat_ok)
		private ImageView mOkWechat;
		@ViewId(R.id.d_m_edit_share_qq_ok)
		private ImageView mOkQQ;
		@ViewId(R.id.d_m_edit_share_local_ok)
		private ImageView mOkLocal;

		@ViewId(R.id.d_m_edit_share_complete)
		private Button mBtnComplete;

		private OnClickListener mOnCompleteClick;

		private Context mContext;
		private String mCurrPlatform;
		private boolean mShareComplete = false, mPaused = false;
		private String mPathSaved;

		public DialogViewHolder(View view) {
			super(view);
			mContext = view.getContext();
		}

		@Override
		protected void init(OnClickListener... args) {
			mBtnSinaWeibo.setOnClickListener(mOnClickListener);
			mBtnQzone.setOnClickListener(mOnClickListener);
			mBtnWechatMoments.setOnClickListener(mOnClickListener);
			mBtnWechat.setOnClickListener(mOnClickListener);
			mBtnQQ.setOnClickListener(mOnClickListener);
			mBtnLocal.setOnClickListener(mOnClickListener);
			mOnCompleteClick = args[0];
			mBtnComplete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startAnimOut();
				}
			});
		}

		@Override
		public void bind(int position, Data data) {
			mBtnComplete.setTag(data.mSnapShotPath);
			resetState();

			mOkSinaWeibo.setVisibility(View.GONE);
			mOkQzone.setVisibility(View.GONE);
			mOkWechatMoments.setVisibility(View.GONE);
			mOkWechat.setVisibility(View.GONE);
			mOkQQ.setVisibility(View.GONE);
			mOkLocal.setVisibility(View.GONE);
		}

		public void onResume() {
			L.d(EditActy.class, "onResume-------------");
			if (mShareComplete) {
				if (mCurrPlatform.equals(SinaWeibo.NAME)) {
					mOkSinaWeibo.setVisibility(View.VISIBLE);
				} else if (mCurrPlatform.equals(QZone.NAME)) {
					mOkQzone.setVisibility(View.VISIBLE);
				} else if (mCurrPlatform.equals(WechatMoments.NAME)) {
					mOkWechatMoments.setVisibility(View.VISIBLE);
				} else if (mCurrPlatform.equals(Wechat.NAME)) {
					mOkWechat.setVisibility(View.VISIBLE);
				} else if (mCurrPlatform.equals(QQ.NAME)) {
					mOkQQ.setVisibility(View.VISIBLE);
				}
			}
			resetState();
		}

		private void resetState() {
			mCurrPlatform = null;
			mShareComplete = false;
			mPaused = false;
			mPathSaved = null;
		}

		public void onPause() {
			mPaused = true;
			L.d(EditActy.class, "onPause++++++++++++++");
		}

		public void startAnimIn() {
			//getView().startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.m_edit_share_panel_in));
		}

		public void startAnimOut() {
			/*Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.m_edit_share_panel_out);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					mOnCompleteClick.onClick(mBtnComplete);
				}
			});
			getView().startAnimation(anim);*/
			mOnCompleteClick.onClick(mBtnComplete);
		}

		private final OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == mBtnSinaWeibo) {
					SinaWeiboShare sina = ShareSdk.share(mContext, SinaWeiboShare.class, mPlatformActionListener);
					//mData.mEditText
					sina.shareTextImagePath(Share.getTextNotOverMax(sina, "#萌Mark日常#", Const.APP_SITE_URL), getData().mSnapShotPath, 0.0f, 0.0f);
					mCurrPlatform = SinaWeibo.NAME;
				} else if (v == mBtnQzone) {
					QZoneShare qzone = ShareSdk.share(mContext, QZoneShare.class, mPlatformActionListener);
					//注意有空格和无空格的情况，不可随意修改
					//qzone.shareTextImagePath("", Const.APP_SITE_URL, " ", bitmapPath, null, Const.APP_SITE_URL);
					qzone.shareTextImagePath("", Const.APP_SITE_URL, Share.getTextNotOverMax(qzone, getData().mEditText, null), getData().mSnapShotPath, null, Const.APP_SITE_URL);
					mCurrPlatform = QZone.NAME;
					//不会正确回调，因此，只要调用成功即视为分享成功
					//mPlatformActionListener.onComplete(ShareSdk.getPlatform(mContext, QZone.NAME), Platform.ACTION_SHARE, null);
				} else if (v == mBtnWechatMoments) {
					WechatMomentsShare wechatMoments = ShareSdk.share(mContext, WechatMomentsShare.class, mPlatformActionListener);
					wechatMoments.shareImage(getData().mSnapShotPath);
					mCurrPlatform = WechatMoments.NAME;
					//不跳页，且不会回调，但是会弹窗，因此会收到onPause()，只要调用成功即视为分享成功，那么只要在onResume()时认为成功即可
					mShareComplete = true;
				} else if (v == mBtnWechat) {
					//这里使用表情接口来发送清晰的图像
					//但是效果不理想，发现表情me对于静态图，还是使用的图像方式而不是表情方式发送的
					WeChat.shareAsEmoji(mContext, "表情分享标题(该参数不起作用)", "内容描述(该参数不起作用)", getData().mSnapShotPath, getData().mSnapShotPath, false);

					//////WechatShare wechat = ShareSdk.share(mContext, WechatShare.class, mPlatformActionListener);
					//////wechat.shareImage(getData().mSnapShotPath);
					mCurrPlatform = Wechat.NAME;
					//不会正确回调，但会跳页，因此，只要调用成功即视为分享成功，同上
					mShareComplete = true;
				} else if (v == mBtnQQ) {
					QQShare qq = ShareSdk.share(mContext, QQShare.class, mPlatformActionListener);
					qq.shareTextImagePath("", null, "", getData().mSnapShotPath);
					mCurrPlatform = QQ.NAME;
				} else if (v == mBtnLocal) {
					//复制文件到相册文件夹
					if (mPathSaved == null) {
						mPathSaved = PhotoUtils.savePictureToPhotoAlbum(mContext, null, getData().mSnapShotPath, null, mContext.getResources().getString(R.string.app_name));
						if (mPathSaved == null) {
							File file = FileUtils.copyFileToDir(getData().mSnapShotPath, App.getPicturesSaveDir(), "QMark-", "png");
							if (file != null) mPathSaved = file.getPath();
						}
						if (mPathSaved != null) {
							Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
							i.setData(Uri.fromFile(new File(mPathSaved)));
							mContext.sendBroadcast(i);
						}
						L.i(EditActy.class, "mPathSaved: " + mPathSaved);
					}
					if (mPathSaved == null) {
						Toast.makeText(mContext, R.string.d_m_edit_share_error_local, Toast.LENGTH_LONG).show();
					} else {
						mOkLocal.setVisibility(View.VISIBLE);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.d_m_edit_share_complete_local, mPathSaved), Toast.LENGTH_LONG).show();
					}
				}
			}
		};

		private final PlatformActionListener mPlatformActionListener = new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int action, Throwable e) {
				L.e(EditActy.class, "[onError]platform:" + platform.getName());
				//parseAction(action);

				mShareComplete = false;
				if (action == Platform.ACTION_SHARE) {
					if (platform.getName().equals(SinaWeibo.NAME)) {
						if (e != null) {
							SinaWeiboError error = new SinaWeiboError().fromJson(e.getMessage());
							Toast.makeText(mContext, error.error_code == 20016 ?
									R.string.d_m_edit_share_error_sina_weibo_update_too_fast_code_20016 :
										R.string.d_m_edit_share_error_sina_weibo, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext, R.string.d_m_edit_share_error_sina_weibo, Toast.LENGTH_LONG).show();
						}
					} else if (platform.getName().equals(QZone.NAME)) {
						//
					} else if (platform.getName().equals(WechatMoments.NAME)) {
						//
					} else if (platform.getName().equals(Wechat.NAME)) {
						//
					} else if (platform.getName().equals(QQ.NAME)) {
						//
					}
				}
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> map) {
				L.d(EditActy.class, "[onComplete]platform:" + platform.getName());
				//parseAction(action);

				mShareComplete = true;
				if (action == Platform.ACTION_SHARE) {
					if (platform.getName().equals(SinaWeibo.NAME)) {
						//Platform.isSSODisable()与是否跳客户端没关系
						L.e(EditActy.class, "complete---------------SinaWeibo");
						/* 启用了客户端分享，但是在打开客户端之前或者没有安装客户端而直接进行了微博分享都会回调本方法，
						 * 而我无法分辨这两种情况，没有相关的方法或返回参数。
						 * 但有一个办法：如果是打开客户端，则会先执行onPause(),再执行本方法。so...
						 */
						if(!mPaused) {
							//不跳页，给来个Toast
							Toast.makeText(mContext, R.string.d_m_edit_share_complete_sina_weibo, Toast.LENGTH_LONG).show();
							mOkSinaWeibo.setVisibility(View.VISIBLE);
							resetState();
						}
					} else if (platform.getName().equals(QZone.NAME)) {
						//正常跳页且正确回调，但统一放到onStart()
						//mOkQzone.setVisibility(View.VISIBLE);
					} else if (platform.getName().equals(WechatMoments.NAME)) {
						//不跳页，且不会回调，但是会弹窗，因此会收到onPause()
						L.e(EditActy.class, "complete---------------WechatMoments");
						//mOkWechatMoments.setVisibility(View.VISIBLE);
					} else if (platform.getName().equals(Wechat.NAME)) {
						//跳页，但不会正确回调，返回即视为发布成功
						//mOkWechat.setVisibility(View.VISIBLE);
					} else if (platform.getName().equals(QQ.NAME)) {
						//会跳页，会正确回调，但是在跳页之前回调，这里等待跳页回来调用onStart()
						//mOkQQ.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void onCancel(Platform platform, int action) {
				L.w(EditActy.class, "[onCancel]platform:" + platform.getName());
				//parseAction(action);

				if (action == Platform.ACTION_SHARE) {
					if (platform.getName().equals(SinaWeibo.NAME)) {
						//
					} else if (platform.getName().equals(QZone.NAME)) {
						//
					} else if (platform.getName().equals(WechatMoments.NAME)) {
						//
					} else if (platform.getName().equals(Wechat.NAME)) {
						//
					} else if (platform.getName().equals(QQ.NAME)) {
						//
					}
				}
			}

			/*
			private void parseAction(int action) {
				switch (action) {
				case Platform.ACTION_AUTHORIZING:
					L.i(EditActy.class, "ACTION_AUTHORIZING");
					break;
				case Platform.ACTION_FOLLOWING_USER:
					L.i(EditActy.class, "ACTION_FOLLOWING_USER");
					break;
				case Platform.ACTION_GETTING_FRIEND_LIST:
					L.i(EditActy.class, "ACTION_GETTING_FRIEND_LIST");
					break;
				case Platform.ACTION_SENDING_DIRECT_MESSAGE:
					L.i(EditActy.class, "ACTION_SENDING_DIRECT_MESSAGE");
					break;
				case Platform.ACTION_SHARE:
					L.i(EditActy.class, "ACTION_SHARE");
					break;
				case Platform.ACTION_TIMELINE:
					L.i(EditActy.class, "ACTION_TIMELINE");
					break;
				case Platform.ACTION_USER_INFOR:
					L.i(EditActy.class, "ACTION_USER_INFOR");
					break;
				}
			}*/
		};
	}

	private static class Data {
		public String mEditText;
		public String mSnapShotPath;

		public Data(String editText, String snapShotPath) {
			mEditText = editText;
			mSnapShotPath = snapShotPath;
		}
	}

	@ViewLayoutId(R.layout.m_edit_guide)
	private static class GuideViewHolder extends ViewHolder<Void, Void> {
		private static final String KEY_GUIDE_SHOWN		= "guide_shown";
		@ViewId(R.id.m_edit_guide_btn_iknow)
		private ImageButton mBtnIknow;

		public GuideViewHolder(View view) {
			super(view);
		}

		public static void ensureGuideShown(Activity context, ViewGroup parent) {
			if (!SPref.getSPref(context, GuideViewHolder.class).getBoolean(KEY_GUIDE_SHOWN, false)) {
				View view = GuideViewHolder.makeView(GuideViewHolder.class, context.getLayoutInflater(), parent);
				GuideViewHolder.bindView(0, view, GuideViewHolder.class, null);
				parent.addView(view);
			}
		}

		@Override
		protected void init(Void... args) {
			mBtnIknow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SPref.edit(getView().getContext(), GuideViewHolder.class).putBoolean(KEY_GUIDE_SHOWN, true).commit();
					((ViewGroup)getView().getParent()).removeView(getView());
				}
			});
		}

		@Override
		public void bind(int position, Void data) {}
	}
}
