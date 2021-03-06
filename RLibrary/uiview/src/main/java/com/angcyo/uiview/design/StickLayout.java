package com.angcyo.uiview.design;

import android.content.Context;
import android.support.annotation.Px;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.UI;

/**
 * Created by angcyo on 2017-03-15.
 */

public class StickLayout extends RelativeLayout {

    View mFloatView;
    int floatTopOffset = 0;
    int floatTop = 0;//
    float downY, downX;
    CanScrollUpCallBack mScrollTarget;
    boolean inTopTouch = false, needHandle = true;
    boolean isFirst = true;
    boolean wantVertical = false;
    private OverScroller mOverScroller;
    private GestureDetectorCompat mGestureDetectorCompat;
    private int mTouchSlop;
    private int maxScrollY, topHeight;
    private int mVelocityY;

    public StickLayout(Context context) {
        this(context, null);
    }

    public StickLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    private void initLayout() {
        mOverScroller = new OverScroller(getContext());
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, final float velocityY) {
                        //L.e("call: onFling([e1, e2, velocityX, velocityY])-> " + velocityX + "  " + velocityY);
                        if (Math.abs(velocityX) > Math.abs(velocityY)) {
                            return false;
                        }

                        if (isFloat() && velocityY > 0) {
                            //L.e("call: onFling return");
                            return false;
                        }
                        mOverScroller.fling(0, getScrollY(), 0, (int) -velocityY, 0, 0, 0, maxScrollY);
                        postInvalidate();
                        final RecyclerView recyclerView = mScrollTarget.getRecyclerView();
                        if (velocityY < -3000 && recyclerView != null) {
                           // L.e("recyclerView fling..............." + -velocityY);
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.fling(0, (int) -velocityY - 3000);
                                }
                            });
                        }
                        return true;
                    }
                });
        mTouchSlop = 0;//ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            int currY = mOverScroller.getCurrY();
            scrollTo(0, currY);
            postInvalidate();
        }
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        int offset = Math.min(maxScrollY, Math.max(0, y));
        boolean layout = false;
        if (getScrollY() != offset) {
            layout = true;
        }
        super.scrollTo(0, offset);
        if (layout) {
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        View topView = getChildAt(0);
        View scrollView = getChildAt(1);
        if (scrollView instanceof CanScrollUpCallBack) {
            mScrollTarget = (CanScrollUpCallBack) scrollView;
        } else {
            if (mScrollTarget == null) {
                mScrollTarget = new CanScrollUpCallBack() {
                    @Override
                    public boolean canChildScrollUp() {
                        return false;
                    }

                    @Override
                    public RecyclerView getRecyclerView() {
                        return null;
                    }
                };
            }
        }
        mFloatView = getChildAt(2);

        measureChild(topView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mFloatView, widthMeasureSpec, heightMeasureSpec);
        measureChild(scrollView, widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize - mFloatView.getMeasuredHeight(), MeasureSpec.EXACTLY));

        floatTop = topView.getMeasuredHeight();
        maxScrollY = floatTop;
        topHeight = floatTop + mFloatView.getMeasuredHeight();

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        L.w("onLayout() -> " + changed + " l:" + l + " t:" + t + " r:" + r + " b:" + b);
        View firstView = getChildAt(0);
        firstView.layout(0, 0, r, firstView.getMeasuredHeight());

        View lastView = getChildAt(1);
        lastView.layout(0, firstView.getMeasuredHeight() + mFloatView.getMeasuredHeight(), r,
                firstView.getMeasuredHeight() + mFloatView.getMeasuredHeight() + lastView.getMeasuredHeight());

        if (mFloatView != null) {
            int scrollY = getScrollY();
            if (isFloat()) {
                mFloatView.layout(mFloatView.getLeft(), scrollY + floatTopOffset, r,
                        scrollY + floatTopOffset + mFloatView.getMeasuredHeight());
            } else {
                mFloatView.layout(mFloatView.getLeft(), firstView.getMeasuredHeight(), r,
                        firstView.getMeasuredHeight() + mFloatView.getMeasuredHeight());
            }
        }
    }

    private boolean isFloat() {
        return getScrollY() >= floatTop;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (!inTopTouch) {
//            return super.dispatchTouchEvent(ev);
//        }
        mOverScroller.abortAnimation();
        mGestureDetectorCompat.onTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                float moveX = ev.getX();
                float offsetY = downY - moveY;
                float offsetX = downX - moveX;

                downY = moveY;
                downX = moveX;

                boolean wantV;
                if (Math.abs(offsetX) > Math.abs(offsetY)) {
                    wantV = false;
                } else {
                    wantV = true;
                }

                boolean first = isFirst;
                isFirst = false;

                if (first) {
                    wantVertical = wantV;

                    if (inTopTouch) {
                        if (wantVertical) {
                            if (offsetTo(offsetY)) {
                                return super.dispatchTouchEvent(ev);
                            }
                        } else {
                            return super.dispatchTouchEvent(ev);
                        }
                    } else {
                        if (wantVertical) {
                            if (offsetTo(offsetY)) {
                                return super.dispatchTouchEvent(ev);
                            }
                        } else {
                            return super.dispatchTouchEvent(ev);
                        }
                    }

                } else {
                    if (inTopTouch) {
                        if (offsetTo(offsetY)) {
                            return super.dispatchTouchEvent(ev);
                        }
                    } else {
                        if (wantVertical == wantV) {
                            if (wantV) {
                                if (offsetTo(offsetY)) {
                                    return super.dispatchTouchEvent(ev);
                                }
                            } else {
                                return super.dispatchTouchEvent(ev);
                            }
                        } else {
                            return super.dispatchTouchEvent(ev);
                        }
                    }
                }

                return false;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onTouchUp();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void onTouchUp() {
        downY = 0;
        downX = 0;
        needHandle = true;
        isFirst = true;
        wantVertical = true;
    }

    private boolean offsetTo(float offsetY) {
        if (Math.abs(offsetY) > mTouchSlop) {
            if (offsetY < 0) {
                //手指下滑
                boolean scrollVertically = mScrollTarget.canChildScrollUp();
                if (!scrollVertically) {
                    scrollBy(0, (int) (offsetY));
                } else {
                    return true;
                }

            } else {
                if (isFloat()) {
                    return true;
                }
                scrollBy(0, (int) (offsetY));
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onTouchUp();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void onTouchDown(MotionEvent ev) {
        downY = ev.getY();
        downX = ev.getX();
        int scrollY = getScrollY();

        if (isFloat()) {
            if (mFloatView.getMeasuredHeight() > downY) {
                inTopTouch = true;
                needHandle = true;
            } else {
                inTopTouch = false;
            }
        } else {
            if (topHeight - scrollY > downY) {
                inTopTouch = true;
                needHandle = true;
            } else {
                inTopTouch = false;
            }
        }
        isFirst = true;
        wantVertical = true;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onStopNestedScroll(final View child) {
        super.onStopNestedScroll(child);
        //L.e("call: onStopNestedScroll([child])-> ");
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) child;
            RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //L.e("call: onStopNestedScroll([child])->  check fling");
                        if (!UI.canChildScrollUp(child)) {
                            if (mVelocityY < 0) {
                                int dy = 0, time = 0;
                                if (mVelocityY < -15000) {
                                    //很快的速度 下滑
                                    dy = -maxScrollY;
                                    time = 100;
                                } else if (mVelocityY < -10000) {
                                    //很小的速度 下滑
                                    dy = -(maxScrollY - maxScrollY / 5);
                                    time = 200;
                                } else if (mVelocityY < -8000) {
                                    //很小的速度 下滑
                                    dy = -(maxScrollY - maxScrollY * 2 / 5);
                                    time = 300;
                                } else if (mVelocityY < -5000) {
                                    //很小的速度 下滑
                                    dy = -(maxScrollY - maxScrollY * 3 / 5);
                                    time = 400;
                                } else if (mVelocityY < -1000) {
                                    //很小的速度 下滑
                                    dy = -(maxScrollY - maxScrollY * 4 / 5);
                                    time = 500;
                                }
                                mOverScroller.startScroll(0, getScrollY(), 0, dy, time);
                                postInvalidate();
                            }
                        }
                        recyclerView.removeOnScrollListener(this);
                        mVelocityY = 0;
                    }
                }
            };
            recyclerView.addOnScrollListener(scrollListener);
        }
    }


    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        //L.e("call: onNestedFling([target, velocityX, velocityY, consumed])-> " + velocityY);
        if (UI.canChildScrollUp(target)) {
            mVelocityY = (int) velocityY;
        } else {
            mVelocityY = 0;
        }
        return false;
    }


    public interface CanScrollUpCallBack {
        /**
         * 顶部是否还可以滚动
         */
        boolean canChildScrollUp();

        /**
         * 用来执行fling操作的view
         */
        RecyclerView getRecyclerView();
    }
}
