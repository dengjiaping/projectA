package com.hzease.tomeet.game.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.hzease.tomeet.AppConstants;
import com.hzease.tomeet.NetActivity;
import com.hzease.tomeet.R;
import com.hzease.tomeet.data.RealmFriendBean;
import com.hzease.tomeet.utils.ToastUtils;
import com.hzease.tomeet.widget.adapters.SelectFriendAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.Sort;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.RichContentMessage;

/**
 * Created by xuq on 2017/7/12.
 */

public class SelectFriendActivity extends NetActivity {
    @BindView(R.id.bt_click)
    Button bt_click;
    @BindView(R.id.lv_select_friend)
    ListView lv_select_friend;
    @BindView(R.id.cb_all_select)
    CheckBox cb_all_select;
    @BindView(R.id.tv_send)
    TextView tv_send;
    private List<RealmFriendBean> friends;
    private final Realm mRealm = Realm.getDefaultInstance();
    private List<Long> userIds = new ArrayList<>();
    private String roomId;
    private String roomName;
    private String desc;
    private int gameId;

    @OnClick(R.id.bt_click)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_click:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int[] toastInt = {0, 0};
                        for (int i = 0; i < userIds.size(); i++) {
                            RichContentMessage richContentMessage = RichContentMessage.obtain("你的小伙伴喊你参加【" + roomName + "】啦!", desc, AppConstants.TOMMET_SHARE_GAME + gameId + ".png");
                            richContentMessage.setExtra("tomeet://www.hzease.com?action=invited&key1=" + roomId);
                            Message myMessage = Message.obtain(String.valueOf(userIds.get(i)), Conversation.ConversationType.PRIVATE, richContentMessage);
                            RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                                @Override
                                public void onAttached(Message message) {
                                    //Logger.e("toastInt  onAttached: 分享给应用内好友");
                                }

                                @Override
                                public void onSuccess(Message message) {
                                    toastInt[0] += 1;
                                    Logger.e("toastInt[0]：" + toastInt[0]);
                                }

                                @Override
                                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                    toastInt[1] += 1;
                                    Logger.e("toastInt[1]：" + toastInt[1] + "richContentMessage - error: " + errorCode.getValue() + "  " + errorCode.getMessage());
                                }
                            });
                            SystemClock.sleep(180);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String toastStr;
                                if (toastInt[0] + toastInt[1] == 1) {
                                    if (toastInt[0] == 1) {
                                        toastStr = "分享成功";
                                    } else {
                                        toastStr = "分享失败";
                                    }
                                } else {
                                    toastStr = toastInt[0] + "位分享成功\n" + toastInt[1] + "位分享失败";
                                }
                                ToastUtils.getToast(toastStr);
                            }
                        });
                    }
                }).start();
                finish();
                break;

        }
    }

    @Override
    protected void netInit(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_select_friend;
    }

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");
        desc = intent.getStringExtra("desc");
        gameId = intent.getIntExtra("gameId", 5);
        friends = mRealm.copyFromRealm(mRealm.where(RealmFriendBean.class).between("point", 5, 10).findAllSorted("point", Sort.DESCENDING));
        final SelectFriendAdapter adapter = new SelectFriendAdapter(this);
        lv_select_friend.setAdapter(adapter);
        lv_select_friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (friends.get(position).isChoose()) {
                    friends.get(position).setChoose(false);
                    userIds.remove(friends.get(position).getId());
                } else {
                    friends.get(position).setChoose(true);
                    userIds.add(friends.get(position).getId());
                }
                adapter.setData(friends);
                if (userIds.size() == friends.size()) {
                    cb_all_select.setChecked(true);
                    cb_all_select.setTextColor(Color.rgb(3, 181, 227));
                } else {
                    cb_all_select.setChecked(false);
                    cb_all_select.setTextColor(Color.rgb(221, 221, 221));
                }
                if (userIds.size() != 0) {
                    bt_click.setEnabled(true);
                    tv_send.setTextColor(Color.rgb(3, 181, 227));
                } else {
                    bt_click.setEnabled(false);
                    tv_send.setTextColor(Color.rgb(221, 221, 221));
                }

            }
        });
        //全选
        cb_all_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < friends.size(); i++) {
                        if (!friends.get(i).isChoose()) {
                            friends.get(i).setChoose(true);
                            userIds.add(friends.get(i).getId());
                            adapter.setData(friends);
                        }
                    }
                    bt_click.setEnabled(true);
                    cb_all_select.setTextColor(Color.rgb(3, 181, 227));
                    tv_send.setTextColor(Color.rgb(3, 181, 227));
                } else {
                    for (int i = 0; i < friends.size(); i++) {
                        friends.get(i).setChoose(false);
                        userIds.clear();
                        adapter.setData(friends);
                        bt_click.setEnabled(false);
                    }
                    bt_click.setEnabled(false);
                    cb_all_select.setTextColor(Color.rgb(221, 221, 221));
                    tv_send.setTextColor(Color.rgb(221, 221, 221));
                }
            }
        });
    }

}
