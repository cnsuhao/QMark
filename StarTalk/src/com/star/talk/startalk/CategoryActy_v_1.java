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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.star.talk.startalk.App.ApiType;
import com.star.talk.startalk.adapter.MainCategoryListAdapter;
import com.star.talk.startalk.data.MainCategoryListBean;
import com.star.talk.startalk.data.api.MainCategory;
import com.star.talk.startalk.viewholder.ShareLinkViewHolder;
import com.star.talk.startalk.widget.Pull2RefreshListView;
import com.tisumoon.AbsBaseListViewActivity;
import com.tisumoon.exception.TokenNotValidException;
import com.umeng.update.UmengUpdateAgent;
import com.wei.c.L;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.anno.ViewListId;
import com.wei.c.phone.Network;
import com.wei.c.phone.Network.State;
import com.wei.c.phone.Network.Type;
import com.wei.c.receiver.net.NetConnectionReceiver;
import com.wei.c.receiver.net.NetObserver;
import com.wei.c.utils.SPref;
import com.wei.c.widget.AbsPull2RefreshListView.OnRefreshListener;

@ViewLayoutId(R.layout.m_category_v_1)
@ViewListId(R.id.m_category_v_1_list)
public class CategoryActy_v_1 extends AbsBaseListViewActivity<Pull2RefreshListView, MainCategoryListBean, MainCategoryListAdapter> implements OnRefreshListener {
	public static void startMe(Context context) {
		startMe(context, new Intent(context, CategoryActy_v_1.class));
	}

	@ViewId(R.id.m_category_v_1_content)
	private ViewGroup mContentView;
	@ViewId(R.id.m_category_v_1_img_state)
	private ImageView mImgState;
	@ViewId(R.id.m_category_v_1_title_left_btn_favorite)
	private ImageButton mBtnFavorite;
	@ViewId(R.id.m_category_v_1_title_right_btn_setting)
	private ImageButton mBtnSetting;

	//加载每一页之前会加1
	private int mPage = 0;
	private boolean mHasNextPage;

	private ShareLinkViewHolder mShareLinkViewHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setOnRefreshListener(this);
		getListView().setOnItemClickListener(mOnItemClickListener);
		mImgState.setOnClickListener(mOnImgStateClick);
		mBtnFavorite.setOnClickListener(mOnBtnFavoriteClick);
		mBtnSetting.setOnClickListener(mOnBtnSettingClick);

		//加载数据，先读缓存
		//App.showLoadingOrNoNet(true, true, false, mImgState);
		boolean loaded = loadFromCache(1);
		//强制刷新发布数量数据
		if (loaded ? Network.isNetConnected(this) : App.checkNetStateAndFeedbackUser()) loadData(true, 1);

