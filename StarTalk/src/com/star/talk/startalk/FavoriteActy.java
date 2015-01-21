package com.star.talk.startalk;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.star.talk.startalk.App.ApiType;
import com.star.talk.startalk.adapter.FavoriteGridAdapter;
import com.star.talk.startalk.adapter.FavoriteGridAdapter.FavoriteGridViewHolder;
import com.star.talk.startalk.adapter.FavoriteGridAdapter.MoreGridViewHolder;
import com.star.talk.startalk.data.FavoriteBean;
import com.star.talk.startalk.data.api.FavoriteList;
import com.star.talk.startalk.data.api.RecentUse;
import com.star.talk.startalk.data.api.TempleList.Temp;
import com.star.talk.startalk.viewholder.FavoriteDeleteViewHolder;
import com.tisumoon.AbsBaseListViewActivity;
import com.tisumoon.exception.TokenNotValidException;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.anno.ViewListId;
import com.wei.c.data.abs.AbsJson;
import com.wei.c.phone.Network;
import com.wei.c.phone.Network.State;
import com.wei.c.phone.Network.Type;
import com.wei.c.receiver.net.NetConnectionReceiver;
import com.wei.c.receiver.net.NetObserver;
import com.wei.c.utils.ArrayUtils;
import com.wei.c.utils.SPref;

