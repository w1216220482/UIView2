package com.hn.d.valley.main.friend.groupchat;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.main.friend.FriendItem;
import com.hn.d.valley.widget.HnGlideImageView;

import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupAdapter extends RModelAdapter<FriendBean> {



    public AddGroupAdapter(Context context) {
        super(context);
        setModel(RModelAdapter.MODEL_MULTI);
    }

    private Action2 action;

    public void setAction(Action2 action) {
        this.action = action;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_firends_addgroup_item;
    }

    @Override
    protected void onBindCommonView(RBaseViewHolder holder, int position, FriendBean bean) {
        if (bean == null) {
            return;
        }

        HnGlideImageView imageView = holder.v(R.id.iv_item_head);
        TextView nickName = holder.tv(R.id.tv_friend_name);
        imageView.setImageUrl(bean.getAvatar());
        nickName.setText(bean.getDefaultMark());

    }

    @Override
    protected void onBindModelView(int model, final boolean isSelector, RBaseViewHolder holder, final int position, final FriendBean bean) {
        final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
//        checkBox.setChecked(isSelector);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(action == null){
                    return;
                }
                action.call(!isSelector,bean);
                setSelectorPosition(position,checkBox);
            }
        };

        checkBox.setOnClickListener(listener);
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, FriendBean bean) {

    }

}
