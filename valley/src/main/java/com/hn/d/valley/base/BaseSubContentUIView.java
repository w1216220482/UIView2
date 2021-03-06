package com.hn.d.valley.base;

import com.angcyo.uiview.model.TitleBarPattern;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/05 11:32
 * 修改人员：Robi
 * 修改时间：2017/01/05 11:32
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseSubContentUIView extends BaseContentUIView {
    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(getTitleString()).setShowBackImageView(true);
    }
}
