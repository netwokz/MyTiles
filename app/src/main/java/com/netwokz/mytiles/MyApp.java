package com.netwokz.mytiles;

import android.app.Application;
import android.content.Context;

/**
 * Created by netwokz on 5/28/2017.
 */

public class MyApp extends Application {
    private static MyApp instance;

    public MyApp() {
        instance = this;
    }

    public static Context getAppContext() {
        return instance;
    }
}
