package com.hzease.tomeet.chat.ui;

import android.os.Bundle;

import com.hzease.tomeet.BaseFragment;
import com.hzease.tomeet.R;
import com.hzease.tomeet.utils.ActivityUtils;

import butterknife.OnClick;

/**
 * Created by Key on 2017/3/24 17:10
 * email: MrKey.K@gmail.com
 * description:
 */

public class AddFriendFragment extends BaseFragment {

    public AddFriendFragment() {
    }

    public static AddFriendFragment newInstance() {
        return new AddFriendFragment();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_add_friend;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @OnClick(R.id.all_add_friend_fmt)
    public void onViewClicked() {
        // read_contacts
        ActivityUtils.replaceFragmentToActivity(getFragmentManager(), AddPhoneContactsFragment.newInstance(), R.id.fl_content_add_friend_activity);
    }
}
