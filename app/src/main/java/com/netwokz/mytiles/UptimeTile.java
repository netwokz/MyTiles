package com.netwokz.mytiles;

import android.graphics.drawable.Icon;
import android.os.SystemClock;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import java.util.concurrent.TimeUnit;

/**
 * Created by netwokz on 11/4/2017.
 */

public class UptimeTile extends TileService {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    // Changes the appearance of the tile.
    private void updateTile() {
        Tile tile = this.getQsTile();
        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_update_white_24px));
        tile.setLabel(formatInterval(getUptime()));
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

    public long getUptime() {
        return SystemClock.elapsedRealtime();
    }

    private String formatInterval(final long millis) {
        final int hour = (int) TimeUnit.MILLISECONDS.toHours(millis);
        final int day = (int) TimeUnit.MILLISECONDS.toDays(millis);
        final int hr = (int) TimeUnit.MILLISECONDS.toHours(millis - TimeUnit.DAYS.toMillis(day));
        final int min = (int) TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hour));
        if (day > 0) {
            StringBuilder builder = new StringBuilder()
                    .append(day)
                    .append("d ")
                    .append(hr)
                    .append("h ")
                    .append(min)
                    .append("m");
            return builder.toString();

        } else {
            if (hr > 0) {
                StringBuilder builder = new StringBuilder()
                        .append(hr)
                        .append("h ")
                        .append(min)
                        .append("m");
                return builder.toString();
            } else {
                StringBuilder builder = new StringBuilder()
                        .append(min)
                        .append("m");
                return builder.toString();
            }
        }
    }
}
