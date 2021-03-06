package com.hzease.tomeet.utils;

import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.hzease.tomeet.AppConstants;
import com.hzease.tomeet.MyExtensionModule;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.data.EventBean;
import com.hzease.tomeet.data.FriendListBean;
import com.hzease.tomeet.data.RealmFriendBean;
import com.hzease.tomeet.data.SimpleGroupInfoBean;
import com.hzease.tomeet.data.SimpleUserInfoBean;
import com.hzease.tomeet.data.UserInfoBean;
import com.hzease.tomeet.widget.MyRongConversationListener;
import com.hzease.tomeet.widget.MyRongReceiveMessageListener;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.realm.Realm;
import io.rong.eventbus.EventBus;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Key on 2017/3/21 12:33
 * email: MrKey.K@gmail.com
 * description: 融云及其他配置文件初始化
 */

public class RongCloudInitUtils {

    private UserInfo myInfo;
    private Map<String, UserInfo> userInfoMap = new HashMap<>();
    private Map<String, Group> groupInfoMap = new HashMap<>();

    public interface LoginCallBack {
        void onNextSuccess();
        void onNextFailed();

        /**
         * 线程结束后
         */
        void doAfterTerminate();

        /**
         * 线程开始前
         */
        void doOnSubscribe();
    }

    /**
     * 登录验证，获取个人信息
     */
    public void loginMust(final LoginCallBack loginCallBack, final String userId_temp, final String userToken_temp) {
        Logger.e("loginMust  " + userId_temp + "    myLoadingStatus " + PTApplication.myLoadingStatus);
        PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_LOADING;
        PTApplication.getRequestService().getMyInfomation(userToken_temp, userId_temp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoBean>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_FAILED;
                        EventBus.getDefault().post(new UserInfoBean());
                        Logger.e(e.getMessage());
                        ToastUtils.getToast("加载用户信息失败，请检查网络");
                    }
                    @Override
                    public void onNext(UserInfoBean userInfoBean) {
                        //Logger.e(userInfoBean.toString());
                        if (userInfoBean.isSuccess()) {
                            // 修改登录状态
                            PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_SUCCEED;
                            // 储存 个人资料
                            PTApplication.myInfomation = userInfoBean;
                            PTApplication.userId = userId_temp;
                            PTApplication.userToken = userToken_temp;
                            myInfo = new UserInfo(userId_temp, userInfoBean.getData().getNickname(), Uri.parse(AppConstants.YY_PT_OSS_PATH + AppConstants.YY_PT_OSS_USER + PTApplication.userId + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL + "#" + userInfoBean.getData().getAvatarSignature()));
                            // 融云 初始化
                            RongCloudInit();
                            new AMapLocUtils().getLonLatAndSendLocation("10910");
                            loginCallBack.onNextSuccess();
                        } else {
                            PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_FAILED;
                            ToastUtils.getToast(userInfoBean.getMsg() + "，请重新登录");
                            // 清除本地记录
                            SharedPreferences sp = SpUtils.getSP(PTApplication.getInstance());
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear().apply();
                            // EventBus发送更新界面消息
                            EventBus.getDefault().post(userInfoBean);
                            Logger.i("get My information 失败：发送EventBus");
                            loginCallBack.onNextFailed();
                        }
                    }
                });
    }

    /**
     * 判断后初始化融云
     */
    public void RongCloudInit() {
        if (!PTApplication.isRongCloudInit && !TextUtils.isEmpty(PTApplication.userId) && !TextUtils.isEmpty(PTApplication.userToken)) {

            // 初始化数据库配置文件
            Realm.setDefaultConfiguration(PTApplication.getRealmConfiguration());
            Logger.w("Realm名字: " + PTApplication.getRealmConfiguration());

            // 极光测试别名
            JPushInterface.setAlias(PTApplication.getInstance(), PTApplication.userId, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Logger.i("极光setAlias：  s: " + s + "  i:  " + i + "  set:  " + set);
                }
            });

            // Rong 接收消息监听 this在主线程
            RongIM.setOnReceiveMessageListener(new MyRongReceiveMessageListener());

            //融云聊天列表监听
            RongIM.setConversationBehaviorListener(new MyRongConversationListener());

            // Rong 发送消息监听(最好还是写在Activity里面,为了更新画面,和注销)
            // RongIM.getInstance().setSendMessageListener(new MyRongSendMessageListener());

            // 融云连接状态监听
            RongIM.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
                @Override
                public void onChanged(ConnectionStatus connectionStatus) {
                    switch (connectionStatus) {
                        // 用户账户在其他设备登录，本机会被踢掉线。
                        case KICKED_OFFLINE_BY_OTHER_CLIENT:
                            PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_FAILED;
                            clearUserInfo("10910008");
                            ToastUtils.getToast("您的帐号已在别地方登录");
                            EventBus.getDefault().post(new EventBean.LoginInvalid());
                            Logger.e("用户账户在其他设备登录，本机会被踢掉线");
                            break;
                        // Token 不正确。
                        case TOKEN_INCORRECT:
                            PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_SUCCEED;
                            clearUserInfo("10910008");
                            ToastUtils.getToast("连接失效，请重新登录");
                            EventBus.getDefault().post(new EventBean.LoginInvalid());
                            Logger.e("Token 不正确。");
                            break;
                        // 网络不可用。
                        case NETWORK_UNAVAILABLE:
                            ToastUtils.getToast("当前网络不可用");
                            Logger.e("当前聊天网络不可用");
                            break;
                        // 连接成功。
                        case CONNECTED:
                            Logger.e("连接成功");
                            //ToastUtils.getToast("聊天服务器重连成功");
                            break;
                        // 断开连接。
                        case DISCONNECTED:
                            ToastUtils.getToast("当前聊天服务器已断开");
                            Logger.e("当前聊天服务器已断开");
                            break;
                        // 服务器异常或无法连接。
                        case SERVER_INVALID:
                            ToastUtils.getToast("服务器异常或无法连接");
                            Logger.e("服务器异常或无法连接");
                            break;
                        // 连接中。
                        case CONNECTING:
                            Logger.e("连接中。");
                            //ToastUtils.getToast("聊天服务器重连中");
                            break;
                    }
                }
            });

            // 建立连接
            RongIM.connect(PTApplication.userToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    Logger.e("RongIM.connect - Token错误");
                    PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_FAILED;
                    clearUserInfo("10910008");
                    EventBus.getDefault().post(new UserInfoBean());
                }

                @Override
                public void onSuccess(String s) {
                    PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_SUCCEED;
                    PTApplication.isRongCloudInit = true;
                    Logger.i("userid: " + s + "   融云是否初始化:  " + PTApplication.isRongCloudInit);
                    EventBus.getDefault().post(new UserInfoBean());
                    reflushFriends();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Logger.e("onError", errorCode.getMessage());
                    PTApplication.myLoadingStatus = AppConstants.YY_PT_LOGIN_FAILED;
                    EventBus.getDefault().post(new UserInfoBean());
                }
            });


            /**
             * 取消 SDK 默认的 ExtensionModule，注册自定义的 ExtensionModule
             * 聊天消息的扩展,为了使用发送位置
             */
            List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
            IExtensionModule defaultModule = null;
            if (moduleList != null) {
                for (IExtensionModule module : moduleList) {
                    if (module instanceof DefaultExtensionModule) {
                        defaultModule = module;
                        break;
                    }
                }
                if (defaultModule != null) {
                    RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                    RongExtensionManager.getInstance().registerExtensionModule(new MyExtensionModule());
                }
            }


            RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                @Override
                public UserInfo getUserInfo(String userId) {
                    return getUserInfoObject(userId);
                }
            }, false);

            /*RongIM.setGroupUserInfoProvider(new RongIM.GroupUserInfoProvider() {
                @Override
                public GroupUserInfo getGroupUserInfo(String groupId, String userId) {
                    Logger.e("groupId: " + groupId + "   userId: " + userId);
                    return new GroupUserInfo(groupId, userId, getUserInfoObject(userId).getName());
                }
            }, true);*/

            RongIM.setGroupInfoProvider(new RongIM.GroupInfoProvider() {
                @Override
                public Group getGroupInfo(String groupId) {
                    Logger.e("调用群信息提供者 groupId: " + groupId);
                    return getGroupInfoObject(groupId);
                }
            }, false);
        }
    }

    /**
     * 连接失效的情况下，清除本地信息
     * @param loginOutCode 登出码
     */
    public static void clearUserInfo(String loginOutCode) {
        new AMapLocUtils().getLonLatAndSendLocation(loginOutCode);
        // 移除Realm 默认配置
        Realm.removeDefaultConfiguration();
        // 注销融云
        if (PTApplication.isRongCloudInit) {
            RongIM.getInstance().logout();
            PTApplication.isRongCloudInit = false;
        }
        PTApplication.userId = "";
        PTApplication.userToken = "";
        // 注销个人信息
        PTApplication.myInfomation = null;
        // 清空本地保存
        SharedPreferences.Editor editor = PTApplication.getInstance().getSharedPreferences(AppConstants.TOMMET_SHARED_PREFERENCE, MODE_PRIVATE).edit();
        editor.putString("userId", PTApplication.userId);
        editor.putString("userToken", PTApplication.userToken);
        editor.apply();
        // 注销阿里云OSS
        PTApplication.aliyunOss = null;
        PTApplication.aliyunOssExpiration = 0;
        // 停止发送友盟用户信息
        MobclickAgent.onProfileSignOff();

        // 移除未读监听
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(PTApplication.unReadMessageObserver);
        PTApplication.badge.setBadgeNumber(0);
    }

    /**
     * 刷新好友列表
     */
    public static void reflushFriends() {
        PTApplication.getRequestService().getFriendList(PTApplication.userId, PTApplication.userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FriendListBean>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("读取好友完毕");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(FriendListBean friendListBean) {
                        Logger.i(friendListBean.getData().toString());
                        if (friendListBean.isSuccess() && friendListBean.getData().size() > 0) {
                            // 如果第一次登录,有好友.无会话消息,先把所有好友插进数据库,再把更新会话信息
                            for (final RealmFriendBean friendBean : friendListBean.getData()) {
                                Realm realm = Realm.getDefaultInstance();
                                try {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            // 增删改
                                            realm.insertOrUpdate(friendBean);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    realm.close();
                                }
                            }

                            RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                                @Override
                                public void onSuccess(List<Conversation> conversations) {
                                    if (conversations != null && conversations.size() > 0) {
                                        for (final Conversation conversation : conversations) {
                                            Realm realm = Realm.getDefaultInstance();
                                            try {
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        // 增删改
                                                        // 有没有可能,有会话,没好友?还是判断下吧
                                                        RealmFriendBean first = realm.where(RealmFriendBean.class).equalTo("id", Long.valueOf(conversation.getTargetId())).findFirst();
                                                        if (first != null) {
                                                            first.setUnreadCount(conversation.getUnreadMessageCount());
                                                            first.setLastMessage(new TextMessage(conversation.getLatestMessage().encode()).getContent());
                                                            first.setLastTime(conversation.getReceivedTime());
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                realm.close();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Logger.e(errorCode.getMessage());
                                }
                            });
                        }
                    }
                });
        // 刷新未读
        RongIM.getInstance().addUnReadMessageCountChangedObserver(PTApplication.unReadMessageObserver, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.SYSTEM);
    }


    private Group getGroupInfoObject(final String groupId) {
        if (!groupInfoMap.containsKey(groupId)) {
            PTApplication.getRequestService().getCircleSampleInfo(groupId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SimpleGroupInfoBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(e.getMessage());
                        }

                        @Override
                        public void onNext(SimpleGroupInfoBean simpleGroupInfoBean) {
                            groupInfoMap.put(groupId, new Group(groupId, simpleGroupInfoBean.getData().getName(), Uri.parse(AppConstants.YY_PT_OSS_CIRCLE_PATH + groupId + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL + "#" + simpleGroupInfoBean.getData().getAvatarSignature())));
                            RongIM.getInstance().refreshGroupInfoCache(groupInfoMap.get(groupId));
                        }
                    });
        }
        return groupInfoMap.get(groupId);
    }

    private UserInfo getUserInfoObject(final String otherId) {
        if (otherId.equals(PTApplication.userId)) {
            return myInfo;
        } else {
            if (!userInfoMap.containsKey(otherId)) {
                long realmOtherId;
                try {
                    realmOtherId = Long.valueOf(otherId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Logger.e(e.getMessage());
                    return null;
                }
                RealmFriendBean friendBean = Realm.getDefaultInstance().where(RealmFriendBean.class).equalTo("id", realmOtherId).findFirst();
                if (friendBean != null) {
                    userInfoMap.put(otherId, new UserInfo(otherId, friendBean.getNickname(), Uri.parse(AppConstants.YY_PT_OSS_USER_PATH + otherId + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL + "#" + friendBean.getAvatarSignature())));
                } else {
                    // 等回调回来后去更新昵称和ID
                    PTApplication.getRequestService().getOtherAvatar(otherId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleUserInfoBean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Logger.e(e.getMessage());
                                }

                                @Override
                                public void onNext(SimpleUserInfoBean simpleUserInfoBean) {
                                    userInfoMap.put(otherId, new UserInfo(otherId, simpleUserInfoBean.getData().getNickname(), Uri.parse(AppConstants.YY_PT_OSS_USER_PATH + otherId + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL + "#" + simpleUserInfoBean.getData().getAvatarSignature())));
                                    Logger.e("里面getOtherAvatar: " + userInfoMap.get(otherId).getName());
                                    RongIM.getInstance().refreshUserInfoCache(userInfoMap.get(otherId));
                                }
                            });
                }
            }

//            Logger.e("返回之前：  昵称： " + userInfoMap.get(otherId).getName() + "\n" + userInfoMap.get(otherId).getPortraitUri());
            return userInfoMap.get(otherId);
        }
    }
}
