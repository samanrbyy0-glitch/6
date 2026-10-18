package com.sami.dailyapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "DailyAlarm")
public class DailyAlarmPlugin extends Plugin {

    @PluginMethod
    public void setAlarms(PluginCall call) {
        Context c = getContext();
        JSArray arr = call.getArray("alarms");
        String json = (arr == null) ? "[]" : arr.toString();
        AlarmStore.cancelAll(c);
        AlarmStore.save(c, json);
        AlarmStore.rescheduleAll(c);
        call.resolve();
    }

    @PluginMethod
    public void testAlarm(PluginCall call) {
        int sec = call.getInt("seconds", 10);
        AlarmStore.scheduleOnce(getContext(), 7777, "تست الارم",
                "اگه این صفحه رو می‌بینی، الارم تمام‌صفحه درست کار می‌کنه.",
                System.currentTimeMillis() + sec * 1000L);
        call.resolve();
    }

    @PluginMethod
    public void status(PluginCall call) {
        Context c = getContext();
        boolean exact = true;
        boolean fullScreen = true;
        if (Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            exact = am.canScheduleExactAlarms();
        }
        if (Build.VERSION.SDK_INT >= 34) {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            fullScreen = nm.canUseFullScreenIntent();
        }
        JSObject o = new JSObject();
        o.put("exact", exact);
        o.put("fullScreen", fullScreen);
        call.resolve(o);
    }

    @PluginMethod
    public void openExactSettings(PluginCall call) {
        if (Build.VERSION.SDK_INT >= 31) {
            Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:" + getContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        }
        call.resolve();
    }

    @PluginMethod
    public void openFullScreenSettings(PluginCall call) {
        if (Build.VERSION.SDK_INT >= 34) {
            Intent i = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                    Uri.parse("package:" + getContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        }
        call.resolve();
    }
}