@ViewLayoutId(R.layout.m_favorite)
@ViewListId(R.id.m_favorite_grid_view)
public class FavoriteActy extends AbsBaseListViewActivity<GridView, FavoriteBean, FavoriteGridAdapter> {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, FavoriteActy.class));
	}

	@ViewId(R.id.m_favorite_title_left_btn_back)
	private ImageButton mBtnBack;
	@ViewId(R.id.m_favorite_title_right_manage)
	private TextView mBtnManage;
	@ViewId(R.id.m_favorite_img_state)
	private ImageView mImgState;
	@ViewId(R.id.m_favorite_radiobtn_recent)
	private RadioButton mRBtnRct;
	@ViewId(R.id.m_favorite_radiobtn_favorite)
	private RadioButton mRBtnFav;
	@ViewId(R.id.m_favorite_swipe_refresh)
	private SwipeRefreshLayout mSwipeRefresh;

	private static final FavoriteBean mFavoriteHasNextBean = new FavoriteBean(true);

	private List<FavoriteBean> mDataRct, mDataFav;
	private CompoundButton mCurrentBtn;

	private int mFavoritePage;
	private boolean mFavoriteHasNextPage = true;
	private boolean mDeleteMode = false;
	private boolean mNeedRefresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBtnManage.setOnClickListener(mOnManageBtnClickListener);
		mImgState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ensureViewState(true, mCurrentBtn == mRBtnRct, true);
			}
		});

		mCurrentBtn = mRBtnFav.isChecked() ? mRBtnFav : mRBtnRct;
		if (mCurrentBtn != mRBtnFav) mRBtnRct.setChecked(true);
		updateBtnManageState();

		mRBtnRct.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRBtnFav.setOnCheckedChangeListener(mOnCheckedChangeListener);
		getListView().setOnItemClickListener(mOnItemClickListener);
		mSwipeRefresh.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (mCurrentBtn == mRBtnFav) {
					if (getAdapter().getCount() == 0) {	//为空，则不显示swipeRefresh
						mSwipeRefresh.setRefreshing(false);
						ensureViewState(true, false, true);
					} else {
						if (App.checkNetStateAndFeedbackUser()) {
							loadDataFavoriteFromNet(true, 1);
						} else {
							updateLoadingState(true, false);
						}
					}
				} else {
					mSwipeRefresh.setRefreshing(false);
				}
			}
		});
		mSwipeRefresh.setColorScheme(R.color.m_favorite_swipe_refresh_0, R.color.m_favorite_swipe_refresh_1,
				R.color.m_favorite_swipe_refresh_2, R.color.m_favorite_swipe_refresh_3);
		//mSwipeFefresh.canChildScrollUp();

		doLoadDataRecentUse(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		NetConnectionReceiver.registerObserver(mNetObserver);
		mNeedRefresh = true;
		doReloadData();
	}

	@Override
	protected void onPause() {
		NetConnectionReceiver.unregisterObserver(mNetObserver);
		super.onPause();
	}

	@Override
	protected FavoriteGridAdapter newAdapter() {
		return new FavoriteGridAdapter(this);
	}

	private final NetObserver mNetObserver = new NetObserver() {
		@Override
		public void onChanged(Type type, State state) {
			ensureViewState(true, mCurrentBtn == mRBtnRct, false);
		}
	};

	private boolean ensureViewState(boolean loaddata, boolean recentUse, boolean feedbackIfNoNet) {
		if (App.showLoadingOrNoNet(getAdapter().getCount() == 0, loaddata, feedbackIfNoNet, mImgState)) {
			if (recentUse) {
				loadDataRecentUseFromNet();
			} else {
				loadDataFavoriteFromNet(true, 1);
			}
			return true;
		}
		return false;
	}

	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			L.d(FavoriteActy.class, "onItemClick----");
			FavoriteBean data;
			if (mCurrentBtn == mRBtnRct) {
				data = mDataRct.get(position);
			} else {
				data = mDataFav.get(position);
				if (mDeleteMode && !data.hasMore) {
					FavoriteGridViewHolder vHolder = (FavoriteGridViewHolder)view.getTag();
					vHolder.setChecked(!data.selected);
					ensureSelectedCountAndBtnState();
					return;
				} else if (data.hasMore) {
					int page = mFavoritePage + 1;
					boolean loaded = loadDataFavoriteFromCache(page);
					MoreGridViewHolder vHolder = (MoreGridViewHolder)view.getTag();
					vHolder.setMoreLoading(!loaded);
					if (loaded) {
						if (Network.isNetConnected(FavoriteActy.this)) loadDataFavoriteFromNet(false, page);
					} else {
						if (App.checkNetStateAndFeedbackUser()) {
							loadDataFavoriteFromNet(false, page);
						} else {
							updateLoadingState(false, false);
						}
					}
					return;
				}
			}
			EditActy.startMe(FavoriteActy.this, data.topicId, data);
		}
	};

	private int mSelectedForDeleteCount;
	private void ensureSelectedCountAndBtnState() {
		mSelectedForDeleteCount = 0;
		for (FavoriteBean data : getAdapter().getData()) {
			if (data.selected) mSelectedForDeleteCount++;
		}
		if (mSelectedForDeleteCount > 0) {
			mBtnManage.setText(R.string.title_m_favorite_right_delete);
		} else {
			mBtnManage.setText(R.string.title_m_favorite_right_complete);
		}
	}

	private final OnClickListener mOnManageBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCurrentBtn == mRBtnFav) {
				if (mDeleteMode) {
					if (mSelectedForDeleteCount > 0) {	//弹出删除对话框
						FavoriteDeleteViewHolder.showDeleteDialog(FavoriteActy.this, (ViewGroup)getWindow().getDecorView(), mOnDeleteClick);
					} else {
						mDeleteMode = false;
						mBtnManage.setText(R.string.title_m_favorite_right_manage);
						getAdapter().setDeleteMode(mDeleteMode);
					}
				} else {
					mDeleteMode = true;
					mBtnManage.setText(R.string.title_m_favorite_right_complete);
					for (FavoriteBean data : getAdapter().getData()) {
						data.selected = false;
					}
					getAdapter().setDeleteMode(mDeleteMode);
				}
			}
		}
	};

	private final OnClickListener mOnDeleteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			List<Long> tempIds = new ArrayList<Long>();
			FavoriteBean data;
			for (int i = 0; i < mDataFav.size(); i++) {
				data = mDataFav.get(i);
				if (data.selected) {
					tempIds.add(data.id);
					mDataFav.remove(i);
					i--;
				}
			}
			getAdapter().notifyDataSetChanged();
			ensureSelectedCountAndBtnState();
			commitFavoriteDelete(tempIds.toArray(new Long[tempIds.size()]));
		}
	};

	private final OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				mCurrentBtn = buttonView;
				//先清空，避免与收藏的列表混淆
				updateListData(null);
				updateBtnManageState();
				getApp().getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						doReloadData();
					}
				});
			}
		}
	};

	private void updateBtnManageState() {
		if (mCurrentBtn == mRBtnRct) {
			mBtnManage.setVisibility(View.GONE);
			//getAdapter().setPageable(false);
		} else {
			mBtnManage.setVisibility(View.VISIBLE);
			//getAdapter().setPageable(true);
		}
	}

	private void doReloadData() {
		if (mCurrentBtn == mRBtnRct) {
			doLoadDataRecentUse(mNeedRefresh);
			mNeedRefresh = false;
		} else {
			doLoadDataFavorite();
		}
	}

	private void doLoadDataRecentUse(boolean refresh) {
		mSwipeRefresh.setRefreshing(false);
		if (refresh || mDataRct == null || mDataRct.size() == 0) {
			mDataRct = loadDataRecentUseFromCache(FavoriteActy.this);
			if (mDataRct == null || mDataRct.size() == 0) {
				//由于总会优先在本地存储记录，只有在本地没有数据的情况下，才访问网络
				ensureViewState(true, true, true);
			} else {
				updateListData(mDataRct);
				updateLoadingState(true, true);
			}
		} else {
			updateListData(mDataRct);
			updateLoadingState(true, true);
		}
	}

	private void doLoadDataFavorite() {
		if (mDataFav == null || mDataFav.size() == 0) {
			//策略不同，由于有分页，总是先读缓存再读网络
			loadDataFavoriteFromCache(1);
			//只是切换tab, 如果为空才从网络加载
			ensureViewState(true, false, true);
		} else {
			updateListData(mDataFav);
			updateLoadingState(true, false);
		}
	}

	private void loadDataRecentUseFromNet() {
		try {
			AjaxParams params = new AjaxParams();
			App.getFHttp().get(Const.Url.recentUse,
					App.getHeadersWithUidToken(),
					params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					L.d(FavoriteActy.class, json);
					if (!App.checkApiJsonError(FavoriteActy.class, json)) {
						parseRecentUseJson(json);
					}
					updateLoadingState(true, true);
					if (mCurrentBtn == mRBtnRct) mImgState.setImageResource(R.drawable.img_m_favorite_recent_empty);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					App.handleApiWorkFailureAndFeedbackUser(FavoriteActy.class, ApiType.DEF, t, errorNo, strMsg);
					updateLoadingState(true, true);
				}
			});
		} catch (TokenNotValidException e) {
			L.e(FavoriteActy.class, e);
		}
	}

	private void parseRecentUseJson(String json) {
		RecentUse source = new RecentUse().fromJson(json);
		if (source.info == null || source.info.common == null) {	//没有更多页，但是尝试加载更多页时，会为null
			return;
		}
		if (source.info.common.length > 0) {
			if (mDataRct == null) {
				mDataRct = new ArrayList<FavoriteBean>();
			} else {
				mDataRct.clear();
			}
			for (Temp temp : source.info.common) {
				mDataRct.add(new FavoriteBean(temp));
			}
			if (mCurrentBtn == mRBtnRct) {
				updateListData(mDataRct);
			}
			saveDataRecentUseToCache(this, mDataRct);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////

	private void loadDataFavoriteFromNet(boolean refresh, int page) {
		try {
			AjaxParams params = new AjaxParams();
			params.put("page", String.valueOf(refresh ? 1 : page));
			App.getFHttp().get(Const.Url.favorite,
					App.getHeadersWithUidToken(),
					params, new MyAjaxCallBack(refresh));
		} catch (TokenNotValidException e) {
			L.e(FavoriteActy.class, e);
		}
	}

	private class MyAjaxCallBack extends AjaxCallBack<String> {
		private boolean mRefresh;
		public MyAjaxCallBack(boolean refresh) {
			mRefresh = refresh;
		}

		@Override
		public void onSuccess(String json) {
			L.d(FavoriteActy.class, json);
			if (!App.checkApiJsonError(FavoriteActy.class, json)) {
				parseFavoriteJson(json, true);
			}
			updateLoadingState(mRefresh, false);
			if (mCurrentBtn == mRBtnFav) mImgState.setImageResource(R.drawable.img_m_favorite_fav_empty);
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			App.handleApiWorkFailureAndFeedbackUser(FavoriteActy.class, ApiType.DEF, t, errorNo, strMsg);
			updateLoadingState(mRefresh, false);
		}
	}

	private void updateLoadingState(boolean refresh, boolean recentUse) {
		if (refresh) {
			mSwipeRefresh.setRefreshing(false);
		} else {
			if (!recentUse) getAdapter().setMoreLoading(false);
		}
		ensureViewState(false, recentUse, false);
	}

	private void commitFavoriteDelete(Long[] tempIds) {
		if (tempIds == null || tempIds.length == 0) return;
		try {
			AjaxParams params = new AjaxParams();
			params.put("mid", ArrayUtils.join(tempIds, ","));
			App.getFHttp().get(Const.Url.deleteFavorite,
					App.getHeadersWithUidToken(),
					params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					L.d(FavoriteActy.class, json);
					if (!App.checkApiJsonError(FavoriteActy.class, json)) {
						//删除缓存
						deleteFavoriteCache(mFavoritePage);
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					loadDataFavoriteFromCache(1);
					App.handleApiWorkFailureAndFeedbackUser(FavoriteActy.class, ApiType.FAV_DELETE, t, errorNo, strMsg);
				}
			});
		} catch (TokenNotValidException e) {
			L.e(FavoriteActy.class, e);
		}
	}

	private void parseFavoriteJson(String json, boolean save) {
		FavoriteList source = new FavoriteList().fromJson(json);
		if (source.info == null || source.info.templates == null) {	//没有更多页，但是尝试加载更多页时，会为null
			mFavoriteHasNextPage = false;
			return;
		}
		if (source.info.page_no == 1 || source.info.page_no > mFavoritePage) {
			mFavoritePage = source.info.page_no;
			mFavoriteHasNextPage = source.info.has_next_page == 1;
		}
		if (save) saveDataFavoriteToCache(mFavoritePage, json);

		Set<FavoriteBean> dataSet = new LinkedHashSet<FavoriteBean>();
		//刷新第一页的数据，直接将其他页数据删除
		if (mFavoritePage > 1 && mDataFav != null) dataSet.addAll(mDataFav);
		dataSet.remove(mFavoriteHasNextBean);
		for (Temp temp : source.info.templates) {
			dataSet.add(new FavoriteBean(temp));
			L.d(FavoriteActy.class, "dataset---add---id:" + temp.mid);
		}
		if (dataSet.size() > 0) {
			if (mFavoriteHasNextPage) {
				mFavoriteHasNextBean.loading = false;
				dataSet.add(mFavoriteHasNextBean);
			}
			if (mDataFav == null) {
				mDataFav = new ArrayList<FavoriteBean>();
			} else {
				mDataFav.clear();
			}
			mDataFav.addAll(dataSet);
		}
		if (mCurrentBtn == mRBtnFav) {
			updateListData(mDataFav);
		}
	}

	private void saveDataFavoriteToCache(int page, String json) {
		SPref.saveAsFile(this, FavoriteActy.class, sCacheKeyFavorite + "_page_" + page, json);
	}

	private boolean loadDataFavoriteFromCache(int page) {
		String json = SPref.getFromFile(this, FavoriteActy.class, sCacheKeyFavorite + "_page_" + page);
		if (json == null) return false;
		parseFavoriteJson(json, false);
		updateLoadingState(page == 1, false);
		return true;
	}

	private void deleteFavoriteCache(int maxPage) {
		for (int i = 1; i <= maxPage; i++) {
			SPref.saveAsFile(this, FavoriteActy.class, sCacheKeyFavorite + "_page_" + i, null);
		}
	}

	public static void cacheRecentUse(Context context, FavoriteBean rct) {
		List<FavoriteBean> data = loadDataRecentUseFromCache(context);
		if (data == null) data = new ArrayList<FavoriteBean>();
		//插入最前面
		data.add(0, rct);
		if (data.size() > 1) {
			Set<FavoriteBean> dataSet = new LinkedHashSet<FavoriteBean>();
			dataSet.addAll(data);
			data.clear();
			data.addAll(dataSet);
		}
		//去掉最后面的
		while (data.size() > 15) {
			data.remove(data.size() - 1);
		}
		saveDataRecentUseToCache(context, data);
	}

	private static void saveDataRecentUseToCache(Context context, List<FavoriteBean> data) {
		SPref.saveAsFile(context, FavoriteActy.class, sCacheKeyRecentUse, (data == null || data.size() == 0) ? null : AbsJson.toJsonWithExposeAnnoFields(data));
	}

	private static List<FavoriteBean> loadDataRecentUseFromCache(Context context) {
		String json = SPref.getFromFile(context, FavoriteActy.class, sCacheKeyRecentUse);
		return json == null ? null : AbsJson.fromJsonWithExposeAnnoFields(json, new TypeToken<ArrayList<FavoriteBean>>(){});
	}

	private static final String sCacheKeyRecentUse		= "recent_use_json";
	private static final String sCacheKeyFavorite		= "favorite_cache_json";
}
