package com.hzease.tomeet.circle.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hzease.tomeet.AppConstants;
import com.hzease.tomeet.BaseFragment;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.R;
import com.hzease.tomeet.circle.ICircleContract;
import com.hzease.tomeet.circle.ui.CircleActivity;
import com.hzease.tomeet.circle.ui.MemberListActivity;
import com.hzease.tomeet.data.CircleInfoBean;
import com.hzease.tomeet.data.CommentItemBean;
import com.hzease.tomeet.data.EnterCircleInfoBean;
import com.hzease.tomeet.data.HomeRoomsBean;
import com.hzease.tomeet.data.JoinCircleBean;
import com.hzease.tomeet.home.ui.CreateRoomBeforeActivity;
import com.hzease.tomeet.utils.ImageCropUtils;
import com.hzease.tomeet.utils.OssUtils;
import com.hzease.tomeet.utils.ToastUtils;
import com.hzease.tomeet.utils.Untils4px2dp;
import com.hzease.tomeet.widget.CircleImageView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by xuq on 2017/4/20.
 */

public class CircleInfoFragment extends BaseFragment implements ICircleContract.View {

    //创建fragment事务管理器对象
    FragmentTransaction transaction;
    CircleActivity mCircleActivity;

    @BindView(R.id.ll_circle_circleannouncement)
    AutoLinearLayout ll_circle_circleannouncement;
    @BindView(R.id.civ_circleinfo_circleicon_fmt)
    CircleImageView civ_circleinfo_circleicon_fmt;
    @BindView(R.id.tv_circleinfo_name_fmt)
    TextView tv_circleinfo_name_fmt;
    @BindView(R.id.tv_circleinfo_member_fmt)
    TextView tv_circleinfo_member_fmt;
    @BindView(R.id.tv_circleinfo_ownerName_fmt)
    TextView tv_circleinfo_ownerName_fmt;
    @BindView(R.id.tv_circleinfo_notice_fmt)
    TextView tv_circleinfo_notice_fmt;
    @BindView(R.id.bt_circleinfo_joincircle_fmt)
    Button bt_circleinfo_joincircle_fmt;
    @BindView(R.id.bt_circleinfo_createcircleroom_fmt)
    Button bt_circleinfo_createcircleroom_fmt;
    @BindView(R.id.bt_circleinfo_joinchat_fmt)
    Button bt_circleinfo_joinchat_fmt;
    @BindView(R.id.tv_circleinfo_distance_fmt)
    TextView tv_circleinfo_distance_fmt;
    @BindView(R.id.all_circleinfo_buttongroup_fmt)
    AutoLinearLayout all_circleinfo_buttongroup_fmt;
    private PopupWindow popupWindow;
    @BindView(R.id.viewPager_tab)
    ViewPager viewPagerTab;
    @BindView(R.id.iv_circle_setting)
    ImageView ivCircleSetting;
    @BindView(R.id.iv_circleinfo_finish_fmt)
    ImageView iv_circleinfo_finish_fmt;
    @BindView(R.id.tv_circleinfo_memberlist_item)
    TextView tv_circleinfo_memberlist_item;
    @BindView(R.id.civ_circleinfo_managericon_fmt)
    CircleImageView civ_circleinfo_managericon_fmt;
    @BindView(R.id.iv_circleinfo_bg_fmt)
    ImageView iv_circleinfo_bg_fmt;
    private List<Fragment> list;
    private AutoRelativeLayout rl_circle_head;
    private String[] tabTitles = {"活动", "等级"};
    private EnterCircleInfoBean.DataBean data;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private ICircleContract.Presenter mPresenter;
    /**
     * 创建底部导航栏对象
     */
    BottomNavigationView bottomNavigationView;
    private long circleId;
    private long ownerId;
    private String showNotices;
    private ActivityFragment activityFragment;
    private LevelFragment levelFragment;
    private PopupWindow popupWindowforImage;
    private int type;
    private String circleName;

