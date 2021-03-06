package com.hn.d.valley.main.home.recommend;

import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.home.NoTitleBaseRecyclerUIView;
import com.hn.d.valley.main.home.UserDiscussAdapter;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.user.DynamicDetailUIView;

import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 推荐
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class RecommendUIView extends NoTitleBaseRecyclerUIView<UserDiscussListBean.DataListBean> {

    /**
     * 需要过滤的tag
     */
    private Tag filterTag;

    public void setFilterTag(Tag filterTag) {
        this.filterTag = filterTag;
    }

    @Override
    protected RExBaseAdapter<String, UserDiscussListBean.DataListBean, String> initRExBaseAdapter() {
        return new UserDiscussAdapter(mActivity) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final UserDiscussListBean.DataListBean dataBean) {
                //super.onBindDataView(holder, posInData, tBean);
                UserDiscussItemControl.initItem(mSubscriptions, holder, dataBean, new Action1<UserDiscussListBean.DataListBean>() {
                    @Override
                    public void call(UserDiscussListBean.DataListBean dataListBean) {
                        loadData();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mOtherILayout.startIView(new DynamicDetailUIView(dataBean.getDiscuss_id()));
                    }
                }, getILayout());

            }

            @Override
            protected ILayout getILayout() {
                return mOtherILayout;
            }
        };
    }

    @Override
    protected void onUILoadData(final String page) {
        add(RRetrofit.create(UserInfoService.class)
                .discussList(Param.buildMap("uid:" + UserCache.getUserAccount(),
                        "type:" + 2, "page:" + page, "tag:" + getFilterTagId(), "first_id:" + first_id, "last_id:" + last_id))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onSucceed(UserDiscussListBean userDiscussListBean) {
                        if (userDiscussListBean == null) {
                            onUILoadDataEnd();
                        } else {
                            List<UserDiscussListBean.DataListBean> data_list = userDiscussListBean.getData_list();
                            initConfigId(userDiscussListBean);
                            onUILoadDataEnd(data_list);
                        }
                    }

                    @Override
                    public void onEnd() {
                        onUILoadDataFinish();
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }

                }));
    }

    public String getFilterTagId() {
        if (filterTag == null) {
            return "";
        }
        return filterTag.getId();
    }
}
