package com.star.talk.startalk.fragment.v2.lefttab;

import android.os.Bundle;
import android.widget.Toast;

import com.star.talk.startalk.Const;
import com.star.talk.startalk.R;
import com.wei.c.L;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsFragment;
import com.wei.c.framework.EventDelegater.EventReceiver;
import com.wei.c.framework.EventDelegater.PeriodMode;

@ViewLayoutId(R.layout.f_f_m_m_tabs_left_btn_page_favorite)
public class FavoriteFrgmt extends AbsFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hostingLocalEventReceiver(Const.EventName.SCROLL_2_TOP_AND_REFRESH_frgmt_favorite, PeriodMode.PAUSE_RESUME, new EventReceiver() {
			@Override
			public void onEvent(Bundle data) {
				scroll2TopAndRefresh();
			}
		});
	}

	public void scroll2TopAndRefresh() {
		L.i(FavoriteFrgmt.class, "FavoriteFrgmt-scroll2TopAndRefresh");
		//TODO 这里需要处理多次连续调用的情况
		Toast.makeText(getActivity(), "FavoriteFrgmt-scroll2TopAndRefresh", Toast.LENGTH_SHORT).show();
	}
}
