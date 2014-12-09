package com.star.talk.startalk.fragment.signinboard;

import android.widget.ListView;

import com.star.talk.startalk.R;
import com.wei.c.anno.ViewId;
import com.wei.c.anno.ViewLayoutId;
import com.wei.c.framework.AbsFragment;

@ViewLayoutId(R.layout.f_f_m_tabs_sign_in_board_neerby)
public class NearByFragment extends AbsFragment {
	@ViewId(R.id.f_f_m_tabs_sign_in_board_neerby_list)
	private ListView mList;
}
