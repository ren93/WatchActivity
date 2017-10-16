package com.qtfreet.watchactivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnCancelListener, OnClickListener {

    CompoundButton mCompoundButton;
    private SensorManager sensorManager = null;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        mCompoundButton = (CompoundButton) findViewById(R.id.sw_window);
        mCompoundButton.setOnCheckedChangeListener(this);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

    }


    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        DefaultSharedPreferences.save(this, isChecked);

        if (isChecked && !isAccessibilitySettingsOn(this)) {
            showSettingDialog();
        }

        if (isChecked && isAccessibilitySettingsOn(this)) {
            ViewWindow.showView(this, getClass().getName());
        } else {
            ViewWindow.removeView();
        }

    }

    private void showSettingDialog() {
        new AlertDialog.Builder(this).setMessage(R.string.dialog_enable_accessibility_msg)
                .setPositiveButton(R.string.dialog_enable_accessibility_positive_btn, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                        startActivity(intent);

                    }
                }).
                setNegativeButton(R.string.dialog_enable_accessibility_Nagetive_btn, this)
                .setOnCancelListener(this)
                .create()
                .show();
    }

    protected void onResume() {
        super.onResume();
        refrehStatus();
        NotificationActionReceiver.initNotification(this);
    }

    protected void onPause() {
        super.onPause();
        if (!DefaultSharedPreferences.read(this)) {
            return;
        }
        NotificationActionReceiver.showNotification(this, false);

    }


    @Override
    public void onCancel(DialogInterface dialog) {
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        refrehStatus();
    }

    private void refrehStatus() {
        mCompoundButton.setChecked(DefaultSharedPreferences.read(this));

    }


    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + WatchingAccessibilityService.class.getCanonicalName();
        Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }


}