    @OnClick({
            R.id.iv_circle_setting,
            R.id.ll_circle_circleannouncement,
            R.id.bt_circleinfo_joincircle_fmt,
            R.id.iv_circleinfo_finish_fmt,
            R.id.tv_circleinfo_memberlist_item,
            R.id.bt_circleinfo_createcircleroom_fmt,
            R.id.bt_circleinfo_joinchat_fmt
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_circle_setting:
                initPopupWindow();
                break;
            case R.id.ll_circle_circleannouncement:
                initCircleAnnouncement(view);
                break;
            case R.id.bt_circleinfo_joincircle_fmt:
                mPresenter.joinCircle(circleId,PTApplication.userToken,PTApplication.userId);
                break;
            case R.id.iv_circleinfo_finish_fmt:
                mCircleActivity.getSupportFragmentManager().popBackStack();
                break;
            case R.id.tv_circleinfo_memberlist_item:
                Intent intent = new Intent(mCircleActivity, MemberListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("circleId",circleId);
                bundle.putLong("ownerId",ownerId);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.bt_circleinfo_createcircleroom_fmt:
                Intent createRoomByCircle = new Intent(mCircleActivity, CreateRoomBeforeActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putLong("circleId",circleId);
                bundle1.putBoolean("isOpen",false);
                createRoomByCircle.putExtras(bundle1);
                startActivity(createRoomByCircle);
                break;
            //进入群聊
            case R.id.bt_circleinfo_joinchat_fmt:
                RongIM.getInstance().startGroupChat(mContext, String.valueOf(circleId),circleName);
                break;
        }
    }

    /**
     * 显示圈子公告pop
     */
    private void initCircleAnnouncement(View v) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_circleannouncement, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置PopupWindow以外部分的背景颜色  有一种变暗的效果
        final WindowManager.LayoutParams wlBackground = getActivity().getWindow().getAttributes();
        wlBackground.alpha = 0.5f;      // 0.0 完全不透明,1.0完全透明
        getActivity().getWindow().setAttributes(wlBackground);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // 当PopupWindow消失时,恢复其为原来的颜色
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getActivity().getWindow().setAttributes(wlBackground);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
        Button mMotifify =  contentView.findViewById(R.id.bt_circlenotice_moditity_pop);
        TextView notices =  contentView.findViewById(R.id.tv_circlenoitce_msg_pop);
        notices.setText(showNotices);
        AutoLinearLayout buttongroup =  contentView.findViewById(R.id.all_circlenotice_button_pop);
        if (String.valueOf(ownerId).equals(PTApplication.userId)){
            buttongroup.setVisibility(View.VISIBLE);
        }else{
            buttongroup.setVisibility(View.GONE);
        }
        mMotifify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("notice",showNotices);
                bundle.putLong("circleId",circleId);
                mCircleActivity.mFragmentList.get(3).setArguments(bundle);
                transaction.replace(R.id.fl_content_bidding_activity, mCircleActivity.mFragmentList.get(3));
                // 然后将该事务添加到返回堆栈，以便用户可以向后导航
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        //设置PopupWindow进入和退出动画
        popupWindow.setAnimationStyle(R.style.anim_popup_centerbar);
        // 设置PopupWindow显示在中间
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    @Override
    public void setPresenter(ICircleContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public static CircleInfoFragment newInstance() {
        return new CircleInfoFragment();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_circleinfo;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        circleId = getArguments().getLong("circleId");
        list = new ArrayList<>();
        activityFragment = new ActivityFragment(circleId);
        levelFragment = new LevelFragment(circleId);
        list.add(activityFragment);
        list.add(levelFragment);
        mCircleActivity = (CircleActivity) getActivity();
        rl_circle_head = (AutoRelativeLayout) mCircleActivity.findViewById(R.id.circle_head);
        rl_circle_head.setVisibility(View.GONE);
        bottomNavigationView = (BottomNavigationView) mCircleActivity.findViewById(R.id.navigation_bottom);
        bottomNavigationView.setVisibility(View.GONE);
        transaction = mCircleActivity.getSupportFragmentManager().beginTransaction();
        initTabTitle();
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tabLayout, Untils4px2dp.px2dp(150), Untils4px2dp.px2dp(150));
            }
        });
        viewPagerTab.setAdapter(new FragmentPagerAdapter(this.getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles[position];
            }
        });
        tabLayout.setupWithViewPager(viewPagerTab);
        /**
         * 获取圈子详情
         */
        mPresenter.getCircleInfo(circleId, PTApplication.userToken, PTApplication.userId);
    }

    private void initTabTitle() {
        for (int i = 0; i < tabTitles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitles[i]));
        }
    }


    //修改tablayout下划线的长度
    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    /**
     * 创建圈子成功
     */
    @Override
    public void createSuccess(JoinCircleBean joinCircleBean) {

    }

    /**
     * 展示喊话内容
     *  @param isSuccess
     * @param commentList
     */
    @Override
    public void showDeclaration(boolean isSuccess, List<CommentItemBean.DataBean> commentList, boolean isLoadMore) {

    }


    /**
     * 完成喊话后的展示
     *
     * @param isSuccess
     * @param msg
     */
    @Override
    public void showDeclareSucccess(boolean isSuccess, String msg) {

    }

    @Override
    public void refreshOneDeclaration(CommentItemBean.DataBean dataBean) {

    }

    /**
     * 显示推荐圈子
     *
     * @param data
     */
    @Override
    public void showRecommandCircle(List<CircleInfoBean.DataBean> data) {

    }

    /**
     * 显示附近圈子
     *
     * @param data
     */
    @Override
    public void showNeayByCircle(List<CircleInfoBean.DataBean> data) {

    }

    /**
     * 显示圈子详情
     *
     * @param data
     */
    @Override
    public void showCircleInfo(EnterCircleInfoBean.DataBean data) {
        if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed() || isRemoving() || !isVisible() || !isAdded()) {
            return;
        }
        this.data = data;
        ownerId = data.getCircle().getManager().getId();
        circleName = data.getCircle().getName();
        tv_circleinfo_name_fmt.setText(data.getCircle().getName());
        //圈子背景
        Glide.with(mContext)
                .load(AppConstants.YY_PT_OSS_CIRCLE_PATH + data.getCircle().getId() + AppConstants.YY_PT_OSS_CIRCLE_BG)
                .error(R.drawable.bg_neaybycircle)
                .centerCrop()
                .signature(new StringSignature(data.getCircle().getBgSignature()))
                .into(iv_circleinfo_bg_fmt);
        //圈子头像
        Glide.with(mContext)
                .load(AppConstants.YY_PT_OSS_CIRCLE_PATH + circleId + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .error(R.drawable.circle_defalut_icon)
                .signature(new StringSignature(data.getCircle().getAvatarSignature()))
                .into(civ_circleinfo_circleicon_fmt);
        //管理员头像
        Glide.with(mContext)
                .load(AppConstants.YY_PT_OSS_USER_PATH + data.getCircle().getManager().getId() + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .signature(new StringSignature(data.getCircle().getManager().getAvatarSignature()))
                .into(civ_circleinfo_managericon_fmt);
        String member = "人数";
        member = member + data.getCircle().getMemberCount() + "·" + "活动" + data.getCircle().getRoomCount();
        tv_circleinfo_member_fmt.setText(member);
        LatLng latLng1 = new LatLng(PTApplication.myLatitude, PTApplication.myLongitude);
        LatLng latLng2 = new LatLng(data.getCircle().getLatitude(), data.getCircle().getLongitude());
        float distance = AMapUtils.calculateLineDistance(latLng1, latLng2) / 1000;
        String result = String.format("%.2f", distance);
        String dis = "距离你" + result + "km";
        tv_circleinfo_distance_fmt.setText(dis);
        tv_circleinfo_ownerName_fmt.setText(data.getCircle().getManager().getNickname());
        String notices = "【公告】";
        if (data.getCircle().getNotice() == ""){
            notices = notices + "这个圈子什么都没有，快来开动你智慧的小脑筋吧！";
        }else{
            notices = notices + data.getCircle().getNotice();
        }
        tv_circleinfo_notice_fmt.setText(notices);
        int expreience = data.getExperience();
        switch (expreience){
            case -1:
                bt_circleinfo_joincircle_fmt.setVisibility(View.VISIBLE);
                all_circleinfo_buttongroup_fmt.setVisibility(View.GONE);
                break;
            default:
                bt_circleinfo_joincircle_fmt.setVisibility(View.GONE);
                all_circleinfo_buttongroup_fmt.setVisibility(View.VISIBLE);
                break;
        }
        if (data.getCircle().getNotice() == ""){
            showNotices ="这个圈子什么都没有，快来开动你智慧的小脑筋吧！";
        }else{
            showNotices = data.getCircle().getNotice();
        }

    }

    @Override
    public void joinCircleSuccess(boolean isSuccess,String msg) {
        if (isSuccess){
            ToastUtils.getToast("加入圈子成功");
        }else{
            ToastUtils.getToast(msg);
        }
        all_circleinfo_buttongroup_fmt.setVisibility(View.VISIBLE);
        bt_circleinfo_joincircle_fmt.setVisibility(View.GONE);
    }

    /**
     * 退出圈子成功
     *
     * @param msg
     */
    @Override
    public void signOutCircleSuccess(String msg) {
        popupWindow.dismiss();
        ToastUtils.getToast("退出圈子成功!!!");
        mCircleActivity.getSupportFragmentManager().popBackStack();
    }

    /**
     * 修改圈子公告成功
     *
     * @param msg
     */
    @Override
    public void modifitySuccess(String msg) {

    }

    /**
     * 显示圈内房间
     *
     * @param data
     */
    @Override
    public void showRoomsByCircle(List<HomeRoomsBean.DataBean> data) {

    }

    @Override
    public void showMyCircle(List<CircleInfoBean.DataBean> data) {

    }

    /**
     * 底部弹出popwind
     */
    class popupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    protected void initPopupWindow() {
        View popupWindowView = getActivity().getLayoutInflater().inflate(R.layout.pop_circle, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //显示位置
        popupWindow.showAtLocation(getActivity().getLayoutInflater().inflate(R.layout.activity_login, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.3f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());
        Button signoutCircle = popupWindowView.findViewById(R.id.bt_pop_signout_fmt);
        Button moditityhead = popupWindowView.findViewById(R.id.bt_pop_modititycirclehead_fmt);
        moditityhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindowforImage(1);
                popupWindow.dismiss();
            }
        });
        Button modititybg = popupWindowView.findViewById(R.id.bt_pop_modititycirclebg_fmt);
        modititybg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindowforImage(2);
                popupWindow.dismiss();
            }
        });
        AutoLinearLayout all_pop_modifity_fmt = (AutoLinearLayout) popupWindowView.findViewById(R.id.all_pop_modifity_fmt);
        if (String.valueOf(ownerId).equals(PTApplication.userId)){
            all_pop_modifity_fmt.setVisibility(View.VISIBLE);
        }else{
            all_pop_modifity_fmt.setVisibility(View.GONE);
        }
        signoutCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.signOutCircle(circleId,PTApplication.userToken,PTApplication.userId);
            }
        });
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return true;
            }
        });
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的
    }

    protected void initPopupWindowforImage(int type) {
        this.type = type;
        View popupWindowView = getActivity().getLayoutInflater().inflate(R.layout.pop, null);
        //内容，高度，宽度
        popupWindowforImage = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowforImage.setAnimationStyle(R.style.AnimationBottomFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindowforImage.setBackgroundDrawable(dw);
        //显示位置
        popupWindowforImage.showAtLocation(getActivity().getLayoutInflater().inflate(R.layout.activity_login, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.3f);
        //关闭事件
        popupWindowforImage.setOnDismissListener(new popupDismissListener());
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        Button gallery = popupWindowView.findViewById(R.id.local);
        Button camera = popupWindowView.findViewById(R.id.tokenphoto);
        Button close = popupWindowView.findViewById(R.id.close);
        // 相册选择头像
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, AppConstants.REQUEST_CODE_GALLERY);
                popupWindowforImage.dismiss();
            }
        });
        // 拍照选择头像
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i("权限："+ ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // 只需要相机权限,不需要SD卡读写权限
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, AppConstants.REQUEST_TAKE_PHOTO_PERMISSION);
                } else {
                    takePhotoForAvatar();
                }
                popupWindowforImage.dismiss();
            }
        });
        // 关闭
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowforImage.dismiss();
            }
        });
    }
    public void takePhotoForAvatar() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PTApplication.imageLocalCache);
        if (ImageCropUtils.checkFileExists()) {
            startActivityForResult(intent, AppConstants.REQUEST_CODE_CAMERA);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e("onRequestPermissionsResult:\n" + requestCode + "\n" + Arrays.toString(permissions) + "\n" + Arrays.toString(grantResults));

        switch (requestCode) {
            // 请求相机权限
            case AppConstants.REQUEST_TAKE_PHOTO_PERMISSION:
                if (grantResults[0] == 0) {
                    Logger.i("相机权限申请成功");
                    takePhotoForAvatar();
                } else {
                    ToastUtils.getToast("相机权限被禁止,无法打开照相机");
                }
                break;
            // 请求SD卡写入权限,一般不可能会弹出来,以防万一
            case AppConstants.REQUEST_SD_WRITE_PERMISSION:
                if (grantResults[0] == 0) {
                    Logger.i("SD权限申请成功");
                } else {
                    ToastUtils.getToast("没有读写SD卡的权限");
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 用户没有进行有效的设置操作，返回
        if (resultCode == Activity.RESULT_CANCELED) {//取消
            ToastUtils.getToast("取消上传头像");
            return;
        }
        Intent resultIntent = null;
        switch (requestCode) {
            //如果是来自相册,直接裁剪图片
            case AppConstants.REQUEST_CODE_GALLERY:
                resultIntent = ImageCropUtils.cropImage(data.getData());
                break;
            case AppConstants.REQUEST_CODE_CAMERA:
                resultIntent = ImageCropUtils.cropImage(PTApplication.imageLocalCache);
                break;
            case AppConstants.REQUEST_CODE_CROP:
                //设置图片框并上传
                switch (type){
                    case 1:
                        new OssUtils().setCircleImageToView(AppConstants.YY_PT_OSS_CIRCLE_AVATAR,String.valueOf(circleId),"");
                        break;
                    case 2:
                        new OssUtils().setCircleImageToView(AppConstants.YY_PT_OSS_CIRCLE_BG,String.valueOf(circleId),"");
                        break;
                }

                break;
        }
        if (requestCode == AppConstants.REQUEST_CODE_GALLERY || requestCode == AppConstants.REQUEST_CODE_CAMERA) {
            if (resultIntent != null) {
                // 只有Intent正确回来的时候才会进来,所有的判断都在创建Intent的时候做
                Logger.d(resultIntent);
                startActivityForResult(resultIntent, AppConstants.REQUEST_CODE_CROP);
            } else {
                // 创建不了,大部分可能是因为没权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 只需要相机权限,不需要SD卡读写权限
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstants.REQUEST_SD_WRITE_PERMISSION);
                }
            }
        }
    }
}