		// 友盟的在线自动更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
	}

	private void ensureViewState(boolean loaddata, boolean feedbackIfNoNet) {
		if (App.showLoadingOrNoNet(getAdapter().getCount() == 0, loaddata, feedbackIfNoNet, mImgState)) {
			loadData(true, 1);
		}
	}

	private final NetObserver mNetObserver = new NetObserver() {
		@Override
		public void onChanged(Type type, State state) {
			ensureViewState(true, false);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		NetConnectionReceiver.registerObserver(mNetObserver);
		if (mShareLinkViewHolder != null) mShareLinkViewHolder.onResume();
	}

	@Override
	protected void onPause() {
		if (mShareLinkViewHolder != null) mShareLinkViewHolder.onPause();
		NetConnectionReceiver.unregisterObserver(mNetObserver);
		super.onPause();
	}

	private long mBackPressedTime = 0;
	@Override
	public void onBackPressed() {
		if (mShareLinkViewHolder != null) {
			mShareLinkViewHolder.destroy();
			mShareLinkViewHolder = null;
			return;
		}
		if (System.currentTimeMillis() - mBackPressedTime > 2500) {
			Toast.makeText(this, R.string.m_category_v_1_back_click_1th_more_to_exit, Toast.LENGTH_SHORT).show();
			mBackPressedTime = System.currentTimeMillis();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected MainCategoryListAdapter newAdapter() {
		return new MainCategoryListAdapter(this);
	}

	@Override
	public void onRefresh() {
		if (App.checkNetStateAndFeedbackUser()) {
			loadData(true, 1);
		} else {
			updateLoadingState(true);
		}
	}

	@Override
	public void onLoad() {	//包括在没有更多页的时候，点击强制刷新
		int page = mPage + 1;
		boolean loaded = loadFromCache(page);
		if (loaded) {
			if (Network.isNetConnected(this)) loadData(false, page);
		} else {
			if (App.checkNetStateAndFeedbackUser()) {
				loadData(false, page);
			} else {
				updateLoadingState(false);
			}
		}
	}

	private final OnClickListener mOnImgStateClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ensureViewState(true, true);
		}
	};

	private final OnClickListener mOnBtnFavoriteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FavoriteActy.startMe(CategoryActy_v_1.this);
		}
	};

	private final OnClickListener mOnBtnSettingClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SettingsActy.startMe(CategoryActy_v_1.this);
		}
	};

	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			L.i(CategoryActy_v_1.class, "onItemClick-------------position:" + position);
			//headerView会被算上一个
			MainCategoryListBean data = getAdapter().getItem(position - getListView().getHeaderViewsCount());
			switch (data.lockShareType) {
			case 1:
				//打开分享列表面板
				mShareLinkViewHolder = ShareLinkViewHolder.ensureLinkShared(CategoryActy_v_1.this, (ViewGroup)mContentView.getParent(), mOnCompleteClick);
				break;
			case 2:
				//跳转到App评论页面
				App.openAppMarketsAtMyDetails(CategoryActy_v_1.this, OPEN_MARKET_REQ_CODE);
				//MarketCommentActy.startMe(CategoryActy_v_1.this);
				break;
			default:
				EditActy.startMe(CategoryActy_v_1.this, data.id);
				break;
			}
		}
	};

	private final OnClickListener mOnCompleteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mShareLinkViewHolder = null;
			updateListData();
		}
	};

	private static final int OPEN_MARKET_REQ_CODE	= 0x8E;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == OPEN_MARKET_REQ_CODE) {
			MarketCommentActy.setMarketCommented(this);
			updateListData();
		}
		/*if (MarketCommentActy.isResultBelongsToMeAndOK(requestCode, resultCode, data)) {
			updateListData();
		}*/
	}

	private void updateListData() {
		updateListData(getAdapter().getData());
	}

	@Override
	public void updateListData(List<MainCategoryListBean> data) {
		ensureData(data);
		super.updateListData(data);
	}

	private void ensureData(List<MainCategoryListBean> data) {
		if (data == null) return;
		for (MainCategoryListBean bean : data) {
			switch (bean.lockShareType) {
			case 1:
				if (ShareLinkViewHolder.isLinkShared(CategoryActy_v_1.this)) bean.lockShareType = 0;
				break;
			case 2:
				if (MarketCommentActy.isMarketCommented(this)) bean.lockShareType = 0;
				break;
			}
		}
	}

	private void loadData(boolean refresh, int page) {
		try {
			AjaxParams params = new AjaxParams();
			params.put("page", String.valueOf(refresh ? 1 : page));
			App.getFHttp().get(Const.Url.getMainCategory(),
					App.getHeadersWithUidToken(),
					params, new MyAjaxCallBack(refresh));
		} catch (TokenNotValidException e) {
			L.e(CategoryActy_v_1.class, e);
		}
	}

	private class MyAjaxCallBack extends AjaxCallBack<String> {
		private boolean mRefresh;
		public MyAjaxCallBack(boolean refresh) {
			mRefresh = refresh;
		}

		@Override
		public void onSuccess(String json) {
			L.d(CategoryActy_v_1.class, json);

			if (!App.checkApiJsonError(CategoryActy_v_1.class, json)) {
				parseJson(json, true);
			}
			updateLoadingState(mRefresh);
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			App.handleApiWorkFailureAndFeedbackUser(CategoryActy_v_1.class, ApiType.DEF, t, errorNo, strMsg);
			updateLoadingState(mRefresh);
		}
	}

	private void updateLoadingState(boolean refresh) {
		if (refresh) {
			getListView().setRefreshComplete(mHasNextPage);
		} else {
			getListView().setLoadComplete(mHasNextPage);
		}
		ensureViewState(false, false);
	}

	private void parseJson(String json, boolean save) {
		MainCategory source = new MainCategory().fromJson(json);
		if (source.info == null || source.info.classes == null) {	//没有更多页，但是尝试加载更多页时，会为null
			mHasNextPage = false;
			return;
		}
		if (source.info.page_no == 1 || source.info.page_no > mPage) {
			mPage = source.info.page_no;
			mHasNextPage = source.info.has_next_page == 1;
		}
		if (save) saveAsCache(mPage, json);

		Set<MainCategoryListBean> dataSet = new LinkedHashSet<MainCategoryListBean>();
		//刷新第一页的数据，直接将其他页数据删除
		if (mPage > 1) dataSet.addAll(getAdapter().getData());
		for (MainCategory.Clases clazs : source.info.classes) {
			dataSet.add(new MainCategoryListBean(clazs));
		}
		List<MainCategoryListBean> listData = new ArrayList<MainCategoryListBean>();
		listData.addAll(dataSet);
		updateListData(listData);
	}

	private static final String sCacheKey	= "data_cache_json";
	private boolean loadFromCache(int page) {
		String json = SPref.getFromFile(this, this, sCacheKey + "_page_" + page);
		if (json == null) return false;
		parseJson(json, false);
		updateLoadingState(page == 1);
		return true;
	}

	private void saveAsCache(int page, String json) {
		SPref.saveAsFile(this, this, sCacheKey + "_page_" + page, json);
	}
}
