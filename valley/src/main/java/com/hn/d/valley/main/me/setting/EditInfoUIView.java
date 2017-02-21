package com.hn.d.valley.main.me.setting;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RDragRecyclerView;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.bumptech.glide.Glide;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.adapter.HnAddImageAdapter;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.dialog.DateDialog;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.oss.OssControl2;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserControl;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：编辑个人资料
 * 创建人员：Robi
 * 创建时间：2017/02/17 17:42
 * 修改人员：Robi
 * 修改时间：2017/02/17 17:42
 * 修改备注：
 * Version: 1.0.0
 */
public class EditInfoUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private HnAddImageAdapter mHnAddImageAdapter;
    private List<Luban.ImageItem> mOldItems = new ArrayList<>();//原先的照片地址
    private List<Luban.ImageItem> mPhones = new ArrayList<>();//原先的照片地址用来adapter
    private SparseArray<String> mUrls = new SparseArray<>();//所有照片墙的图片网络地址
    private OssControl2 mOssControl;
    private Action0 mOnFinishAction;
    //选择用户头像图片返回
    private boolean isSetUserIco = false;
    private ItemInfoLayout mUserIcoInfoLayout;
    private String mUserSetIco;

    public EditInfoUIView(List<String> urls, Action0 onFinishAction) {
        for (String url : urls) {
            mOldItems.add(new Luban.ImageItem(url));
        }
        mPhones.addAll(mOldItems);
        mOnFinishAction = onFinishAction;
    }

    @Override
    protected int getTitleResource() {
        return R.string.edit_into_title;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().addRightItem(TitleBarPattern.TitleBarItem
                .build(mActivity.getResources().getString(R.string.finish), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaveInfo();
                    }
                }));
    }

    /**
     * 保存用户信息
     */
    private void onSaveInfo() {
        startUpload();
    }

    String getAllPhotos() {
        if (mUrls == null || mUrls.size() == 0) {
            return "empty";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mUrls.size(); i++) {
            result.append(mUrls.get(i));
            result.append(",");
        }
        return RUtils.safe(result);
    }

    /**
     * 3:保存信息...end
     */
    private void startSave() {
        final String allPhotos = getAllPhotos();
        if ("empty".equalsIgnoreCase(allPhotos)) {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UserCache.instance().getUserInfoBean().setPhotos("");
                }
            });
        } else {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UserCache.instance().getUserInfoBean().setPhotos(allPhotos);
                }
            });
        }

        add(RRetrofit.create(UserInfoService.class)
                .editInfo(Param.buildMap("photos:" + allPhotos, "avatar:" + mUserSetIco))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean bean) {
                        super.onSucceed(bean);
                        UserCache.instance().setUserInfoBean(bean);
                        T_.ok(mActivity.getString(R.string.save_ok_tip));
                        HnLoading.hide();
                        finishIView();
                        mOnFinishAction.call();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        HnLoading.hide();
                    }
                }));
    }

    /**
     * 2:检查是否需要上传用户头像
     */
    private void checkUserIco() {
        if (TextUtils.isEmpty(mUserSetIco)) {
            mUserSetIco = UserCache.getUserAvatar();
            startSave();
        } else {
            List<String> files = new ArrayList<>();
            files.add(mUserSetIco);
            new OssControl(new OssControl.OnUploadListener() {
                @Override
                public void onUploadStart() {

                }

                @Override
                public void onUploadSucceed(List<String> list) {
                    mUserSetIco = list.get(0);
                    RRealm.exe(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            UserCache.setUserAvatar(mUserSetIco);
                            UserCache.instance().getUserInfoBean().setAvatar(mUserSetIco);
                        }
                    });
                    startSave();
                }

                @Override
                public void onUploadFailed(int code, String msg) {
                    T_.show(msg);
                    HnLoading.hide();
                }
            }).uploadCircleImg(files);
        }
    }

    /**
     * 1:先上传图片
     */
    private void startUpload() {
        mUrls.clear();
        final List<Luban.ImageItem> allDatas = mHnAddImageAdapter.getAllDatas();
        final List<String> needUploadFiles = new ArrayList<>();

        HnLoading.show(mOtherILayout, false);

        if (allDatas.isEmpty()) {
            checkUserIco();
            return;
        }

        for (int i = 0; i < allDatas.size(); i++) {
            Luban.ImageItem imageItem = allDatas.get(i);
            if (TextUtils.isEmpty(imageItem.url)) {
                needUploadFiles.add(imageItem.thumbPath);
            } else {
                mUrls.put(i, imageItem.url);
            }
        }

        mOssControl = new OssControl2(new OssControl2.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                //得到上传成功的文件在图片墙中的位置
                for (String upload : list) {
                    String[] split = upload.split("\\|");
                    String thumbPath = split[0];
                    for (int i = 0; i < allDatas.size(); i++) {
                        Luban.ImageItem imageItem = allDatas.get(i);
                        if (TextUtils.equals(imageItem.thumbPath, thumbPath)) {
                            mUrls.put(i, split[1]);
                        }
                    }
                }
                checkUserIco();
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                T_.show(msg);
                HnLoading.hide();
            }
        });
        mOssControl.uploadCircleImg(needUploadFiles);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_drag_recycler_view;
        }
        return R.layout.item_info_layout;
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        ImagePickerHelper.clearAllSelectedImages();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选中的图片列表
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (isSetUserIco) {
            if (!images.isEmpty()) {
                mUserSetIco = images.get(0);
                Glide.with(mActivity)
                        .load(Uri.fromFile(new File(mUserSetIco)))
                        .override(mUserIcoInfoLayout.getMeasuredHeight(), mUserIcoInfoLayout.getMeasuredHeight())
                        .transform(new GlideCircleTransform(mActivity))
                        .into(mUserIcoInfoLayout.getImageView());
            }
        } else {
            final List<Luban.ImageItem> oldDatas = mHnAddImageAdapter == null ? null : mHnAddImageAdapter.getAllDatas();

            //请求压缩图片,排除掉已经压缩的图片
            Observable<ArrayList<Luban.ImageItem>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data, oldDatas);
            if (observable != null) {
                observable.subscribe(new BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        HnLoading.show(mILayout, false);
                    }

                    @Override
                    public void onSucceed(ArrayList<Luban.ImageItem> strings) {
                        if (BuildConfig.DEBUG) {
                            Luban.logFileItems(mActivity, strings);
                        }
                        //新增的图片,和原有的图片匹配, 进行组合, 产生新的图片集合
                        List<Luban.ImageItem> imageItemList = new ArrayList<>();
                        for (int i = 0; i < images.size(); i++) {
                            String needPath = images.get(i);
                            Luban.ImageItem inListItem = Image.isInList(strings, needPath);
                            if (inListItem != null) {
                                imageItemList.add(inListItem);
                            } else {
                                inListItem = Image.isInList(oldDatas, needPath);
                                imageItemList.add(inListItem);
                            }
                        }

                        if (mHnAddImageAdapter != null) {
                            List<Luban.ImageItem> oldItems = getOldItems();
                            oldItems.addAll(imageItemList);
                            mHnAddImageAdapter.resetData(oldItems);
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        HnLoading.hide();
                    }
                });
            }
        }
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        final int size = 2 * line;

        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();

        //照片墙...
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                bindPhoneWallItem(holder, size);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mUserIcoInfoLayout = holder.v(R.id.item_info_layout);
                mUserIcoInfoLayout.setItemText("头像");
                mUserIcoInfoLayout.setRightDrawableRes(-1);
                mUserIcoInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isSetUserIco = true;
                        ImagePickerHelper.startImagePicker(mActivity, true, true, true, false, 1);
                    }
                });
                mUserIcoInfoLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(mActivity)
                                .load(UserCache.getUserAvatar())
                                .override(mUserIcoInfoLayout.getMeasuredHeight(), mUserIcoInfoLayout.getMeasuredHeight())
                                .transform(new GlideCircleTransform(mActivity))
                                .into(mUserIcoInfoLayout.getImageView());
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindNameItem(holder, userInfoBean);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("ID");
                infoLayout.setRightDrawableRes(-1);
                infoLayout.setItemDarkText(UserCache.getUserAccount());
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("我的二维码");
                infoLayout.setDarkDrawableRes(R.drawable.qr_code);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getResources().getString(R.string.sex));
                infoLayout.setItemDarkText(UserControl.getSex(mActivity, userInfoBean.getSex()));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(UIItemDialog.build()
                                .addItem(mActivity.getString(R.string.man), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateSex(infoLayout, userInfoBean, "1");
                                    }
                                })
                                .addItem(mActivity.getString(R.string.women), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateSex(infoLayout, userInfoBean, "2");
                                    }
                                }));
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("出生日期");
                infoLayout.setItemDarkText("1000-01-01");
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new DateDialog());
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("地区");
                infoLayout.setItemDarkText(userInfoBean.getProvince_name() + " " + userInfoBean.getCity_name());
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("语音介绍");
                infoLayout.setItemDarkText("点击添加");
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {

            @Override
            public void setItemOffsets(Rect rect) {
                super.setItemOffsets(rect);
                rect.bottom = left;
            }

            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindSignatureItem(holder, userInfoBean);
            }
        }));
    }

    /**
     * 照片墙
     */
    protected void bindPhoneWallItem(RBaseViewHolder holder, final int size) {
        final RDragRecyclerView dragRecyclerView = holder.v(R.id.drag_recycler_view);
        ViewGroup.LayoutParams layoutParams = dragRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;

        int itemHeight = layoutParams.width / 3;
        layoutParams.height = itemHeight * 2 + size;
        dragRecyclerView.setLayoutParams(layoutParams);

        //分割线
        dragRecyclerView.addItemDecoration(RExItemDecoration.build(new RExItemDecoration.ItemDecorationCallback() {
            @Override
            public Rect getItemOffsets(LinearLayoutManager layoutManager, int position) {
                Rect rect = new Rect(0, 0, 0, 0);
                if (position == 1) {
                    rect.set(size, 0, size, 0);
                } else if (position > 2) {
                    rect.top = size;
                    if (position == 4) {
                        rect.left = size;
                        rect.right = size;
                    }
                }
                return rect;
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

            }
        }));

        //拖拽处理
        dragRecyclerView.setDragCallback(new RDragRecyclerView.OnDragCallback() {

            @Override
            public boolean canDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                int itemCount = mHnAddImageAdapter.getAllDatas().size();
                if (itemCount == 6) {
                    return true;
                } else {
                    return position != mHnAddImageAdapter.getItemCount() - 1;
                }
            }
        });

        mHnAddImageAdapter = new HnAddImageAdapter(mActivity);
        mHnAddImageAdapter.setItemHeight(itemHeight);
        mHnAddImageAdapter.resetData(mPhones);

        dragRecyclerView.setAdapter(mHnAddImageAdapter);

        //事件处理
        mHnAddImageAdapter.setAddListener(new HnAddImageAdapter.OnAddListener() {

            @Override
            public void onAddClick(View view, int position, Luban.ImageItem item) {
                isSetUserIco = false;
                ImagePickerHelper.startImagePicker(mActivity, true, false, false, true, getMaxSelectorCount());
            }

            @Override
            public void onItemClick(View view, int position, Luban.ImageItem item) {
                ImagePagerUIView.start(mOtherILayout, view,
                        PhotoPager.getImageItems2(mHnAddImageAdapter.getAllDatas()), position);
            }

            @Override
            public void onItemLongClick(View view, int position, Luban.ImageItem item) {
                mHnAddImageAdapter.setDeleteModel(dragRecyclerView);
            }

            @Override
            public void onDeleteClick(View view, int position, Luban.ImageItem item) {
                mHnAddImageAdapter.getAllDatas().remove(position);
                ImagePickerHelper.deleteItemFromSelected(item.path);
                if (position == 5) {
                    mHnAddImageAdapter.notifyItemChanged(position);
                } else {
                    mHnAddImageAdapter.notifyItemRemoved(position);
                    mHnAddImageAdapter.notifyItemRangeChanged(position, 6);
                }
            }
        });
    }

    /**
     * 图片最大选择的数量, 需要减去已经存在的网络图片
     */
    private int getMaxSelectorCount() {
        int urlCount = 0;
        for (Luban.ImageItem item : mPhones) {
            if (!TextUtils.isEmpty(item.url)) {
                urlCount++;
            }
        }
        return 6 - urlCount;
    }

    private List<Luban.ImageItem> getOldItems() {
        List<Luban.ImageItem> items = new ArrayList<>();
        for (Luban.ImageItem item : mPhones) {
            if (!TextUtils.isEmpty(item.url)) {
                items.add(item);
            }
        }
        return items;
    }

    private void updateSex(final ItemInfoLayout infoLayout, final UserInfoBean old, final String sex) {
        if (TextUtils.equals(old.getSex(), sex)) {
            return;
        }
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                old.setSex(sex);
            }
        });
        infoLayout.setItemDarkText(UserControl.getSex(mActivity, sex));
        add(RRetrofit.create(UserInfoService.class)
                .editInfo(Param.buildMap("sex:" + sex))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean bean) {
                        super.onSucceed(bean);
                        UserCache.instance().setUserInfoBean(bean);
                    }
                }));
    }

    /**
     * 个性签名
     */
    protected void bindSignatureItem(RBaseViewHolder holder, final UserInfoBean userInfoBean) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.signature_title));
        infoLayout.setItemDarkText(userInfoBean.getSignature());
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
                    @Override
                    public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                        return super.initTitleBar(titleBarPattern)
                                .setTitleString(mActivity, R.string.signature_title)
                                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.finish),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String value = "empty";
                                                if (!mExEditText.isEmpty()) {
                                                    value = mExEditText.string();
                                                }

                                                RRealm.exe(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        userInfoBean.setSignature(mExEditText.string());
                                                    }
                                                });
                                                infoLayout.setItemDarkText(mExEditText.string());

                                                add(RRetrofit.create(UserInfoService.class)
                                                        .editInfo(Param.buildMap("signature:" + value))
                                                        .compose(Rx.transformer(UserInfoBean.class))
                                                        .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                                                            @Override
                                                            public void onSucceed(UserInfoBean bean) {
                                                                super.onSucceed(bean);
                                                                UserCache.instance().setUserInfoBean(bean);
                                                            }
                                                        }));

                                                finishIView(mIView);
                                            }
                                        }));
                    }

                    @Override
                    public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
                        super.initInputView(holder, editText, bean);
                        editText.setSingleLine(false);
                        editText.setMaxLines(5);
                        int maxCount = mActivity.getResources().getInteger(R.integer.signature_count);
                        editText.setMaxLength(maxCount);
                        String signature = userInfoBean.getSignature();
                        setInputText(signature);
                        editText.setGravity(Gravity.TOP);
                        UI.setViewHeight(editText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                        final TextIndicator textIndicator = holder.v(R.id.single_text_indicator_view);
                        textIndicator.setVisibility(View.VISIBLE);
                        textIndicator.initIndicator(TextUtils.isEmpty(signature) ? 0 : signature.length(), maxCount);

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                textIndicator.setCurrentCount(s.length());
                            }
                        });
                    }
                }));
            }
        });
    }

    /**
     * 用户昵称
     */
    protected void bindNameItem(RBaseViewHolder holder, final UserInfoBean userInfoBean) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.name_text));
        infoLayout.setItemDarkText(userInfoBean.getUsername());
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
                    @Override
                    public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                        return super.initTitleBar(titleBarPattern)
                                .setTitleString(mActivity.getString(R.string.modify_name_title))
                                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.save)
                                        , new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mExEditText.isEmpty()) {
                                                    Anim.band(mExEditText);
                                                    return;
                                                }

                                                final String name = mExEditText.string();
                                                RRealm.exe(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        userInfoBean.setUsername(name);
                                                    }
                                                });
                                                infoLayout.setItemDarkText(name);

                                                add(RRetrofit.create(UserInfoService.class)
                                                        .editInfo(Param.buildMap("username:" + name))
                                                        .compose(Rx.transformer(UserInfoBean.class))
                                                        .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                                                            @Override
                                                            public void onSucceed(UserInfoBean bean) {
                                                                super.onSucceed(bean);
                                                                UserCache.instance().setUserInfoBean(bean);
                                                            }
                                                        }));
                                                finishIView(mIView);
                                            }
                                        }))
                                ;
                    }

                    @Override
                    public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
                        super.initInputView(holder, editText, bean);
                        editText.setMaxLength(mActivity.getResources().getInteger(R.integer.name_count));
                        editText.setHint(R.string.input_name_hint);
                        setInputText(userInfoBean.getUsername());
                    }
                }));
            }
        });
    }
}
