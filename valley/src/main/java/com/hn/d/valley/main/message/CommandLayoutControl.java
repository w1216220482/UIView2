package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RecyclerViewPager;
import com.angcyo.uiview.recycler.adapter.RecyclerViewPagerAdapter;
import com.angcyo.uiview.recycler.RecyclerViewPagerIndicator;
import com.hn.d.valley.R;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/03 15:36
 * 修改人员：Robi
 * 修改时间：2017/01/03 15:36
 * 修改备注：
 * Version: 1.0.0
 */
public class CommandLayoutControl {

    RBaseViewHolder mViewHolder;
    List<CommandItemInfo> mCommandItemInfos;
    Context mContext;
    RecyclerViewPager mRecyclerViewPager;
    RecyclerViewPagerIndicator mRecyclerViewPagerIndicator;

    public CommandLayoutControl(Context context, RBaseViewHolder viewHolder, List<CommandItemInfo> commandItemInfos) {
        mContext = context;
        mViewHolder = viewHolder;
        mCommandItemInfos = commandItemInfos;

        mRecyclerViewPager = viewHolder.v(R.id.recycler_view_pager);
        mRecyclerViewPagerIndicator = viewHolder.v(R.id.recycler_view_pager_indicator);
        mRecyclerViewPager.setItemAnim(false);
//        mRecyclerViewPager.setAdapter(new CommandAdapter(mRecyclerViewPager, mCommandItemInfos));
//        mRecyclerViewPagerIndicator.setupRecyclerViewPager(mRecyclerViewPager);
    }

    public void requestLayout() {
        mRecyclerViewPager.requestLayout();
    }

    public void fixHeight(int height) {
        mRecyclerViewPager.setFixHeight(height);
    }

    public void init() {
        if (mRecyclerViewPager.getAdapter() == null) {
            mRecyclerViewPager.setAdapter(new CommandAdapter(mRecyclerViewPager, mCommandItemInfos));
            mRecyclerViewPagerIndicator.setupRecyclerViewPager(mRecyclerViewPager);
        }
    }

    public static class CommandItemInfo {
        @DrawableRes
        public int icoResId;
        public View.OnClickListener mClickListener;
        public String text;

        public CommandItemInfo(int icoResId, String text, View.OnClickListener clickListener) {
            this.icoResId = icoResId;
            mClickListener = clickListener;
            this.text = text;
        }
    }

    class CommandAdapter extends RecyclerViewPagerAdapter<CommandItemInfo> {

        public CommandAdapter(RecyclerViewPager recyclerViewPager, List<CommandItemInfo> datas) {
            super(recyclerViewPager, datas);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return 0;
        }

        @Override
        protected View createContentView(ViewGroup parent, int viewType) {
            RelativeLayout relativeLayout = new RelativeLayout(mContext);

            TextView textView = new TextView(mContext);
            textView.setTag("text");
            textView.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            relativeLayout.addView(textView, params);

            relativeLayout.setClickable(true);
            relativeLayout.setBackgroundResource(R.drawable.base_bg_selector);
            return relativeLayout;
        }

        @Override
        protected void onBindRawView(RBaseViewHolder holder, int position, CommandItemInfo bean) {
            holder.itemView.setOnClickListener(bean.mClickListener);
            final TextView textView = holder.tag("text");
            textView.setText(bean.text);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, bean.icoResId, 0, 0);
        }
    }
}
