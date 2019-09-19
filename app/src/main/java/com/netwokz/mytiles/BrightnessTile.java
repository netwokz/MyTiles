package com.netwokz.mytiles;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

/**
 * Created by netwokz on 11/1/2017.
 */

public class BrightnessTile extends TileService {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    // Changes the appearance of the tile.
    private void updateTile() {
        log("UPDATE TILE");
        Tile tile = this.getQsTile();
        int mCurrBrightness = getCurrentBrightness(this);
        if (getCurrentBrightnessMode(this) == 1) {
            tile.setLabel("Auto");
        } else if (mCurrBrightness == 63) {
            tile.setLabel("25%");
        } else if (mCurrBrightness == 128) {
            tile.setLabel("50%");
        } else if (mCurrBrightness == 191) {
            tile.setLabel("75%");
        } else if (mCurrBrightness == 255) {
            tile.setLabel("100%");
        }
        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_settings_brightness_white_24px));
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
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
        updateTile();
    }

    public void setScreenBrightness(int stage) {
        switch (stage) {
            case 0: // Automatic
                ScreenBrightness(-1, this);
                break;

            case 1: // 25%
                ScreenBrightness(63, this);
                break;

            case 2: // 50%
                ScreenBrightness(128, this);
                break;

            case 3: // 75%
                ScreenBrightness(191, this);
                break;

            case 4: // 100%
                ScreenBrightness(255, this);
                break;
        }
    }

    private void ScreenBrightness(int level, Context context) {
        if (level == -1) {
            try {
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            } catch (Exception e) {
                Log.e("Screen Brightness Mode", "error changing screen brightness mode");
            }
            return;
        }
        try {
//            android.provider.Settings.System.putInt(context.getContentResolver(),
//                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);


            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                    level);

        } catch (Exception e) {
            Log.e("Screen Brightness", "error changing screen brightness");
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        int mCurrBrightness = getCurrentBrightness(this);
        if (getCurrentBrightnessMode(this) == 1) {
            setScreenBrightness(1);
            updateTile();
            return;
        }

        if (mCurrBrightness == 63) {
            setScreenBrightness(2);
        } else if (mCurrBrightness == 128) {
            setScreenBrightness(3);
        } else if (mCurrBrightness == 191) {
            setScreenBrightness(4);
        } else if (mCurrBrightness == 255) {
            setScreenBrightness(0);
        }
        updateTile();
        log("Level: " + String.valueOf(getCurrentBrightness(this)));
        log("Mode: " + String.valueOf(getCurrentBrightnessMode(this)));
    }

    public int getCurrentBrightness(Context ctx) {
        int mCurrentBrightness = Settings.System.getInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
        return mCurrentBrightness;
    }

    public int getCurrentBrightnessMode(Context ctx) {
        int mCurrentBrightnessMode = Settings.System.getInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, -1);
        return mCurrentBrightnessMode;
    }

    public void log(String msg) {
        Log.d("ScreenTimeoutTile", msg);
    }
}
