package com.hzease.tomeet.widget.adapters;


import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.hzease.tomeet.RecycleViewItemListener;

public abstract class BaseRecycleViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected RecycleViewItemListener itemListener;
    protected List<T> datas = new ArrayList<T>();

    public List<T> getDatas() {
        if (datas == null)
            datas = new ArrayList<T>();
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setItemListener(RecycleViewItemListener listener) {
        this.itemListener = listener;
    }

}