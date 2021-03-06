package com.hzease.tomeet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hzease.tomeet.data.LoginBean;
import com.hzease.tomeet.data.NoDataBean;
import com.hzease.tomeet.data.UserInfoBean;
import com.hzease.tomeet.data.WaitEvaluateV2Bean;
import com.orhanobut.logger.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import rx.Subscriber;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.hzease.tomeet", appContext.getPackageName());
    }

    long phone = 88800000001L;
    String pwd = "123456";
    double latitude = 30.317106;
    double longitude = 120.076083;
    String roomId = "1000000000640";

    @Test
    public void check() throws Exception {
        for (int i = 0; i < 200; i++) {
            final String newPhone = phone + i + "";
            PTApplication.getRequestService().login(newPhone, pwd).subscribe(new Subscriber<LoginBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Logger.e(e.getMessage());
                }

                @Override
                public void onNext(LoginBean loginBean) {
                    if (loginBean.isSuccess()) {
                        PTApplication.getRequestService().roomCheck(latitude, longitude, Long.valueOf(roomId), loginBean.getData().getToken(), Long.valueOf(loginBean.getData().getId()))
                                .subscribe(new Subscriber<NoDataBean>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Logger.e(e.getMessage());
                                    }

                                    @Override
                                    public void onNext(NoDataBean noDataBean) {
                                        if (noDataBean.isSuccess()) {
                                            Logger.i(noDataBean.toString());
                                        } else {
                                            Logger.e(noDataBean.toString());
                                        }
                                    }
                                });
                    } else {
                        Logger.e(loginBean.toString());
                    }
                }
            });
        }
    }


    @Test
    public void regAndInit() throws Exception {
        long phone = 88800000020L;
        String sms = "888888";
        final String pwd = "123456";
        for (int i = 0; i <= 180; i++) {
            final String newPhone = phone + i + "";

            PTApplication.getRequestService().login4sms(newPhone, sms)
                    .subscribe(new Subscriber<LoginBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(e.getMessage());
                        }

                        @Override
                        public void onNext(LoginBean loginBean) {
                            if (loginBean.isSuccess()) {
                                if (!loginBean.getData().isIsInit()) {
                                    PTApplication.getRequestService().finishInfo("1918-07-25", true, newPhone, pwd, loginBean.getData().getToken(), loginBean.getData().getId(),"")
                                            .subscribe(new Subscriber<UserInfoBean>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Logger.e(e.getMessage());
                                                }

                                                @Override
                                                public void onNext(UserInfoBean userInfoBean) {
                                                    if (userInfoBean.isSuccess()) {
                                                        Logger.i(userInfoBean.toString());
                                                    } else {
                                                        Logger.e(userInfoBean.toString());
                                                    }
                                                }
                                            });
                                } else {
                                    Logger.e(loginBean.toString());
                                }
                            } else {
                                Logger.e(loginBean.toString());
                            }
                        }
                    });
        }
    }

    @Test
    public void toEvaluations() throws Exception {
        long phone = 88800000001L;
        String pwd = "123456";
        final long roomId = 1000000000518L;

        for (int i = 0; i < 19; i++) {
            final long newPhone = phone + i;

            PTApplication.getRequestService().login(String.valueOf(newPhone), pwd).subscribe(new Subscriber<LoginBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Logger.e(e.getMessage());
                }

                @Override
                public void onNext(final LoginBean loginBean) {
                    if (loginBean.isSuccess()) {
                        PTApplication.getRequestService().toBeEvaluationFriendsV2(roomId, loginBean.getData().getToken(), loginBean.getData().getId())
                                .subscribe(new Subscriber<WaitEvaluateV2Bean>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Logger.e(e.getMessage());
                                    }

                                    @Override
                                    public void onNext(WaitEvaluateV2Bean waitEvaluateV2Bean) {
                                        if (waitEvaluateV2Bean.isSuccess()) {
                                            List<WaitEvaluateV2Bean.DataBean.EvaluationsBean> users = waitEvaluateV2Bean.getData().getEvaluations();
                                            for (WaitEvaluateV2Bean.DataBean.EvaluationsBean user : users) {
                                                Logger.i("user: " + user);
                                                if (user.getFriendId().equals("10000000000")) {
                                                    user.setLabel("JUnit填充");
                                                    user.setRoomEvaluationPoint("10");
                                                    user.setFriendPoint("10");
                                                } else {
                                                    user.setLabel("ExampleInstrumentedTest");
                                                    user.setRoomEvaluationPoint("1");
                                                    user.setFriendPoint("1");
                                                }
                                            }

                                            waitEvaluateV2Bean.getData().setToken(loginBean.getData().getToken());
                                            waitEvaluateV2Bean.getData().setUserId(loginBean.getData().getId());
                                            waitEvaluateV2Bean.getData().setRoomId(roomId + "");

                                            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                                            String s = gson.toJson(waitEvaluateV2Bean.getData());

                                            Logger.e(s);

                                            PTApplication.getRequestService().evaluateFriendsV2(s)
                                                    .subscribe(new Subscriber<NoDataBean>() {
                                                        @Override
                                                        public void onCompleted() {

                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            Logger.e(e.getMessage());
                                                        }

                                                        @Override
                                                        public void onNext(NoDataBean noDataBean) {
                                                            Logger.e(noDataBean.toString());
                                                        }
                                                    });
                                        } else {
                                            Logger.e(waitEvaluateV2Bean.toString());
                                        }
                                    }
                                });
                    } else {
                        Logger.e(loginBean.toString());
                    }
                }
            });
        }
    }
}
