package com.hzease.tomeet.widget.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hzease.tomeet.AppConstants;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.R;
import com.hzease.tomeet.data.EvaluteBean;
import com.hzease.tomeet.data.WaitEvaluateBean;
import com.hzease.tomeet.widget.CircleImageView;
import com.hzease.tomeet.widget.MySeekBar;
import com.orhanobut.logger.Logger;
import com.xw.repo.BubbleSeekBar;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by xuq on 2017/5/11.
 */

public class WaitEvaluateAdapter extends RecyclerView.Adapter<WaitEvaluateAdapter.ViewHolder> {

    private RecyclerView rvy;
    private List<String> strings = new ArrayList<>();
    EvaluteBean evaluteBean;
    List<WaitEvaluateBean.DataBean> list;
    Context context;

    public WaitEvaluateAdapter(List<WaitEvaluateBean.DataBean> list, Context context, long roomId, RecyclerView rvy) {
        this.list = list;
        this.context = context;
        evaluteBean = new EvaluteBean();
        evaluteBean.setToken(PTApplication.userToken);
        evaluteBean.setUserId(PTApplication.userId);
        evaluteBean.setRoomId(String.valueOf(roomId));
        for (int i = 0; i < list.size(); i++) {
            EvaluteBean.EvaluationsBean e = new EvaluteBean.EvaluationsBean();
            e.setFriendId(String.valueOf(list.get(i).getId()));
            evaluteBean.getEvaluations().add(e);
        }
        for (int i = 0; i < getItemCount(); i++) {
            strings.add("");
        }
        this.rvy = rvy;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_evaluate, null);
        // TODO: 2017/6/6 事件分发
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final EvaluteBean.EvaluationsBean evaluationsBean = evaluteBean.getEvaluations().get(position);
        holder.toServerEvaluate.setTag(position);
        holder.toServerEvaluate.setText(strings.get(position));
        evaluationsBean.setFriendId(String.valueOf(list.get(position).getId()));
        holder.memberName.setText(list.get(position).getNickname());
        Logger.i(position + "Point:   " + holder.likeValue.getProgress());
        if (list.get(position).getPoint() == 0) {
            holder.all_friendlikevalue.setVisibility(View.VISIBLE);
            holder.likeValue.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(int progress, float progressFloat) {
                }

                @Override
                public void getProgressOnActionUp(int progress, float progressFloat) {
                    Logger.i("likeValue - getProgressOnActionUp: " + progress + "   " + holder.showValue.getProgress());
                    evaluationsBean.setFriendPoint(String.valueOf(progress));
                }

                @Override
                public void getProgressOnFinally(int progress, float progressFloat) {
                    Logger.e("likeValue - getProgressOnFinally: " + progress + "   " + holder.showValue.getProgress());
                }


            });
        } else {
            holder.all_friendlikevalue.setVisibility(View.GONE);
            evaluationsBean.setFriendPoint("5");
        }
        rvy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Logger.e("newState" + newState);
                holder.showValue.correctOffsetWhenContainerOnScrolling();
                holder.likeValue.correctOffsetWhenContainerOnScrolling();
                //super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Logger.e("dy" + dy);
            }
        });
        holder.showValue.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                //String showValueToServer = String.valueOf(holder.showValue.getProgress());
                //Logger.e("onProgressChanged showValue " + progress + "   " + showValueToServer);
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                Logger.i("showValue - getProgressOnActionUp: " + progress + "   " + holder.likeValue.getProgress());
                evaluationsBean.setRoomEvaluationPoint(String.valueOf(progress));
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                Logger.e("showValue - getProgressOnFinally: " + progress + "   " + holder.likeValue.getProgress());
            }
        });
        final List<String> labels = list.get(position).getLabels();
        holder.evaluate.setAdapter(new TagAdapter<String>(labels) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView view = (TextView) View.inflate(context, R.layout.tv, null);
                view.setText(labels.get(position));
                return view;
            }
        });
        holder.evaluate.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                holder.toServerEvaluate.setText(labels.get(position));
                List<String> memberLabels = new ArrayList<>();
                memberLabels.add(labels.get(position));
                Logger.e(memberLabels.toString());
                evaluationsBean.setLabels(memberLabels);
                return true;
            }
        });
        //头像
        Glide.with(holder.itemView.getContext())
                .load(AppConstants.YY_PT_OSS_USER_PATH + list.get(position).getId() + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL)
                .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext()))
                .signature(new StringSignature(list.get(position).getAvatarSignature()))
                .into(holder.icon);
    }

    public EvaluteBean getEvaluteBean() {
        return evaluteBean;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView icon;
        TextView memberName;
        MySeekBar likeValue;
        MySeekBar showValue;
        TagFlowLayout evaluate;
        EditText toServerEvaluate;
        AutoLinearLayout all_friendlikevalue;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.memberIcon);
            memberName = itemView.findViewById(R.id.memberName);
            likeValue = itemView.findViewById(R.id.bsb_likevalue_evaluate_item);
            showValue = itemView.findViewById(R.id.bsb_show_evaluate_item);
            evaluate = itemView.findViewById(R.id.flowlayout_tabs_evaluate);
            toServerEvaluate = itemView.findViewById(R.id.et_evaluate_item);
            all_friendlikevalue = itemView.findViewById(R.id.all_friendlikevalue);
            likeValue.correctOffsetWhenContainerOnScrolling();
            showValue.correctOffsetWhenContainerOnScrolling();
            toServerEvaluate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    strings.set((int) (toServerEvaluate.getTag()), s.toString());
                }
            });
        }
    }
}
