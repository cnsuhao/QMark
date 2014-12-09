package com.star.talk.startalk.fragment.maintabs;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.star.talk.startalk.R;
import com.star.talk.startalk.adapter.MineListAdapter;
import com.star.talk.startalk.data.MagicBoardBean;
import com.star.talk.startalk.data.MineListBean;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsListViewFragment;

@ViewLayoutId(R.layout.f_m_tabs_mine)
public class MineFragment extends AbsListViewFragment<ListView, MineListBean, MineListAdapter> {
	@ViewId(R.id.f_m_tabs_mine_title_right_btn_setting)
	private ImageButton mBtnSetting;

	private MineListBean mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getData();
	}

	@Override
	protected int listViewId() {
		return R.id.f_m_tabs_mine_list;
	}

	@Override
	protected MineListAdapter newAdapter() {
		return new MineListAdapter(getActivity(), mOnImgHeadClick, mOnImgBlurClick, mOnFriendsClick,
				mOnMoreClick, mOnCloseClick, mOnResendClick, mOnDeleteClick);
	}

	private final OnClickListener mOnImgHeadClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private final OnClickListener mOnImgBlurClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private final OnClickListener mOnFriendsClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private final OnClickListener mOnMoreClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private final OnClickListener mOnCloseClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private final OnClickListener mOnResendClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private final OnClickListener mOnDeleteClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//TODO
		}
	};

	private void getData() {
		if (mUser == null) {
			mUser = new MineListBean();
			mUser.user = new MineListBean.User();
			mUser.user.headUrl = "http://2461147497.oss-cn-qingdao.aliyuncs.com/QQ%E5%9B%BE%E7%89%8720141023114314.jpg";
			mUser.user.ID = 404608861;
			mUser.user.newFriendsCount = 56;
			mUser.user.nicename = "王大锤";
			mUser.user.starSign = "射手座";
		}
		List<MineListBean> data = new ArrayList<MineListBean>();
		data.add(mUser);
		for (int i = 0; i < 20; i++) {
			MineListBean history = new MineListBean();
			history.history = new MineListBean.History();
			history.history.commentCount = 235;
			history.history.favoriteCount = 57;
			history.history.location = "上海";
			history.history.time = System.currentTimeMillis() - 1000000000;
			history.history.magicBoard = new MagicBoardBean();
			history.history.magicBoard.imgUrl = i % 2 == 0 ? "http://2461147497.oss-cn-qingdao.aliyuncs.com/QQ%E5%9B%BE%E7%89%8720141023114314.jpg" : "http://app-img-path.oss-cn-qingdao.aliyuncs.com/100109_3462eb93a42832ce226000f4698bf5ed.jpeg";
			history.history.magicBoard.textColor = "#ff0000";
			history.history.magicBoard.textFontCode = 1;
			history.history.magicBoard.textDefault = "我叫王大锤" + i;
			history.history.magicBoard.left = 100 + 2*i;
			history.history.magicBoard.top = 80 + i;
			history.history.magicBoard.width = 300 - i;
			history.history.magicBoard.height = 100;
			data.add(history);
		}

		updateListData(data);

		/*try {
			new FinalHttp().get(Const.Url.mineHistory, new Header[]{new BasicHeader("token", UID.nextToken(getActivity()))},
					new AjaxParams(), new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					UserSignInHistory history = new UserSignInHistory().fromJson(json);
					history.info.
					List<MineListBean> data = new ArrayList<MineListBean>();
					data.add(mUser);
					for () {
						
					}

					updateListData(data);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					L.e(MineFragment.class, "errorNo:" + errorNo + ", strMsg:" + strMsg, t);
					Toast.makeText(getActivity(), "加载失败，请重试", Toast.LENGTH_LONG).show();
				}
			});
		} catch (TokenNotValidException e) {
			L.e(MineFragment.class, e);
		}*/
	}
}
