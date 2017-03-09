package com.hn.d.valley.main.friend;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.WaveSideBarView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.FriendListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.main.message.SearchUserUIView;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.widget.LetterIndexView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/7.
 */
public class FriendUIView extends BaseUIView {

//    @BindView(R.id.iv_index_bg)
//    ImageView iv_index;
//    @BindView(R.id.tv_friend_index)
//    TextView tv_index;
//    @BindView(R.id.sidebar_friend_index)
//    WaveSideBarView sidebar_friend;

    private FriendsControl mFriendsControl;

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.add_s).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new SearchUserUIView());
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.friend)).setRightItems(rightItems);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mFriendsControl.init(mBaseContentLayout,mSubscriptions);
        mFriendsControl.setToUserDetailAction(new Action1<FriendBean>() {
            @Override
            public void call(FriendBean o) {
                mOtherILayout.startIView(new UserDetailUIView(o.getUid()));
            }
        });

        loadFriends();
    }

    private void loadFriends() {
        mFriendsControl.loadData();
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        mFriendsControl = new FriendsControl(mActivity);

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_message_friend);
    }
}
