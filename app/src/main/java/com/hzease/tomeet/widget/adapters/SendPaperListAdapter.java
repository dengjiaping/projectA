package com.hzease.tomeet.widget.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hzease.tomeet.AppConstants;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.R;
import com.hzease.tomeet.data.SmallPaperBean;
import com.hzease.tomeet.widget.CircleImageView;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by xuq on 2017/6/13.
 */

public class SendPaperListAdapter extends BaseAdapter {

    List<SmallPaperBean.DataBean> mDatas;
    Context context;
    public SendPaperListAdapter(List<SmallPaperBean.DataBean> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    public void addData(List<SmallPaperBean.DataBean> mDatas){
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(PTApplication.getInstance(), R.layout.item_paperlist,null);
            viewHolder.senderIcon =  convertView.findViewById(R.id.civ_pagerlist_sendericon_item);
            viewHolder.senderInfo =  convertView.findViewById(R.id.tv_pagerlist_senderinfo_item);
            viewHolder.paperState =  convertView.findViewById(R.id.iv_pagerlist_pagerstate_item);
            viewHolder.pagerTime = convertView.findViewById(R.id.tv_pagerlist_time_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Logger.e("userId" + mDatas.get(position));
        // 头像
        Glide.with(context)
                .load(AppConstants.YY_PT_OSS_USER_PATH + mDatas.get(position).getReceiverId() + AppConstants.YY_PT_OSS_AVATAR_THUMBNAIL)
                .bitmapTransform(new CropCircleTransformation(context))
                .signature(new StringSignature(mDatas.get(position).getAvatarSignature()))
                .into(viewHolder.senderIcon);
        if (mDatas.get(position).getState()>3){
            viewHolder.senderInfo.setText("你回复了"+mDatas.get(position).getNickname()+"的小纸条");
        }else{
            viewHolder.senderInfo.setText("你给" + mDatas.get(position).getNickname() + "传了一张小纸条小纸条");
        }
        viewHolder.pagerTime.setText(calculateTime(mDatas.get(position).getCreateTime()));
        return convertView;
    }
    class ViewHolder{
        public CircleImageView senderIcon;
        public TextView senderInfo;
        public ImageView paperState;
        public TextView pagerTime;
    }

    private String calculateTime(long time) {
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long now = (System.currentTimeMillis() + offSet) / 60000;
        long create = (time + offSet) / 60000;
        long diff = now - create;
        if (diff ==0){
            return "刚刚";
        }else if (diff < 60) {
            return diff + "分钟前";
        } else if (diff < 1440) {
            return diff / 60 + "小时前";
        } else {
            return diff / 60 / 24 + "天前";
        }
    }
}
