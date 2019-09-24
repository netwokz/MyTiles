package com.netwokz.mytiles;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by netwokz on 7/4/2017.
 */

public class TimeoutDialogActivity extends Activity implements AdapterView.OnItemClickListener {

    private int[] mTimeoutValues;
    String[] mArray = {"15 Seconds", "30 Seconds", "1 Minute", "2 Minutes", "5 Minutes", "10 Minutes", "30 Minutes", "Never (while charging)"};
    PowerManager.WakeLock wakelock = null;
    Intent batteryStatus = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timeout_dialog_fragment);

        mTimeoutValues = MyApp.getAppContext().getResources().getIntArray(R.array.screen_timeout_values_not_charging);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        ListView mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);

        ArrayList<String> mArrayList = new ArrayList<>();
        for (int i = 0; i < mArray.length; i++) {
            mArrayList.add(mArray[i]);
        }
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.timeout_list_view, mArrayList);
        mListView.setAdapter(mAdapter);
    }

    public void KeepScreenOn(boolean value) {
        if (value) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, getClass().getCanonicalName());
            wakelock.acquire();
        }
    }

    public boolean IsCharging() {
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    /**
     * set screen off timeout
     *
     * @param screenOffTimeout int time in milliseconds
     * @param label            String display label
     */
    private void setTimeout(int screenOffTimeout, String label) {
        android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOffTimeout);
        Toast.makeText(this, "Screen timeout set to " + label, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 7) {
            if (IsCharging()) {
                KeepScreenOn(true);
                Toast.makeText(this, "Screen timeout set to never", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (wakelock != null && wakelock.isHeld()) {
                wakelock.release();
            }
            setTimeout(mTimeoutValues[position], mArray[position]);
            TileService.requestListeningState(this, new ComponentName(this, ScreenTimeoutTile.class));
            finish();
        }
    }
}
