package com.hn.d.valley.widget;

import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.resources.ResUtil;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:00
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:00
 * 修改备注：
 * Version: 1.0.0
 */
public class HnLoading extends UIIDialogImpl {
    static boolean isShowing = false;
    static HnLoading mUILoading;
    private View mLoadView;

    /**
     * 显示
     */
    public static void hide() {
        if (isShowing && mUILoading != null) {
            mUILoading.finishDialog();
            isShowing = false;
        }
    }

    /**
     * 显示
     */
    public static HnLoading show(ILayout layout) {
        if (isShowing) {

        } else {
            if (mUILoading == null) {
                mUILoading = new HnLoading();
            }
            layout.startIView(mUILoading);
            isShowing = true;
        }
        return mUILoading;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        mLoadView = new View(mActivity);
        mLoadView.setBackgroundResource(R.drawable.load_animation_list);
        int size = (int) ResUtil.dpToPx(mActivity, 88);
        dialogRootLayout.addView(mLoadView, new ViewGroup.LayoutParams(size, size));
        return mLoadView;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        ((AnimationDrawable) mLoadView.getBackground()).start();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        ((AnimationDrawable) mLoadView.getBackground()).stop();
    }

    @Override
    public boolean canCanceledOnOutside() {
        return false;
    }
}