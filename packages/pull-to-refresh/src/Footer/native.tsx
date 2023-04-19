import React from 'react'
import { Platform, requireNativeComponent, ViewProps } from 'react-native'
import { PullToRefreshOffsetChangedEvent, PullToRefreshStateChangedEvent } from '../types'

interface NativePullToRefreshFooterProps extends ViewProps {
  onRefresh?: () => void
  onStateChanged?: (event: PullToRefreshStateChangedEvent) => void
  onOffsetChanged?: (event: PullToRefreshOffsetChangedEvent) => void
  refreshing: boolean
  noMoreData?: boolean
  manual?: boolean
}

const NativePullToRefreshFooter = requireNativeComponent<NativePullToRefreshFooterProps>(
  Platform.OS === 'ios' ? 'RefreshFooter' : 'SPullRefreshFooter',
)

type NativePullToRefreshFooterInstance = InstanceType<typeof NativePullToRefreshFooter>

const PullToRefreshFooter = React.forwardRef<NativePullToRefreshFooterInstance, NativePullToRefreshFooterProps>(
  (props, ref) => {
    return <NativePullToRefreshFooter {...props} ref={ref} />
  },
)

export { PullToRefreshFooter }