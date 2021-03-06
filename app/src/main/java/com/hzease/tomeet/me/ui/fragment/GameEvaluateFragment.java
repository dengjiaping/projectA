package com.hzease.tomeet.me.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hzease.tomeet.BaseFragment;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.R;
import com.hzease.tomeet.data.EvaluteBean;
import com.hzease.tomeet.data.GameFinishBean;
import com.hzease.tomeet.data.MyJoinRoomsBean;
import com.hzease.tomeet.data.NoDataBean;
import com.hzease.tomeet.data.PropsMumBean;
import com.hzease.tomeet.data.WaitEvaluateBean;
import com.hzease.tomeet.data.WaitEvaluateV2Bean;
import com.hzease.tomeet.me.IMeContract;
import com.hzease.tomeet.utils.ToastUtils;
import com.hzease.tomeet.widget.SpacesItemDecoration;
import com.hzease.tomeet.widget.adapters.WaitEvaluateAdapter;
import com.hzease.tomeet.widget.adapters.WaitEvaluateAdapterV2;
import com.hzease.tomeet.widget.adapters.WaitEvaluateAdapterV3;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by xuq on 2017/5/11.
 */

public class GameEvaluateFragment extends BaseFragment implements IMeContract.View {


    @BindView(R.id.rv_gameevaluate_show_fmt)
    RecyclerView rv_gameevaluate_show_fmt;
    @BindView(R.id.bt_evaluate_submit_fmt)
    Button bt_evaluate_submit_fmt;
    private IMeContract.Presenter mPresenter;
    /**
     * 创建底部导航栏对象
     */
    BottomNavigationView bottomNavigationView;
    private WaitEvaluateAdapterV3 adapter;
    private long roomId;
    private WaitEvaluateV2Bean.DataBean newDatas;

    public static GameEvaluateFragment newInstance() {
        return new GameEvaluateFragment();
    }

    @OnClick({
            R.id.bt_evaluate_submit_fmt,
            R.id.iv_gameevaluate_question_fmt
    })
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bt_evaluate_submit_fmt:
                newDatas.setToken(PTApplication.userToken);
                newDatas.setUserId(PTApplication.userId);
                newDatas.setRoomId(String.valueOf(roomId));
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String s = gson.toJson(newDatas);
                Logger.e(s);
                PTApplication.getRequestService().evaluateFriendsV2(s)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                                Logger.e("noDatabean" + noDataBean.toString());
                                if (noDataBean.isSuccess()){
                                    ToastUtils.getToast("提交成功");
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }else {
                                    ToastUtils.getToast(noDataBean.getMsg());
                                }
                            }
                        });
                break;
            case R.id.iv_gameevaluate_question_fmt:
                break;
        }
    }
    @Override
    public void setPresenter(IMeContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showMyInfo() {

    }

    @Override
    public void showMyRooms(boolean isSuccess,MyJoinRoomsBean myJoinRoomBean, boolean isLoadMore) {

    }

    @Override
    public void updatePwdSuccess(boolean isSuccess, String msg) {

    }

    @Override
    public void feedBackSuccess(boolean isSuccess, String msg) {

    }

    @Override
    public void authorizedSuccess() {

    }

    @Override
    public void showFinishInfo(GameFinishBean.DataBean data) {

    }
    /**
     * 显示待评价成员
     * @param data
     */
    @Override
    public void showWaitEvaluateMember(WaitEvaluateV2Bean data) {
        for (int i = 0; i < data.getData().getEvaluations().size(); i++) {
            if (data.getData().getEvaluations().get(i).getFriendPoint().equals("0")){
                data.getData().getEvaluations().get(i).setIsfriend(false);
            }else{
                data.getData().getEvaluations().get(i).setIsfriend(true);
            }
        }
        Logger.e(data.toString());
        newDatas = data.getData();
        adapter = new WaitEvaluateAdapterV3(mContext,newDatas.getEvaluations());

        rv_gameevaluate_show_fmt.setAdapter(adapter);
    }



    /**
     * 显示道具数量
     *
     * @param data
     */
    @Override
    public void showPropsMum(PropsMumBean.DataBean data) {

    }

    /**
     * 修改昵称成功
     */
    @Override
    public void showChangeNameSuccess(NoDataBean noDataBean) {

    }

    /**
     * 显示购买道具结果
     *
     * @param success
     * @param msg
     */
    @Override
    public void showBuyPropsResult(int index,boolean success, String msg) {

    }

    @Override
    public void initResult(NoDataBean noDataBean) {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_evaluate;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        roomId = getArguments().getLong("roomId");
        rv_gameevaluate_show_fmt.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_gameevaluate_show_fmt.addItemDecoration(new SpacesItemDecoration(20));
        Logger.e("userId" + PTApplication.userId+"\ntoken:"+PTApplication.userToken + "\nroomId" + roomId);
        mPresenter.waitEvaluate(roomId, PTApplication.userToken,PTApplication.userId);
        bottomNavigationView =  getActivity().findViewById(R.id.navigation_bottom);
        bottomNavigationView.setVisibility(View.GONE);
    }
}
