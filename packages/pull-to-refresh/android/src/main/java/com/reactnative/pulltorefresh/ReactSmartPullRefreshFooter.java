package com.reactnative.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.views.view.ReactViewGroup;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

@SuppressLint("RestrictedApi")
public class ReactSmartPullRefreshFooter extends ReactViewGroup implements RefreshFooter {
    private String TAG = ReactSmartPullRefreshHeaderManager.REACT_CLASS;
    private RefreshKernel mRefreshKernel;
    private OnRefreshChangeListener onRefreshChangeListener;

    public void setOnRefreshHeaderChangeListener(OnRefreshChangeListener onRefreshChangeListener) {
        this.onRefreshChangeListener = onRefreshChangeListener;
    }

    public ReactSmartPullRefreshFooter(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureMode = MeasureSpec.getMode(heightMeasureSpec);
        if (measureMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getParent() instanceof ReactSmartPullRefreshLayout && mRefreshKernel == null) {
            ReactSmartPullRefreshLayout refreshLayout = (ReactSmartPullRefreshLayout) getParent();
            int h = MeasureSpec.getSize(heightMeasureSpec);
            refreshLayout.setHeaderHeightPx(h);
        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mRefreshKernel != null) {
            mRefreshKernel.getRefreshLayout().setNoMoreData(noMoreData);
            return noMoreData;
        }
        return false;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    public void beginLoadMore() {
        if (mRefreshKernel != null) {
            mRefreshKernel.getRefreshLayout().autoLoadMore();
        }
    }

    public void finishLoadMore() {
        if (mRefreshKernel != null) {
            mRefreshKernel.getRefreshLayout().finishLoadMore();
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
            if (onRefreshChangeListener != null && refreshLayout.getState() == RefreshState.Loading) {
                onRefreshChangeListener.onRefresh();
            }
        });
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
            MJRefreshState state = convertRefreshStateToMJRefreshState(newState);
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

    private MJRefreshState convertRefreshStateToMJRefreshState(RefreshState state) {
        if (state.isReleaseToOpening) {
            return MJRefreshState.Coming;
        }
        if (state.isOpening) {
            return MJRefreshState.Refreshing;
        }
        return MJRefreshState.Idle;
    }
}