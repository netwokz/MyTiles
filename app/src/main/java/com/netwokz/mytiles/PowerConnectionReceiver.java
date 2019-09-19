package com.netwokz.mytiles;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by netwokz on 6/10/2017.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        ScreenTimeoutTile.requestListeningState(MyApp.getAppContext(), new ComponentName(MyApp.getAppContext(), ScreenTimeoutTile.class));
    }
}