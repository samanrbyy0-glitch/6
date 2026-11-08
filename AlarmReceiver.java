package com.sami.dailyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent in) {
        Intent s = new Intent(c, AlarmService.class);
        s.putExtra("id", in.getIntExtra("id", 0));
        s.putExtra("title", in.getStringExtra("title"));
        s.putExtra("body", in.getStringExtra("body"));
        if (Build.VERSION.SDK_INT >= 26) {
            c.startForegroundService(s);
        } else {
            c.startService(s);
        }
        AlarmStore.rescheduleAll(c);
    }
}
