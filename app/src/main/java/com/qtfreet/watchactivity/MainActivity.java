package com.qtfreet.watchactivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnCancelListener, OnClickListener {
    CompoundButton mCompoundButton;

    @TargetApi(21)
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked && compoundButton == mCompoundButton
                && getResources().getBoolean(R.bool.use_accessibility_service)
                && WatchingAccessibilityService.getInstance() == null) {
            new Builder(this).setMessage(R.string.dialog_enable_accessibility_msg)
                    .setPositiveButton(R.string.dialog_enable_accessibility_positive_btn, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                            startActivity(intent);

                        }
                    }).setNegativeButton(R.string.dialog_enable_accessibility_Nagetive_btn, this).setOnCancelListener(this).create().show();
            DefaultSharedPreferences.save(this, isChecked);
        } else if (compoundButton == mCompoundButton) {
            DefaultSharedPreferences.save(this, isChecked);
            if (isChecked) {
                ViewWindow.showView(this, getPackageName() + "\n" + getClass().getName());
            } else {
                ViewWindow.removeView();
            }
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        ViewWindow.showView(this, "");
        mCompoundButton = (CompoundButton) findViewById(R.id.sw_window);
        mCompoundButton.setOnCheckedChangeListener(this);
        if (getResources().getBoolean(R.bool.use_watching_service)) {
            startService(new Intent(this, WatchingService.class));
        }
    }

    protected void onPause() {
        super.onPause();
        if (!DefaultSharedPreferences.read(this)) {
            return;
        }
        if (!getResources().getBoolean(R.bool.use_accessibility_service) || WatchingAccessibilityService.getInstance() != null) {
            NotificationActionReceiver.showNotification(this, false);
        }
    }

    protected void onResume() {
        super.onResume();
        status();
        NotificationActionReceiver.initNotification(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        status();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        status();
    }

    private void status() {
        mCompoundButton.setChecked(DefaultSharedPreferences.read(this));
        if (getResources().getBoolean(R.bool.use_accessibility_service) && WatchingAccessibilityService.getInstance() == null) {
            mCompoundButton.setChecked(false);
        }
    }
}