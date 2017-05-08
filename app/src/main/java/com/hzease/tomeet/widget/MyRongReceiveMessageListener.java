package com.hzease.tomeet.widget;

import com.hzease.tomeet.data.RealmFriendBean;
import com.hzease.tomeet.utils.RongCloudInitUtils;
import com.orhanobut.logger.Logger;

import io.realm.Realm;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by Key on 2017/3/27 16:56
 * email: MrKey.K@gmail.com
 * description:
 */

public class MyRongReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    @Override
    public boolean onReceived(final Message message, int left) {
        Logger.e("融云消息接收监听   MessageContentEncode: " + new String(message.getContent().encode())
                + "   getTargetId: " + message.getTargetId() + "   Left: " + left
                + "   ObjectName: " + message.getObjectName() + "\nConversationType: " + message.getConversationType().getName() + "  getValue: " + message.getConversationType().getValue() + "  name: " + message.getConversationType().name() + "  toString : "
                + message.getConversationType().toString() + "\n " + Conversation.ConversationType.PRIVATE.getName());

        boolean dispose = true;
        switch(message.getConversationType().getName()) {
            // 群组
            case "group":

                dispose = false;
                break;
            // 聊天室，这边不处理，直接用eventbus处理
            /*case "chatroom":

                break;*/
            // 系统命令消息
            case "system":
                switch(new String(message.getContent().encode())) {
                    case "{\"name\":\"flushFriends\"}":
                        Logger.w("RC:CmdMsg: " + new String(message.getContent().encode()));
                        RongCloudInitUtils.reflushFriends();
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
                dispose = false;
            break;
        }
        return dispose;
    }
}
