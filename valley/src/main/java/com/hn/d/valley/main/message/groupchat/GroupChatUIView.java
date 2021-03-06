package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.message.P2PChatUIView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;

/**
 * Created by hewking on 2017/3/10.
 */
public class GroupChatUIView extends P2PChatUIView {

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.add_friends_s).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
                if (team != null && team.isMyTeam()) {
                    GroupInfoUIVIew.start(mOtherILayout,mSessionId,sessionType);
                } else {
                    T_.info(mActivity.getString(R.string.team_invalid_tip));
                }
            }
        }));

        return super.getTitleBar().setRightItems(rightItems);
    }


    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }

    /**
     * @param sessionId   聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        mLayout.startIView(new GroupChatUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }



}
