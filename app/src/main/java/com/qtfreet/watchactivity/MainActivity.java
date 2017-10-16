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
        status();
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
        status();
    }

    private void status() {
        mCompoundButton.setChecked(DefaultSharedPreferences.read(this));

    }

    /**
     * 检测辅助功能是否开启<br>
     * 方 法 名：isAccessibilitySettingsOn <br>
     * 创 建 人 <br>
     * 创建时间：2016-6-22 下午2:29:24 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     *
     * @param mContext
     * @return boolean
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = getPackageName() + "/" + WatchingAccessibilityService.class.getCanonicalName();
        Log.i(TAG, "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
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