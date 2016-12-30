package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/29 11:31
 * 修改人员：Robi
 * 修改时间：2016/12/29 11:31
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatControl {

    public RBaseViewHolder mViewHolder;
    public ChatAdapter mChatAdapter;
    public RRecyclerView mRecyclerView;
    HnRefreshLayout mRefreshLayout;
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            mChatAdapter.appendData(messages);
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
        }
    };
    Context mContext;

    Observer<IMMessage> mMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage imMessage) {
            //消息状态发生了改变
            List<IMMessage> allDatas = mChatAdapter.getAllDatas();
            for (int i = 0; i < allDatas.size(); i++) {
                if (allDatas.get(i).isTheSame(imMessage)) {
                    mChatAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };

    public ChatControl(Context context, RBaseViewHolder viewHolder) {
        mContext = context;
        mViewHolder = viewHolder;
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        mChatAdapter = new ChatAdapter(context, null);
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mChatAdapter);
    }

    public void onLoad() {
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
        NIMClient.getService(MsgServiceObserve.class)
                .observeMsgStatus(mMessageObserver, true);
    }

    public void onUnload() {
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    public void resetData(List<IMMessage> messages) {
        mChatAdapter.resetData(messages);
        scrollToEnd();
    }

    public void addData(IMMessage message) {
        mChatAdapter.addLastItem(message);
        scrollToEnd();
    }

    public void scrollToEnd() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
        } else {
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        }
    }

    private boolean needShowTime(long oldTime, long nowTime) {
        return nowTime - oldTime > 60 * 1000;
    }

    class ChatAdapter extends RBaseAdapter<IMMessage> {


        public ChatAdapter(Context context, List<IMMessage> datas) {
            super(context, datas);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_chat_msg_layout;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, IMMessage bean) {
            NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

            String avatar = "";
            View itemRootLayout = holder.v(R.id.item_root_layout);
            View contentRootLayout = holder.v(R.id.msg_content_layout);
            if (bean.getDirect() == MsgDirectionEnum.In) {
                //收到的消息
                itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                contentRootLayout.setBackgroundResource(R.drawable.bubble_box_left_selector);
                if (userInfoCache != null) {
                    avatar = userInfoCache.getUserInfo(bean.getFromAccount()).getAvatar();
                }
            } else {
                //发出去的消息
                itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                contentRootLayout.setBackgroundResource(R.drawable.bubble_box_right_selector);
                avatar = UserCache.instance().getAvatar();
            }

            itemRootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //头像
            DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.msg_ico_view), avatar);

            //消息内容
            updateMsgContent(holder, bean);

            //时间
            TextView timeTextView = holder.tv(R.id.msg_time_view);
            String timeString = TimeUtil.getTimeShowString(bean.getTime(), false);
            timeTextView.setText(timeString);
            if (position == 0) {
                timeTextView.setVisibility(View.VISIBLE);
            } else {
                final IMMessage preMessage = mAllDatas.get(position - 1);
                timeTextView.setVisibility(needShowTime(preMessage.getTime(), bean.getTime()) ? View.VISIBLE : View.GONE);
            }

            //消息状态
            updateMsgStatus(holder, bean);
        }

        private void updateMsgContent(RBaseViewHolder holder, IMMessage bean) {
            switch (bean.getMsgType()) {
                case audio:
                    break;
                case avchat://音视频通话
                    break;
                case custom://第三方APP自定义消息
                    break;
                case file:
                    break;
                case image:
                    break;
                case location:
                    break;
                case notification:
                    break;
                case text:
                    MoonUtil.show(mContext, holder.tv(R.id.msg_text_view), bean.getContent());
                    break;
                case tip:
                    break;
                case undef://未知消息类型
                    break;
                case video://视频消息
                    break;

            }
        }

        private void updateMsgStatus(RBaseViewHolder viewHolder, final IMMessage bean) {
            final View failView = viewHolder.v(R.id.status_fail_view);
            final View sendingView = viewHolder.v(R.id.status_sending_view);

            failView.setVisibility(View.GONE);
            sendingView.setVisibility(View.GONE);
            MsgStatusEnum status = bean.getStatus();
            switch (status) {
                case draft:
                    //草稿
                    break;
                case fail:
                    //失败
                    failView.setVisibility(View.VISIBLE);
                    failView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NIMClient.getService(MsgService.class).sendMessage(bean, true);
                            failView.setVisibility(View.GONE);
                            sendingView.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case read:
                    //已读
                    break;
                case sending:
                    //发送中
                    sendingView.setVisibility(View.VISIBLE);
                    break;
                case success:
                    //成功
                    break;
                case unread:
                    //未读
                    break;
            }

//            L.e("消息状态:" + status.getValue());
        }
    }
}
