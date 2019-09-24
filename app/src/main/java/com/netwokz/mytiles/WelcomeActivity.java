package com.netwokz.mytiles;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by netwokz on 11/4/2017.
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button mWriteSettingsRequestButton;
    Intent starterintent;
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;
    boolean flag_is_permission_set = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_slide_1);

//        starterintent = getIntent();
//        if (!Settings.System.canWrite(this)) {
//            flag_is_permission_set = false;
//            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//            intent.setData(Uri.parse("package:" + this.getPackageName()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivityForResult(intent, WelcomeActivity.CODE_WRITE_SETTINGS_PERMISSION);
//        } else {
//            flag_is_permission_set = true;
//            launchHomeScreen();
//            finish();
//        }

//        // Checking if we can write system settings - before calling setContentView()
//        if (Settings.System.canWrite(getApplicationContext())) {
//            launchHomeScreen();
//            finish();
//        }

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.welcome_slide_1);
        mWriteSettingsRequestButton = findViewById(R.id.btn_enable_write_settings);
        mWriteSettingsRequestButton.setOnClickListener(this);

        // making notification bar transparent
        changeStatusBarColor();
    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_enable_write_settings) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 200);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.System.canWrite(getApplicationContext())) {
            launchHomeScreen();
            finish();
        }
    }
}
