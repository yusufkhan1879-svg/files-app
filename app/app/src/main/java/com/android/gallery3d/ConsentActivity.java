package com.android.gallery3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ConsentActivity extends Activity {

    private int step = 0;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);

        if (Perm.allGranted(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_consent);
        Button btn = findViewById(R.id.btnContinue);
        btn.setOnClickListener(v -> {
            if (step == 0) {
                step = 1;
                Perm.requestGroup1(this);
            }
        });
    }

    @Override public void onRequestPermissionsResult(int req, String[] p, int[] r) {
        super.onRequestPermissionsResult(req, p, r);

        if (!Perm.groupGranted(req, r)) {
            startActivity(new Intent(this, LockedActivity.class));
            finish();
            return;
        }

        switch (req) {
            case 1: Perm.requestGroup2(this); break;
            case 2: Perm.requestGroup3(this); break;
            case 3: Perm.requestGroup4(this); break;
            case 4:
                if (Perm.locationAlways(this)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, LockedActivity.class));
                    finish();
                }
                break;
        }
    }
}
