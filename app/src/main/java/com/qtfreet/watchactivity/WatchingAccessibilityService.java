package com.qtfreet.watchactivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class WatchingAccessibilityService extends AccessibilityService {


    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!DefaultSharedPreferences.read(this)) {
            ViewWindow.removeView();
            return;
        }
        String info = accessibilityEvent.getClassName().toString();
        ViewWindow.showView(this, info);
    }

    public void onInterrupt() {
    }

    protected void onServiceConnected() {
        super.onServiceConnected();
        NotificationActionReceiver.showNotification(this, false);

    }

    public boolean onUnbind(Intent intent) {
        ViewWindow.removeView();
        NotificationActionReceiver.removeNotification(this);
        return super.onUnbind(intent);
    }


}