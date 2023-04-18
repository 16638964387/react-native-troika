package com.reactnative.pulltorefresh.event;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

public class RefreshEvent extends Event<RefreshEvent> {
    public static final String Name = "refreshEvent";
    public static final String JSEventName = "onRefresh";

    public RefreshEvent(int surfaceId, int viewTag) {
        super(surfaceId, viewTag);
    }

    @Override
    public String getEventName() {
        return Name;
    }

    @Nullable
    protected WritableMap getEventData() {
        return Arguments.createMap();
    }
}
