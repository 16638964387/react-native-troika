package com.reactnative.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.view.ReactViewGroup;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

@SuppressLint("RestrictedApi")
public class PullToRefreshFooter extends ReactViewGroup implements RefreshFooter {
    private RefreshKernel mRefreshKernel;
    private OnRefreshChangeListener onRefreshChangeListener;
    private boolean mIsLoadingMore = false;
    private boolean mEnableAutoloadMore = true;
    private boolean mNoMoreData = false;

    public void setOnRefreshHeaderChangeListener(OnRefreshChangeListener onRefreshChangeListener) {
        this.onRefreshChangeListener = onRefreshChangeListener;
    }

    public PullToRefreshFooter(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureMode = MeasureSpec.getMode(heightMeasureSpec);
        if (measureMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getParent() instanceof PullToRefresh && mRefreshKernel == null) {
            PullToRefresh refreshLayout = (PullToRefresh) getParent();
            int h = MeasureSpec.getSize(heightMeasureSpec);
            refreshLayout.setFooterHeightPx(h);
        }
    }

    PullToRefreshFooterLocalData footerLocalData = new PullToRefreshFooterLocalData();

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (footerLocalData.viewRect.top == top
                && footerLocalData.viewRect.bottom == bottom
                && footerLocalData.viewRect.left == left
                && footerLocalData.viewRect.right == right) {
            return;
        }
        footerLocalData.viewRect.top = top;
        footerLocalData.viewRect.bottom = bottom;
        footerLocalData.viewRect.left = left;
        footerLocalData.viewRect.right = right;
        Context context = getContext();
        if (context instanceof ReactContext) {
            ReactContext reactContext = (ReactContext) context;
            UIManagerModule uiManagerModule = reactContext.getNativeModule(UIManagerModule.class);
            if (uiManagerModule != null) {
                uiManagerModule.setViewLocalData(getId(), footerLocalData);
            }
        }
    }

    public void setLoadingMore(boolean loadingMore) {
        mIsLoadingMore = loadingMore;
        if (loadingMore) {
            beginLoadMore();
        } else {
            finishLoadMore();
        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        mNoMoreData = noMoreData;
        if (mRefreshKernel != null) {
            mRefreshKernel.getRefreshLayout().setNoMoreData(noMoreData);
            return noMoreData;
        }
        return false;
    }

    public void setAutoLoadMore(boolean enable) {
        mEnableAutoloadMore = enable;
        if (mRefreshKernel != null) {
            mRefreshKernel.getRefreshLayout().setEnableAutoLoadMore(enable);
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    public void beginLoadMore() {
        if (mRefreshKernel != null) {
            RefreshState refreshState = mRefreshKernel.getRefreshLayout().getState();
            if (!refreshState.isHeader && !refreshState.isOpening) {
                mRefreshKernel.getRefreshLayout().autoLoadMore();
            }
        }
    }

    public void finishLoadMore() {
        if (mRefreshKernel != null) {
            RefreshState refreshState = mRefreshKernel.getRefreshLayout().getState();
            if (!refreshState.isHeader && !refreshState.isFinishing) {
                mRefreshKernel.getRefreshLayout().finishLoadMore();
            }
        }
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.getRefreshLayout().setOnLoadMoreListener(refreshLayout -> {
            if (onRefreshChangeListener != null) {
                onRefreshChangeListener.onRefresh();
            }
        });
        setLoadingMore(mIsLoadingMore);
        setAutoLoadMore(mEnableAutoloadMore);
        setNoMoreData(mNoMoreData);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (isDragging && onRefreshChangeListener != null) {
            onRefreshChangeListener.onOffsetChange(offset);
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (onRefreshChangeListener != null && newState.isFooter) {
            PullToRefreshState state = convertRefreshStateToMJRefreshState(newState);
            onRefreshChangeListener.onStateChanged(state);
        }
    }

    @Override
    public PointerEvents getPointerEvents() {
        RefreshState refreshState = mRefreshKernel != null ? mRefreshKernel.getRefreshLayout().getState() : RefreshState.None;
        if (refreshState.isHeader && refreshState.isOpening) {
            return super.getPointerEvents();
        }
        return PointerEvents.NONE;
    }

    private PullToRefreshState convertRefreshStateToMJRefreshState(RefreshState state) {
        if (state.isReleaseToOpening) {
            return PullToRefreshState.Coming;
        }
        if (state.isOpening) {
            return PullToRefreshState.Refreshing;
        }
        return PullToRefreshState.Idle;
    }
}
