package com.star.talk.startalk.fragment.maintabs;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.star.talk.startalk.Const;
import com.star.talk.startalk.EditActy;
import com.star.talk.startalk.R;
import com.star.talk.startalk.UID;
import com.star.talk.startalk.adapter.EditSourceListAdapter;
import com.star.talk.startalk.data.EditSourceListBean;
import com.star.talk.startalk.data.api.EditSourceCategory;
import com.tisumoon.exception.TokenNotValidException;
import com.wei.c.L;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsListViewFragment;
import com.wei.c.framework.ViewHolder;

@ViewLayoutId(R.layout.f_m_tabs_edit_source)
public class EditSourceFragment extends AbsListViewFragment<ListView, EditSourceListBean, EditSourceListAdapter> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		getListView().setOnItemClickListener(mOnItemClickListener);
		return getCreatedView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	@Override
	protected int listViewId() {
		return R.id.f_m_tabs_edit_source_list;
	}

	@Override
	protected EditSourceListAdapter newAdapter() {
		return new EditSourceListAdapter(getActivity());
	}

	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			EditSourceListBean data = ((ViewHolder<EditSourceListBean, ?>)view.getTag()).getData();
			EditActy.startMe(getActivity(), data.id);
		}
	};

	private void loadData() {
		/*try {
			getFHttp().get(Const.Url.editSourceCategory, new Header[]{new BasicHeader("token", UID.nextToken(getActivity()))},
					new AjaxParams(), new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String json) {
					EditSourceCategory source = new EditSourceCategory().fromJson(json);
					List<EditSourceListBean> data = new ArrayList<EditSourceListBean>();
					for (EditSourceCategory.Clases clases : source.info.classes) {
						data.add(new EditSourceListBean(clases));
						//TODO
						data.add(new EditSourceListBean(clases));
						data.add(new EditSourceListBean(clases));
						data.add(new EditSourceListBean(clases));
						data.add(new EditSourceListBean(clases));
						data.add(new EditSourceListBean(clases));
						data.add(new EditSourceListBean(clases));
					}
					updateListData(data);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					L.e(EditSourceFragment.class, "errorNo:" + errorNo + ", strMsg:" + strMsg, t);
					Toast.makeText(getActivity(), "加载失败，请重试", Toast.LENGTH_LONG).show();
				}
			});
		} catch (TokenNotValidException e) {
			L.e(EditSourceFragment.class, e);
		}*/
	}

	private FinalHttp mFHttp;
	private FinalHttp getFHttp() {
		if (mFHttp == null) mFHttp = new FinalHttp();
		return mFHttp;
	}
}
