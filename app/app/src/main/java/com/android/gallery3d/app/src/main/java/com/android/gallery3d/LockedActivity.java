package com.android.gallery3d;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

public class LockedActivity extends Activity {

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_locked);

        Button retry = findViewById(R.id.btnRetry);
        retry.setOnClickListener(v -> {
            if (!Perm.locationAlways(this)) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName())));
            } else {
                startActivity(new Intent(this, ConsentActivity.class));
                finish();
            }
        });
    }

    @Override public void onBackPressed() {
        moveTaskToBack(true);
    }
}
