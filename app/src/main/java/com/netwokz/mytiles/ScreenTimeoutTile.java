package com.netwokz.mytiles;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by netwokz on 5/25/2017.
 */

public class ScreenTimeoutTile extends TileService implements AdapterView.OnItemClickListener {

    private int[] mTimeoutValuesNotCharging;
    private int[] mTimeoutValuesWhileCharging;
    private Dialog dialog = null;
    String[] mArray = {"15 Seconds", "30 Seconds", "1 Minute", "2 Minutes", "5 Minutes", "10 Minutes", "30 Minutes", "Never (while charging)"};
    PowerManager.WakeLock wakelock = null;
    Intent batteryStatus = null;
    private Window mWindow;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimeoutValuesNotCharging = MyApp.getAppContext().getResources().getIntArray(R.array.screen_timeout_values_not_charging);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTile();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        if (IsCharging()) {
            log("CHARGING");
        } else {
            log("DISCHARGING");
        }
        updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
//        Intent intent = new Intent(getApplicationContext(), TimeoutDialogActivity.class);
//        startActivityAndCollapse(intent);
        int mCurrent = getCurrentTimeout();
        for (int a = 0; a < mTimeoutValuesNotCharging.length; a++) {
            if (mCurrent == 1800000) {
                setTimeout(mTimeoutValuesNotCharging[0], mArray[0]);
            } else if (mCurrent == mTimeoutValuesNotCharging[a]) {
                setTimeout(mTimeoutValuesNotCharging[a + 1], mArray[a + 1]);
            }
        }
        updateTile();
    }

    public void log(String msg) {
        Log.d("ScreenTimeoutTile", msg);
    }

    public String getTime(int millis) {
        log("millis: " + millis);
        if (millis > 59000) {
            return String.format(Locale.getDefault(), "%d min", TimeUnit.MILLISECONDS.toMinutes(millis));
        } else {
            return String.format(Locale.getDefault(), "%d sec", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }
    }

    public int getCurrentTimeout() {
        int mCurrentTimeout = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
        log("mCurrentTimeout: " + mCurrentTimeout);
        return mCurrentTimeout;

    }

    public Dialog myDialog() {
        // custom dialog
        dialog = new Dialog(this, R.style.DailogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timeout_dialog_fragment);
        ListView mListView = (ListView) dialog.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);

        ArrayList<String> mArrayList = new ArrayList<>();
        for (int i = 0; i < mArray.length; i++) {
            mArrayList.add(mArray[i]);
        }
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.timeout_list_view, mArrayList);
        mListView.setAdapter(mAdapter);

        return dialog;
    }

    // Changes the appearance of the tile.
    private void updateTile() {
        log("UPDATE TILE");
        Tile tile = this.getQsTile();
        if (wakelock != null && wakelock.isHeld()) {
            tile.setLabel("Never");
        } else {
            tile.setLabel(getTime(getCurrentTimeout()));
        }
        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_screen_lock_portrait_white));
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
    }

    public void KeepScreenOn(boolean value) {
        if (value) {
            log("KeepScreenOn");
            log("WakeLock acquired");
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
        log(String.valueOf(position));
        if (position == 7) {
            if (IsCharging()) {
                KeepScreenOn(true);
                Toast.makeText(this, "Screen timeout set to never", Toast.LENGTH_SHORT).show();
            }
            if (dialog != null) {
                updateTile();
                dialog.dismiss();
            }
        } else {
            if (wakelock != null && wakelock.isHeld()) {
                wakelock.release();
                log("WakeLock released");
            }
            setTimeout(mTimeoutValuesNotCharging[position], mArray[position]);
            if (dialog != null) {
                updateTile();
                dialog.dismiss();
            }
        }
    }

}
