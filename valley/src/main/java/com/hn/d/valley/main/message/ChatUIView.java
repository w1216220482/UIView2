package com.hn.d.valley.main.message;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.widget.HnRefreshLayout;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：聊天界面
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:46
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:46
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatUIView extends UIContentView {

    String account;
    @BindView(R.id.group_view)
    RadioGroup mGroupView;
    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    HnRefreshLayout mRefreshLayout;
    @BindView(R.id.chat_root_layout)
    RSoftInputLayout mChatRootLayout;
    @BindView(R.id.input_view)
    ExEditText mInputView;
    @BindView(R.id.record_view)
    TextView mRecordView;

    public ChatUIView(String account) {
        this.account = account;
    }

    public static void start(ILayout mLayout, String account) {
        mLayout.startIView(new ChatUIView(account), new UIParam().setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_chat_layout);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mChatRootLayout.showEmojiLayout();
            }
        });
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(NimUserInfoCache.getInstance().getUserDisplayName(account))
                .setShowBackImageView(true);
    }

    /**
     * 切换语音输入
     */
    @OnCheckedChanged(R.id.message_voice_box)
    public void onMessageVoiceBox(boolean isChecked) {
        mInputView.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        mRecordView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        if (isChecked) {
            hideSoftInput();
        }
    }
}