package pro.yueyuan.project_t.circle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pro.yueyuan.project_t.BaseFragment;
import pro.yueyuan.project_t.R;
import pro.yueyuan.project_t.circle.ICircleContract;
import pro.yueyuan.project_t.circle.ui.CircleActivity;
import pro.yueyuan.project_t.circle.ui.SearchCircleActivity;
import pro.yueyuan.project_t.widget.adapters.RecycleViewTestAdapter;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by xuq on 2017/4/18.
 */

public class MyCircleFragment extends BaseFragment implements ICircleContract.View {


    //创建fragment事务管理器对象
    FragmentTransaction transaction;
    CircleActivity mCircleActivity;
    /**
     * 创建底部导航栏对象
     */
    BottomNavigationView bottomNavigationView;
    /**
     * 创建头部布局对象
     */
    AutoRelativeLayout rl_circle_head;
    @BindView(R.id.rv_mycircle_fmt)
    RecyclerView rvMycircleFmt;
    @BindView(R.id.lv_recommendedcircle_fmt)
    RecyclerView lvRecommendedcircleFmt;
    @BindView(R.id.et_circle_search_fmt)
    EditText et_circle_search_fmt;
    //定义一个集合用来接受View
    private List<View> list = new ArrayList<>();

    //recycleview 测试集合
    private LayoutInflater mInflater;
    private List<String> mDatas = new ArrayList<>();
    String[] strings = {"A", "B", "C", "A", "B", "C", "A", "B", "C",};
    @BindView(R.id.cvp_mycircle_fmt)
    ViewPager cvpMycircleFmt;
    private ICircleContract.Presenter mPresenter;


    @OnClick({
            R.id.et_circle_search_fmt
    })
    public void onClick(View v){
        switch (v.getId()){
            case R.id.et_circle_search_fmt:
                startActivity(new Intent(getActivity(), SearchCircleActivity.class));
                break;
        }
    }
    @Override
    public void setPresenter(ICircleContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public static MyCircleFragment newInstance() {
        return new MyCircleFragment();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_circle;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCircleActivity = (CircleActivity) getActivity();
        //设置所在activity的头布局和底部导航栏不可见
        rl_circle_head = (AutoRelativeLayout) mCircleActivity.findViewById(R.id.circle_head);
        if (rl_circle_head.getVisibility() == View.GONE) {
            rl_circle_head.setVisibility(View.VISIBLE);
        }
        bottomNavigationView = (BottomNavigationView) mCircleActivity.findViewById(R.id.navigation_bottom);
        if (bottomNavigationView.getVisibility() == View.GONE) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        transaction = mCircleActivity.getSupportFragmentManager().beginTransaction();
        mInflater = LayoutInflater.from(getContext());
        initViewPagerItem();
        initmDatas();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvMycircleFmt.setLayoutManager(linearLayoutManager);
        lvRecommendedcircleFmt.setLayoutManager(new LinearLayoutManager(getContext()));
        lvRecommendedcircleFmt.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL));
        cvpMycircleFmt.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(list.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(list.get(position), 0);
                return list.get(position);
            }
        });
        rvMycircleFmt.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mInflater.inflate(R.layout.item_circle_recycle, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                viewHolder.mContent = (TextView) view.findViewById(R.id.content);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.mContent.setText(mDatas.get(position));
            }

            @Override
            public int getItemCount() {
                return mDatas.size();
            }
        });


        RecycleViewTestAdapter adapter = new RecycleViewTestAdapter(getContext(),mDatas);
        adapter.setOnItemClickLitener(new RecycleViewTestAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                transaction.replace(R.id.fl_content_bidding_activity, mCircleActivity.mFragmentList.get(2));
                // 然后将该事务添加到返回堆栈，以便用户可以向后导航
                transaction.addToBackStack(null);
                transaction.commit();
                Logger.e("postion  "+position);
            }
        });
        lvRecommendedcircleFmt.setAdapter(adapter);
    }

    private void initmDatas() {
        for (int i = 0; i < strings.length; i++) {
            mDatas.add(strings[i]);
        }
    }

    private void initViewPagerItem() {
        LayoutInflater lf = getActivity().getLayoutInflater().from(getContext());
        View view1 = lf.inflate(R.layout.item_viewpager_home, null);
        View view2 = lf.inflate(R.layout.item_viewpager_next, null);
        list.add(view1);
        list.add(view2);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }
        TextView mContent;
    }

}
