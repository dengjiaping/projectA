package com.hzease.tomeet.widget;

import android.content.Intent;
import android.text.TextUtils;

import com.hzease.tomeet.MyReceiveSmallPaperActivity;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.data.EventBean;
import com.hzease.tomeet.data.RealmFriendBean;
import com.hzease.tomeet.utils.AMapLocUtils;
import com.hzease.tomeet.utils.RongCloudInitUtils;
import com.orhanobut.logger.Logger;

import io.realm.Realm;
import io.rong.eventbus.EventBus;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;
import io.rong.message.TextMessage;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Key on 2017/3/27 16:56
 * email: MrKey.K@gmail.com
 * description:
 */

public class MyRongReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    @Override
    public boolean onReceived(final Message message, int left) {
        Logger.e("融云消息接收监听\nMessageContentEncode: " + new String(message.getContent().encode())
                + "\ngetTargetId: " + message.getTargetId() + "  Left: " + left
                + "   ObjectName: " + message.getObjectName() + "     ConversationType: " + message.getConversationType().getName()
                + "\nSenderID: " + message.getSenderUserId() + "   Received: " + message.getReceivedTime() + "   Sent: " + message.getSentTime());

        // 默认不处理，交给页面自己处理，系统cmd消息由这儿处理
        boolean dispose = false;
        switch (message.getConversationType().getName()) {
            // 群组
            case "group":

                break;
            // 聊天室，这边不处理，直接用eventbus处理
            case "chatroom":
                //如果是cmd消息
                if (message.getObjectName().equals("RC:CmdMsg")) {
                    EventBus.getDefault().post(message);
                }
                break;
            // 系统命令消息
            case "system":
                switch (message.getObjectName()) {
                    case "RC:TxtMsg":
                        Logger.e("系统未读+1");
                        EventBus.getDefault().post(new EventBean.systemUnreadAddOne());
                        break;
                    case "RC:CmdMsg":
                        CommandMessage cmdMsg = new CommandMessage(message.getContent().encode());
                        switch (cmdMsg.getName()) {
                            case "refreshFriends":
                                Logger.w("RC:CmdMsg: " + new String(message.getContent().encode()));
                                RongCloudInitUtils.reflushFriends();
                                break;
                            // 收到小纸条
                            case "receiveScrip":
                                Logger.w("RC:CmdMsg: " + new String(message.getContent().encode()));
                                Intent intent = new Intent(PTApplication.getInstance(), MyReceiveSmallPaperActivity.class);
                                intent.putExtra("json", cmdMsg.getData());
                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                PTApplication.getInstance().startActivity(intent);
                                break;
                            // 发送位置
                            case "sendLocation":
                                Logger.w("RC:CmdMsg: " + new String(message.getContent().encode()));
                                String roomId = cmdMsg.getData();
                                new AMapLocUtils().getLonLatAndSendLocation(TextUtils.isEmpty(roomId) ? "-2" : roomId);
                                break;
                        }
                        break;
                }
                break;
            // 单聊消息
            case "private":
                Realm realm = Realm.getDefaultInstance();
                final RealmFriendBean first = realm.where(RealmFriendBean.class).equalTo("id", Long.valueOf(message.getTargetId())).findFirst();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (first != null) {
                                // 修改最后一次时间
                                first.setLastTime(message.getReceivedTime());
                                String messageContent = "[未能识别消息类型]";
                                switch (message.getObjectName()) {
                                    case "RC:TxtMsg":
                                        // 文字消息TextMessage
                                        messageContent = new TextMessage(message.getContent().encode()).getContent();
                                        break;
                                    case "RC:VcMsg":
                                        // 语音消息VoiceMessage
                                        messageContent = "[语音消息]";
                                        break;
                                    case "RC:ImgMsg":
                                        // 图片消息ImageMessage
                                        messageContent = "[图片消息]";
                                        break;
                                    case "RC:ImgTextMsg":
                                        // 图文消息RichContentMessage
                                        messageContent = "[图文消息]";
                                        break;
                                    case "RC:LBSMsg":
                                        // 位置消息LocationMessage
                                        messageContent = "[位置消息]";
                                        break;
                                    case "RC:StkMsg":
                                        // 表情贴纸消息StickerMessage
                                        messageContent = "[表情贴纸消息]";
                                        break;
                                    case "RC:PSImgTxtMsg":
                                        // 公众服务单图文消息 PublicServiceRichContentMessage
                                        messageContent = "[公众服务单图文消息]";
                                        break;
                                    case "RC:PSMultiImgTxtMsg":
                                        // 公众服务多图文消息 PublicServiceMultiRichContentMessage
                                        messageContent = "[公众服务多图文消息]";
                                        break;
                                }
                                first.setLastMessage(messageContent);
                                // 未读+1
                                first.receiveMessage();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
                break;
        }
        return dispose;
    }
}
